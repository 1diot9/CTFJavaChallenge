package org.apache.catalina.loader;

import jakarta.servlet.ServletContext;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import javax.management.ObjectName;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.util.LifecycleMBeanBase;
import org.apache.catalina.util.ToStringUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jakartaee.ClassConverter;
import org.apache.tomcat.jakartaee.EESpecProfiles;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappLoader.class */
public class WebappLoader extends LifecycleMBeanBase implements Loader {
    private WebappClassLoaderBase classLoader = null;
    private Context context = null;
    private boolean delegate = false;
    private String jakartaConverter = null;
    private String loaderClass = ParallelWebappClassLoader.class.getName();
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String classpath = null;
    private static final Log log = LogFactory.getLog((Class<?>) WebappLoader.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) WebappLoader.class);

    @Override // org.apache.catalina.Loader
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override // org.apache.catalina.Loader
    public Context getContext() {
        return this.context;
    }

    @Override // org.apache.catalina.Loader
    public void setContext(Context context) {
        if (this.context == context) {
            return;
        }
        if (getState().isAvailable()) {
            throw new IllegalStateException(sm.getString("webappLoader.setContext.ise"));
        }
        Context oldContext = this.context;
        this.context = context;
        this.support.firePropertyChange("context", oldContext, this.context);
    }

    @Override // org.apache.catalina.Loader
    public boolean getDelegate() {
        return this.delegate;
    }

    @Override // org.apache.catalina.Loader
    public void setDelegate(boolean delegate) {
        boolean oldDelegate = this.delegate;
        this.delegate = delegate;
        this.support.firePropertyChange("delegate", Boolean.valueOf(oldDelegate), Boolean.valueOf(this.delegate));
    }

    public String getJakartaConverter() {
        return this.jakartaConverter;
    }

    public void setJakartaConverter(String jakartaConverter) {
        String oldJakartaConverter = this.jakartaConverter;
        this.jakartaConverter = jakartaConverter;
        this.support.firePropertyChange("jakartaConverter", oldJakartaConverter, this.jakartaConverter);
    }

    public String getLoaderClass() {
        return this.loaderClass;
    }

    public void setLoaderClass(String loaderClass) {
        this.loaderClass = loaderClass;
    }

    public void setLoaderInstance(WebappClassLoaderBase loaderInstance) {
        this.classLoader = loaderInstance;
        setLoaderClass(loaderInstance.getClass().getName());
    }

    @Override // org.apache.catalina.Loader
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }

    @Override // org.apache.catalina.Loader
    public void backgroundProcess() {
        Context context = getContext();
        if (context != null && context.getReloadable() && modified()) {
            Thread currentThread = Thread.currentThread();
            ClassLoader originalTccl = currentThread.getContextClassLoader();
            try {
                currentThread.setContextClassLoader(WebappLoader.class.getClassLoader());
                context.reload();
                currentThread.setContextClassLoader(originalTccl);
            } catch (Throwable th) {
                currentThread.setContextClassLoader(originalTccl);
                throw th;
            }
        }
    }

    public String[] getLoaderRepositories() {
        if (this.classLoader == null) {
            return new String[0];
        }
        URL[] urls = this.classLoader.getURLs();
        String[] result = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            result[i] = urls[i].toExternalForm();
        }
        return result;
    }

    public String getLoaderRepositoriesString() {
        String[] repositories = getLoaderRepositories();
        StringBuilder sb = new StringBuilder();
        for (String repository : repositories) {
            sb.append(repository).append(':');
        }
        return sb.toString();
    }

    public String getClasspath() {
        return this.classpath;
    }

    @Override // org.apache.catalina.Loader
    public boolean modified() {
        if (this.classLoader != null) {
            return this.classLoader.modified();
        }
        return false;
    }

    @Override // org.apache.catalina.Loader
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }

    public String toString() {
        return ToStringUtil.toString(this, this.context);
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void startInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("webappLoader.starting"));
        }
        if (this.context.getResources() == null) {
            log.info(sm.getString("webappLoader.noResources", this.context));
            setState(LifecycleState.STARTING);
            return;
        }
        try {
            this.classLoader = createClassLoader();
            this.classLoader.setResources(this.context.getResources());
            this.classLoader.setDelegate(this.delegate);
            if (getJakartaConverter() != null) {
                MigrationUtil.addJakartaEETransformer(this.classLoader, getJakartaConverter());
            }
            setClassPath();
            setPermissions();
            this.classLoader.start();
            String contextName = this.context.getName();
            if (!contextName.startsWith("/")) {
                contextName = "/" + contextName;
            }
            ObjectName cloname = new ObjectName(this.context.getDomain() + ":type=" + this.classLoader.getClass().getSimpleName() + ",host=" + this.context.getParent().getName() + ",context=" + contextName);
            Registry.getRegistry(null, null).registerComponent(this.classLoader, cloname, (String) null);
            setState(LifecycleState.STARTING);
        } catch (Throwable t) {
            Throwable t2 = ExceptionUtils.unwrapInvocationTargetException(t);
            ExceptionUtils.handleThrowable(t2);
            throw new LifecycleException(sm.getString("webappLoader.startError"), t2);
        }
    }

    @Override // org.apache.catalina.util.LifecycleBase
    protected void stopInternal() throws LifecycleException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("webappLoader.stopping"));
        }
        setState(LifecycleState.STOPPING);
        ServletContext servletContext = this.context.getServletContext();
        servletContext.removeAttribute(Globals.CLASS_PATH_ATTR);
        if (this.classLoader != null) {
            try {
                this.classLoader.stop();
                try {
                    String contextName = this.context.getName();
                    if (!contextName.startsWith("/")) {
                        contextName = "/" + contextName;
                    }
                    ObjectName cloname = new ObjectName(this.context.getDomain() + ":type=" + this.classLoader.getClass().getSimpleName() + ",host=" + this.context.getParent().getName() + ",context=" + contextName);
                    Registry.getRegistry(null, null).unregisterComponent(cloname);
                } catch (Exception e) {
                    log.warn(sm.getString("webappLoader.stopError"), e);
                }
            } finally {
                this.classLoader.destroy();
            }
        }
        this.classLoader = null;
    }

    private WebappClassLoaderBase createClassLoader() throws Exception {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (ParallelWebappClassLoader.class.getName().equals(this.loaderClass)) {
            return new ParallelWebappClassLoader(this.context.getParentClassLoader());
        }
        Class<?> clazz = Class.forName(this.loaderClass);
        ClassLoader parentClassLoader = this.context.getParentClassLoader();
        Class<?>[] argTypes = {ClassLoader.class};
        Object[] args = {parentClassLoader};
        Constructor<?> constr = clazz.getConstructor(argTypes);
        WebappClassLoaderBase classLoader = (WebappClassLoaderBase) constr.newInstance(args);
        return classLoader;
    }

    private void setPermissions() {
        if (!Globals.IS_SECURITY_ENABLED || this.context == null) {
            return;
        }
        ServletContext servletContext = this.context.getServletContext();
        File workDir = (File) servletContext.getAttribute("jakarta.servlet.context.tempdir");
        if (workDir != null) {
            try {
                String workDirPath = workDir.getCanonicalPath();
                this.classLoader.addPermission(new FilePermission(workDirPath, "read,write"));
                this.classLoader.addPermission(new FilePermission(workDirPath + File.separator + "-", "read,write,delete"));
            } catch (IOException e) {
            }
        }
        for (URL url : this.context.getResources().getBaseUrls()) {
            this.classLoader.addPermission(url);
        }
    }

    private void setClassPath() {
        ServletContext servletContext;
        ClassLoader loader;
        if (this.context == null || (servletContext = this.context.getServletContext()) == null) {
            return;
        }
        StringBuilder classpath = new StringBuilder();
        ClassLoader loader2 = getClassLoader();
        if (this.delegate && loader2 != null) {
            loader2 = loader2.getParent();
        }
        while (loader2 != null && buildClassPath(classpath, loader2)) {
            loader2 = loader2.getParent();
        }
        if (this.delegate && (loader = getClassLoader()) != null) {
            buildClassPath(classpath, loader);
        }
        this.classpath = classpath.toString();
        servletContext.setAttribute(Globals.CLASS_PATH_ATTR, this.classpath);
    }

    private boolean buildClassPath(StringBuilder classpath, ClassLoader loader) {
        String repository;
        if (!(loader instanceof URLClassLoader)) {
            if (loader == ClassLoader.getSystemClassLoader()) {
                String cp = System.getProperty("java.class.path");
                if (cp != null && cp.length() > 0) {
                    if (classpath.length() > 0) {
                        classpath.append(File.pathSeparator);
                    }
                    classpath.append(cp);
                    return false;
                }
                return false;
            }
            if (!JreCompat.isGraalAvailable()) {
                log.info(sm.getString("webappLoader.unknownClassLoader", loader, loader.getClass()));
                return false;
            }
            return false;
        }
        URL[] repositories = ((URLClassLoader) loader).getURLs();
        for (URL url : repositories) {
            String repository2 = url.toString();
            if (repository2.startsWith("file://")) {
                repository = UDecoder.URLDecode(repository2.substring(7), StandardCharsets.UTF_8);
            } else if (repository2.startsWith("file:")) {
                repository = UDecoder.URLDecode(repository2.substring(5), StandardCharsets.UTF_8);
            }
            if (repository != null) {
                if (classpath.length() > 0) {
                    classpath.append(File.pathSeparator);
                }
                classpath.append(repository);
            }
        }
        return true;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getDomainInternal() {
        return this.context.getDomain();
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        StringBuilder name = new StringBuilder("type=Loader");
        name.append(",host=");
        name.append(this.context.getParent().getName());
        name.append(",context=");
        String contextName = this.context.getName();
        if (!contextName.startsWith("/")) {
            name.append('/');
        }
        name.append(contextName);
        return name.toString();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappLoader$MigrationUtil.class */
    private static class MigrationUtil {
        private MigrationUtil() {
        }

        public static void addJakartaEETransformer(WebappClassLoaderBase webappClassLoader, String profileName) {
            EESpecProfiles eESpecProfiles = null;
            try {
                eESpecProfiles = EESpecProfiles.valueOf(profileName);
            } catch (IllegalArgumentException e) {
                WebappLoader.log.warn(WebappLoader.sm.getString("webappLoader.unknownProfile", profileName));
            }
            webappClassLoader.addTransformer(eESpecProfiles != null ? new ClassConverter(eESpecProfiles) : new ClassConverter());
        }
    }
}

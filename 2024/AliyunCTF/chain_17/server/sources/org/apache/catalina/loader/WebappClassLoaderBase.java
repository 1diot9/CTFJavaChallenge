package org.apache.catalina.loader;

import java.beans.Introspector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.catalina.Container;
import org.apache.catalina.Globals;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.juli.WebappProperties;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstrumentableClassLoader;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.scan.Constants;
import org.apache.tomcat.util.security.PermissionCheck;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappClassLoaderBase.class */
public abstract class WebappClassLoaderBase extends URLClassLoader implements Lifecycle, InstrumentableClassLoader, WebappProperties, PermissionCheck {
    private static final Log log = LogFactory.getLog((Class<?>) WebappClassLoaderBase.class);
    private static final List<String> JVM_THREAD_GROUP_NAMES = new ArrayList();
    private static final String JVM_THREAD_GROUP_SYSTEM = "system";
    private static final String CLASS_FILE_SUFFIX = ".class";
    protected static final StringManager sm;
    protected WebResourceRoot resources;
    protected final Map<String, ResourceEntry> resourceEntries;
    protected boolean delegate;
    private final Map<String, Long> jarModificationTimes;
    protected final ArrayList<Permission> permissionList;
    protected final HashMap<String, PermissionCollection> loaderPC;
    protected final SecurityManager securityManager;
    protected final ClassLoader parent;
    private ClassLoader javaseClassLoader;
    private boolean clearReferencesRmiTargets;
    private boolean clearReferencesStopThreads;
    private boolean clearReferencesStopTimerThreads;
    private boolean clearReferencesLogFactoryRelease;
    private boolean clearReferencesHttpClientKeepAliveThread;
    private boolean clearReferencesObjectStreamClassCaches;
    private boolean clearReferencesThreadLocals;
    private boolean skipMemoryLeakChecksOnJvmShutdown;
    private final List<ClassFileTransformer> transformers;
    private boolean hasExternalRepositories;
    private List<URL> localRepositories;
    private volatile LifecycleState state;

    static {
        if (!JreCompat.isGraalAvailable()) {
            registerAsParallelCapable();
        }
        JVM_THREAD_GROUP_NAMES.add(JVM_THREAD_GROUP_SYSTEM);
        JVM_THREAD_GROUP_NAMES.add("RMI Runtime");
        sm = StringManager.getManager((Class<?>) WebappClassLoaderBase.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappClassLoaderBase$PrivilegedFindClassByName.class */
    public class PrivilegedFindClassByName implements PrivilegedAction<Class<?>> {
        private final String name;

        PrivilegedFindClassByName(String name) {
            this.name = name;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Class<?> run() {
            return WebappClassLoaderBase.this.findClassInternal(this.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappClassLoaderBase$PrivilegedGetClassLoader.class */
    public static final class PrivilegedGetClassLoader implements PrivilegedAction<ClassLoader> {
        private final Class<?> clazz;

        public PrivilegedGetClassLoader(Class<?> clazz) {
            this.clazz = clazz;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public ClassLoader run() {
            return this.clazz.getClassLoader();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappClassLoaderBase$PrivilegedJavaseGetResource.class */
    public final class PrivilegedJavaseGetResource implements PrivilegedAction<URL> {
        private final String name;

        public PrivilegedJavaseGetResource(String name) {
            this.name = name;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public URL run() {
            return WebappClassLoaderBase.this.javaseClassLoader.getResource(this.name);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebappClassLoaderBase() {
        super(new URL[0]);
        this.resources = null;
        this.resourceEntries = new ConcurrentHashMap();
        this.delegate = false;
        this.jarModificationTimes = new HashMap();
        this.permissionList = new ArrayList<>();
        this.loaderPC = new HashMap<>();
        this.clearReferencesRmiTargets = true;
        this.clearReferencesStopThreads = false;
        this.clearReferencesStopTimerThreads = false;
        this.clearReferencesLogFactoryRelease = true;
        this.clearReferencesHttpClientKeepAliveThread = true;
        this.clearReferencesObjectStreamClassCaches = true;
        this.clearReferencesThreadLocals = true;
        this.skipMemoryLeakChecksOnJvmShutdown = false;
        this.transformers = new CopyOnWriteArrayList();
        this.hasExternalRepositories = false;
        this.localRepositories = new ArrayList();
        this.state = LifecycleState.NEW;
        ClassLoader p = getParent();
        this.parent = p == null ? getSystemClassLoader() : p;
        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            ClassLoader systemClassLoader = getSystemClassLoader();
            while (true) {
                j = systemClassLoader;
                if (j.getParent() == null) {
                    break;
                } else {
                    systemClassLoader = j.getParent();
                }
            }
        }
        this.javaseClassLoader = j;
        this.securityManager = System.getSecurityManager();
        if (this.securityManager != null) {
            refreshPolicy();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WebappClassLoaderBase(ClassLoader parent) {
        super(new URL[0], parent);
        this.resources = null;
        this.resourceEntries = new ConcurrentHashMap();
        this.delegate = false;
        this.jarModificationTimes = new HashMap();
        this.permissionList = new ArrayList<>();
        this.loaderPC = new HashMap<>();
        this.clearReferencesRmiTargets = true;
        this.clearReferencesStopThreads = false;
        this.clearReferencesStopTimerThreads = false;
        this.clearReferencesLogFactoryRelease = true;
        this.clearReferencesHttpClientKeepAliveThread = true;
        this.clearReferencesObjectStreamClassCaches = true;
        this.clearReferencesThreadLocals = true;
        this.skipMemoryLeakChecksOnJvmShutdown = false;
        this.transformers = new CopyOnWriteArrayList();
        this.hasExternalRepositories = false;
        this.localRepositories = new ArrayList();
        this.state = LifecycleState.NEW;
        ClassLoader p = getParent();
        this.parent = p == null ? getSystemClassLoader() : p;
        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            ClassLoader systemClassLoader = getSystemClassLoader();
            while (true) {
                j = systemClassLoader;
                if (j.getParent() == null) {
                    break;
                } else {
                    systemClassLoader = j.getParent();
                }
            }
        }
        this.javaseClassLoader = j;
        this.securityManager = System.getSecurityManager();
        if (this.securityManager != null) {
            refreshPolicy();
        }
    }

    public void setResources(WebResourceRoot resources) {
        this.resources = resources;
    }

    public String getContextName() {
        if (this.resources == null) {
            return "Unknown";
        }
        return this.resources.getContext().getBaseName();
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPermission(URL url) {
        if (url != null && this.securityManager != null) {
            String protocol = url.getProtocol();
            if ("file".equalsIgnoreCase(protocol)) {
                try {
                    URI uri = url.toURI();
                    File f = new File(uri);
                    String path = f.getCanonicalPath();
                    if (f.isFile()) {
                        addPermission(new FilePermission(path, "read"));
                        return;
                    } else {
                        if (f.isDirectory()) {
                            addPermission(new FilePermission(path, "read"));
                            addPermission(new FilePermission(path + File.separator + "-", "read"));
                            return;
                        }
                        return;
                    }
                } catch (IOException | URISyntaxException e) {
                    log.warn(sm.getString("webappClassLoader.addPermissionNoCanonicalFile", url.toExternalForm()));
                    return;
                }
            }
            log.warn(sm.getString("webappClassLoader.addPermissionNoProtocol", protocol, url.toExternalForm()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPermission(Permission permission) {
        if (this.securityManager != null && permission != null) {
            this.permissionList.add(permission);
        }
    }

    public boolean getClearReferencesRmiTargets() {
        return this.clearReferencesRmiTargets;
    }

    public void setClearReferencesRmiTargets(boolean clearReferencesRmiTargets) {
        this.clearReferencesRmiTargets = clearReferencesRmiTargets;
    }

    public boolean getClearReferencesStopThreads() {
        return this.clearReferencesStopThreads;
    }

    public void setClearReferencesStopThreads(boolean clearReferencesStopThreads) {
        this.clearReferencesStopThreads = clearReferencesStopThreads;
    }

    public boolean getClearReferencesStopTimerThreads() {
        return this.clearReferencesStopTimerThreads;
    }

    public void setClearReferencesStopTimerThreads(boolean clearReferencesStopTimerThreads) {
        this.clearReferencesStopTimerThreads = clearReferencesStopTimerThreads;
    }

    public boolean getClearReferencesLogFactoryRelease() {
        return this.clearReferencesLogFactoryRelease;
    }

    public void setClearReferencesLogFactoryRelease(boolean clearReferencesLogFactoryRelease) {
        this.clearReferencesLogFactoryRelease = clearReferencesLogFactoryRelease;
    }

    public boolean getClearReferencesHttpClientKeepAliveThread() {
        return this.clearReferencesHttpClientKeepAliveThread;
    }

    public void setClearReferencesHttpClientKeepAliveThread(boolean clearReferencesHttpClientKeepAliveThread) {
        this.clearReferencesHttpClientKeepAliveThread = clearReferencesHttpClientKeepAliveThread;
    }

    public boolean getClearReferencesObjectStreamClassCaches() {
        return this.clearReferencesObjectStreamClassCaches;
    }

    public void setClearReferencesObjectStreamClassCaches(boolean clearReferencesObjectStreamClassCaches) {
        this.clearReferencesObjectStreamClassCaches = clearReferencesObjectStreamClassCaches;
    }

    public boolean getClearReferencesThreadLocals() {
        return this.clearReferencesThreadLocals;
    }

    public void setClearReferencesThreadLocals(boolean clearReferencesThreadLocals) {
        this.clearReferencesThreadLocals = clearReferencesThreadLocals;
    }

    public boolean getSkipMemoryLeakChecksOnJvmShutdown() {
        return this.skipMemoryLeakChecksOnJvmShutdown;
    }

    public void setSkipMemoryLeakChecksOnJvmShutdown(boolean skipMemoryLeakChecksOnJvmShutdown) {
        this.skipMemoryLeakChecksOnJvmShutdown = skipMemoryLeakChecksOnJvmShutdown;
    }

    @Override // org.apache.tomcat.InstrumentableClassLoader
    public void addTransformer(ClassFileTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException(sm.getString("webappClassLoader.addTransformer.illegalArgument", getContextName()));
        }
        if (this.transformers.contains(transformer)) {
            log.warn(sm.getString("webappClassLoader.addTransformer.duplicate", transformer, getContextName()));
        } else {
            this.transformers.add(transformer);
            log.info(sm.getString("webappClassLoader.addTransformer", transformer, getContextName()));
        }
    }

    @Override // org.apache.tomcat.InstrumentableClassLoader
    public void removeTransformer(ClassFileTransformer transformer) {
        if (transformer != null && this.transformers.remove(transformer)) {
            log.info(sm.getString("webappClassLoader.removeTransformer", transformer, getContextName()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void copyStateWithoutTransformers(WebappClassLoaderBase base) {
        base.resources = this.resources;
        base.delegate = this.delegate;
        base.state = LifecycleState.NEW;
        base.clearReferencesStopThreads = this.clearReferencesStopThreads;
        base.clearReferencesStopTimerThreads = this.clearReferencesStopTimerThreads;
        base.clearReferencesLogFactoryRelease = this.clearReferencesLogFactoryRelease;
        base.clearReferencesHttpClientKeepAliveThread = this.clearReferencesHttpClientKeepAliveThread;
        base.jarModificationTimes.putAll(this.jarModificationTimes);
        base.permissionList.addAll(this.permissionList);
        base.loaderPC.putAll(this.loaderPC);
    }

    public boolean modified() {
        if (log.isDebugEnabled()) {
            log.debug("modified()");
        }
        for (Map.Entry<String, ResourceEntry> entry : this.resourceEntries.entrySet()) {
            long cachedLastModified = entry.getValue().lastModified;
            long lastModified = this.resources.getClassLoaderResource(entry.getKey()).getLastModified();
            if (lastModified != cachedLastModified) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("webappClassLoader.resourceModified", entry.getKey(), new Date(cachedLastModified), new Date(lastModified)));
                    return true;
                }
                return true;
            }
        }
        WebResource[] jars = this.resources.listResources("/WEB-INF/lib");
        int jarCount = 0;
        for (WebResource jar : jars) {
            if (jar.getName().endsWith(".jar") && jar.isFile() && jar.canRead()) {
                jarCount++;
                Long recordedLastModified = this.jarModificationTimes.get(jar.getName());
                if (recordedLastModified == null) {
                    log.info(sm.getString("webappClassLoader.jarsAdded", this.resources.getContext().getName()));
                    return true;
                }
                if (recordedLastModified.longValue() != jar.getLastModified()) {
                    log.info(sm.getString("webappClassLoader.jarsModified", this.resources.getContext().getName()));
                    return true;
                }
            }
        }
        if (jarCount < this.jarModificationTimes.size()) {
            log.info(sm.getString("webappClassLoader.jarsRemoved", this.resources.getContext().getName()));
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("\r\n  context: ");
        sb.append(getContextName());
        sb.append("\r\n  delegate: ");
        sb.append(this.delegate);
        sb.append("\r\n");
        if (this.parent != null) {
            sb.append("----------> Parent Classloader:\r\n");
            sb.append(this.parent.toString());
            sb.append("\r\n");
        }
        if (this.transformers.size() > 0) {
            sb.append("----------> Class file transformers:\r\n");
            for (ClassFileTransformer transformer : this.transformers) {
                sb.append(transformer).append("\r\n");
            }
        }
        return sb.toString();
    }

    protected final Class<?> doDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectionDomain) {
        return super.defineClass(name, b, off, len, protectionDomain);
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz;
        ClassLoader cl;
        int i;
        if (log.isDebugEnabled()) {
            log.debug("    findClass(" + name + ")");
        }
        checkStateForClassLoading(name);
        if (this.securityManager != null && (i = name.lastIndexOf(46)) >= 0) {
            try {
                if (log.isTraceEnabled()) {
                    log.trace("      securityManager.checkPackageDefinition");
                }
                this.securityManager.checkPackageDefinition(name.substring(0, i));
            } catch (Exception se) {
                if (log.isTraceEnabled()) {
                    log.trace("      -->Exception-->ClassNotFoundException", se);
                }
                throw new ClassNotFoundException(name, se);
            }
        }
        try {
            if (log.isTraceEnabled()) {
                log.trace("      findClassInternal(" + name + ")");
            }
            try {
                if (this.securityManager != null) {
                    PrivilegedAction<Class<?>> dp = new PrivilegedFindClassByName(name);
                    clazz = (Class) AccessController.doPrivileged(dp);
                } else {
                    clazz = findClassInternal(name);
                }
                if (clazz == null && this.hasExternalRepositories) {
                    try {
                        clazz = super.findClass(name);
                    } catch (AccessControlException ace) {
                        log.warn(sm.getString("webappClassLoader.securityException", name, ace.getMessage()), ace);
                        throw new ClassNotFoundException(name, ace);
                    } catch (RuntimeException e) {
                        if (log.isTraceEnabled()) {
                            log.trace("      -->RuntimeException Rethrown", e);
                        }
                        throw e;
                    }
                }
                if (clazz == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("    --> Returning ClassNotFoundException");
                    }
                    throw new ClassNotFoundException(name);
                }
                if (log.isTraceEnabled()) {
                    log.debug("      Returning class " + clazz);
                }
                if (log.isTraceEnabled()) {
                    if (Globals.IS_SECURITY_ENABLED) {
                        cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedGetClassLoader(clazz));
                    } else {
                        cl = clazz.getClassLoader();
                    }
                    log.debug("      Loaded by " + cl.toString());
                }
                return clazz;
            } catch (AccessControlException ace2) {
                log.warn(sm.getString("webappClassLoader.securityException", name, ace2.getMessage()), ace2);
                throw new ClassNotFoundException(name, ace2);
            } catch (RuntimeException e2) {
                if (log.isTraceEnabled()) {
                    log.trace("      -->RuntimeException Rethrown", e2);
                }
                throw e2;
            }
        } catch (ClassNotFoundException e3) {
            if (log.isTraceEnabled()) {
                log.trace("    --> Passing on ClassNotFoundException");
            }
            throw e3;
        }
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public URL findResource(String name) {
        if (log.isDebugEnabled()) {
            log.debug("    findResource(" + name + ")");
        }
        checkStateForResourceLoading(name);
        URL url = null;
        String path = nameToPath(name);
        WebResource resource = this.resources.getClassLoaderResource(path);
        if (resource.exists()) {
            url = resource.getURL();
            trackLastModified(path, resource);
        }
        if (url == null && this.hasExternalRepositories) {
            url = super.findResource(name);
        }
        if (log.isDebugEnabled()) {
            if (url != null) {
                log.debug("    --> Returning '" + url.toString() + "'");
            } else {
                log.debug("    --> Resource not found, returning null");
            }
        }
        return url;
    }

    private void trackLastModified(String path, WebResource resource) {
        if (this.resourceEntries.containsKey(path)) {
            return;
        }
        ResourceEntry entry = new ResourceEntry();
        entry.lastModified = resource.getLastModified();
        synchronized (this.resourceEntries) {
            this.resourceEntries.putIfAbsent(path, entry);
        }
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public Enumeration<URL> findResources(String name) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("    findResources(" + name + ")");
        }
        checkStateForResourceLoading(name);
        LinkedHashSet<URL> result = new LinkedHashSet<>();
        String path = nameToPath(name);
        WebResource[] webResources = this.resources.getClassLoaderResources(path);
        for (WebResource webResource : webResources) {
            if (webResource.exists()) {
                result.add(webResource.getURL());
            }
        }
        if (this.hasExternalRepositories) {
            Enumeration<URL> otherResourcePaths = super.findResources(name);
            while (otherResourcePaths.hasMoreElements()) {
                result.add(otherResourcePaths.nextElement());
            }
        }
        return Collections.enumeration(result);
    }

    @Override // java.lang.ClassLoader
    public URL getResource(String name) {
        URL url;
        if (log.isDebugEnabled()) {
            log.debug("getResource(" + name + ")");
        }
        checkStateForResourceLoading(name);
        boolean delegateFirst = this.delegate || filter(name, false);
        if (delegateFirst) {
            if (log.isDebugEnabled()) {
                log.debug("  Delegating to parent classloader " + this.parent);
            }
            URL url2 = this.parent.getResource(name);
            if (url2 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  --> Returning '" + url2.toString() + "'");
                }
                return url2;
            }
        }
        URL url3 = findResource(name);
        if (url3 != null) {
            if (log.isDebugEnabled()) {
                log.debug("  --> Returning '" + url3.toString() + "'");
            }
            return url3;
        }
        if (!delegateFirst && (url = this.parent.getResource(name)) != null) {
            if (log.isDebugEnabled()) {
                log.debug("  --> Returning '" + url.toString() + "'");
            }
            return url;
        }
        if (log.isDebugEnabled()) {
            log.debug("  --> Resource not found, returning null");
            return null;
        }
        return null;
    }

    @Override // java.lang.ClassLoader
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> parentResources = this.parent.getResources(name);
        Enumeration<URL> localResources = findResources(name);
        boolean delegateFirst = this.delegate || filter(name, false);
        if (delegateFirst) {
            return new CombinedEnumeration(parentResources, localResources);
        }
        return new CombinedEnumeration(localResources, parentResources);
    }

    @Override // java.net.URLClassLoader, java.lang.ClassLoader
    public InputStream getResourceAsStream(String name) {
        URL url;
        if (log.isDebugEnabled()) {
            log.debug("getResourceAsStream(" + name + ")");
        }
        checkStateForResourceLoading(name);
        InputStream stream = null;
        boolean delegateFirst = this.delegate || filter(name, false);
        if (delegateFirst) {
            if (log.isDebugEnabled()) {
                log.debug("  Delegating to parent classloader " + this.parent);
            }
            stream = this.parent.getResourceAsStream(name);
            if (stream != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  --> Returning stream from parent");
                }
                return stream;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("  Searching local repositories");
        }
        String path = nameToPath(name);
        WebResource resource = this.resources.getClassLoaderResource(path);
        if (resource.exists()) {
            stream = resource.getInputStream();
            if (name.endsWith(".class") && this.transformers.size() > 0) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                while (true) {
                    try {
                        try {
                            int numRead = stream.read(buf);
                            if (numRead < 0) {
                                break;
                            }
                            baos.write(buf, 0, numRead);
                        } catch (Throwable th) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                            }
                            throw th;
                        }
                    } catch (IOException e2) {
                        log.error(sm.getString("webappClassLoader.transformError", name), e2);
                        try {
                            stream.close();
                        } catch (IOException e3) {
                        }
                        return null;
                    }
                }
                try {
                    stream.close();
                } catch (IOException e4) {
                }
                byte[] binaryContent = baos.toByteArray();
                String internalName = path.substring(1, path.length() - ".class".length());
                for (ClassFileTransformer transformer : this.transformers) {
                    try {
                        byte[] transformed = transformer.transform(this, internalName, (Class) null, (ProtectionDomain) null, binaryContent);
                        if (transformed != null) {
                            binaryContent = transformed;
                        }
                    } catch (IllegalClassFormatException e5) {
                        log.error(sm.getString("webappClassLoader.transformError", name), e5);
                        return null;
                    }
                }
                stream = new ByteArrayInputStream(binaryContent);
            }
            trackLastModified(path, resource);
        }
        try {
            if (this.hasExternalRepositories && stream == null && (url = super.findResource(name)) != null) {
                stream = url.openStream();
            }
        } catch (IOException e6) {
        }
        if (stream != null) {
            if (log.isDebugEnabled()) {
                log.debug("  --> Returning stream from local");
            }
            return stream;
        }
        if (!delegateFirst) {
            if (log.isDebugEnabled()) {
                log.debug("  Delegating to parent classloader unconditionally " + this.parent);
            }
            InputStream stream2 = this.parent.getResourceAsStream(name);
            if (stream2 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  --> Returning stream from parent");
                }
                return stream2;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("  --> Resource not found, returning null");
            return null;
        }
        return null;
    }

    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        boolean tryLoadingFromJavaseLoader;
        Class<?> clazz;
        int i;
        URL url;
        Object classLoadingLock = JreCompat.isGraalAvailable() ? this : getClassLoadingLock(name);
        Object obj = classLoadingLock;
        synchronized (classLoadingLock) {
            if (log.isDebugEnabled()) {
                log.debug("loadClass(" + name + ", " + resolve + ")");
            }
            checkStateForClassLoading(name);
            Class<?> clazz2 = findLoadedClass0(name);
            if (clazz2 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Returning class from cache");
                }
                if (resolve) {
                    resolveClass(clazz2);
                }
                return clazz2;
            }
            Class<?> clazz3 = JreCompat.isGraalAvailable() ? null : findLoadedClass(name);
            if (clazz3 != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Returning class from cache");
                }
                if (resolve) {
                    resolveClass(clazz3);
                }
                return clazz3;
            }
            String resourceName = binaryNameToPath(name, false);
            ClassLoader javaseLoader = getJavaseClassLoader();
            try {
                if (this.securityManager != null) {
                    PrivilegedAction<URL> dp = new PrivilegedJavaseGetResource(resourceName);
                    url = (URL) AccessController.doPrivileged(dp);
                } else {
                    url = javaseLoader.getResource(resourceName);
                }
                tryLoadingFromJavaseLoader = url != null;
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                tryLoadingFromJavaseLoader = true;
            }
            if (tryLoadingFromJavaseLoader) {
                try {
                    Class<?> clazz4 = javaseLoader.loadClass(name);
                    if (clazz4 != null) {
                        if (resolve) {
                            resolveClass(clazz4);
                        }
                        return clazz4;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
            if (this.securityManager != null && (i = name.lastIndexOf(46)) >= 0) {
                try {
                    this.securityManager.checkPackageAccess(name.substring(0, i));
                } catch (SecurityException se) {
                    String error = sm.getString("webappClassLoader.restrictedPackage", name);
                    log.info(error, se);
                    throw new ClassNotFoundException(error, se);
                }
            }
            boolean delegateLoad = this.delegate || filter(name, true);
            if (delegateLoad) {
                if (log.isDebugEnabled()) {
                    log.debug("  Delegating to parent classloader1 " + this.parent);
                }
                try {
                    Class<?> clazz5 = Class.forName(name, false, this.parent);
                    if (clazz5 != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("  Loading class from parent");
                        }
                        if (resolve) {
                            resolveClass(clazz5);
                        }
                        return clazz5;
                    }
                } catch (ClassNotFoundException e2) {
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("  Searching local repositories");
            }
            try {
                clazz = findClass(name);
            } catch (ClassNotFoundException e3) {
            }
            if (clazz != null) {
                if (log.isDebugEnabled()) {
                    log.debug("  Loading class from local repository");
                }
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
            if (!delegateLoad) {
                if (log.isDebugEnabled()) {
                    log.debug("  Delegating to parent classloader at end: " + this.parent);
                }
                try {
                    Class<?> clazz6 = Class.forName(name, false, this.parent);
                    if (clazz6 != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("  Loading class from parent");
                        }
                        if (resolve) {
                            resolveClass(clazz6);
                        }
                        return clazz6;
                    }
                } catch (ClassNotFoundException e4) {
                }
            }
            throw new ClassNotFoundException(name);
        }
    }

    protected void checkStateForClassLoading(String className) throws ClassNotFoundException {
        try {
            checkStateForResourceLoading(className);
        } catch (IllegalStateException ise) {
            throw new ClassNotFoundException(ise.getMessage(), ise);
        }
    }

    protected void checkStateForResourceLoading(String resource) throws IllegalStateException {
        if (!this.state.isAvailable()) {
            String msg = sm.getString("webappClassLoader.stopped", resource);
            IllegalStateException ise = new IllegalStateException(msg);
            log.info(msg, ise);
            throw ise;
        }
    }

    @Override // java.net.URLClassLoader, java.security.SecureClassLoader
    protected PermissionCollection getPermissions(CodeSource codeSource) {
        String codeUrl = codeSource.getLocation().toString();
        PermissionCollection permissionCollection = this.loaderPC.get(codeUrl);
        PermissionCollection pc = permissionCollection;
        if (permissionCollection == null) {
            pc = super.getPermissions(codeSource);
            if (pc != null) {
                Iterator<Permission> it = this.permissionList.iterator();
                while (it.hasNext()) {
                    Permission p = it.next();
                    pc.add(p);
                }
                this.loaderPC.put(codeUrl, pc);
            }
        }
        return pc;
    }

    @Override // org.apache.tomcat.util.security.PermissionCheck
    public boolean check(Permission permission) {
        if (!Globals.IS_SECURITY_ENABLED) {
            return true;
        }
        Policy currentPolicy = Policy.getPolicy();
        if (currentPolicy != null) {
            URL contextRootUrl = this.resources.getResource("/").getCodeBase();
            CodeSource cs = new CodeSource(contextRootUrl, (Certificate[]) null);
            PermissionCollection pc = currentPolicy.getPermissions(cs);
            if (pc.implies(permission)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // java.net.URLClassLoader
    public URL[] getURLs() {
        ArrayList<URL> result = new ArrayList<>();
        result.addAll(this.localRepositories);
        result.addAll(Arrays.asList(super.getURLs()));
        return (URL[]) result.toArray(new URL[0]);
    }

    @Override // org.apache.catalina.Lifecycle
    public void addLifecycleListener(LifecycleListener listener) {
    }

    @Override // org.apache.catalina.Lifecycle
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override // org.apache.catalina.Lifecycle
    public void removeLifecycleListener(LifecycleListener listener) {
    }

    @Override // org.apache.catalina.Lifecycle
    public LifecycleState getState() {
        return this.state;
    }

    @Override // org.apache.catalina.Lifecycle
    public String getStateName() {
        return getState().toString();
    }

    @Override // org.apache.catalina.Lifecycle
    public void init() {
        this.state = LifecycleState.INITIALIZED;
    }

    @Override // org.apache.catalina.Lifecycle
    public void start() throws LifecycleException {
        this.state = LifecycleState.STARTING_PREP;
        WebResource[] classesResources = this.resources.getResources(Constants.WEB_INF_CLASSES);
        for (WebResource classes : classesResources) {
            if (classes.isDirectory() && classes.canRead()) {
                this.localRepositories.add(classes.getURL());
            }
        }
        WebResource[] jars = this.resources.listResources("/WEB-INF/lib");
        for (WebResource jar : jars) {
            if (jar.getName().endsWith(".jar") && jar.isFile() && jar.canRead()) {
                this.localRepositories.add(jar.getURL());
                this.jarModificationTimes.put(jar.getName(), Long.valueOf(jar.getLastModified()));
            }
        }
        this.state = LifecycleState.STARTED;
    }

    @Override // org.apache.catalina.Lifecycle
    public void stop() throws LifecycleException {
        this.state = LifecycleState.STOPPING_PREP;
        clearReferences();
        this.state = LifecycleState.STOPPING;
        this.resourceEntries.clear();
        this.jarModificationTimes.clear();
        this.resources = null;
        this.permissionList.clear();
        this.loaderPC.clear();
        this.state = LifecycleState.STOPPED;
    }

    @Override // org.apache.catalina.Lifecycle
    public void destroy() {
        this.state = LifecycleState.DESTROYING;
        try {
            super.close();
        } catch (IOException ioe) {
            log.warn(sm.getString("webappClassLoader.superCloseFail"), ioe);
        }
        this.state = LifecycleState.DESTROYED;
    }

    protected ClassLoader getJavaseClassLoader() {
        return this.javaseClassLoader;
    }

    protected void setJavaseClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException(sm.getString("webappClassLoader.javaseClassLoaderNull"));
        }
        this.javaseClassLoader = classLoader;
    }

    protected void clearReferences() {
        if (this.skipMemoryLeakChecksOnJvmShutdown && !this.resources.getContext().getParent().getState().isAvailable()) {
            try {
                Thread dummyHook = new Thread();
                Runtime.getRuntime().addShutdownHook(dummyHook);
                Runtime.getRuntime().removeShutdownHook(dummyHook);
            } catch (IllegalStateException e) {
                return;
            }
        }
        if (!JreCompat.isGraalAvailable()) {
            clearReferencesJdbc();
        }
        clearReferencesThreads();
        if (this.clearReferencesObjectStreamClassCaches && !JreCompat.isGraalAvailable()) {
            clearReferencesObjectStreamClassCaches();
        }
        if (this.clearReferencesThreadLocals && !JreCompat.isGraalAvailable()) {
            checkThreadLocalsForLeaks();
        }
        if (this.clearReferencesRmiTargets) {
            clearReferencesRmiTargets();
        }
        IntrospectionUtils.clear();
        if (this.clearReferencesLogFactoryRelease) {
            LogFactory.release(this);
        }
        Introspector.flushCaches();
        TomcatURLStreamHandlerFactory.release(this);
    }

    private void clearReferencesJdbc() {
        byte[] classBytes = new byte[2048];
        int offset = 0;
        try {
            InputStream is = getResourceAsStream("org/apache/catalina/loader/JdbcLeakPrevention.class");
            try {
                int read = is.read(classBytes, 0, classBytes.length - 0);
                while (read > -1) {
                    offset += read;
                    if (offset == classBytes.length) {
                        byte[] tmp = new byte[classBytes.length * 2];
                        System.arraycopy(classBytes, 0, tmp, 0, classBytes.length);
                        classBytes = tmp;
                    }
                    read = is.read(classBytes, offset, classBytes.length - offset);
                }
                Class<?> lpClass = defineClass("org.apache.catalina.loader.JdbcLeakPrevention", classBytes, 0, offset, getClass().getProtectionDomain());
                Object obj = lpClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                List<String> driverNames = (List) obj.getClass().getMethod("clearJdbcDriverRegistrations", new Class[0]).invoke(obj, new Object[0]);
                for (String name : driverNames) {
                    log.warn(sm.getString("webappClassLoader.clearJdbc", getContextName(), name));
                }
                if (is != null) {
                    is.close();
                }
            } finally {
            }
        } catch (Exception e) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException(e);
            ExceptionUtils.handleThrowable(t);
            log.warn(sm.getString("webappClassLoader.jdbcRemoveFailed", getContextName()), t);
        }
    }

    private void clearReferencesThreads() {
        Thread[] threads = getThreads();
        List<Thread> threadsToStop = new ArrayList<>();
        for (Thread thread : threads) {
            if (thread != null) {
                ClassLoader ccl = thread.getContextClassLoader();
                if (ccl == this && thread != Thread.currentThread()) {
                    String threadName = thread.getName();
                    ThreadGroup tg = thread.getThreadGroup();
                    if (tg != null && JVM_THREAD_GROUP_NAMES.contains(tg.getName())) {
                        if (this.clearReferencesHttpClientKeepAliveThread && threadName.equals("Keep-Alive-Timer")) {
                            thread.setContextClassLoader(this.parent);
                            log.debug(sm.getString("webappClassLoader.checkThreadsHttpClient"));
                        }
                    } else if (thread.isAlive()) {
                        if (thread.getClass().getName().startsWith("java.util.Timer") && this.clearReferencesStopTimerThreads) {
                            clearReferencesStopTimerThread(thread);
                        } else {
                            if (isRequestThread(thread)) {
                                log.warn(sm.getString("webappClassLoader.stackTraceRequestThread", getContextName(), threadName, getStackTrace(thread)));
                            } else {
                                log.warn(sm.getString("webappClassLoader.stackTrace", getContextName(), threadName, getStackTrace(thread)));
                            }
                            if (this.clearReferencesStopThreads) {
                                boolean usingExecutor = false;
                                try {
                                    Object executor = JreCompat.getInstance().getExecutor(thread);
                                    if (executor instanceof ThreadPoolExecutor) {
                                        ((ThreadPoolExecutor) executor).shutdownNow();
                                        usingExecutor = true;
                                    } else if (executor instanceof java.util.concurrent.ThreadPoolExecutor) {
                                        ((java.util.concurrent.ThreadPoolExecutor) executor).shutdownNow();
                                        usingExecutor = true;
                                    }
                                } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | InaccessibleObjectException e) {
                                    log.warn(sm.getString("webappClassLoader.stopThreadFail", thread.getName(), getContextName()), e);
                                }
                                if (!usingExecutor && !thread.isInterrupted()) {
                                    thread.interrupt();
                                }
                                threadsToStop.add(thread);
                            }
                        }
                    }
                }
            }
        }
        int count = 0;
        for (Thread t : threadsToStop) {
            while (t.isAlive() && count < 100) {
                try {
                    Thread.sleep(20L);
                    count++;
                } catch (InterruptedException e2) {
                }
            }
            if (t.isAlive()) {
                t.stop();
            }
        }
    }

    private boolean isRequestThread(Thread thread) {
        StackTraceElement[] elements = thread.getStackTrace();
        if (elements == null || elements.length == 0) {
            return false;
        }
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[elements.length - (i + 1)];
            if ("org.apache.catalina.connector.CoyoteAdapter".equals(element.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void clearReferencesStopTimerThread(Thread thread) {
        try {
            try {
                Field newTasksMayBeScheduledField = thread.getClass().getDeclaredField("newTasksMayBeScheduled");
                newTasksMayBeScheduledField.setAccessible(true);
                Field queueField = thread.getClass().getDeclaredField("queue");
                queueField.setAccessible(true);
                Object queue = queueField.get(thread);
                Method clearMethod = queue.getClass().getDeclaredMethod("clear", new Class[0]);
                clearMethod.setAccessible(true);
                synchronized (queue) {
                    newTasksMayBeScheduledField.setBoolean(thread, false);
                    clearMethod.invoke(queue, new Object[0]);
                    queue.notifyAll();
                }
            } catch (NoSuchFieldException e) {
                Method cancelMethod = thread.getClass().getDeclaredMethod("cancel", new Class[0]);
                synchronized (thread) {
                    cancelMethod.setAccessible(true);
                    cancelMethod.invoke(thread, new Object[0]);
                }
            }
            log.warn(sm.getString("webappClassLoader.warnTimerThread", getContextName(), thread.getName()));
        } catch (Exception e2) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException(e2);
            ExceptionUtils.handleThrowable(t);
            log.warn(sm.getString("webappClassLoader.stopTimerThreadFail", thread.getName(), getContextName()), t);
        }
    }

    private void checkThreadLocalsForLeaks() {
        Thread[] threads = getThreads();
        try {
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            Class<?> tlmClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = tlmClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Method expungeStaleEntriesMethod = tlmClass.getDeclaredMethod("expungeStaleEntries", new Class[0]);
            expungeStaleEntriesMethod.setAccessible(true);
            for (Thread thread : threads) {
                if (thread != null) {
                    Object threadLocalMap = threadLocalsField.get(thread);
                    if (null != threadLocalMap) {
                        expungeStaleEntriesMethod.invoke(threadLocalMap, new Object[0]);
                        checkThreadLocalMapForLeaks(threadLocalMap, tableField);
                    }
                    Object threadLocalMap2 = inheritableThreadLocalsField.get(thread);
                    if (null != threadLocalMap2) {
                        expungeStaleEntriesMethod.invoke(threadLocalMap2, new Object[0]);
                        checkThreadLocalMapForLeaks(threadLocalMap2, tableField);
                    }
                }
            }
        } catch (InaccessibleObjectException e) {
            log.warn(sm.getString("webappClassLoader.addExportsThreadLocal", getCurrentModuleName()));
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.warn(sm.getString("webappClassLoader.checkThreadLocalsForLeaksFail", getContextName()), t);
        }
    }

    private void checkThreadLocalMapForLeaks(Object map, Field internalTableField) throws IllegalAccessException, NoSuchFieldException {
        Object[] table;
        if (map != null && (table = (Object[]) internalTableField.get(map)) != null) {
            for (Object obj : table) {
                if (obj != null) {
                    boolean keyLoadedByWebapp = false;
                    boolean valueLoadedByWebapp = false;
                    Object key = ((Reference) obj).get();
                    if (equals(key) || loadedByThisOrChild(key)) {
                        keyLoadedByWebapp = true;
                    }
                    Field valueField = obj.getClass().getDeclaredField("value");
                    valueField.setAccessible(true);
                    Object value = valueField.get(obj);
                    if (equals(value) || loadedByThisOrChild(value)) {
                        valueLoadedByWebapp = true;
                    }
                    if (keyLoadedByWebapp || valueLoadedByWebapp) {
                        Object[] args = new Object[5];
                        args[0] = getContextName();
                        if (key != null) {
                            args[1] = getPrettyClassName(key.getClass());
                            try {
                                args[2] = key.toString();
                            } catch (Exception e) {
                                log.warn(sm.getString("webappClassLoader.checkThreadLocalsForLeaks.badKey", args[1]), e);
                                args[2] = sm.getString("webappClassLoader.checkThreadLocalsForLeaks.unknown");
                            }
                        }
                        if (value != null) {
                            args[3] = getPrettyClassName(value.getClass());
                            try {
                                args[4] = value.toString();
                            } catch (Exception e2) {
                                log.warn(sm.getString("webappClassLoader.checkThreadLocalsForLeaks.badValue", args[3]), e2);
                                args[4] = sm.getString("webappClassLoader.checkThreadLocalsForLeaks.unknown");
                            }
                        }
                        if (valueLoadedByWebapp) {
                            log.error(sm.getString("webappClassLoader.checkThreadLocalsForLeaks", args));
                        } else if (value == null) {
                            if (log.isDebugEnabled()) {
                                log.debug(sm.getString("webappClassLoader.checkThreadLocalsForLeaksNull", args));
                            }
                        } else if (log.isDebugEnabled()) {
                            log.debug(sm.getString("webappClassLoader.checkThreadLocalsForLeaksNone", args));
                        }
                    }
                }
            }
        }
    }

    private String getPrettyClassName(Class<?> clazz) {
        String name = clazz.getCanonicalName();
        if (name == null) {
            name = clazz.getName();
        }
        return name;
    }

    private String getStackTrace(Thread thread) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement ste : thread.getStackTrace()) {
            builder.append("\n ").append(ste);
        }
        return builder.toString();
    }

    private boolean loadedByThisOrChild(Object o) {
        Class<?> clazz;
        if (o == null) {
            return false;
        }
        if (o instanceof Class) {
            clazz = (Class) o;
        } else {
            clazz = o.getClass();
        }
        ClassLoader classLoader = clazz.getClassLoader();
        while (true) {
            ClassLoader cl = classLoader;
            if (cl != null) {
                if (cl == this) {
                    return true;
                }
                classLoader = cl.getParent();
            } else {
                if (o instanceof Collection) {
                    try {
                        for (Object entry : (Collection) o) {
                            if (loadedByThisOrChild(entry)) {
                                return true;
                            }
                        }
                        return false;
                    } catch (ConcurrentModificationException e) {
                        log.warn(sm.getString("webappClassLoader.loadedByThisOrChildFail", clazz.getName(), getContextName()), e);
                        return false;
                    }
                }
                return false;
            }
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:10:0x0067 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private java.lang.Thread[] getThreads() {
        /*
            r7 = this;
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            java.lang.ThreadGroup r0 = r0.getThreadGroup()
            r8 = r0
        L7:
            r0 = r8
            java.lang.ThreadGroup r0 = r0.getParent()     // Catch: java.lang.SecurityException -> L19
            if (r0 == 0) goto L16
            r0 = r8
            java.lang.ThreadGroup r0 = r0.getParent()     // Catch: java.lang.SecurityException -> L19
            r8 = r0
            goto L7
        L16:
            goto L50
        L19:
            r9 = move-exception
            org.apache.tomcat.util.res.StringManager r0 = org.apache.catalina.loader.WebappClassLoaderBase.sm
            java.lang.String r1 = "webappClassLoader.getThreadGroupError"
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = r2
            r4 = 0
            r5 = r8
            java.lang.String r5 = r5.getName()
            r3[r4] = r5
            java.lang.String r0 = r0.getString(r1, r2)
            r10 = r0
            org.apache.juli.logging.Log r0 = org.apache.catalina.loader.WebappClassLoaderBase.log
            boolean r0 = r0.isDebugEnabled()
            if (r0 == 0) goto L47
            org.apache.juli.logging.Log r0 = org.apache.catalina.loader.WebappClassLoaderBase.log
            r1 = r10
            r2 = r9
            r0.debug(r1, r2)
            goto L50
        L47:
            org.apache.juli.logging.Log r0 = org.apache.catalina.loader.WebappClassLoaderBase.log
            r1 = r10
            r0.warn(r1)
        L50:
            r0 = r8
            int r0 = r0.activeCount()
            r1 = 50
            int r0 = r0 + r1
            r9 = r0
            r0 = r9
            java.lang.Thread[] r0 = new java.lang.Thread[r0]
            r10 = r0
            r0 = r8
            r1 = r10
            int r0 = r0.enumerate(r1)
            r11 = r0
        L64:
            r0 = r11
            r1 = r9
            if (r0 != r1) goto L7d
            r0 = r9
            r1 = 2
            int r0 = r0 * r1
            r9 = r0
            r0 = r9
            java.lang.Thread[] r0 = new java.lang.Thread[r0]
            r10 = r0
            r0 = r8
            r1 = r10
            int r0 = r0.enumerate(r1)
            r11 = r0
            goto L64
        L7d:
            r0 = r10
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.loader.WebappClassLoaderBase.getThreads():java.lang.Thread[]");
    }

    private void clearReferencesRmiTargets() {
        try {
            try {
                Class<?> objectTargetClass = Class.forName("sun.rmi.transport.Target");
                Field cclField = objectTargetClass.getDeclaredField("ccl");
                cclField.setAccessible(true);
                Field stubField = objectTargetClass.getDeclaredField("stub");
                stubField.setAccessible(true);
                Class<?> objectTableClass = Class.forName("sun.rmi.transport.ObjectTable");
                Field objTableField = objectTableClass.getDeclaredField("objTable");
                objTableField.setAccessible(true);
                Object objTable = objTableField.get(null);
                if (objTable == null) {
                    return;
                }
                Field tableLockField = objectTableClass.getDeclaredField("tableLock");
                tableLockField.setAccessible(true);
                Object tableLock = tableLockField.get(null);
                synchronized (tableLock) {
                    if (objTable instanceof Map) {
                        Iterator<?> iter = ((Map) objTable).values().iterator();
                        while (iter.hasNext()) {
                            Object obj = iter.next();
                            Object cclObject = cclField.get(obj);
                            if (this == cclObject) {
                                iter.remove();
                                Object stubObject = stubField.get(obj);
                                log.error(sm.getString("webappClassLoader.clearRmi", stubObject.getClass().getName(), stubObject));
                            }
                        }
                    }
                    Field implTableField = objectTableClass.getDeclaredField("implTable");
                    implTableField.setAccessible(true);
                    Object implTable = implTableField.get(null);
                    if (implTable == null) {
                        return;
                    }
                    if (implTable instanceof Map) {
                        Iterator<?> iter2 = ((Map) implTable).values().iterator();
                        while (iter2.hasNext()) {
                            Object cclObject2 = cclField.get(iter2.next());
                            if (this == cclObject2) {
                                iter2.remove();
                            }
                        }
                    }
                }
            } catch (InaccessibleObjectException e) {
                log.warn(sm.getString("webappClassLoader.addExportsRmi", getCurrentModuleName()));
            } catch (ClassNotFoundException e2) {
                log.info(sm.getString("webappClassLoader.clearRmiInfo", getContextName()), e2);
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e3) {
            log.warn(sm.getString("webappClassLoader.clearRmiFail", getContextName()), e3);
        }
    }

    private void clearReferencesObjectStreamClassCaches() {
        if (JreCompat.isJre19Available()) {
            return;
        }
        try {
            Class<?> clazz = Class.forName("java.io.ObjectStreamClass$Caches");
            clearCache(clazz, "localDescs");
            clearCache(clazz, "reflectors");
        } catch (InaccessibleObjectException e) {
            log.warn(sm.getString("webappClassLoader.addExportsJavaIo", getCurrentModuleName()));
        } catch (ClassCastException | ReflectiveOperationException | SecurityException e2) {
            log.warn(sm.getString("webappClassLoader.clearObjectStreamClassCachesFail", getContextName()), e2);
        }
    }

    private String getCurrentModuleName() {
        String moduleName = getClass().getModule().getName();
        if (moduleName == null) {
            moduleName = "ALL-UNNAMED";
        }
        return moduleName;
    }

    private void clearCache(Class<?> target, String mapName) throws ReflectiveOperationException, SecurityException, ClassCastException {
        Field f = target.getDeclaredField(mapName);
        f.setAccessible(true);
        Object map = f.get(null);
        if (map instanceof Map) {
            Iterator<?> keys = ((Map) map).keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                if (key instanceof Reference) {
                    Object clazz = ((Reference) key).get();
                    if (loadedByThisOrChild(clazz)) {
                        keys.remove();
                    }
                }
            }
        }
    }

    protected Class<?> findClassInternal(String name) {
        boolean sealCheck;
        checkStateForResourceLoading(name);
        if (name == null) {
            return null;
        }
        String path = binaryNameToPath(name, true);
        ResourceEntry entry = this.resourceEntries.get(path);
        WebResource resource = null;
        if (entry == null) {
            resource = this.resources.getClassLoaderResource(path);
            if (!resource.exists()) {
                return null;
            }
            entry = new ResourceEntry();
            entry.lastModified = resource.getLastModified();
            synchronized (this.resourceEntries) {
                ResourceEntry entry2 = this.resourceEntries.get(path);
                if (entry2 == null) {
                    this.resourceEntries.put(path, entry);
                } else {
                    entry = entry2;
                }
            }
        }
        Class<?> clazz = entry.loadedClass;
        if (clazz != null) {
            return clazz;
        }
        Object classLoadingLock = JreCompat.isGraalAvailable() ? this : getClassLoadingLock(name);
        Object obj = classLoadingLock;
        synchronized (classLoadingLock) {
            Class<?> clazz2 = entry.loadedClass;
            if (clazz2 != null) {
                return clazz2;
            }
            if (resource == null) {
                resource = this.resources.getClassLoaderResource(path);
            }
            if (!resource.exists()) {
                return null;
            }
            byte[] binaryContent = resource.getContent();
            if (binaryContent == null) {
                return null;
            }
            Manifest manifest = resource.getManifest();
            URL codeBase = resource.getCodeBase();
            Certificate[] certificates = resource.getCertificates();
            if (this.transformers.size() > 0) {
                String internalName = path.substring(1, path.length() - ".class".length());
                for (ClassFileTransformer transformer : this.transformers) {
                    try {
                        byte[] transformed = transformer.transform(this, internalName, (Class) null, (ProtectionDomain) null, binaryContent);
                        if (transformed != null) {
                            binaryContent = transformed;
                        }
                    } catch (IllegalClassFormatException e) {
                        log.error(sm.getString("webappClassLoader.transformError", name), e);
                        return null;
                    }
                }
            }
            String packageName = null;
            int pos = name.lastIndexOf(46);
            if (pos != -1) {
                packageName = name.substring(0, pos);
            }
            Package pkg = null;
            if (packageName != null) {
                pkg = getPackage(packageName);
                if (pkg == null) {
                    try {
                        if (manifest == null) {
                            definePackage(packageName, null, null, null, null, null, null, null);
                        } else {
                            definePackage(packageName, manifest, codeBase);
                        }
                    } catch (IllegalArgumentException e2) {
                    }
                    pkg = getPackage(packageName);
                }
            }
            if (this.securityManager != null && pkg != null) {
                if (pkg.isSealed()) {
                    sealCheck = pkg.isSealed(codeBase);
                } else {
                    sealCheck = manifest == null || !isPackageSealed(packageName, manifest);
                }
                if (!sealCheck) {
                    throw new SecurityException("Sealing violation loading " + name + " : Package " + packageName + " is sealed.");
                }
            }
            try {
                Class<?> clazz3 = defineClass(name, binaryContent, 0, binaryContent.length, new CodeSource(codeBase, certificates));
                entry.loadedClass = clazz3;
                return clazz3;
            } catch (UnsupportedClassVersionError ucve) {
                throw new UnsupportedClassVersionError(ucve.getLocalizedMessage() + " " + sm.getString("webappClassLoader.wrongVersion", name));
            }
        }
    }

    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(".class");
        return path.toString();
    }

    private String nameToPath(String name) {
        if (name.startsWith("/")) {
            return name;
        }
        StringBuilder path = new StringBuilder(1 + name.length());
        path.append('/');
        path.append(name);
        return path.toString();
    }

    protected boolean isPackageSealed(String name, Manifest man) {
        Attributes attr;
        String path = name.replace('.', '/') + "/";
        Attributes attr2 = man.getAttributes(path);
        String sealed = null;
        if (attr2 != null) {
            sealed = attr2.getValue(Attributes.Name.SEALED);
        }
        if (sealed == null && (attr = man.getMainAttributes()) != null) {
            sealed = attr.getValue(Attributes.Name.SEALED);
        }
        return "true".equalsIgnoreCase(sealed);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> findLoadedClass0(String name) {
        String path = binaryNameToPath(name, true);
        ResourceEntry entry = this.resourceEntries.get(path);
        if (entry != null) {
            return entry.loadedClass;
        }
        return null;
    }

    protected void refreshPolicy() {
        try {
            Policy policy = Policy.getPolicy();
            policy.refresh();
        } catch (AccessControlException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean filter(String name, boolean isClassName) {
        if (name == null) {
            return false;
        }
        if (name.startsWith("jakarta")) {
            if (name.length() == 7) {
                return false;
            }
            char ch2 = name.charAt(7);
            if (isClassName && ch2 == '.') {
                if (name.startsWith("servlet.jsp.jstl.", 8)) {
                    return false;
                }
                if (name.startsWith("annotation.", 8) || name.startsWith("el.", 8) || name.startsWith("servlet.", 8) || name.startsWith("websocket.", 8) || name.startsWith("security.auth.message.", 8)) {
                    return true;
                }
                return false;
            }
            if (isClassName || ch2 != '/' || name.startsWith("servlet/jsp/jstl/", 8)) {
                return false;
            }
            if (name.startsWith("annotation/", 8) || name.startsWith("el/", 8) || name.startsWith("servlet/", 8) || name.startsWith("websocket/", 8) || name.startsWith("security/auth/message/", 8)) {
                return true;
            }
            return false;
        }
        if (name.startsWith("javax")) {
            if (name.length() == 5) {
                return false;
            }
            char ch3 = name.charAt(5);
            if (isClassName && ch3 == '.') {
                if (name.startsWith("websocket.", 6)) {
                    return true;
                }
                return false;
            }
            if (!isClassName && ch3 == '/' && name.startsWith("websocket/", 6)) {
                return true;
            }
            return false;
        }
        if (!name.startsWith("org") || name.length() == 3) {
            return false;
        }
        char ch4 = name.charAt(3);
        if (isClassName && ch4 == '.') {
            if (!name.startsWith("apache.", 4) || name.startsWith("tomcat.jdbc.", 11)) {
                return false;
            }
            if (name.startsWith("el.", 11) || name.startsWith("catalina.", 11) || name.startsWith("jasper.", 11) || name.startsWith("juli.", 11) || name.startsWith("tomcat.", 11) || name.startsWith("naming.", 11) || name.startsWith("coyote.", 11)) {
                return true;
            }
            return false;
        }
        if (isClassName || ch4 != '/' || !name.startsWith("apache/", 4) || name.startsWith("tomcat/jdbc/", 11)) {
            return false;
        }
        if (name.startsWith("el/", 11) || name.startsWith("catalina/", 11) || name.startsWith("jasper/", 11) || name.startsWith("juli/", 11) || name.startsWith("tomcat/", 11) || name.startsWith("naming/", 11) || name.startsWith("coyote/", 11)) {
            return true;
        }
        return false;
    }

    @Override // java.net.URLClassLoader
    protected void addURL(URL url) {
        super.addURL(url);
        this.hasExternalRepositories = true;
    }

    @Override // org.apache.juli.WebappProperties
    public String getWebappName() {
        return getContextName();
    }

    @Override // org.apache.juli.WebappProperties
    public String getHostName() {
        Container host;
        if (this.resources != null && (host = this.resources.getContext().getParent()) != null) {
            return host.getName();
        }
        return null;
    }

    @Override // org.apache.juli.WebappProperties
    public String getServiceName() {
        Container host;
        Container engine;
        if (this.resources != null && (host = this.resources.getContext().getParent()) != null && (engine = host.getParent()) != null) {
            return engine.getName();
        }
        return null;
    }

    @Override // org.apache.juli.WebappProperties
    public boolean hasLoggingConfig() {
        if (!Globals.IS_SECURITY_ENABLED) {
            return findResource("logging.properties") != null;
        }
        Boolean result = (Boolean) AccessController.doPrivileged(new PrivilegedHasLoggingConfig());
        return result.booleanValue();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappClassLoaderBase$PrivilegedHasLoggingConfig.class */
    private class PrivilegedHasLoggingConfig implements PrivilegedAction<Boolean> {
        private PrivilegedHasLoggingConfig() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.security.PrivilegedAction
        public Boolean run() {
            return Boolean.valueOf(WebappClassLoaderBase.this.findResource("logging.properties") != null);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/WebappClassLoaderBase$CombinedEnumeration.class */
    private static class CombinedEnumeration implements Enumeration<URL> {
        private final Enumeration<URL>[] sources;
        private int index = 0;

        CombinedEnumeration(Enumeration<URL> enum1, Enumeration<URL> enum2) {
            Enumeration<URL>[] sources = {enum1, enum2};
            this.sources = sources;
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return inc();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public URL nextElement() {
            if (inc()) {
                return this.sources[this.index].nextElement();
            }
            throw new NoSuchElementException();
        }

        private boolean inc() {
            while (this.index < this.sources.length) {
                if (this.sources[this.index].hasMoreElements()) {
                    return true;
                }
                this.index++;
            }
            return false;
        }
    }
}

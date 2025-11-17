package org.apache.catalina.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Valve;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.web.context.ContextLoader;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardHost.class */
public class StandardHost extends ContainerBase implements Host {
    private static final Log log = LogFactory.getLog((Class<?>) StandardHost.class);
    private String[] aliases = new String[0];
    private final Object aliasesLock = new Object();
    private String appBase = "webapps";
    private volatile File appBaseFile = null;
    private String legacyAppBase = "webapps-javaee";
    private volatile File legacyAppBaseFile = null;
    private String xmlBase = null;
    private volatile File hostConfigBase = null;
    private boolean autoDeploy = true;
    private String configClass = "org.apache.catalina.startup.ContextConfig";
    private String contextClass = "org.apache.catalina.core.StandardContext";
    private boolean deployOnStartup = true;
    private boolean deployXML;
    private boolean copyXML;
    private String errorReportValveClass;
    private boolean unpackWARs;
    private String workDir;
    private boolean createDirs;
    private final Map<ClassLoader, String> childClassLoaders;
    private Pattern deployIgnore;
    private boolean undeployOldVersions;
    private boolean failCtxIfServletStartFails;

    public StandardHost() {
        this.deployXML = !Globals.IS_SECURITY_ENABLED;
        this.copyXML = false;
        this.errorReportValveClass = "org.apache.catalina.valves.ErrorReportValve";
        this.unpackWARs = true;
        this.workDir = null;
        this.createDirs = true;
        this.childClassLoaders = new WeakHashMap();
        this.deployIgnore = null;
        this.undeployOldVersions = false;
        this.failCtxIfServletStartFails = false;
        this.pipeline.setBasic(new StandardHostValve());
    }

    @Override // org.apache.catalina.Host
    public boolean getUndeployOldVersions() {
        return this.undeployOldVersions;
    }

    @Override // org.apache.catalina.Host
    public void setUndeployOldVersions(boolean undeployOldVersions) {
        this.undeployOldVersions = undeployOldVersions;
    }

    @Override // org.apache.catalina.Host
    public ExecutorService getStartStopExecutor() {
        return this.startStopExecutor;
    }

    @Override // org.apache.catalina.Host
    public String getAppBase() {
        return this.appBase;
    }

    @Override // org.apache.catalina.Host
    public File getAppBaseFile() {
        if (this.appBaseFile != null) {
            return this.appBaseFile;
        }
        File file = new File(getAppBase());
        if (!file.isAbsolute()) {
            file = new File(getCatalinaBase(), file.getPath());
        }
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
        }
        this.appBaseFile = file;
        return file;
    }

    @Override // org.apache.catalina.Host
    public void setAppBase(String appBase) {
        if (appBase.trim().equals("")) {
            log.warn(sm.getString("standardHost.problematicAppBase", getName()));
        }
        String oldAppBase = this.appBase;
        this.appBase = appBase;
        this.support.firePropertyChange("appBase", oldAppBase, this.appBase);
        this.appBaseFile = null;
    }

    @Override // org.apache.catalina.Host
    public String getLegacyAppBase() {
        return this.legacyAppBase;
    }

    @Override // org.apache.catalina.Host
    public File getLegacyAppBaseFile() {
        if (this.legacyAppBaseFile != null) {
            return this.legacyAppBaseFile;
        }
        File file = new File(getLegacyAppBase());
        if (!file.isAbsolute()) {
            file = new File(getCatalinaBase(), file.getPath());
        }
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
        }
        this.legacyAppBaseFile = file;
        return file;
    }

    @Override // org.apache.catalina.Host
    public void setLegacyAppBase(String legacyAppBase) {
        if (legacyAppBase.trim().equals("")) {
            log.warn(sm.getString("standardHost.problematicLegacyAppBase", getName()));
        }
        String oldLegacyAppBase = this.legacyAppBase;
        this.legacyAppBase = legacyAppBase;
        this.support.firePropertyChange("legacyAppBase", oldLegacyAppBase, this.legacyAppBase);
        this.legacyAppBaseFile = null;
    }

    @Override // org.apache.catalina.Host
    public String getXmlBase() {
        return this.xmlBase;
    }

    @Override // org.apache.catalina.Host
    public void setXmlBase(String xmlBase) {
        String oldXmlBase = this.xmlBase;
        this.xmlBase = xmlBase;
        this.support.firePropertyChange("xmlBase", oldXmlBase, this.xmlBase);
    }

    @Override // org.apache.catalina.Host
    public File getConfigBaseFile() {
        String path;
        if (this.hostConfigBase != null) {
            return this.hostConfigBase;
        }
        if (getXmlBase() != null) {
            path = getXmlBase();
        } else {
            StringBuilder xmlDir = new StringBuilder("conf");
            Container parent = getParent();
            if (parent instanceof Engine) {
                xmlDir.append('/');
                xmlDir.append(parent.getName());
            }
            xmlDir.append('/');
            xmlDir.append(getName());
            path = xmlDir.toString();
        }
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(getCatalinaBase(), path);
        }
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
        }
        this.hostConfigBase = file;
        return file;
    }

    @Override // org.apache.catalina.Host
    public boolean getCreateDirs() {
        return this.createDirs;
    }

    @Override // org.apache.catalina.Host
    public void setCreateDirs(boolean createDirs) {
        this.createDirs = createDirs;
    }

    @Override // org.apache.catalina.Host
    public boolean getAutoDeploy() {
        return this.autoDeploy;
    }

    @Override // org.apache.catalina.Host
    public void setAutoDeploy(boolean autoDeploy) {
        boolean oldAutoDeploy = this.autoDeploy;
        this.autoDeploy = autoDeploy;
        this.support.firePropertyChange("autoDeploy", oldAutoDeploy, this.autoDeploy);
    }

    @Override // org.apache.catalina.Host
    public String getConfigClass() {
        return this.configClass;
    }

    @Override // org.apache.catalina.Host
    public void setConfigClass(String configClass) {
        String oldConfigClass = this.configClass;
        this.configClass = configClass;
        this.support.firePropertyChange("configClass", oldConfigClass, this.configClass);
    }

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        this.support.firePropertyChange(ContextLoader.CONTEXT_CLASS_PARAM, oldContextClass, this.contextClass);
    }

    @Override // org.apache.catalina.Host
    public boolean getDeployOnStartup() {
        return this.deployOnStartup;
    }

    @Override // org.apache.catalina.Host
    public void setDeployOnStartup(boolean deployOnStartup) {
        boolean oldDeployOnStartup = this.deployOnStartup;
        this.deployOnStartup = deployOnStartup;
        this.support.firePropertyChange("deployOnStartup", oldDeployOnStartup, this.deployOnStartup);
    }

    public boolean isDeployXML() {
        return this.deployXML;
    }

    public void setDeployXML(boolean deployXML) {
        this.deployXML = deployXML;
    }

    public boolean isCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    public String getErrorReportValveClass() {
        return this.errorReportValveClass;
    }

    public void setErrorReportValveClass(String errorReportValveClass) {
        String oldErrorReportValveClassClass = this.errorReportValveClass;
        this.errorReportValveClass = errorReportValveClass;
        this.support.firePropertyChange("errorReportValveClass", oldErrorReportValveClassClass, this.errorReportValveClass);
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public String getName() {
        return this.name;
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("standardHost.nullName"));
        }
        String name2 = name.toLowerCase(Locale.ENGLISH);
        String oldName = this.name;
        this.name = name2;
        this.support.firePropertyChange("name", oldName, this.name);
    }

    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }

    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    public String getWorkDir() {
        return this.workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    @Override // org.apache.catalina.Host
    public String getDeployIgnore() {
        if (this.deployIgnore == null) {
            return null;
        }
        return this.deployIgnore.toString();
    }

    @Override // org.apache.catalina.Host
    public Pattern getDeployIgnorePattern() {
        return this.deployIgnore;
    }

    @Override // org.apache.catalina.Host
    public void setDeployIgnore(String deployIgnore) {
        String oldDeployIgnore;
        if (this.deployIgnore == null) {
            oldDeployIgnore = null;
        } else {
            oldDeployIgnore = this.deployIgnore.toString();
        }
        if (deployIgnore == null) {
            this.deployIgnore = null;
        } else {
            this.deployIgnore = Pattern.compile(deployIgnore);
        }
        this.support.firePropertyChange("deployIgnore", oldDeployIgnore, deployIgnore);
    }

    public boolean isFailCtxIfServletStartFails() {
        return this.failCtxIfServletStartFails;
    }

    public void setFailCtxIfServletStartFails(boolean failCtxIfServletStartFails) {
        boolean oldFailCtxIfServletStartFails = this.failCtxIfServletStartFails;
        this.failCtxIfServletStartFails = failCtxIfServletStartFails;
        this.support.firePropertyChange("failCtxIfServletStartFails", oldFailCtxIfServletStartFails, failCtxIfServletStartFails);
    }

    @Override // org.apache.catalina.Host
    public void addAlias(String alias) {
        String alias2 = alias.toLowerCase(Locale.ENGLISH);
        synchronized (this.aliasesLock) {
            for (String s : this.aliases) {
                if (s.equals(alias2)) {
                    return;
                }
            }
            String[] newAliases = (String[]) Arrays.copyOf(this.aliases, this.aliases.length + 1);
            newAliases[this.aliases.length] = alias2;
            this.aliases = newAliases;
            fireContainerEvent(Host.ADD_ALIAS_EVENT, alias2);
        }
    }

    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.Container
    public void addChild(Container child) {
        if (!(child instanceof Context)) {
            throw new IllegalArgumentException(sm.getString("standardHost.notContext"));
        }
        child.addLifecycleListener(new MemoryLeakTrackingListener());
        Context context = (Context) child;
        if (context.getPath() == null) {
            ContextName cn2 = new ContextName(context.getDocBase(), true);
            context.setPath(cn2.getPath());
        }
        super.addChild(child);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/StandardHost$MemoryLeakTrackingListener.class */
    private class MemoryLeakTrackingListener implements LifecycleListener {
        private MemoryLeakTrackingListener() {
        }

        @Override // org.apache.catalina.LifecycleListener
        public void lifecycleEvent(LifecycleEvent event) {
            if (event.getType().equals(Lifecycle.AFTER_START_EVENT) && (event.getSource() instanceof Context)) {
                Context context = (Context) event.getSource();
                StandardHost.this.childClassLoaders.put(context.getLoader().getClassLoader(), context.getServletContext().getContextPath());
            }
        }
    }

    public String[] findReloadedContextMemoryLeaks() {
        System.gc();
        List<String> result = new ArrayList<>();
        for (Map.Entry<ClassLoader, String> entry : this.childClassLoaders.entrySet()) {
            ClassLoader cl = entry.getKey();
            if ((cl instanceof WebappClassLoaderBase) && !((WebappClassLoaderBase) cl).getState().isAvailable()) {
                result.add(entry.getValue());
            }
        }
        return (String[]) result.toArray(new String[0]);
    }

    @Override // org.apache.catalina.Host
    public String[] findAliases() {
        String[] strArr;
        synchronized (this.aliasesLock) {
            strArr = this.aliases;
        }
        return strArr;
    }

    @Override // org.apache.catalina.Host
    public void removeAlias(String alias) {
        String alias2 = alias.toLowerCase(Locale.ENGLISH);
        synchronized (this.aliasesLock) {
            int n = -1;
            int i = 0;
            while (true) {
                if (i >= this.aliases.length) {
                    break;
                }
                if (!this.aliases[i].equals(alias2)) {
                    i++;
                } else {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            int j = 0;
            String[] results = new String[this.aliases.length - 1];
            for (int i2 = 0; i2 < this.aliases.length; i2++) {
                if (i2 != n) {
                    int i3 = j;
                    j++;
                    results[i3] = this.aliases[i2];
                }
            }
            this.aliases = results;
            fireContainerEvent(Host.REMOVE_ALIAS_EVENT, alias2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.core.ContainerBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        String errorValve = getErrorReportValveClass();
        if (errorValve != null && !errorValve.equals("")) {
            try {
                boolean found = false;
                Valve[] valves = getPipeline().getValves();
                int length = valves.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Valve valve = valves[i];
                    if (!errorValve.equals(valve.getClass().getName())) {
                        i++;
                    } else {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Valve valve2 = ErrorReportValve.class.getName().equals(errorValve) ? new ErrorReportValve() : (Valve) Class.forName(errorValve).getConstructor(new Class[0]).newInstance(new Object[0]);
                    getPipeline().addValve(valve2);
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("standardHost.invalidErrorReportValveClass", errorValve), t);
            }
        }
        super.startInternal();
    }

    public String[] getValveNames() throws Exception {
        ObjectName oname;
        Valve[] valves = getPipeline().getValves();
        String[] mbeanNames = new String[valves.length];
        for (int i = 0; i < valves.length; i++) {
            if ((valves[i] instanceof JmxEnabled) && (oname = ((JmxEnabled) valves[i]).getObjectName()) != null) {
                mbeanNames[i] = oname.toString();
            }
        }
        return mbeanNames;
    }

    public String[] getAliases() {
        String[] strArr;
        synchronized (this.aliasesLock) {
            strArr = this.aliases;
        }
        return strArr;
    }

    @Override // org.apache.catalina.util.LifecycleMBeanBase
    protected String getObjectNameKeyProperties() {
        return "type=Host" + getMBeanKeyProperties();
    }
}

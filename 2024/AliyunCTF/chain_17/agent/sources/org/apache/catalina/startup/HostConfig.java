package org.apache.catalina.startup;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.security.DeployXmlPermission;
import org.apache.catalina.util.ContextName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jakartaee.Migration;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig.class */
public class HostConfig implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) HostConfig.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) HostConfig.class);
    protected static final long FILE_MODIFICATION_RESOLUTION_MS = 1000;
    protected String contextClass = "org.apache.catalina.core.StandardContext";
    protected Host host = null;
    protected ObjectName oname = null;
    protected boolean deployXML = false;
    protected boolean copyXML = false;
    protected boolean unpackWARs = false;
    protected final Map<String, DeployedApplication> deployed = new ConcurrentHashMap();
    private Set<String> servicedSet = ConcurrentHashMap.newKeySet();
    protected Digester digester = createDigester(this.contextClass);
    private final Object digesterLock = new Object();
    protected final Set<String> invalidWars = new HashSet();

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        if (!oldContextClass.equals(contextClass)) {
            synchronized (this.digesterLock) {
                this.digester = createDigester(getContextClass());
            }
        }
    }

    public boolean isDeployXML() {
        return this.deployXML;
    }

    public void setDeployXML(boolean deployXML) {
        this.deployXML = deployXML;
    }

    private boolean isDeployThisXML(File docBase, ContextName cn2) {
        Policy currentPolicy;
        boolean deployThisXML = isDeployXML();
        if (Globals.IS_SECURITY_ENABLED && !deployThisXML && (currentPolicy = Policy.getPolicy()) != null) {
            try {
                URL contextRootUrl = docBase.toURI().toURL();
                CodeSource cs = new CodeSource(contextRootUrl, (Certificate[]) null);
                PermissionCollection pc = currentPolicy.getPermissions(cs);
                Permission p = new DeployXmlPermission(cn2.getBaseName());
                if (pc.implies(p)) {
                    deployThisXML = true;
                }
            } catch (MalformedURLException e) {
                log.warn(sm.getString("hostConfig.docBaseUrlInvalid"), e);
            }
        }
        return deployThisXML;
    }

    public boolean isCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }

    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            this.host = (Host) event.getLifecycle();
            if (this.host instanceof StandardHost) {
                setCopyXML(((StandardHost) this.host).isCopyXML());
                setDeployXML(((StandardHost) this.host).isDeployXML());
                setUnpackWARs(((StandardHost) this.host).isUnpackWARs());
                setContextClass(((StandardHost) this.host).getContextClass());
            }
            if (event.getType().equals(Lifecycle.PERIODIC_EVENT)) {
                check();
                return;
            }
            if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
                beforeStart();
            } else if (event.getType().equals(Lifecycle.START_EVENT)) {
                start();
            } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
                stop();
            }
        } catch (ClassCastException e) {
            log.error(sm.getString("hostConfig.cce", event.getLifecycle()), e);
        }
    }

    public boolean tryAddServiced(String name) {
        if (this.servicedSet.add(name)) {
            return true;
        }
        return false;
    }

    public void removeServiced(String name) {
        this.servicedSet.remove(name);
    }

    public long getDeploymentTime(String name) {
        synchronized (this.host) {
            DeployedApplication app = this.deployed.get(name);
            if (app == null) {
                return 0L;
            }
            return app.timestamp;
        }
    }

    public boolean isDeployed(String name) {
        return this.deployed.containsKey(name);
    }

    protected static Digester createDigester(String contextClassName) {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("Context", contextClassName, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties("Context");
        return digester;
    }

    protected File returnCanonicalPath(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(this.host.getCatalinaBase(), path);
        }
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
    }

    public String getConfigBaseName() {
        return this.host.getConfigBaseFile().getAbsolutePath();
    }

    protected void deployApps() {
        migrateLegacyApps();
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        String[] filteredAppPaths = filterAppPaths(appBase.list());
        deployDescriptors(configBase, configBase.list());
        deployWARs(appBase, filteredAppPaths);
        deployDirectories(appBase, filteredAppPaths);
    }

    protected String[] filterAppPaths(String[] unfilteredAppPaths) {
        Pattern filter = this.host.getDeployIgnorePattern();
        if (filter == null || unfilteredAppPaths == null) {
            return unfilteredAppPaths;
        }
        List<String> filteredList = new ArrayList<>();
        Matcher matcher = null;
        for (String appPath : unfilteredAppPaths) {
            if (matcher == null) {
                matcher = filter.matcher(appPath);
            } else {
                matcher.reset(appPath);
            }
            if (matcher.matches()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("hostConfig.ignorePath", appPath));
                }
            } else {
                filteredList.add(appPath);
            }
        }
        return (String[]) filteredList.toArray(new String[0]);
    }

    protected void deployApps(String name) {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        ContextName cn2 = new ContextName(name, false);
        String baseName = cn2.getBaseName();
        if (deploymentExists(cn2.getName())) {
            return;
        }
        File xml = new File(configBase, baseName + ".xml");
        if (xml.exists()) {
            deployDescriptor(cn2, xml);
            return;
        }
        File war = new File(appBase, baseName + ".war");
        if (war.exists()) {
            deployWAR(cn2, war);
            return;
        }
        File dir = new File(appBase, baseName);
        if (dir.exists()) {
            deployDirectory(cn2, dir);
        }
    }

    protected void deployDescriptors(File configBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (String file : files) {
            File contextXml = new File(configBase, file);
            if (file.toLowerCase(Locale.ENGLISH).endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                ContextName cn2 = new ContextName(file, true);
                if (tryAddServiced(cn2.getName())) {
                    try {
                        if (deploymentExists(cn2.getName())) {
                            removeServiced(cn2.getName());
                        } else {
                            results.add(es.submit(new DeployDescriptor(this, cn2, contextXml)));
                        }
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        removeServiced(cn2.getName());
                        throw t;
                    }
                } else {
                    continue;
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployDescriptor.threaded.error"), e);
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    protected void deployDescriptor(ContextName cn2, File contextXml) {
        DeployedApplication deployedApp = new DeployedApplication(cn2.getName(), true);
        long startTime = 0;
        if (log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            log.info(sm.getString("hostConfig.deployDescriptor", contextXml.getAbsolutePath()));
        }
        Context context = null;
        boolean isExternalWar = false;
        boolean isExternal = false;
        try {
            try {
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("hostConfig.deployDescriptor.error", contextXml.getAbsolutePath()), t);
                File expandedDocBase = new File(this.host.getAppBaseFile(), cn2.getBaseName());
                if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    expandedDocBase = new File(context.getDocBase());
                    if (!expandedDocBase.isAbsolute()) {
                        expandedDocBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                    }
                }
                boolean unpackWAR = this.unpackWARs;
                if (unpackWAR && (context instanceof StandardContext)) {
                    unpackWAR = ((StandardContext) null).getUnpackWAR();
                }
                if (0 == 0) {
                    if (0 == 0) {
                        File warDocBase = new File(expandedDocBase.getAbsolutePath() + ".war");
                        if (warDocBase.exists()) {
                            deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), Long.valueOf(warDocBase.lastModified()));
                        } else {
                            deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), 0L);
                        }
                    }
                    if (unpackWAR) {
                        deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), Long.valueOf(expandedDocBase.lastModified()));
                        addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), null);
                    } else {
                        addWatchedResources(deployedApp, null, null);
                    }
                    if (0 == 0) {
                        deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                    }
                } else if (unpackWAR) {
                    deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), Long.valueOf(expandedDocBase.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), null);
                } else {
                    addWatchedResources(deployedApp, null, null);
                }
                addGlobalRedeployResources(deployedApp);
            }
            try {
                synchronized (this.digesterLock) {
                    try {
                        FileInputStream fis = new FileInputStream(contextXml);
                        try {
                            context = (Context) this.digester.parse(fis);
                            fis.close();
                            this.digester.reset();
                            if (context == null) {
                                context = new FailedContext();
                            }
                        } catch (Throwable th) {
                            try {
                                fis.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (Exception e) {
                        log.error(sm.getString("hostConfig.deployDescriptor.error", contextXml.getAbsolutePath()), e);
                        this.digester.reset();
                        if (0 == 0) {
                            context = new FailedContext();
                        }
                    }
                }
                if (context.getPath() != null) {
                    log.warn(sm.getString("hostConfig.deployDescriptor.path", context.getPath(), contextXml.getAbsolutePath()));
                }
                Class<?> clazz = Class.forName(this.host.getConfigClass());
                LifecycleListener listener = (LifecycleListener) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                context.addLifecycleListener(listener);
                context.setConfigFile(contextXml.toURI().toURL());
                context.setName(cn2.getName());
                context.setPath(cn2.getPath());
                context.setWebappVersion(cn2.getVersion());
                if (context.getDocBase() != null) {
                    File docBase = new File(context.getDocBase());
                    if (!docBase.isAbsolute()) {
                        docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                    }
                    if (docBase.getCanonicalFile().toPath().startsWith(this.host.getAppBaseFile().toPath())) {
                        log.warn(sm.getString("hostConfig.deployDescriptor.localDocBaseSpecified", docBase));
                        context.setDocBase(null);
                    } else {
                        isExternal = true;
                        deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                        deployedApp.redeployResources.put(docBase.getAbsolutePath(), Long.valueOf(docBase.lastModified()));
                        if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                            isExternalWar = true;
                        }
                        File war = new File(this.host.getAppBaseFile(), cn2.getBaseName() + ".war");
                        if (war.exists()) {
                            log.warn(sm.getString("hostConfig.deployDescriptor.hiddenWar", contextXml.getAbsolutePath(), war.getAbsolutePath()));
                        }
                        File dir = new File(this.host.getAppBaseFile(), cn2.getBaseName());
                        if (dir.exists()) {
                            log.warn(sm.getString("hostConfig.deployDescriptor.hiddenDir", contextXml.getAbsolutePath(), dir.getAbsolutePath()));
                        }
                    }
                }
                this.host.addChild(context);
                File expandedDocBase2 = new File(this.host.getAppBaseFile(), cn2.getBaseName());
                if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    expandedDocBase2 = new File(context.getDocBase());
                    if (!expandedDocBase2.isAbsolute()) {
                        expandedDocBase2 = new File(this.host.getAppBaseFile(), context.getDocBase());
                    }
                }
                boolean unpackWAR2 = this.unpackWARs;
                if (unpackWAR2 && (context instanceof StandardContext)) {
                    unpackWAR2 = ((StandardContext) context).getUnpackWAR();
                }
                if (!isExternalWar) {
                    if (!isExternal) {
                        File warDocBase2 = new File(expandedDocBase2.getAbsolutePath() + ".war");
                        if (warDocBase2.exists()) {
                            deployedApp.redeployResources.put(warDocBase2.getAbsolutePath(), Long.valueOf(warDocBase2.lastModified()));
                        } else {
                            deployedApp.redeployResources.put(warDocBase2.getAbsolutePath(), 0L);
                        }
                    }
                    if (unpackWAR2) {
                        deployedApp.redeployResources.put(expandedDocBase2.getAbsolutePath(), Long.valueOf(expandedDocBase2.lastModified()));
                        addWatchedResources(deployedApp, expandedDocBase2.getAbsolutePath(), context);
                    } else {
                        addWatchedResources(deployedApp, null, context);
                    }
                    if (!isExternal) {
                        deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                    }
                } else if (unpackWAR2) {
                    deployedApp.redeployResources.put(expandedDocBase2.getAbsolutePath(), Long.valueOf(expandedDocBase2.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase2.getAbsolutePath(), context);
                } else {
                    addWatchedResources(deployedApp, null, context);
                }
                addGlobalRedeployResources(deployedApp);
                if (this.host.findChild(context.getName()) != null) {
                    this.deployed.put(context.getName(), deployedApp);
                }
                if (log.isInfoEnabled()) {
                    log.info(sm.getString("hostConfig.deployDescriptor.finished", contextXml.getAbsolutePath(), Long.valueOf(System.currentTimeMillis() - startTime)));
                }
            } catch (Throwable th3) {
                this.digester.reset();
                if (0 == 0) {
                    new FailedContext();
                }
                throw th3;
            }
        } catch (Throwable th4) {
            File expandedDocBase3 = new File(this.host.getAppBaseFile(), cn2.getBaseName());
            if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                expandedDocBase3 = new File(context.getDocBase());
                if (!expandedDocBase3.isAbsolute()) {
                    expandedDocBase3 = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
            }
            boolean unpackWAR3 = this.unpackWARs;
            if (unpackWAR3 && (context instanceof StandardContext)) {
                unpackWAR3 = ((StandardContext) null).getUnpackWAR();
            }
            if (0 == 0) {
                if (0 == 0) {
                    File warDocBase3 = new File(expandedDocBase3.getAbsolutePath() + ".war");
                    if (warDocBase3.exists()) {
                        deployedApp.redeployResources.put(warDocBase3.getAbsolutePath(), Long.valueOf(warDocBase3.lastModified()));
                    } else {
                        deployedApp.redeployResources.put(warDocBase3.getAbsolutePath(), 0L);
                    }
                }
                if (unpackWAR3) {
                    deployedApp.redeployResources.put(expandedDocBase3.getAbsolutePath(), Long.valueOf(expandedDocBase3.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase3.getAbsolutePath(), null);
                } else {
                    addWatchedResources(deployedApp, null, null);
                }
                if (0 == 0) {
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                }
            } else if (unpackWAR3) {
                deployedApp.redeployResources.put(expandedDocBase3.getAbsolutePath(), Long.valueOf(expandedDocBase3.lastModified()));
                addWatchedResources(deployedApp, expandedDocBase3.getAbsolutePath(), null);
            } else {
                addWatchedResources(deployedApp, null, null);
            }
            addGlobalRedeployResources(deployedApp);
            throw th4;
        }
    }

    protected void deployWARs(File appBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (String file : files) {
            if (!file.equalsIgnoreCase("META-INF") && !file.equalsIgnoreCase("WEB-INF")) {
                File war = new File(appBase, file);
                if (file.toLowerCase(Locale.ENGLISH).endsWith(".war") && war.isFile() && !this.invalidWars.contains(file)) {
                    ContextName cn2 = new ContextName(file, true);
                    if (tryAddServiced(cn2.getName())) {
                        try {
                            if (deploymentExists(cn2.getName())) {
                                DeployedApplication app = this.deployed.get(cn2.getName());
                                boolean unpackWAR = this.unpackWARs;
                                if (unpackWAR && (this.host.findChild(cn2.getName()) instanceof StandardContext)) {
                                    unpackWAR = ((StandardContext) this.host.findChild(cn2.getName())).getUnpackWAR();
                                }
                                if (!unpackWAR && app != null) {
                                    File dir = new File(appBase, cn2.getBaseName());
                                    if (dir.exists()) {
                                        if (!app.loggedDirWarning) {
                                            log.warn(sm.getString("hostConfig.deployWar.hiddenDir", dir.getAbsoluteFile(), war.getAbsoluteFile()));
                                            app.loggedDirWarning = true;
                                        }
                                    } else {
                                        app.loggedDirWarning = false;
                                    }
                                }
                                removeServiced(cn2.getName());
                            } else if (!validateContextPath(appBase, cn2.getBaseName())) {
                                log.error(sm.getString("hostConfig.illegalWarName", file));
                                this.invalidWars.add(file);
                                removeServiced(cn2.getName());
                            } else {
                                results.add(es.submit(new DeployWar(this, cn2, war)));
                            }
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            removeServiced(cn2.getName());
                            throw t;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployWar.threaded.error"), e);
            }
        }
    }

    private boolean validateContextPath(File appBase, String contextPath) {
        try {
            String canonicalAppBase = appBase.getCanonicalPath();
            StringBuilder docBase = new StringBuilder(canonicalAppBase);
            if (canonicalAppBase.endsWith(File.separator)) {
                docBase.append(contextPath.substring(1).replace('/', File.separatorChar));
            } else {
                docBase.append(contextPath.replace('/', File.separatorChar));
            }
            String canonicalDocBase = new File(docBase.toString()).getCanonicalPath();
            if (canonicalDocBase.endsWith(File.separator)) {
                docBase.append(File.separator);
            }
            return canonicalDocBase.equals(docBase.toString());
        } catch (IOException e) {
            return false;
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(27:21|22|(3:26|27|(24:29|30|c3|39|40|41|(1:43)|44|(6:46|(1:48)|49|(1:53)|54|(15:58|59|60|61|62|63|64|65|66|67|68|(1:70)|71|72|73))|103|(1:195)(1:109)|110|(1:112)|113|114|(1:148)(1:120)|121|122|(1:126)|127|(1:146)(2:131|(1:139))|140|141|(2:143|144)(1:145)))|219|(2:282|(1:287)(1:286))(5:223|224|16b|243|244)|41|(0)|44|(0)|103|(1:105)|195|110|(0)|113|114|(1:116)|148|121|122|(2:124|126)|127|(1:129)|146|140|141|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x05f4, code lost:            r24 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x05f6, code lost:            org.apache.tomcat.util.ExceptionUtils.handleThrowable(r24);        org.apache.catalina.startup.HostConfig.log.error(org.apache.catalina.startup.HostConfig.sm.getString("hostConfig.deployWar.error", r13.getAbsolutePath()), r24);     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x061a, code lost:            r24 = r11.unpackWARs;     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x0621, code lost:            if (r24 != false) goto L225;     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x062c, code lost:            r24 = ((org.apache.catalina.core.StandardContext) r18).getUnpackWAR();     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x0638, code lost:            if (r24 == false) goto L242;     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x0645, code lost:            r0 = new java.io.File(r11.host.getAppBaseFile(), r12.getBaseName());        r0.redeployResources.put(r0.getAbsolutePath(), java.lang.Long.valueOf(r0.lastModified()));        addWatchedResources(r0, r0.getAbsolutePath(), r18);     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x0680, code lost:            if (r0 != false) goto L234;     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x0694, code lost:            r0.redeployResources.put(r14.getAbsolutePath(), java.lang.Long.valueOf(r14.lastModified()));     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x06b4, code lost:            addGlobalRedeployResources(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x06ab, code lost:            addWatchedResources(r0, null, r18);     */
    /* JADX WARN: Removed duplicated region for block: B:112:0x0462  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x077e  */
    /* JADX WARN: Removed duplicated region for block: B:145:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x02ec  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0349  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void deployWAR(org.apache.catalina.util.ContextName r12, java.io.File r13) {
        /*
            Method dump skipped, instructions count: 1959
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.deployWAR(org.apache.catalina.util.ContextName, java.io.File):void");
    }

    protected void deployDirectories(File appBase, String[] files) {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (String file : files) {
            if (!file.equalsIgnoreCase("META-INF") && !file.equalsIgnoreCase("WEB-INF")) {
                File dir = new File(appBase, file);
                if (dir.isDirectory()) {
                    ContextName cn2 = new ContextName(file, false);
                    if (tryAddServiced(cn2.getName())) {
                        try {
                            if (deploymentExists(cn2.getName())) {
                                removeServiced(cn2.getName());
                            } else {
                                results.add(es.submit(new DeployDirectory(this, cn2, dir)));
                            }
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            removeServiced(cn2.getName());
                            throw t;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployDir.threaded.error"), e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0507  */
    /* JADX WARN: Removed duplicated region for block: B:34:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x02d4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void deployDirectory(org.apache.catalina.util.ContextName r12, java.io.File r13) {
        /*
            Method dump skipped, instructions count: 1327
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.deployDirectory(org.apache.catalina.util.ContextName, java.io.File):void");
    }

    protected void migrateLegacyApps() {
        ContextName cn2;
        File appBase = this.host.getAppBaseFile();
        File legacyAppBase = this.host.getLegacyAppBaseFile();
        if (!legacyAppBase.isDirectory()) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        String[] migrationCandidates = legacyAppBase.list();
        if (migrationCandidates == null) {
            return;
        }
        for (String migrationCandidate : migrationCandidates) {
            File source = new File(legacyAppBase, migrationCandidate);
            File destination = new File(appBase, migrationCandidate);
            if (source.lastModified() > destination.lastModified()) {
                if (source.isFile() && source.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    cn2 = new ContextName(migrationCandidate, true);
                } else if (source.isDirectory()) {
                    cn2 = new ContextName(migrationCandidate, false);
                } else {
                    continue;
                }
                if (tryAddServiced(cn2.getBaseName())) {
                    try {
                        results.add(es.submit(new MigrateApp(this, cn2, source, destination)));
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        removeServiced(cn2.getName());
                        throw t;
                    }
                } else {
                    continue;
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.migrateApp.threaded.error"), e);
            }
        }
    }

    protected void migrateLegacyApp(File source, File destination) {
        File tempNew = null;
        try {
            try {
                tempNew = File.createTempFile("new", null, this.host.getLegacyAppBaseFile());
                File tempOld = File.createTempFile("old", null, this.host.getLegacyAppBaseFile());
                Files.delete(tempNew.toPath());
                Files.delete(tempOld.toPath());
                Migration migration = new Migration();
                migration.setSource(source);
                migration.setDestination(tempNew);
                migration.execute();
                if (destination.exists()) {
                    Files.move(destination.toPath(), tempOld.toPath(), new CopyOption[0]);
                }
                Files.move(tempNew.toPath(), destination.toPath(), new CopyOption[0]);
                ExpandWar.delete(tempOld);
                if (tempNew != null && tempNew.exists()) {
                    ExpandWar.delete(tempNew);
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.warn(sm.getString("hostConfig.migrateError"), t);
                if (tempNew != null && tempNew.exists()) {
                    ExpandWar.delete(tempNew);
                }
            }
        } catch (Throwable th) {
            if (tempNew != null && tempNew.exists()) {
                ExpandWar.delete(tempNew);
            }
            throw th;
        }
    }

    protected boolean deploymentExists(String contextName) {
        return this.deployed.containsKey(contextName) || this.host.findChild(contextName) != null;
    }

    protected void addWatchedResources(DeployedApplication app, String docBase, Context context) {
        File docBaseFile = null;
        if (docBase != null) {
            docBaseFile = new File(docBase);
            if (!docBaseFile.isAbsolute()) {
                docBaseFile = new File(this.host.getAppBaseFile(), docBase);
            }
        }
        String[] watchedResources = context.findWatchedResources();
        for (String watchedResource : watchedResources) {
            File resource = new File(watchedResource);
            if (!resource.isAbsolute()) {
                if (docBase != null) {
                    resource = new File(docBaseFile, watchedResource);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignoring non-existent WatchedResource '" + resource.getAbsolutePath() + "'");
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Watching WatchedResource '" + resource.getAbsolutePath() + "'");
            }
            app.reloadResources.put(resource.getAbsolutePath(), Long.valueOf(resource.lastModified()));
        }
    }

    protected void addGlobalRedeployResources(DeployedApplication app) {
        File hostContextXml = new File(getConfigBaseName(), Constants.HostContextXml);
        if (hostContextXml.isFile()) {
            app.redeployResources.put(hostContextXml.getAbsolutePath(), Long.valueOf(hostContextXml.lastModified()));
        }
        File globalContextXml = returnCanonicalPath(Constants.DefaultContextXml);
        if (globalContextXml.isFile()) {
            app.redeployResources.put(globalContextXml.getAbsolutePath(), Long.valueOf(globalContextXml.lastModified()));
        }
    }

    protected void checkResources(DeployedApplication app, boolean skipFileModificationResolutionCheck) {
        String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);
        long currentTimeWithResolutionOffset = System.currentTimeMillis() - 1000;
        for (int i = 0; i < resources.length; i++) {
            File resource = new File(resources[i]);
            if (log.isDebugEnabled()) {
                log.debug("Checking context[" + app.name + "] redeploy resource " + resource);
            }
            long lastModified = app.redeployResources.get(resources[i]).longValue();
            if (!resource.exists() && lastModified != 0) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                if (!resource.exists()) {
                    undeploy(app);
                    deleteRedeployResources(app, resources, i, true);
                    return;
                }
            } else if (resource.lastModified() != lastModified && (!this.host.getAutoDeploy() || resource.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) {
                if (resource.isDirectory()) {
                    app.redeployResources.put(resources[i], Long.valueOf(resource.lastModified()));
                } else {
                    if (app.hasDescriptor && resource.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        Context context = (Context) this.host.findChild(app.name);
                        String docBase = context.getDocBase();
                        if (!docBase.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                            File docBaseFile = new File(docBase);
                            if (!docBaseFile.isAbsolute()) {
                                docBaseFile = new File(this.host.getAppBaseFile(), docBase);
                            }
                            reload(app, docBaseFile, resource.getAbsolutePath());
                        } else {
                            reload(app, null, null);
                        }
                        app.redeployResources.put(resources[i], Long.valueOf(resource.lastModified()));
                        app.timestamp = System.currentTimeMillis();
                        boolean unpackWAR = this.unpackWARs;
                        if (unpackWAR && (context instanceof StandardContext)) {
                            unpackWAR = ((StandardContext) context).getUnpackWAR();
                        }
                        if (unpackWAR) {
                            addWatchedResources(app, context.getDocBase(), context);
                            return;
                        } else {
                            addWatchedResources(app, null, context);
                            return;
                        }
                    }
                    undeploy(app);
                    deleteRedeployResources(app, resources, i, false);
                    return;
                }
            }
        }
        boolean update = false;
        for (String s : (String[]) app.reloadResources.keySet().toArray(new String[0])) {
            File resource2 = new File(s);
            if (log.isDebugEnabled()) {
                log.debug("Checking context[" + app.name + "] reload resource " + resource2);
            }
            if ((resource2.lastModified() != app.reloadResources.get(s).longValue() && (!this.host.getAutoDeploy() || resource2.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) || update) {
                if (!update) {
                    reload(app, null, null);
                    update = true;
                }
                app.reloadResources.put(s, Long.valueOf(resource2.lastModified()));
            }
            app.timestamp = System.currentTimeMillis();
        }
    }

    private void reload(DeployedApplication app, File fileToRemove, String newDocBase) {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("hostConfig.reload", app.name));
        }
        Context context = (Context) this.host.findChild(app.name);
        if (context.getState().isAvailable()) {
            if (fileToRemove != null && newDocBase != null) {
                context.addLifecycleListener(new ExpandedDirectoryRemovalListener(fileToRemove, newDocBase));
            }
            context.reload();
            return;
        }
        if (fileToRemove != null && newDocBase != null) {
            ExpandWar.delete(fileToRemove);
            context.setDocBase(newDocBase);
        }
        try {
            context.start();
        } catch (Exception e) {
            log.error(sm.getString("hostConfig.context.restart", app.name), e);
        }
    }

    private void undeploy(DeployedApplication app) {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("hostConfig.undeploy", app.name));
        }
        Container context = this.host.findChild(app.name);
        try {
            this.host.removeChild(context);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.warn(sm.getString("hostConfig.context.remove", app.name), t);
        }
        this.deployed.remove(app.name);
    }

    private void deleteRedeployResources(DeployedApplication app, String[] resources, int i, boolean deleteReloadResources) {
        for (int j = i + 1; j < resources.length; j++) {
            File current = new File(resources[j]);
            if (!Constants.HostContextXml.equals(current.getName()) && isDeletableResource(app, current)) {
                if (log.isDebugEnabled()) {
                    log.debug("Delete " + current);
                }
                ExpandWar.delete(current);
            }
        }
        if (deleteReloadResources) {
            String[] resources2 = (String[]) app.reloadResources.keySet().toArray(new String[0]);
            for (String s : resources2) {
                File current2 = new File(s);
                if (!Constants.HostContextXml.equals(current2.getName()) && isDeletableResource(app, current2)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Delete " + current2);
                    }
                    ExpandWar.delete(current2);
                }
            }
        }
    }

    private boolean isDeletableResource(DeployedApplication app, File resource) {
        if (!resource.isAbsolute()) {
            log.warn(sm.getString("hostConfig.resourceNotAbsolute", app.name, resource));
            return false;
        }
        try {
            String canonicalLocation = resource.getParentFile().getCanonicalPath();
            try {
                String canonicalAppBase = this.host.getAppBaseFile().getCanonicalPath();
                if (canonicalLocation.equals(canonicalAppBase)) {
                    return true;
                }
                try {
                    String canonicalConfigBase = this.host.getConfigBaseFile().getCanonicalPath();
                    if (canonicalLocation.equals(canonicalConfigBase) && resource.getName().endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                        return true;
                    }
                    return false;
                } catch (IOException e) {
                    log.warn(sm.getString("hostConfig.canonicalizing", this.host.getConfigBaseFile(), app.name), e);
                    return false;
                }
            } catch (IOException e2) {
                log.warn(sm.getString("hostConfig.canonicalizing", this.host.getAppBaseFile(), app.name), e2);
                return false;
            }
        } catch (IOException e3) {
            log.warn(sm.getString("hostConfig.canonicalizing", resource.getParentFile(), app.name), e3);
            return false;
        }
    }

    public void beforeStart() {
        if (this.host.getCreateDirs()) {
            File[] dirs = {this.host.getAppBaseFile(), this.host.getConfigBaseFile()};
            for (File dir : dirs) {
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    log.error(sm.getString("hostConfig.createDirs", dir));
                }
            }
        }
    }

    public void start() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hostConfig.start"));
        }
        try {
            ObjectName hostON = this.host.getObjectName();
            this.oname = new ObjectName(hostON.getDomain() + ":type=Deployer,host=" + this.host.getName());
            Registry.getRegistry(null, null).registerComponent(this, this.oname, getClass().getName());
        } catch (Exception e) {
            log.warn(sm.getString("hostConfig.jmx.register", this.oname), e);
        }
        if (!this.host.getAppBaseFile().isDirectory()) {
            log.error(sm.getString("hostConfig.appBase", this.host.getName(), this.host.getAppBaseFile().getPath()));
            this.host.setDeployOnStartup(false);
            this.host.setAutoDeploy(false);
        }
        if (this.host.getDeployOnStartup()) {
            deployApps();
        }
    }

    public void stop() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hostConfig.stop"));
        }
        if (this.oname != null) {
            try {
                Registry.getRegistry(null, null).unregisterComponent(this.oname);
            } catch (Exception e) {
                log.warn(sm.getString("hostConfig.jmx.unregister", this.oname), e);
            }
        }
        this.oname = null;
    }

    protected void check() {
        if (this.host.getAutoDeploy()) {
            DeployedApplication[] apps = (DeployedApplication[]) this.deployed.values().toArray(new DeployedApplication[0]);
            for (DeployedApplication app : apps) {
                if (tryAddServiced(app.name)) {
                    try {
                        checkResources(app, false);
                        removeServiced(app.name);
                    } catch (Throwable th) {
                        removeServiced(app.name);
                        throw th;
                    }
                }
            }
            if (this.host.getUndeployOldVersions()) {
                checkUndeploy();
            }
            deployApps();
        }
    }

    public void check(String name) {
        synchronized (this.host) {
            if (this.host.getState().isAvailable()) {
                if (tryAddServiced(name)) {
                    try {
                        DeployedApplication app = this.deployed.get(name);
                        if (app != null) {
                            checkResources(app, true);
                        }
                        deployApps(name);
                        removeServiced(name);
                    } catch (Throwable th) {
                        removeServiced(name);
                        throw th;
                    }
                }
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    public void checkUndeploy() {
        int sessionCount;
        synchronized (this.host) {
            if (this.deployed.size() < 2) {
                return;
            }
            SortedSet<String> sortedAppNames = new TreeSet<>(this.deployed.keySet());
            Iterator<String> iter = sortedAppNames.iterator();
            ContextName previous = new ContextName(iter.next(), false);
            do {
                ContextName current = new ContextName(iter.next(), false);
                if (current.getPath().equals(previous.getPath())) {
                    Context previousContext = (Context) this.host.findChild(previous.getName());
                    Context currentContext = (Context) this.host.findChild(current.getName());
                    if (previousContext != null && currentContext != null && currentContext.getState().isAvailable() && tryAddServiced(previous.getName())) {
                        try {
                            Manager manager = previousContext.getManager();
                            if (manager != null) {
                                if (manager instanceof DistributedManager) {
                                    sessionCount = ((DistributedManager) manager).getActiveSessionsFull();
                                } else {
                                    sessionCount = manager.getActiveSessions();
                                }
                                if (sessionCount == 0) {
                                    if (log.isInfoEnabled()) {
                                        log.info(sm.getString("hostConfig.undeployVersion", previous.getName()));
                                    }
                                    DeployedApplication app = this.deployed.get(previous.getName());
                                    String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);
                                    undeploy(app);
                                    deleteRedeployResources(app, resources, -1, true);
                                }
                            }
                            removeServiced(previous.getName());
                        } catch (Throwable th) {
                            removeServiced(previous.getName());
                            throw th;
                        }
                    }
                }
                previous = current;
            } while (iter.hasNext());
        }
    }

    public void manageApp(Context context) {
        String contextName = context.getName();
        if (this.deployed.containsKey(contextName)) {
            return;
        }
        DeployedApplication deployedApp = new DeployedApplication(contextName, false);
        boolean isWar = false;
        if (context.getDocBase() != null) {
            File docBase = new File(context.getDocBase());
            if (!docBase.isAbsolute()) {
                docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
            }
            deployedApp.redeployResources.put(docBase.getAbsolutePath(), Long.valueOf(docBase.lastModified()));
            if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                isWar = true;
            }
        }
        this.host.addChild(context);
        boolean unpackWAR = this.unpackWARs;
        if (unpackWAR && (context instanceof StandardContext)) {
            unpackWAR = ((StandardContext) context).getUnpackWAR();
        }
        if (isWar && unpackWAR) {
            File docBase2 = new File(this.host.getAppBaseFile(), context.getBaseName());
            deployedApp.redeployResources.put(docBase2.getAbsolutePath(), Long.valueOf(docBase2.lastModified()));
            addWatchedResources(deployedApp, docBase2.getAbsolutePath(), context);
        } else {
            addWatchedResources(deployedApp, null, context);
        }
        this.deployed.put(contextName, deployedApp);
    }

    public void unmanageApp(String contextName) {
        this.deployed.remove(contextName);
        this.host.removeChild(this.host.findChild(contextName));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig$DeployedApplication.class */
    public static class DeployedApplication {
        public final String name;
        public final boolean hasDescriptor;
        public final LinkedHashMap<String, Long> redeployResources = new LinkedHashMap<>();
        public final HashMap<String, Long> reloadResources = new HashMap<>();
        public long timestamp = System.currentTimeMillis();
        public boolean loggedDirWarning = false;

        public DeployedApplication(String name, boolean hasDescriptor) {
            this.name = name;
            this.hasDescriptor = hasDescriptor;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig$DeployDescriptor.class */
    public static class DeployDescriptor implements Runnable {
        private HostConfig config;

        /* renamed from: cn, reason: collision with root package name */
        private ContextName f0cn;
        private File descriptor;

        DeployDescriptor(HostConfig config, ContextName cn2, File descriptor) {
            this.config = config;
            this.f0cn = cn2;
            this.descriptor = descriptor;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.deployDescriptor(this.f0cn, this.descriptor);
            } finally {
                this.config.removeServiced(this.f0cn.getName());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig$DeployWar.class */
    public static class DeployWar implements Runnable {
        private HostConfig config;

        /* renamed from: cn, reason: collision with root package name */
        private ContextName f2cn;
        private File war;

        DeployWar(HostConfig config, ContextName cn2, File war) {
            this.config = config;
            this.f2cn = cn2;
            this.war = war;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.deployWAR(this.f2cn, this.war);
            } finally {
                this.config.removeServiced(this.f2cn.getName());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig$DeployDirectory.class */
    public static class DeployDirectory implements Runnable {
        private HostConfig config;

        /* renamed from: cn, reason: collision with root package name */
        private ContextName f1cn;
        private File dir;

        DeployDirectory(HostConfig config, ContextName cn2, File dir) {
            this.config = config;
            this.f1cn = cn2;
            this.dir = dir;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.deployDirectory(this.f1cn, this.dir);
            } finally {
                this.config.removeServiced(this.f1cn.getName());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig$MigrateApp.class */
    public static class MigrateApp implements Runnable {
        private HostConfig config;

        /* renamed from: cn, reason: collision with root package name */
        private ContextName f3cn;
        private File source;
        private File destination;

        MigrateApp(HostConfig config, ContextName cn2, File source, File destination) {
            this.config = config;
            this.f3cn = cn2;
            this.source = source;
            this.destination = destination;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.migrateLegacyApp(this.source, this.destination);
            } finally {
                this.config.removeServiced(this.f3cn.getName());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/HostConfig$ExpandedDirectoryRemovalListener.class */
    public static class ExpandedDirectoryRemovalListener implements LifecycleListener {
        private final File toDelete;
        private final String newDocBase;

        ExpandedDirectoryRemovalListener(File toDelete, String newDocBase) {
            this.toDelete = toDelete;
            this.newDocBase = newDocBase;
        }

        @Override // org.apache.catalina.LifecycleListener
        public void lifecycleEvent(LifecycleEvent event) {
            if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
                Context context = (Context) event.getLifecycle();
                ExpandWar.delete(this.toDelete);
                context.setDocBase(this.newDocBase);
                context.removeLifecycleListener(this);
            }
        }
    }
}

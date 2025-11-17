package org.apache.catalina.startup;

import ch.qos.logback.classic.encoder.JsonEncoder;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.security.SecurityConfig;
import org.apache.juli.ClassLoaderLogManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.log.SystemLogHandler;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/Catalina.class */
public class Catalina {
    public static final String SERVER_XML = "conf/server.xml";
    protected boolean await = false;
    protected String configFile = SERVER_XML;
    protected ClassLoader parentClassLoader = Catalina.class.getClassLoader();
    protected Server server = null;
    protected boolean useShutdownHook = true;
    protected Thread shutdownHook = null;
    protected boolean useNaming = true;
    protected boolean loaded = false;
    protected boolean throwOnInitFailure = Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE");
    protected boolean generateCode = false;
    protected File generatedCodeLocation = null;
    protected String generatedCodeLocationParameter = null;
    protected String generatedCodePackage = "catalinaembedded";
    protected boolean useGeneratedCode = false;
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private static final Log log = LogFactory.getLog((Class<?>) Catalina.class);

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/Catalina$ServerXml.class */
    public interface ServerXml {
        void load(Catalina catalina);
    }

    public Catalina() {
        setSecurityProtection();
        ExceptionUtils.preload();
    }

    public void setConfigFile(String file) {
        this.configFile = file;
    }

    public String getConfigFile() {
        return this.configFile;
    }

    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    public boolean getUseShutdownHook() {
        return this.useShutdownHook;
    }

    public boolean getGenerateCode() {
        return this.generateCode;
    }

    public void setGenerateCode(boolean generateCode) {
        this.generateCode = generateCode;
    }

    public boolean getUseGeneratedCode() {
        return this.useGeneratedCode;
    }

    public void setUseGeneratedCode(boolean useGeneratedCode) {
        this.useGeneratedCode = useGeneratedCode;
    }

    public File getGeneratedCodeLocation() {
        return this.generatedCodeLocation;
    }

    public void setGeneratedCodeLocation(File generatedCodeLocation) {
        this.generatedCodeLocation = generatedCodeLocation;
    }

    public String getGeneratedCodePackage() {
        return this.generatedCodePackage;
    }

    public void setGeneratedCodePackage(String generatedCodePackage) {
        this.generatedCodePackage = generatedCodePackage;
    }

    public boolean getThrowOnInitFailure() {
        return this.throwOnInitFailure;
    }

    public void setThrowOnInitFailure(boolean throwOnInitFailure) {
        this.throwOnInitFailure = throwOnInitFailure;
    }

    public void setParentClassLoader(ClassLoader parentClassLoader) {
        this.parentClassLoader = parentClassLoader;
    }

    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        return ClassLoader.getSystemClassLoader();
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public boolean isUseNaming() {
        return this.useNaming;
    }

    public void setUseNaming(boolean useNaming) {
        this.useNaming = useNaming;
    }

    public void setAwait(boolean b) {
        this.await = b;
    }

    public boolean isAwait() {
        return this.await;
    }

    protected boolean arguments(String[] args) {
        boolean isConfig = false;
        boolean isGenerateCode = false;
        if (args.length < 1) {
            usage();
            return false;
        }
        for (String arg : args) {
            if (isConfig) {
                this.configFile = arg;
                isConfig = false;
            } else if (arg.equals("-config")) {
                isConfig = true;
            } else if (arg.equals("-generateCode")) {
                setGenerateCode(true);
                isGenerateCode = true;
            } else if (arg.equals("-useGeneratedCode")) {
                setUseGeneratedCode(true);
                isGenerateCode = false;
            } else if (arg.equals("-nonaming")) {
                setUseNaming(false);
                isGenerateCode = false;
            } else {
                if (arg.equals("-help")) {
                    usage();
                    return false;
                }
                if (arg.equals(Lifecycle.START_EVENT)) {
                    isGenerateCode = false;
                } else if (arg.equals("configtest")) {
                    isGenerateCode = false;
                } else if (arg.equals(Lifecycle.STOP_EVENT)) {
                    isGenerateCode = false;
                } else if (isGenerateCode) {
                    this.generatedCodeLocationParameter = arg;
                    isGenerateCode = false;
                } else {
                    usage();
                    return false;
                }
            }
        }
        return true;
    }

    protected File configFile() {
        File file = new File(this.configFile);
        if (!file.isAbsolute()) {
            file = new File(Bootstrap.getCatalinaBase(), this.configFile);
        }
        return file;
    }

    protected Digester createStartDigester() {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setRulesValidation(true);
        Map<Class<?>, List<String>> fakeAttributes = new HashMap<>();
        List<String> objectAttrs = new ArrayList<>();
        objectAttrs.add(JsonEncoder.CLASS_NAME_ATTR_NAME);
        fakeAttributes.put(Object.class, objectAttrs);
        List<String> contextAttrs = new ArrayList<>();
        contextAttrs.add("source");
        fakeAttributes.put(StandardContext.class, contextAttrs);
        List<String> connectorAttrs = new ArrayList<>();
        connectorAttrs.add("portOffset");
        fakeAttributes.put(Connector.class, connectorAttrs);
        digester.setFakeAttributes(fakeAttributes);
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate(HttpHeaders.SERVER, "org.apache.catalina.core.StandardServer", JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(HttpHeaders.SERVER);
        digester.addSetNext(HttpHeaders.SERVER, "setServer", "org.apache.catalina.Server");
        digester.addObjectCreate("Server/GlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addSetProperties("Server/GlobalNamingResources");
        digester.addSetNext("Server/GlobalNamingResources", "setGlobalNamingResources", "org.apache.catalina.deploy.NamingResourcesImpl");
        digester.addRule("Server/Listener", new ListenerCreateRule(null, JsonEncoder.CLASS_NAME_ATTR_NAME));
        digester.addSetProperties("Server/Listener");
        digester.addSetNext("Server/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service", "org.apache.catalina.core.StandardService", JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties("Server/Service");
        digester.addSetNext("Server/Service", "addService", "org.apache.catalina.Service");
        digester.addObjectCreate("Server/Service/Listener", null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties("Server/Service/Listener");
        digester.addSetNext("Server/Service/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Executor", "org.apache.catalina.core.StandardThreadExecutor", JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties("Server/Service/Executor");
        digester.addSetNext("Server/Service/Executor", "addExecutor", "org.apache.catalina.Executor");
        digester.addRule("Server/Service/Connector", new ConnectorCreateRule());
        digester.addSetProperties("Server/Service/Connector", new String[]{"executor", "sslImplementationName", "protocol"});
        digester.addSetNext("Server/Service/Connector", "addConnector", "org.apache.catalina.connector.Connector");
        digester.addRule("Server/Service/Connector", new AddPortOffsetRule());
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig", "addSslHostConfig", "org.apache.tomcat.util.net.SSLHostConfig");
        digester.addRule("Server/Service/Connector/SSLHostConfig/Certificate", new CertificateCreateRule());
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/Certificate", new String[]{"type"});
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/Certificate", "addCertificate", "org.apache.tomcat.util.net.SSLHostConfigCertificate");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf", "setOpenSslConf", "org.apache.tomcat.util.net.openssl.OpenSSLConf");
        digester.addObjectCreate("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addSetProperties("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd");
        digester.addSetNext("Server/Service/Connector/SSLHostConfig/OpenSSLConf/OpenSSLConfCmd", "addCmd", "org.apache.tomcat.util.net.openssl.OpenSSLConfCmd");
        digester.addObjectCreate("Server/Service/Connector/Listener", null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties("Server/Service/Connector/Listener");
        digester.addSetNext("Server/Service/Connector/Listener", "addLifecycleListener", "org.apache.catalina.LifecycleListener");
        digester.addObjectCreate("Server/Service/Connector/UpgradeProtocol", null, JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties("Server/Service/Connector/UpgradeProtocol");
        digester.addSetNext("Server/Service/Connector/UpgradeProtocol", "addUpgradeProtocol", "org.apache.coyote.UpgradeProtocol");
        digester.addRuleSet(new NamingRuleSet("Server/GlobalNamingResources/"));
        digester.addRuleSet(new EngineRuleSet("Server/Service/"));
        digester.addRuleSet(new HostRuleSet("Server/Service/Engine/"));
        digester.addRuleSet(new ContextRuleSet("Server/Service/Engine/Host/"));
        addClusterRuleSet(digester, "Server/Service/Engine/Host/Cluster/");
        digester.addRuleSet(new NamingRuleSet("Server/Service/Engine/Host/Context/"));
        digester.addRule("Server/Service/Engine", new SetParentClassLoaderRule(this.parentClassLoader));
        addClusterRuleSet(digester, "Server/Service/Engine/Cluster/");
        return digester;
    }

    private void addClusterRuleSet(Digester digester, String prefix) {
        try {
            Class<?> clazz = Class.forName("org.apache.catalina.ha.ClusterRuleSet");
            Constructor<?> constructor = clazz.getConstructor(String.class);
            RuleSet ruleSet = (RuleSet) constructor.newInstance(prefix);
            digester.addRuleSet(ruleSet);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("catalina.noCluster", e.getClass().getName() + ": " + e.getMessage()), e);
            } else if (log.isInfoEnabled()) {
                log.info(sm.getString("catalina.noCluster", e.getClass().getName() + ": " + e.getMessage()));
            }
        }
    }

    protected Digester createStopDigester() {
        Digester digester = new Digester();
        digester.setUseContextClassLoader(true);
        digester.addObjectCreate(HttpHeaders.SERVER, "org.apache.catalina.core.StandardServer", JsonEncoder.CLASS_NAME_ATTR_NAME);
        digester.addSetProperties(HttpHeaders.SERVER);
        digester.addSetNext(HttpHeaders.SERVER, "setServer", "org.apache.catalina.Server");
        return digester;
    }

    protected void parseServerXml(boolean start) {
        ConfigFileLoader.setSource(new CatalinaBaseConfigurationSource(Bootstrap.getCatalinaBaseFile(), getConfigFile()));
        File file = configFile();
        if (this.useGeneratedCode && !Digester.isGeneratedCodeLoaderSet()) {
            String loaderClassName = this.generatedCodePackage + ".DigesterGeneratedCodeLoader";
            try {
                Digester.GeneratedCodeLoader loader = (Digester.GeneratedCodeLoader) Catalina.class.getClassLoader().loadClass(loaderClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                Digester.setGeneratedCodeLoader(loader);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.info(sm.getString("catalina.noLoader", loaderClassName), e);
                } else {
                    log.info(sm.getString("catalina.noLoader", loaderClassName));
                }
                this.useGeneratedCode = false;
            }
        }
        File serverXmlLocation = null;
        String xmlClassName = null;
        if (this.generateCode || this.useGeneratedCode) {
            xmlClassName = start ? this.generatedCodePackage + ".ServerXml" : this.generatedCodePackage + ".ServerXmlStop";
        }
        if (this.generateCode) {
            if (this.generatedCodeLocationParameter != null) {
                this.generatedCodeLocation = new File(this.generatedCodeLocationParameter);
                if (!this.generatedCodeLocation.isAbsolute()) {
                    this.generatedCodeLocation = new File(Bootstrap.getCatalinaHomeFile(), this.generatedCodeLocationParameter);
                }
            } else {
                this.generatedCodeLocation = new File(Bootstrap.getCatalinaHomeFile(), "work");
            }
            serverXmlLocation = new File(this.generatedCodeLocation, this.generatedCodePackage);
            if (!serverXmlLocation.isDirectory() && !serverXmlLocation.mkdirs()) {
                log.warn(sm.getString("catalina.generatedCodeLocationError", this.generatedCodeLocation.getAbsolutePath()));
                this.generateCode = false;
            }
        }
        ServerXml serverXml = null;
        if (this.useGeneratedCode) {
            serverXml = (ServerXml) Digester.loadGeneratedClass(xmlClassName);
        }
        if (serverXml != null) {
            serverXml.load(this);
            return;
        }
        try {
            ConfigurationSource.Resource resource = ConfigFileLoader.getSource().getServerXml();
            try {
                Digester digester = start ? createStartDigester() : createStopDigester();
                InputStream inputStream = resource.getInputStream();
                InputSource inputSource = new InputSource(resource.getURI().toURL().toString());
                inputSource.setByteStream(inputStream);
                digester.push(this);
                if (this.generateCode) {
                    digester.startGeneratingCode();
                    generateClassHeader(digester, start);
                }
                digester.parse(inputSource);
                if (this.generateCode) {
                    generateClassFooter(digester);
                    FileWriter writer = new FileWriter(new File(serverXmlLocation, start ? "ServerXml.java" : "ServerXmlStop.java"));
                    try {
                        writer.write(digester.getGeneratedCode().toString());
                        writer.close();
                        digester.endGeneratingCode();
                        Digester.addGeneratedClass(xmlClassName);
                    } catch (Throwable th) {
                        try {
                            writer.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
                if (resource != null) {
                    resource.close();
                }
            } finally {
            }
        } catch (Exception e2) {
            log.warn(sm.getString("catalina.configFail", file.getAbsolutePath()), e2);
            if (file.exists() && !file.canRead()) {
                log.warn(sm.getString("catalina.incorrectPermissions"));
            }
        }
    }

    public void stopServer() {
        stopServer(null);
    }

    public void stopServer(String[] arguments) {
        if (arguments != null) {
            arguments(arguments);
        }
        Server s = getServer();
        if (s == null) {
            parseServerXml(false);
            if (getServer() == null) {
                log.error(sm.getString("catalina.stopError"));
                System.exit(1);
            }
            Server s2 = getServer();
            if (s2.getPortWithOffset() > 0) {
                try {
                    Socket socket = new Socket(s2.getAddress(), s2.getPortWithOffset());
                    try {
                        OutputStream stream = socket.getOutputStream();
                        try {
                            String shutdown = s2.getShutdown();
                            for (int i = 0; i < shutdown.length(); i++) {
                                stream.write(shutdown.charAt(i));
                            }
                            stream.flush();
                            if (stream != null) {
                                stream.close();
                            }
                            socket.close();
                            return;
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        try {
                            socket.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                        throw th3;
                    }
                } catch (ConnectException ce) {
                    log.error(sm.getString("catalina.stopServer.connectException", s2.getAddress(), String.valueOf(s2.getPortWithOffset()), String.valueOf(s2.getPort()), String.valueOf(s2.getPortOffset())));
                    log.error(sm.getString("catalina.stopError"), ce);
                    System.exit(1);
                    return;
                } catch (IOException e) {
                    log.error(sm.getString("catalina.stopError"), e);
                    System.exit(1);
                    return;
                }
            }
            log.error(sm.getString("catalina.stopServer"));
            System.exit(1);
            return;
        }
        try {
            s.stop();
            s.destroy();
        } catch (LifecycleException e2) {
            log.error(sm.getString("catalina.stopError"), e2);
        }
    }

    public void load() {
        if (this.loaded) {
            return;
        }
        this.loaded = true;
        long t1 = System.nanoTime();
        initNaming();
        parseServerXml(true);
        Server s = getServer();
        if (s == null) {
            return;
        }
        getServer().setCatalina(this);
        getServer().setCatalinaHome(Bootstrap.getCatalinaHomeFile());
        getServer().setCatalinaBase(Bootstrap.getCatalinaBaseFile());
        initStreams();
        try {
            getServer().init();
        } catch (LifecycleException e) {
            if (this.throwOnInitFailure) {
                throw new Error(e);
            }
            log.error(sm.getString("catalina.initError"), e);
        }
        if (log.isInfoEnabled()) {
            log.info(sm.getString("catalina.init", Long.toString(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1))));
        }
    }

    public void load(String[] args) {
        try {
            if (arguments(args)) {
                load();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void start() {
        if (getServer() == null) {
            load();
        }
        if (getServer() == null) {
            log.fatal(sm.getString("catalina.noServer"));
            return;
        }
        long t1 = System.nanoTime();
        try {
            getServer().start();
            if (log.isInfoEnabled()) {
                log.info(sm.getString("catalina.startup", Long.toString(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t1))));
            }
            if (this.generateCode) {
                generateLoader();
            }
            if (this.useShutdownHook) {
                if (this.shutdownHook == null) {
                    this.shutdownHook = new CatalinaShutdownHook();
                }
                Runtime.getRuntime().addShutdownHook(this.shutdownHook);
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager) logManager).setUseShutdownHook(false);
                }
            }
            if (this.await) {
                await();
                stop();
            }
        } catch (LifecycleException e) {
            log.fatal(sm.getString("catalina.serverStartFail"), e);
            try {
                getServer().destroy();
            } catch (LifecycleException e1) {
                log.debug("destroy() failed for failed Server ", e1);
            }
        }
    }

    public void stop() {
        try {
            if (this.useShutdownHook) {
                Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                LogManager logManager = LogManager.getLogManager();
                if (logManager instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager) logManager).setUseShutdownHook(true);
                }
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        try {
            Server s = getServer();
            LifecycleState state = s.getState();
            if (LifecycleState.STOPPING_PREP.compareTo(state) > 0 || LifecycleState.DESTROYED.compareTo(state) < 0) {
                s.stop();
                s.destroy();
            }
        } catch (LifecycleException e) {
            log.error(sm.getString("catalina.stopError"), e);
        }
    }

    public void await() {
        getServer().await();
    }

    protected void usage() {
        System.out.println(sm.getString("catalina.usage"));
    }

    protected void initStreams() {
        System.setOut(new SystemLogHandler(System.out));
        System.setErr(new SystemLogHandler(System.err));
    }

    protected void initNaming() {
        if (!this.useNaming) {
            log.info(sm.getString("catalina.noNaming"));
            System.setProperty("catalina.useNaming", "false");
            return;
        }
        System.setProperty("catalina.useNaming", "true");
        String value = "org.apache.naming";
        String oldValue = System.getProperty("java.naming.factory.url.pkgs");
        if (oldValue != null) {
            value = value + ":" + oldValue;
        }
        System.setProperty("java.naming.factory.url.pkgs", value);
        if (log.isDebugEnabled()) {
            log.debug("Setting naming prefix=" + value);
        }
        String value2 = System.getProperty(CoreTestConstants.JAVA_NAMING_FACTORY_INITIAL);
        if (value2 == null) {
            System.setProperty(CoreTestConstants.JAVA_NAMING_FACTORY_INITIAL, "org.apache.naming.java.javaURLContextFactory");
        } else {
            log.debug("INITIAL_CONTEXT_FACTORY already set " + value2);
        }
    }

    protected void setSecurityProtection() {
        SecurityConfig securityConfig = SecurityConfig.newInstance();
        securityConfig.setPackageDefinition();
        securityConfig.setPackageAccess();
    }

    protected void generateLoader() {
        StringBuilder code = new StringBuilder();
        code.append("package ").append(this.generatedCodePackage).append(';').append(System.lineSeparator());
        code.append("public class ").append("DigesterGeneratedCodeLoader");
        code.append(" implements org.apache.tomcat.util.digester.Digester.GeneratedCodeLoader {").append(System.lineSeparator());
        code.append("public Object loadGeneratedCode(String className) {").append(System.lineSeparator());
        code.append("switch (className) {").append(System.lineSeparator());
        for (String generatedClassName : Digester.getGeneratedClasses()) {
            code.append("case \"").append(generatedClassName).append("\" : return new ").append(generatedClassName);
            code.append("();").append(System.lineSeparator());
        }
        code.append("default: return null; }").append(System.lineSeparator());
        code.append("}}").append(System.lineSeparator());
        File loaderLocation = new File(this.generatedCodeLocation, this.generatedCodePackage);
        try {
            FileWriter writer = new FileWriter(new File(loaderLocation, "DigesterGeneratedCodeLoader" + ".java"));
            try {
                writer.write(code.toString());
                writer.close();
            } finally {
            }
        } catch (IOException e) {
            log.debug("Error writing code loader", e);
        }
    }

    protected void generateClassHeader(Digester digester, boolean start) {
        StringBuilder code = digester.getGeneratedCode();
        code.append("package ").append(this.generatedCodePackage).append(';').append(System.lineSeparator());
        code.append("public class ServerXml");
        if (!start) {
            code.append("Stop");
        }
        code.append(" implements ");
        code.append(ServerXml.class.getName().replace('$', '.')).append(" {").append(System.lineSeparator());
        code.append("public void load(").append(Catalina.class.getName());
        code.append(' ').append(digester.toVariableName(this)).append(") {").append(System.lineSeparator());
    }

    protected void generateClassFooter(Digester digester) {
        StringBuilder code = digester.getGeneratedCode();
        code.append('}').append(System.lineSeparator());
        code.append('}').append(System.lineSeparator());
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/Catalina$CatalinaShutdownHook.class */
    protected class CatalinaShutdownHook extends Thread {
        protected CatalinaShutdownHook() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                try {
                    if (Catalina.this.getServer() != null) {
                        Catalina.this.stop();
                    }
                    LogManager logManager = LogManager.getLogManager();
                    if (logManager instanceof ClassLoaderLogManager) {
                        ((ClassLoaderLogManager) logManager).shutdown();
                    }
                } catch (Throwable ex) {
                    ExceptionUtils.handleThrowable(ex);
                    Catalina.log.error(Catalina.sm.getString("catalina.shutdownHookFail"), ex);
                    LogManager logManager2 = LogManager.getLogManager();
                    if (logManager2 instanceof ClassLoaderLogManager) {
                        ((ClassLoaderLogManager) logManager2).shutdown();
                    }
                }
            } catch (Throwable th) {
                LogManager logManager3 = LogManager.getLogManager();
                if (logManager3 instanceof ClassLoaderLogManager) {
                    ((ClassLoaderLogManager) logManager3).shutdown();
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/Catalina$SetParentClassLoaderRule.class */
    public final class SetParentClassLoaderRule extends Rule {
        ClassLoader parentClassLoader;

        SetParentClassLoaderRule(ClassLoader parentClassLoader) {
            this.parentClassLoader = null;
            this.parentClassLoader = parentClassLoader;
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            if (this.digester.getLogger().isDebugEnabled()) {
                this.digester.getLogger().debug("Setting parent class loader");
            }
            Container top = (Container) this.digester.peek();
            top.setParentClassLoader(this.parentClassLoader);
            StringBuilder code = this.digester.getGeneratedCode();
            if (code != null) {
                code.append(this.digester.toVariableName(top)).append(".setParentClassLoader(");
                code.append(this.digester.toVariableName(Catalina.this)).append(".getParentClassLoader());");
                code.append(System.lineSeparator());
            }
        }
    }
}

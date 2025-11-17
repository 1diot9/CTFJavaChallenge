package org.apache.juli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/ClassLoaderLogManager.class */
public class ClassLoaderLogManager extends LogManager {
    private static ThreadLocal<Boolean> addingLocalRootLogger = ThreadLocal.withInitial(() -> {
        return Boolean.FALSE;
    });
    public static final String DEBUG_PROPERTY = ClassLoaderLogManager.class.getName() + ".debug";
    protected final Map<ClassLoader, ClassLoaderLogInfo> classLoaderLoggers = new WeakHashMap();
    protected final ThreadLocal<String> prefix = new ThreadLocal<>();
    protected volatile boolean useShutdownHook = true;

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/ClassLoaderLogManager$Cleaner.class */
    private final class Cleaner extends Thread {
        private Cleaner() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            if (ClassLoaderLogManager.this.useShutdownHook) {
                ClassLoaderLogManager.this.shutdown();
            }
        }
    }

    public ClassLoaderLogManager() {
        try {
            Runtime.getRuntime().addShutdownHook(new Cleaner());
        } catch (IllegalStateException e) {
        }
    }

    public boolean isUseShutdownHook() {
        return this.useShutdownHook;
    }

    public void setUseShutdownHook(boolean useShutdownHook) {
        this.useShutdownHook = useShutdownHook;
    }

    @Override // java.util.logging.LogManager
    public synchronized boolean addLogger(Logger logger) {
        String loggerName = logger.getName();
        ClassLoader classLoader = getClassLoader();
        ClassLoaderLogInfo info = getClassLoaderInfo(classLoader);
        if (info.loggers.containsKey(loggerName)) {
            return false;
        }
        info.loggers.put(loggerName, logger);
        String levelString = getProperty(loggerName + ".level");
        if (levelString != null) {
            try {
                AccessController.doPrivileged(() -> {
                    logger.setLevel(Level.parse(levelString.trim()));
                    return null;
                });
            } catch (IllegalArgumentException e) {
            }
        }
        int dotIndex = loggerName.lastIndexOf(46);
        if (dotIndex >= 0) {
            String parentName = loggerName.substring(0, dotIndex);
            Logger.getLogger(parentName);
        }
        LogNode node = info.rootNode.findNode(loggerName);
        node.logger = logger;
        Logger parentLogger = node.findParentLogger();
        if (parentLogger != null) {
            doSetParentLogger(logger, parentLogger);
        }
        node.setParentLogger(logger);
        String handlers = getProperty(loggerName + ".handlers");
        if (handlers != null) {
            logger.setUseParentHandlers(false);
            StringTokenizer tok = new StringTokenizer(handlers, ",");
            while (tok.hasMoreTokens()) {
                String handlerName = tok.nextToken().trim();
                Handler handler = null;
                ClassLoader classLoader2 = classLoader;
                while (true) {
                    ClassLoader current = classLoader2;
                    if (current == null) {
                        break;
                    }
                    ClassLoaderLogInfo info2 = this.classLoaderLoggers.get(current);
                    if (info2 != null) {
                        handler = info2.handlers.get(handlerName);
                        if (handler != null) {
                            break;
                        }
                    }
                    classLoader2 = current.getParent();
                }
                if (handler != null) {
                    logger.addHandler(handler);
                }
            }
        }
        String useParentHandlersString = getProperty(loggerName + ".useParentHandlers");
        if (Boolean.parseBoolean(useParentHandlersString)) {
            logger.setUseParentHandlers(true);
            return true;
        }
        return true;
    }

    @Override // java.util.logging.LogManager
    public synchronized Logger getLogger(String name) {
        ClassLoader classLoader = getClassLoader();
        return getClassLoaderInfo(classLoader).loggers.get(name);
    }

    @Override // java.util.logging.LogManager
    public synchronized Enumeration<String> getLoggerNames() {
        ClassLoader classLoader = getClassLoader();
        return Collections.enumeration(getClassLoaderInfo(classLoader).loggers.keySet());
    }

    @Override // java.util.logging.LogManager
    public String getProperty(String name) {
        if (".handlers".equals(name) && !addingLocalRootLogger.get().booleanValue()) {
            return null;
        }
        String prefix = this.prefix.get();
        String result = null;
        if (prefix != null) {
            result = findProperty(prefix + name);
        }
        if (result == null) {
            result = findProperty(name);
        }
        if (result != null) {
            result = replace(result);
        }
        return result;
    }

    private synchronized String findProperty(String name) {
        ClassLoader classLoader = getClassLoader();
        ClassLoaderLogInfo info = getClassLoaderInfo(classLoader);
        String result = info.props.getProperty(name);
        if (result == null && info.props.isEmpty()) {
            if (classLoader != null) {
                ClassLoader parent = classLoader.getParent();
                while (true) {
                    ClassLoader current = parent;
                    if (current == null) {
                        break;
                    }
                    ClassLoaderLogInfo info2 = this.classLoaderLoggers.get(current);
                    if (info2 != null) {
                        result = info2.props.getProperty(name);
                        if (result != null || !info2.props.isEmpty()) {
                            break;
                        }
                    }
                    parent = current.getParent();
                }
            }
            if (result == null) {
                result = super.getProperty(name);
            }
        }
        return result;
    }

    @Override // java.util.logging.LogManager
    public void readConfiguration() throws IOException, SecurityException {
        checkAccess();
        readConfiguration(getClassLoader());
    }

    @Override // java.util.logging.LogManager
    public void readConfiguration(InputStream is) throws IOException, SecurityException {
        checkAccess();
        reset();
        readConfiguration(is, getClassLoader());
    }

    @Override // java.util.logging.LogManager
    public synchronized void reset() throws SecurityException {
        Thread thread = Thread.currentThread();
        if (thread.getClass().getName().startsWith("java.util.logging.LogManager$")) {
            return;
        }
        ClassLoader classLoader = getClassLoader();
        ClassLoaderLogInfo clLogInfo = getClassLoaderInfo(classLoader);
        resetLoggers(clLogInfo);
    }

    public synchronized void shutdown() {
        for (ClassLoaderLogInfo clLogInfo : this.classLoaderLoggers.values()) {
            resetLoggers(clLogInfo);
        }
    }

    private void resetLoggers(ClassLoaderLogInfo clLogInfo) {
        for (Logger logger : clLogInfo.loggers.values()) {
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                logger.removeHandler(handler);
            }
        }
        for (Handler handler2 : clLogInfo.handlers.values()) {
            try {
                handler2.close();
            } catch (Exception e) {
            }
        }
        clLogInfo.handlers.clear();
    }

    protected synchronized ClassLoaderLogInfo getClassLoaderInfo(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        ClassLoaderLogInfo info = this.classLoaderLoggers.get(classLoader);
        if (info == null) {
            ClassLoader classLoaderParam = classLoader;
            AccessController.doPrivileged(() -> {
                try {
                    readConfiguration(classLoaderParam);
                    return null;
                } catch (IOException e) {
                    return null;
                }
            });
            info = this.classLoaderLoggers.get(classLoader);
        }
        return info;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected synchronized void readConfiguration(ClassLoader classLoader) throws IOException {
        Logger log;
        InputStream is = null;
        try {
            if (classLoader instanceof WebappProperties) {
                if (((WebappProperties) classLoader).hasLoggingConfig()) {
                    is = classLoader.getResourceAsStream("logging.properties");
                }
            } else if (classLoader instanceof URLClassLoader) {
                URL logConfig = ((URLClassLoader) classLoader).findResource("logging.properties");
                if (null != logConfig) {
                    if (Boolean.getBoolean(DEBUG_PROPERTY)) {
                        System.err.println(getClass().getName() + ".readConfiguration(): Found logging.properties at " + logConfig);
                    }
                    is = classLoader.getResourceAsStream("logging.properties");
                } else if (Boolean.getBoolean(DEBUG_PROPERTY)) {
                    System.err.println(getClass().getName() + ".readConfiguration(): Found no logging.properties");
                }
            }
        } catch (AccessControlException ace) {
            ClassLoaderLogInfo info = this.classLoaderLoggers.get(ClassLoader.getSystemClassLoader());
            if (info != null && (log = info.loggers.get("")) != null) {
                Permission perm = ace.getPermission();
                if ((perm instanceof FilePermission) && perm.getActions().equals("read")) {
                    log.warning("Reading " + perm.getName() + " is not permitted. See \"per context logging\" in the default catalina.policy file.");
                } else {
                    log.warning("Reading logging.properties is not permitted in some context. See \"per context logging\" in the default catalina.policy file.");
                    log.warning("Original error was: " + ace.getMessage());
                }
            }
        }
        if (is == null && classLoader == ClassLoader.getSystemClassLoader()) {
            String configFileStr = System.getProperty("java.util.logging.config.file");
            if (configFileStr != null) {
                try {
                    is = new FileInputStream(replace(configFileStr));
                } catch (IOException e) {
                    System.err.println("Configuration error");
                    e.printStackTrace();
                }
            }
            if (is == null) {
                File defaultFile = new File(new File(System.getProperty("java.home"), "conf"), "logging.properties");
                try {
                    is = new FileInputStream(defaultFile);
                } catch (IOException e2) {
                    System.err.println("Configuration error");
                    e2.printStackTrace();
                }
            }
        }
        Logger localRootLogger = new RootLogger();
        if (is == null) {
            ClassLoaderLogInfo info2 = null;
            for (ClassLoader current = classLoader.getParent(); current != null && info2 == null; current = current.getParent()) {
                info2 = getClassLoaderInfo(current);
            }
            if (info2 != null) {
                localRootLogger.setParent(info2.rootNode.logger);
            }
        }
        this.classLoaderLoggers.put(classLoader, new ClassLoaderLogInfo(new LogNode(null, localRootLogger)));
        if (is != null) {
            readConfiguration(is, classLoader);
        }
        if (localRootLogger.getParent() == null && localRootLogger.getLevel() == null) {
            localRootLogger.setLevel(Level.INFO);
        }
        try {
            addingLocalRootLogger.set(Boolean.TRUE);
            addLogger(localRootLogger);
            addingLocalRootLogger.set(Boolean.FALSE);
        } catch (Throwable th) {
            addingLocalRootLogger.set(Boolean.FALSE);
            throw th;
        }
    }

    protected synchronized void readConfiguration(InputStream is, ClassLoader classLoader) throws IOException {
        int pos;
        ClassLoaderLogInfo info = this.classLoaderLoggers.get(classLoader);
        try {
            try {
                info.props.load(is);
                try {
                    is.close();
                } catch (IOException e) {
                }
            } catch (Throwable th) {
                try {
                    is.close();
                } catch (IOException e2) {
                }
                throw th;
            }
        } catch (IOException e3) {
            System.err.println("Configuration error");
            e3.printStackTrace();
            try {
                is.close();
            } catch (IOException e4) {
            }
        }
        String rootHandlers = info.props.getProperty(".handlers");
        String handlers = info.props.getProperty("handlers");
        Logger localRootLogger = info.rootNode.logger;
        if (handlers != null) {
            StringTokenizer tok = new StringTokenizer(handlers, ",");
            while (tok.hasMoreTokens()) {
                String handlerName = tok.nextToken().trim();
                String handlerClassName = handlerName;
                String prefix = "";
                if (handlerClassName.length() > 0) {
                    if (Character.isDigit(handlerClassName.charAt(0)) && (pos = handlerClassName.indexOf(46)) >= 0) {
                        prefix = handlerClassName.substring(0, pos + 1);
                        handlerClassName = handlerClassName.substring(pos + 1);
                    }
                    try {
                        this.prefix.set(prefix);
                        Handler handler = (Handler) classLoader.loadClass(handlerClassName).getConstructor(new Class[0]).newInstance(new Object[0]);
                        this.prefix.set(null);
                        info.handlers.put(handlerName, handler);
                        if (rootHandlers == null) {
                            localRootLogger.addHandler(handler);
                        }
                    } catch (Exception e5) {
                        System.err.println("Handler error");
                        e5.printStackTrace();
                    }
                }
            }
        }
    }

    protected static void doSetParentLogger(Logger logger, Logger parent) {
        AccessController.doPrivileged(() -> {
            logger.setParent(parent);
            return null;
        });
    }

    protected String replace(String str) {
        String result = str;
        int pos_start = str.indexOf("${");
        if (pos_start >= 0) {
            StringBuilder builder = new StringBuilder();
            int pos_end = -1;
            while (true) {
                if (pos_start < 0) {
                    break;
                }
                builder.append((CharSequence) str, pos_end + 1, pos_start);
                pos_end = str.indexOf(125, pos_start + 2);
                if (pos_end < 0) {
                    pos_end = pos_start - 1;
                    break;
                }
                String propName = str.substring(pos_start + 2, pos_end);
                String replacement = replaceWebApplicationProperties(propName);
                if (replacement == null) {
                    replacement = propName.length() > 0 ? System.getProperty(propName) : null;
                }
                if (replacement != null) {
                    builder.append(replacement);
                } else {
                    builder.append((CharSequence) str, pos_start, pos_end + 1);
                }
                pos_start = str.indexOf("${", pos_end + 1);
            }
            builder.append((CharSequence) str, pos_end + 1, str.length());
            result = builder.toString();
        }
        return result;
    }

    private String replaceWebApplicationProperties(String propName) {
        Object classLoader = getClassLoader();
        if (classLoader instanceof WebappProperties) {
            WebappProperties wProps = (WebappProperties) classLoader;
            if ("classloader.webappName".equals(propName)) {
                return wProps.getWebappName();
            }
            if ("classloader.hostName".equals(propName)) {
                return wProps.getHostName();
            }
            if ("classloader.serviceName".equals(propName)) {
                return wProps.getServiceName();
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ClassLoader getClassLoader() {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            result = ClassLoaderLogManager.class.getClassLoader();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/ClassLoaderLogManager$LogNode.class */
    public static final class LogNode {
        Logger logger;
        final Map<String, LogNode> children;
        final LogNode parent;

        LogNode(LogNode parent, Logger logger) {
            this.children = new HashMap();
            this.parent = parent;
            this.logger = logger;
        }

        LogNode(LogNode parent) {
            this(parent, null);
        }

        LogNode findNode(String name) {
            String nextName;
            LogNode currentNode = this;
            if (this.logger.getName().equals(name)) {
                return this;
            }
            while (name != null) {
                int dotIndex = name.indexOf(46);
                if (dotIndex < 0) {
                    nextName = name;
                    name = null;
                } else {
                    nextName = name.substring(0, dotIndex);
                    name = name.substring(dotIndex + 1);
                }
                LogNode childNode = currentNode.children.get(nextName);
                if (childNode == null) {
                    childNode = new LogNode(currentNode);
                    currentNode.children.put(nextName, childNode);
                }
                currentNode = childNode;
            }
            return currentNode;
        }

        Logger findParentLogger() {
            Logger logger = null;
            LogNode logNode = this.parent;
            while (true) {
                LogNode node = logNode;
                if (node == null || logger != null) {
                    break;
                }
                logger = node.logger;
                logNode = node.parent;
            }
            return logger;
        }

        void setParentLogger(Logger parent) {
            for (LogNode childNode : this.children.values()) {
                if (childNode.logger == null) {
                    childNode.setParentLogger(parent);
                } else {
                    ClassLoaderLogManager.doSetParentLogger(childNode.logger, parent);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/ClassLoaderLogManager$ClassLoaderLogInfo.class */
    public static final class ClassLoaderLogInfo {
        final LogNode rootNode;
        final Map<String, Logger> loggers = new ConcurrentHashMap();
        final Map<String, Handler> handlers = new HashMap();
        final Properties props = new Properties();

        ClassLoaderLogInfo(LogNode rootNode) {
            this.rootNode = rootNode;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/ClassLoaderLogManager$RootLogger.class */
    public static class RootLogger extends Logger {
        public RootLogger() {
            super("", null);
        }
    }
}

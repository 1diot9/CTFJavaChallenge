package org.apache.catalina.startup;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.startup.ClassLoaderFactory;
import org.apache.catalina.valves.Constants;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/Bootstrap.class */
public final class Bootstrap {
    private static final File catalinaBaseFile;
    private static final File catalinaHomeFile;
    private Object catalinaDaemon = null;
    ClassLoader commonLoader = null;
    ClassLoader catalinaLoader = null;
    ClassLoader sharedLoader = null;
    private static final Log log = LogFactory.getLog((Class<?>) Bootstrap.class);
    private static final Object daemonLock = new Object();
    private static volatile Bootstrap daemon = null;
    private static final Pattern PATH_PATTERN = Pattern.compile("(\"[^\"]*\")|(([^,])*)");

    static {
        String userDir = System.getProperty("user.dir");
        String home = System.getProperty("catalina.home");
        File homeFile = null;
        if (home != null) {
            File f = new File(home);
            try {
                homeFile = f.getCanonicalFile();
            } catch (IOException e) {
                homeFile = f.getAbsoluteFile();
            }
        }
        if (homeFile == null) {
            File bootstrapJar = new File(userDir, "bootstrap.jar");
            if (bootstrapJar.exists()) {
                File f2 = new File(userDir, CallerDataConverter.DEFAULT_RANGE_DELIMITER);
                try {
                    homeFile = f2.getCanonicalFile();
                } catch (IOException e2) {
                    homeFile = f2.getAbsoluteFile();
                }
            }
        }
        if (homeFile == null) {
            File f3 = new File(userDir);
            try {
                homeFile = f3.getCanonicalFile();
            } catch (IOException e3) {
                homeFile = f3.getAbsoluteFile();
            }
        }
        catalinaHomeFile = homeFile;
        System.setProperty("catalina.home", catalinaHomeFile.getPath());
        String base = System.getProperty("catalina.base");
        if (base == null) {
            catalinaBaseFile = catalinaHomeFile;
        } else {
            File baseFile = new File(base);
            try {
                baseFile = baseFile.getCanonicalFile();
            } catch (IOException e4) {
                baseFile = baseFile.getAbsoluteFile();
            }
            catalinaBaseFile = baseFile;
        }
        System.setProperty("catalina.base", catalinaBaseFile.getPath());
    }

    private void initClassLoaders() {
        try {
            this.commonLoader = createClassLoader(Constants.AccessLog.COMMON_ALIAS, null);
            if (this.commonLoader == null) {
                this.commonLoader = getClass().getClassLoader();
            }
            this.catalinaLoader = createClassLoader("server", this.commonLoader);
            this.sharedLoader = createClassLoader("shared", this.commonLoader);
        } catch (Throwable t) {
            handleThrowable(t);
            log.error("Class loader creation threw exception", t);
            System.exit(1);
        }
    }

    private ClassLoader createClassLoader(String name, ClassLoader parent) throws Exception {
        String value = CatalinaProperties.getProperty(name + ".loader");
        if (value == null || value.equals("")) {
            return parent;
        }
        String value2 = replace(value);
        List<ClassLoaderFactory.Repository> repositories = new ArrayList<>();
        String[] repositoryPaths = getPaths(value2);
        for (String repository : repositoryPaths) {
            try {
                URI uri = new URI(repository);
                uri.toURL();
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.URL));
            } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
                if (repository.endsWith("*.jar")) {
                    repositories.add(new ClassLoaderFactory.Repository(repository.substring(0, repository.length() - "*.jar".length()), ClassLoaderFactory.RepositoryType.GLOB));
                } else if (repository.endsWith(".jar")) {
                    repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.JAR));
                } else {
                    repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.DIR));
                }
            }
        }
        return ClassLoaderFactory.createClassLoader(repositories, parent);
    }

    protected String replace(String str) {
        String replacement;
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
                if (propName.length() == 0) {
                    replacement = null;
                } else if ("catalina.home".equals(propName)) {
                    replacement = getCatalinaHome();
                } else if ("catalina.base".equals(propName)) {
                    replacement = getCatalinaBase();
                } else {
                    replacement = System.getProperty(propName);
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

    public void init() throws Exception {
        initClassLoaders();
        Thread.currentThread().setContextClassLoader(this.catalinaLoader);
        SecurityClassLoad.securityClassLoad(this.catalinaLoader);
        if (log.isDebugEnabled()) {
            log.debug("Loading startup class");
        }
        Class<?> startupClass = this.catalinaLoader.loadClass("org.apache.catalina.startup.Catalina");
        Object startupInstance = startupClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        if (log.isDebugEnabled()) {
            log.debug("Setting startup class properties");
        }
        Class<?>[] paramTypes = {Class.forName("java.lang.ClassLoader")};
        Object[] paramValues = {this.sharedLoader};
        Method method = startupInstance.getClass().getMethod("setParentClassLoader", paramTypes);
        method.invoke(startupInstance, paramValues);
        this.catalinaDaemon = startupInstance;
    }

    private void load(String[] arguments) throws Exception {
        Class<?>[] paramTypes;
        Object[] param;
        if (arguments == null || arguments.length == 0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[]{arguments.getClass()};
            param = new Object[]{arguments};
        }
        Method method = this.catalinaDaemon.getClass().getMethod("load", paramTypes);
        if (log.isDebugEnabled()) {
            log.debug("Calling startup class " + method);
        }
        method.invoke(this.catalinaDaemon, param);
    }

    private Object getServer() throws Exception {
        Method method = this.catalinaDaemon.getClass().getMethod("getServer", new Class[0]);
        return method.invoke(this.catalinaDaemon, new Object[0]);
    }

    public void init(String[] arguments) throws Exception {
        init();
        load(arguments);
    }

    public void start() throws Exception {
        if (this.catalinaDaemon == null) {
            init();
        }
        Method method = this.catalinaDaemon.getClass().getMethod(Lifecycle.START_EVENT, (Class[]) null);
        method.invoke(this.catalinaDaemon, (Object[]) null);
    }

    public void stop() throws Exception {
        Method method = this.catalinaDaemon.getClass().getMethod(Lifecycle.STOP_EVENT, (Class[]) null);
        method.invoke(this.catalinaDaemon, (Object[]) null);
    }

    public void stopServer() throws Exception {
        Method method = this.catalinaDaemon.getClass().getMethod("stopServer", (Class[]) null);
        method.invoke(this.catalinaDaemon, (Object[]) null);
    }

    public void stopServer(String[] arguments) throws Exception {
        Class<?>[] paramTypes;
        Object[] param;
        if (arguments == null || arguments.length == 0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[]{arguments.getClass()};
            param = new Object[]{arguments};
        }
        Method method = this.catalinaDaemon.getClass().getMethod("stopServer", paramTypes);
        method.invoke(this.catalinaDaemon, param);
    }

    public void setAwait(boolean await) throws Exception {
        Class<?>[] paramTypes = {Boolean.TYPE};
        Object[] paramValues = {Boolean.valueOf(await)};
        Method method = this.catalinaDaemon.getClass().getMethod("setAwait", paramTypes);
        method.invoke(this.catalinaDaemon, paramValues);
    }

    public boolean getAwait() throws Exception {
        Class<?>[] paramTypes = new Class[0];
        Object[] paramValues = new Object[0];
        Method method = this.catalinaDaemon.getClass().getMethod("getAwait", paramTypes);
        Boolean b = (Boolean) method.invoke(this.catalinaDaemon, paramValues);
        return b.booleanValue();
    }

    public void destroy() {
    }

    public static void main(String[] args) {
        synchronized (daemonLock) {
            if (daemon == null) {
                Bootstrap bootstrap = new Bootstrap();
                try {
                    bootstrap.init();
                    daemon = bootstrap;
                } catch (Throwable t) {
                    handleThrowable(t);
                    log.error("Init exception", t);
                    return;
                }
            } else {
                Thread.currentThread().setContextClassLoader(daemon.catalinaLoader);
            }
        }
        try {
            String command = Lifecycle.START_EVENT;
            if (args.length > 0) {
                command = args[args.length - 1];
            }
            if (command.equals("startd")) {
                args[args.length - 1] = Lifecycle.START_EVENT;
                daemon.load(args);
                daemon.start();
            } else if (command.equals("stopd")) {
                args[args.length - 1] = Lifecycle.STOP_EVENT;
                daemon.stop();
            } else if (command.equals(Lifecycle.START_EVENT)) {
                daemon.setAwait(true);
                daemon.load(args);
                daemon.start();
                if (null == daemon.getServer()) {
                    System.exit(1);
                }
            } else if (command.equals(Lifecycle.STOP_EVENT)) {
                daemon.stopServer(args);
            } else if (command.equals("configtest")) {
                daemon.load(args);
                if (null == daemon.getServer()) {
                    System.exit(1);
                }
                System.exit(0);
            } else {
                log.warn("Bootstrap: command \"" + command + "\" does not exist.");
            }
        } catch (Throwable th) {
            t = th;
            if ((t instanceof InvocationTargetException) && t.getCause() != null) {
                t = t.getCause();
            }
            handleThrowable(t);
            log.error("Error running command", t);
            System.exit(1);
        }
    }

    public static String getCatalinaHome() {
        return catalinaHomeFile.getPath();
    }

    public static String getCatalinaBase() {
        return catalinaBaseFile.getPath();
    }

    public static File getCatalinaHomeFile() {
        return catalinaHomeFile;
    }

    public static File getCatalinaBaseFile() {
        return catalinaBaseFile;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (!(t instanceof StackOverflowError) && (t instanceof VirtualMachineError)) {
            throw ((VirtualMachineError) t);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Throwable unwrapInvocationTargetException(Throwable t) {
        if ((t instanceof InvocationTargetException) && t.getCause() != null) {
            return t.getCause();
        }
        return t;
    }

    protected static String[] getPaths(String value) {
        List<String> result = new ArrayList<>();
        Matcher matcher = PATH_PATTERN.matcher(value);
        while (matcher.find()) {
            String path = value.substring(matcher.start(), matcher.end()).trim();
            if (path.length() != 0) {
                char first = path.charAt(0);
                char last = path.charAt(path.length() - 1);
                if (first == '\"' && last == '\"' && path.length() > 1) {
                    path = path.substring(1, path.length() - 1).trim();
                    if (path.length() == 0) {
                    }
                } else if (path.contains("\"")) {
                    throw new IllegalArgumentException("The double quote [\"] character can only be used to quote paths. It must not appear in a path. This loader path is not valid: [" + value + "]");
                }
                result.add(path);
            }
        }
        return (String[]) result.toArray(new String[0]);
    }
}

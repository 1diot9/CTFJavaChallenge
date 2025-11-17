package ch.qos.logback.core.util;

import java.lang.module.ModuleDescriptor;
import java.util.Optional;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/EnvUtil.class */
public class EnvUtil {
    private EnvUtil() {
    }

    public static String logbackVersion() {
        String moduleVersion = logbackVersionByModule();
        if (moduleVersion != null) {
            return moduleVersion;
        }
        Package pkg = EnvUtil.class.getPackage();
        if (pkg == null) {
            return null;
        }
        return pkg.getImplementationVersion();
    }

    private static String logbackVersionByModule() {
        ModuleDescriptor md;
        Module module = EnvUtil.class.getModule();
        if (module == null || (md = module.getDescriptor()) == null) {
            return null;
        }
        Optional<String> opt = md.rawVersion();
        return opt.orElse(null);
    }

    public static int getJDKVersion(String javaVersionStr) {
        int i;
        int version = 0;
        for (char ch2 : javaVersionStr.toCharArray()) {
            if (Character.isDigit(ch2)) {
                i = (version * 10) + (ch2 - '0');
            } else {
                if (version != 1) {
                    break;
                }
                i = 0;
            }
            version = i;
        }
        return version;
    }

    private static boolean isJDK_N_OrHigher(int n) {
        int version;
        String javaVersionStr = System.getProperty("java.version", "");
        return !javaVersionStr.isEmpty() && (version = getJDKVersion(javaVersionStr)) > 0 && n <= version;
    }

    public static boolean isJDK5() {
        return isJDK_N_OrHigher(5);
    }

    public static boolean isJDK6OrHigher() {
        return isJDK_N_OrHigher(6);
    }

    public static boolean isJDK7OrHigher() {
        return isJDK_N_OrHigher(7);
    }

    public static boolean isJDK16OrHigher() {
        return isJDK_N_OrHigher(16);
    }

    public static boolean isJDK18OrHigher() {
        return isJDK_N_OrHigher(18);
    }

    public static boolean isJDK21OrHigher() {
        return isJDK_N_OrHigher(21);
    }

    public static boolean isJaninoAvailable() {
        ClassLoader classLoader = EnvUtil.class.getClassLoader();
        try {
            Class<?> bindingClass = classLoader.loadClass("org.codehaus.janino.ScriptEvaluator");
            return bindingClass != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.startsWith("Windows");
    }

    public static boolean isClassAvailable(Class callerClass, String className) {
        ClassLoader classLoader = Loader.getClassLoaderOfClass(callerClass);
        try {
            Class<?> bindingClass = classLoader.loadClass(className);
            return bindingClass != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

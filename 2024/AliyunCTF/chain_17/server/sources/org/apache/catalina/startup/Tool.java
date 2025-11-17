package org.apache.catalina.startup;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/startup/Tool.class */
public final class Tool {
    private static final Log log = LogFactory.getLog((Class<?>) Tool.class);
    private static boolean ant = false;
    private static final String catalinaHome = System.getProperty("catalina.home");
    private static boolean common = false;
    private static boolean server = false;
    private static boolean shared = false;

    public static void main(String[] args) {
        if (catalinaHome == null) {
            log.error("Must set 'catalina.home' system property");
            System.exit(1);
        }
        int index = 0;
        while (true) {
            if (index == args.length) {
                usage();
                System.exit(1);
            }
            if ("-ant".equals(args[index])) {
                ant = true;
            } else if ("-common".equals(args[index])) {
                common = true;
            } else if ("-server".equals(args[index])) {
                server = true;
            } else if (!"-shared".equals(args[index])) {
                break;
            } else {
                shared = true;
            }
            index++;
        }
        if (index > args.length) {
            usage();
            System.exit(1);
        }
        if (ant) {
            System.setProperty("ant.home", catalinaHome);
        }
        ClassLoader classLoader = null;
        try {
            List<File> packed = new ArrayList<>();
            List<File> unpacked = new ArrayList<>();
            unpacked.add(new File(catalinaHome, "classes"));
            packed.add(new File(catalinaHome, "lib"));
            if (common) {
                unpacked.add(new File(catalinaHome, "common" + File.separator + "classes"));
                packed.add(new File(catalinaHome, "common" + File.separator + "lib"));
            }
            if (server) {
                unpacked.add(new File(catalinaHome, "server" + File.separator + "classes"));
                packed.add(new File(catalinaHome, "server" + File.separator + "lib"));
            }
            if (shared) {
                unpacked.add(new File(catalinaHome, "shared" + File.separator + "classes"));
                packed.add(new File(catalinaHome, "shared" + File.separator + "lib"));
            }
            classLoader = ClassLoaderFactory.createClassLoader((File[]) unpacked.toArray(new File[0]), (File[]) packed.toArray(new File[0]), null);
        } catch (Throwable t) {
            Bootstrap.handleThrowable(t);
            log.error("Class loader creation threw exception", t);
            System.exit(1);
        }
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> clazz = null;
        int i = index;
        int index2 = index + 1;
        String className = args[i];
        try {
            if (log.isDebugEnabled()) {
                log.debug("Loading application class " + className);
            }
            clazz = classLoader.loadClass(className);
        } catch (Throwable t2) {
            Bootstrap.handleThrowable(t2);
            log.error("Exception creating instance of " + className, t2);
            System.exit(1);
        }
        Method method = null;
        String[] params = new String[args.length - index2];
        System.arraycopy(args, index2, params, 0, params.length);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Identifying main() method");
            }
            Class<?>[] paramTypes = {params.getClass()};
            method = clazz.getMethod("main", paramTypes);
        } catch (Throwable t3) {
            Bootstrap.handleThrowable(t3);
            log.error("Exception locating main() method", t3);
            System.exit(1);
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("Calling main() method");
            }
            Object[] paramValues = {params};
            method.invoke(null, paramValues);
        } catch (Throwable t4) {
            Throwable t5 = Bootstrap.unwrapInvocationTargetException(t4);
            Bootstrap.handleThrowable(t5);
            log.error("Exception calling main() method", t5);
            System.exit(1);
        }
    }

    private static void usage() {
        log.info("Usage:  java org.apache.catalina.startup.Tool [<options>] <class> [<arguments>]");
    }
}

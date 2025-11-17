package org.apache.tomcat.util.compat;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.validation.DataBinder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/compat/JreCompat.class */
public class JreCompat {
    private static final JreCompat instance;
    private static final boolean graalAvailable;
    private static final boolean jre16Available;
    private static final boolean jre19Available;
    private static final boolean jre21Available;
    private static final boolean jre22Available;
    private static final StringManager sm = StringManager.getManager((Class<?>) JreCompat.class);

    static {
        boolean result = false;
        try {
            Class<?> nativeImageClazz = Class.forName("org.graalvm.nativeimage.ImageInfo");
            result = Boolean.TRUE.equals(nativeImageClazz.getMethod("inImageCode", new Class[0]).invoke(null, new Object[0]));
        } catch (ClassNotFoundException e) {
        } catch (IllegalArgumentException | ReflectiveOperationException e2) {
        }
        graalAvailable = result || System.getProperty("org.graalvm.nativeimage.imagecode") != null;
        if (Jre22Compat.isSupported()) {
            instance = new Jre22Compat();
            jre22Available = true;
            jre21Available = true;
            jre19Available = true;
            jre16Available = true;
            return;
        }
        if (Jre21Compat.isSupported()) {
            instance = new Jre21Compat();
            jre22Available = false;
            jre21Available = true;
            jre19Available = true;
            jre16Available = true;
            return;
        }
        if (Jre19Compat.isSupported()) {
            instance = new Jre19Compat();
            jre22Available = false;
            jre21Available = false;
            jre19Available = true;
            jre16Available = true;
            return;
        }
        if (Jre16Compat.isSupported()) {
            instance = new Jre16Compat();
            jre22Available = false;
            jre21Available = false;
            jre19Available = false;
            jre16Available = true;
            return;
        }
        instance = new JreCompat();
        jre22Available = false;
        jre21Available = false;
        jre19Available = false;
        jre16Available = false;
    }

    public static JreCompat getInstance() {
        return instance;
    }

    public static boolean isGraalAvailable() {
        return graalAvailable;
    }

    public static boolean isJre16Available() {
        return jre16Available;
    }

    public static boolean isJre19Available() {
        return jre19Available;
    }

    public static boolean isJre21Available() {
        return jre21Available;
    }

    public static boolean isJre22Available() {
        return jre22Available;
    }

    public SocketAddress getUnixDomainSocketAddress(String path) {
        return null;
    }

    public ServerSocketChannel openUnixDomainServerSocketChannel() {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noUnixDomainSocket"));
    }

    public SocketChannel openUnixDomainSocketChannel() {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noUnixDomainSocket"));
    }

    public Object getExecutor(Thread thread) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = null;
        Object target = null;
        for (String fieldName : new String[]{DataBinder.DEFAULT_OBJECT_NAME, "runnable", "action"}) {
            try {
                Field targetField = thread.getClass().getDeclaredField(fieldName);
                targetField.setAccessible(true);
                target = targetField.get(thread);
                break;
            } catch (NoSuchFieldException e) {
            }
        }
        if (target != null && target.getClass().getCanonicalName() != null && (target.getClass().getCanonicalName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor.Worker") || target.getClass().getCanonicalName().equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
            Field executorField = target.getClass().getDeclaredField("this$0");
            executorField.setAccessible(true);
            result = executorField.get(target);
        }
        return result;
    }

    public Object createVirtualThreadBuilder(String name) {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noVirtualThreads"));
    }

    public void threadBuilderStart(Object threadBuilder, Runnable command) {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noVirtualThreads"));
    }
}

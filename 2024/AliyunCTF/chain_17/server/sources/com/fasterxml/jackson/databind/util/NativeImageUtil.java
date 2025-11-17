package com.fasterxml.jackson.databind.util;

import java.lang.reflect.InvocationTargetException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/NativeImageUtil.class */
public class NativeImageUtil {
    private static final boolean RUNNING_IN_SVM;

    static {
        RUNNING_IN_SVM = System.getProperty("org.graalvm.nativeimage.imagecode") != null;
    }

    private NativeImageUtil() {
    }

    private static boolean isRunningInNativeImage() {
        return RUNNING_IN_SVM && "runtime".equals(System.getProperty("org.graalvm.nativeimage.imagecode"));
    }

    public static boolean isUnsupportedFeatureError(Throwable e) {
        if (!isRunningInNativeImage()) {
            return false;
        }
        if (e instanceof InvocationTargetException) {
            e = e.getCause();
        }
        return e.getClass().getName().equals("com.oracle.svm.core.jdk.UnsupportedFeatureError");
    }

    public static boolean needsReflectionConfiguration(Class<?> cl) {
        if (isRunningInNativeImage()) {
            return (cl.getDeclaredFields().length == 0 || ClassUtil.isRecordType(cl)) && cl.getDeclaredMethods().length == 0 && cl.getDeclaredConstructors().length == 0;
        }
        return false;
    }
}

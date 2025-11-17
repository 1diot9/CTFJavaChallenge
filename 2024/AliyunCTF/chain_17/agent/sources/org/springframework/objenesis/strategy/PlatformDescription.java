package org.springframework.objenesis.strategy;

import java.lang.reflect.Field;
import org.springframework.objenesis.ObjenesisException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/objenesis/strategy/PlatformDescription.class */
public final class PlatformDescription {
    public static final String GNU = "GNU libgcj";
    public static final String HOTSPOT = "Java HotSpot";

    @Deprecated
    public static final String SUN = "Java HotSpot";
    public static final String OPENJDK = "OpenJDK";
    public static final String PERC = "PERC";
    public static final String DALVIK = "Dalvik";
    public static final String SPECIFICATION_VERSION = System.getProperty("java.specification.version");
    public static final String VM_VERSION = System.getProperty("java.runtime.version");
    public static final String VM_INFO = System.getProperty("java.vm.info");
    public static final String VENDOR_VERSION = System.getProperty("java.vm.version");
    public static final String VENDOR = System.getProperty("java.vm.vendor");
    public static final String JVM_NAME = System.getProperty("java.vm.name");
    public static final int ANDROID_VERSION = getAndroidVersion();
    public static final boolean IS_ANDROID_OPENJDK = getIsAndroidOpenJDK();
    public static final String GAE_VERSION = getGaeRuntimeVersion();

    public static String describePlatform() {
        String desc = "Java " + SPECIFICATION_VERSION + " (VM vendor name=\"" + VENDOR + "\", VM vendor version=" + VENDOR_VERSION + ", JVM name=\"" + JVM_NAME + "\", JVM version=" + VM_VERSION + ", JVM info=" + VM_INFO;
        if (ANDROID_VERSION != 0) {
            desc = desc + ", API level=" + ANDROID_VERSION;
        }
        return desc + ")";
    }

    public static boolean isThisJVM(String name) {
        return JVM_NAME.startsWith(name);
    }

    public static boolean isAndroidOpenJDK() {
        return IS_ANDROID_OPENJDK;
    }

    private static boolean getIsAndroidOpenJDK() {
        String bootClasspath;
        return (getAndroidVersion() == 0 || (bootClasspath = System.getProperty("java.boot.class.path")) == null || !bootClasspath.toLowerCase().contains("core-oj.jar")) ? false : true;
    }

    public static boolean isAfterJigsaw() {
        String version = SPECIFICATION_VERSION;
        return version.indexOf(46) < 0;
    }

    public static boolean isAfterJava11() {
        if (!isAfterJigsaw()) {
            return false;
        }
        int version = Integer.parseInt(SPECIFICATION_VERSION);
        return version >= 11;
    }

    public static boolean isGoogleAppEngine() {
        return GAE_VERSION != null;
    }

    private static String getGaeRuntimeVersion() {
        return System.getProperty("com.google.appengine.runtime.version");
    }

    private static int getAndroidVersion() {
        if (!isThisJVM(DALVIK)) {
            return 0;
        }
        return getAndroidVersion0();
    }

    private static int getAndroidVersion0() {
        try {
            Class<?> clazz = Class.forName("android.os.Build$VERSION");
            try {
                Field field = clazz.getField("SDK_INT");
                try {
                    int version = ((Integer) field.get(null)).intValue();
                    return version;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } catch (NoSuchFieldException e2) {
                return getOldAndroidVersion(clazz);
            }
        } catch (ClassNotFoundException e3) {
            throw new ObjenesisException(e3);
        }
    }

    private static int getOldAndroidVersion(Class<?> versionClass) {
        try {
            Field field = versionClass.getField("SDK");
            try {
                String version = (String) field.get(null);
                return Integer.parseInt(version);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e2) {
            throw new ObjenesisException(e2);
        }
    }

    private PlatformDescription() {
    }
}

package org.apache.catalina.core;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/core/AprStatus.class */
public class AprStatus {
    private static volatile boolean aprInitialized = false;
    private static volatile boolean aprAvailable = false;
    private static volatile boolean useOpenSSL = true;
    private static volatile boolean instanceCreated = false;

    public static boolean isAprInitialized() {
        return aprInitialized;
    }

    public static boolean isAprAvailable() {
        return aprAvailable;
    }

    public static boolean getUseOpenSSL() {
        return useOpenSSL;
    }

    public static boolean isInstanceCreated() {
        return instanceCreated;
    }

    public static void setAprInitialized(boolean aprInitialized2) {
        aprInitialized = aprInitialized2;
    }

    public static void setAprAvailable(boolean aprAvailable2) {
        aprAvailable = aprAvailable2;
    }

    public static void setUseOpenSSL(boolean useOpenSSL2) {
        useOpenSSL = useOpenSSL2;
    }

    public static void setInstanceCreated(boolean instanceCreated2) {
        instanceCreated = instanceCreated2;
    }
}

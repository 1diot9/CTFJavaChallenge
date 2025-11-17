package ch.qos.logback.classic.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/PlatformInfo.class */
public class PlatformInfo {
    private static final int UNINITIALIZED = -1;
    private static int hasJMXObjectName = -1;

    public static boolean hasJMXObjectName() {
        if (hasJMXObjectName == -1) {
            try {
                Class.forName("javax.management.ObjectName");
                hasJMXObjectName = 1;
            } catch (Throwable th) {
                hasJMXObjectName = 0;
            }
        }
        return hasJMXObjectName == 1;
    }
}

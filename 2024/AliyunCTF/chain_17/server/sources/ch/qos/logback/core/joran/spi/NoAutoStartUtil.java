package ch.qos.logback.core.joran.spi;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/NoAutoStartUtil.class */
public class NoAutoStartUtil {
    public static boolean notMarkedWithNoAutoStart(Object o) {
        if (o == null) {
            return false;
        }
        Class<?> clazz = o.getClass();
        NoAutoStart a = (NoAutoStart) clazz.getAnnotation(NoAutoStart.class);
        return a == null;
    }
}

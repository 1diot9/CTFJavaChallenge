package ch.qos.logback.core.status;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/WarnStatus.class */
public class WarnStatus extends StatusBase {
    public WarnStatus(String msg, Object origin) {
        super(1, msg, origin);
    }

    public WarnStatus(String msg, Object origin, Throwable t) {
        super(1, msg, origin, t);
    }
}

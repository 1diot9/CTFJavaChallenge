package ch.qos.logback.core.status;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/ErrorStatus.class */
public class ErrorStatus extends StatusBase {
    public ErrorStatus(String msg, Object origin) {
        super(2, msg, origin);
    }

    public ErrorStatus(String msg, Object origin, Throwable t) {
        super(2, msg, origin, t);
    }
}

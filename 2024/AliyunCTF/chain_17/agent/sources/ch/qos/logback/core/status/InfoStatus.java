package ch.qos.logback.core.status;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/InfoStatus.class */
public class InfoStatus extends StatusBase {
    public InfoStatus(String msg, Object origin) {
        super(0, msg, origin);
    }

    public InfoStatus(String msg, Object origin, Throwable t) {
        super(0, msg, origin, t);
    }
}

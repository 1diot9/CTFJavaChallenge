package ch.qos.logback.core.status;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/StatusListener.class */
public interface StatusListener {
    void addStatusEvent(Status status);

    default boolean isResetResistant() {
        return false;
    }
}

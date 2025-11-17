package ch.qos.logback.core.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/LifeCycle.class */
public interface LifeCycle {
    void start();

    void stop();

    boolean isStarted();
}

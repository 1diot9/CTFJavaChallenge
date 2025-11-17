package org.springframework.context;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/SmartLifecycle.class */
public interface SmartLifecycle extends Lifecycle, Phased {
    public static final int DEFAULT_PHASE = Integer.MAX_VALUE;

    default boolean isAutoStartup() {
        return true;
    }

    default void stop(Runnable callback) {
        stop();
        callback.run();
    }

    default int getPhase() {
        return Integer.MAX_VALUE;
    }
}

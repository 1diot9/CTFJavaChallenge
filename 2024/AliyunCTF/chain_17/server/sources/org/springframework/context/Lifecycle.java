package org.springframework.context;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/Lifecycle.class */
public interface Lifecycle {
    void start();

    void stop();

    boolean isRunning();
}

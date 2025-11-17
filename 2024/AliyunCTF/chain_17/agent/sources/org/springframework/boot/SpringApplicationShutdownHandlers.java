package org.springframework.boot;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationShutdownHandlers.class */
public interface SpringApplicationShutdownHandlers {
    void add(Runnable action);

    void remove(Runnable action);
}

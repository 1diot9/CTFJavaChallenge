package org.springframework.boot;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationShutdownHandlers.class */
public interface SpringApplicationShutdownHandlers {
    void add(Runnable action);

    void remove(Runnable action);
}

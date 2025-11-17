package org.springframework.boot.rsocket.server;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/server/RSocketServerCustomizer.class */
public interface RSocketServerCustomizer {
    void customize(io.rsocket.core.RSocketServer rSocketServer);
}

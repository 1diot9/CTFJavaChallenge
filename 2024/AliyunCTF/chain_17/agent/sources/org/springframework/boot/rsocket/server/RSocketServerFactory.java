package org.springframework.boot.rsocket.server;

import io.rsocket.SocketAcceptor;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/server/RSocketServerFactory.class */
public interface RSocketServerFactory {
    RSocketServer create(SocketAcceptor socketAcceptor);
}

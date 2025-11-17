package org.springframework.boot.rsocket.server;

import java.net.InetSocketAddress;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/server/RSocketServer.class */
public interface RSocketServer {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/server/RSocketServer$Transport.class */
    public enum Transport {
        TCP,
        WEBSOCKET
    }

    void start() throws RSocketServerException;

    void stop() throws RSocketServerException;

    InetSocketAddress address();
}

package org.springframework.boot.web.server;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/server/WebServer.class */
public interface WebServer {
    void start() throws WebServerException;

    void stop() throws WebServerException;

    int getPort();

    default void shutDownGracefully(GracefulShutdownCallback callback) {
        callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
    }

    default void destroy() {
        stop();
    }
}

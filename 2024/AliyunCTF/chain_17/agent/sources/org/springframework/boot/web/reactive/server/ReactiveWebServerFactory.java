package org.springframework.boot.web.reactive.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.http.server.reactive.HttpHandler;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/server/ReactiveWebServerFactory.class */
public interface ReactiveWebServerFactory extends WebServerFactory {
    WebServer getWebServer(HttpHandler httpHandler);
}

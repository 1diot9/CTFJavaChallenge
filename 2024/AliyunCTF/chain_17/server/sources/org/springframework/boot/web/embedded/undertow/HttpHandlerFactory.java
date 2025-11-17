package org.springframework.boot.web.embedded.undertow;

import io.undertow.server.HttpHandler;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/HttpHandlerFactory.class */
public interface HttpHandlerFactory {
    HttpHandler getHandler(HttpHandler next);
}

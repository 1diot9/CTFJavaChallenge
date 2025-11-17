package org.springframework.web.server;

import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/WebHandler.class */
public interface WebHandler {
    Mono<Void> handle(ServerWebExchange exchange);
}

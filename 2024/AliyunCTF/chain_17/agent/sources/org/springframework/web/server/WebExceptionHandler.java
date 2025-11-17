package org.springframework.web.server;

import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/server/WebExceptionHandler.class */
public interface WebExceptionHandler {
    Mono<Void> handle(ServerWebExchange exchange, Throwable ex);
}

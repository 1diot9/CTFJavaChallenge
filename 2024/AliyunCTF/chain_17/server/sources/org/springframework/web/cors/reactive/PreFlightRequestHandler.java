package org.springframework.web.cors.reactive;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/reactive/PreFlightRequestHandler.class */
public interface PreFlightRequestHandler {
    Mono<Void> handlePreFlight(ServerWebExchange exchange);
}

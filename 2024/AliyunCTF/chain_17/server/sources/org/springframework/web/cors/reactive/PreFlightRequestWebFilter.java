package org.springframework.web.cors.reactive;

import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/reactive/PreFlightRequestWebFilter.class */
public class PreFlightRequestWebFilter implements WebFilter {
    private final PreFlightRequestHandler handler;

    public PreFlightRequestWebFilter(PreFlightRequestHandler handler) {
        Assert.notNull(handler, "PreFlightRequestHandler is required");
        this.handler = handler;
    }

    @Override // org.springframework.web.server.WebFilter
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return CorsUtils.isPreFlightRequest(exchange.getRequest()) ? this.handler.handlePreFlight(exchange) : chain.filter(exchange);
    }
}

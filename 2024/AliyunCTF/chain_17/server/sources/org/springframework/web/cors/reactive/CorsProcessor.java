package org.springframework.web.cors.reactive;

import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/reactive/CorsProcessor.class */
public interface CorsProcessor {
    boolean process(@Nullable CorsConfiguration configuration, ServerWebExchange exchange);
}

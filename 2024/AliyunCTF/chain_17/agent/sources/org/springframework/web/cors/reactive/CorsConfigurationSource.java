package org.springframework.web.cors.reactive;

import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/reactive/CorsConfigurationSource.class */
public interface CorsConfigurationSource {
    @Nullable
    CorsConfiguration getCorsConfiguration(ServerWebExchange exchange);
}

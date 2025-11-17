package org.springframework.web.cors.reactive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/reactive/UrlBasedCorsConfigurationSource.class */
public class UrlBasedCorsConfigurationSource implements CorsConfigurationSource {
    private final PathPatternParser patternParser;
    private final Map<PathPattern, CorsConfiguration> corsConfigurations;

    public UrlBasedCorsConfigurationSource() {
        this(PathPatternParser.defaultInstance);
    }

    public UrlBasedCorsConfigurationSource(PathPatternParser patternParser) {
        this.corsConfigurations = new LinkedHashMap();
        this.patternParser = patternParser;
    }

    public void setCorsConfigurations(@Nullable Map<String, CorsConfiguration> configMap) {
        this.corsConfigurations.clear();
        if (configMap != null) {
            configMap.forEach(this::registerCorsConfiguration);
        }
    }

    public void registerCorsConfiguration(String path, CorsConfiguration config) {
        this.corsConfigurations.put(this.patternParser.parse(path), config);
    }

    @Override // org.springframework.web.cors.reactive.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        PathContainer path = exchange.getRequest().getPath().pathWithinApplication();
        for (Map.Entry<PathPattern, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
            if (entry.getKey().matches(path)) {
                return entry.getValue();
            }
        }
        return null;
    }
}

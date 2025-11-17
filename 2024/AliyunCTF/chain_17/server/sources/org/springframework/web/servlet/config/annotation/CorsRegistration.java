package org.springframework.web.servlet.config.annotation;

import java.util.Arrays;
import org.springframework.web.cors.CorsConfiguration;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/config/annotation/CorsRegistration.class */
public class CorsRegistration {
    private final String pathPattern;
    private CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();

    public CorsRegistration(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public CorsRegistration allowedOrigins(String... origins) {
        this.config.setAllowedOrigins(Arrays.asList(origins));
        return this;
    }

    public CorsRegistration allowedOriginPatterns(String... patterns) {
        this.config.setAllowedOriginPatterns(Arrays.asList(patterns));
        return this;
    }

    public CorsRegistration allowedMethods(String... methods) {
        this.config.setAllowedMethods(Arrays.asList(methods));
        return this;
    }

    public CorsRegistration allowedHeaders(String... headers) {
        this.config.setAllowedHeaders(Arrays.asList(headers));
        return this;
    }

    public CorsRegistration exposedHeaders(String... headers) {
        this.config.setExposedHeaders(Arrays.asList(headers));
        return this;
    }

    public CorsRegistration allowCredentials(boolean allowCredentials) {
        this.config.setAllowCredentials(Boolean.valueOf(allowCredentials));
        return this;
    }

    public CorsRegistration allowPrivateNetwork(boolean allowPrivateNetwork) {
        this.config.setAllowPrivateNetwork(Boolean.valueOf(allowPrivateNetwork));
        return this;
    }

    public CorsRegistration maxAge(long maxAge) {
        this.config.setMaxAge(Long.valueOf(maxAge));
        return this;
    }

    public CorsRegistration combine(CorsConfiguration other) {
        this.config = this.config.combine(other);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getPathPattern() {
        return this.pathPattern;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CorsConfiguration getCorsConfiguration() {
        return this.config;
    }
}

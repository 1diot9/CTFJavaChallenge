package org.springframework.web.cors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/CorsConfigurationSource.class */
public interface CorsConfigurationSource {
    @Nullable
    CorsConfiguration getCorsConfiguration(HttpServletRequest request);
}

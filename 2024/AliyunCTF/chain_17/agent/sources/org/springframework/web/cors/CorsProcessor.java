package org.springframework.web.cors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/cors/CorsProcessor.class */
public interface CorsProcessor {
    boolean processRequest(@Nullable CorsConfiguration configuration, HttpServletRequest request, HttpServletResponse response) throws IOException;
}

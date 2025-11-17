package org.springframework.web.servlet.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/MatchableHandlerMapping.class */
public interface MatchableHandlerMapping extends HandlerMapping {
    @Nullable
    RequestMatchResult match(HttpServletRequest request, String pattern);

    @Nullable
    default PathPatternParser getPatternParser() {
        return null;
    }
}

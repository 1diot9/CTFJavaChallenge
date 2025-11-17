package org.springframework.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/RequestToViewNameTranslator.class */
public interface RequestToViewNameTranslator {
    @Nullable
    String getViewName(HttpServletRequest request) throws Exception;
}

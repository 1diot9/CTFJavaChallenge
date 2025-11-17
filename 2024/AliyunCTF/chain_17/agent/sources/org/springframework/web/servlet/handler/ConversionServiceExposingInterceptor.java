package org.springframework.web.servlet.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/ConversionServiceExposingInterceptor.class */
public class ConversionServiceExposingInterceptor implements HandlerInterceptor {
    private final ConversionService conversionService;

    public ConversionServiceExposingInterceptor(ConversionService conversionService) {
        Assert.notNull(conversionService, "The ConversionService may not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.web.servlet.HandlerInterceptor
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        request.setAttribute(ConversionService.class.getName(), this.conversionService);
        return true;
    }
}

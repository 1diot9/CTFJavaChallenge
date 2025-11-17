package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcRegistrations.class */
public interface WebMvcRegistrations {
    default RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return null;
    }

    default RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return null;
    }

    default ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
        return null;
    }
}

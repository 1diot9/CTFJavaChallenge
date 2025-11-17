package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/handler/HandlerMethodMappingNamingStrategy.class */
public interface HandlerMethodMappingNamingStrategy<T> {
    String getName(HandlerMethod handlerMethod, T mapping);
}

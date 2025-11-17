package org.springframework.web.servlet.mvc.method;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/RequestMappingInfoHandlerMethodMappingNamingStrategy.class */
public class RequestMappingInfoHandlerMethodMappingNamingStrategy implements HandlerMethodMappingNamingStrategy<RequestMappingInfo> {
    public static final String SEPARATOR = "#";

    @Override // org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
    public String getName(HandlerMethod handlerMethod, RequestMappingInfo mapping) {
        if (mapping.getName() != null) {
            return mapping.getName();
        }
        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName();
        for (int i = 0; i < simpleTypeName.length(); i++) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }
        sb.append("#").append(handlerMethod.getMethod().getName());
        return sb.toString();
    }
}

package org.springframework.boot.autoconfigure.security.servlet;

import org.springframework.security.web.util.matcher.RequestMatcher;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/RequestMatcherProvider.class */
public interface RequestMatcherProvider {
    RequestMatcher getRequestMatcher(String pattern);
}

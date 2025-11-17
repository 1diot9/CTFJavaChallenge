package org.springframework.boot.autoconfigure.security.servlet;

import java.util.function.Function;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/AntPathRequestMatcherProvider.class */
public class AntPathRequestMatcherProvider implements RequestMatcherProvider {
    private final Function<String, String> pathFactory;

    public AntPathRequestMatcherProvider(Function<String, String> pathFactory) {
        this.pathFactory = pathFactory;
    }

    @Override // org.springframework.boot.autoconfigure.security.servlet.RequestMatcherProvider
    public RequestMatcher getRequestMatcher(String pattern) {
        return new AntPathRequestMatcher(this.pathFactory.apply(pattern));
    }
}

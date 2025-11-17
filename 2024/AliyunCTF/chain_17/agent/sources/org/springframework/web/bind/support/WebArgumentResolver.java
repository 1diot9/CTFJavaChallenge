package org.springframework.web.bind.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/WebArgumentResolver.class */
public interface WebArgumentResolver {
    public static final Object UNRESOLVED = new Object();

    @Nullable
    Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception;
}

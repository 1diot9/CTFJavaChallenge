package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpServiceArgumentResolver.class */
public interface HttpServiceArgumentResolver {
    boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues);
}

package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.util.UriBuilderFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/UriBuilderFactoryArgumentResolver.class */
public class UriBuilderFactoryArgumentResolver implements HttpServiceArgumentResolver {
    @Override // org.springframework.web.service.invoker.HttpServiceArgumentResolver
    public boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (!parameter.getParameterType().equals(UriBuilderFactory.class)) {
            return false;
        }
        if (argument != null) {
            requestValues.setUriBuilderFactory((UriBuilderFactory) argument);
            return true;
        }
        return true;
    }
}

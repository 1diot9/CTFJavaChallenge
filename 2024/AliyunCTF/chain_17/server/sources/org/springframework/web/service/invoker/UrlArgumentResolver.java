package org.springframework.web.service.invoker;

import java.net.URI;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/UrlArgumentResolver.class */
public class UrlArgumentResolver implements HttpServiceArgumentResolver {
    @Override // org.springframework.web.service.invoker.HttpServiceArgumentResolver
    public boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (!parameter.getParameterType().equals(URI.class)) {
            return false;
        }
        if (argument != null) {
            requestValues.setUri((URI) argument);
            return true;
        }
        return true;
    }
}

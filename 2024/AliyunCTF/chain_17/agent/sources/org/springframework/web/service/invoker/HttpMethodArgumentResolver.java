package org.springframework.web.service.invoker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/HttpMethodArgumentResolver.class */
public class HttpMethodArgumentResolver implements HttpServiceArgumentResolver {
    private static final Log logger = LogFactory.getLog((Class<?>) HttpMethodArgumentResolver.class);

    @Override // org.springframework.web.service.invoker.HttpServiceArgumentResolver
    public boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (!parameter.getParameterType().equals(HttpMethod.class)) {
            return false;
        }
        Assert.notNull(argument, "HttpMethod is required");
        HttpMethod httpMethod = (HttpMethod) argument;
        requestValues.setHttpMethod(httpMethod);
        if (logger.isTraceEnabled()) {
            logger.trace("Resolved HTTP method to: " + httpMethod.name());
            return true;
        }
        return true;
    }
}

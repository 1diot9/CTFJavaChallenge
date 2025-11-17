package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/RequestAttributeArgumentResolver.class */
public class RequestAttributeArgumentResolver extends AbstractNamedValueArgumentResolver {
    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected AbstractNamedValueArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestAttribute annot = (RequestAttribute) parameter.getParameterAnnotation(RequestAttribute.class);
        if (annot == null) {
            return null;
        }
        return new AbstractNamedValueArgumentResolver.NamedValueInfo(annot.name(), annot.required(), null, "request attribute", false);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        requestValues.addAttribute(name, value);
    }
}

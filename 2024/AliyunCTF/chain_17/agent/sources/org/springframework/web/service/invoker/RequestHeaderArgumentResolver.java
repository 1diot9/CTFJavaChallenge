package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/RequestHeaderArgumentResolver.class */
public class RequestHeaderArgumentResolver extends AbstractNamedValueArgumentResolver {
    public RequestHeaderArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected AbstractNamedValueArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestHeader annot = (RequestHeader) parameter.getParameterAnnotation(RequestHeader.class);
        if (annot == null) {
            return null;
        }
        return new AbstractNamedValueArgumentResolver.NamedValueInfo(annot.name(), annot.required(), annot.defaultValue(), "request header", true);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        requestValues.addHeader(name, (String) value);
    }
}

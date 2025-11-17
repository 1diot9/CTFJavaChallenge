package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/RequestParamArgumentResolver.class */
public class RequestParamArgumentResolver extends AbstractNamedValueArgumentResolver {
    public RequestParamArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected AbstractNamedValueArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestParam annot = (RequestParam) parameter.getParameterAnnotation(RequestParam.class);
        if (annot == null) {
            return null;
        }
        return new AbstractNamedValueArgumentResolver.NamedValueInfo(annot.name(), annot.required(), annot.defaultValue(), "request parameter", true);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        requestValues.addRequestParameter(name, (String) value);
    }
}

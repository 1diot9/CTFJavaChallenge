package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/CookieValueArgumentResolver.class */
public class CookieValueArgumentResolver extends AbstractNamedValueArgumentResolver {
    public CookieValueArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected AbstractNamedValueArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        CookieValue annot = (CookieValue) parameter.getParameterAnnotation(CookieValue.class);
        if (annot == null) {
            return null;
        }
        return new AbstractNamedValueArgumentResolver.NamedValueInfo(annot.name(), annot.required(), annot.defaultValue(), "cookie value", true);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        requestValues.addCookie(name, (String) value);
    }
}

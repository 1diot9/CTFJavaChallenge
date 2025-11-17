package org.springframework.web.service.invoker;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/PathVariableArgumentResolver.class */
public class PathVariableArgumentResolver extends AbstractNamedValueArgumentResolver {
    public PathVariableArgumentResolver(ConversionService conversionService) {
        super(conversionService);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected AbstractNamedValueArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        PathVariable annot = (PathVariable) parameter.getParameterAnnotation(PathVariable.class);
        if (annot == null) {
            return null;
        }
        return new AbstractNamedValueArgumentResolver.NamedValueInfo(annot.name(), annot.required(), null, "path variable", false);
    }

    @Override // org.springframework.web.service.invoker.AbstractNamedValueArgumentResolver
    protected void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        requestValues.setUriVariable(name, (String) value);
    }
}

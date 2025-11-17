package org.springframework.web.servlet.mvc.method.annotation;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.method.support.UriComponentsContributor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/PathVariableMethodArgumentResolver.class */
public class PathVariableMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver implements UriComponentsContributor {
    private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(PathVariable.class)) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            PathVariable pathVariable = (PathVariable) parameter.getParameterAnnotation(PathVariable.class);
            return pathVariable != null && StringUtils.hasText(pathVariable.value());
        }
        return true;
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        PathVariable ann = (PathVariable) parameter.getParameterAnnotation(PathVariable.class);
        Assert.state(ann != null, "No PathVariable annotation");
        return new PathVariableNamedValueInfo(ann);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Map<String, String> uriTemplateVars = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        if (uriTemplateVars != null) {
            return uriTemplateVars.get(name);
        }
        return null;
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingPathVariableException(name, parameter);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        throw new MissingPathVariableException(name, parameter, true);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request) {
        String key = View.PATH_VARIABLES;
        Map<String, Object> pathVars = (Map) request.getAttribute(key, 0);
        if (pathVars == null) {
            pathVars = new HashMap<>();
            request.setAttribute(key, pathVars, 0);
        }
        pathVars.put(name, arg);
    }

    @Override // org.springframework.web.method.support.UriComponentsContributor
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        if (Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())) {
            return;
        }
        PathVariable ann = (PathVariable) parameter.getParameterAnnotation(PathVariable.class);
        String name = (ann == null || !StringUtils.hasLength(ann.value())) ? parameter.getParameterName() : ann.value();
        String formatted = formatUriValue(conversionService, new TypeDescriptor(parameter.nestedIfOptional()), value);
        uriVariables.put(name, formatted);
    }

    @Nullable
    protected String formatUriValue(@Nullable ConversionService cs, @Nullable TypeDescriptor sourceType, Object value) {
        if (value instanceof String) {
            String string = (String) value;
            return string;
        }
        if (cs != null) {
            return (String) cs.convert(value, sourceType, STRING_TYPE_DESCRIPTOR);
        }
        return value.toString();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/PathVariableMethodArgumentResolver$PathVariableNamedValueInfo.class */
    private static class PathVariableNamedValueInfo extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        public PathVariableNamedValueInfo(PathVariable annotation) {
            super(annotation.name(), annotation.required(), ValueConstants.DEFAULT_NONE);
        }
    }
}

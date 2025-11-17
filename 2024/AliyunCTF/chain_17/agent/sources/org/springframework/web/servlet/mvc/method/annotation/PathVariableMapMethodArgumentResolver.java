package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/PathVariableMapMethodArgumentResolver.class */
public class PathVariableMapMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        PathVariable ann = (PathVariable) parameter.getParameterAnnotation(PathVariable.class);
        return (ann == null || !Map.class.isAssignableFrom(parameter.getParameterType()) || StringUtils.hasText(ann.value())) ? false : true;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Map<String, String> uriTemplateVars = (Map) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, 0);
        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            return Collections.unmodifiableMap(uriTemplateVars);
        }
        return Collections.emptyMap();
    }
}

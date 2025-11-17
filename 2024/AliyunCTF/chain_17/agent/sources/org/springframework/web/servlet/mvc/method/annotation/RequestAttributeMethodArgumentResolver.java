package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestAttributeMethodArgumentResolver.class */
public class RequestAttributeMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestAttribute.class);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestAttribute ann = (RequestAttribute) parameter.getParameterAnnotation(RequestAttribute.class);
        Assert.state(ann != null, "No RequestAttribute annotation");
        return new AbstractNamedValueMethodArgumentResolver.NamedValueInfo(ann.name(), ann.required(), ValueConstants.DEFAULT_NONE);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) {
        return request.getAttribute(name, 0);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new ServletRequestBindingException("Missing request attribute '" + name + "' of type " + parameter.getNestedParameterType().getSimpleName());
    }
}

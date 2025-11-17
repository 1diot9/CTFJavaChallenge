package org.springframework.web.method.annotation;

import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/RequestHeaderMethodArgumentResolver.class */
public class RequestHeaderMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    public RequestHeaderMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class) && !Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType());
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        RequestHeader ann = (RequestHeader) parameter.getParameterAnnotation(RequestHeader.class);
        Assert.state(ann != null, "No RequestHeader annotation");
        return new RequestHeaderNamedValueInfo(ann);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        String[] headerValues = request.getHeaderValues(name);
        if (headerValues != null) {
            return headerValues.length == 1 ? headerValues[0] : headerValues;
        }
        return null;
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingRequestHeaderException(name, parameter);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        throw new MissingRequestHeaderException(name, parameter, true);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/RequestHeaderMethodArgumentResolver$RequestHeaderNamedValueInfo.class */
    private static final class RequestHeaderNamedValueInfo extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        private RequestHeaderNamedValueInfo(RequestHeader annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}

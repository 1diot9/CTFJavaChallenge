package org.springframework.web.method.annotation;

import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/ExpressionValueMethodArgumentResolver.class */
public class ExpressionValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    public ExpressionValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Value.class);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        Value ann = (Value) parameter.getParameterAnnotation(Value.class);
        Assert.state(ann != null, "No Value annotation");
        return new ExpressionValueNamedValueInfo(ann);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        return null;
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new UnsupportedOperationException("@Value is never required: " + parameter.getMethod());
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/ExpressionValueMethodArgumentResolver$ExpressionValueNamedValueInfo.class */
    private static final class ExpressionValueNamedValueInfo extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        private ExpressionValueNamedValueInfo(Value annotation) {
            super("@Value", false, annotation.value());
        }
    }
}

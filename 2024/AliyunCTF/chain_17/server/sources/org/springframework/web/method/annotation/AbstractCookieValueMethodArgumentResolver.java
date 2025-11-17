package org.springframework.web.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/AbstractCookieValueMethodArgumentResolver.class */
public abstract class AbstractCookieValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    public AbstractCookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CookieValue.class);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected AbstractNamedValueMethodArgumentResolver.NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        CookieValue annotation = (CookieValue) parameter.getParameterAnnotation(CookieValue.class);
        Assert.state(annotation != null, "No CookieValue annotation");
        return new CookieValueNamedValueInfo(annotation);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
        throw new MissingRequestCookieException(name, parameter);
    }

    @Override // org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver
    protected void handleMissingValueAfterConversion(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        throw new MissingRequestCookieException(name, parameter, true);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/AbstractCookieValueMethodArgumentResolver$CookieValueNamedValueInfo.class */
    private static final class CookieValueNamedValueInfo extends AbstractNamedValueMethodArgumentResolver.NamedValueInfo {
        private CookieValueNamedValueInfo(CookieValue annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}

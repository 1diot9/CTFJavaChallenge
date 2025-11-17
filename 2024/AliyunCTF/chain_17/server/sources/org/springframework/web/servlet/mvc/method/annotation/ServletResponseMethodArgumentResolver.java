package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletResponseMethodArgumentResolver.class */
public class ServletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return ServletResponse.class.isAssignableFrom(paramType) || OutputStream.class.isAssignableFrom(paramType) || Writer.class.isAssignableFrom(paramType);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        if (mavContainer != null) {
            mavContainer.setRequestHandled(true);
        }
        Class<?> paramType = parameter.getParameterType();
        if (ServletResponse.class.isAssignableFrom(paramType)) {
            return resolveNativeResponse(webRequest, paramType);
        }
        return resolveArgument(paramType, (ServletResponse) resolveNativeResponse(webRequest, ServletResponse.class));
    }

    private <T> T resolveNativeResponse(NativeWebRequest nativeWebRequest, Class<T> cls) {
        T t = (T) nativeWebRequest.getNativeResponse(cls);
        if (t == null) {
            throw new IllegalStateException("Current response is not of type [" + cls.getName() + "]: " + nativeWebRequest);
        }
        return t;
    }

    private Object resolveArgument(Class<?> paramType, ServletResponse response) throws IOException {
        if (OutputStream.class.isAssignableFrom(paramType)) {
            return response.getOutputStream();
        }
        if (Writer.class.isAssignableFrom(paramType)) {
            return response.getWriter();
        }
        throw new UnsupportedOperationException("Unknown parameter type: " + paramType);
    }
}

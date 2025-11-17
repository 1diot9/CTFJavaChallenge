package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletRequestMethodArgumentResolver.class */
public class ServletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Nullable
    private static Class<?> pushBuilder;

    static {
        try {
            pushBuilder = ClassUtils.forName("jakarta.servlet.http.PushBuilder", ServletRequestMethodArgumentResolver.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            pushBuilder = null;
        }
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return WebRequest.class.isAssignableFrom(paramType) || ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType) || HttpSession.class.isAssignableFrom(paramType) || (pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) || ((Principal.class.isAssignableFrom(paramType) && !parameter.hasParameterAnnotations()) || InputStream.class.isAssignableFrom(paramType) || Reader.class.isAssignableFrom(paramType) || HttpMethod.class == paramType || Locale.class == paramType || TimeZone.class == paramType || ZoneId.class == paramType);
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        if (WebRequest.class.isAssignableFrom(paramType)) {
            if (!paramType.isInstance(webRequest)) {
                throw new IllegalStateException("Current request is not of type [" + paramType.getName() + "]: " + webRequest);
            }
            return webRequest;
        }
        if (ServletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {
            return resolveNativeRequest(webRequest, paramType);
        }
        return resolveArgument(paramType, (HttpServletRequest) resolveNativeRequest(webRequest, HttpServletRequest.class));
    }

    private <T> T resolveNativeRequest(NativeWebRequest nativeWebRequest, Class<T> cls) {
        T t = (T) nativeWebRequest.getNativeRequest(cls);
        if (t == null) {
            throw new IllegalStateException("Current request is not of type [" + cls.getName() + "]: " + nativeWebRequest);
        }
        return t;
    }

    @Nullable
    private Object resolveArgument(Class<?> paramType, HttpServletRequest request) throws IOException {
        if (HttpSession.class.isAssignableFrom(paramType)) {
            HttpSession session = request.getSession();
            if (session != null && !paramType.isInstance(session)) {
                throw new IllegalStateException("Current session is not of type [" + paramType.getName() + "]: " + session);
            }
            return session;
        }
        if (pushBuilder != null && pushBuilder.isAssignableFrom(paramType)) {
            return PushBuilderDelegate.resolvePushBuilder(request, paramType);
        }
        if (InputStream.class.isAssignableFrom(paramType)) {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null && !paramType.isInstance(inputStream)) {
                throw new IllegalStateException("Request input stream is not of type [" + paramType.getName() + "]: " + inputStream);
            }
            return inputStream;
        }
        if (Reader.class.isAssignableFrom(paramType)) {
            Reader reader = request.getReader();
            if (reader != null && !paramType.isInstance(reader)) {
                throw new IllegalStateException("Request body reader is not of type [" + paramType.getName() + "]: " + reader);
            }
            return reader;
        }
        if (Principal.class.isAssignableFrom(paramType)) {
            Principal userPrincipal = request.getUserPrincipal();
            if (userPrincipal != null && !paramType.isInstance(userPrincipal)) {
                throw new IllegalStateException("Current user principal is not of type [" + paramType.getName() + "]: " + userPrincipal);
            }
            return userPrincipal;
        }
        if (HttpMethod.class == paramType) {
            return HttpMethod.valueOf(request.getMethod());
        }
        if (Locale.class == paramType) {
            return RequestContextUtils.getLocale(request);
        }
        if (TimeZone.class == paramType) {
            TimeZone timeZone = RequestContextUtils.getTimeZone(request);
            return timeZone != null ? timeZone : TimeZone.getDefault();
        }
        if (ZoneId.class == paramType) {
            TimeZone timeZone2 = RequestContextUtils.getTimeZone(request);
            return timeZone2 != null ? timeZone2.toZoneId() : ZoneId.systemDefault();
        }
        throw new UnsupportedOperationException("Unknown parameter type: " + paramType.getName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/ServletRequestMethodArgumentResolver$PushBuilderDelegate.class */
    public static class PushBuilderDelegate {
        private PushBuilderDelegate() {
        }

        @Nullable
        public static Object resolvePushBuilder(HttpServletRequest request, Class<?> paramType) {
            PushBuilder pushBuilder = request.newPushBuilder();
            if (pushBuilder != null && !paramType.isInstance(pushBuilder)) {
                throw new IllegalStateException("Current push builder is not of type [" + paramType.getName() + "]: " + pushBuilder);
            }
            return pushBuilder;
        }
    }
}

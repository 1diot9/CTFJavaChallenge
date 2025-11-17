package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.ControllerAdviceBean;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/RequestResponseBodyAdviceChain.class */
class RequestResponseBodyAdviceChain implements RequestBodyAdvice, ResponseBodyAdvice<Object> {
    private final List<Object> requestBodyAdvice = new ArrayList(4);
    private final List<Object> responseBodyAdvice = new ArrayList(4);

    public RequestResponseBodyAdviceChain(@Nullable List<Object> requestResponseBodyAdvice) {
        this.requestBodyAdvice.addAll(getAdviceByType(requestResponseBodyAdvice, RequestBodyAdvice.class));
        this.responseBodyAdvice.addAll(getAdviceByType(requestResponseBodyAdvice, ResponseBodyAdvice.class));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> List<T> getAdviceByType(@Nullable List<Object> requestResponseBodyAdvice, Class<T> adviceType) {
        Class<?> cls;
        if (requestResponseBodyAdvice != null) {
            ArrayList arrayList = new ArrayList();
            for (Object advice : requestResponseBodyAdvice) {
                if (advice instanceof ControllerAdviceBean) {
                    ControllerAdviceBean adviceBean = (ControllerAdviceBean) advice;
                    cls = adviceBean.getBeanType();
                } else {
                    cls = advice.getClass();
                }
                Class<?> beanType = cls;
                if (beanType != null && adviceType.isAssignableFrom(beanType)) {
                    arrayList.add(advice);
                }
            }
            return arrayList;
        }
        return Collections.emptyList();
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
    public boolean supports(MethodParameter param, Type type, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
    public HttpInputMessage beforeBodyRead(HttpInputMessage request, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (advice.supports(parameter, targetType, converterType)) {
                request = advice.beforeBodyRead(request, parameter, targetType, converterType);
            }
        }
        return request;
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (advice.supports(parameter, targetType, converterType)) {
                body = advice.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
            }
        }
        return body;
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        return processBody(body, returnType, contentType, converterType, request, response);
    }

    @Override // org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice
    @Nullable
    public Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        for (RequestBodyAdvice advice : getMatchingAdvice(parameter, RequestBodyAdvice.class)) {
            if (advice.supports(parameter, targetType, converterType)) {
                body = advice.handleEmptyBody(body, inputMessage, parameter, targetType, converterType);
            }
        }
        return body;
    }

    @Nullable
    private <T> Object processBody(@Nullable Object body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        for (ResponseBodyAdvice<?> advice : getMatchingAdvice(returnType, ResponseBodyAdvice.class)) {
            if (advice.supports(returnType, converterType)) {
                body = advice.beforeBodyWrite(body, returnType, contentType, converterType, request, response);
            }
        }
        return body;
    }

    private <A> List<A> getMatchingAdvice(MethodParameter parameter, Class<? extends A> adviceType) {
        List<Object> availableAdvice = getAdvice(adviceType);
        if (CollectionUtils.isEmpty(availableAdvice)) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(availableAdvice.size());
        Iterator<Object> it = availableAdvice.iterator();
        while (it.hasNext()) {
            Object advice = it.next();
            if (advice instanceof ControllerAdviceBean) {
                ControllerAdviceBean adviceBean = (ControllerAdviceBean) advice;
                if (adviceBean.isApplicableToBeanType(parameter.getContainingClass())) {
                    advice = adviceBean.resolveBean();
                }
            }
            if (adviceType.isAssignableFrom(advice.getClass())) {
                arrayList.add(advice);
            }
        }
        return arrayList;
    }

    private List<Object> getAdvice(Class<?> adviceType) {
        if (RequestBodyAdvice.class == adviceType) {
            return this.requestBodyAdvice;
        }
        if (ResponseBodyAdvice.class == adviceType) {
            return this.responseBodyAdvice;
        }
        throw new IllegalArgumentException("Unexpected adviceType: " + adviceType);
    }
}

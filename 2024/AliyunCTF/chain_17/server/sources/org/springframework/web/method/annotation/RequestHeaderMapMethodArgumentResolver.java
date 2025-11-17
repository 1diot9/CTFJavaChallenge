package org.springframework.web.method.annotation;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/RequestHeaderMapMethodArgumentResolver.class */
public class RequestHeaderMapMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class) && Map.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        MultiValueMap<String, String> result;
        Class<?> paramType = parameter.getParameterType();
        if (MultiValueMap.class.isAssignableFrom(paramType)) {
            if (HttpHeaders.class.isAssignableFrom(paramType)) {
                result = new HttpHeaders();
            } else {
                result = new LinkedMultiValueMap<>();
            }
            Iterator<String> iterator = webRequest.getHeaderNames();
            while (iterator.hasNext()) {
                String headerName = iterator.next();
                String[] headerValues = webRequest.getHeaderValues(headerName);
                if (headerValues != null) {
                    for (String str : headerValues) {
                        result.add(headerName, str);
                    }
                }
            }
            return result;
        }
        Map<String, String> result2 = new LinkedHashMap<>();
        Iterator<String> iterator2 = webRequest.getHeaderNames();
        while (iterator2.hasNext()) {
            String headerName2 = iterator2.next();
            String headerValue = webRequest.getHeader(headerName2);
            if (headerValue != null) {
                result2.put(headerName2, headerValue);
            }
        }
        return result2;
    }
}

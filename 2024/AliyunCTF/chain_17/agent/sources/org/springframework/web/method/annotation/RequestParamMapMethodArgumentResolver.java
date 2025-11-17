package org.springframework.web.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/RequestParamMapMethodArgumentResolver.class */
public class RequestParamMapMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        RequestParam requestParam = (RequestParam) parameter.getParameterAnnotation(RequestParam.class);
        return (requestParam == null || !Map.class.isAssignableFrom(parameter.getParameterType()) || StringUtils.hasText(requestParam.name())) ? false : true;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
        if (MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            Class<?> valueType = resolvableType.as(MultiValueMap.class).getGeneric(1).resolve();
            if (valueType == MultipartFile.class) {
                MultipartRequest multipartRequest = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
                return multipartRequest != null ? multipartRequest.getMultiFileMap() : new LinkedMultiValueMap(0);
            }
            if (valueType == Part.class) {
                HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
                if (servletRequest != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                    Collection<Part> parts = servletRequest.getParts();
                    LinkedMultiValueMap<String, Part> result = new LinkedMultiValueMap<>(parts.size());
                    for (Part part : parts) {
                        result.add(part.getName(), part);
                    }
                    return result;
                }
                return new LinkedMultiValueMap(0);
            }
            Map<String, String[]> parameterMap = webRequest.getParameterMap();
            MultiValueMap<String, String> result2 = new LinkedMultiValueMap<>(parameterMap.size());
            parameterMap.forEach((key, values) -> {
                for (String value : values) {
                    result2.add(key, value);
                }
            });
            return result2;
        }
        Class<?> valueType2 = resolvableType.asMap().getGeneric(1).resolve();
        if (valueType2 == MultipartFile.class) {
            MultipartRequest multipartRequest2 = MultipartResolutionDelegate.resolveMultipartRequest(webRequest);
            return multipartRequest2 != null ? multipartRequest2.getFileMap() : new LinkedHashMap(0);
        }
        if (valueType2 == Part.class) {
            HttpServletRequest servletRequest2 = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
            if (servletRequest2 != null && MultipartResolutionDelegate.isMultipartRequest(servletRequest2)) {
                Collection<Part> parts2 = servletRequest2.getParts();
                LinkedHashMap<String, Part> result3 = CollectionUtils.newLinkedHashMap(parts2.size());
                for (Part part2 : parts2) {
                    if (!result3.containsKey(part2.getName())) {
                        result3.put(part2.getName(), part2);
                    }
                }
                return result3;
            }
            return new LinkedHashMap(0);
        }
        Map<String, String[]> parameterMap2 = webRequest.getParameterMap();
        Map<String, String> result4 = CollectionUtils.newLinkedHashMap(parameterMap2.size());
        parameterMap2.forEach((key2, values2) -> {
            if (values2.length > 0) {
                result4.put(key2, values2[0]);
            }
        });
        return result4;
    }
}

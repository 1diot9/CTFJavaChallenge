package org.springframework.web.bind.support;

import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/WebDataBinderFactory.class */
public interface WebDataBinderFactory {
    WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName) throws Exception;

    default WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName, ResolvableType targetType) throws Exception {
        return createBinder(webRequest, target, objectName);
    }
}

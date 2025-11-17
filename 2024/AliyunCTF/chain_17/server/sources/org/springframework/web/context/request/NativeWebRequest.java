package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/NativeWebRequest.class */
public interface NativeWebRequest extends WebRequest {
    Object getNativeRequest();

    @Nullable
    Object getNativeResponse();

    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> requiredType);

    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> requiredType);
}

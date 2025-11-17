package org.springframework.http.server.reactive;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ServerHttpResponse.class */
public interface ServerHttpResponse extends ReactiveHttpOutputMessage {
    boolean setStatusCode(@Nullable HttpStatusCode status);

    @Nullable
    HttpStatusCode getStatusCode();

    MultiValueMap<String, ResponseCookie> getCookies();

    void addCookie(ResponseCookie cookie);

    default boolean setRawStatusCode(@Nullable Integer value) {
        return setStatusCode(value != null ? HttpStatusCode.valueOf(value.intValue()) : null);
    }

    @Nullable
    @Deprecated(since = "6.0")
    default Integer getRawStatusCode() {
        HttpStatusCode httpStatus = getStatusCode();
        if (httpStatus != null) {
            return Integer.valueOf(httpStatus.value());
        }
        return null;
    }
}

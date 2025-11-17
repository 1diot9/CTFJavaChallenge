package org.springframework.http.client.reactive;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ClientHttpResponse.class */
public interface ClientHttpResponse extends ReactiveHttpInputMessage {
    HttpStatusCode getStatusCode();

    MultiValueMap<String, ResponseCookie> getCookies();

    default String getId() {
        return ObjectUtils.getIdentityHexString(this);
    }
}

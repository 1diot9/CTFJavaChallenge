package org.springframework.http.client.reactive;

import java.net.URI;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/reactive/ClientHttpRequest.class */
public interface ClientHttpRequest extends ReactiveHttpOutputMessage {
    HttpMethod getMethod();

    URI getURI();

    MultiValueMap<String, HttpCookie> getCookies();

    <T> T getNativeRequest();
}

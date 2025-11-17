package org.springframework.web.servlet.function;

import java.time.Duration;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/AsyncServerResponse.class */
public interface AsyncServerResponse extends ServerResponse {
    ServerResponse block();

    static AsyncServerResponse create(Object asyncResponse) {
        return DefaultAsyncServerResponse.create(asyncResponse, null);
    }

    static AsyncServerResponse create(Object asyncResponse, Duration timeout) {
        return DefaultAsyncServerResponse.create(asyncResponse, timeout);
    }
}

package org.springframework.http;

import java.net.URI;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpRequest.class */
public interface HttpRequest extends HttpMessage {
    HttpMethod getMethod();

    URI getURI();
}

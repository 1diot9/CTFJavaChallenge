package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ClientHttpRequest.class */
public interface ClientHttpRequest extends HttpRequest, HttpOutputMessage {
    ClientHttpResponse execute() throws IOException;
}

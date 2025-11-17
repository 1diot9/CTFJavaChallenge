package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ClientHttpRequestInterceptor.class */
public interface ClientHttpRequestInterceptor {
    ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException;
}

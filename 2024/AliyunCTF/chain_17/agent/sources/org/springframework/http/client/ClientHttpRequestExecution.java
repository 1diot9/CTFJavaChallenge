package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ClientHttpRequestExecution.class */
public interface ClientHttpRequestExecution {
    ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException;
}

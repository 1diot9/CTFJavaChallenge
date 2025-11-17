package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ClientHttpRequestFactory.class */
public interface ClientHttpRequestFactory {
    ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException;
}

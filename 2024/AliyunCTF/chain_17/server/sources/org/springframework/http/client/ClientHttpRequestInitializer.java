package org.springframework.http.client;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/ClientHttpRequestInitializer.class */
public interface ClientHttpRequestInitializer {
    void initialize(ClientHttpRequest request);
}

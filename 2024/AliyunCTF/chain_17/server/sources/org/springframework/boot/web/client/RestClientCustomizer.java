package org.springframework.boot.web.client;

import org.springframework.web.client.RestClient;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/RestClientCustomizer.class */
public interface RestClientCustomizer {
    void customize(RestClient.Builder restClientBuilder);
}

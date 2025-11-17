package org.springframework.boot.web.client;

import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/RestTemplateRequestCustomizer.class */
public interface RestTemplateRequestCustomizer<T extends ClientHttpRequest> {
    void customize(T request);
}

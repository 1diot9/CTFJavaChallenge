package org.springframework.boot.web.reactive.function.client;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/function/client/WebClientCustomizer.class */
public interface WebClientCustomizer {
    void customize(WebClient.Builder webClientBuilder);
}

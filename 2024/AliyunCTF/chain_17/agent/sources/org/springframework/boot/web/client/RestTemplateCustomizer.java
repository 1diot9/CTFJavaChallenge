package org.springframework.boot.web.client;

import org.springframework.web.client.RestTemplate;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/RestTemplateCustomizer.class */
public interface RestTemplateCustomizer {
    void customize(RestTemplate restTemplate);
}

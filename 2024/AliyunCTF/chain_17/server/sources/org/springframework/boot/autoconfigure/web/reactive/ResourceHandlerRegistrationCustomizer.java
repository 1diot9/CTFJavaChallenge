package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.web.reactive.config.ResourceHandlerRegistration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ResourceHandlerRegistrationCustomizer.class */
public interface ResourceHandlerRegistrationCustomizer {
    void customize(ResourceHandlerRegistration registration);
}

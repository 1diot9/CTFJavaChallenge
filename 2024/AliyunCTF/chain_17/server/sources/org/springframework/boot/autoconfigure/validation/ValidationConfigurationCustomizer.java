package org.springframework.boot.autoconfigure.validation;

import jakarta.validation.Configuration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/validation/ValidationConfigurationCustomizer.class */
public interface ValidationConfigurationCustomizer {
    void customize(Configuration<?> configuration);
}

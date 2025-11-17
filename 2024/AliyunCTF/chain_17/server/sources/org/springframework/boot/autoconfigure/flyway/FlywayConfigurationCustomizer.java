package org.springframework.boot.autoconfigure.flyway;

import org.flywaydb.core.api.configuration.FluentConfiguration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayConfigurationCustomizer.class */
public interface FlywayConfigurationCustomizer {
    void customize(FluentConfiguration configuration);
}

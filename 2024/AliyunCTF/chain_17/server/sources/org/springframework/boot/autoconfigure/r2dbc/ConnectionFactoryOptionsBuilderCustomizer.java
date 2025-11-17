package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactoryOptions;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/r2dbc/ConnectionFactoryOptionsBuilderCustomizer.class */
public interface ConnectionFactoryOptionsBuilderCustomizer {
    void customize(ConnectionFactoryOptions.Builder builder);
}

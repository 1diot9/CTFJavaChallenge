package org.springframework.boot.autoconfigure.jooq;

import org.jooq.impl.DefaultConfiguration;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/DefaultConfigurationCustomizer.class */
public interface DefaultConfigurationCustomizer {
    void customize(DefaultConfiguration configuration);
}

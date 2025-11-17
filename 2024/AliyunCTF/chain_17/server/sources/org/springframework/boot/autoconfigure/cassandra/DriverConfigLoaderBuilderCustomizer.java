package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/DriverConfigLoaderBuilderCustomizer.class */
public interface DriverConfigLoaderBuilderCustomizer {
    void customize(ProgrammaticDriverConfigLoaderBuilder builder);
}

package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.oss.driver.api.core.CqlSessionBuilder;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cassandra/CqlSessionBuilderCustomizer.class */
public interface CqlSessionBuilderCustomizer {
    void customize(CqlSessionBuilder cqlSessionBuilder);
}

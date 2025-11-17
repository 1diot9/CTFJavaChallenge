package org.springframework.boot.autoconfigure.neo4j;

import org.neo4j.driver.Config;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/ConfigBuilderCustomizer.class */
public interface ConfigBuilderCustomizer {
    void customize(Config.ConfigBuilder configBuilder);
}

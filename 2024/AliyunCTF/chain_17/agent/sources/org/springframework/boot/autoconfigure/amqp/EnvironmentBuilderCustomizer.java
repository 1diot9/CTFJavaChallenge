package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.stream.EnvironmentBuilder;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/EnvironmentBuilderCustomizer.class */
public interface EnvironmentBuilderCustomizer {
    void customize(EnvironmentBuilder builder);
}

package org.springframework.boot.autoconfigure.kafka;

import org.springframework.kafka.core.DefaultKafkaProducerFactory;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/DefaultKafkaProducerFactoryCustomizer.class */
public interface DefaultKafkaProducerFactoryCustomizer {
    void customize(DefaultKafkaProducerFactory<?, ?> producerFactory);
}

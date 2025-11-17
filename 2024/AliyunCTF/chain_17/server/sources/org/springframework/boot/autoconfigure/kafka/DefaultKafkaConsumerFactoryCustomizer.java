package org.springframework.boot.autoconfigure.kafka;

import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/DefaultKafkaConsumerFactoryCustomizer.class */
public interface DefaultKafkaConsumerFactoryCustomizer {
    void customize(DefaultKafkaConsumerFactory<?, ?> consumerFactory);
}

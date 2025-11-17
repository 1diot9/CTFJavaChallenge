package org.springframework.boot.autoconfigure.kafka;

import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/StreamsBuilderFactoryBeanCustomizer.class */
public interface StreamsBuilderFactoryBeanCustomizer {
    void customize(StreamsBuilderFactoryBean factoryBean);
}

package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.client.ConnectionFactory;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/ConnectionFactoryCustomizer.class */
public interface ConnectionFactoryCustomizer {
    void customize(ConnectionFactory factory);
}

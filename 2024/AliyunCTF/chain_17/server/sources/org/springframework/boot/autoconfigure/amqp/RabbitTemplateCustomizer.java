package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitTemplateCustomizer.class */
public interface RabbitTemplateCustomizer {
    void customize(RabbitTemplate rabbitTemplate);
}

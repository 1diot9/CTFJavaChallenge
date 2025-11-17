package org.springframework.boot.autoconfigure.amqp;

import org.springframework.retry.support.RetryTemplate;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitRetryTemplateCustomizer.class */
public interface RabbitRetryTemplateCustomizer {

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitRetryTemplateCustomizer$Target.class */
    public enum Target {
        SENDER,
        LISTENER
    }

    void customize(Target target, RetryTemplate retryTemplate);
}

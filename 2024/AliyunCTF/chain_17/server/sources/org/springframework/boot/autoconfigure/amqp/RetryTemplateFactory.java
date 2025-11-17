package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import java.util.Objects;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RetryTemplateFactory.class */
class RetryTemplateFactory {
    private final List<RabbitRetryTemplateCustomizer> customizers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RetryTemplateFactory(List<RabbitRetryTemplateCustomizer> customizers) {
        this.customizers = customizers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RetryTemplate createRetryTemplate(RabbitProperties.Retry properties, RabbitRetryTemplateCustomizer.Target target) {
        PropertyMapper map = PropertyMapper.get();
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getMaxAttempts);
        Objects.requireNonNull(policy);
        from.to((v1) -> {
            r1.setMaxAttempts(v1);
        });
        template.setRetryPolicy(policy);
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        Objects.requireNonNull(properties);
        PropertyMapper.Source as = map.from(properties::getInitialInterval).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(backOffPolicy);
        as.to((v1) -> {
            r1.setInitialInterval(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getMultiplier);
        Objects.requireNonNull(backOffPolicy);
        from2.to((v1) -> {
            r1.setMultiplier(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source as2 = map.from(properties::getMaxInterval).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(backOffPolicy);
        as2.to((v1) -> {
            r1.setMaxInterval(v1);
        });
        template.setBackOffPolicy(backOffPolicy);
        if (this.customizers != null) {
            for (RabbitRetryTemplateCustomizer customizer : this.customizers) {
                customizer.customize(target, template);
            }
        }
        return template;
    }
}

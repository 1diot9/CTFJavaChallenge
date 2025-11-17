package org.springframework.boot.autoconfigure.pulsar;

import java.util.Objects;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/DeadLetterPolicyMapper.class */
final class DeadLetterPolicyMapper {
    private DeadLetterPolicyMapper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DeadLetterPolicy map(PulsarProperties.Consumer.DeadLetterPolicy policy) {
        Assert.state(policy.getMaxRedeliverCount() > 0, "Pulsar DeadLetterPolicy must have a positive 'max-redelivery-count' property value");
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        DeadLetterPolicy.DeadLetterPolicyBuilder builder = DeadLetterPolicy.builder();
        Objects.requireNonNull(policy);
        PropertyMapper.Source from = map.from(policy::getMaxRedeliverCount);
        Objects.requireNonNull(builder);
        from.to((v1) -> {
            r1.maxRedeliverCount(v1);
        });
        Objects.requireNonNull(policy);
        PropertyMapper.Source from2 = map.from(policy::getRetryLetterTopic);
        Objects.requireNonNull(builder);
        from2.to(builder::retryLetterTopic);
        Objects.requireNonNull(policy);
        PropertyMapper.Source from3 = map.from(policy::getDeadLetterTopic);
        Objects.requireNonNull(builder);
        from3.to(builder::deadLetterTopic);
        Objects.requireNonNull(policy);
        PropertyMapper.Source from4 = map.from(policy::getInitialSubscriptionName);
        Objects.requireNonNull(builder);
        from4.to(builder::initialSubscriptionName);
        return builder.build();
    }
}

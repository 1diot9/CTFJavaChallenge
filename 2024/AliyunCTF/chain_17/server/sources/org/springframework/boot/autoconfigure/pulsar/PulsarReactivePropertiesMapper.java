package org.springframework.boot.autoconfigure.pulsar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.pulsar.reactive.client.api.ReactiveMessageConsumerBuilder;
import org.apache.pulsar.reactive.client.api.ReactiveMessageReaderBuilder;
import org.apache.pulsar.reactive.client.api.ReactiveMessageSenderBuilder;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.pulsar.reactive.listener.ReactivePulsarContainerProperties;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarReactivePropertiesMapper.class */
final class PulsarReactivePropertiesMapper {
    private final PulsarProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PulsarReactivePropertiesMapper(PulsarProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void customizeMessageSenderBuilder(ReactiveMessageSenderBuilder<T> builder) {
        PulsarProperties.Producer properties = this.properties.getProducer();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from = map.from((Supplier) properties::getName);
        Objects.requireNonNull(builder);
        from.to(builder::producerName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from2 = map.from((Supplier) properties::getTopicName);
        Objects.requireNonNull(builder);
        from2.to(builder::topic);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from3 = map.from((Supplier) properties::getSendTimeout);
        Objects.requireNonNull(builder);
        from3.to(builder::sendTimeout);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from4 = map.from((Supplier) properties::getMessageRoutingMode);
        Objects.requireNonNull(builder);
        from4.to(builder::messageRoutingMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from5 = map.from((Supplier) properties::getHashingScheme);
        Objects.requireNonNull(builder);
        from5.to(builder::hashingScheme);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from6 = map.from((Supplier) properties::isBatchingEnabled);
        Objects.requireNonNull(builder);
        from6.to((v1) -> {
            r1.batchingEnabled(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from7 = map.from((Supplier) properties::isChunkingEnabled);
        Objects.requireNonNull(builder);
        from7.to((v1) -> {
            r1.chunkingEnabled(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from8 = map.from((Supplier) properties::getCompressionType);
        Objects.requireNonNull(builder);
        from8.to(builder::compressionType);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from9 = map.from((Supplier) properties::getAccessMode);
        Objects.requireNonNull(builder);
        from9.to(builder::accessMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void customizeMessageConsumerBuilder(ReactiveMessageConsumerBuilder<T> builder) {
        PulsarProperties.Consumer properties = this.properties.getConsumer();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from = map.from((Supplier) properties::getName);
        Objects.requireNonNull(builder);
        from.to(builder::consumerName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<R> as = map.from((Supplier) properties::getTopics).as((v1) -> {
            return new ArrayList(v1);
        });
        Objects.requireNonNull(builder);
        as.to((v1) -> {
            r1.topics(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from2 = map.from((Supplier) properties::getTopicsPattern);
        Objects.requireNonNull(builder);
        from2.to(builder::topicsPattern);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from3 = map.from((Supplier) properties::getPriorityLevel);
        Objects.requireNonNull(builder);
        from3.to(builder::priorityLevel);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from4 = map.from((Supplier) properties::isReadCompacted);
        Objects.requireNonNull(builder);
        from4.to((v1) -> {
            r1.readCompacted(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<R> as2 = map.from((Supplier) properties::getDeadLetterPolicy).as(DeadLetterPolicyMapper::map);
        Objects.requireNonNull(builder);
        as2.to(builder::deadLetterPolicy);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from5 = map.from((Supplier) properties::isRetryEnable);
        Objects.requireNonNull(builder);
        from5.to((v1) -> {
            r1.retryLetterTopicEnable(v1);
        });
        customizerMessageConsumerBuilderSubscription(builder);
    }

    private <T> void customizerMessageConsumerBuilderSubscription(ReactiveMessageConsumerBuilder<T> builder) {
        PulsarProperties.Consumer.Subscription properties = this.properties.getConsumer().getSubscription();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from = map.from((Supplier) properties::getName);
        Objects.requireNonNull(builder);
        from.to(builder::subscriptionName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from2 = map.from((Supplier) properties::getInitialPosition);
        Objects.requireNonNull(builder);
        from2.to(builder::subscriptionInitialPosition);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from3 = map.from((Supplier) properties::getMode);
        Objects.requireNonNull(builder);
        from3.to(builder::subscriptionMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from4 = map.from((Supplier) properties::getTopicsMode);
        Objects.requireNonNull(builder);
        from4.to(builder::topicsPatternSubscriptionMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from5 = map.from((Supplier) properties::getType);
        Objects.requireNonNull(builder);
        from5.to(builder::subscriptionType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void customizeContainerProperties(ReactivePulsarContainerProperties<T> containerProperties) {
        customizePulsarContainerConsumerSubscriptionProperties(containerProperties);
        customizePulsarContainerListenerProperties(containerProperties);
    }

    private void customizePulsarContainerConsumerSubscriptionProperties(ReactivePulsarContainerProperties<?> containerProperties) {
        PulsarProperties.Consumer.Subscription properties = this.properties.getConsumer().getSubscription();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getType);
        Objects.requireNonNull(containerProperties);
        from.to(containerProperties::setSubscriptionType);
    }

    private void customizePulsarContainerListenerProperties(ReactivePulsarContainerProperties<?> containerProperties) {
        PulsarProperties.Listener properties = this.properties.getListener();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getSchemaType);
        Objects.requireNonNull(containerProperties);
        from.to(containerProperties::setSchemaType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void customizeMessageReaderBuilder(ReactiveMessageReaderBuilder<?> builder) {
        PulsarProperties.Reader properties = this.properties.getReader();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getName);
        Objects.requireNonNull(builder);
        from.to(builder::readerName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getTopics);
        Objects.requireNonNull(builder);
        from2.to(builder::topics);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getSubscriptionName);
        Objects.requireNonNull(builder);
        from3.to(builder::subscriptionName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getSubscriptionRolePrefix);
        Objects.requireNonNull(builder);
        from4.to(builder::generatedSubscriptionNamePrefix);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from5 = map.from(properties::isReadCompacted);
        Objects.requireNonNull(builder);
        from5.to(builder::readCompacted);
    }
}

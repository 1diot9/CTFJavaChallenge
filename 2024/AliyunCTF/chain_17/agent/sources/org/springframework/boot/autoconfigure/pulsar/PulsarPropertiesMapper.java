package org.springframework.boot.autoconfigure.pulsar;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.ReaderBuilder;
import org.apache.pulsar.common.util.ObjectMapperFactory;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.pulsar.listener.PulsarContainerProperties;
import org.springframework.pulsar.reader.PulsarReaderContainerProperties;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarPropertiesMapper.class */
final class PulsarPropertiesMapper {
    private final PulsarProperties properties;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarPropertiesMapper$AuthenticationConsumer.class */
    public interface AuthenticationConsumer {
        void accept(String authPluginClassName, String authParamString) throws PulsarClientException.UnsupportedAuthenticationException;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PulsarPropertiesMapper(PulsarProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void customizeClientBuilder(ClientBuilder clientBuilder, PulsarConnectionDetails connectionDetails) {
        PulsarProperties.Client properties = this.properties.getClient();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(connectionDetails);
        PropertyMapper.Source from = map.from(connectionDetails::getBrokerUrl);
        Objects.requireNonNull(clientBuilder);
        from.to(clientBuilder::serviceUrl);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getConnectionTimeout);
        Objects.requireNonNull(clientBuilder);
        from2.to(timeoutProperty((v1, v2) -> {
            r2.connectionTimeout(v1, v2);
        }));
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getOperationTimeout);
        Objects.requireNonNull(clientBuilder);
        from3.to(timeoutProperty((v1, v2) -> {
            r2.operationTimeout(v1, v2);
        }));
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getLookupTimeout);
        Objects.requireNonNull(clientBuilder);
        from4.to(timeoutProperty((v1, v2) -> {
            r2.lookupTimeout(v1, v2);
        }));
        Objects.requireNonNull(clientBuilder);
        customizeAuthentication(clientBuilder::authentication, properties.getAuthentication());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void customizeAdminBuilder(PulsarAdminBuilder adminBuilder, PulsarConnectionDetails connectionDetails) {
        PulsarProperties.Admin properties = this.properties.getAdmin();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(connectionDetails);
        PropertyMapper.Source from = map.from(connectionDetails::getAdminUrl);
        Objects.requireNonNull(adminBuilder);
        from.to(adminBuilder::serviceHttpUrl);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getConnectionTimeout);
        Objects.requireNonNull(adminBuilder);
        from2.to(timeoutProperty((v1, v2) -> {
            r2.connectionTimeout(v1, v2);
        }));
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getReadTimeout);
        Objects.requireNonNull(adminBuilder);
        from3.to(timeoutProperty((v1, v2) -> {
            r2.readTimeout(v1, v2);
        }));
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getRequestTimeout);
        Objects.requireNonNull(adminBuilder);
        from4.to(timeoutProperty((v1, v2) -> {
            r2.requestTimeout(v1, v2);
        }));
        Objects.requireNonNull(adminBuilder);
        customizeAuthentication(adminBuilder::authentication, properties.getAuthentication());
    }

    private void customizeAuthentication(AuthenticationConsumer authentication, PulsarProperties.Authentication properties) {
        if (StringUtils.hasText(properties.getPluginClassName())) {
            try {
                authentication.accept(properties.getPluginClassName(), getAuthenticationParamsJson(properties.getParam()));
            } catch (PulsarClientException.UnsupportedAuthenticationException ex) {
                throw new IllegalStateException("Unable to configure Pulsar authentication", ex);
            }
        }
    }

    private String getAuthenticationParamsJson(Map<String, String> params) {
        Map<String, String> sortedParams = new TreeMap<>(params);
        try {
            return ObjectMapperFactory.create().writeValueAsString(sortedParams);
        } catch (Exception ex) {
            throw new IllegalStateException("Could not convert auth parameters to encoded string", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void customizeProducerBuilder(ProducerBuilder<T> producerBuilder) {
        PulsarProperties.Producer properties = this.properties.getProducer();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from = map.from((Supplier) properties::getName);
        Objects.requireNonNull(producerBuilder);
        from.to(producerBuilder::producerName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from2 = map.from((Supplier) properties::getTopicName);
        Objects.requireNonNull(producerBuilder);
        from2.to(producerBuilder::topic);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from3 = map.from((Supplier) properties::getSendTimeout);
        Objects.requireNonNull(producerBuilder);
        from3.to(timeoutProperty((v1, v2) -> {
            r2.sendTimeout(v1, v2);
        }));
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from4 = map.from((Supplier) properties::getMessageRoutingMode);
        Objects.requireNonNull(producerBuilder);
        from4.to(producerBuilder::messageRoutingMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from5 = map.from((Supplier) properties::getHashingScheme);
        Objects.requireNonNull(producerBuilder);
        from5.to(producerBuilder::hashingScheme);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from6 = map.from((Supplier) properties::isBatchingEnabled);
        Objects.requireNonNull(producerBuilder);
        from6.to((v1) -> {
            r1.enableBatching(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from7 = map.from((Supplier) properties::isChunkingEnabled);
        Objects.requireNonNull(producerBuilder);
        from7.to((v1) -> {
            r1.enableChunking(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from8 = map.from((Supplier) properties::getCompressionType);
        Objects.requireNonNull(producerBuilder);
        from8.to(producerBuilder::compressionType);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from9 = map.from((Supplier) properties::getAccessMode);
        Objects.requireNonNull(producerBuilder);
        from9.to(producerBuilder::accessMode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void customizeConsumerBuilder(ConsumerBuilder<T> consumerBuilder) {
        PulsarProperties.Consumer properties = this.properties.getConsumer();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from = map.from((Supplier) properties::getName);
        Objects.requireNonNull(consumerBuilder);
        from.to(consumerBuilder::consumerName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<R> as = map.from((Supplier) properties::getTopics).as((v1) -> {
            return new ArrayList(v1);
        });
        Objects.requireNonNull(consumerBuilder);
        as.to((v1) -> {
            r1.topics(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from2 = map.from((Supplier) properties::getTopicsPattern);
        Objects.requireNonNull(consumerBuilder);
        from2.to(consumerBuilder::topicsPattern);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from3 = map.from((Supplier) properties::getPriorityLevel);
        Objects.requireNonNull(consumerBuilder);
        from3.to((v1) -> {
            r1.priorityLevel(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from4 = map.from((Supplier) properties::isReadCompacted);
        Objects.requireNonNull(consumerBuilder);
        from4.to((v1) -> {
            r1.readCompacted(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source<R> as2 = map.from((Supplier) properties::getDeadLetterPolicy).as(DeadLetterPolicyMapper::map);
        Objects.requireNonNull(consumerBuilder);
        as2.to(consumerBuilder::deadLetterPolicy);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from5 = map.from((Supplier) properties::isRetryEnable);
        Objects.requireNonNull(consumerBuilder);
        from5.to((v1) -> {
            r1.enableRetry(v1);
        });
        customizeConsumerBuilderSubscription(consumerBuilder);
    }

    private void customizeConsumerBuilderSubscription(ConsumerBuilder<?> consumerBuilder) {
        PulsarProperties.Consumer.Subscription properties = this.properties.getConsumer().getSubscription();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getName);
        Objects.requireNonNull(consumerBuilder);
        from.to(consumerBuilder::subscriptionName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getInitialPosition);
        Objects.requireNonNull(consumerBuilder);
        from2.to(consumerBuilder::subscriptionInitialPosition);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getMode);
        Objects.requireNonNull(consumerBuilder);
        from3.to(consumerBuilder::subscriptionMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getTopicsMode);
        Objects.requireNonNull(consumerBuilder);
        from4.to(consumerBuilder::subscriptionTopicsMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from5 = map.from(properties::getType);
        Objects.requireNonNull(consumerBuilder);
        from5.to(consumerBuilder::subscriptionType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void customizeContainerProperties(PulsarContainerProperties containerProperties) {
        customizePulsarContainerConsumerSubscriptionProperties(containerProperties);
        customizePulsarContainerListenerProperties(containerProperties);
    }

    private void customizePulsarContainerConsumerSubscriptionProperties(PulsarContainerProperties containerProperties) {
        PulsarProperties.Consumer.Subscription properties = this.properties.getConsumer().getSubscription();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getType);
        Objects.requireNonNull(containerProperties);
        from.to(containerProperties::setSubscriptionType);
    }

    private void customizePulsarContainerListenerProperties(PulsarContainerProperties containerProperties) {
        PulsarProperties.Listener properties = this.properties.getListener();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getSchemaType);
        Objects.requireNonNull(containerProperties);
        from.to(containerProperties::setSchemaType);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::isObservationEnabled);
        Objects.requireNonNull(containerProperties);
        from2.to((v1) -> {
            r1.setObservationEnabled(v1);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void customizeReaderBuilder(ReaderBuilder<T> readerBuilder) {
        PulsarProperties.Reader properties = this.properties.getReader();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from = map.from((Supplier) properties::getName);
        Objects.requireNonNull(readerBuilder);
        from.to(readerBuilder::readerName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from2 = map.from((Supplier) properties::getTopics);
        Objects.requireNonNull(readerBuilder);
        from2.to(readerBuilder::topics);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from3 = map.from((Supplier) properties::getSubscriptionName);
        Objects.requireNonNull(readerBuilder);
        from3.to(readerBuilder::subscriptionName);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from4 = map.from((Supplier) properties::getSubscriptionRolePrefix);
        Objects.requireNonNull(readerBuilder);
        from4.to(readerBuilder::subscriptionRolePrefix);
        Objects.requireNonNull(properties);
        PropertyMapper.Source<T> from5 = map.from((Supplier) properties::isReadCompacted);
        Objects.requireNonNull(readerBuilder);
        from5.to((v1) -> {
            r1.readCompacted(v1);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void customizeReaderContainerProperties(PulsarReaderContainerProperties readerContainerProperties) {
        PulsarProperties.Reader properties = this.properties.getReader();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getTopics);
        Objects.requireNonNull(readerContainerProperties);
        from.to(readerContainerProperties::setTopics);
    }

    private Consumer<Duration> timeoutProperty(BiConsumer<Integer, TimeUnit> setter) {
        return duration -> {
            setter.accept(Integer.valueOf((int) duration.toMillis()), TimeUnit.MILLISECONDS);
        };
    }
}

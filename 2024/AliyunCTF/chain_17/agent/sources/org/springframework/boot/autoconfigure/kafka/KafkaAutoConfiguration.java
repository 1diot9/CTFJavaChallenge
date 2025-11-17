package org.springframework.boot.autoconfigure.kafka;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.security.jaas.KafkaJaasLoginModuleInitializer;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.retry.backoff.BackOffPolicyBuilder;

@EnableConfigurationProperties({KafkaProperties.class})
@AutoConfiguration
@ConditionalOnClass({KafkaTemplate.class})
@Import({KafkaAnnotationDrivenConfiguration.class, KafkaStreamsAnnotationDrivenConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/KafkaAutoConfiguration.class */
public class KafkaAutoConfiguration {
    private final KafkaProperties properties;

    KafkaAutoConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean({KafkaConnectionDetails.class})
    @Bean
    PropertiesKafkaConnectionDetails kafkaConnectionDetails(KafkaProperties properties) {
        return new PropertiesKafkaConnectionDetails(properties);
    }

    @ConditionalOnMissingBean({KafkaTemplate.class})
    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory, ProducerListener<Object, Object> kafkaProducerListener, ObjectProvider<RecordMessageConverter> messageConverter) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        Objects.requireNonNull(kafkaTemplate);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        PropertyMapper.Source from = map.from((PropertyMapper) kafkaProducerListener);
        Objects.requireNonNull(kafkaTemplate);
        from.to(kafkaTemplate::setProducerListener);
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.properties.getTemplate().getDefaultTopic());
        Objects.requireNonNull(kafkaTemplate);
        from2.to(kafkaTemplate::setDefaultTopic);
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.properties.getTemplate().getTransactionIdPrefix());
        Objects.requireNonNull(kafkaTemplate);
        from3.to(kafkaTemplate::setTransactionIdPrefix);
        PropertyMapper.Source from4 = map.from((PropertyMapper) Boolean.valueOf(this.properties.getTemplate().isObservationEnabled()));
        Objects.requireNonNull(kafkaTemplate);
        from4.to((v1) -> {
            r1.setObservationEnabled(v1);
        });
        return kafkaTemplate;
    }

    @ConditionalOnMissingBean({ProducerListener.class})
    @Bean
    public LoggingProducerListener<Object, Object> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @ConditionalOnMissingBean({ConsumerFactory.class})
    @Bean
    public DefaultKafkaConsumerFactory<?, ?> kafkaConsumerFactory(KafkaConnectionDetails connectionDetails, ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers, ObjectProvider<SslBundles> sslBundles) {
        Map<String, Object> properties = this.properties.buildConsumerProperties(sslBundles.getIfAvailable());
        applyKafkaConnectionDetailsForConsumer(properties, connectionDetails);
        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(properties);
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(factory);
        });
        return factory;
    }

    @ConditionalOnMissingBean({ProducerFactory.class})
    @Bean
    public DefaultKafkaProducerFactory<?, ?> kafkaProducerFactory(KafkaConnectionDetails connectionDetails, ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers, ObjectProvider<SslBundles> sslBundles) {
        Map<String, Object> properties = this.properties.buildProducerProperties(sslBundles.getIfAvailable());
        applyKafkaConnectionDetailsForProducer(properties, connectionDetails);
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(properties);
        String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(factory);
        });
        return factory;
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.kafka.producer.transaction-id-prefix"})
    @Bean
    public KafkaTransactionManager<?, ?> kafkaTransactionManager(ProducerFactory<?, ?> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.kafka.jaas.enabled"})
    @Bean
    public KafkaJaasLoginModuleInitializer kafkaJaasInitializer() throws IOException {
        KafkaJaasLoginModuleInitializer jaas = new KafkaJaasLoginModuleInitializer();
        KafkaProperties.Jaas jaasProperties = this.properties.getJaas();
        if (jaasProperties.getControlFlag() != null) {
            jaas.setControlFlag(jaasProperties.getControlFlag());
        }
        if (jaasProperties.getLoginModule() != null) {
            jaas.setLoginModule(jaasProperties.getLoginModule());
        }
        jaas.setOptions(jaasProperties.getOptions());
        return jaas;
    }

    @ConditionalOnMissingBean
    @Bean
    public KafkaAdmin kafkaAdmin(KafkaConnectionDetails connectionDetails, ObjectProvider<SslBundles> sslBundles) {
        Map<String, Object> properties = this.properties.buildAdminProperties(sslBundles.getIfAvailable());
        applyKafkaConnectionDetailsForAdmin(properties, connectionDetails);
        KafkaAdmin kafkaAdmin = new KafkaAdmin(properties);
        KafkaProperties.Admin admin = this.properties.getAdmin();
        if (admin.getCloseTimeout() != null) {
            kafkaAdmin.setCloseTimeout((int) admin.getCloseTimeout().getSeconds());
        }
        if (admin.getOperationTimeout() != null) {
            kafkaAdmin.setOperationTimeout((int) admin.getOperationTimeout().getSeconds());
        }
        kafkaAdmin.setFatalIfBrokerNotAvailable(admin.isFailFast());
        kafkaAdmin.setModifyTopicConfigs(admin.isModifyTopicConfigs());
        kafkaAdmin.setAutoCreate(admin.isAutoCreate());
        return kafkaAdmin;
    }

    @ConditionalOnProperty(name = {"spring.kafka.retry.topic.enabled"})
    @Bean
    @ConditionalOnSingleCandidate(KafkaTemplate.class)
    public RetryTopicConfiguration kafkaRetryTopicConfiguration(KafkaTemplate<?, ?> kafkaTemplate) {
        KafkaProperties.Retry.Topic retryTopic = this.properties.getRetry().getTopic();
        RetryTopicConfigurationBuilder builder = RetryTopicConfigurationBuilder.newInstance().maxAttempts(retryTopic.getAttempts()).useSingleTopicForSameIntervals().suffixTopicsWithIndexValues().doNotAutoCreateRetryTopics();
        setBackOffPolicy(builder, retryTopic);
        return builder.create(kafkaTemplate);
    }

    private void applyKafkaConnectionDetailsForConsumer(Map<String, Object> properties, KafkaConnectionDetails connectionDetails) {
        properties.put("bootstrap.servers", connectionDetails.getConsumerBootstrapServers());
        if (!(connectionDetails instanceof PropertiesKafkaConnectionDetails)) {
            properties.put("security.protocol", "PLAINTEXT");
        }
    }

    private void applyKafkaConnectionDetailsForProducer(Map<String, Object> properties, KafkaConnectionDetails connectionDetails) {
        properties.put("bootstrap.servers", connectionDetails.getProducerBootstrapServers());
        if (!(connectionDetails instanceof PropertiesKafkaConnectionDetails)) {
            properties.put("security.protocol", "PLAINTEXT");
        }
    }

    private void applyKafkaConnectionDetailsForAdmin(Map<String, Object> properties, KafkaConnectionDetails connectionDetails) {
        properties.put("bootstrap.servers", connectionDetails.getAdminBootstrapServers());
        if (!(connectionDetails instanceof PropertiesKafkaConnectionDetails)) {
            properties.put("security.protocol", "PLAINTEXT");
        }
    }

    private static void setBackOffPolicy(RetryTopicConfigurationBuilder builder, KafkaProperties.Retry.Topic retryTopic) {
        long delay = retryTopic.getDelay() != null ? retryTopic.getDelay().toMillis() : 0L;
        if (delay > 0) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            BackOffPolicyBuilder backOffPolicy = BackOffPolicyBuilder.newBuilder();
            PropertyMapper.Source from = map.from((PropertyMapper) Long.valueOf(delay));
            Objects.requireNonNull(backOffPolicy);
            from.to((v1) -> {
                r1.delay(v1);
            });
            PropertyMapper.Source as = map.from((PropertyMapper) retryTopic.getMaxDelay()).as((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(backOffPolicy);
            as.to((v1) -> {
                r1.maxDelay(v1);
            });
            PropertyMapper.Source from2 = map.from((PropertyMapper) Double.valueOf(retryTopic.getMultiplier()));
            Objects.requireNonNull(backOffPolicy);
            from2.to((v1) -> {
                r1.multiplier(v1);
            });
            PropertyMapper.Source from3 = map.from((PropertyMapper) Boolean.valueOf(retryTopic.isRandomBackOff()));
            Objects.requireNonNull(backOffPolicy);
            from3.to((v1) -> {
                r1.random(v1);
            });
            builder.customBackoff(backOffPolicy.build());
            return;
        }
        builder.noBackoff();
    }
}

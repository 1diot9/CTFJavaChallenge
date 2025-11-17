package org.springframework.boot.autoconfigure.pulsar;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.reactive.client.adapter.AdaptedReactivePulsarClientFactory;
import org.apache.pulsar.reactive.client.adapter.ProducerCacheProvider;
import org.apache.pulsar.reactive.client.api.ReactiveMessageConsumerBuilder;
import org.apache.pulsar.reactive.client.api.ReactiveMessageReaderBuilder;
import org.apache.pulsar.reactive.client.api.ReactiveMessageSenderBuilder;
import org.apache.pulsar.reactive.client.api.ReactiveMessageSenderCache;
import org.apache.pulsar.reactive.client.api.ReactivePulsarClient;
import org.apache.pulsar.reactive.client.producercache.CaffeineShadedProducerCacheProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.pulsar.core.SchemaResolver;
import org.springframework.pulsar.core.TopicResolver;
import org.springframework.pulsar.reactive.config.DefaultReactivePulsarListenerContainerFactory;
import org.springframework.pulsar.reactive.config.annotation.EnableReactivePulsar;
import org.springframework.pulsar.reactive.core.DefaultReactivePulsarConsumerFactory;
import org.springframework.pulsar.reactive.core.DefaultReactivePulsarReaderFactory;
import org.springframework.pulsar.reactive.core.DefaultReactivePulsarSenderFactory;
import org.springframework.pulsar.reactive.core.ReactiveMessageConsumerBuilderCustomizer;
import org.springframework.pulsar.reactive.core.ReactiveMessageReaderBuilderCustomizer;
import org.springframework.pulsar.reactive.core.ReactiveMessageSenderBuilderCustomizer;
import org.springframework.pulsar.reactive.core.ReactivePulsarConsumerFactory;
import org.springframework.pulsar.reactive.core.ReactivePulsarReaderFactory;
import org.springframework.pulsar.reactive.core.ReactivePulsarSenderFactory;
import org.springframework.pulsar.reactive.core.ReactivePulsarTemplate;
import org.springframework.pulsar.reactive.listener.ReactivePulsarContainerProperties;

@AutoConfiguration(after = {PulsarAutoConfiguration.class})
@ConditionalOnClass({PulsarClient.class, ReactivePulsarClient.class, ReactivePulsarTemplate.class})
@Import({PulsarConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarReactiveAutoConfiguration.class */
public class PulsarReactiveAutoConfiguration {
    private final PulsarProperties properties;
    private final PulsarReactivePropertiesMapper propertiesMapper;

    PulsarReactiveAutoConfiguration(PulsarProperties properties) {
        this.properties = properties;
        this.propertiesMapper = new PulsarReactivePropertiesMapper(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    ReactivePulsarClient reactivePulsarClient(PulsarClient pulsarClient) {
        return AdaptedReactivePulsarClientFactory.create(pulsarClient);
    }

    @ConditionalOnClass({CaffeineShadedProducerCacheProvider.class})
    @ConditionalOnMissingBean({ProducerCacheProvider.class})
    @ConditionalOnProperty(name = {"spring.pulsar.producer.cache.enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    CaffeineShadedProducerCacheProvider reactivePulsarProducerCacheProvider() {
        PulsarProperties.Producer.Cache properties = this.properties.getProducer().getCache();
        return new CaffeineShadedProducerCacheProvider(properties.getExpireAfterAccess(), Duration.ofMinutes(10L), Long.valueOf(properties.getMaximumSize()), Integer.valueOf(properties.getInitialCapacity()));
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.pulsar.producer.cache.enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    ReactiveMessageSenderCache reactivePulsarMessageSenderCache(ObjectProvider<ProducerCacheProvider> producerCacheProvider) {
        return reactivePulsarMessageSenderCache(producerCacheProvider.getIfAvailable());
    }

    private ReactiveMessageSenderCache reactivePulsarMessageSenderCache(ProducerCacheProvider producerCacheProvider) {
        return producerCacheProvider != null ? AdaptedReactivePulsarClientFactory.createCache(producerCacheProvider) : AdaptedReactivePulsarClientFactory.createCache();
    }

    @ConditionalOnMissingBean({ReactivePulsarSenderFactory.class})
    @Bean
    DefaultReactivePulsarSenderFactory<?> reactivePulsarSenderFactory(ReactivePulsarClient reactivePulsarClient, ObjectProvider<ReactiveMessageSenderCache> reactiveMessageSenderCache, TopicResolver topicResolver, ObjectProvider<ReactiveMessageSenderBuilderCustomizer<?>> customizersProvider) {
        List<ReactiveMessageSenderBuilderCustomizer<?>> customizers = new ArrayList<>();
        PulsarReactivePropertiesMapper pulsarReactivePropertiesMapper = this.propertiesMapper;
        Objects.requireNonNull(pulsarReactivePropertiesMapper);
        customizers.add(pulsarReactivePropertiesMapper::customizeMessageSenderBuilder);
        customizers.addAll(customizersProvider.orderedStream().toList());
        List<ReactiveMessageSenderBuilderCustomizer<Object>> lambdaSafeCustomizers = List.of(builder -> {
            applyMessageSenderBuilderCustomizers(customizers, builder);
        });
        return DefaultReactivePulsarSenderFactory.builderFor(reactivePulsarClient).withDefaultConfigCustomizers(lambdaSafeCustomizers).withMessageSenderCache(reactiveMessageSenderCache.getIfAvailable()).withTopicResolver(topicResolver).build();
    }

    private void applyMessageSenderBuilderCustomizers(List<ReactiveMessageSenderBuilderCustomizer<?>> customizers, ReactiveMessageSenderBuilder<?> builder) {
        LambdaSafe.callbacks(ReactiveMessageSenderBuilderCustomizer.class, customizers, builder, new Object[0]).invoke(customizer -> {
            customizer.customize(builder);
        });
    }

    @ConditionalOnMissingBean({ReactivePulsarConsumerFactory.class})
    @Bean
    DefaultReactivePulsarConsumerFactory<?> reactivePulsarConsumerFactory(ReactivePulsarClient pulsarReactivePulsarClient, ObjectProvider<ReactiveMessageConsumerBuilderCustomizer<?>> customizersProvider) {
        List<ReactiveMessageConsumerBuilderCustomizer<?>> customizers = new ArrayList<>();
        PulsarReactivePropertiesMapper pulsarReactivePropertiesMapper = this.propertiesMapper;
        Objects.requireNonNull(pulsarReactivePropertiesMapper);
        customizers.add(pulsarReactivePropertiesMapper::customizeMessageConsumerBuilder);
        customizers.addAll(customizersProvider.orderedStream().toList());
        List<ReactiveMessageConsumerBuilderCustomizer<Object>> lambdaSafeCustomizers = List.of(builder -> {
            applyMessageConsumerBuilderCustomizers(customizers, builder);
        });
        return new DefaultReactivePulsarConsumerFactory<>(pulsarReactivePulsarClient, lambdaSafeCustomizers);
    }

    private void applyMessageConsumerBuilderCustomizers(List<ReactiveMessageConsumerBuilderCustomizer<?>> customizers, ReactiveMessageConsumerBuilder<?> builder) {
        LambdaSafe.callbacks(ReactiveMessageConsumerBuilderCustomizer.class, customizers, builder, new Object[0]).invoke(customizer -> {
            customizer.customize(builder);
        });
    }

    @ConditionalOnMissingBean(name = {"reactivePulsarListenerContainerFactory"})
    @Bean
    DefaultReactivePulsarListenerContainerFactory<?> reactivePulsarListenerContainerFactory(ReactivePulsarConsumerFactory<Object> reactivePulsarConsumerFactory, SchemaResolver schemaResolver, TopicResolver topicResolver) {
        ReactivePulsarContainerProperties<Object> containerProperties = new ReactivePulsarContainerProperties<>();
        containerProperties.setSchemaResolver(schemaResolver);
        containerProperties.setTopicResolver(topicResolver);
        this.propertiesMapper.customizeContainerProperties(containerProperties);
        return new DefaultReactivePulsarListenerContainerFactory<>(reactivePulsarConsumerFactory, containerProperties);
    }

    @ConditionalOnMissingBean({ReactivePulsarReaderFactory.class})
    @Bean
    DefaultReactivePulsarReaderFactory<?> reactivePulsarReaderFactory(ReactivePulsarClient reactivePulsarClient, ObjectProvider<ReactiveMessageReaderBuilderCustomizer<?>> customizersProvider) {
        List<ReactiveMessageReaderBuilderCustomizer<?>> customizers = new ArrayList<>();
        PulsarReactivePropertiesMapper pulsarReactivePropertiesMapper = this.propertiesMapper;
        Objects.requireNonNull(pulsarReactivePropertiesMapper);
        customizers.add(pulsarReactivePropertiesMapper::customizeMessageReaderBuilder);
        customizers.addAll(customizersProvider.orderedStream().toList());
        List<ReactiveMessageReaderBuilderCustomizer<Object>> lambdaSafeCustomizers = List.of(builder -> {
            applyMessageReaderBuilderCustomizers(customizers, builder);
        });
        return new DefaultReactivePulsarReaderFactory<>(reactivePulsarClient, lambdaSafeCustomizers);
    }

    private void applyMessageReaderBuilderCustomizers(List<ReactiveMessageReaderBuilderCustomizer<?>> customizers, ReactiveMessageReaderBuilder<?> builder) {
        LambdaSafe.callbacks(ReactiveMessageReaderBuilderCustomizer.class, customizers, builder, new Object[0]).invoke(customizer -> {
            customizer.customize(builder);
        });
    }

    @ConditionalOnMissingBean
    @Bean
    ReactivePulsarTemplate<?> pulsarReactiveTemplate(ReactivePulsarSenderFactory<?> reactivePulsarSenderFactory, SchemaResolver schemaResolver, TopicResolver topicResolver) {
        return new ReactivePulsarTemplate<>(reactivePulsarSenderFactory, schemaResolver, topicResolver);
    }

    @ConditionalOnMissingBean(name = {"org.springframework.pulsar.config.internalReactivePulsarListenerAnnotationProcessor"})
    @EnableReactivePulsar
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarReactiveAutoConfiguration$EnableReactivePulsarConfiguration.class */
    static class EnableReactivePulsarConfiguration {
        EnableReactivePulsarConfiguration() {
        }
    }
}

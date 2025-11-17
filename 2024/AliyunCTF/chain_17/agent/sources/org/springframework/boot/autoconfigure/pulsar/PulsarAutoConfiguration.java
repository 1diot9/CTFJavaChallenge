package org.springframework.boot.autoconfigure.pulsar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.ReaderBuilder;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.pulsar.annotation.EnablePulsar;
import org.springframework.pulsar.config.ConcurrentPulsarListenerContainerFactory;
import org.springframework.pulsar.config.DefaultPulsarReaderContainerFactory;
import org.springframework.pulsar.core.CachingPulsarProducerFactory;
import org.springframework.pulsar.core.ConsumerBuilderCustomizer;
import org.springframework.pulsar.core.DefaultPulsarConsumerFactory;
import org.springframework.pulsar.core.DefaultPulsarProducerFactory;
import org.springframework.pulsar.core.DefaultPulsarReaderFactory;
import org.springframework.pulsar.core.ProducerBuilderCustomizer;
import org.springframework.pulsar.core.PulsarConsumerFactory;
import org.springframework.pulsar.core.PulsarProducerFactory;
import org.springframework.pulsar.core.PulsarReaderFactory;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.pulsar.core.ReaderBuilderCustomizer;
import org.springframework.pulsar.core.SchemaResolver;
import org.springframework.pulsar.core.TopicResolver;
import org.springframework.pulsar.listener.PulsarContainerProperties;
import org.springframework.pulsar.reader.PulsarReaderContainerProperties;

@AutoConfiguration
@ConditionalOnClass({PulsarClient.class, PulsarTemplate.class})
@Import({PulsarConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarAutoConfiguration.class */
public class PulsarAutoConfiguration {
    private PulsarProperties properties;
    private PulsarPropertiesMapper propertiesMapper;

    PulsarAutoConfiguration(PulsarProperties properties) {
        this.properties = properties;
        this.propertiesMapper = new PulsarPropertiesMapper(properties);
    }

    @ConditionalOnMissingBean({PulsarProducerFactory.class})
    @ConditionalOnProperty(name = {"spring.pulsar.producer.cache.enabled"}, havingValue = "false")
    @Bean
    DefaultPulsarProducerFactory<?> pulsarProducerFactory(PulsarClient pulsarClient, TopicResolver topicResolver, ObjectProvider<ProducerBuilderCustomizer<?>> customizersProvider) {
        List<ProducerBuilderCustomizer<Object>> lambdaSafeCustomizers = lambdaSafeProducerBuilderCustomizers(customizersProvider);
        return new DefaultPulsarProducerFactory<>(pulsarClient, this.properties.getProducer().getTopicName(), lambdaSafeCustomizers, topicResolver);
    }

    @ConditionalOnMissingBean({PulsarProducerFactory.class})
    @ConditionalOnProperty(name = {"spring.pulsar.producer.cache.enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    CachingPulsarProducerFactory<?> cachingPulsarProducerFactory(PulsarClient pulsarClient, TopicResolver topicResolver, ObjectProvider<ProducerBuilderCustomizer<?>> customizersProvider) {
        PulsarProperties.Producer.Cache cacheProperties = this.properties.getProducer().getCache();
        List<ProducerBuilderCustomizer<Object>> lambdaSafeCustomizers = lambdaSafeProducerBuilderCustomizers(customizersProvider);
        return new CachingPulsarProducerFactory<>(pulsarClient, this.properties.getProducer().getTopicName(), lambdaSafeCustomizers, topicResolver, cacheProperties.getExpireAfterAccess(), Long.valueOf(cacheProperties.getMaximumSize()), Integer.valueOf(cacheProperties.getInitialCapacity()));
    }

    private List<ProducerBuilderCustomizer<Object>> lambdaSafeProducerBuilderCustomizers(ObjectProvider<ProducerBuilderCustomizer<?>> customizersProvider) {
        List<ProducerBuilderCustomizer<?>> customizers = new ArrayList<>();
        PulsarPropertiesMapper pulsarPropertiesMapper = this.propertiesMapper;
        Objects.requireNonNull(pulsarPropertiesMapper);
        customizers.add(pulsarPropertiesMapper::customizeProducerBuilder);
        customizers.addAll(customizersProvider.orderedStream().toList());
        return List.of(builder -> {
            applyProducerBuilderCustomizers(customizers, builder);
        });
    }

    private void applyProducerBuilderCustomizers(List<ProducerBuilderCustomizer<?>> customizers, ProducerBuilder<?> builder) {
        LambdaSafe.callbacks(ProducerBuilderCustomizer.class, customizers, builder, new Object[0]).invoke(customizer -> {
            customizer.customize(builder);
        });
    }

    @ConditionalOnMissingBean
    @Bean
    PulsarTemplate<?> pulsarTemplate(PulsarProducerFactory<?> pulsarProducerFactory, ObjectProvider<ProducerInterceptor> producerInterceptors, SchemaResolver schemaResolver, TopicResolver topicResolver) {
        return new PulsarTemplate<>(pulsarProducerFactory, producerInterceptors.orderedStream().toList(), schemaResolver, topicResolver, this.properties.getTemplate().isObservationsEnabled());
    }

    @ConditionalOnMissingBean({PulsarConsumerFactory.class})
    @Bean
    DefaultPulsarConsumerFactory<Object> pulsarConsumerFactory(PulsarClient pulsarClient, ObjectProvider<ConsumerBuilderCustomizer<?>> customizersProvider) {
        List<ConsumerBuilderCustomizer<?>> customizers = new ArrayList<>();
        PulsarPropertiesMapper pulsarPropertiesMapper = this.propertiesMapper;
        Objects.requireNonNull(pulsarPropertiesMapper);
        customizers.add(pulsarPropertiesMapper::customizeConsumerBuilder);
        customizers.addAll(customizersProvider.orderedStream().toList());
        List<ConsumerBuilderCustomizer<Object>> lambdaSafeCustomizers = List.of(builder -> {
            applyConsumerBuilderCustomizers(customizers, builder);
        });
        return new DefaultPulsarConsumerFactory<>(pulsarClient, lambdaSafeCustomizers);
    }

    private void applyConsumerBuilderCustomizers(List<ConsumerBuilderCustomizer<?>> customizers, ConsumerBuilder<?> builder) {
        LambdaSafe.callbacks(ConsumerBuilderCustomizer.class, customizers, builder, new Object[0]).invoke(customizer -> {
            customizer.customize(builder);
        });
    }

    @ConditionalOnMissingBean(name = {"pulsarListenerContainerFactory"})
    @Bean
    ConcurrentPulsarListenerContainerFactory<Object> pulsarListenerContainerFactory(PulsarConsumerFactory<Object> pulsarConsumerFactory, SchemaResolver schemaResolver, TopicResolver topicResolver, Environment environment) {
        PulsarContainerProperties containerProperties = new PulsarContainerProperties(new String[0]);
        containerProperties.setSchemaResolver(schemaResolver);
        containerProperties.setTopicResolver(topicResolver);
        if (Threading.VIRTUAL.isActive(environment)) {
            containerProperties.setConsumerTaskExecutor(new VirtualThreadTaskExecutor());
        }
        this.propertiesMapper.customizeContainerProperties(containerProperties);
        return new ConcurrentPulsarListenerContainerFactory<>(pulsarConsumerFactory, containerProperties);
    }

    @ConditionalOnMissingBean({PulsarReaderFactory.class})
    @Bean
    DefaultPulsarReaderFactory<?> pulsarReaderFactory(PulsarClient pulsarClient, ObjectProvider<ReaderBuilderCustomizer<?>> customizersProvider) {
        List<ReaderBuilderCustomizer<?>> customizers = new ArrayList<>();
        PulsarPropertiesMapper pulsarPropertiesMapper = this.propertiesMapper;
        Objects.requireNonNull(pulsarPropertiesMapper);
        customizers.add(pulsarPropertiesMapper::customizeReaderBuilder);
        customizers.addAll(customizersProvider.orderedStream().toList());
        List<ReaderBuilderCustomizer<Object>> lambdaSafeCustomizers = List.of(builder -> {
            applyReaderBuilderCustomizers(customizers, builder);
        });
        return new DefaultPulsarReaderFactory<>(pulsarClient, lambdaSafeCustomizers);
    }

    private void applyReaderBuilderCustomizers(List<ReaderBuilderCustomizer<?>> customizers, ReaderBuilder<?> builder) {
        LambdaSafe.callbacks(ReaderBuilderCustomizer.class, customizers, builder, new Object[0]).invoke(customizer -> {
            customizer.customize(builder);
        });
    }

    @ConditionalOnMissingBean(name = {"pulsarReaderContainerFactory"})
    @Bean
    DefaultPulsarReaderContainerFactory<?> pulsarReaderContainerFactory(PulsarReaderFactory<?> pulsarReaderFactory, SchemaResolver schemaResolver, Environment environment) {
        PulsarReaderContainerProperties readerContainerProperties = new PulsarReaderContainerProperties();
        readerContainerProperties.setSchemaResolver(schemaResolver);
        if (Threading.VIRTUAL.isActive(environment)) {
            readerContainerProperties.setReaderTaskExecutor(new VirtualThreadTaskExecutor());
        }
        this.propertiesMapper.customizeReaderContainerProperties(readerContainerProperties);
        return new DefaultPulsarReaderContainerFactory<>(pulsarReaderFactory, readerContainerProperties);
    }

    @ConditionalOnMissingBean(name = {"org.springframework.pulsar.config.internalPulsarListenerAnnotationProcessor", "org.springframework.pulsar.config.internalPulsarReaderAnnotationProcessor"})
    @Configuration(proxyBeanMethods = false)
    @EnablePulsar
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarAutoConfiguration$EnablePulsarConfiguration.class */
    static class EnablePulsarConfiguration {
        EnablePulsarConfiguration() {
        }
    }
}

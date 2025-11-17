package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.EnvironmentBuilder;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.config.StreamRabbitListenerContainerFactory;
import org.springframework.rabbit.stream.listener.ConsumerCustomizer;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamOperations;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({StreamRabbitListenerContainerFactory.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitStreamConfiguration.class */
class RabbitStreamConfiguration {
    RabbitStreamConfiguration() {
    }

    @ConditionalOnMissingBean(name = {"rabbitListenerContainerFactory"})
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = {"type"}, havingValue = "stream")
    @Bean(name = {"rabbitListenerContainerFactory"})
    StreamRabbitListenerContainerFactory streamRabbitListenerContainerFactory(Environment rabbitStreamEnvironment, RabbitProperties properties, ObjectProvider<ConsumerCustomizer> consumerCustomizer, ObjectProvider<ContainerCustomizer<StreamListenerContainer>> containerCustomizer) {
        StreamRabbitListenerContainerFactory factory = new StreamRabbitListenerContainerFactory(rabbitStreamEnvironment);
        factory.setNativeListener(properties.getListener().getStream().isNativeListener());
        Objects.requireNonNull(factory);
        consumerCustomizer.ifUnique(factory::setConsumerCustomizer);
        Objects.requireNonNull(factory);
        containerCustomizer.ifUnique(factory::setContainerCustomizer);
        return factory;
    }

    @ConditionalOnMissingBean(name = {"rabbitStreamEnvironment"})
    @Bean(name = {"rabbitStreamEnvironment"})
    Environment rabbitStreamEnvironment(RabbitProperties properties, ObjectProvider<EnvironmentBuilderCustomizer> customizers) {
        EnvironmentBuilder builder = configure(Environment.builder(), properties);
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    RabbitStreamTemplateConfigurer rabbitStreamTemplateConfigurer(RabbitProperties properties, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<StreamMessageConverter> streamMessageConverter, ObjectProvider<ProducerCustomizer> producerCustomizer) {
        RabbitStreamTemplateConfigurer configurer = new RabbitStreamTemplateConfigurer();
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setStreamMessageConverter(streamMessageConverter.getIfUnique());
        configurer.setProducerCustomizer(producerCustomizer.getIfUnique());
        return configurer;
    }

    @ConditionalOnMissingBean({RabbitStreamOperations.class})
    @ConditionalOnProperty(prefix = "spring.rabbitmq.stream", name = {"name"})
    @Bean
    RabbitStreamTemplate rabbitStreamTemplate(Environment rabbitStreamEnvironment, RabbitProperties properties, RabbitStreamTemplateConfigurer configurer) {
        RabbitStreamTemplate template = new RabbitStreamTemplate(rabbitStreamEnvironment, properties.getStream().getName());
        configurer.configure(template);
        return template;
    }

    static EnvironmentBuilder configure(EnvironmentBuilder builder, RabbitProperties properties) {
        builder.lazyInitialization(true);
        RabbitProperties.Stream stream = properties.getStream();
        PropertyMapper map = PropertyMapper.get();
        PropertyMapper.Source from = map.from((PropertyMapper) stream.getHost());
        Objects.requireNonNull(builder);
        from.to(builder::host);
        PropertyMapper.Source from2 = map.from((PropertyMapper) Integer.valueOf(stream.getPort()));
        Objects.requireNonNull(builder);
        from2.to((v1) -> {
            r1.port(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) stream.getVirtualHost());
        Objects.requireNonNull(properties);
        PropertyMapper.Source whenNonNull = from3.as(withFallback(properties::getVirtualHost)).whenNonNull();
        Objects.requireNonNull(builder);
        whenNonNull.to(builder::virtualHost);
        PropertyMapper.Source from4 = map.from((PropertyMapper) stream.getUsername());
        Objects.requireNonNull(properties);
        PropertyMapper.Source whenNonNull2 = from4.as(withFallback(properties::getUsername)).whenNonNull();
        Objects.requireNonNull(builder);
        whenNonNull2.to(builder::username);
        PropertyMapper.Source from5 = map.from((PropertyMapper) stream.getPassword());
        Objects.requireNonNull(properties);
        PropertyMapper.Source whenNonNull3 = from5.as(withFallback(properties::getPassword)).whenNonNull();
        Objects.requireNonNull(builder);
        whenNonNull3.to(builder::password);
        return builder;
    }

    private static Function<String, String> withFallback(Supplier<String> fallback) {
        return value -> {
            return value != null ? value : (String) fallback.get();
        };
    }
}

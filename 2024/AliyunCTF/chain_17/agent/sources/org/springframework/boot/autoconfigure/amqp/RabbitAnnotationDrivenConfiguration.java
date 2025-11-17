package org.springframework.boot.autoconfigure.amqp;

import java.util.Objects;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EnableRabbit.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitAnnotationDrivenConfiguration.class */
class RabbitAnnotationDrivenConfiguration {
    private final ObjectProvider<MessageConverter> messageConverter;
    private final ObjectProvider<MessageRecoverer> messageRecoverer;
    private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;
    private final RabbitProperties properties;

    RabbitAnnotationDrivenConfiguration(ObjectProvider<MessageConverter> messageConverter, ObjectProvider<MessageRecoverer> messageRecoverer, ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers, RabbitProperties properties) {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retryTemplateCustomizers = retryTemplateCustomizers;
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    @Bean
    SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer() {
        return simpleListenerConfigurer();
    }

    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.VIRTUAL)
    @Bean(name = {"simpleRabbitListenerContainerFactoryConfigurer"})
    SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurerVirtualThreads() {
        SimpleRabbitListenerContainerFactoryConfigurer configurer = simpleListenerConfigurer();
        configurer.setTaskExecutor(new VirtualThreadTaskExecutor());
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"rabbitListenerContainerFactory"})
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = {"type"}, havingValue = "simple", matchIfMissing = true)
    @Bean(name = {"rabbitListenerContainerFactory"})
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory, ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        Objects.requireNonNull(factory);
        simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
        return factory;
    }

    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    @Bean
    DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer() {
        return directListenerConfigurer();
    }

    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.VIRTUAL)
    @Bean(name = {"directRabbitListenerContainerFactoryConfigurer"})
    DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurerVirtualThreads() {
        DirectRabbitListenerContainerFactoryConfigurer configurer = directListenerConfigurer();
        configurer.setTaskExecutor(new VirtualThreadTaskExecutor());
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"rabbitListenerContainerFactory"})
    @ConditionalOnProperty(prefix = "spring.rabbitmq.listener", name = {"type"}, havingValue = "direct")
    @Bean(name = {"rabbitListenerContainerFactory"})
    DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(DirectRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory, ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer) {
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        Objects.requireNonNull(factory);
        directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
        return factory;
    }

    private SimpleRabbitListenerContainerFactoryConfigurer simpleListenerConfigurer() {
        SimpleRabbitListenerContainerFactoryConfigurer configurer = new SimpleRabbitListenerContainerFactoryConfigurer(this.properties);
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
        configurer.setRetryTemplateCustomizers(this.retryTemplateCustomizers.orderedStream().toList());
        return configurer;
    }

    private DirectRabbitListenerContainerFactoryConfigurer directListenerConfigurer() {
        DirectRabbitListenerContainerFactoryConfigurer configurer = new DirectRabbitListenerContainerFactoryConfigurer(this.properties);
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
        configurer.setRetryTemplateCustomizers(this.retryTemplateCustomizers.orderedStream().toList());
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"org.springframework.amqp.rabbit.config.internalRabbitListenerAnnotationProcessor"})
    @EnableRabbit
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitAnnotationDrivenConfiguration$EnableRabbitConfiguration.class */
    static class EnableRabbitConfiguration {
        EnableRabbitConfiguration() {
        }
    }
}

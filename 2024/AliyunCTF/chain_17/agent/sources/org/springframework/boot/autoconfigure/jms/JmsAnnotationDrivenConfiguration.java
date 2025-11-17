package org.springframework.boot.autoconfigure.jms;

import io.micrometer.observation.ObservationRegistry;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.ExceptionListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EnableJms.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration.class */
class JmsAnnotationDrivenConfiguration {
    private final ObjectProvider<DestinationResolver> destinationResolver;
    private final ObjectProvider<JtaTransactionManager> transactionManager;
    private final ObjectProvider<MessageConverter> messageConverter;
    private final ObjectProvider<ExceptionListener> exceptionListener;
    private final ObjectProvider<ObservationRegistry> observationRegistry;
    private final JmsProperties properties;

    JmsAnnotationDrivenConfiguration(ObjectProvider<DestinationResolver> destinationResolver, ObjectProvider<JtaTransactionManager> transactionManager, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<ExceptionListener> exceptionListener, ObjectProvider<ObservationRegistry> observationRegistry, JmsProperties properties) {
        this.destinationResolver = destinationResolver;
        this.transactionManager = transactionManager;
        this.messageConverter = messageConverter;
        this.exceptionListener = exceptionListener;
        this.observationRegistry = observationRegistry;
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer() {
        DefaultJmsListenerContainerFactoryConfigurer configurer = new DefaultJmsListenerContainerFactoryConfigurer();
        configurer.setDestinationResolver(this.destinationResolver.getIfUnique());
        configurer.setTransactionManager(this.transactionManager.getIfUnique());
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setExceptionListener(this.exceptionListener.getIfUnique());
        configurer.setObservationRegistry(this.observationRegistry.getIfUnique());
        configurer.setJmsProperties(this.properties);
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"jmsListenerContainerFactory"})
    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    DefaultJmsListenerContainerFactory jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @ConditionalOnMissingBean(name = {"org.springframework.jms.config.internalJmsListenerAnnotationProcessor"})
    @Configuration(proxyBeanMethods = false)
    @EnableJms
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration$EnableJmsConfiguration.class */
    static class EnableJmsConfiguration {
        EnableJmsConfiguration() {
        }
    }

    @ConditionalOnJndi
    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration$JndiConfiguration.class */
    static class JndiConfiguration {
        JndiConfiguration() {
        }

        @ConditionalOnMissingBean({DestinationResolver.class})
        @Bean
        JndiDestinationResolver destinationResolver() {
            JndiDestinationResolver resolver = new JndiDestinationResolver();
            resolver.setFallbackToDynamicDestination(true);
            return resolver;
        }
    }
}

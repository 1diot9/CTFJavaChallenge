package org.springframework.boot.autoconfigure.jms;

import io.micrometer.observation.ObservationRegistry;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.ExceptionListener;
import java.util.Objects;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/DefaultJmsListenerContainerFactoryConfigurer.class */
public final class DefaultJmsListenerContainerFactoryConfigurer {
    private DestinationResolver destinationResolver;
    private MessageConverter messageConverter;
    private ExceptionListener exceptionListener;
    private JtaTransactionManager transactionManager;
    private JmsProperties jmsProperties;
    private ObservationRegistry observationRegistry;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDestinationResolver(DestinationResolver destinationResolver) {
        this.destinationResolver = destinationResolver;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTransactionManager(JtaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setJmsProperties(JmsProperties jmsProperties) {
        this.jmsProperties = jmsProperties;
    }

    public void setObservationRegistry(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public void configure(DefaultJmsListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        Assert.notNull(factory, "Factory must not be null");
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(Boolean.valueOf(this.jmsProperties.isPubSubDomain()));
        JmsProperties.Listener listenerProperties = this.jmsProperties.getListener();
        JmsProperties.Listener.Session sessionProperties = listenerProperties.getSession();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.transactionManager);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setTransactionManager(v1);
        });
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.destinationResolver);
        Objects.requireNonNull(factory);
        from2.to(factory::setDestinationResolver);
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.messageConverter);
        Objects.requireNonNull(factory);
        from3.to(factory::setMessageConverter);
        PropertyMapper.Source from4 = map.from((PropertyMapper) this.exceptionListener);
        Objects.requireNonNull(factory);
        from4.to(factory::setExceptionListener);
        AcknowledgeMode acknowledgeMode = sessionProperties.getAcknowledgeMode();
        Objects.requireNonNull(acknowledgeMode);
        PropertyMapper.Source from5 = map.from(acknowledgeMode::getMode);
        Objects.requireNonNull(factory);
        from5.to(factory::setSessionAcknowledgeMode);
        if (this.transactionManager == null && sessionProperties.getTransacted() == null) {
            factory.setSessionTransacted(true);
        }
        PropertyMapper.Source from6 = map.from((PropertyMapper) this.observationRegistry);
        Objects.requireNonNull(factory);
        from6.to(factory::setObservationRegistry);
        Objects.requireNonNull(sessionProperties);
        PropertyMapper.Source from7 = map.from(sessionProperties::getTransacted);
        Objects.requireNonNull(factory);
        from7.to(factory::setSessionTransacted);
        Objects.requireNonNull(listenerProperties);
        PropertyMapper.Source from8 = map.from(listenerProperties::isAutoStartup);
        Objects.requireNonNull(factory);
        from8.to((v1) -> {
            r1.setAutoStartup(v1);
        });
        Objects.requireNonNull(listenerProperties);
        PropertyMapper.Source from9 = map.from(listenerProperties::formatConcurrency);
        Objects.requireNonNull(factory);
        from9.to(factory::setConcurrency);
        Objects.requireNonNull(listenerProperties);
        PropertyMapper.Source as = map.from(listenerProperties::getReceiveTimeout).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(factory);
        as.to(factory::setReceiveTimeout);
    }
}

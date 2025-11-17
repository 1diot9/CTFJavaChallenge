package org.springframework.boot.autoconfigure.amqp;

import java.util.Objects;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/SimpleRabbitListenerContainerFactoryConfigurer.class */
public final class SimpleRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<SimpleRabbitListenerContainerFactory> {
    public SimpleRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        super(rabbitProperties);
    }

    @Override // org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer
    public void configure(SimpleRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.SimpleContainer config = getRabbitProperties().getListener().getSimple();
        configure(factory, connectionFactory, config);
        Objects.requireNonNull(config);
        PropertyMapper.Source whenNonNull = map.from(config::getConcurrency).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull.to(factory::setConcurrentConsumers);
        Objects.requireNonNull(config);
        PropertyMapper.Source whenNonNull2 = map.from(config::getMaxConcurrency).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull2.to(factory::setMaxConcurrentConsumers);
        Objects.requireNonNull(config);
        PropertyMapper.Source whenNonNull3 = map.from(config::getBatchSize).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull3.to(factory::setBatchSize);
        Objects.requireNonNull(config);
        PropertyMapper.Source from = map.from(config::isConsumerBatchEnabled);
        Objects.requireNonNull(factory);
        from.to((v1) -> {
            r1.setConsumerBatchEnabled(v1);
        });
    }
}

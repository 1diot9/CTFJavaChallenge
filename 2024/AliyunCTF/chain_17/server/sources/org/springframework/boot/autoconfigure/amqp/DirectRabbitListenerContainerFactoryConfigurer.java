package org.springframework.boot.autoconfigure.amqp;

import java.util.Objects;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/DirectRabbitListenerContainerFactoryConfigurer.class */
public final class DirectRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<DirectRabbitListenerContainerFactory> {
    public DirectRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        super(rabbitProperties);
    }

    @Override // org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer
    public void configure(DirectRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.DirectContainer config = getRabbitProperties().getListener().getDirect();
        configure(factory, connectionFactory, config);
        Objects.requireNonNull(config);
        PropertyMapper.Source whenNonNull = map.from(config::getConsumersPerQueue).whenNonNull();
        Objects.requireNonNull(factory);
        whenNonNull.to(factory::setConsumersPerQueue);
    }
}

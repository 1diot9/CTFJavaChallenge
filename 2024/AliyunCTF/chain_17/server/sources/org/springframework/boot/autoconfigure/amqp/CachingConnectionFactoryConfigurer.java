package org.springframework.boot.autoconfigure.amqp;

import java.util.Objects;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/CachingConnectionFactoryConfigurer.class */
public class CachingConnectionFactoryConfigurer extends AbstractConnectionFactoryConfigurer<CachingConnectionFactory> {
    public CachingConnectionFactoryConfigurer(RabbitProperties properties) {
        this(properties, new PropertiesRabbitConnectionDetails(properties));
    }

    public CachingConnectionFactoryConfigurer(RabbitProperties properties, RabbitConnectionDetails connectionDetails) {
        super(properties, connectionDetails);
    }

    @Override // org.springframework.boot.autoconfigure.amqp.AbstractConnectionFactoryConfigurer
    public void configure(CachingConnectionFactory connectionFactory, RabbitProperties rabbitProperties) {
        PropertyMapper map = PropertyMapper.get();
        Objects.requireNonNull(rabbitProperties);
        PropertyMapper.Source from = map.from(rabbitProperties::isPublisherReturns);
        Objects.requireNonNull(connectionFactory);
        from.to((v1) -> {
            r1.setPublisherReturns(v1);
        });
        Objects.requireNonNull(rabbitProperties);
        PropertyMapper.Source whenNonNull = map.from(rabbitProperties::getPublisherConfirmType).whenNonNull();
        Objects.requireNonNull(connectionFactory);
        whenNonNull.to(connectionFactory::setPublisherConfirmType);
        RabbitProperties.Cache.Channel channel = rabbitProperties.getCache().getChannel();
        Objects.requireNonNull(channel);
        PropertyMapper.Source whenNonNull2 = map.from(channel::getSize).whenNonNull();
        Objects.requireNonNull(connectionFactory);
        whenNonNull2.to((v1) -> {
            r1.setChannelCacheSize(v1);
        });
        Objects.requireNonNull(channel);
        PropertyMapper.Source as = map.from(channel::getCheckoutTimeout).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(connectionFactory);
        as.to((v1) -> {
            r1.setChannelCheckoutTimeout(v1);
        });
        RabbitProperties.Cache.Connection connection = rabbitProperties.getCache().getConnection();
        Objects.requireNonNull(connection);
        PropertyMapper.Source whenNonNull3 = map.from(connection::getMode).whenNonNull();
        Objects.requireNonNull(connectionFactory);
        whenNonNull3.to(connectionFactory::setCacheMode);
        Objects.requireNonNull(connection);
        PropertyMapper.Source whenNonNull4 = map.from(connection::getSize).whenNonNull();
        Objects.requireNonNull(connectionFactory);
        whenNonNull4.to((v1) -> {
            r1.setConnectionCacheSize(v1);
        });
    }
}

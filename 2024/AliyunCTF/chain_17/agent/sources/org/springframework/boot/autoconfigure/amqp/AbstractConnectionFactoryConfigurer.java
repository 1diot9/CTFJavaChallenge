package org.springframework.boot.autoconfigure.amqp;

import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/AbstractConnectionFactoryConfigurer.class */
public abstract class AbstractConnectionFactoryConfigurer<T extends AbstractConnectionFactory> {
    private final RabbitProperties rabbitProperties;
    private ConnectionNameStrategy connectionNameStrategy;
    private final RabbitConnectionDetails connectionDetails;

    protected abstract void configure(T connectionFactory, RabbitProperties rabbitProperties);

    protected AbstractConnectionFactoryConfigurer(RabbitProperties properties) {
        this(properties, new PropertiesRabbitConnectionDetails(properties));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractConnectionFactoryConfigurer(RabbitProperties properties, RabbitConnectionDetails connectionDetails) {
        Assert.notNull(properties, "Properties must not be null");
        Assert.notNull(connectionDetails, "ConnectionDetails must not be null");
        this.rabbitProperties = properties;
        this.connectionDetails = connectionDetails;
    }

    protected final ConnectionNameStrategy getConnectionNameStrategy() {
        return this.connectionNameStrategy;
    }

    public final void setConnectionNameStrategy(ConnectionNameStrategy connectionNameStrategy) {
        this.connectionNameStrategy = connectionNameStrategy;
    }

    public final void configure(T connectionFactory) {
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        PropertyMapper map = PropertyMapper.get();
        String addresses = (String) this.connectionDetails.getAddresses().stream().map(address -> {
            return address.host() + ":" + address.port();
        }).collect(Collectors.joining(","));
        PropertyMapper.Source from = map.from((PropertyMapper) addresses);
        Objects.requireNonNull(connectionFactory);
        from.to(connectionFactory::setAddresses);
        RabbitProperties rabbitProperties = this.rabbitProperties;
        Objects.requireNonNull(rabbitProperties);
        PropertyMapper.Source whenNonNull = map.from(rabbitProperties::getAddressShuffleMode).whenNonNull();
        Objects.requireNonNull(connectionFactory);
        whenNonNull.to(connectionFactory::setAddressShuffleMode);
        PropertyMapper.Source whenNonNull2 = map.from((PropertyMapper) this.connectionNameStrategy).whenNonNull();
        Objects.requireNonNull(connectionFactory);
        whenNonNull2.to(connectionFactory::setConnectionNameStrategy);
        configure(connectionFactory, this.rabbitProperties);
    }
}

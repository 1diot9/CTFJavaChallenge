package org.springframework.boot.autoconfigure.jms;

import jakarta.jms.ConnectionFactory;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsPoolConnectionFactoryFactory.class */
public class JmsPoolConnectionFactoryFactory {
    private final JmsPoolConnectionFactoryProperties properties;

    public JmsPoolConnectionFactoryFactory(JmsPoolConnectionFactoryProperties properties) {
        this.properties = properties;
    }

    public JmsPoolConnectionFactory createPooledConnectionFactory(ConnectionFactory connectionFactory) {
        JmsPoolConnectionFactory pooledConnectionFactory = new JmsPoolConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(connectionFactory);
        pooledConnectionFactory.setBlockIfSessionPoolIsFull(this.properties.isBlockIfFull());
        if (this.properties.getBlockIfFullTimeout() != null) {
            pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(this.properties.getBlockIfFullTimeout().toMillis());
        }
        if (this.properties.getIdleTimeout() != null) {
            pooledConnectionFactory.setConnectionIdleTimeout((int) this.properties.getIdleTimeout().toMillis());
        }
        pooledConnectionFactory.setMaxConnections(this.properties.getMaxConnections());
        pooledConnectionFactory.setMaxSessionsPerConnection(this.properties.getMaxSessionsPerConnection());
        if (this.properties.getTimeBetweenExpirationCheck() != null) {
            pooledConnectionFactory.setConnectionCheckInterval(this.properties.getTimeBetweenExpirationCheck().toMillis());
        }
        pooledConnectionFactory.setUseAnonymousProducers(this.properties.isUseAnonymousProducers());
        return pooledConnectionFactory;
    }
}

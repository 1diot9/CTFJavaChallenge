package org.springframework.boot.autoconfigure.jms.activemq;

import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({TransactionManager.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@ConditionalOnBean({XAConnectionFactoryWrapper.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQXAConnectionFactoryConfiguration.class */
class ActiveMQXAConnectionFactoryConfiguration {
    ActiveMQXAConnectionFactoryConfiguration() {
    }

    @Primary
    @Bean(name = {"jmsConnectionFactory", "xaJmsConnectionFactory"})
    ConnectionFactory jmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers, XAConnectionFactoryWrapper wrapper, ActiveMQConnectionDetails connectionDetails) throws Exception {
        ActiveMQXAConnectionFactory connectionFactory = new ActiveMQConnectionFactoryFactory(properties, factoryCustomizers.orderedStream().toList(), connectionDetails).createConnectionFactory(ActiveMQXAConnectionFactory.class);
        return wrapper.wrapConnectionFactory(connectionFactory);
    }

    @ConditionalOnProperty(prefix = "spring.activemq.pool", name = {"enabled"}, havingValue = "false", matchIfMissing = true)
    @Bean
    ActiveMQConnectionFactory nonXaJmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers, ActiveMQConnectionDetails connectionDetails) {
        return new ActiveMQConnectionFactoryFactory(properties, factoryCustomizers.orderedStream().toList(), connectionDetails).createConnectionFactory(ActiveMQConnectionFactory.class);
    }
}

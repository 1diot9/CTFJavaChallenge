package org.springframework.boot.autoconfigure.jms.artemis;

import jakarta.jms.ConnectionFactory;
import jakarta.transaction.TransactionManager;
import org.apache.activemq.artemis.jms.client.ActiveMQXAConnectionFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jms.XAConnectionFactoryWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({TransactionManager.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@ConditionalOnBean({XAConnectionFactoryWrapper.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisXAConnectionFactoryConfiguration.class */
class ArtemisXAConnectionFactoryConfiguration {
    ArtemisXAConnectionFactoryConfiguration() {
    }

    @Primary
    @Bean(name = {"jmsConnectionFactory", "xaJmsConnectionFactory"})
    ConnectionFactory jmsConnectionFactory(ListableBeanFactory beanFactory, ArtemisProperties properties, XAConnectionFactoryWrapper wrapper) throws Exception {
        return wrapper.wrapConnectionFactory(new ArtemisConnectionFactoryFactory(beanFactory, properties).createConnectionFactory(ActiveMQXAConnectionFactory.class));
    }

    @Bean
    ActiveMQXAConnectionFactory nonXaJmsConnectionFactory(ListableBeanFactory beanFactory, ArtemisProperties properties) {
        return new ArtemisConnectionFactoryFactory(beanFactory, properties).createConnectionFactory(ActiveMQXAConnectionFactory.class);
    }
}

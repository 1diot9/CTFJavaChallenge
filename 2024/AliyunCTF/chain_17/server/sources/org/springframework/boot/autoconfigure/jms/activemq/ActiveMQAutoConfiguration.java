package org.springframework.boot.autoconfigure.jms.activemq;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({ActiveMQProperties.class, JmsProperties.class})
@AutoConfiguration(before = {JmsAutoConfiguration.class}, after = {JndiConnectionFactoryAutoConfiguration.class})
@ConditionalOnClass({ConnectionFactory.class, ActiveMQConnectionFactory.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@Import({ActiveMQXAConnectionFactoryConfiguration.class, ActiveMQConnectionFactoryConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQAutoConfiguration.class */
public class ActiveMQAutoConfiguration {
    @ConditionalOnMissingBean({ActiveMQConnectionDetails.class})
    @Bean
    ActiveMQConnectionDetails activemqConnectionDetails(ActiveMQProperties properties) {
        return new PropertiesActiveMQConnectionDetails(properties);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQAutoConfiguration$PropertiesActiveMQConnectionDetails.class */
    static class PropertiesActiveMQConnectionDetails implements ActiveMQConnectionDetails {
        private final ActiveMQProperties properties;

        PropertiesActiveMQConnectionDetails(ActiveMQProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionDetails
        public String getBrokerUrl() {
            return this.properties.determineBrokerUrl();
        }

        @Override // org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionDetails
        public String getUser() {
            return this.properties.getUser();
        }

        @Override // org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionDetails
        public String getPassword() {
            return this.properties.getPassword();
        }
    }
}

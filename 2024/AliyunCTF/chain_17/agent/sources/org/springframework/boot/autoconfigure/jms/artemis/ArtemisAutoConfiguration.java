package org.springframework.boot.autoconfigure.jms.artemis;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({ArtemisProperties.class, JmsProperties.class})
@AutoConfiguration(before = {JmsAutoConfiguration.class}, after = {JndiConnectionFactoryAutoConfiguration.class})
@ConditionalOnClass({ConnectionFactory.class, ActiveMQConnectionFactory.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@Import({ArtemisEmbeddedServerConfiguration.class, ArtemisXAConnectionFactoryConfiguration.class, ArtemisConnectionFactoryConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisAutoConfiguration.class */
public class ArtemisAutoConfiguration {
}

package org.springframework.boot.autoconfigure.jms.artemis;

import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.core.config.CoreAddressConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.TopicConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.TopicConfigurationImpl;
import org.apache.catalina.Lifecycle;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({EmbeddedActiveMQ.class})
@ConditionalOnProperty(prefix = "spring.artemis.embedded", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisEmbeddedServerConfiguration.class */
class ArtemisEmbeddedServerConfiguration {
    private final ArtemisProperties properties;

    ArtemisEmbeddedServerConfiguration(ArtemisProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    org.apache.activemq.artemis.core.config.Configuration artemisConfiguration() {
        return new ArtemisEmbeddedConfigurationFactory(this.properties).createConfiguration();
    }

    @ConditionalOnMissingBean
    @Bean(initMethod = Lifecycle.START_EVENT, destroyMethod = Lifecycle.STOP_EVENT)
    EmbeddedActiveMQ embeddedActiveMq(org.apache.activemq.artemis.core.config.Configuration configuration, JMSConfiguration jmsConfiguration, ObjectProvider<ArtemisConfigurationCustomizer> configurationCustomizers) {
        for (JMSQueueConfiguration queueConfiguration : jmsConfiguration.getQueueConfigurations()) {
            String queueName = queueConfiguration.getName();
            configuration.addAddressConfiguration(new CoreAddressConfiguration().setName(queueName).addRoutingType(RoutingType.ANYCAST).addQueueConfiguration(new QueueConfiguration(queueName).setAddress(queueName).setFilterString(queueConfiguration.getSelector()).setDurable(Boolean.valueOf(queueConfiguration.isDurable())).setRoutingType(RoutingType.ANYCAST)));
        }
        for (TopicConfiguration topicConfiguration : jmsConfiguration.getTopicConfigurations()) {
            configuration.addAddressConfiguration(new CoreAddressConfiguration().setName(topicConfiguration.getName()).addRoutingType(RoutingType.MULTICAST));
        }
        configurationCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(configuration);
        });
        EmbeddedActiveMQ embeddedActiveMq = new EmbeddedActiveMQ();
        embeddedActiveMq.setConfiguration(configuration);
        return embeddedActiveMq;
    }

    @ConditionalOnMissingBean
    @Bean
    JMSConfiguration artemisJmsConfiguration(ObjectProvider<JMSQueueConfiguration> queuesConfiguration, ObjectProvider<TopicConfiguration> topicsConfiguration) {
        JMSConfigurationImpl jMSConfigurationImpl = new JMSConfigurationImpl();
        jMSConfigurationImpl.getQueueConfigurations().addAll(queuesConfiguration.orderedStream().toList());
        jMSConfigurationImpl.getTopicConfigurations().addAll(topicsConfiguration.orderedStream().toList());
        addQueues(jMSConfigurationImpl, this.properties.getEmbedded().getQueues());
        addTopics(jMSConfigurationImpl, this.properties.getEmbedded().getTopics());
        return jMSConfigurationImpl;
    }

    private void addQueues(JMSConfiguration configuration, String[] queues) {
        boolean persistent = this.properties.getEmbedded().isPersistent();
        for (String queue : queues) {
            JMSQueueConfigurationImpl jmsQueueConfiguration = new JMSQueueConfigurationImpl();
            jmsQueueConfiguration.setName(queue);
            jmsQueueConfiguration.setDurable(persistent);
            jmsQueueConfiguration.setBindings(new String[]{"/queue/" + queue});
            configuration.getQueueConfigurations().add(jmsQueueConfiguration);
        }
    }

    private void addTopics(JMSConfiguration configuration, String[] topics) {
        for (String topic : topics) {
            TopicConfigurationImpl topicConfiguration = new TopicConfigurationImpl();
            topicConfiguration.setName(topic);
            topicConfiguration.setBindings(new String[]{"/topic/" + topic});
            configuration.getTopicConfigurations().add(topicConfiguration);
        }
    }
}

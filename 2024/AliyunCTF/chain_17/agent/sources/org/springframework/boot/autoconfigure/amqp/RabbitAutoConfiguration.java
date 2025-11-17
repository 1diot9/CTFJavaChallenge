package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

@EnableConfigurationProperties({RabbitProperties.class})
@AutoConfiguration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@Import({RabbitAnnotationDrivenConfiguration.class, RabbitStreamConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration.class */
public class RabbitAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$RabbitConnectionFactoryCreator.class */
    protected static class RabbitConnectionFactoryCreator {
        private final RabbitProperties properties;

        protected RabbitConnectionFactoryCreator(RabbitProperties properties) {
            this.properties = properties;
        }

        @ConditionalOnMissingBean({RabbitConnectionDetails.class})
        @Bean
        RabbitConnectionDetails rabbitConnectionDetails() {
            return new PropertiesRabbitConnectionDetails(this.properties);
        }

        @ConditionalOnMissingBean
        @Bean
        RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(ResourceLoader resourceLoader, RabbitConnectionDetails connectionDetails, ObjectProvider<CredentialsProvider> credentialsProvider, ObjectProvider<CredentialsRefreshService> credentialsRefreshService, ObjectProvider<SslBundles> sslBundles) {
            RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader, this.properties, connectionDetails, sslBundles.getIfAvailable());
            configurer.setCredentialsProvider(credentialsProvider.getIfUnique());
            configurer.setCredentialsRefreshService(credentialsRefreshService.getIfUnique());
            return configurer;
        }

        @ConditionalOnMissingBean
        @Bean
        CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(RabbitConnectionDetails connectionDetails, ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) {
            CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(this.properties, connectionDetails);
            configurer.setConnectionNameStrategy(connectionNameStrategy.getIfUnique());
            return configurer;
        }

        @ConditionalOnMissingBean({ConnectionFactory.class})
        @Bean
        CachingConnectionFactory rabbitConnectionFactory(RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer, CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer, ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception {
            RabbitConnectionFactoryBean connectionFactoryBean = new SslBundleRabbitConnectionFactoryBean();
            rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
            connectionFactoryBean.afterPropertiesSet();
            com.rabbitmq.client.ConnectionFactory connectionFactory = (com.rabbitmq.client.ConnectionFactory) connectionFactoryBean.getObject();
            connectionFactoryCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(connectionFactory);
            });
            CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
            rabbitCachingConnectionFactoryConfigurer.configure(factory);
            return factory;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Import({RabbitConnectionFactoryCreator.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$RabbitTemplateConfiguration.class */
    protected static class RabbitTemplateConfiguration {
        protected RabbitTemplateConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        public RabbitTemplateConfigurer rabbitTemplateConfigurer(RabbitProperties properties, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
            RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
            configurer.setMessageConverter(messageConverter.getIfUnique());
            configurer.setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().toList());
            return configurer;
        }

        @ConditionalOnMissingBean({RabbitOperations.class})
        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory, ObjectProvider<RabbitTemplateCustomizer> customizers) {
            RabbitTemplate template = new RabbitTemplate();
            configurer.configure(template, connectionFactory);
            customizers.orderedStream().forEach(customizer -> {
                customizer.customize(template);
            });
            return template;
        }

        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "spring.rabbitmq", name = {"dynamic"}, matchIfMissing = true)
        @Bean
        public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({RabbitMessagingTemplate.class})
    @ConditionalOnMissingBean({RabbitMessagingTemplate.class})
    @Import({RabbitTemplateConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$MessagingTemplateConfiguration.class */
    protected static class MessagingTemplateConfiguration {
        protected MessagingTemplateConfiguration() {
        }

        @Bean
        @ConditionalOnSingleCandidate(RabbitTemplate.class)
        public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
            return new RabbitMessagingTemplate(rabbitTemplate);
        }
    }
}

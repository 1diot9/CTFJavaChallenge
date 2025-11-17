package org.springframework.boot.autoconfigure.jms;

import io.micrometer.observation.ObservationRegistry;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Message;
import java.util.List;
import java.util.Objects;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.jms.core.JmsMessageOperations;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;

@EnableConfigurationProperties({JmsProperties.class})
@AutoConfiguration
@ConditionalOnClass({Message.class, JmsTemplate.class})
@ImportRuntimeHints({JmsRuntimeHints.class})
@ConditionalOnBean({ConnectionFactory.class})
@Import({JmsAnnotationDrivenConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration.class */
public class JmsAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$JmsTemplateConfiguration.class */
    protected static class JmsTemplateConfiguration {
        private final JmsProperties properties;
        private final ObjectProvider<DestinationResolver> destinationResolver;
        private final ObjectProvider<MessageConverter> messageConverter;
        private final ObjectProvider<ObservationRegistry> observationRegistry;

        public JmsTemplateConfiguration(JmsProperties properties, ObjectProvider<DestinationResolver> destinationResolver, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<ObservationRegistry> observationRegistry) {
            this.properties = properties;
            this.destinationResolver = destinationResolver;
            this.messageConverter = messageConverter;
            this.observationRegistry = observationRegistry;
        }

        @ConditionalOnMissingBean({JmsOperations.class})
        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
            PropertyMapper map = PropertyMapper.get();
            JmsTemplate template = new JmsTemplate(connectionFactory);
            template.setPubSubDomain(this.properties.isPubSubDomain());
            ObjectProvider<DestinationResolver> objectProvider = this.destinationResolver;
            Objects.requireNonNull(objectProvider);
            PropertyMapper.Source whenNonNull = map.from(objectProvider::getIfUnique).whenNonNull();
            Objects.requireNonNull(template);
            whenNonNull.to(template::setDestinationResolver);
            ObjectProvider<MessageConverter> objectProvider2 = this.messageConverter;
            Objects.requireNonNull(objectProvider2);
            PropertyMapper.Source whenNonNull2 = map.from(objectProvider2::getIfUnique).whenNonNull();
            Objects.requireNonNull(template);
            whenNonNull2.to(template::setMessageConverter);
            ObjectProvider<ObservationRegistry> objectProvider3 = this.observationRegistry;
            Objects.requireNonNull(objectProvider3);
            PropertyMapper.Source whenNonNull3 = map.from(objectProvider3::getIfUnique).whenNonNull();
            Objects.requireNonNull(template);
            whenNonNull3.to(template::setObservationRegistry);
            mapTemplateProperties(this.properties.getTemplate(), template);
            return template;
        }

        private void mapTemplateProperties(JmsProperties.Template properties, JmsTemplate template) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            AcknowledgeMode acknowledgeMode = properties.getSession().getAcknowledgeMode();
            Objects.requireNonNull(acknowledgeMode);
            PropertyMapper.Source from = map.from(acknowledgeMode::getMode);
            Objects.requireNonNull(template);
            from.to((v1) -> {
                r1.setSessionAcknowledgeMode(v1);
            });
            JmsProperties.Template.Session session = properties.getSession();
            Objects.requireNonNull(session);
            PropertyMapper.Source from2 = map.from(session::isTransacted);
            Objects.requireNonNull(template);
            from2.to((v1) -> {
                r1.setSessionTransacted(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source whenNonNull = map.from(properties::getDefaultDestination).whenNonNull();
            Objects.requireNonNull(template);
            whenNonNull.to(template::setDefaultDestinationName);
            Objects.requireNonNull(properties);
            PropertyMapper.Source as = map.from(properties::getDeliveryDelay).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(template);
            as.to((v1) -> {
                r1.setDeliveryDelay(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source from3 = map.from(properties::determineQosEnabled);
            Objects.requireNonNull(template);
            from3.to((v1) -> {
                r1.setExplicitQosEnabled(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source as2 = map.from(properties::getDeliveryMode).as((v0) -> {
                return v0.getValue();
            });
            Objects.requireNonNull(template);
            as2.to((v1) -> {
                r1.setDeliveryMode(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source whenNonNull2 = map.from(properties::getPriority).whenNonNull();
            Objects.requireNonNull(template);
            whenNonNull2.to((v1) -> {
                r1.setPriority(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source as3 = map.from(properties::getTimeToLive).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(template);
            as3.to((v1) -> {
                r1.setTimeToLive(v1);
            });
            Objects.requireNonNull(properties);
            PropertyMapper.Source as4 = map.from(properties::getReceiveTimeout).as((v0) -> {
                return v0.toMillis();
            });
            Objects.requireNonNull(template);
            as4.to((v1) -> {
                r1.setReceiveTimeout(v1);
            });
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JmsMessagingTemplate.class})
    @Import({JmsTemplateConfiguration.class})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$MessagingTemplateConfiguration.class */
    protected static class MessagingTemplateConfiguration {
        protected MessagingTemplateConfiguration() {
        }

        @ConditionalOnMissingBean({JmsMessageOperations.class})
        @Bean
        @ConditionalOnSingleCandidate(JmsTemplate.class)
        public JmsMessagingTemplate jmsMessagingTemplate(JmsProperties properties, JmsTemplate jmsTemplate) {
            JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate(jmsTemplate);
            mapTemplateProperties(properties.getTemplate(), messagingTemplate);
            return messagingTemplate;
        }

        private void mapTemplateProperties(JmsProperties.Template properties, JmsMessagingTemplate messagingTemplate) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(properties);
            PropertyMapper.Source from = map.from(properties::getDefaultDestination);
            Objects.requireNonNull(messagingTemplate);
            from.to(messagingTemplate::setDefaultDestinationName);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$JmsRuntimeHints.class */
    static class JmsRuntimeHints implements RuntimeHintsRegistrar {
        JmsRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(TypeReference.of((Class<?>) AcknowledgeMode.class), type -> {
                type.withMethod("of", List.of(TypeReference.of((Class<?>) String.class)), ExecutableMode.INVOKE);
            });
        }
    }
}

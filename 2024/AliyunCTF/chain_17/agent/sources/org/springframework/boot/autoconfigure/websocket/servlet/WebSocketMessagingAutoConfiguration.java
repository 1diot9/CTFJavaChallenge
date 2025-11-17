package org.springframework.boot.autoconfigure.websocket.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.AbstractMessageBrokerConfiguration;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@AutoConfiguration(after = {JacksonAutoConfiguration.class})
@ConditionalOnClass({WebSocketMessageBrokerConfigurer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketMessagingAutoConfiguration.class */
public class WebSocketMessagingAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean({DelegatingWebSocketMessageBrokerConfiguration.class, ObjectMapper.class})
    @ConditionalOnClass({ObjectMapper.class, AbstractMessageBrokerConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/websocket/servlet/WebSocketMessagingAutoConfiguration$WebSocketMessageConverterConfiguration.class */
    static class WebSocketMessageConverterConfiguration implements WebSocketMessageBrokerConfigurer {
        private final ObjectMapper objectMapper;

        WebSocketMessageConverterConfiguration(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
            MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter(this.objectMapper);
            DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
            resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
            converter.setContentTypeResolver(resolver);
            messageConverters.add(new StringMessageConverter());
            messageConverters.add(new ByteArrayMessageConverter());
            messageConverters.add(converter);
            return false;
        }

        @Bean
        static LazyInitializationExcludeFilter eagerStompWebSocketHandlerMapping() {
            return (name, definition, type) -> {
                return name.equals("stompWebSocketHandlerMapping");
            };
        }
    }
}

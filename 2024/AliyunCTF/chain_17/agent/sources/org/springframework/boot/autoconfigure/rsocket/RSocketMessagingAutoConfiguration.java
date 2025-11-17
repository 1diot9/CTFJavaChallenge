package org.springframework.boot.autoconfigure.rsocket;

import io.rsocket.RSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

@AutoConfiguration(after = {RSocketStrategiesAutoConfiguration.class})
@ConditionalOnClass({RSocketRequester.class, RSocket.class, TcpServerTransport.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketMessagingAutoConfiguration.class */
public class RSocketMessagingAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies rSocketStrategies, ObjectProvider<RSocketMessageHandlerCustomizer> customizers) {
        RSocketMessageHandler messageHandler = new RSocketMessageHandler();
        messageHandler.setRSocketStrategies(rSocketStrategies);
        customizers.orderedStream().forEach(customizer -> {
            customizer.customize(messageHandler);
        });
        return messageHandler;
    }
}

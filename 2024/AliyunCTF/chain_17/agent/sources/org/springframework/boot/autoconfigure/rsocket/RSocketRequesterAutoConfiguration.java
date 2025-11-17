package org.springframework.boot.autoconfigure.rsocket;

import io.rsocket.RSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.netty.http.server.HttpServer;

@AutoConfiguration(after = {RSocketStrategiesAutoConfiguration.class})
@ConditionalOnClass({RSocketRequester.class, RSocket.class, HttpServer.class, TcpServerTransport.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketRequesterAutoConfiguration.class */
public class RSocketRequesterAutoConfiguration {
    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    public RSocketRequester.Builder rSocketRequesterBuilder(RSocketStrategies strategies, ObjectProvider<RSocketConnectorConfigurer> connectorConfigurers) {
        RSocketRequester.Builder builder = RSocketRequester.builder().rsocketStrategies(strategies);
        Stream<RSocketConnectorConfigurer> orderedStream = connectorConfigurers.orderedStream();
        Objects.requireNonNull(builder);
        orderedStream.forEach(builder::rsocketConnector);
        return builder;
    }
}

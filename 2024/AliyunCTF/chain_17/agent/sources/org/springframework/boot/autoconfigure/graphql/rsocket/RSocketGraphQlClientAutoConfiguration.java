package org.springframework.boot.autoconfigure.graphql.rsocket;

import graphql.GraphQL;
import io.rsocket.RSocket;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.graphql.client.RSocketGraphQlClient;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;

@AutoConfiguration(after = {RSocketRequesterAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, RSocketGraphQlClient.class, RSocketRequester.class, RSocket.class, TcpClientTransport.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/rsocket/RSocketGraphQlClientAutoConfiguration.class */
public class RSocketGraphQlClientAutoConfiguration {
    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    public RSocketGraphQlClient.Builder<?> rsocketGraphQlClientBuilder(RSocketRequester.Builder rsocketRequesterBuilder) {
        return RSocketGraphQlClient.builder(rsocketRequesterBuilder.dataMimeType(MimeTypeUtils.APPLICATION_JSON));
    }
}

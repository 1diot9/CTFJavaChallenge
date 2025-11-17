package org.springframework.boot.autoconfigure.graphql.rsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import io.rsocket.core.RSocketServer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.server.GraphQlRSocketHandler;
import org.springframework.graphql.server.RSocketGraphQlInterceptor;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import reactor.netty.http.server.HttpServer;

@AutoConfiguration(after = {GraphQlAutoConfiguration.class, RSocketMessagingAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, GraphQlSource.class, RSocketServer.class, HttpServer.class})
@ConditionalOnBean({RSocketMessageHandler.class, AnnotatedControllerConfigurer.class})
@ConditionalOnProperty(prefix = "spring.graphql.rsocket", name = {"mapping"})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/rsocket/GraphQlRSocketAutoConfiguration.class */
public class GraphQlRSocketAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public GraphQlRSocketHandler graphQlRSocketHandler(ExecutionGraphQlService graphQlService, ObjectProvider<RSocketGraphQlInterceptor> interceptors, ObjectMapper objectMapper) {
        return new GraphQlRSocketHandler(graphQlService, interceptors.orderedStream().toList(), new Jackson2JsonEncoder(objectMapper, new MimeType[0]));
    }

    @ConditionalOnMissingBean
    @Bean
    public GraphQlRSocketController graphQlRSocketController(GraphQlRSocketHandler handler) {
        return new GraphQlRSocketController(handler);
    }
}

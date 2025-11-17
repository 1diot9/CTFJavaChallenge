package org.springframework.boot.autoconfigure.graphql.reactive;

import graphql.GraphQL;
import java.util.Collections;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.graphql.GraphQlCorsProperties;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.annotation.Order;
import org.springframework.core.log.LogMessage;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.webflux.GraphQlHttpHandler;
import org.springframework.graphql.server.webflux.GraphQlWebSocketHandler;
import org.springframework.graphql.server.webflux.GraphiQlHandler;
import org.springframework.graphql.server.webflux.SchemaHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketUpgradeHandlerPredicate;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({GraphQlCorsProperties.class})
@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, GraphQlHttpHandler.class})
@ImportRuntimeHints({GraphiQlResourceHints.class})
@ConditionalOnBean({ExecutionGraphQlService.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/reactive/GraphQlWebFluxAutoConfiguration.class */
public class GraphQlWebFluxAutoConfiguration {
    private static final RequestPredicate SUPPORTS_MEDIATYPES = RequestPredicates.accept(new MediaType[]{MediaType.APPLICATION_GRAPHQL_RESPONSE, MediaType.APPLICATION_JSON, MediaType.APPLICATION_GRAPHQL}).and(RequestPredicates.contentType(new MediaType[]{MediaType.APPLICATION_JSON}));
    private static final Log logger = LogFactory.getLog((Class<?>) GraphQlWebFluxAutoConfiguration.class);

    @ConditionalOnMissingBean
    @Bean
    public GraphQlHttpHandler graphQlHttpHandler(WebGraphQlHandler webGraphQlHandler) {
        return new GraphQlHttpHandler(webGraphQlHandler);
    }

    @ConditionalOnMissingBean
    @Bean
    public WebGraphQlHandler webGraphQlHandler(ExecutionGraphQlService service, ObjectProvider<WebGraphQlInterceptor> interceptors) {
        return WebGraphQlHandler.builder(service).interceptors(interceptors.orderedStream().toList()).build();
    }

    @Bean
    @Order(0)
    public RouterFunction<ServerResponse> graphQlRouterFunction(GraphQlHttpHandler httpHandler, GraphQlSource graphQlSource, GraphQlProperties properties) {
        String path = properties.getPath();
        logger.info(LogMessage.format("GraphQL endpoint HTTP POST %s", path));
        RouterFunctions.Builder builder = RouterFunctions.route();
        RouterFunctions.Builder builder2 = builder.GET(path, this::onlyAllowPost);
        RequestPredicate requestPredicate = SUPPORTS_MEDIATYPES;
        Objects.requireNonNull(httpHandler);
        RouterFunctions.Builder builder3 = builder2.POST(path, requestPredicate, httpHandler::handleRequest);
        if (properties.getGraphiql().isEnabled()) {
            GraphiQlHandler graphQlHandler = new GraphiQlHandler(path, properties.getWebsocket().getPath());
            String path2 = properties.getGraphiql().getPath();
            Objects.requireNonNull(graphQlHandler);
            builder3 = builder3.GET(path2, graphQlHandler::handleRequest);
        }
        if (properties.getSchema().getPrinter().isEnabled()) {
            SchemaHandler schemaHandler = new SchemaHandler(graphQlSource);
            Objects.requireNonNull(schemaHandler);
            builder3 = builder3.GET(path + "/schema", schemaHandler::handleRequest);
        }
        return builder3.build();
    }

    private Mono<ServerResponse> onlyAllowPost(ServerRequest request) {
        return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).headers(this::onlyAllowPost).build();
    }

    private void onlyAllowPost(HttpHeaders headers) {
        headers.setAllow(Collections.singleton(HttpMethod.POST));
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/reactive/GraphQlWebFluxAutoConfiguration$GraphQlEndpointCorsConfiguration.class */
    public static class GraphQlEndpointCorsConfiguration implements WebFluxConfigurer {
        final GraphQlProperties graphQlProperties;
        final GraphQlCorsProperties corsProperties;

        public GraphQlEndpointCorsConfiguration(GraphQlProperties graphQlProps, GraphQlCorsProperties corsProps) {
            this.graphQlProperties = graphQlProps;
            this.corsProperties = corsProps;
        }

        public void addCorsMappings(CorsRegistry registry) {
            CorsConfiguration configuration = this.corsProperties.toCorsConfiguration();
            if (configuration != null) {
                registry.addMapping(this.graphQlProperties.getPath()).combine(configuration);
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "spring.graphql.websocket", name = {"path"})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/reactive/GraphQlWebFluxAutoConfiguration$WebSocketConfiguration.class */
    public static class WebSocketConfiguration {
        @ConditionalOnMissingBean
        @Bean
        public GraphQlWebSocketHandler graphQlWebSocketHandler(WebGraphQlHandler webGraphQlHandler, GraphQlProperties properties, ServerCodecConfigurer configurer) {
            return new GraphQlWebSocketHandler(webGraphQlHandler, configurer, properties.getWebsocket().getConnectionInitTimeout());
        }

        @Bean
        public HandlerMapping graphQlWebSocketEndpoint(GraphQlWebSocketHandler graphQlWebSocketHandler, GraphQlProperties properties) {
            String path = properties.getWebsocket().getPath();
            GraphQlWebFluxAutoConfiguration.logger.info(LogMessage.format("GraphQL endpoint WebSocket %s", path));
            SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
            mapping.setHandlerPredicate(new WebSocketUpgradeHandlerPredicate());
            mapping.setUrlMap(Collections.singletonMap(path, graphQlWebSocketHandler));
            mapping.setOrder(-2);
            return mapping;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/reactive/GraphQlWebFluxAutoConfiguration$GraphiQlResourceHints.class */
    static class GraphiQlResourceHints implements RuntimeHintsRegistrar {
        GraphiQlResourceHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("graphiql/index.html");
        }
    }
}

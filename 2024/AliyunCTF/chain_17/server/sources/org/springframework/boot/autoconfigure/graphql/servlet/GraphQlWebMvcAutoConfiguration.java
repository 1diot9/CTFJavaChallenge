package org.springframework.boot.autoconfigure.graphql.servlet;

import graphql.GraphQL;
import jakarta.websocket.server.ServerContainer;
import java.util.Collections;
import java.util.Map;
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
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
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
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.graphql.server.webmvc.GraphQlWebSocketHandler;
import org.springframework.graphql.server.webmvc.GraphiQlHandler;
import org.springframework.graphql.server.webmvc.SchemaHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;

@EnableConfigurationProperties({GraphQlCorsProperties.class})
@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, GraphQlHttpHandler.class})
@ImportRuntimeHints({GraphiQlResourceHints.class})
@ConditionalOnBean({ExecutionGraphQlService.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/servlet/GraphQlWebMvcAutoConfiguration.class */
public class GraphQlWebMvcAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) GraphQlWebMvcAutoConfiguration.class);
    private static final MediaType[] SUPPORTED_MEDIA_TYPES = {MediaType.APPLICATION_GRAPHQL_RESPONSE, MediaType.APPLICATION_JSON, MediaType.APPLICATION_GRAPHQL};

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
        RequestPredicate and = RequestPredicates.contentType(MediaType.APPLICATION_JSON).and(RequestPredicates.accept(SUPPORTED_MEDIA_TYPES));
        Objects.requireNonNull(httpHandler);
        RouterFunctions.Builder builder3 = builder2.POST(path, and, httpHandler::handleRequest);
        if (properties.getGraphiql().isEnabled()) {
            GraphiQlHandler graphiQLHandler = new GraphiQlHandler(path, properties.getWebsocket().getPath());
            String path2 = properties.getGraphiql().getPath();
            Objects.requireNonNull(graphiQLHandler);
            builder3 = builder3.GET(path2, graphiQLHandler::handleRequest);
        }
        if (properties.getSchema().getPrinter().isEnabled()) {
            SchemaHandler schemaHandler = new SchemaHandler(graphQlSource);
            Objects.requireNonNull(schemaHandler);
            builder3 = builder3.GET(path + "/schema", schemaHandler::handleRequest);
        }
        return builder3.build();
    }

    private ServerResponse onlyAllowPost(ServerRequest request) {
        return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).headers(this::onlyAllowPost).build();
    }

    private void onlyAllowPost(HttpHeaders headers) {
        headers.setAllow(Collections.singleton(HttpMethod.POST));
    }

    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/servlet/GraphQlWebMvcAutoConfiguration$GraphQlEndpointCorsConfiguration.class */
    public static class GraphQlEndpointCorsConfiguration implements WebMvcConfigurer {
        final GraphQlProperties graphQlProperties;
        final GraphQlCorsProperties corsProperties;

        public GraphQlEndpointCorsConfiguration(GraphQlProperties graphQlProps, GraphQlCorsProperties corsProps) {
            this.graphQlProperties = graphQlProps;
            this.corsProperties = corsProps;
        }

        @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
        public void addCorsMappings(CorsRegistry registry) {
            CorsConfiguration configuration = this.corsProperties.toCorsConfiguration();
            if (configuration != null) {
                registry.addMapping(this.graphQlProperties.getPath()).combine(configuration);
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ServerContainer.class, WebSocketHandler.class})
    @ConditionalOnProperty(prefix = "spring.graphql.websocket", name = {"path"})
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/servlet/GraphQlWebMvcAutoConfiguration$WebSocketConfiguration.class */
    public static class WebSocketConfiguration {
        @ConditionalOnMissingBean
        @Bean
        public GraphQlWebSocketHandler graphQlWebSocketHandler(WebGraphQlHandler webGraphQlHandler, GraphQlProperties properties, HttpMessageConverters converters) {
            return new GraphQlWebSocketHandler(webGraphQlHandler, getJsonConverter(converters), properties.getWebsocket().getConnectionInitTimeout());
        }

        private GenericHttpMessageConverter<Object> getJsonConverter(HttpMessageConverters converters) {
            return (GenericHttpMessageConverter) converters.getConverters().stream().filter(this::canReadJsonMap).findFirst().map(this::asGenericHttpMessageConverter).orElseThrow(() -> {
                return new IllegalStateException("No JSON converter");
            });
        }

        private boolean canReadJsonMap(HttpMessageConverter<?> candidate) {
            return candidate.canRead(Map.class, MediaType.APPLICATION_JSON);
        }

        private GenericHttpMessageConverter<Object> asGenericHttpMessageConverter(HttpMessageConverter<?> converter) {
            return (GenericHttpMessageConverter) converter;
        }

        @Bean
        public HandlerMapping graphQlWebSocketMapping(GraphQlWebSocketHandler handler, GraphQlProperties properties) {
            String path = properties.getWebsocket().getPath();
            GraphQlWebMvcAutoConfiguration.logger.info(LogMessage.format("GraphQL endpoint WebSocket %s", path));
            WebSocketHandlerMapping mapping = new WebSocketHandlerMapping();
            mapping.setWebSocketUpgradeMatch(true);
            mapping.setUrlMap(Collections.singletonMap(path, handler.initWebSocketHttpRequestHandler(new DefaultHandshakeHandler())));
            mapping.setOrder(-2);
            return mapping;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/servlet/GraphQlWebMvcAutoConfiguration$GraphiQlResourceHints.class */
    static class GraphiQlResourceHints implements RuntimeHintsRegistrar {
        GraphiQlResourceHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("graphiql/index.html");
        }
    }
}

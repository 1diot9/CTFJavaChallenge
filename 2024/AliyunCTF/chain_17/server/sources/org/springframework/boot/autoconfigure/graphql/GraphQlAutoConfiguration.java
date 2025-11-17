package org.springframework.boot.autoconfigure.graphql;

import graphql.GraphQL;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.log.LogMessage;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.data.pagination.ConnectionFieldTypeVisitor;
import org.springframework.graphql.data.pagination.CursorEncoder;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.graphql.data.pagination.EncodingCursorStrategy;
import org.springframework.graphql.data.query.ScrollPositionCursorStrategy;
import org.springframework.graphql.data.query.SliceConnectionAdapter;
import org.springframework.graphql.data.query.WindowConnectionAdapter;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.ConnectionTypeDefinitionConfigurer;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.DefaultBatchLoaderRegistry;
import org.springframework.graphql.execution.DefaultExecutionGraphQlService;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.execution.SubscriptionExceptionResolver;

@EnableConfigurationProperties({GraphQlProperties.class})
@AutoConfiguration
@ConditionalOnClass({GraphQL.class, GraphQlSource.class})
@ConditionalOnGraphQlSchema
@ImportRuntimeHints({GraphQlResourcesRuntimeHints.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/GraphQlAutoConfiguration.class */
public class GraphQlAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) GraphQlAutoConfiguration.class);
    private final ListableBeanFactory beanFactory;

    public GraphQlAutoConfiguration(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @ConditionalOnMissingBean
    @Bean
    public GraphQlSource graphQlSource(ResourcePatternResolver resourcePatternResolver, GraphQlProperties properties, ObjectProvider<DataFetcherExceptionResolver> exceptionResolvers, ObjectProvider<SubscriptionExceptionResolver> subscriptionExceptionResolvers, ObjectProvider<Instrumentation> instrumentations, ObjectProvider<RuntimeWiringConfigurer> wiringConfigurers, ObjectProvider<GraphQlSourceBuilderCustomizer> sourceCustomizers) {
        String[] schemaLocations = properties.getSchema().getLocations();
        Resource[] schemaResources = resolveSchemaResources(resourcePatternResolver, schemaLocations, properties.getSchema().getFileExtensions());
        GraphQlSource.SchemaResourceBuilder builder = GraphQlSource.schemaResourceBuilder().schemaResources(schemaResources).exceptionResolvers(exceptionResolvers.orderedStream().toList()).subscriptionExceptionResolvers(subscriptionExceptionResolvers.orderedStream().toList()).instrumentation(instrumentations.orderedStream().toList());
        if (properties.getSchema().getInspection().isEnabled()) {
            Log log = logger;
            Objects.requireNonNull(log);
            builder.inspectSchemaMappings((v1) -> {
                r1.info(v1);
            });
        }
        if (!properties.getSchema().getIntrospection().isEnabled()) {
            builder.configureRuntimeWiring(this::enableIntrospection);
        }
        builder.configureTypeDefinitions(new ConnectionTypeDefinitionConfigurer());
        Stream<RuntimeWiringConfigurer> orderedStream = wiringConfigurers.orderedStream();
        Objects.requireNonNull(builder);
        orderedStream.forEach(builder::configureRuntimeWiring);
        sourceCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder.build();
    }

    private RuntimeWiring.Builder enableIntrospection(RuntimeWiring.Builder wiring) {
        return wiring.fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY);
    }

    private Resource[] resolveSchemaResources(ResourcePatternResolver resolver, String[] locations, String[] extensions) {
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            for (String extension : extensions) {
                resources.addAll(resolveSchemaResources(resolver, location + "*" + extension));
            }
        }
        return (Resource[]) resources.toArray(new Resource[0]);
    }

    private List<Resource> resolveSchemaResources(ResourcePatternResolver resolver, String pattern) {
        try {
            return Arrays.asList(resolver.getResources(pattern));
        } catch (IOException ex) {
            logger.debug(LogMessage.format("Could not resolve schema location: '%s'", pattern), ex);
            return Collections.emptyList();
        }
    }

    @ConditionalOnMissingBean
    @Bean
    public BatchLoaderRegistry batchLoaderRegistry() {
        return new DefaultBatchLoaderRegistry();
    }

    @ConditionalOnMissingBean
    @Bean
    public ExecutionGraphQlService executionGraphQlService(GraphQlSource graphQlSource, BatchLoaderRegistry batchLoaderRegistry) {
        DefaultExecutionGraphQlService service = new DefaultExecutionGraphQlService(graphQlSource);
        service.addDataLoaderRegistrar(batchLoaderRegistry);
        return service;
    }

    @ConditionalOnMissingBean
    @Bean
    public AnnotatedControllerConfigurer annotatedControllerConfigurer(@Qualifier("applicationTaskExecutor") ObjectProvider<Executor> executorProvider) {
        AnnotatedControllerConfigurer controllerConfigurer = new AnnotatedControllerConfigurer();
        controllerConfigurer.addFormatterRegistrar(registry -> {
            ApplicationConversionService.addBeans(registry, this.beanFactory);
        });
        Objects.requireNonNull(controllerConfigurer);
        executorProvider.ifAvailable(controllerConfigurer::setExecutor);
        return controllerConfigurer;
    }

    @Bean
    DataFetcherExceptionResolver annotatedControllerConfigurerDataFetcherExceptionResolver(AnnotatedControllerConfigurer annotatedControllerConfigurer) {
        return annotatedControllerConfigurer.getExceptionResolver();
    }

    @ConditionalOnClass({ScrollPosition.class})
    @Configuration(proxyBeanMethods = false)
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/GraphQlAutoConfiguration$GraphQlDataAutoConfiguration.class */
    static class GraphQlDataAutoConfiguration {
        GraphQlDataAutoConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        EncodingCursorStrategy<ScrollPosition> cursorStrategy() {
            return CursorStrategy.withEncoder(new ScrollPositionCursorStrategy(), CursorEncoder.base64());
        }

        @Bean
        GraphQlSourceBuilderCustomizer cursorStrategyCustomizer(CursorStrategy<?> cursorStrategy) {
            if (cursorStrategy.supports(ScrollPosition.class)) {
                ConnectionFieldTypeVisitor connectionFieldTypeVisitor = ConnectionFieldTypeVisitor.create(List.of(new WindowConnectionAdapter(cursorStrategy), new SliceConnectionAdapter(cursorStrategy)));
                return builder -> {
                    builder.typeVisitors(List.of(connectionFieldTypeVisitor));
                };
            }
            return builder2 -> {
            };
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/GraphQlAutoConfiguration$GraphQlResourcesRuntimeHints.class */
    static class GraphQlResourcesRuntimeHints implements RuntimeHintsRegistrar {
        GraphQlResourcesRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("graphql/*.graphqls").registerPattern("graphql/*.gqls");
        }
    }
}

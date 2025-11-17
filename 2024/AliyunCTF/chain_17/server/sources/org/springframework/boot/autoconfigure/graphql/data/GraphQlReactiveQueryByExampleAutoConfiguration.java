package org.springframework.boot.autoconfigure.graphql.data;

import graphql.GraphQL;
import java.util.Collections;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.graphql.data.query.QueryByExampleDataFetcher;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, QueryByExampleDataFetcher.class, ReactiveQueryByExampleExecutor.class})
@ConditionalOnBean({GraphQlSource.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/data/GraphQlReactiveQueryByExampleAutoConfiguration.class */
public class GraphQlReactiveQueryByExampleAutoConfiguration {
    @Bean
    public GraphQlSourceBuilderCustomizer reactiveQueryByExampleRegistrar(ObjectProvider<ReactiveQueryByExampleExecutor<?>> reactiveExecutors) {
        RuntimeWiringConfigurer configurer = QueryByExampleDataFetcher.autoRegistrationConfigurer(Collections.emptyList(), reactiveExecutors.orderedStream().toList());
        return builder -> {
            builder.configureRuntimeWiring(configurer);
        };
    }
}

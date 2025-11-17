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
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.graphql.data.query.QueryByExampleDataFetcher;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, QueryByExampleDataFetcher.class, QueryByExampleExecutor.class})
@ConditionalOnBean({GraphQlSource.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/data/GraphQlQueryByExampleAutoConfiguration.class */
public class GraphQlQueryByExampleAutoConfiguration {
    @Bean
    public GraphQlSourceBuilderCustomizer queryByExampleRegistrar(ObjectProvider<QueryByExampleExecutor<?>> executors) {
        RuntimeWiringConfigurer configurer = QueryByExampleDataFetcher.autoRegistrationConfigurer(executors.orderedStream().toList(), Collections.emptyList());
        return builder -> {
            builder.configureRuntimeWiring(configurer);
        };
    }
}

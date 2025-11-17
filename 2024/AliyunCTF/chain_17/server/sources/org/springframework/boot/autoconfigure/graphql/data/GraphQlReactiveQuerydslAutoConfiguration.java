package org.springframework.boot.autoconfigure.graphql.data;

import com.querydsl.core.Query;
import graphql.GraphQL;
import java.util.Collections;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.querydsl.ReactiveQuerydslPredicateExecutor;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@AutoConfiguration(after = {GraphQlAutoConfiguration.class})
@ConditionalOnClass({GraphQL.class, Query.class, QuerydslDataFetcher.class, ReactiveQuerydslPredicateExecutor.class})
@ConditionalOnBean({GraphQlSource.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/data/GraphQlReactiveQuerydslAutoConfiguration.class */
public class GraphQlReactiveQuerydslAutoConfiguration {
    @Bean
    public GraphQlSourceBuilderCustomizer reactiveQuerydslRegistrar(ObjectProvider<ReactiveQuerydslPredicateExecutor<?>> reactiveExecutors) {
        RuntimeWiringConfigurer configurer = QuerydslDataFetcher.autoRegistrationConfigurer(Collections.emptyList(), reactiveExecutors.orderedStream().toList());
        return builder -> {
            builder.configureRuntimeWiring(configurer);
        };
    }
}

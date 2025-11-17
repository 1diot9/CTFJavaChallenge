package org.springframework.boot.autoconfigure.data.neo4j;

import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.ReactiveNeo4jClient;
import org.springframework.data.neo4j.core.ReactiveNeo4jOperations;
import org.springframework.data.neo4j.core.ReactiveNeo4jTemplate;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Flux;

@AutoConfiguration(after = {Neo4jDataAutoConfiguration.class})
@ConditionalOnClass({Driver.class, ReactiveNeo4jTemplate.class, ReactiveTransactionManager.class, Flux.class})
@ConditionalOnBean({Driver.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jReactiveDataAutoConfiguration.class */
public class Neo4jReactiveDataAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public ReactiveDatabaseSelectionProvider reactiveDatabaseSelectionProvider(Neo4jDataProperties dataProperties) {
        String database = dataProperties.getDatabase();
        return database != null ? ReactiveDatabaseSelectionProvider.createStaticDatabaseSelectionProvider(database) : ReactiveDatabaseSelectionProvider.getDefaultSelectionProvider();
    }

    @ConditionalOnMissingBean
    @Bean({"reactiveNeo4jClient"})
    public ReactiveNeo4jClient reactiveNeo4jClient(Driver driver, ReactiveDatabaseSelectionProvider databaseNameProvider) {
        return ReactiveNeo4jClient.create(driver, databaseNameProvider);
    }

    @ConditionalOnMissingBean({ReactiveNeo4jOperations.class})
    @Bean({"reactiveNeo4jTemplate"})
    public ReactiveNeo4jTemplate reactiveNeo4jTemplate(ReactiveNeo4jClient neo4jClient, Neo4jMappingContext neo4jMappingContext) {
        return new ReactiveNeo4jTemplate(neo4jClient, neo4jMappingContext);
    }
}

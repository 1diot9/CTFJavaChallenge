package org.springframework.boot.autoconfigure.data.neo4j;

import java.util.Set;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.neo4j.Neo4jAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.aot.Neo4jManagedTypes;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.Neo4jOperations;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;
import org.springframework.data.neo4j.core.mapping.Neo4jMappingContext;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

@EnableConfigurationProperties({Neo4jDataProperties.class})
@AutoConfiguration(before = {TransactionAutoConfiguration.class}, after = {Neo4jAutoConfiguration.class, TransactionManagerCustomizationAutoConfiguration.class})
@ConditionalOnClass({Driver.class, Neo4jTransactionManager.class, PlatformTransactionManager.class})
@ConditionalOnBean({Driver.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jDataAutoConfiguration.class */
public class Neo4jDataAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public Neo4jConversions neo4jConversions() {
        return new Neo4jConversions();
    }

    @ConditionalOnMissingBean
    @Bean
    Neo4jManagedTypes neo4jManagedTypes(ApplicationContext applicationContext) throws ClassNotFoundException {
        Set<Class<?>> initialEntityClasses = new EntityScanner(applicationContext).scan(Node.class, RelationshipProperties.class);
        return Neo4jManagedTypes.fromIterable(initialEntityClasses);
    }

    @ConditionalOnMissingBean
    @Bean
    public Neo4jMappingContext neo4jMappingContext(Neo4jManagedTypes managedTypes, Neo4jConversions neo4jConversions) {
        Neo4jMappingContext context = new Neo4jMappingContext(neo4jConversions);
        context.setManagedTypes(managedTypes);
        return context;
    }

    @ConditionalOnMissingBean
    @Bean
    public DatabaseSelectionProvider databaseSelectionProvider(Neo4jDataProperties properties) {
        String database = properties.getDatabase();
        return database != null ? DatabaseSelectionProvider.createStaticDatabaseSelectionProvider(database) : DatabaseSelectionProvider.getDefaultSelectionProvider();
    }

    @ConditionalOnMissingBean
    @Bean({"neo4jClient"})
    public Neo4jClient neo4jClient(Driver driver, DatabaseSelectionProvider databaseNameProvider) {
        return Neo4jClient.create(driver, databaseNameProvider);
    }

    @ConditionalOnMissingBean({Neo4jOperations.class})
    @Bean({"neo4jTemplate"})
    public Neo4jTemplate neo4jTemplate(Neo4jClient neo4jClient, Neo4jMappingContext neo4jMappingContext) {
        return new Neo4jTemplate(neo4jClient, neo4jMappingContext);
    }

    @ConditionalOnMissingBean({TransactionManager.class})
    @Bean({"transactionManager"})
    public Neo4jTransactionManager transactionManager(Driver driver, DatabaseSelectionProvider databaseNameProvider, ObjectProvider<TransactionManagerCustomizers> optionalCustomizers) {
        Neo4jTransactionManager transactionManager = new Neo4jTransactionManager(driver, databaseNameProvider);
        optionalCustomizers.ifAvailable(customizer -> {
            customizer.customize((TransactionManager) transactionManager);
        });
        return transactionManager;
    }
}

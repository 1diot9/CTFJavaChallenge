package org.springframework.boot.autoconfigure.data.neo4j;

import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.support.ReactiveNeo4jRepositoryFactoryBean;
import reactor.core.publisher.Flux;

@AutoConfiguration(after = {Neo4jReactiveDataAutoConfiguration.class})
@ConditionalOnClass({Driver.class, ReactiveNeo4jRepository.class, Flux.class})
@ConditionalOnMissingBean({ReactiveNeo4jRepositoryFactoryBean.class, ReactiveNeo4jRepositoryConfigurationExtension.class})
@ConditionalOnRepositoryType(store = "neo4j", type = RepositoryType.REACTIVE)
@Import({Neo4jReactiveRepositoriesRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jReactiveRepositoriesAutoConfiguration.class */
public class Neo4jReactiveRepositoriesAutoConfiguration {
}

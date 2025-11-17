package org.springframework.boot.autoconfigure.data.neo4j;

import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.support.Neo4jRepositoryFactoryBean;

@AutoConfiguration(after = {Neo4jDataAutoConfiguration.class})
@ConditionalOnClass({Driver.class, Neo4jRepository.class})
@ConditionalOnMissingBean({Neo4jRepositoryFactoryBean.class, Neo4jRepositoryConfigurationExtension.class})
@ConditionalOnRepositoryType(store = "neo4j", type = RepositoryType.IMPERATIVE)
@Import({Neo4jRepositoriesRegistrar.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jRepositoriesAutoConfiguration.class */
public class Neo4jRepositoriesAutoConfiguration {
}

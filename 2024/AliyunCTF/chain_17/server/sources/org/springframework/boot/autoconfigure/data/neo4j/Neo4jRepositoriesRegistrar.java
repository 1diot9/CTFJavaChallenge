package org.springframework.boot.autoconfigure.data.neo4j;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jRepositoriesRegistrar.class */
class Neo4jRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    Neo4jRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableNeo4jRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableNeo4jRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new Neo4jRepositoryConfigurationExtension();
    }

    @EnableNeo4jRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jRepositoriesRegistrar$EnableNeo4jRepositoriesConfiguration.class */
    private static final class EnableNeo4jRepositoriesConfiguration {
        private EnableNeo4jRepositoriesConfiguration() {
        }
    }
}

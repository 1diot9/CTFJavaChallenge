package org.springframework.boot.autoconfigure.data.neo4j;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.neo4j.repository.config.EnableReactiveNeo4jRepositories;
import org.springframework.data.neo4j.repository.config.ReactiveNeo4jRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jReactiveRepositoriesRegistrar.class */
class Neo4jReactiveRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    Neo4jReactiveRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveNeo4jRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableReactiveNeo4jRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveNeo4jRepositoryConfigurationExtension();
    }

    @EnableReactiveNeo4jRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jReactiveRepositoriesRegistrar$EnableReactiveNeo4jRepositoriesConfiguration.class */
    private static final class EnableReactiveNeo4jRepositoriesConfiguration {
        private EnableReactiveNeo4jRepositoriesConfiguration() {
        }
    }
}

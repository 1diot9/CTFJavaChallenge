package org.springframework.boot.autoconfigure.data.cassandra;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import org.springframework.data.cassandra.repository.config.ReactiveCassandraRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveRepositoriesRegistrar.class */
class CassandraReactiveRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    CassandraReactiveRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveCassandraRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableReactiveCassandraRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveCassandraRepositoryConfigurationExtension();
    }

    @EnableReactiveCassandraRepositories
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveRepositoriesRegistrar$EnableReactiveCassandraRepositoriesConfiguration.class */
    private static final class EnableReactiveCassandraRepositoriesConfiguration {
        private EnableReactiveCassandraRepositoriesConfiguration() {
        }
    }
}

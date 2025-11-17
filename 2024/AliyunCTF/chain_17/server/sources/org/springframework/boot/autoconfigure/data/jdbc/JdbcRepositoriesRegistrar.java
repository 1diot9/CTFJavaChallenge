package org.springframework.boot.autoconfigure.data.jdbc;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesRegistrar.class */
class JdbcRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    JdbcRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJdbcRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableJdbcRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new JdbcRepositoryConfigExtension();
    }

    @EnableJdbcRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jdbc/JdbcRepositoriesRegistrar$EnableJdbcRepositoriesConfiguration.class */
    private static final class EnableJdbcRepositoriesConfiguration {
        private EnableJdbcRepositoriesConfiguration() {
        }
    }
}

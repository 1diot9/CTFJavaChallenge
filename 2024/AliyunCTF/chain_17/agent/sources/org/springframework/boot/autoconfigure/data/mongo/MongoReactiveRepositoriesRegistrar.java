package org.springframework.boot.autoconfigure.data.mongo;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.mongodb.repository.config.ReactiveMongoRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveRepositoriesRegistrar.class */
class MongoReactiveRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    MongoReactiveRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveMongoRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableReactiveMongoRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveMongoRepositoryConfigurationExtension();
    }

    @EnableReactiveMongoRepositories
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/mongo/MongoReactiveRepositoriesRegistrar$EnableReactiveMongoRepositoriesConfiguration.class */
    private static final class EnableReactiveMongoRepositoriesConfiguration {
        private EnableReactiveMongoRepositoriesConfiguration() {
        }
    }
}

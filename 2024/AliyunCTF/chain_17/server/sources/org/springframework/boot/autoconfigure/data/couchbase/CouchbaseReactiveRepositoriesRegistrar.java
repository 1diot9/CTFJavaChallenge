package org.springframework.boot.autoconfigure.data.couchbase;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import org.springframework.data.couchbase.repository.config.ReactiveCouchbaseRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseReactiveRepositoriesRegistrar.class */
class CouchbaseReactiveRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    CouchbaseReactiveRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableReactiveCouchbaseRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableReactiveCouchbaseRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new ReactiveCouchbaseRepositoryConfigurationExtension();
    }

    @EnableReactiveCouchbaseRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseReactiveRepositoriesRegistrar$EnableReactiveCouchbaseRepositoriesConfiguration.class */
    private static final class EnableReactiveCouchbaseRepositoriesConfiguration {
        private EnableReactiveCouchbaseRepositoriesConfiguration() {
        }
    }
}

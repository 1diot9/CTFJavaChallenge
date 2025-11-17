package org.springframework.boot.autoconfigure.data.couchbase;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.couchbase.repository.config.CouchbaseRepositoryConfigurationExtension;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseRepositoriesRegistrar.class */
class CouchbaseRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    CouchbaseRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableCouchbaseRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableCouchbaseRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new CouchbaseRepositoryConfigurationExtension();
    }

    @EnableCouchbaseRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseRepositoriesRegistrar$EnableCouchbaseRepositoriesConfiguration.class */
    private static final class EnableCouchbaseRepositoriesConfiguration {
        private EnableCouchbaseRepositoriesConfiguration() {
        }
    }
}

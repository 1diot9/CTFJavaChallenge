package org.springframework.boot.autoconfigure.data.couchbase;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;

@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(CouchbaseClientFactory.class)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseClientFactoryDependentConfiguration.class */
class CouchbaseClientFactoryDependentConfiguration {
    CouchbaseClientFactoryDependentConfiguration() {
    }

    @ConditionalOnMissingBean(name = {"couchbaseTemplate"})
    @Bean(name = {"couchbaseTemplate"})
    CouchbaseTemplate couchbaseTemplate(CouchbaseClientFactory couchbaseClientFactory, MappingCouchbaseConverter mappingCouchbaseConverter) {
        return new CouchbaseTemplate(couchbaseClientFactory, mappingCouchbaseConverter);
    }

    @ConditionalOnMissingBean(name = {"couchbaseRepositoryOperationsMapping"})
    @Bean(name = {"couchbaseRepositoryOperationsMapping"})
    RepositoryOperationsMapping couchbaseRepositoryOperationsMapping(CouchbaseTemplate couchbaseTemplate) {
        return new RepositoryOperationsMapping(couchbaseTemplate);
    }
}

package org.springframework.boot.autoconfigure.data.couchbase;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.core.ReactiveCouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;

@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(CouchbaseClientFactory.class)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseReactiveDataConfiguration.class */
class CouchbaseReactiveDataConfiguration {
    CouchbaseReactiveDataConfiguration() {
    }

    @ConditionalOnMissingBean(name = {"reactiveCouchbaseTemplate"})
    @Bean(name = {"reactiveCouchbaseTemplate"})
    ReactiveCouchbaseTemplate reactiveCouchbaseTemplate(CouchbaseClientFactory couchbaseClientFactory, MappingCouchbaseConverter mappingCouchbaseConverter) {
        return new ReactiveCouchbaseTemplate(couchbaseClientFactory, mappingCouchbaseConverter);
    }

    @ConditionalOnMissingBean(name = {"reactiveCouchbaseRepositoryOperationsMapping"})
    @Bean(name = {"reactiveCouchbaseRepositoryOperationsMapping"})
    ReactiveRepositoryOperationsMapping reactiveCouchbaseRepositoryOperationsMapping(ReactiveCouchbaseTemplate reactiveCouchbaseTemplate) {
        return new ReactiveRepositoryOperationsMapping(reactiveCouchbaseTemplate);
    }
}

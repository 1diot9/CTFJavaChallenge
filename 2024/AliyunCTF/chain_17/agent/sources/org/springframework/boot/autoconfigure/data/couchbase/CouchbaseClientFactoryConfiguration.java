package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Cluster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty({"spring.data.couchbase.bucket-name"})
@ConditionalOnSingleCandidate(Cluster.class)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseClientFactoryConfiguration.class */
class CouchbaseClientFactoryConfiguration {
    CouchbaseClientFactoryConfiguration() {
    }

    @ConditionalOnMissingBean
    @Bean
    CouchbaseClientFactory couchbaseClientFactory(Cluster cluster, CouchbaseDataProperties properties) {
        return new SimpleCouchbaseClientFactory(cluster, properties.getBucketName(), properties.getScopeName());
    }
}

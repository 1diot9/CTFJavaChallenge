package org.springframework.boot.autoconfigure.cache;

import com.couchbase.client.java.Cluster;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.util.ObjectUtils;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Cluster.class, CouchbaseClientFactory.class, CouchbaseCacheManager.class})
@ConditionalOnSingleCandidate(CouchbaseClientFactory.class)
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CouchbaseCacheConfiguration.class */
class CouchbaseCacheConfiguration {
    CouchbaseCacheConfiguration() {
    }

    @Bean
    CouchbaseCacheManager cacheManager(CacheProperties cacheProperties, CacheManagerCustomizers customizers, ObjectProvider<CouchbaseCacheManagerBuilderCustomizer> couchbaseCacheManagerBuilderCustomizers, CouchbaseClientFactory clientFactory) {
        List<String> cacheNames = cacheProperties.getCacheNames();
        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager.builder(clientFactory);
        CacheProperties.Couchbase couchbase = cacheProperties.getCouchbase();
        org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration config = org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration.defaultCacheConfig();
        if (couchbase.getExpiration() != null) {
            config = config.entryExpiry(couchbase.getExpiration());
        }
        builder.cacheDefaults(config);
        if (!ObjectUtils.isEmpty(cacheNames)) {
            builder.initialCacheNames(new LinkedHashSet(cacheNames));
        }
        couchbaseCacheManagerBuilderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        CouchbaseCacheManager cacheManager = builder.build();
        return customizers.customize(cacheManager);
    }
}

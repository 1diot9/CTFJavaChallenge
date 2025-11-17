package org.springframework.boot.autoconfigure.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HazelcastInstance.class, HazelcastCacheManager.class})
@ConditionalOnSingleCandidate(HazelcastInstance.class)
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/HazelcastCacheConfiguration.class */
class HazelcastCacheConfiguration {
    HazelcastCacheConfiguration() {
    }

    @Bean
    HazelcastCacheManager cacheManager(CacheManagerCustomizers customizers, HazelcastInstance existingHazelcastInstance) {
        HazelcastCacheManager cacheManager = new HazelcastCacheManager(existingHazelcastInstance);
        return customizers.customize(cacheManager);
    }
}

package org.springframework.boot.autoconfigure.cache;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean({CacheManager.class})
@Configuration(proxyBeanMethods = false)
@Conditional({CacheCondition.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/SimpleCacheConfiguration.class */
class SimpleCacheConfiguration {
    SimpleCacheConfiguration() {
    }

    @Bean
    ConcurrentMapCacheManager cacheManager(CacheProperties cacheProperties, CacheManagerCustomizers cacheManagerCustomizers) {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return (ConcurrentMapCacheManager) cacheManagerCustomizers.customize(cacheManager);
    }
}

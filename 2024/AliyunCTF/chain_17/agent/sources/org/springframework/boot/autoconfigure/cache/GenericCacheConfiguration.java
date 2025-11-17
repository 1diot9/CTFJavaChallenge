package org.springframework.boot.autoconfigure.cache;

import java.util.Collection;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean({CacheManager.class})
@ConditionalOnBean({Cache.class})
@Conditional({CacheCondition.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/GenericCacheConfiguration.class */
class GenericCacheConfiguration {
    GenericCacheConfiguration() {
    }

    @Bean
    SimpleCacheManager cacheManager(CacheManagerCustomizers customizers, Collection<Cache> caches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return (SimpleCacheManager) customizers.customize(cacheManager);
    }
}

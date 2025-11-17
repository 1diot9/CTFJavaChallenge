package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/CachingConfigurer.class */
public interface CachingConfigurer {
    @Nullable
    default CacheManager cacheManager() {
        return null;
    }

    @Nullable
    default CacheResolver cacheResolver() {
        return null;
    }

    @Nullable
    default KeyGenerator keyGenerator() {
        return null;
    }

    @Nullable
    default CacheErrorHandler errorHandler() {
        return null;
    }
}

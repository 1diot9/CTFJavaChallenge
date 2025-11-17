package org.springframework.cache.annotation;

import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.Nullable;

@Deprecated(since = "6.0")
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/annotation/CachingConfigurerSupport.class */
public class CachingConfigurerSupport implements CachingConfigurer {
    @Override // org.springframework.cache.annotation.CachingConfigurer
    @Nullable
    public CacheManager cacheManager() {
        return null;
    }

    @Override // org.springframework.cache.annotation.CachingConfigurer
    @Nullable
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override // org.springframework.cache.annotation.CachingConfigurer
    @Nullable
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Override // org.springframework.cache.annotation.CachingConfigurer
    @Nullable
    public CacheErrorHandler errorHandler() {
        return null;
    }
}

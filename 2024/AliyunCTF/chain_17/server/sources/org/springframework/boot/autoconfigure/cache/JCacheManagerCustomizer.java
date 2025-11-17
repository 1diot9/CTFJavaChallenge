package org.springframework.boot.autoconfigure.cache;

import javax.cache.CacheManager;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/JCacheManagerCustomizer.class */
public interface JCacheManagerCustomizer {
    void customize(CacheManager cacheManager);
}

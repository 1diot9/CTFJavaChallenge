package org.springframework.boot.autoconfigure.cache;

import org.springframework.cache.CacheManager;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CacheManagerCustomizer.class */
public interface CacheManagerCustomizer<T extends CacheManager> {
    void customize(T cacheManager);
}

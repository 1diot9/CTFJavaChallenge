package org.springframework.boot.autoconfigure.cache;

import org.springframework.data.redis.cache.RedisCacheManager;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/RedisCacheManagerBuilderCustomizer.class */
public interface RedisCacheManagerBuilderCustomizer {
    void customize(RedisCacheManager.RedisCacheManagerBuilder builder);
}

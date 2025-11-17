package org.springframework.boot.autoconfigure.cache;

import org.springframework.data.couchbase.cache.CouchbaseCacheManager;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/cache/CouchbaseCacheManagerBuilderCustomizer.class */
public interface CouchbaseCacheManagerBuilderCustomizer {
    void customize(CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder);
}

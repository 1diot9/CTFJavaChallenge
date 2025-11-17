package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/support/NoOpCacheManager.class */
public class NoOpCacheManager implements CacheManager {
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap(16);
    private final Set<String> cacheNames = new LinkedHashSet(16);

    @Override // org.springframework.cache.CacheManager
    @Nullable
    public Cache getCache(String name) {
        Cache cache = this.caches.get(name);
        if (cache == null) {
            this.caches.computeIfAbsent(name, NoOpCache::new);
            synchronized (this.cacheNames) {
                this.cacheNames.add(name);
            }
        }
        return this.caches.get(name);
    }

    @Override // org.springframework.cache.CacheManager
    public Collection<String> getCacheNames() {
        Set unmodifiableSet;
        synchronized (this.cacheNames) {
            unmodifiableSet = Collections.unmodifiableSet(this.cacheNames);
        }
        return unmodifiableSet;
    }
}

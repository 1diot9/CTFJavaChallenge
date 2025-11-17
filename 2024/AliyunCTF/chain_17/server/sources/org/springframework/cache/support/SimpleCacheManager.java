package org.springframework.cache.support;

import java.util.Collection;
import java.util.Collections;
import org.springframework.cache.Cache;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/support/SimpleCacheManager.class */
public class SimpleCacheManager extends AbstractCacheManager {
    private Collection<? extends Cache> caches = Collections.emptySet();

    public void setCaches(Collection<? extends Cache> caches) {
        this.caches = caches;
    }

    @Override // org.springframework.cache.support.AbstractCacheManager
    protected Collection<? extends Cache> loadCaches() {
        return this.caches;
    }
}

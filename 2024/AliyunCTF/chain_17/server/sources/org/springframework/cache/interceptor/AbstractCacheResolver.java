package org.springframework.cache.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/AbstractCacheResolver.class */
public abstract class AbstractCacheResolver implements CacheResolver, InitializingBean {

    @Nullable
    private CacheManager cacheManager;

    @Nullable
    protected abstract Collection<String> getCacheNames(CacheOperationInvocationContext<?> context);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCacheResolver() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        Assert.state(this.cacheManager != null, "No CacheManager set");
        return this.cacheManager;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Assert.notNull(this.cacheManager, "CacheManager is required");
    }

    @Override // org.springframework.cache.interceptor.CacheResolver
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<Cache> result = new ArrayList<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            Cache cache = getCacheManager().getCache(cacheName);
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" + cacheName + "' for " + context.getOperation());
            }
            result.add(cache);
        }
        return result;
    }
}

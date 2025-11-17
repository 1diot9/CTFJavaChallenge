package org.springframework.cache.interceptor;

import java.util.Collection;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/interceptor/NamedCacheResolver.class */
public class NamedCacheResolver extends AbstractCacheResolver {

    @Nullable
    private Collection<String> cacheNames;

    public NamedCacheResolver() {
    }

    public NamedCacheResolver(CacheManager cacheManager, String... cacheNames) {
        super(cacheManager);
        this.cacheNames = List.of((Object[]) cacheNames);
    }

    public void setCacheNames(Collection<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    @Override // org.springframework.cache.interceptor.AbstractCacheResolver
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return this.cacheNames;
    }
}

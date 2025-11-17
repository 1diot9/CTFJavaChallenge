package org.springframework.cache.support;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/support/NoOpCache.class */
public class NoOpCache implements Cache {
    private final String name;

    public NoOpCache(String name) {
        Assert.notNull(name, "Cache name must not be null");
        this.name = name;
    }

    @Override // org.springframework.cache.Cache
    public String getName() {
        return this.name;
    }

    @Override // org.springframework.cache.Cache
    public Object getNativeCache() {
        return this;
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper get(Object key) {
        return null;
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        return null;
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        } catch (Exception ex) {
            throw new Cache.ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public CompletableFuture<?> retrieve(Object key) {
        return null;
    }

    @Override // org.springframework.cache.Cache
    public <T> CompletableFuture<T> retrieve(Object key, Supplier<CompletableFuture<T>> valueLoader) {
        return valueLoader.get();
    }

    @Override // org.springframework.cache.Cache
    public void put(Object key, @Nullable Object value) {
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return null;
    }

    @Override // org.springframework.cache.Cache
    public void evict(Object key) {
    }

    @Override // org.springframework.cache.Cache
    public boolean evictIfPresent(Object key) {
        return false;
    }

    @Override // org.springframework.cache.Cache
    public void clear() {
    }

    @Override // org.springframework.cache.Cache
    public boolean invalidate() {
        return false;
    }
}

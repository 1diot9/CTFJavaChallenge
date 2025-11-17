package org.springframework.cache.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/concurrent/ConcurrentMapCache.class */
public class ConcurrentMapCache extends AbstractValueAdaptingCache {
    private final String name;
    private final ConcurrentMap<Object, Object> store;

    @Nullable
    private final SerializationDelegate serialization;

    public ConcurrentMapCache(String name) {
        this(name, new ConcurrentHashMap(256), true);
    }

    public ConcurrentMapCache(String name, boolean allowNullValues) {
        this(name, new ConcurrentHashMap(256), allowNullValues);
    }

    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues) {
        this(name, store, allowNullValues, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store, boolean allowNullValues, @Nullable SerializationDelegate serialization) {
        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(store, "Store must not be null");
        this.name = name;
        this.store = store;
        this.serialization = serialization;
    }

    public final boolean isStoreByValue() {
        return this.serialization != null;
    }

    @Override // org.springframework.cache.Cache
    public final String getName() {
        return this.name;
    }

    @Override // org.springframework.cache.Cache
    public final ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override // org.springframework.cache.support.AbstractValueAdaptingCache
    @Nullable
    protected Object lookup(Object key) {
        return this.store.get(key);
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public <T> T get(Object obj, Callable<T> callable) {
        return (T) fromStoreValue(this.store.computeIfAbsent(obj, k -> {
            try {
                return toStoreValue(callable.call());
            } catch (Throwable ex) {
                throw new Cache.ValueRetrievalException(obj, callable, ex);
            }
        }));
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public CompletableFuture<?> retrieve(Object key) {
        Object value = lookup(key);
        if (value != null) {
            return CompletableFuture.completedFuture(isAllowNullValues() ? toValueWrapper(value) : fromStoreValue(value));
        }
        return null;
    }

    @Override // org.springframework.cache.Cache
    public <T> CompletableFuture<T> retrieve(Object key, Supplier<CompletableFuture<T>> valueLoader) {
        return CompletableFuture.supplyAsync(() -> {
            return fromStoreValue(this.store.computeIfAbsent(key, k -> {
                return toStoreValue(((CompletableFuture) valueLoader.get()).join());
            }));
        });
    }

    @Override // org.springframework.cache.Cache
    public void put(Object key, @Nullable Object value) {
        this.store.put(key, toStoreValue(value));
    }

    @Override // org.springframework.cache.Cache
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object existing = this.store.putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    @Override // org.springframework.cache.Cache
    public void evict(Object key) {
        this.store.remove(key);
    }

    @Override // org.springframework.cache.Cache
    public boolean evictIfPresent(Object key) {
        return this.store.remove(key) != null;
    }

    @Override // org.springframework.cache.Cache
    public void clear() {
        this.store.clear();
    }

    @Override // org.springframework.cache.Cache
    public boolean invalidate() {
        boolean notEmpty = !this.store.isEmpty();
        this.store.clear();
        return notEmpty;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cache.support.AbstractValueAdaptingCache
    public Object toStoreValue(@Nullable Object userValue) {
        Object storeValue = super.toStoreValue(userValue);
        if (this.serialization != null) {
            try {
                return this.serialization.serializeToByteArray(storeValue);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue + "'. Does it implement Serializable?", ex);
            }
        }
        return storeValue;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.cache.support.AbstractValueAdaptingCache
    public Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue != null && this.serialization != null) {
            try {
                return super.fromStoreValue(this.serialization.deserializeFromByteArray((byte[]) storeValue));
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", ex);
            }
        }
        return super.fromStoreValue(storeValue);
    }
}

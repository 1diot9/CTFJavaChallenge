package org.springframework.cache.concurrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/cache/concurrent/ConcurrentMapCacheManager.class */
public class ConcurrentMapCacheManager implements CacheManager, BeanClassLoaderAware {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap(16);
    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean storeByValue = false;

    @Nullable
    private SerializationDelegate serialization;

    public ConcurrentMapCacheManager() {
    }

    public ConcurrentMapCacheManager(String... cacheNames) {
        setCacheNames(Arrays.asList(cacheNames));
    }

    public void setCacheNames(@Nullable Collection<String> cacheNames) {
        if (cacheNames != null) {
            for (String name : cacheNames) {
                this.cacheMap.put(name, createConcurrentMapCache(name));
            }
            this.dynamic = false;
            return;
        }
        this.dynamic = true;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        if (allowNullValues != this.allowNullValues) {
            this.allowNullValues = allowNullValues;
            recreateCaches();
        }
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public void setStoreByValue(boolean storeByValue) {
        if (storeByValue != this.storeByValue) {
            this.storeByValue = storeByValue;
            recreateCaches();
        }
    }

    public boolean isStoreByValue() {
        return this.storeByValue;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.serialization = new SerializationDelegate(classLoader);
        if (isStoreByValue()) {
            recreateCaches();
        }
    }

    @Override // org.springframework.cache.CacheManager
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    @Override // org.springframework.cache.CacheManager
    @Nullable
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null && this.dynamic) {
            cache = this.cacheMap.computeIfAbsent(name, this::createConcurrentMapCache);
        }
        return cache;
    }

    private void recreateCaches() {
        for (Map.Entry<String, Cache> entry : this.cacheMap.entrySet()) {
            entry.setValue(createConcurrentMapCache(entry.getKey()));
        }
    }

    protected Cache createConcurrentMapCache(String name) {
        SerializationDelegate actualSerialization = isStoreByValue() ? this.serialization : null;
        return new ConcurrentMapCache(name, new ConcurrentHashMap(256), isAllowNullValues(), actualSerialization);
    }
}

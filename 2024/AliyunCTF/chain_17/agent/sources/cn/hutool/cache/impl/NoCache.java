package cn.hutool.cache.impl;

import cn.hutool.cache.Cache;
import cn.hutool.core.lang.func.Func0;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/NoCache.class */
public class NoCache<K, V> implements Cache<K, V> {
    private static final long serialVersionUID = 1;

    @Override // cn.hutool.cache.Cache
    public int capacity() {
        return 0;
    }

    @Override // cn.hutool.cache.Cache
    public long timeout() {
        return 0L;
    }

    @Override // cn.hutool.cache.Cache
    public void put(K key, V object) {
    }

    @Override // cn.hutool.cache.Cache
    public void put(K key, V object, long timeout) {
    }

    @Override // cn.hutool.cache.Cache
    public boolean containsKey(K key) {
        return false;
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key) {
        return null;
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key, boolean isUpdateLastAccess) {
        return null;
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key, Func0<V> supplier) {
        return get(key, true, supplier);
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key, boolean isUpdateLastAccess, Func0<V> supplier) {
        if (null == supplier) {
            return null;
        }
        try {
            return supplier.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.lang.Iterable
    public Iterator<V> iterator() {
        return new Iterator<V>() { // from class: cn.hutool.cache.impl.NoCache.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return false;
            }

            @Override // java.util.Iterator
            public V next() {
                return null;
            }
        };
    }

    @Override // cn.hutool.cache.Cache
    public Iterator<CacheObj<K, V>> cacheObjIterator() {
        return null;
    }

    @Override // cn.hutool.cache.Cache
    public int prune() {
        return 0;
    }

    @Override // cn.hutool.cache.Cache
    public boolean isFull() {
        return false;
    }

    @Override // cn.hutool.cache.Cache
    public void remove(K key) {
    }

    @Override // cn.hutool.cache.Cache
    public void clear() {
    }

    @Override // cn.hutool.cache.Cache
    public int size() {
        return 0;
    }

    @Override // cn.hutool.cache.Cache
    public boolean isEmpty() {
        return false;
    }
}

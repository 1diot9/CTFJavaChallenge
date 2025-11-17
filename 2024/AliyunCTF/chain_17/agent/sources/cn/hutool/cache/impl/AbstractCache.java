package cn.hutool.cache.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheListener;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutableObj;
import cn.hutool.core.map.SafeConcurrentHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/AbstractCache.class */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    private static final long serialVersionUID = 1;
    protected Map<Mutable<K>, CacheObj<K, V>> cacheMap;
    protected int capacity;
    protected long timeout;
    protected boolean existCustomTimeout;
    protected CacheListener<K, V> listener;
    protected final SafeConcurrentHashMap<K, Lock> keyLockMap = new SafeConcurrentHashMap<>();
    protected LongAdder hitCount = new LongAdder();
    protected LongAdder missCount = new LongAdder();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int pruneCache();

    @Override // cn.hutool.cache.Cache
    public void put(K key, V object) {
        put(key, object, this.timeout);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void putWithoutLock(K key, V object, long timeout) {
        CacheObj<K, V> co = new CacheObj<>(key, object, timeout);
        if (timeout != 0) {
            this.existCustomTimeout = true;
        }
        if (isFull()) {
            pruneCache();
        }
        this.cacheMap.put(MutableObj.of(key), co);
    }

    public long getHitCount() {
        return this.hitCount.sum();
    }

    public long getMissCount() {
        return this.missCount.sum();
    }

    @Override // cn.hutool.cache.Cache
    public V get(K key, boolean isUpdateLastAccess, Func0<V> supplier) {
        V v = get((AbstractCache<K, V>) key, isUpdateLastAccess);
        if (null == v && null != supplier) {
            Lock keyLock = this.keyLockMap.computeIfAbsent(key, k -> {
                return new ReentrantLock();
            });
            keyLock.lock();
            try {
                CacheObj<K, V> co = getWithoutLock(key);
                if (null == co || co.isExpired()) {
                    try {
                        v = supplier.call();
                        put(key, v, this.timeout);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    v = co.get(isUpdateLastAccess);
                }
            } finally {
                keyLock.unlock();
                this.keyLockMap.remove(key);
            }
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CacheObj<K, V> getWithoutLock(K key) {
        return this.cacheMap.get(MutableObj.of(key));
    }

    @Override // java.lang.Iterable
    public Iterator<V> iterator() {
        CacheObjIterator<K, V> copiedIterator = (CacheObjIterator) cacheObjIterator();
        return new CacheValuesIterator(copiedIterator);
    }

    @Override // cn.hutool.cache.Cache
    public int capacity() {
        return this.capacity;
    }

    @Override // cn.hutool.cache.Cache
    public long timeout() {
        return this.timeout;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPruneExpiredActive() {
        return this.timeout != 0 || this.existCustomTimeout;
    }

    @Override // cn.hutool.cache.Cache
    public boolean isFull() {
        return this.capacity > 0 && this.cacheMap.size() >= this.capacity;
    }

    @Override // cn.hutool.cache.Cache
    public int size() {
        return this.cacheMap.size();
    }

    @Override // cn.hutool.cache.Cache
    public boolean isEmpty() {
        return this.cacheMap.isEmpty();
    }

    public String toString() {
        return this.cacheMap.toString();
    }

    @Override // cn.hutool.cache.Cache
    public AbstractCache<K, V> setListener(CacheListener<K, V> listener) {
        this.listener = listener;
        return this;
    }

    public Set<K> keySet() {
        return (Set) this.cacheMap.keySet().stream().map((v0) -> {
            return v0.get2();
        }).collect(Collectors.toSet());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRemove(K key, V cachedObject) {
        CacheListener<K, V> listener = this.listener;
        if (null != listener) {
            listener.onRemove(key, cachedObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CacheObj<K, V> removeWithoutLock(K key, boolean withMissCount) {
        CacheObj<K, V> co = this.cacheMap.remove(MutableObj.of(key));
        if (withMissCount) {
            this.missCount.increment();
        }
        return co;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Iterator<CacheObj<K, V>> cacheObjIter() {
        return this.cacheMap.values().iterator();
    }
}

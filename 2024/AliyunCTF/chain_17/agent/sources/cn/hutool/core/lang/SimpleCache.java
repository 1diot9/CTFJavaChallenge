package cn.hutool.core.lang;

import cn.hutool.core.collection.TransIter;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutableObj;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.map.WeakConcurrentMap;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/SimpleCache.class */
public class SimpleCache<K, V> implements Iterable<Map.Entry<K, V>>, Serializable {
    private static final long serialVersionUID = 1;
    private final Map<Mutable<K>, V> rawMap;
    private final ReadWriteLock lock;
    protected final Map<K, Lock> keyLockMap;

    public SimpleCache() {
        this(new WeakConcurrentMap());
    }

    public SimpleCache(Map<Mutable<K>, V> initMap) {
        this.lock = new ReentrantReadWriteLock();
        this.keyLockMap = new SafeConcurrentHashMap();
        this.rawMap = initMap;
    }

    public V get(K key) {
        this.lock.readLock().lock();
        try {
            return this.rawMap.get(MutableObj.of(key));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public V get(K key, Func0<V> supplier) {
        return get(key, null, supplier);
    }

    public V get(K key, Predicate<V> validPredicate, Func0<V> supplier) {
        V v = get(key);
        if (null != validPredicate && null != v && false == validPredicate.test(v)) {
            v = null;
        }
        if (null == v && null != supplier) {
            Lock keyLock = this.keyLockMap.computeIfAbsent(key, k -> {
                return new ReentrantLock();
            });
            keyLock.lock();
            try {
                v = get(key);
                if (null == v || (null != validPredicate && false == validPredicate.test(v))) {
                    try {
                        v = supplier.call();
                        put(key, v);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                keyLock.unlock();
                this.keyLockMap.remove(key);
            }
        }
        return v;
    }

    public V put(K key, V value) {
        this.lock.writeLock().lock();
        try {
            this.rawMap.put(MutableObj.of(key), value);
            return value;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public V remove(K key) {
        this.lock.writeLock().lock();
        try {
            return this.rawMap.remove(MutableObj.of(key));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.rawMap.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override // java.lang.Iterable
    public Iterator<Map.Entry<K, V>> iterator() {
        return new TransIter(this.rawMap.entrySet().iterator(), entry -> {
            return new Map.Entry<K, V>() { // from class: cn.hutool.core.lang.SimpleCache.1
                @Override // java.util.Map.Entry
                public K getKey() {
                    return (K) ((Mutable) entry.getKey()).get2();
                }

                @Override // java.util.Map.Entry
                public V getValue() {
                    return (V) entry.getValue();
                }

                @Override // java.util.Map.Entry
                public V setValue(V v) {
                    return (V) entry.setValue(v);
                }
            };
        });
    }
}

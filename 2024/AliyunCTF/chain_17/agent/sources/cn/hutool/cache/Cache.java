package cn.hutool.cache;

import cn.hutool.cache.impl.CacheObj;
import cn.hutool.core.lang.func.Func0;
import java.io.Serializable;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/Cache.class */
public interface Cache<K, V> extends Iterable<V>, Serializable {
    int capacity();

    long timeout();

    void put(K k, V v);

    void put(K k, V v, long j);

    V get(K k, boolean z, Func0<V> func0);

    V get(K k, boolean z);

    Iterator<CacheObj<K, V>> cacheObjIterator();

    int prune();

    boolean isFull();

    void remove(K k);

    void clear();

    int size();

    boolean isEmpty();

    boolean containsKey(K k);

    default V get(K key) {
        return get((Cache<K, V>) key, true);
    }

    default V get(K key, Func0<V> supplier) {
        return get(key, true, supplier);
    }

    default Cache<K, V> setListener(CacheListener<K, V> listener) {
        return this;
    }
}

package cn.hutool.cache.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/FIFOCache.class */
public class FIFOCache<K, V> extends StampedCache<K, V> {
    private static final long serialVersionUID = 1;

    public FIFOCache(int capacity) {
        this(capacity, 0L);
    }

    public FIFOCache(int capacity, long timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
        this.cacheMap = new LinkedHashMap(capacity + 1, 1.0f, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.cache.impl.AbstractCache
    public int pruneCache() {
        int count = 0;
        CacheObj<K, V> first = null;
        Iterator<CacheObj<K, V>> values = cacheObjIter();
        if (isPruneExpiredActive()) {
            while (values.hasNext()) {
                CacheObj<K, V> co = values.next();
                if (co.isExpired()) {
                    values.remove();
                    onRemove(co.key, co.obj);
                    count++;
                } else if (first == null) {
                    first = co;
                }
            }
        } else {
            first = values.hasNext() ? values.next() : null;
        }
        if (isFull() && null != first) {
            removeWithoutLock(first.key, false);
            onRemove(first.key, first.obj);
            count++;
        }
        return count;
    }
}

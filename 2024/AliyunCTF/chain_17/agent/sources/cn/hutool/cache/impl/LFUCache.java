package cn.hutool.cache.impl;

import java.util.HashMap;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/LFUCache.class */
public class LFUCache<K, V> extends StampedCache<K, V> {
    private static final long serialVersionUID = 1;

    public LFUCache(int capacity) {
        this(capacity, 0L);
    }

    public LFUCache(int capacity, long timeout) {
        capacity = Integer.MAX_VALUE == capacity ? capacity - 1 : capacity;
        this.capacity = capacity;
        this.timeout = timeout;
        this.cacheMap = new HashMap(capacity + 1, 1.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.cache.impl.AbstractCache
    public int pruneCache() {
        int count = 0;
        CacheObj<K, V> comin = null;
        Iterator<CacheObj<K, V>> values = cacheObjIter();
        while (values.hasNext()) {
            CacheObj<K, V> co = values.next();
            if (co.isExpired()) {
                values.remove();
                onRemove(co.key, co.obj);
                count++;
            } else if (comin == null || co.accessCount.get() < comin.accessCount.get()) {
                comin = co;
            }
        }
        if (isFull() && comin != null) {
            long minAccessCount = comin.accessCount.get();
            Iterator<CacheObj<K, V>> values2 = cacheObjIter();
            while (values2.hasNext()) {
                CacheObj<K, V> co1 = values2.next();
                if (co1.accessCount.addAndGet(-minAccessCount) <= 0) {
                    values2.remove();
                    onRemove(co1.key, co1.obj);
                    count++;
                }
            }
        }
        return count;
    }
}

package cn.hutool.cache.impl;

import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.map.FixedLinkedHashMap;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/LRUCache.class */
public class LRUCache<K, V> extends ReentrantCache<K, V> {
    private static final long serialVersionUID = 1;

    public LRUCache(int capacity) {
        this(capacity, 0L);
    }

    public LRUCache(int capacity, long timeout) {
        capacity = Integer.MAX_VALUE == capacity ? capacity - 1 : capacity;
        this.capacity = capacity;
        this.timeout = timeout;
        FixedLinkedHashMap<Mutable<K>, CacheObj<K, V>> fixedLinkedHashMap = new FixedLinkedHashMap<>(capacity);
        fixedLinkedHashMap.setRemoveListener(entry -> {
            if (null != this.listener) {
                this.listener.onRemove(((Mutable) entry.getKey()).get2(), ((CacheObj) entry.getValue()).getValue());
            }
        });
        this.cacheMap = fixedLinkedHashMap;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.cache.impl.AbstractCache
    public int pruneCache() {
        if (!isPruneExpiredActive()) {
            return 0;
        }
        int count = 0;
        Iterator<CacheObj<K, V>> values = cacheObjIter();
        while (values.hasNext()) {
            CacheObj<K, V> co = values.next();
            if (co.isExpired()) {
                values.remove();
                onRemove(co.key, co.obj);
                count++;
            }
        }
        return count;
    }
}

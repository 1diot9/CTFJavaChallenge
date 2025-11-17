package cn.hutool.cache.impl;

import java.io.Serializable;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/CacheValuesIterator.class */
public class CacheValuesIterator<V> implements Iterator<V>, Serializable {
    private static final long serialVersionUID = 1;
    private final CacheObjIterator<?, V> cacheObjIter;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheValuesIterator(CacheObjIterator<?, V> iterator) {
        this.cacheObjIter = iterator;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.cacheObjIter.hasNext();
    }

    @Override // java.util.Iterator
    public V next() {
        return this.cacheObjIter.next().getValue();
    }

    @Override // java.util.Iterator
    public void remove() {
        this.cacheObjIter.remove();
    }
}

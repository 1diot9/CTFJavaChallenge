package cn.hutool.cache.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cache/impl/CacheObjIterator.class */
public class CacheObjIterator<K, V> implements Iterator<CacheObj<K, V>>, Serializable {
    private static final long serialVersionUID = 1;
    private final Iterator<CacheObj<K, V>> iterator;
    private CacheObj<K, V> nextValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CacheObjIterator(Iterator<CacheObj<K, V>> iterator) {
        this.iterator = iterator;
        nextValue();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nextValue != null;
    }

    @Override // java.util.Iterator
    public CacheObj<K, V> next() {
        if (false == hasNext()) {
            throw new NoSuchElementException();
        }
        CacheObj<K, V> cachedObject = this.nextValue;
        nextValue();
        return cachedObject;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Cache values Iterator is not support to modify.");
    }

    private void nextValue() {
        while (this.iterator.hasNext()) {
            this.nextValue = this.iterator.next();
            if (!this.nextValue.isExpired()) {
                return;
            }
        }
        this.nextValue = null;
    }
}

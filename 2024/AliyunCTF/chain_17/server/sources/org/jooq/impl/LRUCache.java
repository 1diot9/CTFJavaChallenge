package org.jooq.impl;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LRUCache.class */
final class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int size;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LRUCache(int size) {
        super(size, 0.75f, true);
        this.size = size;
    }

    @Override // java.util.LinkedHashMap
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.size;
    }
}

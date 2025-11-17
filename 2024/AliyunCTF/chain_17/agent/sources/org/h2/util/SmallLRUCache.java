package org.h2.util;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/SmallLRUCache.class */
public class SmallLRUCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1;
    private int size;

    private SmallLRUCache(int i) {
        super(i, 0.75f, true);
        this.size = i;
    }

    public static <K, V> SmallLRUCache<K, V> newInstance(int i) {
        return new SmallLRUCache<>(i);
    }

    public void setMaxSize(int i) {
        this.size = i;
    }

    @Override // java.util.LinkedHashMap
    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return size() > this.size;
    }
}

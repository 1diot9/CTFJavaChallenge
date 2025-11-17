package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiConsumer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/LRUMap.class */
public class LRUMap<K, V> implements LookupCache<K, V>, Serializable {
    private static final long serialVersionUID = 2;
    protected final int _initialEntries;
    protected final int _maxEntries;
    protected final transient PrivateMaxEntriesMap<K, V> _map;

    public LRUMap(int initialEntries, int maxEntries) {
        this._initialEntries = initialEntries;
        this._maxEntries = maxEntries;
        this._map = new PrivateMaxEntriesMap.Builder().initialCapacity(initialEntries).maximumCapacity(maxEntries).concurrencyLevel(4).build();
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V put(K key, V value) {
        return this._map.put(key, value);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V putIfAbsent(K key, V value) {
        return this._map.putIfAbsent(key, value);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public V get(Object key) {
        return this._map.get(key);
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public void clear() {
        this._map.clear();
    }

    @Override // com.fasterxml.jackson.databind.util.LookupCache
    public int size() {
        return this._map.size();
    }

    public void contents(BiConsumer<K, V> consumer) {
        for (Map.Entry<K, V> entry : this._map.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    protected Object readResolve() {
        return new LRUMap(this._initialEntries, this._maxEntries);
    }
}

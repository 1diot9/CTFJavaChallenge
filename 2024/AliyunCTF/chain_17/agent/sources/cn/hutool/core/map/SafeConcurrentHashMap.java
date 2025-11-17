package cn.hutool.core.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/SafeConcurrentHashMap.class */
public class SafeConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
    private static final long serialVersionUID = 1;

    public SafeConcurrentHashMap() {
    }

    public SafeConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public SafeConcurrentHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public SafeConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SafeConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override // java.util.concurrent.ConcurrentHashMap, java.util.Map, java.util.concurrent.ConcurrentMap
    public V computeIfAbsent(K k, Function<? super K, ? extends V> function) {
        return (V) MapUtil.computeIfAbsent(this, k, function);
    }
}

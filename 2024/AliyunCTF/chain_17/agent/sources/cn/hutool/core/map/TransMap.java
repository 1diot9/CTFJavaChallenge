package cn.hutool.core.map;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/TransMap.class */
public abstract class TransMap<K, V> extends MapWrapper<K, V> {
    private static final long serialVersionUID = 1;

    protected abstract K customKey(Object obj);

    protected abstract V customValue(Object obj);

    public TransMap(Supplier<Map<K, V>> mapFactory) {
        super(mapFactory);
    }

    public TransMap(Map<K, V> emptyMap) {
        super(emptyMap);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V get(Object obj) {
        return (V) super.get(customKey(obj));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V put(K k, V v) {
        return (V) super.put(customKey(k), customValue(v));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public boolean containsKey(Object key) {
        return super.containsKey(customKey(key));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V remove(Object obj) {
        return (V) super.remove(customKey(obj));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public boolean remove(Object key, Object value) {
        return super.remove(customKey(key), customValue(value));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public boolean replace(K key, V oldValue, V newValue) {
        return super.replace(customKey(key), customValue(oldValue), customValue(newValue));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V replace(K k, V v) {
        return (V) super.replace(customKey(k), customValue(v));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V getOrDefault(Object obj, V v) {
        return (V) super.getOrDefault(customKey(obj), customValue(v));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return (V) super.computeIfPresent(customKey(k), (k2, v) -> {
            return biFunction.apply(customKey(k2), customValue(v));
        });
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        return (V) super.compute(customKey(k), (k2, v) -> {
            return biFunction.apply(customKey(k2), customValue(v));
        });
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction) {
        return (V) super.merge(customKey(k), customValue(v), (v1, v2) -> {
            return biFunction.apply(customValue(v1), customValue(v2));
        });
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V putIfAbsent(K k, V v) {
        return (V) super.putIfAbsent(customKey(k), customValue(v));
    }

    @Override // cn.hutool.core.map.MapWrapper, java.util.Map
    public V computeIfAbsent(K k, Function<? super K, ? extends V> function) {
        return (V) super.computeIfAbsent(customKey(k), function);
    }
}

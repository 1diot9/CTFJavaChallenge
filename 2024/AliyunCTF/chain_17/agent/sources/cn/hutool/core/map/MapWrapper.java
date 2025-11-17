package cn.hutool.core.map;

import cn.hutool.core.util.ObjectUtil;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/MapWrapper.class */
public class MapWrapper<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable, Cloneable {
    private static final long serialVersionUID = -7524578042008586382L;
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected static final int DEFAULT_INITIAL_CAPACITY = 16;
    private Map<K, V> raw;

    public MapWrapper(Supplier<Map<K, V>> mapFactory) {
        this(mapFactory.get());
    }

    public MapWrapper(Map<K, V> raw) {
        this.raw = raw;
    }

    public Map<K, V> getRaw() {
        return this.raw;
    }

    @Override // java.util.Map
    public int size() {
        return this.raw.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.raw.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.raw.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.raw.containsValue(value);
    }

    @Override // java.util.Map
    public V get(Object key) {
        return this.raw.get(key);
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        return this.raw.put(key, value);
    }

    @Override // java.util.Map
    public V remove(Object key) {
        return this.raw.remove(key);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        this.raw.putAll(m);
    }

    @Override // java.util.Map
    public void clear() {
        this.raw.clear();
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return this.raw.values();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return this.raw.keySet();
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return this.raw.entrySet();
    }

    @Override // java.lang.Iterable
    public Iterator<Map.Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    @Override // java.util.Map
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapWrapper<?, ?> that = (MapWrapper) o;
        return Objects.equals(this.raw, that.raw);
    }

    @Override // java.util.Map
    public int hashCode() {
        return Objects.hash(this.raw);
    }

    public String toString() {
        return this.raw.toString();
    }

    @Override // java.util.Map
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.raw.forEach(action);
    }

    @Override // java.util.Map
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.raw.replaceAll(function);
    }

    @Override // java.util.Map
    public V putIfAbsent(K key, V value) {
        return this.raw.putIfAbsent(key, value);
    }

    @Override // java.util.Map
    public boolean remove(Object key, Object value) {
        return this.raw.remove(key, value);
    }

    @Override // java.util.Map
    public boolean replace(K key, V oldValue, V newValue) {
        return this.raw.replace(key, oldValue, newValue);
    }

    @Override // java.util.Map
    public V replace(K key, V value) {
        return this.raw.replace(key, value);
    }

    @Override // java.util.Map
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return this.raw.computeIfAbsent(key, mappingFunction);
    }

    @Override // java.util.Map
    public V getOrDefault(Object key, V defaultValue) {
        return this.raw.getOrDefault(key, defaultValue);
    }

    @Override // java.util.Map
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.raw.computeIfPresent(key, remappingFunction);
    }

    @Override // java.util.Map
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.raw.compute(key, remappingFunction);
    }

    @Override // java.util.Map
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.raw.merge(key, value, remappingFunction);
    }

    @Override // 
    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public MapWrapper<K, V> mo212clone() throws CloneNotSupportedException {
        MapWrapper<K, V> clone = (MapWrapper) super.clone();
        clone.raw = (Map) ObjectUtil.clone(this.raw);
        return clone;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.raw);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.raw = (Map) in.readObject();
    }
}

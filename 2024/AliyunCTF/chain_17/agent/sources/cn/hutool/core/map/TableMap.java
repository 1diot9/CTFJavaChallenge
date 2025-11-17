package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/TableMap.class */
public class TableMap<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable {
    private static final long serialVersionUID = 1;
    private static final int DEFAULT_CAPACITY = 10;
    private final List<K> keys;
    private final List<V> values;

    public TableMap() {
        this(10);
    }

    public TableMap(int size) {
        this.keys = new ArrayList(size);
        this.values = new ArrayList(size);
    }

    public TableMap(K[] keys, V[] values) {
        this.keys = CollUtil.toList(keys);
        this.values = CollUtil.toList(values);
    }

    @Override // java.util.Map
    public int size() {
        return this.keys.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return CollUtil.isEmpty((Collection<?>) this.keys);
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.keys.contains(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.values.contains(value);
    }

    @Override // java.util.Map
    public V get(Object key) {
        int index = this.keys.indexOf(key);
        if (index > -1) {
            return this.values.get(index);
        }
        return null;
    }

    public K getKey(V value) {
        int index = this.values.indexOf(value);
        if (index > -1) {
            return this.keys.get(index);
        }
        return null;
    }

    public List<V> getValues(K key) {
        return CollUtil.getAny(this.values, ListUtil.indexOfAll(this.keys, ele -> {
            return ObjectUtil.equal(ele, key);
        }));
    }

    public List<K> getKeys(V value) {
        return CollUtil.getAny(this.keys, ListUtil.indexOfAll(this.values, ele -> {
            return ObjectUtil.equal(ele, value);
        }));
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        this.keys.add(key);
        this.values.add(value);
        return null;
    }

    @Override // java.util.Map
    public V remove(Object key) {
        V v = null;
        while (true) {
            V lastValue = v;
            int index = this.keys.indexOf(key);
            if (index > -1) {
                v = removeByIndex(index);
            } else {
                return lastValue;
            }
        }
    }

    public V removeByIndex(int index) {
        this.keys.remove(index);
        return this.values.remove(index);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map
    public void clear() {
        this.keys.clear();
        this.values.clear();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        return new HashSet(this.keys);
    }

    public List<K> keys() {
        return Collections.unmodifiableList(this.keys);
    }

    @Override // java.util.Map
    public Collection<V> values() {
        return Collections.unmodifiableList(this.values);
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> hashSet = new LinkedHashSet<>();
        for (int i = 0; i < size(); i++) {
            hashSet.add(MapUtil.entry(this.keys.get(i), this.values.get(i)));
        }
        return hashSet;
    }

    @Override // java.lang.Iterable
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<Map.Entry<K, V>>() { // from class: cn.hutool.core.map.TableMap.1
            private final Iterator<K> keysIter;
            private final Iterator<V> valuesIter;

            {
                this.keysIter = TableMap.this.keys.iterator();
                this.valuesIter = TableMap.this.values.iterator();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.keysIter.hasNext() && this.valuesIter.hasNext();
            }

            @Override // java.util.Iterator
            public Map.Entry<K, V> next() {
                return MapUtil.entry(this.keysIter.next(), this.valuesIter.next());
            }

            @Override // java.util.Iterator
            public void remove() {
                this.keysIter.remove();
                this.valuesIter.remove();
            }
        };
    }

    public String toString() {
        return "TableMap{keys=" + this.keys + ", values=" + this.values + '}';
    }

    @Override // java.util.Map
    public void forEach(BiConsumer<? super K, ? super V> biConsumer) {
        for (int i = 0; i < size(); i++) {
            biConsumer.accept(this.keys.get(i), this.values.get(i));
        }
    }

    @Override // java.util.Map
    public boolean remove(Object key, Object value) {
        boolean removed = false;
        int i = 0;
        while (i < size()) {
            if (ObjUtil.equals(key, this.keys.get(i)) && ObjUtil.equals(value, this.values.get(i))) {
                removeByIndex(i);
                removed = true;
                i--;
            }
            i++;
        }
        return removed;
    }

    @Override // java.util.Map
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> biFunction) {
        for (int i = 0; i < size(); i++) {
            this.values.set(i, biFunction.apply(this.keys.get(i), this.values.get(i)));
        }
    }

    @Override // java.util.Map
    public boolean replace(K key, V oldValue, V newValue) {
        for (int i = 0; i < size(); i++) {
            if (ObjUtil.equals(key, this.keys.get(i)) && ObjUtil.equals(oldValue, this.values.get(i))) {
                this.values.set(i, newValue);
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public V replace(K key, V value) {
        V lastValue = null;
        for (int i = 0; i < size(); i++) {
            if (ObjUtil.equals(key, this.keys.get(i))) {
                lastValue = this.values.set(i, value);
            }
        }
        return lastValue;
    }

    @Override // java.util.Map
    public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        if (null == biFunction) {
            return null;
        }
        V v = null;
        int i = 0;
        while (i < size()) {
            if (ObjUtil.equals(k, this.keys.get(i))) {
                V apply = biFunction.apply(k, this.values.get(i));
                if (null != apply) {
                    v = this.values.set(i, apply);
                } else {
                    removeByIndex(i);
                    i--;
                }
            }
            i++;
        }
        return v;
    }
}

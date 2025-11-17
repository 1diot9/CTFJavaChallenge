package cn.hutool.core.collection;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/UniqueKeySet.class */
public class UniqueKeySet<K, V> extends AbstractSet<V> implements Serializable {
    private static final long serialVersionUID = 1;
    private Map<K, V> map;
    private final Function<V, K> uniqueGenerator;

    public UniqueKeySet(Function<V, K> uniqueGenerator) {
        this(false, (Function) uniqueGenerator);
    }

    public UniqueKeySet(Function<V, K> uniqueGenerator, Collection<? extends V> c) {
        this(false, (Function) uniqueGenerator, (Collection) c);
    }

    public UniqueKeySet(boolean isLinked, Function<V, K> uniqueGenerator) {
        this(MapBuilder.create(isLinked), uniqueGenerator);
    }

    public UniqueKeySet(boolean isLinked, Function<V, K> uniqueGenerator, Collection<? extends V> c) {
        this(isLinked, uniqueGenerator);
        addAll(c);
    }

    public UniqueKeySet(int initialCapacity, float loadFactor, Function<V, K> uniqueGenerator) {
        this(MapBuilder.create(new HashMap(initialCapacity, loadFactor)), uniqueGenerator);
    }

    public UniqueKeySet(MapBuilder<K, V> builder, Function<V, K> uniqueGenerator) {
        this.map = builder.build();
        this.uniqueGenerator = uniqueGenerator;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<V> iterator() {
        return this.map.values().iterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.map.size();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean contains(Object o) {
        return this.map.containsKey(this.uniqueGenerator.apply(o));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(V v) {
        return null == this.map.put(this.uniqueGenerator.apply(v), v);
    }

    public boolean addIfAbsent(V v) {
        return null == this.map.putIfAbsent(this.uniqueGenerator.apply(v), v);
    }

    public boolean addAllIfAbsent(Collection<? extends V> c) {
        boolean modified = false;
        for (V v : c) {
            if (addIfAbsent(v)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean remove(Object o) {
        return null != this.map.remove(this.uniqueGenerator.apply(o));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        this.map.clear();
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public UniqueKeySet<K, V> m124clone() {
        try {
            UniqueKeySet<K, V> newSet = (UniqueKeySet) super.clone();
            newSet.map = (Map) ObjectUtil.clone(this.map);
            return newSet;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

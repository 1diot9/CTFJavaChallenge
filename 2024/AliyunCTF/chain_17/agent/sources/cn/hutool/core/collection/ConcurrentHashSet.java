package cn.hutool.core.collection;

import cn.hutool.core.map.SafeConcurrentHashMap;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/ConcurrentHashSet.class */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 7997886765361607470L;
    private static final Boolean PRESENT = true;
    private final SafeConcurrentHashMap<E, Boolean> map;

    public ConcurrentHashSet() {
        this.map = new SafeConcurrentHashMap<>();
    }

    public ConcurrentHashSet(int initialCapacity) {
        this.map = new SafeConcurrentHashMap<>(initialCapacity);
    }

    public ConcurrentHashSet(int initialCapacity, float loadFactor) {
        this.map = new SafeConcurrentHashMap<>(initialCapacity, loadFactor);
    }

    public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this.map = new SafeConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

    public ConcurrentHashSet(Iterable<E> iter) {
        if (iter instanceof Collection) {
            Collection<E> collection = (Collection) iter;
            this.map = new SafeConcurrentHashMap<>((int) (collection.size() / 0.75f));
            addAll(collection);
        } else {
            this.map = new SafeConcurrentHashMap<>();
            for (E e : iter) {
                add(e);
            }
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
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
        return this.map.containsKey(o);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(E e) {
        return this.map.put(e, PRESENT) == null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean remove(Object o) {
        return PRESENT.equals(this.map.remove(o));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        this.map.clear();
    }
}

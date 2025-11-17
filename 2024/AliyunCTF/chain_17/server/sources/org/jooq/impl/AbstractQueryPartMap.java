package org.jooq.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractQueryPartMap.class */
public abstract class AbstractQueryPartMap<K extends QueryPart, V extends QueryPart> extends AbstractQueryPart implements QOM.UnmodifiableMap<K, V> {
    private final Map<K, V> map;

    @Override // org.jooq.QueryPartInternal
    public abstract void accept(Context<?> context);

    abstract java.util.function.Function<? super Map<K, V>, ? extends AbstractQueryPartMap<K, V>> $construct();

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractQueryPartMap() {
        this(new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractQueryPartMap(Map<K, V> map) {
        this.map = map;
    }

    @Override // java.util.Map
    public final int size() {
        return this.map.size();
    }

    @Override // java.util.Map
    public final boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.Map
    public final boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override // java.util.Map
    public final boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override // java.util.Map
    public final V get(Object key) {
        return this.map.get(key);
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        return this.map.put(key, value);
    }

    @Override // java.util.Map
    public final V remove(Object key) {
        return this.map.remove(key);
    }

    @Override // java.util.Map
    public final void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override // java.util.Map
    public final void clear() {
        this.map.clear();
    }

    @Override // java.util.Map
    public final Set<K> keySet() {
        return this.map.keySet();
    }

    @Override // java.util.Map
    public final Collection<V> values() {
        return this.map.values();
    }

    @Override // java.util.Map
    public final Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    @Override // org.jooq.impl.QOM.UnmodifiableMap
    public final QOM.UnmodifiableList<QOM.Tuple2<K, V>> $tuples() {
        return QOM.unmodifiable(Tools.map(entrySet(), e -> {
            return QOM.tuple((QueryPart) e.getKey(), (QueryPart) e.getValue());
        }));
    }
}

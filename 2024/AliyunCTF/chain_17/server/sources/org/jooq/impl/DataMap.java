package org.jooq.impl;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DataMap.class */
public final class DataMap extends AbstractMap<Object, Object> {
    Map<Object, Object> externalMap;
    final EnumSet<Tools.BooleanDataKey> internalSet = EnumSet.noneOf(Tools.BooleanDataKey.class);
    final EnumMap<Tools.SimpleDataKey, Object> internalMap = new EnumMap<>(Tools.SimpleDataKey.class);
    final Set<Map.Entry<Object, Object>> entrySet = new EntrySet();

    @Override // java.util.AbstractMap, java.util.Map
    public final int size() {
        return this.internalSet.size() + internalMap().size() + external(false).size();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean isEmpty() {
        return this.internalSet.isEmpty() && internalMap().isEmpty() && external(false).isEmpty();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsKey(Object key) {
        if (key instanceof Tools.BooleanDataKey) {
            return this.internalSet.contains(key);
        }
        return delegate(key, false).containsKey(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsValue(Object value) {
        if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue() && this.internalSet.size() > 0) {
                return true;
            }
            if (!((Boolean) value).booleanValue() && this.internalSet.size() < Tools.BooleanDataKey.values().length) {
                return true;
            }
        }
        return internalMap().containsValue(value) || external(false).containsValue(value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object get(Object key) {
        if (key instanceof Tools.BooleanDataKey) {
            if (this.internalSet.contains(key)) {
                return true;
            }
            return (Boolean) null;
        }
        return delegate(key, false).get(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object put(Object key, Object value) {
        if (key instanceof Tools.BooleanDataKey) {
            if (Boolean.TRUE.equals(value)) {
                if (this.internalSet.add((Tools.BooleanDataKey) key)) {
                    return (Boolean) null;
                }
                return Boolean.TRUE;
            }
            if (this.internalSet.remove(key)) {
                return Boolean.TRUE;
            }
            return (Boolean) null;
        }
        return delegate(key, true).put(key, value);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Object remove(Object key) {
        if (key instanceof Tools.BooleanDataKey) {
            if (this.internalSet.remove(key)) {
                return Boolean.TRUE;
            }
            return (Boolean) null;
        }
        return delegate(key, true).remove(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final void clear() {
        this.internalSet.clear();
        internalMap().clear();
        external(true).clear();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Set<Map.Entry<Object, Object>> entrySet() {
        return this.entrySet;
    }

    private final Map<Object, Object> internalMap() {
        return this.internalMap;
    }

    private final Map<Object, Object> external(boolean initialise) {
        if (this.externalMap == null) {
            if (initialise) {
                this.externalMap = new HashMap();
            } else {
                return Collections.emptyMap();
            }
        }
        return this.externalMap;
    }

    private final Map<Object, Object> delegate(Object key, boolean initialise) {
        return key instanceof Tools.SimpleDataKey ? internalMap() : external(initialise);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DataMap$EntrySet.class */
    private class EntrySet extends AbstractSet<Map.Entry<Object, Object>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public final Iterator<Map.Entry<Object, Object>> iterator() {
            return new Iterator<Map.Entry<Object, Object>>() { // from class: org.jooq.impl.DataMap.EntrySet.1
                final Iterator<Tools.BooleanDataKey> internalSetIterator;
                final Iterator<Map.Entry<Object, Object>> internalMapIterator;
                final Iterator<Map.Entry<Object, Object>> externalMapIterator;

                {
                    this.internalSetIterator = DataMap.this.internalSet.iterator();
                    this.internalMapIterator = DataMap.this.internalMap().entrySet().iterator();
                    this.externalMapIterator = DataMap.this.external(false).entrySet().iterator();
                }

                @Override // java.util.Iterator
                public final boolean hasNext() {
                    return this.internalSetIterator.hasNext() || this.internalMapIterator.hasNext() || this.externalMapIterator.hasNext();
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.Iterator
                public final Map.Entry<Object, Object> next() {
                    if (this.internalSetIterator.hasNext()) {
                        return new AbstractMap.SimpleImmutableEntry(this.internalSetIterator.next(), true);
                    }
                    if (this.internalMapIterator.hasNext()) {
                        return this.internalMapIterator.next();
                    }
                    return this.externalMapIterator.next();
                }

                @Override // java.util.Iterator
                public final void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public final int size() {
            return DataMap.this.size();
        }
    }
}

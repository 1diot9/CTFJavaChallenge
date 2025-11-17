package org.springframework.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap.class */
public class LinkedCaseInsensitiveMap<V> implements Map<String, V>, Serializable, Cloneable {
    private final LinkedHashMap<String, V> targetMap;
    private final HashMap<String, String> caseInsensitiveKeys;
    private final Locale locale;

    @Nullable
    private volatile transient Set<String> keySet;

    @Nullable
    private volatile transient Collection<V> values;

    @Nullable
    private volatile transient Set<Map.Entry<String, V>> entrySet;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object putIfAbsent(String key, @Nullable Object value) {
        return putIfAbsent2(key, (String) value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object put(String key, @Nullable Object value) {
        return put2(key, (String) value);
    }

    public LinkedCaseInsensitiveMap() {
        this((Locale) null);
    }

    public LinkedCaseInsensitiveMap(@Nullable Locale locale) {
        this(12, locale);
    }

    public LinkedCaseInsensitiveMap(int expectedSize) {
        this(expectedSize, null);
    }

    public LinkedCaseInsensitiveMap(int expectedSize, @Nullable Locale locale) {
        this.targetMap = new LinkedHashMap<String, V>((int) (expectedSize / 0.75f), 0.75f) { // from class: org.springframework.util.LinkedCaseInsensitiveMap.1
            @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
            public boolean containsKey(Object key) {
                return LinkedCaseInsensitiveMap.this.containsKey(key);
            }

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
                boolean doRemove = LinkedCaseInsensitiveMap.this.removeEldestEntry(eldest);
                if (doRemove) {
                    LinkedCaseInsensitiveMap.this.removeCaseInsensitiveKey(eldest.getKey());
                }
                return doRemove;
            }
        };
        this.caseInsensitiveKeys = CollectionUtils.newHashMap(expectedSize);
        this.locale = locale != null ? locale : Locale.getDefault();
    }

    private LinkedCaseInsensitiveMap(LinkedCaseInsensitiveMap<V> other) {
        this.targetMap = (LinkedHashMap) other.targetMap.clone();
        this.caseInsensitiveKeys = (HashMap) other.caseInsensitiveKeys.clone();
        this.locale = other.locale;
    }

    @Override // java.util.Map
    public int size() {
        return this.targetMap.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            String string = (String) key;
            if (this.caseInsensitiveKeys.containsKey(convertKey(string))) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    @Override // java.util.Map
    @Nullable
    public V get(Object key) {
        if (key instanceof String) {
            String string = (String) key;
            String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey(string));
            if (caseInsensitiveKey != null) {
                return this.targetMap.get(caseInsensitiveKey);
            }
            return null;
        }
        return null;
    }

    @Override // java.util.Map
    @Nullable
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String) {
            String string = (String) key;
            String caseInsensitiveKey = this.caseInsensitiveKeys.get(convertKey(string));
            if (caseInsensitiveKey != null) {
                return this.targetMap.get(caseInsensitiveKey);
            }
        }
        return defaultValue;
    }

    @Nullable
    /* renamed from: put, reason: avoid collision after fix types in other method */
    public V put2(String key, @Nullable V value) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        V oldKeyValue = null;
        if (oldKey != null && !oldKey.equals(key)) {
            oldKeyValue = this.targetMap.remove(oldKey);
        }
        V oldValue = this.targetMap.put(key, value);
        return oldKeyValue != null ? oldKeyValue : oldValue;
    }

    @Override // java.util.Map
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        map.forEach(this::put2);
    }

    @Nullable
    /* renamed from: putIfAbsent, reason: avoid collision after fix types in other method */
    public V putIfAbsent2(String key, @Nullable V value) {
        String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            V oldKeyValue = this.targetMap.get(oldKey);
            if (oldKeyValue != null) {
                return oldKeyValue;
            }
            key = oldKey;
        }
        return this.targetMap.putIfAbsent(key, value);
    }

    @Override // java.util.Map
    @Nullable
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
        String oldKey = this.caseInsensitiveKeys.putIfAbsent(convertKey(key), key);
        if (oldKey != null) {
            V oldKeyValue = this.targetMap.get(oldKey);
            if (oldKeyValue != null) {
                return oldKeyValue;
            }
            key = oldKey;
        }
        return this.targetMap.computeIfAbsent(key, mappingFunction);
    }

    @Override // java.util.Map
    @Nullable
    public V remove(Object key) {
        if (key instanceof String) {
            String string = (String) key;
            String caseInsensitiveKey = removeCaseInsensitiveKey(string);
            if (caseInsensitiveKey != null) {
                return this.targetMap.remove(caseInsensitiveKey);
            }
            return null;
        }
        return null;
    }

    @Override // java.util.Map
    public void clear() {
        this.caseInsensitiveKeys.clear();
        this.targetMap.clear();
    }

    @Override // java.util.Map
    public Set<String> keySet() {
        Set<String> keySet = this.keySet;
        if (keySet == null) {
            keySet = new KeySet(this.targetMap.keySet());
            this.keySet = keySet;
        }
        return keySet;
    }

    @Override // java.util.Map
    public Collection<V> values() {
        Collection<V> values = this.values;
        if (values == null) {
            values = new Values(this.targetMap.values());
            this.values = values;
        }
        return values;
    }

    @Override // java.util.Map
    public Set<Map.Entry<String, V>> entrySet() {
        Set<Map.Entry<String, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet(this.targetMap.entrySet());
            this.entrySet = entrySet;
        }
        return entrySet;
    }

    @Override // java.util.Map
    public void forEach(BiConsumer<? super String, ? super V> action) {
        this.targetMap.forEach(action);
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public LinkedCaseInsensitiveMap<V> m2640clone() {
        return new LinkedCaseInsensitiveMap<>(this);
    }

    @Override // java.util.Map
    public boolean equals(@Nullable Object other) {
        return this == other || this.targetMap.equals(other);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.targetMap.hashCode();
    }

    public String toString() {
        return this.targetMap.toString();
    }

    public Locale getLocale() {
        return this.locale;
    }

    protected String convertKey(String key) {
        return key.toLowerCase(getLocale());
    }

    protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
        return false;
    }

    @Nullable
    private String removeCaseInsensitiveKey(String key) {
        return this.caseInsensitiveKeys.remove(convertKey(key));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$KeySet.class */
    private class KeySet extends AbstractSet<String> {
        private final Set<String> delegate;

        KeySet(Set<String> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<String> iterator() {
            return new KeySetIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            return LinkedCaseInsensitiveMap.this.remove(o) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            LinkedCaseInsensitiveMap.this.clear();
        }

        @Override // java.util.Collection, java.lang.Iterable, java.util.Set
        public Spliterator<String> spliterator() {
            return this.delegate.spliterator();
        }

        @Override // java.lang.Iterable
        public void forEach(Consumer<? super String> action) {
            this.delegate.forEach(action);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$Values.class */
    private class Values extends AbstractCollection<V> {
        private final Collection<V> delegate;

        Values(Collection<V> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValuesIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            LinkedCaseInsensitiveMap.this.clear();
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Spliterator<V> spliterator() {
            return this.delegate.spliterator();
        }

        @Override // java.lang.Iterable
        public void forEach(Consumer<? super V> action) {
            this.delegate.forEach(action);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$EntrySet.class */
    private class EntrySet extends AbstractSet<Map.Entry<String, V>> {
        private final Set<Map.Entry<String, V>> delegate;

        public EntrySet(Set<Map.Entry<String, V>> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<String, V>> iterator() {
            return new EntrySetIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            if (this.delegate.remove(o)) {
                LinkedCaseInsensitiveMap.this.removeCaseInsensitiveKey((String) ((Map.Entry) o).getKey());
                return true;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.delegate.clear();
            LinkedCaseInsensitiveMap.this.caseInsensitiveKeys.clear();
        }

        @Override // java.util.Collection, java.lang.Iterable, java.util.Set
        public Spliterator<Map.Entry<String, V>> spliterator() {
            return this.delegate.spliterator();
        }

        @Override // java.lang.Iterable
        public void forEach(Consumer<? super Map.Entry<String, V>> action) {
            this.delegate.forEach(action);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$EntryIterator.class */
    public abstract class EntryIterator<T> implements Iterator<T> {
        private final Iterator<Map.Entry<String, V>> delegate;

        @Nullable
        private Map.Entry<String, V> last;

        public EntryIterator() {
            this.delegate = LinkedCaseInsensitiveMap.this.targetMap.entrySet().iterator();
        }

        protected Map.Entry<String, V> nextEntry() {
            Map.Entry<String, V> entry = this.delegate.next();
            this.last = entry;
            return entry;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.delegate.hasNext();
        }

        @Override // java.util.Iterator
        public void remove() {
            this.delegate.remove();
            if (this.last != null) {
                LinkedCaseInsensitiveMap.this.removeCaseInsensitiveKey(this.last.getKey());
                this.last = null;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$KeySetIterator.class */
    private class KeySetIterator extends LinkedCaseInsensitiveMap<V>.EntryIterator<String> {
        private KeySetIterator() {
            super();
        }

        @Override // java.util.Iterator
        public String next() {
            return nextEntry().getKey();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$ValuesIterator.class */
    private class ValuesIterator extends LinkedCaseInsensitiveMap<V>.EntryIterator<V> {
        private ValuesIterator() {
            super();
        }

        @Override // java.util.Iterator
        public V next() {
            return nextEntry().getValue();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedCaseInsensitiveMap$EntrySetIterator.class */
    private class EntrySetIterator extends LinkedCaseInsensitiveMap<V>.EntryIterator<Map.Entry<String, V>> {
        private EntrySetIterator() {
            super();
        }

        @Override // java.util.Iterator
        public Map.Entry<String, V> next() {
            return nextEntry();
        }
    }
}

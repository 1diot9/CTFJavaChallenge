package org.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UnmodifiableMultiValueMap.class */
public final class UnmodifiableMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable {
    private static final long serialVersionUID = -8697084563854098920L;
    private final MultiValueMap<K, V> delegate;

    @Nullable
    private transient Set<K> keySet;

    @Nullable
    private transient Set<Map.Entry<K, List<V>>> entrySet;

    @Nullable
    private transient Collection<List<V>> values;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object merge(Object key, Object value, BiFunction remappingFunction) {
        return merge((UnmodifiableMultiValueMap<K, V>) key, (List) value, remappingFunction);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object compute(Object key, BiFunction remappingFunction) {
        return compute((UnmodifiableMultiValueMap<K, V>) key, (BiFunction<? super UnmodifiableMultiValueMap<K, V>, ? super List<V>, ? extends List<V>>) remappingFunction);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object computeIfPresent(Object key, BiFunction remappingFunction) {
        return computeIfPresent((UnmodifiableMultiValueMap<K, V>) key, (BiFunction<? super UnmodifiableMultiValueMap<K, V>, ? super List<V>, ? extends List<V>>) remappingFunction);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object computeIfAbsent(Object key, Function mappingFunction) {
        return computeIfAbsent((UnmodifiableMultiValueMap<K, V>) key, (Function<? super UnmodifiableMultiValueMap<K, V>, ? extends List<V>>) mappingFunction);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object replace(Object key, Object value) {
        return replace((UnmodifiableMultiValueMap<K, V>) key, (List) value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ boolean replace(Object key, Object oldValue, Object newValue) {
        return replace((UnmodifiableMultiValueMap<K, V>) key, (List) oldValue, (List) newValue);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    public /* bridge */ /* synthetic */ Object putIfAbsent(Object key, Object value) {
        return putIfAbsent((UnmodifiableMultiValueMap<K, V>) key, (List) value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.Map
    @Nullable
    public /* bridge */ /* synthetic */ Object put(Object key, Object value) {
        return put((UnmodifiableMultiValueMap<K, V>) key, (List) value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public UnmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    @Override // java.util.Map
    public int size() {
        return this.delegate.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override // java.util.Map
    @Nullable
    public List<V> get(Object key) {
        List<V> result = (List) this.delegate.get(key);
        if (result != null) {
            return Collections.unmodifiableList(result);
        }
        return null;
    }

    @Override // org.springframework.util.MultiValueMap
    public V getFirst(K key) {
        return this.delegate.getFirst(key);
    }

    @Override // java.util.Map
    public List<V> getOrDefault(Object key, List<V> defaultValue) {
        List<V> result = (List) this.delegate.getOrDefault(key, defaultValue);
        if (result != defaultValue) {
            result = Collections.unmodifiableList(result);
        }
        return result;
    }

    @Override // java.util.Map
    public void forEach(BiConsumer<? super K, ? super List<V>> action) {
        this.delegate.forEach((k, vs) -> {
            action.accept(k, Collections.unmodifiableList(vs));
        });
    }

    @Override // org.springframework.util.MultiValueMap
    public Map<K, V> toSingleValueMap() {
        return this.delegate.toSingleValueMap();
    }

    @Override // java.util.Map
    public boolean equals(@Nullable Object other) {
        return this == other || this.delegate.equals(other);
    }

    @Override // java.util.Map
    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return this.delegate.toString();
    }

    @Override // java.util.Map
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.delegate.keySet());
        }
        return this.keySet;
    }

    @Override // java.util.Map
    public Set<Map.Entry<K, List<V>>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new UnmodifiableEntrySet(this.delegate.entrySet());
        }
        return this.entrySet;
    }

    @Override // java.util.Map
    public Collection<List<V>> values() {
        if (this.values == null) {
            this.values = new UnmodifiableValueCollection(this.delegate.values());
        }
        return this.values;
    }

    @Nullable
    public List<V> put(K key, List<V> value) {
        throw new UnsupportedOperationException();
    }

    public List<V> putIfAbsent(K key, List<V> value) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public List<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.util.MultiValueMap
    public void add(K key, @Nullable V value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(K key, List<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.util.MultiValueMap
    public void addAll(MultiValueMap<K, V> values) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.util.MultiValueMap
    public void addIfAbsent(K key, @Nullable V value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.util.MultiValueMap
    public void set(K key, @Nullable V value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.util.MultiValueMap
    public void setAll(Map<K, V> values) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public void replaceAll(BiFunction<? super K, ? super List<V>, ? extends List<V>> function) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    public boolean replace(K key, List<V> oldValue, List<V> newValue) {
        throw new UnsupportedOperationException();
    }

    public List<V> replace(K key, List<V> value) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public List<V> computeIfAbsent(K key, Function<? super K, ? extends List<V>> mappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public List<V> computeIfPresent(K key, BiFunction<? super K, ? super List<V>, ? extends List<V>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public List<V> compute(K key, BiFunction<? super K, ? super List<V>, ? extends List<V>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    public List<V> merge(K key, List<V> value, BiFunction<? super List<V>, ? super List<V>, ? extends List<V>> remappingFunction) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Map
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UnmodifiableMultiValueMap$UnmodifiableEntrySet.class */
    private static class UnmodifiableEntrySet<K, V> implements Set<Map.Entry<K, List<V>>>, Serializable {
        private static final long serialVersionUID = 2407578793783925203L;
        private final Set<Map.Entry<K, List<V>>> delegate;

        /* JADX WARN: Multi-variable type inference failed */
        public UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends List<? extends V>>> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.Set, java.util.Collection
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override // java.util.Set, java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return this.delegate.containsAll(c);
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Iterator<Map.Entry<K, List<V>>> iterator() {
            final Iterator<? extends Map.Entry<? extends K, ? extends List<? extends V>>> iterator = this.delegate.iterator();
            return new Iterator<Map.Entry<K, List<V>>>() { // from class: org.springframework.util.UnmodifiableMultiValueMap.UnmodifiableEntrySet.1
                @Override // java.util.Iterator
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override // java.util.Iterator
                public Map.Entry<K, List<V>> next() {
                    return new UnmodifiableEntry((Map.Entry) iterator.next());
                }
            };
        }

        @Override // java.util.Set, java.util.Collection
        public Object[] toArray() {
            Object[] result = this.delegate.toArray();
            filterArray(result);
            return result;
        }

        @Override // java.util.Set, java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            T[] tArr2 = (T[]) this.delegate.toArray(tArr);
            filterArray(tArr2);
            return tArr2;
        }

        private void filterArray(Object[] result) {
            for (int i = 0; i < result.length; i++) {
                Object obj = result[i];
                if (obj instanceof Map.Entry) {
                    Map.Entry<?, ?> entry = (Map.Entry) obj;
                    result[i] = new UnmodifiableEntry(entry);
                }
            }
        }

        @Override // java.lang.Iterable
        public void forEach(Consumer<? super Map.Entry<K, List<V>>> action) {
            this.delegate.forEach(e -> {
                action.accept(new UnmodifiableEntry(e));
            });
        }

        @Override // java.util.Collection
        public Stream<Map.Entry<K, List<V>>> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override // java.util.Collection
        public Stream<Map.Entry<K, List<V>>> parallelStream() {
            return StreamSupport.stream(spliterator(), true);
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public Spliterator<Map.Entry<K, List<V>>> spliterator() {
            return new UnmodifiableEntrySpliterator(this.delegate.spliterator());
        }

        @Override // java.util.Set, java.util.Collection
        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof Set) {
                    Set<?> that = (Set) other;
                    if (size() != that.size() || !containsAll(that)) {
                    }
                }
                return false;
            }
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean add(Map.Entry<K, List<V>> kListEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean removeIf(Predicate<? super Map.Entry<K, List<V>>> filter) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean addAll(Collection<? extends Map.Entry<K, List<V>>> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UnmodifiableMultiValueMap$UnmodifiableEntrySet$UnmodifiableEntrySpliterator.class */
        public static class UnmodifiableEntrySpliterator<K, V> implements Spliterator<Map.Entry<K, List<V>>> {
            private final Spliterator<Map.Entry<K, List<V>>> delegate;

            /* JADX WARN: Multi-variable type inference failed */
            public UnmodifiableEntrySpliterator(Spliterator<? extends Map.Entry<? extends K, ? extends List<? extends V>>> delegate) {
                this.delegate = delegate;
            }

            @Override // java.util.Spliterator
            public boolean tryAdvance(Consumer<? super Map.Entry<K, List<V>>> action) {
                return this.delegate.tryAdvance(entry -> {
                    action.accept(new UnmodifiableEntry(entry));
                });
            }

            @Override // java.util.Spliterator
            public void forEachRemaining(Consumer<? super Map.Entry<K, List<V>>> action) {
                this.delegate.forEachRemaining(entry -> {
                    action.accept(new UnmodifiableEntry(entry));
                });
            }

            @Override // java.util.Spliterator
            @Nullable
            public Spliterator<Map.Entry<K, List<V>>> trySplit() {
                Spliterator<? extends Map.Entry<? extends K, ? extends List<? extends V>>> split = this.delegate.trySplit();
                if (split != null) {
                    return new UnmodifiableEntrySpliterator(split);
                }
                return null;
            }

            @Override // java.util.Spliterator
            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            @Override // java.util.Spliterator
            public long getExactSizeIfKnown() {
                return this.delegate.getExactSizeIfKnown();
            }

            @Override // java.util.Spliterator
            public int characteristics() {
                return this.delegate.characteristics();
            }

            @Override // java.util.Spliterator
            public boolean hasCharacteristics(int characteristics) {
                return this.delegate.hasCharacteristics(characteristics);
            }

            @Override // java.util.Spliterator
            public Comparator<? super Map.Entry<K, List<V>>> getComparator() {
                return this.delegate.getComparator();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UnmodifiableMultiValueMap$UnmodifiableEntrySet$UnmodifiableEntry.class */
        public static class UnmodifiableEntry<K, V> implements Map.Entry<K, List<V>> {
            private final Map.Entry<K, List<V>> delegate;

            /* JADX WARN: Multi-variable type inference failed */
            public UnmodifiableEntry(Map.Entry<? extends K, ? extends List<? extends V>> delegate) {
                Assert.notNull(delegate, "Delegate must not be null");
                this.delegate = delegate;
            }

            @Override // java.util.Map.Entry
            public K getKey() {
                return this.delegate.getKey();
            }

            @Override // java.util.Map.Entry
            public List<V> getValue() {
                return Collections.unmodifiableList(this.delegate.getValue());
            }

            @Override // java.util.Map.Entry
            public List<V> setValue(List<V> value) {
                throw new UnsupportedOperationException();
            }

            @Override // java.util.Map.Entry
            public boolean equals(@Nullable Object other) {
                if (this != other) {
                    if (other instanceof Map.Entry) {
                        Map.Entry<?, ?> that = (Map.Entry) other;
                        if (!getKey().equals(that.getKey()) || !getValue().equals(that.getValue())) {
                        }
                    }
                    return false;
                }
                return true;
            }

            @Override // java.util.Map.Entry
            public int hashCode() {
                return this.delegate.hashCode();
            }

            public String toString() {
                return this.delegate.toString();
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UnmodifiableMultiValueMap$UnmodifiableValueCollection.class */
    private static class UnmodifiableValueCollection<V> implements Collection<List<V>>, Serializable {
        private static final long serialVersionUID = 5518377583904339588L;
        private final Collection<List<V>> delegate;

        public UnmodifiableValueCollection(Collection<List<V>> delegate) {
            this.delegate = delegate;
        }

        @Override // java.util.Collection
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.Collection
        public boolean isEmpty() {
            return this.delegate.isEmpty();
        }

        @Override // java.util.Collection
        public boolean contains(Object o) {
            return this.delegate.contains(o);
        }

        @Override // java.util.Collection
        public boolean containsAll(Collection<?> c) {
            return this.delegate.containsAll(c);
        }

        @Override // java.util.Collection
        public Object[] toArray() {
            Object[] result = this.delegate.toArray();
            filterArray(result);
            return result;
        }

        @Override // java.util.Collection
        public <T> T[] toArray(T[] tArr) {
            T[] tArr2 = (T[]) this.delegate.toArray(tArr);
            filterArray(tArr2);
            return tArr2;
        }

        private void filterArray(Object[] array) {
            for (int i = 0; i < array.length; i++) {
                Object obj = array[i];
                if (obj instanceof List) {
                    List<?> list = (List) obj;
                    array[i] = Collections.unmodifiableList(list);
                }
            }
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Iterator<List<V>> iterator() {
            final Iterator<List<V>> iterator = this.delegate.iterator();
            return new Iterator<List<V>>() { // from class: org.springframework.util.UnmodifiableMultiValueMap.UnmodifiableValueCollection.1
                @Override // java.util.Iterator
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override // java.util.Iterator
                public List<V> next() {
                    return Collections.unmodifiableList((List) iterator.next());
                }
            };
        }

        @Override // java.lang.Iterable
        public void forEach(Consumer<? super List<V>> action) {
            this.delegate.forEach(list -> {
                action.accept(Collections.unmodifiableList(list));
            });
        }

        @Override // java.util.Collection, java.lang.Iterable
        public Spliterator<List<V>> spliterator() {
            return new UnmodifiableValueSpliterator(this.delegate.spliterator());
        }

        @Override // java.util.Collection
        public Stream<List<V>> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override // java.util.Collection
        public Stream<List<V>> parallelStream() {
            return StreamSupport.stream(spliterator(), true);
        }

        @Override // java.util.Collection
        public boolean equals(@Nullable Object other) {
            return this == other || this.delegate.equals(other);
        }

        @Override // java.util.Collection
        public int hashCode() {
            return this.delegate.hashCode();
        }

        public String toString() {
            return this.delegate.toString();
        }

        @Override // java.util.Collection
        public boolean add(List<V> ts) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean addAll(Collection<? extends List<V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public boolean removeIf(Predicate<? super List<V>> filter) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public void clear() {
            throw new UnsupportedOperationException();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/UnmodifiableMultiValueMap$UnmodifiableValueCollection$UnmodifiableValueSpliterator.class */
        public static class UnmodifiableValueSpliterator<T> implements Spliterator<List<T>> {
            private final Spliterator<List<T>> delegate;

            public UnmodifiableValueSpliterator(Spliterator<List<T>> delegate) {
                this.delegate = delegate;
            }

            @Override // java.util.Spliterator
            public boolean tryAdvance(Consumer<? super List<T>> action) {
                return this.delegate.tryAdvance(l -> {
                    action.accept(Collections.unmodifiableList(l));
                });
            }

            @Override // java.util.Spliterator
            public void forEachRemaining(Consumer<? super List<T>> action) {
                this.delegate.forEachRemaining(l -> {
                    action.accept(Collections.unmodifiableList(l));
                });
            }

            @Override // java.util.Spliterator
            @Nullable
            public Spliterator<List<T>> trySplit() {
                Spliterator<List<T>> split = this.delegate.trySplit();
                if (split != null) {
                    return new UnmodifiableValueSpliterator(split);
                }
                return null;
            }

            @Override // java.util.Spliterator
            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            @Override // java.util.Spliterator
            public long getExactSizeIfKnown() {
                return this.delegate.getExactSizeIfKnown();
            }

            @Override // java.util.Spliterator
            public int characteristics() {
                return this.delegate.characteristics();
            }

            @Override // java.util.Spliterator
            public boolean hasCharacteristics(int characteristics) {
                return this.delegate.hasCharacteristics(characteristics);
            }

            @Override // java.util.Spliterator
            public Comparator<? super List<T>> getComparator() {
                return this.delegate.getComparator();
            }
        }
    }
}

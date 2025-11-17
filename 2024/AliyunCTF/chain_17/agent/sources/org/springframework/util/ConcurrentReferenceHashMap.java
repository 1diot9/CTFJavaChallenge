package org.springframework.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import jdk.internal.vm.annotation.IntrinsicCandidate;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap.class */
public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final ReferenceType DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;
    private static final int MAXIMUM_CONCURRENCY_LEVEL = 65536;
    private static final int MAXIMUM_SEGMENT_SIZE = 1073741824;
    private final ConcurrentReferenceHashMap<K, V>.Segment[] segments;
    private final float loadFactor;
    private final ReferenceType referenceType;
    private final int shift;

    @Nullable
    private volatile Set<Map.Entry<K, V>> entrySet;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$Entries.class */
    public interface Entries<V> {
        void add(@Nullable V value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$Reference.class */
    public interface Reference<K, V> {
        @Nullable
        Entry<K, V> get();

        int getHash();

        @Nullable
        Reference<K, V> getNext();

        void release();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$ReferenceType.class */
    public enum ReferenceType {
        SOFT,
        WEAK
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$Restructure.class */
    public enum Restructure {
        WHEN_NECESSARY,
        NEVER
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$TaskOption.class */
    public enum TaskOption {
        RESTRUCTURE_BEFORE,
        RESTRUCTURE_AFTER,
        SKIP_IF_EMPTY,
        RESIZE
    }

    public ConcurrentReferenceHashMap() {
        this(16, 0.75f, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, int concurrencyLevel) {
        this(initialCapacity, 0.75f, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, ReferenceType referenceType) {
        this(initialCapacity, 0.75f, 16, referenceType);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this(initialCapacity, loadFactor, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType referenceType) {
        Assert.isTrue(initialCapacity >= 0, "Initial capacity must not be negative");
        Assert.isTrue(loadFactor > 0.0f, "Load factor must be positive");
        Assert.isTrue(concurrencyLevel > 0, "Concurrency level must be positive");
        Assert.notNull(referenceType, "Reference type must not be null");
        this.loadFactor = loadFactor;
        this.shift = calculateShift(concurrencyLevel, 65536);
        int size = 1 << this.shift;
        this.referenceType = referenceType;
        int roundedUpSegmentCapacity = (int) (((initialCapacity + size) - 1) / size);
        int initialSize = 1 << calculateShift(roundedUpSegmentCapacity, 1073741824);
        ConcurrentReferenceHashMap<K, V>.Segment[] segments = (Segment[]) Array.newInstance((Class<?>) Segment.class, size);
        int resizeThreshold = (int) (initialSize * getLoadFactor());
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new Segment(initialSize, resizeThreshold);
        }
        this.segments = segments;
    }

    protected final float getLoadFactor() {
        return this.loadFactor;
    }

    protected final int getSegmentsSize() {
        return this.segments.length;
    }

    protected final ConcurrentReferenceHashMap<K, V>.Segment getSegment(int index) {
        return this.segments[index];
    }

    protected ConcurrentReferenceHashMap<K, V>.ReferenceManager createReferenceManager() {
        return new ReferenceManager();
    }

    protected int getHash(@Nullable Object o) {
        int hash = o != null ? o.hashCode() : 0;
        int hash2 = hash + ((hash << 15) ^ (-12931));
        int hash3 = hash2 ^ (hash2 >>> 10);
        int hash4 = hash3 + (hash3 << 3);
        int hash5 = hash4 ^ (hash4 >>> 6);
        int hash6 = hash5 + (hash5 << 2) + (hash5 << 14);
        return hash6 ^ (hash6 >>> 16);
    }

    @Override // java.util.AbstractMap, java.util.Map
    @Nullable
    public V get(@Nullable Object key) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = ref != null ? ref.get() : null;
        if (entry != null) {
            return entry.getValue();
        }
        return null;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    @Nullable
    public V getOrDefault(@Nullable Object key, @Nullable V defaultValue) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = ref != null ? ref.get() : null;
        return entry != null ? entry.getValue() : defaultValue;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(@Nullable Object key) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = ref != null ? ref.get() : null;
        return entry != null && ObjectUtils.nullSafeEquals(entry.getKey(), key);
    }

    @Nullable
    protected final Reference<K, V> getReference(@Nullable Object key, Restructure restructure) {
        int hash = getHash(key);
        return getSegmentForHash(hash).getReference(key, hash, restructure);
    }

    @Override // java.util.AbstractMap, java.util.Map
    @Nullable
    public V put(@Nullable K key, @Nullable V value) {
        return put(key, value, true);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    @Nullable
    public V putIfAbsent(@Nullable K key, @Nullable V value) {
        return put(key, value, false);
    }

    @Nullable
    private V put(@Nullable K k, @Nullable final V v, final boolean z) {
        return (V) doTask(k, new ConcurrentReferenceHashMap<K, V>.Task<V>(new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.RESIZE}) { // from class: org.springframework.util.ConcurrentReferenceHashMap.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.springframework.util.ConcurrentReferenceHashMap.Task
            @Nullable
            protected V execute(@Nullable Reference<K, V> reference, @Nullable Entry<K, V> entry, @Nullable Entries<V> entries) {
                if (entry != 0) {
                    V v2 = (V) entry.getValue();
                    if (z) {
                        entry.setValue(v);
                    }
                    return v2;
                }
                Assert.state(entries != 0, "No entries segment");
                entries.add(v);
                return null;
            }
        });
    }

    @Override // java.util.AbstractMap, java.util.Map
    @Nullable
    public V remove(@Nullable Object obj) {
        return (V) doTask(obj, new ConcurrentReferenceHashMap<K, V>.Task<V>(TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY) { // from class: org.springframework.util.ConcurrentReferenceHashMap.2
            @Override // org.springframework.util.ConcurrentReferenceHashMap.Task
            @Nullable
            protected V execute(@Nullable Reference<K, V> ref, @Nullable Entry<K, V> entry) {
                if (entry != null) {
                    if (ref != null) {
                        ref.release();
                    }
                    return ((Entry) entry).value;
                }
                return null;
            }
        });
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(@Nullable Object obj, @Nullable final Object obj2) {
        return Boolean.TRUE.equals((Boolean) doTask(obj, new ConcurrentReferenceHashMap<K, V>.Task<Boolean>(new TaskOption[]{TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY}) { // from class: org.springframework.util.ConcurrentReferenceHashMap.3
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // org.springframework.util.ConcurrentReferenceHashMap.Task
            public Boolean execute(@Nullable Reference<K, V> ref, @Nullable Entry<K, V> entry) {
                if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), obj2)) {
                    if (ref != null) {
                        ref.release();
                    }
                    return true;
                }
                return false;
            }
        }));
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(@Nullable K k, @Nullable final V v, @Nullable final V v2) {
        return Boolean.TRUE.equals((Boolean) doTask(k, new ConcurrentReferenceHashMap<K, V>.Task<Boolean>(new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY}) { // from class: org.springframework.util.ConcurrentReferenceHashMap.4
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.springframework.util.ConcurrentReferenceHashMap.Task
            public Boolean execute(@Nullable Reference<K, V> ref, @Nullable Entry<K, V> entry) {
                if (entry != 0 && ObjectUtils.nullSafeEquals(entry.getValue(), v)) {
                    entry.setValue(v2);
                    return true;
                }
                return false;
            }
        }));
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    @Nullable
    public V replace(@Nullable K k, @Nullable final V v) {
        return (V) doTask(k, new ConcurrentReferenceHashMap<K, V>.Task<V>(new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY}) { // from class: org.springframework.util.ConcurrentReferenceHashMap.5
            /* JADX WARN: Multi-variable type inference failed */
            @Override // org.springframework.util.ConcurrentReferenceHashMap.Task
            @Nullable
            protected V execute(@Nullable Reference<K, V> reference, @Nullable Entry<K, V> entry) {
                if (entry != 0) {
                    V v2 = (V) entry.getValue();
                    entry.setValue(v);
                    return v2;
                }
                return null;
            }
        });
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        for (ConcurrentReferenceHashMap<K, V>.Segment segment : this.segments) {
            segment.clear();
        }
    }

    public void purgeUnreferencedEntries() {
        for (ConcurrentReferenceHashMap<K, V>.Segment segment : this.segments) {
            segment.restructureIfNecessary(false);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        int size = 0;
        for (ConcurrentReferenceHashMap<K, V>.Segment segment : this.segments) {
            size += segment.getCount();
        }
        return size;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        for (ConcurrentReferenceHashMap<K, V>.Segment segment : this.segments) {
            if (segment.getCount() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }

    @Nullable
    private <T> T doTask(@Nullable Object obj, ConcurrentReferenceHashMap<K, V>.Task<T> task) {
        int hash = getHash(obj);
        return (T) getSegmentForHash(hash).doTask(hash, obj, task);
    }

    private ConcurrentReferenceHashMap<K, V>.Segment getSegmentForHash(int hash) {
        return this.segments[(hash >>> (32 - this.shift)) & (this.segments.length - 1)];
    }

    protected static int calculateShift(int minimumValue, int maximumValue) {
        int shift = 0;
        int value = 1;
        while (value < minimumValue && value < maximumValue) {
            value <<= 1;
            shift++;
        }
        return shift;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$Segment.class */
    public final class Segment extends ReentrantLock {
        private final ConcurrentReferenceHashMap<K, V>.ReferenceManager referenceManager;
        private final int initialSize;
        private volatile Reference<K, V>[] references;
        private final AtomicInteger count = new AtomicInteger();
        private int resizeThreshold;

        public Segment(int initialSize, int resizeThreshold) {
            this.referenceManager = ConcurrentReferenceHashMap.this.createReferenceManager();
            this.initialSize = initialSize;
            this.references = createReferenceArray(initialSize);
            this.resizeThreshold = resizeThreshold;
        }

        @Nullable
        public Reference<K, V> getReference(@Nullable Object key, int hash, Restructure restructure) {
            if (restructure == Restructure.WHEN_NECESSARY) {
                restructureIfNecessary(false);
            }
            if (this.count.get() == 0) {
                return null;
            }
            Reference<K, V>[] references = this.references;
            int index = getIndex(hash, references);
            Reference<K, V> head = references[index];
            return findInChain(head, key, hash);
        }

        @Nullable
        public <T> T doTask(final int hash, @Nullable final Object key, final ConcurrentReferenceHashMap<K, V>.Task<T> task) {
            boolean resize = task.hasOption(TaskOption.RESIZE);
            if (task.hasOption(TaskOption.RESTRUCTURE_BEFORE)) {
                restructureIfNecessary(resize);
            }
            if (task.hasOption(TaskOption.SKIP_IF_EMPTY) && this.count.get() == 0) {
                return task.execute(null, null, null);
            }
            lock();
            try {
                int index = getIndex(hash, this.references);
                Reference<K, V> head = this.references[index];
                Reference<K, V> ref = findInChain(head, key, hash);
                Entry<K, V> entry = ref != null ? ref.get() : null;
                Entries<V> entries = value -> {
                    Entry<K, V> newEntry = new Entry<>(key, value);
                    Reference<K, V> newReference = this.referenceManager.createReference(newEntry, hash, head);
                    this.references[index] = newReference;
                    this.count.incrementAndGet();
                };
                T execute = task.execute(ref, entry, entries);
                unlock();
                if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                    restructureIfNecessary(resize);
                }
                return execute;
            } catch (Throwable th) {
                unlock();
                if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                    restructureIfNecessary(resize);
                }
                throw th;
            }
        }

        public void clear() {
            if (this.count.get() == 0) {
                return;
            }
            lock();
            try {
                this.references = createReferenceArray(this.initialSize);
                this.resizeThreshold = (int) (this.references.length * ConcurrentReferenceHashMap.this.getLoadFactor());
                this.count.set(0);
            } finally {
                unlock();
            }
        }

        void restructureIfNecessary(boolean allowResize) {
            int currCount = this.count.get();
            boolean needsResize = allowResize && currCount > 0 && currCount >= this.resizeThreshold;
            Reference<K, V> ref = this.referenceManager.pollForPurge();
            if (ref != null || needsResize) {
                restructure(allowResize, ref);
            }
        }

        private void restructure(boolean allowResize, @Nullable Reference<K, V> ref) {
            Entry<K, V> entry;
            lock();
            try {
                int expectedCount = this.count.get();
                Set<Reference<K, V>> toPurge = Collections.emptySet();
                if (ref != null) {
                    toPurge = new HashSet<>();
                    while (ref != null) {
                        toPurge.add(ref);
                        ref = this.referenceManager.pollForPurge();
                    }
                }
                int expectedCount2 = expectedCount - toPurge.size();
                boolean needsResize = expectedCount2 > 0 && expectedCount2 >= this.resizeThreshold;
                boolean resizing = false;
                int restructureSize = this.references.length;
                if (allowResize && needsResize && restructureSize < 1073741824) {
                    restructureSize <<= 1;
                    resizing = true;
                }
                int newCount = 0;
                if (resizing) {
                    Reference<K, V>[] restructured = createReferenceArray(restructureSize);
                    for (Reference<K, V> reference : this.references) {
                        for (Reference<K, V> ref2 = reference; ref2 != null; ref2 = ref2.getNext()) {
                            if (!toPurge.contains(ref2) && (entry = ref2.get()) != null) {
                                int index = getIndex(ref2.getHash(), restructured);
                                restructured[index] = this.referenceManager.createReference(entry, ref2.getHash(), restructured[index]);
                                newCount++;
                            }
                        }
                    }
                    this.references = restructured;
                    this.resizeThreshold = (int) (this.references.length * ConcurrentReferenceHashMap.this.getLoadFactor());
                } else {
                    for (int i = 0; i < this.references.length; i++) {
                        Reference<K, V> purgedRef = null;
                        for (Reference<K, V> ref3 = this.references[i]; ref3 != null; ref3 = ref3.getNext()) {
                            if (!toPurge.contains(ref3)) {
                                Entry<K, V> entry2 = ref3.get();
                                if (entry2 != null) {
                                    purgedRef = this.referenceManager.createReference(entry2, ref3.getHash(), purgedRef);
                                }
                                newCount++;
                            }
                        }
                        this.references[i] = purgedRef;
                    }
                }
                this.count.set(Math.max(newCount, 0));
                unlock();
            } catch (Throwable th) {
                unlock();
                throw th;
            }
        }

        @Nullable
        private Reference<K, V> findInChain(Reference<K, V> ref, @Nullable Object key, int hash) {
            Entry<K, V> entry;
            Reference<K, V> reference = ref;
            while (true) {
                Reference<K, V> currRef = reference;
                if (currRef != null) {
                    if (currRef.getHash() == hash && (entry = currRef.get()) != null) {
                        K entryKey = entry.getKey();
                        if (ObjectUtils.nullSafeEquals(entryKey, key)) {
                            return currRef;
                        }
                    }
                    reference = currRef.getNext();
                } else {
                    return null;
                }
            }
        }

        private Reference<K, V>[] createReferenceArray(int size) {
            return new Reference[size];
        }

        private int getIndex(int hash, Reference<K, V>[] references) {
            return hash & (references.length - 1);
        }

        public int getSize() {
            return this.references.length;
        }

        public int getCount() {
            return this.count.get();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$Entry.class */
    public static final class Entry<K, V> implements Map.Entry<K, V> {

        @Nullable
        private final K key;

        @Nullable
        private volatile V value;

        public Entry(@Nullable K key, @Nullable V value) {
            this.key = key;
            this.value = value;
        }

        @Override // java.util.Map.Entry
        @Nullable
        public K getKey() {
            return this.key;
        }

        @Override // java.util.Map.Entry
        @Nullable
        public V getValue() {
            return this.value;
        }

        @Override // java.util.Map.Entry
        @Nullable
        public V setValue(@Nullable V value) {
            V previous = this.value;
            this.value = value;
            return previous;
        }

        @Override // java.util.Map.Entry
        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof Map.Entry) {
                    Map.Entry<?, ?> that = (Map.Entry) other;
                    if (!ObjectUtils.nullSafeEquals(getKey(), that.getKey()) || !ObjectUtils.nullSafeEquals(getValue(), that.getValue())) {
                    }
                }
                return false;
            }
            return true;
        }

        @Override // java.util.Map.Entry
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.key) ^ ObjectUtils.nullSafeHashCode(this.value);
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$Task.class */
    public abstract class Task<T> {
        private final EnumSet<TaskOption> options;

        public Task(TaskOption... options) {
            this.options = options.length == 0 ? EnumSet.noneOf(TaskOption.class) : EnumSet.of(options[0], options);
        }

        public boolean hasOption(TaskOption option) {
            return this.options.contains(option);
        }

        @Nullable
        protected T execute(@Nullable Reference<K, V> ref, @Nullable Entry<K, V> entry, @Nullable Entries<V> entries) {
            return execute(ref, entry);
        }

        @Nullable
        protected T execute(@Nullable Reference<K, V> ref, @Nullable Entry<K, V> entry) {
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$EntrySet.class */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(@Nullable Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) o;
                Reference<K, V> ref = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), Restructure.NEVER);
                Entry<K, V> otherEntry = ref != null ? ref.get() : null;
                if (otherEntry != null) {
                    return ObjectUtils.nullSafeEquals(entry.getValue(), otherEntry.getValue());
                }
                return false;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry) o;
                return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$EntryIterator.class */
    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private int segmentIndex;
        private int referenceIndex;

        @Nullable
        private Reference<K, V>[] references;

        @Nullable
        private Reference<K, V> reference;

        @Nullable
        private Entry<K, V> next;

        @Nullable
        private Entry<K, V> last;

        public EntryIterator() {
            moveToNextSegment();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            getNextIfNecessary();
            return this.next != null;
        }

        @Override // java.util.Iterator
        public Entry<K, V> next() {
            getNextIfNecessary();
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.last = this.next;
            this.next = null;
            return this.last;
        }

        private void getNextIfNecessary() {
            while (this.next == null) {
                moveToNextReference();
                if (this.reference == null) {
                    return;
                } else {
                    this.next = this.reference.get();
                }
            }
        }

        private void moveToNextReference() {
            if (this.reference != null) {
                this.reference = this.reference.getNext();
            }
            while (this.reference == null && this.references != null) {
                if (this.referenceIndex >= this.references.length) {
                    moveToNextSegment();
                    this.referenceIndex = 0;
                } else {
                    this.reference = this.references[this.referenceIndex];
                    this.referenceIndex++;
                }
            }
        }

        private void moveToNextSegment() {
            this.reference = null;
            this.references = null;
            if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
                this.references = ((Segment) ConcurrentReferenceHashMap.this.segments[this.segmentIndex]).references;
                this.segmentIndex++;
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            Assert.state(this.last != null, "No element to remove");
            ConcurrentReferenceHashMap.this.remove(this.last.getKey());
            this.last = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$ReferenceManager.class */
    public class ReferenceManager {
        private final ReferenceQueue<Entry<K, V>> queue = new ReferenceQueue<>();

        protected ReferenceManager() {
        }

        public Reference<K, V> createReference(Entry<K, V> entry, int hash, @Nullable Reference<K, V> next) {
            if (ConcurrentReferenceHashMap.this.referenceType == ReferenceType.WEAK) {
                return new WeakEntryReference(entry, hash, next, this.queue);
            }
            return new SoftEntryReference(entry, hash, next, this.queue);
        }

        @Nullable
        public Reference<K, V> pollForPurge() {
            return (Reference) this.queue.poll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$SoftEntryReference.class */
    public static final class SoftEntryReference<K, V> extends SoftReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;

        @Nullable
        private final Reference<K, V> nextReference;

        @Override // java.lang.ref.SoftReference, java.lang.ref.Reference, org.springframework.util.ConcurrentReferenceHashMap.Reference
        public /* bridge */ /* synthetic */ Entry get() {
            return (Entry) super.get();
        }

        public SoftEntryReference(Entry<K, V> entry, int hash, @Nullable Reference<K, V> next, ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        @Override // org.springframework.util.ConcurrentReferenceHashMap.Reference
        public int getHash() {
            return this.hash;
        }

        @Override // org.springframework.util.ConcurrentReferenceHashMap.Reference
        @Nullable
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        @Override // org.springframework.util.ConcurrentReferenceHashMap.Reference
        public void release() {
            enqueue();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentReferenceHashMap$WeakEntryReference.class */
    public static final class WeakEntryReference<K, V> extends WeakReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;

        @Nullable
        private final Reference<K, V> nextReference;

        @Override // java.lang.ref.Reference, org.springframework.util.ConcurrentReferenceHashMap.Reference
        @IntrinsicCandidate
        public /* bridge */ /* synthetic */ Entry get() {
            return (Entry) super.get();
        }

        public WeakEntryReference(Entry<K, V> entry, int hash, @Nullable Reference<K, V> next, ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        @Override // org.springframework.util.ConcurrentReferenceHashMap.Reference
        public int getHash() {
            return this.hash;
        }

        @Override // org.springframework.util.ConcurrentReferenceHashMap.Reference
        @Nullable
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        @Override // org.springframework.util.ConcurrentReferenceHashMap.Reference
        public void release() {
            enqueue();
        }
    }
}

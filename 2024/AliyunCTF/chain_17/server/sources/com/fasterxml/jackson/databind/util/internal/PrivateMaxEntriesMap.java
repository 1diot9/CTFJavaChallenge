package com.fasterxml.jackson.databind.util.internal;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap.class */
public final class PrivateMaxEntriesMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    static final long MAXIMUM_CAPACITY = 9223372034707292160L;
    static final int READ_BUFFER_THRESHOLD = 4;
    static final int READ_BUFFER_DRAIN_THRESHOLD = 8;
    static final int READ_BUFFER_SIZE = 16;
    static final int READ_BUFFER_INDEX_MASK = 15;
    static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
    final ConcurrentMap<K, Node<K, V>> data;
    final int concurrencyLevel;
    final AtomicLong capacity;
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    static final long serialVersionUID = 1;
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    static final int NUMBER_OF_READ_BUFFERS = Math.min(4, ceilingNextPowerOfTwo(NCPU));
    static final int READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;
    final Lock evictionLock = new ReentrantLock();
    final AtomicLong weightedSize = new AtomicLong();
    final LinkedDeque<Node<K, V>> evictionDeque = new LinkedDeque<>();
    final Queue<Runnable> writeBuffer = new ConcurrentLinkedQueue();
    final AtomicReference<DrainStatus> drainStatus = new AtomicReference<>(DrainStatus.IDLE);
    final long[] readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
    final AtomicLongArray readBufferWriteCount = new AtomicLongArray(NUMBER_OF_READ_BUFFERS);
    final AtomicLongArray readBufferDrainAtWriteCount = new AtomicLongArray(NUMBER_OF_READ_BUFFERS);
    final AtomicReferenceArray<Node<K, V>> readBuffers = new AtomicReferenceArray<>(NUMBER_OF_READ_BUFFERS * 16);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$DrainStatus.class */
    public enum DrainStatus {
        IDLE { // from class: com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus.1
            @Override // com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus
            boolean shouldDrainBuffers(boolean delayable) {
                return !delayable;
            }
        },
        REQUIRED { // from class: com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus.2
            @Override // com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus
            boolean shouldDrainBuffers(boolean delayable) {
                return true;
            }
        },
        PROCESSING { // from class: com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus.3
            @Override // com.fasterxml.jackson.databind.util.internal.PrivateMaxEntriesMap.DrainStatus
            boolean shouldDrainBuffers(boolean delayable) {
                return false;
            }
        };

        abstract boolean shouldDrainBuffers(boolean z);
    }

    static int ceilingNextPowerOfTwo(int x) {
        return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
    }

    private static int readBufferIndex(int bufferIndex, int entryIndex) {
        return (16 * bufferIndex) + entryIndex;
    }

    PrivateMaxEntriesMap(Builder<K, V> builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.capacity = new AtomicLong(Math.min(builder.capacity, MAXIMUM_CAPACITY));
        this.data = new ConcurrentHashMap(builder.initialCapacity, 0.75f, this.concurrencyLevel);
    }

    static void checkNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public long capacity() {
        return this.capacity.get();
    }

    public void setCapacity(long capacity) {
        checkArgument(capacity >= 0);
        this.evictionLock.lock();
        try {
            this.capacity.lazySet(Math.min(capacity, MAXIMUM_CAPACITY));
            drainBuffers();
            evict();
        } finally {
            this.evictionLock.unlock();
        }
    }

    boolean hasOverflowed() {
        return this.weightedSize.get() > this.capacity.get();
    }

    void evict() {
        Node<K, V> node;
        while (hasOverflowed() && (node = this.evictionDeque.poll()) != null) {
            this.data.remove(node.key, node);
            makeDead(node);
        }
    }

    void afterRead(Node<K, V> node) {
        int bufferIndex = readBufferIndex();
        long writeCount = recordRead(bufferIndex, node);
        drainOnReadIfNeeded(bufferIndex, writeCount);
    }

    static int readBufferIndex() {
        return ((int) Thread.currentThread().getId()) & READ_BUFFERS_MASK;
    }

    long recordRead(int bufferIndex, Node<K, V> node) {
        long writeCount = this.readBufferWriteCount.get(bufferIndex);
        this.readBufferWriteCount.lazySet(bufferIndex, writeCount + serialVersionUID);
        int index = (int) (writeCount & 15);
        this.readBuffers.lazySet(readBufferIndex(bufferIndex, index), node);
        return writeCount;
    }

    void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
        long pending = writeCount - this.readBufferDrainAtWriteCount.get(bufferIndex);
        boolean delayable = pending < 4;
        DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(delayable)) {
            tryToDrainBuffers();
        }
    }

    void afterWrite(Runnable task) {
        this.writeBuffer.add(task);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        tryToDrainBuffers();
    }

    void tryToDrainBuffers() {
        if (this.evictionLock.tryLock()) {
            try {
                this.drainStatus.lazySet(DrainStatus.PROCESSING);
                drainBuffers();
            } finally {
                this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
                this.evictionLock.unlock();
            }
        }
    }

    void drainBuffers() {
        drainReadBuffers();
        drainWriteBuffer();
    }

    void drainReadBuffers() {
        int start = (int) Thread.currentThread().getId();
        int end = start + NUMBER_OF_READ_BUFFERS;
        for (int i = start; i < end; i++) {
            drainReadBuffer(i & READ_BUFFERS_MASK);
        }
    }

    void drainReadBuffer(int bufferIndex) {
        long writeCount = this.readBufferWriteCount.get(bufferIndex);
        for (int i = 0; i < 8; i++) {
            int index = (int) (this.readBufferReadCount[bufferIndex] & 15);
            int arrayIndex = readBufferIndex(bufferIndex, index);
            Node<K, V> node = this.readBuffers.get(arrayIndex);
            if (node == null) {
                break;
            }
            this.readBuffers.lazySet(arrayIndex, null);
            applyRead(node);
            long[] jArr = this.readBufferReadCount;
            jArr[bufferIndex] = jArr[bufferIndex] + serialVersionUID;
        }
        this.readBufferDrainAtWriteCount.lazySet(bufferIndex, writeCount);
    }

    void applyRead(Node<K, V> node) {
        if (this.evictionDeque.contains((Linked<?>) node)) {
            this.evictionDeque.moveToBack(node);
        }
    }

    void drainWriteBuffer() {
        Runnable task;
        for (int i = 0; i < 16 && (task = this.writeBuffer.poll()) != null; i++) {
            task.run();
        }
    }

    boolean tryToRetire(Node<K, V> node, WeightedValue<V> expect) {
        if (expect.isAlive()) {
            return node.compareAndSet(expect, new WeightedValue(expect.value, -expect.weight));
        }
        return false;
    }

    void makeRetired(Node<K, V> node) {
        WeightedValue weightedValue;
        do {
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive()) {
                return;
            }
        } while (!node.compareAndSet(weightedValue, new WeightedValue(weightedValue.value, -weightedValue.weight)));
    }

    void makeDead(Node<K, V> node) {
        WeightedValue weightedValue;
        do {
            weightedValue = (WeightedValue) node.get();
        } while (!node.compareAndSet(weightedValue, new WeightedValue(weightedValue.value, 0)));
        this.weightedSize.lazySet(this.weightedSize.get() - Math.abs(weightedValue.weight));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$AddTask.class */
    public final class AddTask implements Runnable {
        final Node<K, V> node;
        final int weight;

        AddTask(Node<K, V> node, int weight) {
            this.weight = weight;
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + this.weight);
            if (((WeightedValue) this.node.get()).isAlive()) {
                PrivateMaxEntriesMap.this.evictionDeque.add((LinkedDeque<Node<K, V>>) this.node);
                PrivateMaxEntriesMap.this.evict();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$RemovalTask.class */
    public final class RemovalTask implements Runnable {
        final Node<K, V> node;

        RemovalTask(Node<K, V> node) {
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            PrivateMaxEntriesMap.this.evictionDeque.remove((LinkedDeque<Node<K, V>>) this.node);
            PrivateMaxEntriesMap.this.makeDead(this.node);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$UpdateTask.class */
    public final class UpdateTask implements Runnable {
        final int weightDifference;
        final Node<K, V> node;

        UpdateTask(Node<K, V> node, int weightDifference) {
            this.weightDifference = weightDifference;
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + this.weightDifference);
            PrivateMaxEntriesMap.this.applyRead(this.node);
            PrivateMaxEntriesMap.this.evict();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        return this.data.size();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        this.evictionLock.lock();
        while (true) {
            try {
                Node<K, V> node = this.evictionDeque.poll();
                if (node == null) {
                    break;
                }
                this.data.remove(node.key, node);
                makeDead(node);
            } finally {
                this.evictionLock.unlock();
            }
        }
        for (int i = 0; i < this.readBuffers.length(); i++) {
            this.readBuffers.lazySet(i, null);
        }
        while (true) {
            Runnable task = this.writeBuffer.poll();
            if (task != null) {
                task.run();
            } else {
                return;
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        return this.data.containsKey(key);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object value) {
        checkNotNull(value);
        for (Node<K, V> node : this.data.values()) {
            if (node.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object key) {
        Node<K, V> node = this.data.get(key);
        if (node == null) {
            return null;
        }
        afterRead(node);
        return node.getValue();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K key, V value) {
        return put(key, value, false);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K key, V value) {
        return put(key, value, true);
    }

    V put(K key, V value, boolean onlyIfAbsent) {
        WeightedValue weightedValue;
        checkNotNull(key);
        checkNotNull(value);
        WeightedValue weightedValue2 = new WeightedValue(value, 1);
        Node<K, V> node = new Node<>(key, weightedValue2);
        while (true) {
            Node<K, V> prior = this.data.putIfAbsent(node.key, node);
            if (prior == null) {
                afterWrite(new AddTask(node, 1));
                return null;
            }
            if (onlyIfAbsent) {
                afterRead(prior);
                return prior.getValue();
            }
            do {
                weightedValue = (WeightedValue) prior.get();
                if (!weightedValue.isAlive()) {
                    break;
                }
            } while (!prior.compareAndSet(weightedValue, weightedValue2));
            int weightedDifference = 1 - weightedValue.weight;
            if (weightedDifference == 0) {
                afterRead(prior);
            } else {
                afterWrite(new UpdateTask(prior, weightedDifference));
            }
            return weightedValue.value;
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object key) {
        Node<K, V> node = this.data.remove(key);
        if (node == null) {
            return null;
        }
        makeRetired(node);
        afterWrite(new RemovalTask(node));
        return node.getValue();
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(Object obj, Object obj2) {
        Node<K, V> node = this.data.get(obj);
        if (node == null || obj2 == null) {
            return false;
        }
        WeightedValue<V> weightedValue = (WeightedValue) node.get();
        while (weightedValue.contains(obj2)) {
            if (tryToRetire(node, weightedValue)) {
                if (this.data.remove(obj, node)) {
                    afterWrite(new RemovalTask(node));
                    return true;
                }
                return false;
            }
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive()) {
                return false;
            }
        }
        return false;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V replace(K key, V value) {
        WeightedValue weightedValue;
        checkNotNull(key);
        checkNotNull(value);
        WeightedValue weightedValue2 = new WeightedValue(value, 1);
        Node<K, V> node = this.data.get(key);
        if (node == null) {
            return null;
        }
        do {
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive()) {
                return null;
            }
        } while (!node.compareAndSet(weightedValue, weightedValue2));
        int weightedDifference = 1 - weightedValue.weight;
        if (weightedDifference == 0) {
            afterRead(node);
        } else {
            afterWrite(new UpdateTask(node, weightedDifference));
        }
        return weightedValue.value;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(K key, V oldValue, V newValue) {
        WeightedValue weightedValue;
        checkNotNull(key);
        checkNotNull(oldValue);
        checkNotNull(newValue);
        WeightedValue weightedValue2 = new WeightedValue(newValue, 1);
        Node<K, V> node = this.data.get(key);
        if (node == null) {
            return false;
        }
        do {
            weightedValue = (WeightedValue) node.get();
            if (!weightedValue.isAlive() || !weightedValue.contains(oldValue)) {
                return false;
            }
        } while (!node.compareAndSet(weightedValue, weightedValue2));
        int weightedDifference = 1 - weightedValue.weight;
        if (weightedDifference == 0) {
            afterRead(node);
            return true;
        }
        afterWrite(new UpdateTask(node, weightedDifference));
        return true;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        if (ks != null) {
            return ks;
        }
        KeySet keySet = new KeySet();
        this.keySet = keySet;
        return keySet;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        Collection<V> vs = this.values;
        if (vs != null) {
            return vs;
        }
        Values values = new Values();
        this.values = values;
        return values;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = this.entrySet;
        if (es != null) {
            return es;
        }
        EntrySet entrySet = new EntrySet();
        this.entrySet = entrySet;
        return entrySet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$WeightedValue.class */
    public static final class WeightedValue<V> {
        final int weight;
        final V value;

        WeightedValue(V value, int weight) {
            this.weight = weight;
            this.value = value;
        }

        boolean contains(Object o) {
            return o == this.value || this.value.equals(o);
        }

        boolean isAlive() {
            return this.weight > 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$Node.class */
    public static final class Node<K, V> extends AtomicReference<WeightedValue<V>> implements Linked<Node<K, V>> {
        final K key;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, WeightedValue<V> weightedValue) {
            super(weightedValue);
            this.key = key;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public Node<K, V> getPrevious() {
            return this.prev;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public void setPrevious(Node<K, V> prev) {
            this.prev = prev;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public Node<K, V> getNext() {
            return this.next;
        }

        @Override // com.fasterxml.jackson.databind.util.internal.Linked
        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        V getValue() {
            return ((WeightedValue) get()).value;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$KeySet.class */
    final class KeySet extends AbstractSet<K> {
        final PrivateMaxEntriesMap<K, V> map;

        KeySet() {
            this.map = PrivateMaxEntriesMap.this;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.map.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.map.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return PrivateMaxEntriesMap.this.containsKey(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            return this.map.remove(obj) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public Object[] toArray() {
            return this.map.data.keySet().toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public <T> T[] toArray(T[] tArr) {
            return (T[]) this.map.data.keySet().toArray(tArr);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$KeyIterator.class */
    final class KeyIterator implements Iterator<K> {
        final Iterator<K> iterator;
        K current;

        KeyIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.keySet().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public K next() {
            this.current = this.iterator.next();
            return this.current;
        }

        @Override // java.util.Iterator
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current);
            this.current = null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$Values.class */
    final class Values extends AbstractCollection<V> {
        Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return PrivateMaxEntriesMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            PrivateMaxEntriesMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return PrivateMaxEntriesMap.this.containsValue(o);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$ValueIterator.class */
    final class ValueIterator implements Iterator<V> {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;

        ValueIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public V next() {
            this.current = this.iterator.next();
            return this.current.getValue();
        }

        @Override // java.util.Iterator
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$EntrySet.class */
    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        final PrivateMaxEntriesMap<K, V> map;

        EntrySet() {
            this.map = PrivateMaxEntriesMap.this;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.map.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            this.map.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            Node<K, V> node = this.map.data.get(entry.getKey());
            return node != null && node.getValue().equals(entry.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean add(Map.Entry<K, V> entry) {
            throw new UnsupportedOperationException("ConcurrentLinkedHashMap does not allow add to be called on entrySet()");
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry) obj;
            return this.map.remove(entry.getKey(), entry.getValue());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$EntryIterator.class */
    final class EntryIterator implements Iterator<Map.Entry<K, V>> {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;

        EntryIterator() {
            this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Iterator
        public Map.Entry<K, V> next() {
            this.current = this.iterator.next();
            return new WriteThroughEntry(this.current);
        }

        @Override // java.util.Iterator
        public void remove() {
            PrivateMaxEntriesMap.checkState(this.current != null);
            PrivateMaxEntriesMap.this.remove(this.current.key);
            this.current = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$WriteThroughEntry.class */
    public final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V> {
        static final long serialVersionUID = 1;

        WriteThroughEntry(Node<K, V> node) {
            super(node.key, node.getValue());
        }

        @Override // java.util.AbstractMap.SimpleEntry, java.util.Map.Entry
        public V setValue(V v) {
            PrivateMaxEntriesMap.this.put(getKey(), v);
            return (V) super.setValue(v);
        }

        Object writeReplace() {
            return new AbstractMap.SimpleEntry(this);
        }
    }

    Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$SerializationProxy.class */
    static final class SerializationProxy<K, V> implements Serializable {
        final int concurrencyLevel;
        final Map<K, V> data;
        final long capacity;
        static final long serialVersionUID = 1;

        SerializationProxy(PrivateMaxEntriesMap<K, V> map) {
            this.concurrencyLevel = map.concurrencyLevel;
            this.data = new HashMap(map);
            this.capacity = map.capacity.get();
        }

        Object readResolve() {
            PrivateMaxEntriesMap<K, V> map = new Builder().maximumCapacity(this.capacity).build();
            map.putAll(this.data);
            return map;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/internal/PrivateMaxEntriesMap$Builder.class */
    public static final class Builder<K, V> {
        static final int DEFAULT_CONCURRENCY_LEVEL = 16;
        static final int DEFAULT_INITIAL_CAPACITY = 16;
        long capacity = -1;
        int initialCapacity = 16;
        int concurrencyLevel = 16;

        public Builder<K, V> initialCapacity(int initialCapacity) {
            PrivateMaxEntriesMap.checkArgument(initialCapacity >= 0);
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder<K, V> maximumCapacity(long capacity) {
            PrivateMaxEntriesMap.checkArgument(capacity >= 0);
            this.capacity = capacity;
            return this;
        }

        public Builder<K, V> concurrencyLevel(int concurrencyLevel) {
            PrivateMaxEntriesMap.checkArgument(concurrencyLevel > 0);
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        public PrivateMaxEntriesMap<K, V> build() {
            PrivateMaxEntriesMap.checkState(this.capacity >= 0);
            return new PrivateMaxEntriesMap<>(this);
        }
    }
}

package org.springframework.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache.class */
public final class ConcurrentLruCache<K, V> {
    private final int capacity;
    private final AtomicInteger currentSize;
    private final ConcurrentMap<K, Node<K, V>> cache;
    private final Function<K, V> generator;
    private final ReadOperations<K, V> readOperations;
    private final WriteOperations writeOperations;
    private final Lock evictionLock;
    private final EvictionQueue<K, V> evictionQueue;
    private final AtomicReference<DrainStatus> drainStatus;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$CacheEntryState.class */
    public enum CacheEntryState {
        ACTIVE,
        PENDING_REMOVAL,
        REMOVED
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$DrainStatus.class */
    public enum DrainStatus {
        IDLE { // from class: org.springframework.util.ConcurrentLruCache.DrainStatus.1
            @Override // org.springframework.util.ConcurrentLruCache.DrainStatus
            boolean shouldDrainBuffers(boolean delayable) {
                return !delayable;
            }
        },
        REQUIRED { // from class: org.springframework.util.ConcurrentLruCache.DrainStatus.2
            @Override // org.springframework.util.ConcurrentLruCache.DrainStatus
            boolean shouldDrainBuffers(boolean delayable) {
                return true;
            }
        },
        PROCESSING { // from class: org.springframework.util.ConcurrentLruCache.DrainStatus.3
            @Override // org.springframework.util.ConcurrentLruCache.DrainStatus
            boolean shouldDrainBuffers(boolean delayable) {
                return false;
            }
        };

        abstract boolean shouldDrainBuffers(boolean delayable);
    }

    public ConcurrentLruCache(int capacity, Function<K, V> generator) {
        this(capacity, generator, 16);
    }

    private ConcurrentLruCache(int capacity, Function<K, V> generator, int concurrencyLevel) {
        this.currentSize = new AtomicInteger();
        this.evictionLock = new ReentrantLock();
        this.evictionQueue = new EvictionQueue<>();
        this.drainStatus = new AtomicReference<>(DrainStatus.IDLE);
        Assert.isTrue(capacity >= 0, "Capacity must be >= 0");
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap(16, 0.75f, concurrencyLevel);
        this.generator = generator;
        this.readOperations = new ReadOperations<>(this.evictionQueue);
        this.writeOperations = new WriteOperations();
    }

    public V get(K key) {
        if (this.capacity == 0) {
            return this.generator.apply(key);
        }
        Node<K, V> node = this.cache.get(key);
        if (node == null) {
            V value = this.generator.apply(key);
            put(key, value);
            return value;
        }
        processRead(node);
        return node.getValue();
    }

    private void put(K key, V value) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(value, "value must not be null");
        CacheEntry<V> cacheEntry = new CacheEntry<>(value, CacheEntryState.ACTIVE);
        Node<K, V> node = new Node<>(key, cacheEntry);
        Node<K, V> prior = this.cache.putIfAbsent(node.key, node);
        if (prior == null) {
            processWrite(new AddTask(node));
        } else {
            processRead(prior);
        }
    }

    private void processRead(Node<K, V> node) {
        boolean drainRequested = this.readOperations.recordRead(node);
        DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(drainRequested)) {
            drainOperations();
        }
    }

    private void processWrite(Runnable task) {
        this.writeOperations.add(task);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        drainOperations();
    }

    private void drainOperations() {
        if (this.evictionLock.tryLock()) {
            try {
                this.drainStatus.lazySet(DrainStatus.PROCESSING);
                this.readOperations.drain();
                this.writeOperations.drain();
            } finally {
                this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
                this.evictionLock.unlock();
            }
        }
    }

    public int capacity() {
        return this.capacity;
    }

    @Deprecated(since = "6.0")
    public int sizeLimit() {
        return this.capacity;
    }

    public int size() {
        return this.cache.size();
    }

    public void clear() {
        this.evictionLock.lock();
        while (true) {
            try {
                Node<K, V> node = this.evictionQueue.poll();
                if (node != null) {
                    this.cache.remove(node.key, node);
                    markAsRemoved(node);
                } else {
                    this.readOperations.clear();
                    this.writeOperations.drainAll();
                    return;
                }
            } finally {
                this.evictionLock.unlock();
            }
        }
    }

    private void markAsRemoved(Node<K, V> node) {
        CacheEntry cacheEntry;
        do {
            cacheEntry = (CacheEntry) node.get();
        } while (!node.compareAndSet(cacheEntry, new CacheEntry(cacheEntry.value, CacheEntryState.REMOVED)));
        this.currentSize.lazySet(this.currentSize.get() - 1);
    }

    public boolean contains(K key) {
        return this.cache.containsKey(key);
    }

    @Nullable
    public boolean remove(K key) {
        Node<K, V> node = this.cache.remove(key);
        if (node == null) {
            return false;
        }
        markForRemoval(node);
        processWrite(new RemovalTask(node));
        return true;
    }

    private void markForRemoval(Node<K, V> node) {
        CacheEntry cacheEntry;
        do {
            cacheEntry = (CacheEntry) node.get();
            if (!cacheEntry.isActive()) {
                return;
            }
        } while (!node.compareAndSet(cacheEntry, new CacheEntry(cacheEntry.value, CacheEntryState.PENDING_REMOVAL)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$AddTask.class */
    public final class AddTask implements Runnable {
        final Node<K, V> node;

        AddTask(Node<K, V> node) {
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            ConcurrentLruCache.this.currentSize.lazySet(ConcurrentLruCache.this.currentSize.get() + 1);
            if (((CacheEntry) this.node.get()).isActive()) {
                ConcurrentLruCache.this.evictionQueue.add(this.node);
                evictEntries();
            }
        }

        private void evictEntries() {
            Node<K, V> node;
            while (ConcurrentLruCache.this.currentSize.get() > ConcurrentLruCache.this.capacity && (node = ConcurrentLruCache.this.evictionQueue.poll()) != null) {
                ConcurrentLruCache.this.cache.remove(node.key, node);
                ConcurrentLruCache.this.markAsRemoved(node);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$RemovalTask.class */
    private final class RemovalTask implements Runnable {
        final Node<K, V> node;

        RemovalTask(Node<K, V> node) {
            this.node = node;
        }

        @Override // java.lang.Runnable
        public void run() {
            ConcurrentLruCache.this.evictionQueue.remove(this.node);
            ConcurrentLruCache.this.markAsRemoved(this.node);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$CacheEntry.class */
    public static final class CacheEntry<V> extends Record {
        private final V value;
        private final CacheEntryState state;

        private CacheEntry(V value, CacheEntryState state) {
            this.value = value;
            this.state = state;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, CacheEntry.class), CacheEntry.class, "value;state", "FIELD:Lorg/springframework/util/ConcurrentLruCache$CacheEntry;->value:Ljava/lang/Object;", "FIELD:Lorg/springframework/util/ConcurrentLruCache$CacheEntry;->state:Lorg/springframework/util/ConcurrentLruCache$CacheEntryState;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, CacheEntry.class), CacheEntry.class, "value;state", "FIELD:Lorg/springframework/util/ConcurrentLruCache$CacheEntry;->value:Ljava/lang/Object;", "FIELD:Lorg/springframework/util/ConcurrentLruCache$CacheEntry;->state:Lorg/springframework/util/ConcurrentLruCache$CacheEntryState;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, CacheEntry.class, Object.class), CacheEntry.class, "value;state", "FIELD:Lorg/springframework/util/ConcurrentLruCache$CacheEntry;->value:Ljava/lang/Object;", "FIELD:Lorg/springframework/util/ConcurrentLruCache$CacheEntry;->state:Lorg/springframework/util/ConcurrentLruCache$CacheEntryState;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public V value() {
            return this.value;
        }

        public CacheEntryState state() {
            return this.state;
        }

        boolean isActive() {
            return this.state == CacheEntryState.ACTIVE;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$ReadOperations.class */
    public static final class ReadOperations<K, V> {
        private static final int BUFFER_COUNT = detectNumberOfBuffers();
        private static final int BUFFERS_MASK = BUFFER_COUNT - 1;
        private static final int MAX_PENDING_OPERATIONS = 32;
        private static final int MAX_DRAIN_COUNT = 64;
        private static final int BUFFER_SIZE = 128;
        private static final int BUFFER_INDEX_MASK = 127;
        private final AtomicLongArray recordedCount = new AtomicLongArray(BUFFER_COUNT);
        private final long[] readCount = new long[BUFFER_COUNT];
        private final AtomicLongArray processedCount = new AtomicLongArray(BUFFER_COUNT);
        private final AtomicReferenceArray<Node<K, V>>[] buffers = new AtomicReferenceArray[BUFFER_COUNT];
        private final EvictionQueue<K, V> evictionQueue;

        private static int detectNumberOfBuffers() {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            int nextPowerOfTwo = 1 << (32 - Integer.numberOfLeadingZeros(availableProcessors - 1));
            return Math.min(4, nextPowerOfTwo);
        }

        ReadOperations(EvictionQueue<K, V> evictionQueue) {
            this.evictionQueue = evictionQueue;
            for (int i = 0; i < BUFFER_COUNT; i++) {
                this.buffers[i] = new AtomicReferenceArray<>(128);
            }
        }

        private static int getBufferIndex() {
            return ((int) Thread.currentThread().getId()) & BUFFERS_MASK;
        }

        boolean recordRead(Node<K, V> node) {
            int bufferIndex = getBufferIndex();
            long writeCount = this.recordedCount.get(bufferIndex);
            this.recordedCount.lazySet(bufferIndex, writeCount + 1);
            int index = (int) (writeCount & 127);
            this.buffers[bufferIndex].lazySet(index, node);
            long pending = writeCount - this.processedCount.get(bufferIndex);
            return pending < 32;
        }

        void drain() {
            int start = (int) Thread.currentThread().getId();
            int end = start + BUFFER_COUNT;
            for (int i = start; i < end; i++) {
                drainReadBuffer(i & BUFFERS_MASK);
            }
        }

        void clear() {
            for (int i = 0; i < BUFFER_COUNT; i++) {
                AtomicReferenceArray<Node<K, V>> buffer = this.buffers[i];
                for (int j = 0; j < 128; j++) {
                    buffer.lazySet(j, null);
                }
            }
        }

        private void drainReadBuffer(int bufferIndex) {
            int index;
            AtomicReferenceArray<Node<K, V>> buffer;
            Node<K, V> node;
            long writeCount = this.recordedCount.get(bufferIndex);
            for (int i = 0; i < 64 && (node = (buffer = this.buffers[bufferIndex]).get((index = (int) (this.readCount[bufferIndex] & 127)))) != null; i++) {
                buffer.lazySet(index, null);
                this.evictionQueue.moveToBack(node);
                long[] jArr = this.readCount;
                jArr[bufferIndex] = jArr[bufferIndex] + 1;
            }
            this.processedCount.lazySet(bufferIndex, writeCount);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$WriteOperations.class */
    public static final class WriteOperations {
        private static final int DRAIN_THRESHOLD = 16;
        private final Queue<Runnable> operations = new ConcurrentLinkedQueue();

        private WriteOperations() {
        }

        public void add(Runnable task) {
            this.operations.add(task);
        }

        public void drain() {
            Runnable task;
            for (int i = 0; i < 16 && (task = this.operations.poll()) != null; i++) {
                task.run();
            }
        }

        public void drainAll() {
            while (true) {
                Runnable task = this.operations.poll();
                if (task != null) {
                    task.run();
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$Node.class */
    public static final class Node<K, V> extends AtomicReference<CacheEntry<V>> {
        final K key;

        @Nullable
        Node<K, V> prev;

        @Nullable
        Node<K, V> next;

        Node(K key, CacheEntry<V> cacheEntry) {
            super(cacheEntry);
            this.key = key;
        }

        @Nullable
        public Node<K, V> getPrevious() {
            return this.prev;
        }

        public void setPrevious(@Nullable Node<K, V> prev) {
            this.prev = prev;
        }

        @Nullable
        public Node<K, V> getNext() {
            return this.next;
        }

        public void setNext(@Nullable Node<K, V> next) {
            this.next = next;
        }

        V getValue() {
            return ((CacheEntry) get()).value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/ConcurrentLruCache$EvictionQueue.class */
    public static final class EvictionQueue<K, V> {

        @Nullable
        Node<K, V> first;

        @Nullable
        Node<K, V> last;

        private EvictionQueue() {
        }

        @Nullable
        Node<K, V> poll() {
            if (this.first == null) {
                return null;
            }
            Node<K, V> f = this.first;
            Node<K, V> next = f.getNext();
            f.setNext(null);
            this.first = next;
            if (next == null) {
                this.last = null;
            } else {
                next.setPrevious(null);
            }
            return f;
        }

        void add(Node<K, V> e) {
            if (contains(e)) {
                return;
            }
            linkLast(e);
        }

        private boolean contains(Node<K, V> e) {
            return (e.getPrevious() == null && e.getNext() == null && e != this.first) ? false : true;
        }

        private void linkLast(final Node<K, V> e) {
            Node<K, V> l = this.last;
            this.last = e;
            if (l == null) {
                this.first = e;
            } else {
                l.setNext(e);
                e.setPrevious(l);
            }
        }

        private void unlink(Node<K, V> e) {
            Node<K, V> prev = e.getPrevious();
            Node<K, V> next = e.getNext();
            if (prev == null) {
                this.first = next;
            } else {
                prev.setNext(next);
                e.setPrevious(null);
            }
            if (next == null) {
                this.last = prev;
            } else {
                next.setPrevious(prev);
                e.setNext(null);
            }
        }

        void moveToBack(Node<K, V> e) {
            if (contains(e) && e != this.last) {
                unlink(e);
                linkLast(e);
            }
        }

        void remove(Node<K, V> e) {
            if (contains(e)) {
                unlink(e);
            }
        }
    }
}

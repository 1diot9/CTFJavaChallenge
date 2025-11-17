package org.h2.mvstore.cache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.h2.mvstore.DataUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/cache/CacheLongKeyLIRS.class */
public class CacheLongKeyLIRS<V> {
    private long maxMemory;
    private final Segment<V>[] segments;
    private final int segmentCount;
    private final int segmentShift;
    private final int segmentMask;
    private final int stackMoveDistance;
    private final int nonResidentQueueSize;
    private final int nonResidentQueueSizeHigh;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/cache/CacheLongKeyLIRS$Config.class */
    public static class Config {
        public long maxMemory = 1;
        public int segmentCount = 16;
        public int stackMoveDistance = 32;
        public final int nonResidentQueueSize = 3;
        public final int nonResidentQueueSizeHigh = 12;
    }

    public CacheLongKeyLIRS(Config config) {
        setMaxMemory(config.maxMemory);
        config.getClass();
        this.nonResidentQueueSize = 3;
        config.getClass();
        this.nonResidentQueueSizeHigh = 12;
        DataUtils.checkArgument(Integer.bitCount(config.segmentCount) == 1, "The segment count must be a power of 2, is {0}", Integer.valueOf(config.segmentCount));
        this.segmentCount = config.segmentCount;
        this.segmentMask = this.segmentCount - 1;
        this.stackMoveDistance = config.stackMoveDistance;
        this.segments = new Segment[this.segmentCount];
        clear();
        this.segmentShift = 32 - Integer.bitCount(this.segmentMask);
    }

    public void clear() {
        long maxItemSize = getMaxItemSize();
        for (int i = 0; i < this.segmentCount; i++) {
            this.segments[i] = new Segment<>(maxItemSize, this.stackMoveDistance, 8, this.nonResidentQueueSize, this.nonResidentQueueSizeHigh);
        }
    }

    public long getMaxItemSize() {
        return Math.max(1L, this.maxMemory / this.segmentCount);
    }

    private Entry<V> find(long j) {
        int hash = getHash(j);
        return getSegment(hash).find(j, hash);
    }

    public boolean containsKey(long j) {
        Entry<V> find = find(j);
        return (find == null || find.value == null) ? false : true;
    }

    public V peek(long j) {
        Entry<V> find = find(j);
        if (find == null) {
            return null;
        }
        return find.getValue();
    }

    public V put(long j, V v) {
        return put(j, v, sizeOf(v));
    }

    public V put(long j, V v, long j2) {
        V put;
        if (v == null) {
            throw DataUtils.newIllegalArgumentException("The value may not be null", new Object[0]);
        }
        int hash = getHash(j);
        int segmentIndex = getSegmentIndex(hash);
        Segment<V> segment = this.segments[segmentIndex];
        synchronized (segment) {
            put = resizeIfNeeded(segment, segmentIndex).put(j, hash, v, j2);
        }
        return put;
    }

    private Segment<V> resizeIfNeeded(Segment<V> segment, int i) {
        int newMapLen = segment.getNewMapLen();
        if (newMapLen == 0) {
            return segment;
        }
        if (segment == this.segments[i]) {
            segment = new Segment<>(segment, newMapLen);
            this.segments[i] = segment;
        }
        return segment;
    }

    protected long sizeOf(V v) {
        return 16L;
    }

    public V remove(long j) {
        V remove;
        int hash = getHash(j);
        int segmentIndex = getSegmentIndex(hash);
        Segment<V> segment = this.segments[segmentIndex];
        synchronized (segment) {
            remove = resizeIfNeeded(segment, segmentIndex).remove(j, hash);
        }
        return remove;
    }

    public long getMemory(long j) {
        Entry<V> find = find(j);
        if (find == null) {
            return 0L;
        }
        return find.getMemory();
    }

    public static int getMemoryOverhead() {
        return 112;
    }

    public V get(long j) {
        int hash = getHash(j);
        Segment<V> segment = getSegment(hash);
        return segment.get(segment.find(j, hash));
    }

    private Segment<V> getSegment(int i) {
        return this.segments[getSegmentIndex(i)];
    }

    private int getSegmentIndex(int i) {
        return (i >>> this.segmentShift) & this.segmentMask;
    }

    static int getHash(long j) {
        int i = (int) ((j >>> 32) ^ j);
        int i2 = ((i >>> 16) ^ i) * 73244475;
        int i3 = ((i2 >>> 16) ^ i2) * 73244475;
        return (i3 >>> 16) ^ i3;
    }

    public long getUsedMemory() {
        long j = 0;
        for (Segment<V> segment : this.segments) {
            j += segment.usedMemory;
        }
        return j;
    }

    public void setMaxMemory(long j) {
        DataUtils.checkArgument(j > 0, "Max memory must be larger than 0, is {0}", Long.valueOf(j));
        this.maxMemory = j;
        if (this.segments != null) {
            long length = 1 + (j / this.segments.length);
            for (Segment<V> segment : this.segments) {
                segment.setMaxMemory(length);
            }
        }
    }

    public long getMaxMemory() {
        return this.maxMemory;
    }

    public synchronized Set<Map.Entry<Long, V>> entrySet() {
        return getMap().entrySet();
    }

    public Set<Long> keySet() {
        HashSet hashSet = new HashSet();
        for (Segment<V> segment : this.segments) {
            hashSet.addAll(segment.keySet());
        }
        return hashSet;
    }

    public int sizeNonResident() {
        int i = 0;
        for (Segment<V> segment : this.segments) {
            i += segment.queue2Size;
        }
        return i;
    }

    public int sizeMapArray() {
        int i = 0;
        for (Segment<V> segment : this.segments) {
            i += segment.entries.length;
        }
        return i;
    }

    public int sizeHot() {
        int i = 0;
        for (Segment<V> segment : this.segments) {
            i += (segment.mapSize - segment.queueSize) - segment.queue2Size;
        }
        return i;
    }

    public long getHits() {
        long j = 0;
        for (Segment<V> segment : this.segments) {
            j += segment.hits;
        }
        return j;
    }

    public long getMisses() {
        int i = 0;
        for (Segment<V> segment : this.segments) {
            i = (int) (i + segment.misses);
        }
        return i;
    }

    public int size() {
        int i = 0;
        for (Segment<V> segment : this.segments) {
            i += segment.mapSize - segment.queue2Size;
        }
        return i;
    }

    public List<Long> keys(boolean z, boolean z2) {
        ArrayList arrayList = new ArrayList();
        for (Segment<V> segment : this.segments) {
            arrayList.addAll(segment.keys(z, z2));
        }
        return arrayList;
    }

    public List<V> values() {
        ArrayList arrayList = new ArrayList();
        Iterator<Long> it = keySet().iterator();
        while (it.hasNext()) {
            V peek = peek(it.next().longValue());
            if (peek != null) {
                arrayList.add(peek);
            }
        }
        return arrayList;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsValue(V v) {
        return getMap().containsValue(v);
    }

    public Map<Long, V> getMap() {
        HashMap hashMap = new HashMap();
        Iterator<Long> it = keySet().iterator();
        while (it.hasNext()) {
            long longValue = it.next().longValue();
            V peek = peek(longValue);
            if (peek != null) {
                hashMap.put(Long.valueOf(longValue), peek);
            }
        }
        return hashMap;
    }

    public void putAll(Map<Long, ? extends V> map) {
        for (Map.Entry<Long, ? extends V> entry : map.entrySet()) {
            put(entry.getKey().longValue(), entry.getValue());
        }
    }

    public void trimNonResidentQueue() {
        for (Segment<V> segment : this.segments) {
            synchronized (segment) {
                segment.trimNonResidentQueue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/cache/CacheLongKeyLIRS$Segment.class */
    public static class Segment<V> {
        int mapSize;
        int queueSize;
        int queue2Size;
        long hits;
        long misses;
        final Entry<V>[] entries;
        long usedMemory;
        private final int stackMoveDistance;
        private long maxMemory;
        private final int mask;
        private final int nonResidentQueueSize;
        private final int nonResidentQueueSizeHigh;
        private final Entry<V> stack;
        private int stackSize;
        private final Entry<V> queue;
        private final Entry<V> queue2;
        private int stackMoveCounter;

        Segment(long j, int i, int i2, int i3, int i4) {
            setMaxMemory(j);
            this.stackMoveDistance = i;
            this.nonResidentQueueSize = i3;
            this.nonResidentQueueSizeHigh = i4;
            this.mask = i2 - 1;
            this.stack = new Entry<>();
            Entry<V> entry = this.stack;
            Entry<V> entry2 = this.stack;
            Entry<V> entry3 = this.stack;
            entry2.stackNext = entry3;
            entry.stackPrev = entry3;
            this.queue = new Entry<>();
            Entry<V> entry4 = this.queue;
            Entry<V> entry5 = this.queue;
            Entry<V> entry6 = this.queue;
            entry5.queueNext = entry6;
            entry4.queuePrev = entry6;
            this.queue2 = new Entry<>();
            Entry<V> entry7 = this.queue2;
            Entry<V> entry8 = this.queue2;
            Entry<V> entry9 = this.queue2;
            entry8.queueNext = entry9;
            entry7.queuePrev = entry9;
            this.entries = new Entry[i2];
        }

        Segment(Segment<V> segment, int i) {
            this(segment.maxMemory, segment.stackMoveDistance, i, segment.nonResidentQueueSize, segment.nonResidentQueueSizeHigh);
            this.hits = segment.hits;
            this.misses = segment.misses;
            Entry<V> entry = segment.stack.stackPrev;
            while (true) {
                Entry<V> entry2 = entry;
                if (entry2 == segment.stack) {
                    break;
                }
                Entry<V> entry3 = new Entry<>(entry2);
                addToMap(entry3);
                addToStack(entry3);
                entry = entry2.stackPrev;
            }
            Entry<V> entry4 = segment.queue.queuePrev;
            while (true) {
                Entry<V> entry5 = entry4;
                if (entry5 == segment.queue) {
                    break;
                }
                Entry<V> find = find(entry5.key, CacheLongKeyLIRS.getHash(entry5.key));
                if (find == null) {
                    find = new Entry<>(entry5);
                    addToMap(find);
                }
                addToQueue(this.queue, find);
                entry4 = entry5.queuePrev;
            }
            Entry<V> entry6 = segment.queue2.queuePrev;
            while (true) {
                Entry<V> entry7 = entry6;
                if (entry7 != segment.queue2) {
                    Entry<V> find2 = find(entry7.key, CacheLongKeyLIRS.getHash(entry7.key));
                    if (find2 == null) {
                        find2 = new Entry<>(entry7);
                        addToMap(find2);
                    }
                    addToQueue(this.queue2, find2);
                    entry6 = entry7.queuePrev;
                } else {
                    return;
                }
            }
        }

        int getNewMapLen() {
            int i = this.mask + 1;
            if (i * 3 < this.mapSize * 4 && i < 268435456) {
                return i * 2;
            }
            if (i > 32 && i / 8 > this.mapSize) {
                return i / 2;
            }
            return 0;
        }

        private void addToMap(Entry<V> entry) {
            int hash = CacheLongKeyLIRS.getHash(entry.key) & this.mask;
            entry.mapNext = this.entries[hash];
            this.entries[hash] = entry;
            this.usedMemory += entry.getMemory();
            this.mapSize++;
        }

        synchronized V get(Entry<V> entry) {
            V value = entry == null ? null : entry.getValue();
            if (value == null) {
                this.misses++;
            } else {
                access(entry);
                this.hits++;
            }
            return value;
        }

        private void access(Entry<V> entry) {
            if (entry.isHot()) {
                if (entry != this.stack.stackNext && entry.stackNext != null && this.stackMoveCounter - entry.topMove > this.stackMoveDistance) {
                    boolean z = entry == this.stack.stackPrev;
                    removeFromStack(entry);
                    if (z) {
                        pruneStack();
                    }
                    addToStack(entry);
                    return;
                }
                return;
            }
            V value = entry.getValue();
            if (value != null) {
                removeFromQueue(entry);
                if (entry.reference != null) {
                    entry.value = value;
                    entry.reference = null;
                    this.usedMemory += entry.memory;
                }
                if (entry.stackNext != null) {
                    removeFromStack(entry);
                    convertOldestHotToCold();
                } else {
                    addToQueue(this.queue, entry);
                }
                addToStack(entry);
                pruneStack();
            }
        }

        synchronized V put(long j, int i, V v, long j2) {
            Entry<V> find = find(j, i);
            boolean z = find != null;
            V v2 = null;
            if (z) {
                v2 = find.getValue();
                remove(j, i);
            }
            if (j2 + 112 > this.maxMemory) {
                return v2;
            }
            Entry<V> entry = new Entry<>(j, v, j2);
            int i2 = i & this.mask;
            entry.mapNext = this.entries[i2];
            this.entries[i2] = entry;
            this.usedMemory += entry.memory;
            if (this.usedMemory > this.maxMemory) {
                evict();
                if (this.stackSize > 0) {
                    addToQueue(this.queue, entry);
                }
            }
            this.mapSize++;
            addToStack(entry);
            if (z) {
                access(entry);
            }
            return v2;
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x007e  */
        /* JADX WARN: Removed duplicated region for block: B:13:0x008c  */
        /* JADX WARN: Removed duplicated region for block: B:21:0x00b9  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        synchronized V remove(long r7, int r9) {
            /*
                r6 = this;
                r0 = r9
                r1 = r6
                int r1 = r1.mask
                r0 = r0 & r1
                r10 = r0
                r0 = r6
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V>[] r0 = r0.entries
                r1 = r10
                r0 = r0[r1]
                r11 = r0
                r0 = r11
                if (r0 != 0) goto L18
                r0 = 0
                return r0
            L18:
                r0 = r11
                long r0 = r0.key
                r1 = r7
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 != 0) goto L31
                r0 = r6
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V>[] r0 = r0.entries
                r1 = r10
                r2 = r11
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r2 = r2.mapNext
                r0[r1] = r2
                goto L57
            L31:
                r0 = r11
                r12 = r0
                r0 = r11
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r0 = r0.mapNext
                r11 = r0
                r0 = r11
                if (r0 != 0) goto L43
                r0 = 0
                return r0
            L43:
                r0 = r11
                long r0 = r0.key
                r1 = r7
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 != 0) goto L31
                r0 = r12
                r1 = r11
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r1 = r1.mapNext
                r0.mapNext = r1
            L57:
                r0 = r11
                java.lang.Object r0 = r0.getValue()
                r12 = r0
                r0 = r6
                r1 = r0
                int r1 = r1.mapSize
                r2 = 1
                int r1 = r1 - r2
                r0.mapSize = r1
                r0 = r6
                r1 = r0
                long r1 = r1.usedMemory
                r2 = r11
                long r2 = r2.getMemory()
                long r1 = r1 - r2
                r0.usedMemory = r1
                r0 = r11
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r0 = r0.stackNext
                if (r0 == 0) goto L84
                r0 = r6
                r1 = r11
                r0.removeFromStack(r1)
            L84:
                r0 = r11
                boolean r0 = r0.isHot()
                if (r0 == 0) goto Lb9
                r0 = r6
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r0 = r0.queue
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r0 = r0.queueNext
                r11 = r0
                r0 = r11
                r1 = r6
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r1 = r1.queue
                if (r0 == r1) goto Lb2
                r0 = r6
                r1 = r11
                r0.removeFromQueue(r1)
                r0 = r11
                org.h2.mvstore.cache.CacheLongKeyLIRS$Entry<V> r0 = r0.stackNext
                if (r0 != 0) goto Lb2
                r0 = r6
                r1 = r11
                r0.addToStackBottom(r1)
            Lb2:
                r0 = r6
                r0.pruneStack()
                goto Lbf
            Lb9:
                r0 = r6
                r1 = r11
                r0.removeFromQueue(r1)
            Lbf:
                r0 = r12
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.cache.CacheLongKeyLIRS.Segment.remove(long, int):java.lang.Object");
        }

        private void evict() {
            do {
                evictBlock();
            } while (this.usedMemory > this.maxMemory);
        }

        private void evictBlock() {
            while (this.queueSize <= ((this.mapSize - this.queue2Size) >>> 5) && this.stackSize > 0) {
                convertOldestHotToCold();
            }
            while (this.usedMemory > this.maxMemory && this.queueSize > 0) {
                Entry<V> entry = this.queue.queuePrev;
                this.usedMemory -= entry.memory;
                removeFromQueue(entry);
                entry.reference = new WeakReference<>(entry.value);
                entry.value = null;
                addToQueue(this.queue2, entry);
                trimNonResidentQueue();
            }
        }

        void trimNonResidentQueue() {
            WeakReference<V> weakReference;
            int i = this.mapSize - this.queue2Size;
            int i2 = this.nonResidentQueueSizeHigh * i;
            int i3 = this.nonResidentQueueSize * i;
            while (this.queue2Size > i3) {
                Entry<V> entry = this.queue2.queuePrev;
                if (this.queue2Size > i2 || (weakReference = entry.reference) == null || weakReference.get() == null) {
                    remove(entry.key, CacheLongKeyLIRS.getHash(entry.key));
                } else {
                    return;
                }
            }
        }

        private void convertOldestHotToCold() {
            Entry<V> entry = this.stack.stackPrev;
            if (entry == this.stack) {
                throw new IllegalStateException();
            }
            removeFromStack(entry);
            addToQueue(this.queue, entry);
            pruneStack();
        }

        private void pruneStack() {
            while (true) {
                Entry<V> entry = this.stack.stackPrev;
                if (!entry.isHot()) {
                    removeFromStack(entry);
                } else {
                    return;
                }
            }
        }

        Entry<V> find(long j, int i) {
            Entry<V> entry;
            Entry<V> entry2 = this.entries[i & this.mask];
            while (true) {
                entry = entry2;
                if (entry == null || entry.key == j) {
                    break;
                }
                entry2 = entry.mapNext;
            }
            return entry;
        }

        private void addToStack(Entry<V> entry) {
            entry.stackPrev = this.stack;
            entry.stackNext = this.stack.stackNext;
            entry.stackNext.stackPrev = entry;
            this.stack.stackNext = entry;
            this.stackSize++;
            int i = this.stackMoveCounter;
            this.stackMoveCounter = i + 1;
            entry.topMove = i;
        }

        private void addToStackBottom(Entry<V> entry) {
            entry.stackNext = this.stack;
            entry.stackPrev = this.stack.stackPrev;
            entry.stackPrev.stackNext = entry;
            this.stack.stackPrev = entry;
            this.stackSize++;
        }

        private void removeFromStack(Entry<V> entry) {
            entry.stackPrev.stackNext = entry.stackNext;
            entry.stackNext.stackPrev = entry.stackPrev;
            entry.stackNext = null;
            entry.stackPrev = null;
            this.stackSize--;
        }

        private void addToQueue(Entry<V> entry, Entry<V> entry2) {
            entry2.queuePrev = entry;
            entry2.queueNext = entry.queueNext;
            entry2.queueNext.queuePrev = entry2;
            entry.queueNext = entry2;
            if (entry2.value != null) {
                this.queueSize++;
            } else {
                this.queue2Size++;
            }
        }

        private void removeFromQueue(Entry<V> entry) {
            entry.queuePrev.queueNext = entry.queueNext;
            entry.queueNext.queuePrev = entry.queuePrev;
            entry.queueNext = null;
            entry.queuePrev = null;
            if (entry.value != null) {
                this.queueSize--;
            } else {
                this.queue2Size--;
            }
        }

        synchronized List<Long> keys(boolean z, boolean z2) {
            ArrayList arrayList = new ArrayList();
            if (z) {
                Entry<V> entry = z2 ? this.queue2 : this.queue;
                Entry<V> entry2 = entry.queueNext;
                while (true) {
                    Entry<V> entry3 = entry2;
                    if (entry3 == entry) {
                        break;
                    }
                    arrayList.add(Long.valueOf(entry3.key));
                    entry2 = entry3.queueNext;
                }
            } else {
                Entry<V> entry4 = this.stack.stackNext;
                while (true) {
                    Entry<V> entry5 = entry4;
                    if (entry5 == this.stack) {
                        break;
                    }
                    arrayList.add(Long.valueOf(entry5.key));
                    entry4 = entry5.stackNext;
                }
            }
            return arrayList;
        }

        synchronized Set<Long> keySet() {
            HashSet hashSet = new HashSet();
            Entry<V> entry = this.stack.stackNext;
            while (true) {
                Entry<V> entry2 = entry;
                if (entry2 == this.stack) {
                    break;
                }
                hashSet.add(Long.valueOf(entry2.key));
                entry = entry2.stackNext;
            }
            Entry<V> entry3 = this.queue.queueNext;
            while (true) {
                Entry<V> entry4 = entry3;
                if (entry4 != this.queue) {
                    hashSet.add(Long.valueOf(entry4.key));
                    entry3 = entry4.queueNext;
                } else {
                    return hashSet;
                }
            }
        }

        void setMaxMemory(long j) {
            this.maxMemory = j;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/cache/CacheLongKeyLIRS$Entry.class */
    public static class Entry<V> {
        static final int TOTAL_MEMORY_OVERHEAD = 112;
        final long key;
        V value;
        WeakReference<V> reference;
        final long memory;
        int topMove;
        Entry<V> stackNext;
        Entry<V> stackPrev;
        Entry<V> queueNext;
        Entry<V> queuePrev;
        Entry<V> mapNext;

        Entry() {
            this(0L, null, 0L);
        }

        Entry(long j, V v, long j2) {
            this.key = j;
            this.memory = j2 + 112;
            this.value = v;
        }

        Entry(Entry<V> entry) {
            this.key = entry.key;
            this.memory = entry.memory;
            this.value = entry.value;
            this.reference = entry.reference;
            this.topMove = entry.topMove;
        }

        boolean isHot() {
            return this.queueNext == null;
        }

        V getValue() {
            return this.value == null ? this.reference.get() : this.value;
        }

        long getMemory() {
            if (this.value == null) {
                return 0L;
            }
            return this.memory;
        }
    }
}

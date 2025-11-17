package org.h2.mvstore;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.ObjectDataType;
import org.h2.util.MemoryEstimator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap.class */
public class MVMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    public final MVStore store;
    private final AtomicReference<RootReference<K, V>> root;
    private final int id;
    private final long createVersion;
    private final DataType<K> keyType;
    private final DataType<V> valueType;
    private final int keysPerPage;
    private final boolean singleWriter;
    private final K[] keysBuffer;
    private final V[] valuesBuffer;
    private final Object lock;
    private volatile boolean notificationRequested;
    private volatile boolean closed;
    private boolean readOnly;
    private boolean isVolatile;
    private final AtomicLong avgKeySize;
    private final AtomicLong avgValSize;
    static final /* synthetic */ boolean $assertionsDisabled;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$Decision.class */
    public enum Decision {
        ABORT,
        REMOVE,
        PUT,
        REPEAT
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$MapBuilder.class */
    public interface MapBuilder<M extends MVMap<K, V>, K, V> {
        M create(MVStore mVStore, Map<String, Object> map);

        DataType<K> getKeyType();

        DataType<V> getValueType();

        void setKeyType(DataType<? super K> dataType);

        void setValueType(DataType<? super V> dataType);
    }

    static {
        $assertionsDisabled = !MVMap.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MVMap(Map<String, Object> map, DataType<K> dataType, DataType<V> dataType2) {
        this((MVStore) map.get("store"), dataType, dataType2, DataUtils.readHexInt(map, "id", 0), DataUtils.readHexLong(map, "createVersion", 0L), new AtomicReference(), ((MVStore) map.get("store")).getKeysPerPage(), map.containsKey("singleWriter") && ((Boolean) map.get("singleWriter")).booleanValue());
        setInitialRoot(createEmptyLeaf(), this.store.getCurrentVersion());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MVMap(MVMap<K, V> mVMap) {
        this(mVMap.store, mVMap.keyType, mVMap.valueType, mVMap.id, mVMap.createVersion, new AtomicReference(mVMap.root.get()), mVMap.keysPerPage, mVMap.singleWriter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MVMap(MVStore mVStore, int i, DataType<K> dataType, DataType<V> dataType2) {
        this(mVStore, dataType, dataType2, i, 0L, new AtomicReference(), mVStore.getKeysPerPage(), false);
        setInitialRoot(createEmptyLeaf(), mVStore.getCurrentVersion());
    }

    private MVMap(MVStore mVStore, DataType<K> dataType, DataType<V> dataType2, int i, long j, AtomicReference<RootReference<K, V>> atomicReference, int i2, boolean z) {
        this.lock = new Object();
        this.store = mVStore;
        this.id = i;
        this.createVersion = j;
        this.keyType = dataType;
        this.valueType = dataType2;
        this.root = atomicReference;
        this.keysPerPage = i2;
        this.keysBuffer = z ? dataType.createStorage(i2) : null;
        this.valuesBuffer = z ? dataType2.createStorage(i2) : null;
        this.singleWriter = z;
        this.avgKeySize = dataType.isMemoryEstimationAllowed() ? new AtomicLong() : null;
        this.avgValSize = dataType2.isMemoryEstimationAllowed() ? new AtomicLong() : null;
    }

    protected MVMap<K, V> cloneIt() {
        return new MVMap<>(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getMapRootKey(int i) {
        return DataUtils.META_ROOT + Integer.toHexString(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getMapKey(int i) {
        return DataUtils.META_MAP + Integer.toHexString(i);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        DataUtils.checkArgument(v != null, "The value may not be null", new Object[0]);
        return operate(k, v, DecisionMaker.PUT);
    }

    public final K firstKey() {
        return getFirstLast(true);
    }

    public final K lastKey() {
        return getFirstLast(false);
    }

    public final K getKey(long j) {
        if (j < 0 || j >= sizeAsLong()) {
            return null;
        }
        Page<K, V> rootPage = getRootPage();
        long j2 = 0;
        while (!rootPage.isLeaf()) {
            int i = 0;
            int childPageCount = getChildPageCount(rootPage);
            while (i < childPageCount) {
                long counts = rootPage.getCounts(i);
                if (j < counts + j2) {
                    break;
                }
                j2 += counts;
                i++;
            }
            if (i == childPageCount) {
                return null;
            }
            rootPage = rootPage.getChildPage(i);
        }
        if (j >= j2 + rootPage.getKeyCount()) {
            return null;
        }
        return rootPage.getKey((int) (j - j2));
    }

    public final List<K> keyList() {
        return new AbstractList<K>() { // from class: org.h2.mvstore.MVMap.1
            @Override // java.util.AbstractList, java.util.List
            public K get(int i) {
                return (K) MVMap.this.getKey(i);
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return MVMap.this.size();
            }

            @Override // java.util.AbstractList, java.util.List
            public int indexOf(Object obj) {
                return (int) MVMap.this.getKeyIndex(obj);
            }
        };
    }

    public final long getKeyIndex(K k) {
        int binarySearch;
        Page<K, V> rootPage = getRootPage();
        if (rootPage.getTotalCount() == 0) {
            return -1L;
        }
        long j = 0;
        while (true) {
            binarySearch = rootPage.binarySearch(k);
            if (rootPage.isLeaf()) {
                break;
            }
            int i = binarySearch + 1;
            if (binarySearch < 0) {
                i = -i;
            }
            for (int i2 = 0; i2 < i; i2++) {
                j += rootPage.getCounts(i2);
            }
            rootPage = rootPage.getChildPage(i);
        }
        if (binarySearch < 0) {
            j = -j;
        }
        return j + binarySearch;
    }

    private K getFirstLast(boolean z) {
        return getFirstLast(getRootPage(), z);
    }

    private K getFirstLast(Page<K, V> page, boolean z) {
        if (page.getTotalCount() == 0) {
            return null;
        }
        while (!page.isLeaf()) {
            page = page.getChildPage(z ? 0 : getChildPageCount(page) - 1);
        }
        return page.getKey(z ? 0 : page.getKeyCount() - 1);
    }

    public final K higherKey(K k) {
        return getMinMax(k, false, true);
    }

    public final K higherKey(RootReference<K, V> rootReference, K k) {
        return getMinMax((RootReference<RootReference<K, V>, V>) rootReference, (RootReference<K, V>) k, false, true);
    }

    public final K ceilingKey(K k) {
        return getMinMax(k, false, false);
    }

    public final K floorKey(K k) {
        return getMinMax(k, true, false);
    }

    public final K lowerKey(K k) {
        return getMinMax(k, true, true);
    }

    public final K lowerKey(RootReference<K, V> rootReference, K k) {
        return getMinMax((RootReference<RootReference<K, V>, V>) rootReference, (RootReference<K, V>) k, true, true);
    }

    private K getMinMax(K k, boolean z, boolean z2) {
        return getMinMax((RootReference<RootReference<K, V>, V>) flushAndGetRoot(), (RootReference<K, V>) k, z, z2);
    }

    private K getMinMax(RootReference<K, V> rootReference, K k, boolean z, boolean z2) {
        return getMinMax((Page<Page<K, V>, V>) rootReference.root, (Page<K, V>) k, z, z2);
    }

    private K getMinMax(Page<K, V> page, K k, boolean z, boolean z2) {
        int binarySearch = page.binarySearch(k);
        if (page.isLeaf()) {
            if (binarySearch < 0) {
                binarySearch = (-binarySearch) - (z ? 2 : 1);
            } else if (z2) {
                binarySearch += z ? -1 : 1;
            }
            if (binarySearch < 0 || binarySearch >= page.getKeyCount()) {
                return null;
            }
            return page.getKey(binarySearch);
        }
        int i = binarySearch + 1;
        if (binarySearch < 0) {
            i = -i;
        }
        while (i >= 0 && i < getChildPageCount(page)) {
            K minMax = getMinMax((Page<Page<K, V>, V>) page.getChildPage(i), (Page<K, V>) k, z, z2);
            if (minMax != null) {
                return minMax;
            }
            i += z ? -1 : 1;
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public final V get(Object obj) {
        return get(getRootPage(), obj);
    }

    public V get(Page<K, V> page, K k) {
        return (V) Page.get(page, k);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean containsKey(Object obj) {
        return get(obj) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        clearIt();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<K, V> clearIt() {
        RootReference<K, V> flushAndGetRoot;
        boolean isLockedByCurrentThread;
        Page<K, V> page;
        long j;
        Page<K, V> createEmptyLeaf = createEmptyLeaf();
        int i = 0;
        while (true) {
            flushAndGetRoot = flushAndGetRoot();
            if (flushAndGetRoot.getTotalCount() == 0) {
                return flushAndGetRoot;
            }
            isLockedByCurrentThread = flushAndGetRoot.isLockedByCurrentThread();
            if (!isLockedByCurrentThread) {
                int i2 = i;
                i++;
                if (i2 == 0) {
                    beforeWrite();
                } else if (i > 3 || flushAndGetRoot.isLocked()) {
                    flushAndGetRoot = lockRoot(flushAndGetRoot, i);
                    isLockedByCurrentThread = true;
                }
            }
            page = flushAndGetRoot.root;
            j = flushAndGetRoot.version;
            if (isLockedByCurrentThread) {
                break;
            }
            try {
                flushAndGetRoot = flushAndGetRoot.updateRootPage(createEmptyLeaf, i);
                if (flushAndGetRoot != null) {
                    break;
                }
                if (isLockedByCurrentThread) {
                    unlockRoot(page);
                }
            } catch (Throwable th) {
                if (isLockedByCurrentThread) {
                    unlockRoot(page);
                }
                throw th;
            }
        }
        if (isPersistent()) {
            registerUnsavedMemory(page.removeAllRecursive(j));
        }
        RootReference<K, V> rootReference = flushAndGetRoot;
        if (isLockedByCurrentThread) {
            unlockRoot(createEmptyLeaf);
        }
        return rootReference;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void registerUnsavedMemory(int i) {
        if (isPersistent()) {
            this.store.registerUnsavedMemory(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void close() {
        this.closed = true;
    }

    public final boolean isClosed() {
        return this.closed;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return operate(obj, null, DecisionMaker.REMOVE);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public final V putIfAbsent(K k, V v) {
        return operate(k, v, DecisionMaker.IF_ABSENT);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(Object obj, Object obj2) {
        EqualsDecisionMaker equalsDecisionMaker = new EqualsDecisionMaker(this.valueType, obj2);
        operate(obj, null, equalsDecisionMaker);
        return equalsDecisionMaker.getDecision() != Decision.ABORT;
    }

    static <X> boolean areValuesEqual(DataType<X> dataType, X x, X x2) {
        return x == x2 || !(x == null || x2 == null || dataType.compare(x, x2) != 0);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public final boolean replace(K k, V v, V v2) {
        EqualsDecisionMaker equalsDecisionMaker = new EqualsDecisionMaker(this.valueType, v);
        V operate = operate(k, v2, equalsDecisionMaker);
        boolean z = equalsDecisionMaker.getDecision() != Decision.ABORT;
        if ($assertionsDisabled || !z || areValuesEqual(this.valueType, v, operate)) {
            return z;
        }
        throw new AssertionError(v + " != " + operate);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public final V replace(K k, V v) {
        return operate(k, v, DecisionMaker.IF_PRESENT);
    }

    final int compare(K k, K k2) {
        return this.keyType.compare(k, k2);
    }

    public final DataType<K> getKeyType() {
        return this.keyType;
    }

    public final DataType<V> getValueType() {
        return this.valueType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSingleWriter() {
        return this.singleWriter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Page<K, V> readPage(long j) {
        return this.store.readPage(this, j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setRootPos(long j, long j2) {
        Page<K, V> readOrCreateRootPage = readOrCreateRootPage(j);
        if (readOrCreateRootPage.map != this) {
            if (!$assertionsDisabled && this.id != readOrCreateRootPage.map.id) {
                throw new AssertionError();
            }
            readOrCreateRootPage = readOrCreateRootPage.copy(this, false);
        }
        setInitialRoot(readOrCreateRootPage, j2 - 1);
        setWriteVersion(j2);
    }

    private Page<K, V> readOrCreateRootPage(long j) {
        return j == 0 ? createEmptyLeaf() : readPage(j);
    }

    public final Iterator<K> keyIterator(K k) {
        return cursor(k, null, false);
    }

    public final Iterator<K> keyIteratorReverse(K k) {
        return cursor(k, null, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean rewritePage(long j) {
        Page<K, V> readPage = readPage(j);
        if (readPage.getKeyCount() == 0) {
            return true;
        }
        if (!$assertionsDisabled && !readPage.isSaved()) {
            throw new AssertionError();
        }
        K key = readPage.getKey(0);
        if (!isClosed()) {
            RewriteDecisionMaker rewriteDecisionMaker = new RewriteDecisionMaker(readPage.getPos());
            V operate = operate(key, null, rewriteDecisionMaker);
            boolean z = rewriteDecisionMaker.getDecision() != Decision.ABORT;
            if (!$assertionsDisabled && z && operate == null) {
                throw new AssertionError();
            }
            return z;
        }
        return false;
    }

    public final Cursor<K, V> cursor(K k) {
        return cursor(k, null, false);
    }

    public final Cursor<K, V> cursor(K k, K k2, boolean z) {
        return cursor(flushAndGetRoot(), k, k2, z);
    }

    public Cursor<K, V> cursor(RootReference<K, V> rootReference, K k, K k2, boolean z) {
        return new Cursor<>(rootReference, k, k2, z);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final Set<Map.Entry<K, V>> entrySet() {
        final RootReference<K, V> flushAndGetRoot = flushAndGetRoot();
        return new AbstractSet<Map.Entry<K, V>>() { // from class: org.h2.mvstore.MVMap.2
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<K, V>> iterator() {
                final Cursor<K, V> cursor = MVMap.this.cursor(flushAndGetRoot, null, null, false);
                return new Iterator<Map.Entry<K, V>>() { // from class: org.h2.mvstore.MVMap.2.1
                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return cursor.hasNext();
                    }

                    @Override // java.util.Iterator
                    public Map.Entry<K, V> next() {
                        return new AbstractMap.SimpleImmutableEntry(cursor.next(), cursor.getValue());
                    }
                };
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return MVMap.this.size();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object obj) {
                return MVMap.this.containsKey(obj);
            }
        };
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        final RootReference<K, V> flushAndGetRoot = flushAndGetRoot();
        return new AbstractSet<K>() { // from class: org.h2.mvstore.MVMap.3
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<K> iterator() {
                return MVMap.this.cursor(flushAndGetRoot, null, null, false);
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return MVMap.this.size();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object obj) {
                return MVMap.this.containsKey(obj);
            }
        };
    }

    public final String getName() {
        return this.store.getMapName(this.id);
    }

    public final MVStore getStore() {
        return this.store;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isPersistent() {
        return this.store.isPersistent() && !this.isVolatile;
    }

    public final int getId() {
        return this.id;
    }

    public final Page<K, V> getRootPage() {
        return flushAndGetRoot().root;
    }

    public RootReference<K, V> getRoot() {
        return this.root.get();
    }

    public RootReference<K, V> flushAndGetRoot() {
        RootReference<K, V> root = getRoot();
        if (this.singleWriter && root.getAppendCounter() > 0) {
            return flushAppendBuffer(root, true);
        }
        return root;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setInitialRoot(Page<K, V> page, long j) {
        this.root.set(new RootReference<>(page, j));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean compareAndSetRoot(RootReference<K, V> rootReference, RootReference<K, V> rootReference2) {
        return this.root.compareAndSet(rootReference, rootReference2);
    }

    final void rollbackTo(long j) {
        if (j > this.createVersion) {
            rollbackRoot(j);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean rollbackRoot(long j) {
        RootReference<K, V> rootReference;
        RootReference<K, V> flushAndGetRoot = flushAndGetRoot();
        while (flushAndGetRoot.version >= j && (rootReference = flushAndGetRoot.previous) != null) {
            if (this.root.compareAndSet(flushAndGetRoot, rootReference)) {
                flushAndGetRoot = rootReference;
                this.closed = false;
            }
        }
        setWriteVersion(j);
        return flushAndGetRoot.version < j;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static <K, V> boolean updateRoot(RootReference<K, V> rootReference, Page<K, V> page, int i) {
        return rootReference.updateRootPage(page, (long) i) != null;
    }

    private void removeUnusedOldVersions(RootReference<K, V> rootReference) {
        rootReference.removeUnusedOldVersions(this.store.getOldestVersionToKeep());
    }

    public final boolean isReadOnly() {
        return this.readOnly;
    }

    public final void setVolatile(boolean z) {
        this.isVolatile = z;
    }

    public final boolean isVolatile() {
        return this.isVolatile;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void beforeWrite() {
        if (!$assertionsDisabled && getRoot().isLockedByCurrentThread()) {
            throw new AssertionError(getRoot());
        }
        if (this.closed) {
            int id = getId();
            throw DataUtils.newMVStoreException(4, "Map {0}({1}) is closed. {2}", this.store.getMapName(id), Integer.valueOf(id), this.store.getPanicException());
        }
        if (this.readOnly) {
            throw DataUtils.newUnsupportedOperationException("This map is read-only");
        }
        this.store.beforeWrite(this);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int hashCode() {
        return this.id;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public final int size() {
        long sizeAsLong = sizeAsLong();
        if (sizeAsLong > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) sizeAsLong;
    }

    public final long sizeAsLong() {
        return getRoot().getTotalCount();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return sizeAsLong() == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final long getCreateVersion() {
        return this.createVersion;
    }

    public final MVMap<K, V> openVersion(long j) {
        RootReference<K, V> rootReference;
        if (this.readOnly) {
            throw DataUtils.newUnsupportedOperationException("This map is read-only; need to call the method on the writable map");
        }
        DataUtils.checkArgument(j >= this.createVersion, "Unknown version {0}; this map was created in version is {1}", Long.valueOf(j), Long.valueOf(this.createVersion));
        RootReference<K, V> flushAndGetRoot = flushAndGetRoot();
        removeUnusedOldVersions(flushAndGetRoot);
        while (true) {
            rootReference = flushAndGetRoot.previous;
            if (rootReference == null || rootReference.version < j) {
                break;
            }
            flushAndGetRoot = rootReference;
        }
        if (rootReference == null && j < this.store.getOldestVersionToKeep()) {
            throw DataUtils.newIllegalArgumentException("Unknown version {0}", Long.valueOf(j));
        }
        MVMap<K, V> openReadOnly = openReadOnly(flushAndGetRoot.root, j);
        if ($assertionsDisabled || openReadOnly.getVersion() <= j) {
            return openReadOnly;
        }
        throw new AssertionError(openReadOnly.getVersion() + " <= " + j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final MVMap<K, V> openReadOnly(long j, long j2) {
        return openReadOnly(readOrCreateRootPage(j), j2);
    }

    private MVMap<K, V> openReadOnly(Page<K, V> page, long j) {
        MVMap<K, V> cloneIt = cloneIt();
        cloneIt.readOnly = true;
        cloneIt.setInitialRoot(page, j);
        return cloneIt;
    }

    public final long getVersion() {
        return getRoot().getVersion();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasChangesSince(long j) {
        return getRoot().hasChangesSince(j, isPersistent());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getChildPageCount(Page<K, V> page) {
        return page.getRawChildPageCount();
    }

    public String getType() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String asString(String str) {
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            DataUtils.appendMap(sb, "name", str);
        }
        if (this.createVersion != 0) {
            DataUtils.appendMap(sb, "createVersion", this.createVersion);
        }
        String type = getType();
        if (type != null) {
            DataUtils.appendMap(sb, "type", type);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x006b, code lost:            r10 = null;        removeUnusedOldVersions(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x007a, code lost:            if (0 == 0) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x007d, code lost:            unlockRoot();     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0084, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final org.h2.mvstore.RootReference<K, V> setWriteVersion(long r6) {
        /*
            r5 = this;
            r0 = 0
            r8 = r0
        L2:
            r0 = r5
            org.h2.mvstore.RootReference r0 = r0.flushAndGetRoot()
            r9 = r0
            r0 = r9
            long r0 = r0.version
            r1 = r6
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L15
            r0 = r9
            return r0
        L15:
            r0 = r5
            boolean r0 = r0.isClosed()
            if (r0 == 0) goto L3b
            r0 = r9
            long r0 = r0.getVersion()
            r1 = 1
            long r0 = r0 + r1
            r1 = r5
            org.h2.mvstore.MVStore r1 = r1.store
            long r1 = r1.getOldestVersionToKeep()
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L3b
            r0 = r5
            org.h2.mvstore.MVStore r0 = r0.store
            r1 = r5
            int r1 = r1.id
            r0.deregisterMapRoot(r1)
            r0 = 0
            return r0
        L3b:
            r0 = 0
            r10 = r0
            int r8 = r8 + 1
            r0 = r8
            r1 = 3
            if (r0 > r1) goto L4e
            r0 = r9
            boolean r0 = r0.isLocked()
            if (r0 == 0) goto L5d
        L4e:
            r0 = r5
            r1 = r9
            r2 = r8
            org.h2.mvstore.RootReference r0 = r0.lockRoot(r1, r2)
            r10 = r0
            r0 = r5
            org.h2.mvstore.RootReference r0 = r0.flushAndGetRoot()
            r9 = r0
        L5d:
            r0 = r9
            r1 = r6
            r2 = r8
            org.h2.mvstore.RootReference r0 = r0.tryUnlockAndUpdateVersion(r1, r2)     // Catch: java.lang.Throwable -> L92
            r9 = r0
            r0 = r9
            if (r0 == 0) goto L85
            r0 = 0
            r10 = r0
            r0 = r5
            r1 = r9
            r0.removeUnusedOldVersions(r1)     // Catch: java.lang.Throwable -> L92
            r0 = r9
            r11 = r0
            r0 = r10
            if (r0 == 0) goto L82
            r0 = r5
            org.h2.mvstore.RootReference r0 = r0.unlockRoot()
        L82:
            r0 = r11
            return r0
        L85:
            r0 = r10
            if (r0 == 0) goto La1
            r0 = r5
            org.h2.mvstore.RootReference r0 = r0.unlockRoot()
            goto La1
        L92:
            r12 = move-exception
            r0 = r10
            if (r0 == 0) goto L9e
            r0 = r5
            org.h2.mvstore.RootReference r0 = r0.unlockRoot()
        L9e:
            r0 = r12
            throw r0
        La1:
            goto L2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.MVMap.setWriteVersion(long):org.h2.mvstore.RootReference");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Page<K, V> createEmptyLeaf() {
        return Page.createEmptyLeaf(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Page<K, V> createEmptyNode() {
        return Page.createEmptyNode(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void copyFrom(MVMap<K, V> mVMap) {
        MVStore.TxCounter registerVersionUsage = this.store.registerVersionUsage();
        try {
            beforeWrite();
            copy(mVMap.getRootPage(), null, 0);
        } finally {
            this.store.deregisterVersionUsage(registerVersionUsage);
        }
    }

    private void copy(Page<K, V> page, Page<K, V> page2, int i) {
        Page<K, V> copy = page.copy(this, true);
        if (page2 == null) {
            setInitialRoot(copy, -1L);
        } else {
            page2.setChild(i, copy);
        }
        if (!page.isLeaf()) {
            for (int i2 = 0; i2 < getChildPageCount(copy); i2++) {
                if (page.getChildPagePos(i2) != 0) {
                    copy(page.getChildPage(i2), copy, i2);
                }
            }
            copy.setComplete();
        }
        this.store.registerUnsavedMemoryAndCommitIfNeeded(copy.getMemory());
    }

    /* JADX WARN: Code restructure failed: missing block: B:110:0x03a1, code lost:            if (r12 == false) goto L126;     */
    /* JADX WARN: Code restructure failed: missing block: B:112:0x03a5, code lost:            if (r0 != false) goto L126;     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x03a8, code lost:            r9 = unlockRoot();     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x03c4, code lost:            return r9;     */
    /* JADX WARN: Finally extract failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.mvstore.RootReference<K, V> flushAppendBuffer(org.h2.mvstore.RootReference<K, V> r9, boolean r10) {
        /*
            Method dump skipped, instructions count: 965
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.MVMap.flushAppendBuffer(org.h2.mvstore.RootReference, boolean):org.h2.mvstore.RootReference");
    }

    private static <K, V> Page<K, V> replacePage(CursorPos<K, V> cursorPos, Page<K, V> page, IntValueHolder intValueHolder) {
        int memory = page.isSaved() ? 0 : page.getMemory();
        while (cursorPos != null) {
            Page<K, V> page2 = cursorPos.page;
            if (page2.getKeyCount() > 0) {
                Page<K, V> page3 = page;
                page = page2.copy();
                page.setChild(cursorPos.index, page3);
                memory += page.getMemory();
            }
            cursorPos = cursorPos.parent;
        }
        intValueHolder.value += memory;
        return page;
    }

    public void append(K k, V v) {
        if (this.singleWriter) {
            beforeWrite();
            RootReference<K, V> lockRoot = lockRoot(getRoot(), 1);
            int appendCounter = lockRoot.getAppendCounter();
            try {
                if (appendCounter >= this.keysPerPage) {
                    appendCounter = flushAppendBuffer(lockRoot, false).getAppendCounter();
                    if (!$assertionsDisabled && appendCounter >= this.keysPerPage) {
                        throw new AssertionError();
                    }
                }
                this.keysBuffer[appendCounter] = k;
                if (this.valuesBuffer != null) {
                    this.valuesBuffer[appendCounter] = v;
                }
                unlockRoot(appendCounter + 1);
                return;
            } catch (Throwable th) {
                unlockRoot(appendCounter);
                throw th;
            }
        }
        put(k, v);
    }

    public void trimLast() {
        if (this.singleWriter) {
            RootReference<K, V> root = getRoot();
            int appendCounter = root.getAppendCounter();
            boolean z = appendCounter == 0;
            if (!z) {
                root = lockRoot(root, 1);
                try {
                    appendCounter = root.getAppendCounter();
                    z = appendCounter == 0;
                    if (!z) {
                        appendCounter--;
                    }
                } finally {
                    unlockRoot(appendCounter);
                }
            }
            if (z) {
                Page<K, V> page = root.root.getAppendCursorPos(null).page;
                if (!$assertionsDisabled && !page.isLeaf()) {
                    throw new AssertionError();
                }
                if (!$assertionsDisabled && page.getKeyCount() <= 0) {
                    throw new AssertionError();
                }
                remove(page.getKey(page.getKeyCount() - 1));
                return;
            }
            return;
        }
        remove(lastKey());
    }

    @Override // java.util.AbstractMap
    public final String toString() {
        return asString(null);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$BasicBuilder.class */
    public static abstract class BasicBuilder<M extends MVMap<K, V>, K, V> implements MapBuilder<M, K, V> {
        private DataType<K> keyType;
        private DataType<V> valueType;

        protected abstract M create(Map<String, Object> map);

        @Override // org.h2.mvstore.MVMap.MapBuilder
        public DataType<K> getKeyType() {
            return this.keyType;
        }

        @Override // org.h2.mvstore.MVMap.MapBuilder
        public DataType<V> getValueType() {
            return this.valueType;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.h2.mvstore.MVMap.MapBuilder
        public void setKeyType(DataType<? super K> dataType) {
            this.keyType = dataType;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.h2.mvstore.MVMap.MapBuilder
        public void setValueType(DataType<? super V> dataType) {
            this.valueType = dataType;
        }

        public BasicBuilder<M, K, V> keyType(DataType<? super K> dataType) {
            setKeyType(dataType);
            return this;
        }

        public BasicBuilder<M, K, V> valueType(DataType<? super V> dataType) {
            setValueType(dataType);
            return this;
        }

        @Override // org.h2.mvstore.MVMap.MapBuilder
        public M create(MVStore mVStore, Map<String, Object> map) {
            if (getKeyType() == null) {
                setKeyType(new ObjectDataType());
            }
            if (getValueType() == null) {
                setValueType(new ObjectDataType());
            }
            DataType<K> keyType = getKeyType();
            DataType<V> valueType = getValueType();
            map.put("store", mVStore);
            map.put("key", keyType);
            map.put("val", valueType);
            return create(map);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$Builder.class */
    public static class Builder<K, V> extends BasicBuilder<MVMap<K, V>, K, V> {
        private boolean singleWriter;

        @Override // org.h2.mvstore.MVMap.BasicBuilder
        public Builder<K, V> keyType(DataType<? super K> dataType) {
            setKeyType(dataType);
            return this;
        }

        @Override // org.h2.mvstore.MVMap.BasicBuilder
        public Builder<K, V> valueType(DataType<? super V> dataType) {
            setValueType(dataType);
            return this;
        }

        public Builder<K, V> singleWriter() {
            this.singleWriter = true;
            return this;
        }

        @Override // org.h2.mvstore.MVMap.BasicBuilder
        protected MVMap<K, V> create(Map<String, Object> map) {
            map.put("singleWriter", Boolean.valueOf(this.singleWriter));
            Object obj = map.get("type");
            if (obj == null || obj.equals("rtree")) {
                return new MVMap<>(map, getKeyType(), getValueType());
            }
            throw new IllegalArgumentException("Incompatible map type");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$DecisionMaker.class */
    public static abstract class DecisionMaker<V> {
        public static final DecisionMaker<Object> DEFAULT = new DecisionMaker<Object>() { // from class: org.h2.mvstore.MVMap.DecisionMaker.1
            @Override // org.h2.mvstore.MVMap.DecisionMaker
            public Decision decide(Object obj, Object obj2) {
                return obj2 == null ? Decision.REMOVE : Decision.PUT;
            }

            public String toString() {
                return "default";
            }
        };
        public static final DecisionMaker<Object> PUT = new DecisionMaker<Object>() { // from class: org.h2.mvstore.MVMap.DecisionMaker.2
            @Override // org.h2.mvstore.MVMap.DecisionMaker
            public Decision decide(Object obj, Object obj2) {
                return Decision.PUT;
            }

            public String toString() {
                return "put";
            }
        };
        public static final DecisionMaker<Object> REMOVE = new DecisionMaker<Object>() { // from class: org.h2.mvstore.MVMap.DecisionMaker.3
            @Override // org.h2.mvstore.MVMap.DecisionMaker
            public Decision decide(Object obj, Object obj2) {
                return Decision.REMOVE;
            }

            public String toString() {
                return "remove";
            }
        };
        static final DecisionMaker<Object> IF_ABSENT = new DecisionMaker<Object>() { // from class: org.h2.mvstore.MVMap.DecisionMaker.4
            @Override // org.h2.mvstore.MVMap.DecisionMaker
            public Decision decide(Object obj, Object obj2) {
                return obj == null ? Decision.PUT : Decision.ABORT;
            }

            public String toString() {
                return "if_absent";
            }
        };
        static final DecisionMaker<Object> IF_PRESENT = new DecisionMaker<Object>() { // from class: org.h2.mvstore.MVMap.DecisionMaker.5
            @Override // org.h2.mvstore.MVMap.DecisionMaker
            public Decision decide(Object obj, Object obj2) {
                return obj != null ? Decision.PUT : Decision.ABORT;
            }

            public String toString() {
                return "if_present";
            }
        };

        public abstract Decision decide(V v, V v2);

        public Decision decide(V v, V v2, CursorPos<?, ?> cursorPos) {
            return decide(v, v2);
        }

        public <T extends V> T selectValue(T t, T t2) {
            return t2;
        }

        public void reset() {
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:106:0x014e, code lost:            if (r14 == false) goto L54;     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x0151, code lost:            unlockRoot(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x015a, code lost:            return null;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0167, code lost:            if (r20 != null) goto L60;     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x016a, code lost:            r21 = r20.page;        r22 = r20.index;        r20 = r20.parent;        r0 = r21.getKeyCount();     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0188, code lost:            if (r0 != 0) goto L150;     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x018d, code lost:            if (r20 != null) goto L152;     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0193, code lost:            if (r0 > 1) goto L76;     */
    /* JADX WARN: Code restructure failed: missing block: B:60:0x0199, code lost:            if (r0 != 1) goto L75;     */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x019f, code lost:            if (org.h2.mvstore.MVMap.$assertionsDisabled != false) goto L74;     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x01a5, code lost:            if (r22 <= 1) goto L74;     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01af, code lost:            throw new java.lang.AssertionError();     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x01b0, code lost:            r21 = r21.getChildPage(1 - r22);     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x031f, code lost:            if (isPersistent() == false) goto L105;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0322, code lost:            registerUnsavedMemory(r0.value + r0.processRemovalInfo(r0));     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x0339, code lost:            if (r14 == false) goto L108;     */
    /* JADX WARN: Code restructure failed: missing block: B:85:0x033c, code lost:            unlockRoot(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0345, code lost:            return r19;     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01be, code lost:            r21 = org.h2.mvstore.Page.createEmptyLeaf(r7);     */
    /* JADX WARN: Failed to find 'out' block for switch in B:23:0x00bd. Please report as an issue. */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:71:0x02f7 A[Catch: all -> 0x0346, TryCatch #0 {all -> 0x0346, blocks: (B:14:0x0059, B:16:0x0066, B:19:0x007e, B:22:0x00a7, B:23:0x00bd, B:135:0x00dc, B:27:0x00f4, B:29:0x00fd, B:96:0x012d, B:98:0x0136, B:48:0x015b, B:52:0x016a, B:61:0x019c, B:66:0x01a8, B:67:0x01af, B:68:0x01b0, B:89:0x01be, B:92:0x01c7, B:112:0x01d8, B:114:0x01ec, B:115:0x01f8, B:117:0x020a, B:119:0x021b, B:127:0x022d, B:131:0x0266, B:129:0x02a4, B:132:0x02de, B:69:0x02e7, B:71:0x02f7, B:73:0x0308, B:80:0x031b, B:82:0x0322, B:141:0x00a0), top: B:13:0x0059 }] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x031b A[ADDED_TO_REGION, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r19v0, types: [V, java.lang.Object] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public V operate(K r8, V r9, org.h2.mvstore.MVMap.DecisionMaker<? super V> r10) {
        /*
            Method dump skipped, instructions count: 855
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.MVMap.operate(java.lang.Object, java.lang.Object, org.h2.mvstore.MVMap$DecisionMaker):java.lang.Object");
    }

    private RootReference<K, V> lockRoot(RootReference<K, V> rootReference, int i) {
        while (true) {
            int i2 = i;
            i++;
            RootReference<K, V> tryLock = tryLock(rootReference, i2);
            if (tryLock != null) {
                return tryLock;
            }
            rootReference = getRoot();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RootReference<K, V> tryLock(RootReference<K, V> rootReference, int i) {
        RootReference<K, V> tryLock = rootReference.tryLock(i);
        if (tryLock != null) {
            return tryLock;
        }
        if (!$assertionsDisabled && rootReference.isLockedByCurrentThread()) {
            throw new AssertionError(rootReference);
        }
        RootReference<K, V> rootReference2 = rootReference.previous;
        int i2 = 1;
        if (rootReference2 != null) {
            long j = rootReference.updateAttemptCounter - rootReference2.updateAttemptCounter;
            if (!$assertionsDisabled && j < 0) {
                throw new AssertionError(j);
            }
            long j2 = rootReference.updateCounter - rootReference2.updateCounter;
            if (!$assertionsDisabled && j2 < 0) {
                throw new AssertionError(j2);
            }
            if (!$assertionsDisabled && j < j2) {
                throw new AssertionError(j + " >= " + j2);
            }
            i2 = 1 + ((int) ((j + 1) / (j2 + 1)));
        }
        if (i > 4) {
            if (i <= 12) {
                Thread.yield();
                return null;
            }
            if (i <= 70 - (2 * i2)) {
                try {
                    Thread.sleep(i2);
                    return null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            synchronized (this.lock) {
                this.notificationRequested = true;
                try {
                    this.lock.wait(5L);
                } catch (InterruptedException e2) {
                }
            }
            return null;
        }
        return null;
    }

    private RootReference<K, V> unlockRoot() {
        return unlockRoot(null, -1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public RootReference<K, V> unlockRoot(Page<K, V> page) {
        return unlockRoot(page, -1);
    }

    private void unlockRoot(int i) {
        unlockRoot(null, i);
    }

    private RootReference<K, V> unlockRoot(Page<K, V> page, int i) {
        RootReference<K, V> updatePageAndLockedStatus;
        do {
            RootReference<K, V> root = getRoot();
            if (!$assertionsDisabled && !root.isLockedByCurrentThread()) {
                throw new AssertionError();
            }
            updatePageAndLockedStatus = root.updatePageAndLockedStatus(page == null ? root.root : page, false, i == -1 ? root.getAppendCounter() : i);
        } while (updatePageAndLockedStatus == null);
        notifyWaiters();
        return updatePageAndLockedStatus;
    }

    private void notifyWaiters() {
        if (this.notificationRequested) {
            synchronized (this.lock) {
                this.notificationRequested = false;
                this.lock.notify();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isMemoryEstimationAllowed() {
        return (this.avgKeySize == null && this.avgValSize == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int evaluateMemoryForKeys(K[] kArr, int i) {
        if (this.avgKeySize == null) {
            return calculateMemory(this.keyType, kArr, i);
        }
        return MemoryEstimator.estimateMemory(this.avgKeySize, this.keyType, kArr, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int evaluateMemoryForValues(V[] vArr, int i) {
        if (this.avgValSize == null) {
            return calculateMemory(this.valueType, vArr, i);
        }
        return MemoryEstimator.estimateMemory(this.avgValSize, this.valueType, vArr, i);
    }

    private static <T> int calculateMemory(DataType<T> dataType, T[] tArr, int i) {
        int i2 = i * 8;
        for (int i3 = 0; i3 < i; i3++) {
            i2 += dataType.getMemory(tArr[i3]);
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int evaluateMemoryForKey(K k) {
        if (this.avgKeySize == null) {
            return this.keyType.getMemory(k);
        }
        return MemoryEstimator.estimateMemory(this.avgKeySize, this.keyType, k);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int evaluateMemoryForValue(V v) {
        if (this.avgValSize == null) {
            return this.valueType.getMemory(v);
        }
        return MemoryEstimator.estimateMemory(this.avgValSize, this.valueType, v);
    }

    static int samplingPct(AtomicLong atomicLong) {
        return MemoryEstimator.samplingPct(atomicLong);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$EqualsDecisionMaker.class */
    private static final class EqualsDecisionMaker<V> extends DecisionMaker<V> {
        private final DataType<V> dataType;
        private final V expectedValue;
        private Decision decision;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !MVMap.class.desiredAssertionStatus();
        }

        EqualsDecisionMaker(DataType<V> dataType, V v) {
            this.dataType = dataType;
            this.expectedValue = v;
        }

        @Override // org.h2.mvstore.MVMap.DecisionMaker
        public Decision decide(V v, V v2) {
            if (!$assertionsDisabled && this.decision != null) {
                throw new AssertionError();
            }
            this.decision = !MVMap.areValuesEqual(this.dataType, this.expectedValue, v) ? Decision.ABORT : v2 == null ? Decision.REMOVE : Decision.PUT;
            return this.decision;
        }

        @Override // org.h2.mvstore.MVMap.DecisionMaker
        public void reset() {
            this.decision = null;
        }

        Decision getDecision() {
            return this.decision;
        }

        public String toString() {
            return "equals_to " + this.expectedValue;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$RewriteDecisionMaker.class */
    private static final class RewriteDecisionMaker<V> extends DecisionMaker<V> {
        private final long pagePos;
        private Decision decision;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !MVMap.class.desiredAssertionStatus();
        }

        RewriteDecisionMaker(long j) {
            this.pagePos = j;
        }

        @Override // org.h2.mvstore.MVMap.DecisionMaker
        public Decision decide(V v, V v2, CursorPos<?, ?> cursorPos) {
            if (!$assertionsDisabled && this.decision != null) {
                throw new AssertionError();
            }
            this.decision = Decision.ABORT;
            if (DataUtils.isLeafPosition(this.pagePos)) {
                if (cursorPos.page.getPos() == this.pagePos) {
                    this.decision = decide(v, v2);
                }
                return this.decision;
            }
            while (true) {
                CursorPos<?, ?> cursorPos2 = cursorPos.parent;
                cursorPos = cursorPos2;
                if (cursorPos2 == null) {
                    break;
                }
                if (cursorPos.page.getPos() == this.pagePos) {
                    this.decision = decide(v, v2);
                    break;
                }
            }
            return this.decision;
        }

        @Override // org.h2.mvstore.MVMap.DecisionMaker
        public Decision decide(V v, V v2) {
            this.decision = v == null ? Decision.ABORT : Decision.PUT;
            return this.decision;
        }

        @Override // org.h2.mvstore.MVMap.DecisionMaker
        public <T extends V> T selectValue(T t, T t2) {
            return t;
        }

        @Override // org.h2.mvstore.MVMap.DecisionMaker
        public void reset() {
            this.decision = null;
        }

        Decision getDecision() {
            return this.decision;
        }

        public String toString() {
            return "rewrite";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVMap$IntValueHolder.class */
    public static final class IntValueHolder {
        int value;

        IntValueHolder() {
        }
    }
}

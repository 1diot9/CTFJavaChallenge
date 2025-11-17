package org.h2.mvstore;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import org.h2.compress.CompressDeflate;
import org.h2.compress.CompressLZF;
import org.h2.compress.Compressor;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.StringDataType;
import org.h2.store.fs.FileUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVStore.class */
public class MVStore implements AutoCloseable {
    private static final int STATE_OPEN = 0;
    private static final int STATE_STOPPING = 1;
    private static final int STATE_CLOSED = 2;
    static final long INITIAL_VERSION = -1;
    private volatile int state;
    private final FileStore<?> fileStore;
    private final boolean fileStoreShallBeClosed;
    private final int keysPerPage;
    private final MVMap<String, String> meta;
    private final int compressionLevel;
    private Compressor compressorFast;
    private Compressor compressorHigh;
    public final Thread.UncaughtExceptionHandler backgroundExceptionHandler;
    private int unsavedMemory;
    private final int autoCommitMemory;
    private volatile boolean saveNeeded;
    private volatile boolean metaChanged;
    private volatile MVStoreException panicException;
    private long lastTimeAbsolute;
    private long leafCount;
    private long nonLeafCount;
    private volatile LongConsumer oldestVersionTracker;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final ReentrantLock storeLock = new ReentrantLock(true);
    private final AtomicBoolean storeOperationInProgress = new AtomicBoolean();
    private long updateCounter = 0;
    private long updateAttemptCounter = 0;
    private final ConcurrentHashMap<Integer, MVMap<?, ?>> maps = new ConcurrentHashMap<>();
    private final AtomicInteger lastMapId = new AtomicInteger();
    private int versionsToKeep = 5;
    private final AtomicLong oldestVersionToKeep = new AtomicLong();
    private final Deque<TxCounter> versions = new LinkedList();
    private volatile long currentVersion;
    private volatile TxCounter currentTxCounter = new TxCounter(this.currentVersion);

    /*  JADX ERROR: Failed to decode insn: 0x0039: MOVE_MULTI, method: org.h2.mvstore.MVStore.store(boolean):long
        java.lang.ArrayIndexOutOfBoundsException: arraycopy: source index -1 out of bounds for object array[6]
        	at java.base/java.lang.System.arraycopy(Native Method)
        	at jadx.plugins.input.java.data.code.StackState.insert(StackState.java:49)
        	at jadx.plugins.input.java.data.code.CodeDecodeState.insert(CodeDecodeState.java:118)
        	at jadx.plugins.input.java.data.code.JavaInsnsRegister.dup2x1(JavaInsnsRegister.java:313)
        	at jadx.plugins.input.java.data.code.JavaInsnData.decode(JavaInsnData.java:46)
        	at jadx.core.dex.instructions.InsnDecoder.lambda$process$0(InsnDecoder.java:54)
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:81)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:50)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:156)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:443)
        	at jadx.core.ProcessClass.process(ProcessClass.java:70)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:110)
        	at jadx.core.dex.nodes.ClassNode.generateClassCode(ClassNode.java:400)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:388)
        	at jadx.core.dex.nodes.ClassNode.getCode(ClassNode.java:338)
        */
    private long store(boolean r7) {
        /*
            r6 = this;
            boolean r0 = org.h2.mvstore.MVStore.$assertionsDisabled
            if (r0 != 0) goto L18
            r0 = r6
            java.util.concurrent.locks.ReentrantLock r0 = r0.storeLock
            boolean r0 = r0.isHeldByCurrentThread()
            if (r0 != 0) goto L18
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r1 = r0
            r1.<init>()
            throw r0
            r0 = r6
            boolean r0 = r0.isOpenOrStopping()
            if (r0 == 0) goto L8c
            r0 = r6
            boolean r0 = r0.hasUnsavedChanges()
            if (r0 == 0) goto L8c
            r0 = r6
            java.util.concurrent.atomic.AtomicBoolean r0 = r0.storeOperationInProgress
            r1 = 0
            r2 = 1
            boolean r0 = r0.compareAndSet(r1, r2)
            if (r0 == 0) goto L8c
            r0 = r6
            r1 = r0
            long r1 = r1.currentVersion
            r2 = 1
            long r1 = r1 + r2
            // decode failed: arraycopy: source index -1 out of bounds for object array[6]
            r0.currentVersion = r1
            r8 = r-1
            r-1 = r6
            org.h2.mvstore.FileStore<?> r-1 = r-1.fileStore
            if (r-1 != 0) goto L50
            r-1 = r6
            r0 = r6
            long r0 = r0.currentVersion
            r-1.setWriteVersion(r0)
            goto L71
            r-1 = r6
            org.h2.mvstore.FileStore<?> r-1 = r-1.fileStore
            r-1.isReadOnly()
            if (r-1 == 0) goto L65
            r-1 = 2
            java.lang.String r0 = "This store is read-only"
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            org.h2.mvstore.DataUtils.newMVStoreException(r-1, r0, r1)
            throw r-1
            r-1 = r6
            org.h2.mvstore.FileStore<?> r-1 = r-1.fileStore
            r-1.dropUnusedChunks()
            r-1 = r6
            r0 = r7
            r-1.storeNow(r0)
            r-1 = r8
            r10 = r-1
            r-1 = r6
            java.util.concurrent.atomic.AtomicBoolean r-1 = r-1.storeOperationInProgress
            r0 = 0
            r-1.set(r0)
            r-1 = r10
            return r-1
            r12 = move-exception
            r0 = r6
            java.util.concurrent.atomic.AtomicBoolean r0 = r0.storeOperationInProgress
            r1 = 0
            r0.set(r1)
            r0 = r12
            throw r0
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.MVStore.store(boolean):long");
    }

    static {
        $assertionsDisabled = !MVStore.class.desiredAssertionStatus();
    }

    MVStore(Map<String, Object> map) {
        this.compressionLevel = DataUtils.getConfigParam(map, "compress", 0);
        String str = (String) map.get("fileName");
        FileStore<?> fileStore = (FileStore) map.get("fileStore");
        boolean z = false;
        if (fileStore == null) {
            if (str != null) {
                fileStore = new SingleFileStore(map);
                z = true;
            }
            this.fileStoreShallBeClosed = true;
        } else {
            if (str != null) {
                throw new IllegalArgumentException("fileName && fileStore");
            }
            Boolean bool = (Boolean) map.get("fileStoreIsAdopted");
            this.fileStoreShallBeClosed = bool != null && bool.booleanValue();
        }
        this.fileStore = fileStore;
        this.keysPerPage = DataUtils.getConfigParam(map, "keysPerPage", 48);
        this.backgroundExceptionHandler = (Thread.UncaughtExceptionHandler) map.get("backgroundExceptionHandler");
        if (fileStore != null) {
            this.autoCommitMemory = DataUtils.getConfigParam(map, "autoCommitBufferSize", Math.max(1, Math.min(19, Utils.scaleForAvailableMemory(64))) * 1024) * 1024;
            char[] cArr = (char[]) map.remove("encryptionKey");
            MVMap<String, String> mVMap = null;
            this.storeLock.lock();
            if (z) {
                try {
                    try {
                        fileStore.open(str, map.containsKey("readOnly"), cArr);
                    } catch (MVStoreException e) {
                        panic(e);
                        if (cArr != null) {
                            Arrays.fill(cArr, (char) 0);
                        }
                        unlockAndCheckPanicCondition();
                    }
                } catch (Throwable th) {
                    if (cArr != null) {
                        Arrays.fill(cArr, (char) 0);
                    }
                    unlockAndCheckPanicCondition();
                    throw th;
                }
            }
            fileStore.bind(this);
            mVMap = fileStore.start();
            if (cArr != null) {
                Arrays.fill(cArr, (char) 0);
            }
            unlockAndCheckPanicCondition();
            this.meta = mVMap;
            scrubMetaMap();
            setAutoCommitDelay(DataUtils.getConfigParam(map, "autoCommitDelay", 1000));
        } else {
            this.autoCommitMemory = 0;
            this.meta = openMetaMap();
        }
        onVersionChange(this.currentVersion);
    }

    public MVMap<String, String> openMetaMap() {
        MVMap<String, String> mVMap = new MVMap<>(this, this.fileStore != null ? this.fileStore.getMetaMapId(this::getNextMapId) : 1, StringDataType.INSTANCE, StringDataType.INSTANCE);
        mVMap.setRootPos(getRootPos(mVMap.getId()), this.currentVersion);
        return mVMap;
    }

    private void scrubMetaMap() {
        HashSet hashSet = new HashSet();
        Iterator<String> keyIterator = this.meta.keyIterator(DataUtils.META_NAME);
        while (keyIterator.hasNext()) {
            String next = keyIterator.next();
            if (!next.startsWith(DataUtils.META_NAME)) {
                break;
            } else if (!next.substring(DataUtils.META_NAME.length()).equals(getMapName(DataUtils.parseHexInt(this.meta.get(next))))) {
                hashSet.add(next);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.meta.remove((String) it.next());
            markMetaChanged();
        }
        Iterator<String> keyIterator2 = this.meta.keyIterator(DataUtils.META_MAP);
        while (keyIterator2.hasNext()) {
            String next2 = keyIterator2.next();
            if (next2.startsWith(DataUtils.META_MAP)) {
                String mapName = DataUtils.getMapName(this.meta.get(next2));
                String substring = next2.substring(DataUtils.META_MAP.length());
                adjustLastMapId(DataUtils.parseHexInt(substring));
                if (!substring.equals(this.meta.get(DataUtils.META_NAME + mapName))) {
                    this.meta.put(DataUtils.META_NAME + mapName, substring);
                    markMetaChanged();
                }
            } else {
                return;
            }
        }
    }

    private void unlockAndCheckPanicCondition() {
        this.storeLock.unlock();
        MVStoreException panicException = getPanicException();
        if (panicException != null) {
            closeImmediately();
            throw panicException;
        }
    }

    public void panic(MVStoreException mVStoreException) {
        if (isOpen()) {
            handleException(mVStoreException);
            this.panicException = mVStoreException;
        }
        throw mVStoreException;
    }

    public MVStoreException getPanicException() {
        return this.panicException;
    }

    public static MVStore open(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("fileName", str);
        return new MVStore(hashMap);
    }

    public <K, V> MVMap<K, V> openMap(String str) {
        return openMap(str, new MVMap.Builder());
    }

    public <M extends MVMap<K, V>, K, V> M openMap(String str, MVMap.MapBuilder<M, K, V> mapBuilder) {
        int mapId = getMapId(str);
        if (mapId >= 0) {
            MVMap<K, V> map = getMap(mapId);
            if (map == null) {
                map = openMap(mapId, mapBuilder);
            }
            if (!$assertionsDisabled && mapBuilder.getKeyType() != null && !map.getKeyType().getClass().equals(mapBuilder.getKeyType().getClass())) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && mapBuilder.getValueType() != null && !map.getValueType().getClass().equals(mapBuilder.getValueType().getClass())) {
                throw new AssertionError();
            }
            return (M) map;
        }
        HashMap hashMap = new HashMap();
        int nextMapId = getNextMapId();
        if (!$assertionsDisabled && getMap(nextMapId) != null) {
            throw new AssertionError();
        }
        hashMap.put("id", Integer.valueOf(nextMapId));
        long j = this.currentVersion;
        hashMap.put("createVersion", Long.valueOf(j));
        MVMap<?, ?> create = mapBuilder.create(this, hashMap);
        String hexString = Integer.toHexString(nextMapId);
        this.meta.put(MVMap.getMapKey(nextMapId), create.asString(str));
        if (this.meta.putIfAbsent(DataUtils.META_NAME + str, hexString) != null) {
            this.meta.remove(MVMap.getMapKey(nextMapId));
            return (M) openMap(str, mapBuilder);
        }
        create.setRootPos(0L, j);
        markMetaChanged();
        MVMap<?, ?> putIfAbsent = this.maps.putIfAbsent(Integer.valueOf(nextMapId), create);
        if (putIfAbsent != null) {
            create = putIfAbsent;
        }
        return (M) create;
    }

    public <M extends MVMap<K, V>, K, V> M openMap(int i, MVMap.MapBuilder<M, K, V> mapBuilder) {
        MVMap<K, V> mVMap;
        do {
            MVMap<K, V> map = getMap(i);
            mVMap = map;
            if (map != null) {
                break;
            }
            String str = this.meta.get(MVMap.getMapKey(i));
            DataUtils.checkArgument(str != null, "Missing map with id {0}", Integer.valueOf(i));
            HashMap hashMap = new HashMap(DataUtils.parseMap(str));
            hashMap.put("id", Integer.valueOf(i));
            mVMap = mapBuilder.create(this, hashMap);
            mVMap.setRootPos(getRootPos(i), this.currentVersion);
        } while (this.maps.putIfAbsent(Integer.valueOf(i), mVMap) != null);
        return (M) mVMap;
    }

    public <K, V> MVMap<K, V> getMap(int i) {
        checkNotClosed();
        return (MVMap) this.maps.get(Integer.valueOf(i));
    }

    public Set<String> getMapNames() {
        HashSet hashSet = new HashSet();
        checkNotClosed();
        Iterator<String> keyIterator = this.meta.keyIterator(DataUtils.META_NAME);
        while (keyIterator.hasNext()) {
            String next = keyIterator.next();
            if (!next.startsWith(DataUtils.META_NAME)) {
                break;
            }
            hashSet.add(next.substring(DataUtils.META_NAME.length()));
        }
        return hashSet;
    }

    public Map<String, String> getLayoutMap() {
        if (this.fileStore == null) {
            return null;
        }
        return this.fileStore.getLayoutMap();
    }

    private boolean isRegularMap(MVMap<?, ?> mVMap) {
        return mVMap != this.meta && (this.fileStore == null || this.fileStore.isRegularMap(mVMap));
    }

    public MVMap<String, String> getMetaMap() {
        checkNotClosed();
        return this.meta;
    }

    public boolean hasMap(String str) {
        return this.meta.containsKey(DataUtils.META_NAME + str);
    }

    public boolean hasData(String str) {
        return hasMap(str) && getRootPos(getMapId(str)) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markMetaChanged() {
        this.metaChanged = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getLastMapId() {
        return this.lastMapId.get();
    }

    private int getNextMapId() {
        return this.lastMapId.incrementAndGet();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustLastMapId(int i) {
        if (i > this.lastMapId.get()) {
            this.lastMapId.set(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resetLastMapId(int i) {
        this.lastMapId.set(i);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        closeStore(true, 0);
    }

    public void close(int i) {
        if (!isClosed()) {
            if (this.fileStore != null) {
                boolean z = i == -1;
                if (this.fileStore.isReadOnly()) {
                    z = false;
                } else {
                    commit();
                }
                if (z) {
                    i = 0;
                }
                closeStore(true, i);
                String fileName = this.fileStore.getFileName();
                if (z && FileUtils.exists(fileName)) {
                    MVStoreTool.compact(fileName, true);
                    return;
                }
                return;
            }
            close();
        }
    }

    public void closeImmediately() {
        try {
            closeStore(false, 0);
        } catch (Throwable th) {
            handleException(th);
        }
    }

    /* JADX WARN: Finally extract failed */
    private void closeStore(boolean z, int i) {
        while (!isClosed()) {
            setAutoCommitDelay(-1);
            setOldestVersionTracker(null);
            this.storeLock.lock();
            try {
                if (this.state == 0) {
                    this.state = 1;
                    if (z) {
                        try {
                            try {
                                if (this.fileStore != null && !this.fileStore.isReadOnly()) {
                                    for (MVMap<?, ?> mVMap : this.maps.values()) {
                                        if (mVMap.isClosed()) {
                                            this.fileStore.deregisterMapRoot(mVMap.getId());
                                        }
                                    }
                                    setRetentionTime(0);
                                    commit();
                                    if (!$assertionsDisabled && this.oldestVersionToKeep.get() != this.currentVersion) {
                                        throw new AssertionError(this.oldestVersionToKeep.get() + " != " + this.currentVersion);
                                    }
                                    this.fileStore.stop(i);
                                }
                            } catch (Throwable th) {
                                this.state = 2;
                                throw th;
                            }
                        } finally {
                        }
                    }
                    if (this.meta != null) {
                        this.meta.close();
                    }
                    Iterator it = new ArrayList(this.maps.values()).iterator();
                    while (it.hasNext()) {
                        ((MVMap) it.next()).close();
                    }
                    this.maps.clear();
                    if (this.fileStore != null && this.fileStoreShallBeClosed) {
                        this.fileStore.close();
                    }
                    this.state = 2;
                }
            } finally {
                this.storeLock.unlock();
            }
        }
    }

    public boolean isPersistent() {
        return this.fileStore != null;
    }

    public long tryCommit() {
        return tryCommit(mVStore -> {
            return true;
        });
    }

    private long tryCommit(Predicate<MVStore> predicate) {
        if (canStartStoreOperation() && this.storeLock.tryLock()) {
            try {
                if (predicate.test(this)) {
                    long store = store(false);
                    unlockAndCheckPanicCondition();
                    return store;
                }
                unlockAndCheckPanicCondition();
                return -1L;
            } catch (Throwable th) {
                unlockAndCheckPanicCondition();
                throw th;
            }
        }
        return -1L;
    }

    public long commit() {
        return commit(mVStore -> {
            return true;
        });
    }

    private long commit(Predicate<MVStore> predicate) {
        if (canStartStoreOperation()) {
            this.storeLock.lock();
            try {
                if (predicate.test(this)) {
                    long store = store(true);
                    unlockAndCheckPanicCondition();
                    return store;
                }
                unlockAndCheckPanicCondition();
                return -1L;
            } catch (Throwable th) {
                unlockAndCheckPanicCondition();
                throw th;
            }
        }
        return -1L;
    }

    private boolean canStartStoreOperation() {
        return (this.storeLock.isHeldByCurrentThread() && this.storeOperationInProgress.get()) ? false : true;
    }

    private void setWriteVersion(long j) {
        Iterator<MVMap<?, ?>> it = this.maps.values().iterator();
        while (it.hasNext()) {
            MVMap<?, ?> next = it.next();
            if (!$assertionsDisabled && !isRegularMap(next)) {
                throw new AssertionError();
            }
            if (next.setWriteVersion(j) == null) {
                it.remove();
            }
        }
        this.meta.setWriteVersion(j);
        onVersionChange(j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void storeNow() {
        this.currentVersion++;
        storeNow(true);
    }

    private void storeNow(boolean z) {
        try {
            int i = this.unsavedMemory;
            long j = this.currentVersion;
            if (!$assertionsDisabled && !this.storeLock.isHeldByCurrentThread()) {
                throw new AssertionError();
            }
            this.fileStore.storeIt(collectChangedMapRoots(j), j, z);
            this.saveNeeded = false;
            this.unsavedMemory = Math.max(0, this.unsavedMemory - i);
        } catch (MVStoreException e) {
            panic(e);
        } catch (Throwable th) {
            panic(DataUtils.newMVStoreException(3, "{0}", th.toString(), th));
        }
    }

    private ArrayList<Page<?, ?>> collectChangedMapRoots(long j) {
        long j2 = j - 2;
        ArrayList<Page<?, ?>> arrayList = new ArrayList<>();
        Iterator<MVMap<?, ?>> it = this.maps.values().iterator();
        while (it.hasNext()) {
            MVMap<?, ?> next = it.next();
            RootReference<?, ?> writeVersion = next.setWriteVersion(j);
            if (writeVersion == null) {
                it.remove();
            } else if (next.getCreateVersion() < j && !next.isVolatile() && next.hasChangesSince(j2)) {
                if (!$assertionsDisabled && writeVersion.version > j) {
                    throw new AssertionError(writeVersion.version + " > " + j);
                }
                arrayList.add(writeVersion.root);
            }
        }
        RootReference<String, String> writeVersion2 = this.meta.setWriteVersion(j);
        if (this.meta.hasChangesSince(j2) || this.metaChanged) {
            if (!$assertionsDisabled && (writeVersion2 == null || writeVersion2.version > j)) {
                throw new AssertionError(writeVersion2 == null ? "null" : writeVersion2.version + " > " + j);
            }
            arrayList.add(writeVersion2.root);
        }
        return arrayList;
    }

    public long getTimeAbsolute() {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastTimeAbsolute != 0 && currentTimeMillis < this.lastTimeAbsolute) {
            currentTimeMillis = this.lastTimeAbsolute;
        } else {
            this.lastTimeAbsolute = currentTimeMillis;
        }
        return currentTimeMillis;
    }

    public boolean hasUnsavedChanges() {
        if (this.metaChanged) {
            return true;
        }
        long j = this.currentVersion - 1;
        for (MVMap<?, ?> mVMap : this.maps.values()) {
            if (!mVMap.isClosed() && mVMap.hasChangesSince(j)) {
                return true;
            }
        }
        return this.fileStore != null && this.fileStore.hasChangesSince(j);
    }

    public void executeFilestoreOperation(Runnable runnable) {
        this.storeLock.lock();
        try {
            checkNotClosed();
            this.fileStore.executeFileStoreOperation(runnable);
        } catch (MVStoreException e) {
            panic(e);
        } catch (Throwable th) {
            panic(DataUtils.newMVStoreException(3, "{0}", th.toString(), th));
        } finally {
            unlockAndCheckPanicCondition();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <R> R tryExecuteUnderStoreLock(Callable<R> callable) throws InterruptedException {
        R r = null;
        try {
            if (this.storeLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
                try {
                    r = callable.call();
                    unlockAndCheckPanicCondition();
                } catch (MVStoreException e) {
                    panic(e);
                    unlockAndCheckPanicCondition();
                } catch (Throwable th) {
                    panic(DataUtils.newMVStoreException(3, "{0}", th.toString(), th));
                    unlockAndCheckPanicCondition();
                }
            }
            return r;
        } catch (Throwable th2) {
            unlockAndCheckPanicCondition();
            throw th2;
        }
    }

    public void sync() {
        checkOpen();
        FileStore<?> fileStore = this.fileStore;
        if (fileStore != null) {
            fileStore.sync();
        }
    }

    public void compactFile(int i) {
        if (this.fileStore != null) {
            setRetentionTime(0);
            this.storeLock.lock();
            try {
                this.fileStore.compactStore(i);
            } finally {
                unlockAndCheckPanicCondition();
            }
        }
    }

    public boolean compact(int i, int i2) {
        checkOpen();
        return this.fileStore != null && this.fileStore.compact(i, i2);
    }

    public int getFillRate() {
        return this.fileStore.getFillRate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <K, V> Page<K, V> readPage(MVMap<K, V> mVMap, long j) {
        checkNotClosed();
        return this.fileStore.readPage(mVMap, j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void accountForRemovedPage(long j, long j2, boolean z, int i) {
        this.fileStore.accountForRemovedPage(j, j2, z, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Compressor getCompressorFast() {
        if (this.compressorFast == null) {
            this.compressorFast = new CompressLZF();
        }
        return this.compressorFast;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Compressor getCompressorHigh() {
        if (this.compressorHigh == null) {
            this.compressorHigh = new CompressDeflate();
        }
        return this.compressorHigh;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public int getKeysPerPage() {
        return this.keysPerPage;
    }

    public long getMaxPageSize() {
        if (this.fileStore == null) {
            return Long.MAX_VALUE;
        }
        return this.fileStore.getMaxPageSize();
    }

    public int getCacheSize() {
        if (this.fileStore == null) {
            return 0;
        }
        return this.fileStore.getCacheSize();
    }

    public int getCacheSizeUsed() {
        if (this.fileStore == null) {
            return 0;
        }
        return this.fileStore.getCacheSizeUsed();
    }

    public void setCacheSize(int i) {
        if (this.fileStore != null) {
            this.fileStore.setCacheSize(Math.max(1, i / 1024));
        }
    }

    public boolean isSpaceReused() {
        return this.fileStore.isSpaceReused();
    }

    public void setReuseSpace(boolean z) {
        this.fileStore.setReuseSpace(z);
    }

    public int getRetentionTime() {
        if (this.fileStore == null) {
            return 0;
        }
        return this.fileStore.getRetentionTime();
    }

    public void setRetentionTime(int i) {
        if (this.fileStore != null) {
            this.fileStore.setRetentionTime(i);
        }
    }

    public boolean isVersioningRequired() {
        return !(this.fileStore == null || this.fileStore.isReadOnly()) || this.versionsToKeep > 0;
    }

    public void setVersionsToKeep(int i) {
        this.versionsToKeep = i;
    }

    public long getVersionsToKeep() {
        return this.versionsToKeep;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getOldestVersionToKeep() {
        return Math.min(this.oldestVersionToKeep.get(), Math.max(this.currentVersion - this.versionsToKeep, -1L));
    }

    private void setOldestVersionToKeep(long j) {
        long j2;
        do {
            j2 = this.oldestVersionToKeep.get();
        } while (!(j <= j2 || this.oldestVersionToKeep.compareAndSet(j2, j)));
        if (!$assertionsDisabled && j > this.currentVersion) {
            throw new AssertionError(j + " <= " + this.currentVersion);
        }
        if (this.oldestVersionTracker != null) {
            this.oldestVersionTracker.accept(j);
        }
    }

    public void setOldestVersionTracker(LongConsumer longConsumer) {
        this.oldestVersionTracker = longConsumer;
    }

    private boolean isKnownVersion(long j) {
        long currentVersion = getCurrentVersion();
        if (j > currentVersion || j < 0) {
            return false;
        }
        return j == currentVersion || this.fileStore == null || this.fileStore.isKnownVersion(j);
    }

    public void registerUnsavedMemory(int i) {
        if (!$assertionsDisabled && this.fileStore == null) {
            throw new AssertionError();
        }
        this.unsavedMemory += i;
        if (needStore()) {
            this.saveNeeded = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerUnsavedMemoryAndCommitIfNeeded(int i) {
        registerUnsavedMemory(i);
        if (this.saveNeeded) {
            commit();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void beforeWrite(MVMap<?, ?> mVMap) {
        if (this.saveNeeded && isOpenOrStopping()) {
            if ((this.storeLock.isHeldByCurrentThread() || !mVMap.getRoot().isLockedByCurrentThread()) && this.fileStore.isRegularMap(mVMap)) {
                this.saveNeeded = false;
                if (this.autoCommitMemory > 0 && needStore()) {
                    if (requireStore() && !mVMap.isSingleWriter()) {
                        commit((v0) -> {
                            return v0.requireStore();
                        });
                    } else {
                        tryCommit((v0) -> {
                            return v0.needStore();
                        });
                    }
                }
            }
        }
    }

    private boolean requireStore() {
        return 3 * this.unsavedMemory > 4 * this.autoCommitMemory;
    }

    private boolean needStore() {
        return this.autoCommitMemory > 0 && this.fileStore.shouldSaveNow(this.unsavedMemory, this.autoCommitMemory);
    }

    public int getStoreVersion() {
        checkOpen();
        String str = this.meta.get("setting.storeVersion");
        if (str == null) {
            return 0;
        }
        return DataUtils.parseHexInt(str);
    }

    public void setStoreVersion(int i) {
        this.storeLock.lock();
        try {
            checkOpen();
            markMetaChanged();
            this.meta.put("setting.storeVersion", Integer.toHexString(i));
        } finally {
            this.storeLock.unlock();
        }
    }

    public void rollback() {
        rollbackTo(this.currentVersion);
    }

    public void rollbackTo(long j) {
        this.storeLock.lock();
        try {
            this.currentVersion = j;
            checkOpen();
            DataUtils.checkArgument(isKnownVersion(j), "Unknown version {0}", Long.valueOf(j));
            while (true) {
                TxCounter peekLast = this.versions.peekLast();
                if (peekLast == null || peekLast.version < j) {
                    break;
                } else {
                    this.versions.removeLast();
                }
            }
            this.currentTxCounter = new TxCounter(j);
            if (this.oldestVersionToKeep.get() > j) {
                this.oldestVersionToKeep.set(j);
            }
            if (this.fileStore != null) {
                this.fileStore.rollbackTo(j);
            }
            if (!this.meta.rollbackRoot(j)) {
                this.meta.setRootPos(getRootPos(this.meta.getId()), j - 1);
            }
            this.metaChanged = false;
            Iterator it = new ArrayList(this.maps.values()).iterator();
            while (it.hasNext()) {
                MVMap mVMap = (MVMap) it.next();
                int id = mVMap.getId();
                if (mVMap.getCreateVersion() >= j) {
                    mVMap.close();
                    this.maps.remove(Integer.valueOf(id));
                } else if (!mVMap.rollbackRoot(j)) {
                    mVMap.setRootPos(getRootPos(id), j);
                }
            }
            onVersionChange(this.currentVersion);
            if ($assertionsDisabled || !hasUnsavedChanges()) {
            } else {
                throw new AssertionError();
            }
        } finally {
            unlockAndCheckPanicCondition();
        }
    }

    private long getRootPos(int i) {
        if (this.fileStore == null) {
            return 0L;
        }
        return this.fileStore.getRootPos(i);
    }

    public long getCurrentVersion() {
        return this.currentVersion;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCurrentVersion(long j) {
        this.currentVersion = j;
    }

    public FileStore<?> getFileStore() {
        return this.fileStore;
    }

    public Map<String, Object> getStoreHeader() {
        return this.fileStore.getStoreHeader();
    }

    private void checkOpen() {
        if (!isOpen()) {
            throw DataUtils.newMVStoreException(4, "This store is closed", this.panicException);
        }
    }

    private void checkNotClosed() {
        if (!isOpenOrStopping()) {
            throw DataUtils.newMVStoreException(4, "This store is closed", this.panicException);
        }
    }

    public void renameMap(MVMap<?, ?> mVMap, String str) {
        checkOpen();
        DataUtils.checkArgument(isRegularMap(mVMap), "Renaming the meta map is not allowed", new Object[0]);
        int id = mVMap.getId();
        String mapName = getMapName(id);
        if (mapName != null && !mapName.equals(str)) {
            String hexString = Integer.toHexString(id);
            String putIfAbsent = this.meta.putIfAbsent(DataUtils.META_NAME + str, hexString);
            DataUtils.checkArgument(putIfAbsent == null || putIfAbsent.equals(hexString), "A map named {0} already exists", str);
            this.meta.put(MVMap.getMapKey(id), mVMap.asString(str));
            this.meta.remove(DataUtils.META_NAME + mapName);
            markMetaChanged();
        }
    }

    public void removeMap(MVMap<?, ?> mVMap) {
        this.storeLock.lock();
        try {
            checkOpen();
            DataUtils.checkArgument(isRegularMap(mVMap), "Removing the meta map is not allowed", new Object[0]);
            RootReference<?, ?> clearIt = mVMap.clearIt();
            mVMap.close();
            this.updateCounter += clearIt.updateCounter;
            this.updateAttemptCounter += clearIt.updateAttemptCounter;
            int id = mVMap.getId();
            String mapName = getMapName(id);
            if (this.meta.remove(MVMap.getMapKey(id)) != null) {
                markMetaChanged();
            }
            if (this.meta.remove(DataUtils.META_NAME + mapName) != null) {
                markMetaChanged();
            }
            if (!isVersioningRequired()) {
                this.maps.remove(Integer.valueOf(id));
            }
        } finally {
            this.storeLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deregisterMapRoot(int i) {
        if (this.fileStore != null && this.fileStore.deregisterMapRoot(i)) {
            markMetaChanged();
        }
    }

    public void removeMap(String str) {
        int mapId = getMapId(str);
        if (mapId > 0) {
            MVMap<?, ?> map = getMap(mapId);
            if (map == null) {
                map = openMap(str, MVStoreTool.getGenericMapBuilder());
            }
            removeMap(map);
        }
    }

    public String getMapName(int i) {
        String str = this.meta.get(MVMap.getMapKey(i));
        if (str == null) {
            return null;
        }
        return DataUtils.getMapName(str);
    }

    private int getMapId(String str) {
        String str2 = this.meta.get(DataUtils.META_NAME + str);
        if (str2 == null) {
            return -1;
        }
        return DataUtils.parseHexInt(str2);
    }

    public void populateInfo(BiConsumer<String, String> biConsumer) {
        biConsumer.accept("info.UPDATE_FAILURE_PERCENT", String.format(Locale.ENGLISH, "%.2f%%", Double.valueOf(100.0d * getUpdateFailureRatio())));
        biConsumer.accept("info.LEAF_RATIO", Integer.toString(getLeafRatio()));
        if (this.fileStore != null) {
            this.fileStore.populateInfo(biConsumer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean handleException(Throwable th) {
        if (this.backgroundExceptionHandler != null) {
            try {
                this.backgroundExceptionHandler.uncaughtException(Thread.currentThread(), th);
                return true;
            } catch (Throwable th2) {
                if (th != th2) {
                    th.addSuppressed(th2);
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isOpen() {
        return this.state == 0;
    }

    public boolean isClosed() {
        if (isOpen()) {
            return false;
        }
        this.storeLock.lock();
        try {
            return this.state == 2;
        } finally {
            this.storeLock.unlock();
        }
    }

    private boolean isOpenOrStopping() {
        return this.state <= 1;
    }

    public void setAutoCommitDelay(int i) {
        if (this.fileStore != null) {
            this.fileStore.setAutoCommitDelay(i);
        }
    }

    public int getAutoCommitDelay() {
        if (this.fileStore == null) {
            return 0;
        }
        return this.fileStore.getAutoCommitDelay();
    }

    public int getAutoCommitMemory() {
        return this.autoCommitMemory;
    }

    public int getUnsavedMemory() {
        return this.unsavedMemory;
    }

    public boolean isReadOnly() {
        return this.fileStore != null && this.fileStore.isReadOnly();
    }

    private int getLeafRatio() {
        return (int) ((this.leafCount * 100) / Math.max(1L, this.leafCount + this.nonLeafCount));
    }

    private double getUpdateFailureRatio() {
        long j = this.updateCounter;
        long j2 = this.updateAttemptCounter;
        RootReference<String, String> root = this.meta.getRoot();
        long j3 = j + root.updateCounter;
        long j4 = j2 + root.updateAttemptCounter;
        Iterator<MVMap<?, ?>> it = this.maps.values().iterator();
        while (it.hasNext()) {
            RootReference<?, ?> root2 = it.next().getRoot();
            j3 += root2.updateCounter;
            j4 += root2.updateAttemptCounter;
        }
        if (j4 == 0) {
            return 0.0d;
        }
        return 1.0d - (j3 / j4);
    }

    public TxCounter registerVersionUsage() {
        while (true) {
            TxCounter txCounter = this.currentTxCounter;
            if (txCounter.incrementAndGet() > 0) {
                return txCounter;
            }
            if (!$assertionsDisabled && txCounter == this.currentTxCounter) {
                throw new AssertionError(txCounter);
            }
            txCounter.decrementAndGet();
        }
    }

    public void deregisterVersionUsage(TxCounter txCounter) {
        if (decrementVersionUsageCounter(txCounter)) {
            if (this.storeLock.isHeldByCurrentThread()) {
                dropUnusedVersions();
            } else if (this.storeLock.tryLock()) {
                try {
                    dropUnusedVersions();
                } finally {
                    this.storeLock.unlock();
                }
            }
        }
    }

    public boolean decrementVersionUsageCounter(TxCounter txCounter) {
        return txCounter != null && txCounter.decrementAndGet() <= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onVersionChange(long j) {
        this.metaChanged = false;
        TxCounter txCounter = this.currentTxCounter;
        if (!$assertionsDisabled && txCounter.get() < 0) {
            throw new AssertionError();
        }
        this.versions.add(txCounter);
        this.currentTxCounter = new TxCounter(j);
        txCounter.decrementAndGet();
        dropUnusedVersions();
    }

    private void dropUnusedVersions() {
        TxCounter peek;
        while (true) {
            peek = this.versions.peek();
            if (peek == null || peek.get() >= 0) {
                break;
            } else {
                this.versions.poll();
            }
        }
        setOldestVersionToKeep((peek != null ? peek : this.currentTxCounter).version);
    }

    public void countNewPage(boolean z) {
        if (z) {
            this.leafCount++;
        } else {
            this.nonLeafCount++;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVStore$TxCounter.class */
    public static final class TxCounter {
        public final long version;
        private volatile int counter;
        private static final AtomicIntegerFieldUpdater<TxCounter> counterUpdater = AtomicIntegerFieldUpdater.newUpdater(TxCounter.class, "counter");

        TxCounter(long j) {
            this.version = j;
        }

        int get() {
            return this.counter;
        }

        int incrementAndGet() {
            return counterUpdater.incrementAndGet(this);
        }

        int decrementAndGet() {
            return counterUpdater.decrementAndGet(this);
        }

        public String toString() {
            return "v=" + this.version + " / cnt=" + this.counter;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/MVStore$Builder.class */
    public static final class Builder {
        private final HashMap<String, Object> config;

        private Builder(HashMap<String, Object> hashMap) {
            this.config = hashMap;
        }

        public Builder() {
            this.config = new HashMap<>();
        }

        private Builder set(String str, Object obj) {
            this.config.put(str, obj);
            return this;
        }

        public Builder autoCommitDisabled() {
            return set("autoCommitDelay", 0);
        }

        public Builder autoCommitBufferSize(int i) {
            return set("autoCommitBufferSize", Integer.valueOf(i));
        }

        public Builder autoCompactFillRate(int i) {
            return set("autoCompactFillRate", Integer.valueOf(i));
        }

        public Builder fileName(String str) {
            return set("fileName", str);
        }

        public Builder encryptionKey(char[] cArr) {
            return set("encryptionKey", cArr);
        }

        public Builder readOnly() {
            return set("readOnly", 1);
        }

        public Builder keysPerPage(int i) {
            return set("keysPerPage", Integer.valueOf(i));
        }

        public Builder recoveryMode() {
            return set("recoveryMode", 1);
        }

        public Builder cacheSize(int i) {
            return set("cacheSize", Integer.valueOf(i));
        }

        public Builder cacheConcurrency(int i) {
            return set("cacheConcurrency", Integer.valueOf(i));
        }

        public Builder compress() {
            return set("compress", 1);
        }

        public Builder compressHigh() {
            return set("compress", 2);
        }

        public Builder pageSplitSize(int i) {
            return set("pageSplitSize", Integer.valueOf(i));
        }

        public Builder backgroundExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            return set("backgroundExceptionHandler", uncaughtExceptionHandler);
        }

        public Builder fileStore(FileStore<?> fileStore) {
            return set("fileStore", fileStore);
        }

        public Builder adoptFileStore(FileStore fileStore) {
            set("fileStoreIsAdopted", true);
            return set("fileStore", fileStore);
        }

        public MVStore open() {
            return new MVStore(this.config);
        }

        public String toString() {
            return DataUtils.appendMap(new StringBuilder(), this.config).toString();
        }

        public static Builder fromString(String str) {
            return new Builder(DataUtils.parseMap(str));
        }
    }
}

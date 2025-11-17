package org.h2.mvstore;

import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.IntSupplier;
import java.util.zip.ZipOutputStream;
import org.apache.tomcat.jni.SSL;
import org.h2.mvstore.Chunk;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.cache.CacheLongKeyLIRS;
import org.h2.mvstore.type.StringDataType;
import org.h2.util.MathUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/FileStore.class */
public abstract class FileStore<C extends Chunk<C>> {
    static final String HDR_H = "H";
    static final String HDR_BLOCK_SIZE = "blockSize";
    static final String HDR_FORMAT = "format";
    static final String HDR_CREATED = "created";
    static final String HDR_FORMAT_READ = "formatRead";
    static final String HDR_CHUNK = "chunk";
    static final String HDR_BLOCK = "block";
    static final String HDR_VERSION = "version";
    static final String HDR_CLEAN = "clean";
    static final String HDR_FLETCHER = "fletcher";
    public static final String META_ID_KEY = "meta.id";
    static final int BLOCK_SIZE = 4096;
    private static final int FORMAT_WRITE_MIN = 3;
    private static final int FORMAT_WRITE_MAX = 3;
    private static final int FORMAT_READ_MIN = 3;
    private static final int FORMAT_READ_MAX = 3;
    MVStore mvStore;
    private boolean closed;
    private String fileName;
    private final int maxPageSize;
    private long size;
    private boolean readOnly;
    private ThreadPoolExecutor serializationExecutor;
    private ThreadPoolExecutor bufferSaveExecutor;
    private final CacheLongKeyLIRS<Page<?, ?>> cache;
    private final CacheLongKeyLIRS<long[]> chunksToC;
    protected volatile C lastChunk;
    private int lastChunkId;
    private long creationTime;
    private MVMap<String, String> layout;
    private final int autoCompactFillRate;
    private int autoCommitDelay;
    private long autoCompactLastFileOpCount;
    private long lastCommitTime;
    protected final boolean recoveryMode;
    public static final int PIPE_LENGTH = 3;
    private int serializationExecutorHWM;
    private int bufferSaveExecutorHWM;
    static final /* synthetic */ boolean $assertionsDisabled;
    protected final AtomicLong readCount = new AtomicLong();
    protected final AtomicLong readBytes = new AtomicLong();
    protected final AtomicLong writeCount = new AtomicLong();
    protected final AtomicLong writeBytes = new AtomicLong();
    private int retentionTime = getDefaultRetentionTime();
    private final ReentrantLock serializationLock = new ReentrantLock(true);
    private final Queue<RemovedPageInfo> removedPages = new PriorityBlockingQueue();
    protected final ReentrantLock saveChunkLock = new ReentrantLock(true);
    final ConcurrentHashMap<Integer, C> chunks = new ConcurrentHashMap<>();
    protected final HashMap<String, Object> storeHeader = new HashMap<>();
    private final Queue<WriteBuffer> writeBufferPool = new ArrayBlockingQueue(4);
    private final Deque<C> deadChunks = new ConcurrentLinkedDeque();
    private final AtomicReference<BackgroundWriterThread> backgroundWriterThread = new AtomicReference<>();

    public abstract void open(String str, boolean z, char[] cArr);

    public abstract FileStore<C> open(String str, boolean z);

    public abstract boolean shouldSaveNow(int i, int i2);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void writeFully(C c, long j, ByteBuffer byteBuffer);

    public abstract ByteBuffer readFully(C c, long j, int i);

    protected abstract void allocateChunkSpace(C c, WriteBuffer writeBuffer);

    protected abstract void writeChunk(C c, WriteBuffer writeBuffer);

    protected abstract void writeCleanShutdownMark();

    protected abstract void adjustStoreToLastChunk();

    protected abstract C createChunk(int i);

    public abstract C createChunk(String str);

    protected abstract C createChunk(Map<String, String> map);

    protected abstract void freeChunkSpace(Iterable<C> iterable);

    protected abstract boolean validateFileLength(String str);

    protected abstract void compactStore(int i, long j, int i2, MVStore mVStore);

    protected abstract void doHousekeeping(MVStore mVStore) throws InterruptedException;

    protected abstract void initializeStoreHeader(long j);

    protected abstract void readStoreHeader(boolean z);

    public abstract int getFillRate();

    protected abstract void shrinkStoreIfPossible(int i);

    protected abstract void markUsed(long j, int i);

    public abstract void backup(ZipOutputStream zipOutputStream) throws IOException;

    static {
        $assertionsDisabled = !FileStore.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FileStore(Map<String, Object> map) {
        this.recoveryMode = map.containsKey("recoveryMode");
        this.autoCompactFillRate = DataUtils.getConfigParam(map, "autoCompactFillRate", 90);
        CacheLongKeyLIRS.Config config = null;
        int configParam = DataUtils.getConfigParam(map, "cacheSize", 16);
        if (configParam > 0) {
            config = new CacheLongKeyLIRS.Config();
            config.maxMemory = configParam * FileSize.KB_COEFFICIENT * FileSize.KB_COEFFICIENT;
            Object obj = map.get("cacheConcurrency");
            if (obj != null) {
                config.segmentCount = ((Integer) obj).intValue();
            }
        }
        this.cache = config == null ? null : new CacheLongKeyLIRS<>(config);
        CacheLongKeyLIRS.Config config2 = new CacheLongKeyLIRS.Config();
        config2.maxMemory = FileSize.MB_COEFFICIENT;
        this.chunksToC = new CacheLongKeyLIRS<>(config2);
        int i = Integer.MAX_VALUE;
        if (this.cache != null) {
            i = 16384;
            int maxItemSize = (int) (this.cache.getMaxItemSize() >> 4);
            if (16384 > maxItemSize) {
                i = maxItemSize;
            }
        }
        this.maxPageSize = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void init(String str, boolean z) {
        this.fileName = str;
        this.readOnly = z;
    }

    public final void bind(MVStore mVStore) {
        if (this.mvStore != mVStore) {
            long pos = this.layout == null ? 0L : this.layout.getRootPage().getPos();
            this.layout = new MVMap<>(mVStore, 0, StringDataType.INSTANCE, StringDataType.INSTANCE);
            this.layout.setRootPos(pos, mVStore.getCurrentVersion());
            this.mvStore = mVStore;
            mVStore.resetLastMapId(this.lastChunk == null ? 0 : this.lastChunk.mapId);
            mVStore.setCurrentVersion(lastChunkVersion());
        }
    }

    public final void stop(long j) {
        if (j > 0) {
            compactStore(j);
        }
        writeCleanShutdown();
        clearCaches();
    }

    public void close() {
        this.layout.close();
        this.closed = true;
        this.chunks.clear();
    }

    public final int getMetaMapId(IntSupplier intSupplier) {
        int parseHexInt;
        String str = this.layout.get(META_ID_KEY);
        if (str == null) {
            parseHexInt = intSupplier.getAsInt();
            this.layout.put(META_ID_KEY, Integer.toHexString(parseHexInt));
        } else {
            parseHexInt = DataUtils.parseHexInt(str);
        }
        return parseHexInt;
    }

    public final Map<String, String> getLayoutMap() {
        return new TreeMap(this.layout);
    }

    public final boolean isRegularMap(MVMap<?, ?> mVMap) {
        return mVMap != this.layout;
    }

    public final long getRootPos(int i) {
        String str = this.layout.get(MVMap.getMapRootKey(i));
        if (str == null) {
            return 0L;
        }
        return DataUtils.parseHexLong(str);
    }

    public final boolean deregisterMapRoot(int i) {
        return this.layout.remove(MVMap.getMapRootKey(i)) != null;
    }

    public final boolean hasChangesSince(long j) {
        return this.layout.hasChangesSince(j) && j > -1;
    }

    public final long lastChunkVersion() {
        C c = this.lastChunk;
        if (c == null) {
            return 0L;
        }
        return c.version;
    }

    public final long getMaxPageSize() {
        return this.maxPageSize;
    }

    public final int getRetentionTime() {
        return this.retentionTime;
    }

    public final void setRetentionTime(int i) {
        this.retentionTime = i;
    }

    public final int getAutoCommitDelay() {
        return this.autoCommitDelay;
    }

    public final void setAutoCommitDelay(int i) {
        if (this.autoCommitDelay != i) {
            this.autoCommitDelay = i;
            if (!isReadOnly()) {
                stopBackgroundThread(i >= 0);
                if (i > 0 && this.mvStore.isOpen()) {
                    BackgroundWriterThread backgroundWriterThread = new BackgroundWriterThread(this, Math.max(1, i / 10), toString());
                    if (this.backgroundWriterThread.compareAndSet(null, backgroundWriterThread)) {
                        backgroundWriterThread.start();
                        this.serializationExecutor = Utils.createSingleThreadExecutor("H2-serialization");
                        this.bufferSaveExecutor = Utils.createSingleThreadExecutor("H2-save");
                    }
                }
            }
        }
    }

    public final boolean isKnownVersion(long j) {
        if (this.chunks.isEmpty()) {
            return true;
        }
        if (getChunkForVersion(j) == null) {
            return false;
        }
        try {
            for (C c : getChunksFromLayoutMap(getLayoutMap(j))) {
                if (!this.layout.containsKey(Chunk.getMetaKey(c.id)) && !isValidChunk(c)) {
                    return false;
                }
            }
            return true;
        } catch (MVStoreException e) {
            return false;
        }
    }

    public final void rollbackTo(long j) {
        if (j == 0) {
            String str = this.layout.get(META_ID_KEY);
            this.layout.setInitialRoot(this.layout.createEmptyLeaf(), -1L);
            this.layout.put(META_ID_KEY, str);
        } else if (!this.layout.rollbackRoot(j)) {
            this.layout.setInitialRoot(getLayoutMap(j).getRootPage(), j);
        }
        this.serializationLock.lock();
        try {
            C chunkForVersion = getChunkForVersion(j);
            if (chunkForVersion != null) {
                this.saveChunkLock.lock();
                try {
                    this.deadChunks.clear();
                    setLastChunk(chunkForVersion);
                    adjustStoreToLastChunk();
                    this.saveChunkLock.unlock();
                } catch (Throwable th) {
                    this.saveChunkLock.unlock();
                    throw th;
                }
            }
            this.removedPages.clear();
            clearCaches();
        } finally {
            this.serializationLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void initializeCommonHeaderAttributes(long j) {
        setLastChunk(null);
        this.creationTime = j;
        this.storeHeader.put(HDR_H, 2);
        this.storeHeader.put(HDR_BLOCK_SIZE, 4096);
        this.storeHeader.put(HDR_FORMAT, 3);
        this.storeHeader.put(HDR_CREATED, Long.valueOf(this.creationTime));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void processCommonHeaderAttributes() {
        this.creationTime = DataUtils.readHexLong(this.storeHeader, HDR_CREATED, 0L);
        long currentTimeMillis = System.currentTimeMillis();
        if (1970 + ((int) (currentTimeMillis / 31557600000L)) < 2014) {
            this.creationTime = currentTimeMillis - getRetentionTime();
        } else if (currentTimeMillis < this.creationTime) {
            this.creationTime = currentTimeMillis;
            this.storeHeader.put(HDR_CREATED, Long.valueOf(this.creationTime));
        }
        int readHexInt = DataUtils.readHexInt(this.storeHeader, HDR_BLOCK_SIZE, 4096);
        if (readHexInt != 4096) {
            throw DataUtils.newMVStoreException(5, "Block size {0} is currently not supported", Integer.valueOf(readHexInt));
        }
        long readHexLong = DataUtils.readHexLong(this.storeHeader, HDR_FORMAT, 1L);
        if (!isReadOnly()) {
            if (readHexLong > 3) {
                throw getUnsupportedWriteFormatException(readHexLong, 3, "The write format {0} is larger than the supported format {1}");
            }
            if (readHexLong < 3) {
                throw getUnsupportedWriteFormatException(readHexLong, 3, "The write format {0} is smaller than the supported format {1}");
            }
        }
        long readHexLong2 = DataUtils.readHexLong(this.storeHeader, HDR_FORMAT_READ, readHexLong);
        if (readHexLong2 > 3) {
            throw DataUtils.newMVStoreException(5, "The read format {0} is larger than the supported format {1}", Long.valueOf(readHexLong2), 3);
        }
        if (readHexLong2 < 3) {
            throw DataUtils.newMVStoreException(5, "The read format {0} is smaller than the supported format {1}", Long.valueOf(readHexLong2), 3);
        }
    }

    private long getTimeSinceCreation() {
        return Math.max(0L, this.mvStore.getTimeAbsolute() - getCreationTime());
    }

    private MVMap<String, String> getLayoutMap(long j) {
        C chunkForVersion = getChunkForVersion(j);
        DataUtils.checkArgument(chunkForVersion != null, "Unknown version {0}", Long.valueOf(j));
        return this.layout.openReadOnly(chunkForVersion.layoutRootPos, j);
    }

    private C getChunkForVersion(long j) {
        C c = null;
        for (C c2 : this.chunks.values()) {
            if (c2.version <= j && (c == null || c2.id > c.id)) {
                c = c2;
            }
        }
        return c;
    }

    private void scrubLayoutMap(MVMap<String, String> mVMap) {
        HashSet hashSet = new HashSet();
        for (String str : new String[]{DataUtils.META_NAME, DataUtils.META_MAP}) {
            Iterator<String> keyIterator = this.layout.keyIterator(str);
            while (keyIterator.hasNext()) {
                String next = keyIterator.next();
                if (!next.startsWith(str)) {
                    break;
                }
                mVMap.putIfAbsent(next, this.layout.get(next));
                this.mvStore.markMetaChanged();
                hashSet.add(next);
            }
        }
        Iterator<String> keyIterator2 = this.layout.keyIterator(DataUtils.META_ROOT);
        while (keyIterator2.hasNext()) {
            String next2 = keyIterator2.next();
            if (!next2.startsWith(DataUtils.META_ROOT)) {
                break;
            }
            String substring = next2.substring(next2.lastIndexOf(46) + 1);
            if (!mVMap.containsKey(DataUtils.META_MAP + substring) && DataUtils.parseHexInt(substring) != mVMap.getId()) {
                hashSet.add(next2);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.layout.remove((String) it.next());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean hasPersistentData() {
        return this.lastChunk != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isIdle() {
        return this.autoCompactLastFileOpCount == getWriteCount() + getReadCount();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setLastChunk(C c) {
        this.lastChunk = c;
        this.chunks.clear();
        this.lastChunkId = 0;
        long j = 0;
        if (c != null) {
            this.lastChunkId = c.id;
            j = c.layoutRootPos;
            this.chunks.put(Integer.valueOf(c.id), c);
        }
        this.layout.setRootPos(j, lastChunkVersion());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void registerDeadChunk(C c) {
        this.deadChunks.offer(c);
    }

    public final void dropUnusedChunks() {
        if (!this.deadChunks.isEmpty()) {
            long oldestVersionToKeep = this.mvStore.getOldestVersionToKeep();
            long timeSinceCreation = getTimeSinceCreation();
            ArrayList arrayList = new ArrayList();
            while (true) {
                C poll = this.deadChunks.poll();
                if (poll == null || (!(isSeasonedChunk(poll, timeSinceCreation) && canOverwriteChunk(poll, oldestVersionToKeep)) && this.deadChunks.offerFirst(poll))) {
                    break;
                }
                if (this.chunks.remove(Integer.valueOf(poll.id)) != null) {
                    long[] cleanToCCache = cleanToCCache(poll);
                    if (cleanToCCache != null && this.cache != null) {
                        for (long j : cleanToCCache) {
                            this.cache.remove(DataUtils.composePagePos(poll.id, j));
                        }
                    }
                    if (this.layout.remove(Chunk.getMetaKey(poll.id)) != null) {
                        this.mvStore.markMetaChanged();
                    }
                    if (poll.isAllocated()) {
                        arrayList.add(poll);
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                this.saveChunkLock.lock();
                try {
                    freeChunkSpace(arrayList);
                    this.saveChunkLock.unlock();
                } catch (Throwable th) {
                    this.saveChunkLock.unlock();
                    throw th;
                }
            }
        }
    }

    private static <C extends Chunk<C>> boolean canOverwriteChunk(C c, long j) {
        return !c.isLive() && c.unusedAtVersion < j;
    }

    private boolean isSeasonedChunk(C c, long j) {
        int retentionTime = getRetentionTime();
        return retentionTime < 0 || c.time + ((long) retentionTime) <= j;
    }

    private boolean isRewritable(C c, long j) {
        return c.isRewritable() && isSeasonedChunk(c, j);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ByteBuffer readFully(FileChannel fileChannel, long j, int i) {
        ByteBuffer allocate = ByteBuffer.allocate(i);
        DataUtils.readFully(fileChannel, j, allocate);
        this.readCount.incrementAndGet();
        this.readBytes.addAndGet(i);
        return allocate;
    }

    public Map<String, Object> getStoreHeader() {
        return this.storeHeader;
    }

    private C createChunk(long j, long j2) {
        C createChunk = createChunk(findNewChunkId());
        createChunk.time = j;
        createChunk.version = j2;
        createChunk.occupancy = new BitSet();
        return createChunk;
    }

    private int findNewChunkId() {
        C c;
        do {
            int i = this.lastChunkId + 1;
            this.lastChunkId = i;
            int i2 = i & Chunk.MAX_ID;
            if (i2 == this.lastChunkId || (c = this.chunks.get(Integer.valueOf(i2))) == null) {
                return i2;
            }
        } while (c.isSaved());
        throw DataUtils.newMVStoreException(3, "Last block {0} not stored, possibly due to out-of-memory", c);
    }

    protected void writeCleanShutdown() {
        if (!isReadOnly()) {
            this.saveChunkLock.lock();
            try {
                writeCleanShutdownMark();
                sync();
                if ($assertionsDisabled || validateFileLength("on close")) {
                } else {
                    throw new AssertionError();
                }
            } finally {
                this.saveChunkLock.unlock();
            }
        }
    }

    public void saveChunkMetadataChanges(C c) {
        if (!$assertionsDisabled && !this.serializationLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        }
        while (true) {
            if (c.isAllocated()) {
                break;
            }
            this.saveChunkLock.lock();
            try {
                if (c.isAllocated()) {
                    break;
                }
                this.saveChunkLock.unlock();
                Thread.yield();
            } finally {
                this.saveChunkLock.unlock();
            }
        }
        this.layout.put(Chunk.getMetaKey(c.id), c.asString());
    }

    public boolean compact(int i, int i2) {
        if (hasPersistentData() && i > 0 && getChunksFillRate() < i) {
            try {
                Boolean bool = (Boolean) this.mvStore.tryExecuteUnderStoreLock(() -> {
                    return Boolean.valueOf(rewriteChunks(i2, 100));
                });
                if (bool != null) {
                    if (bool.booleanValue()) {
                        return true;
                    }
                }
                return false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public void compactStore(long j) {
        compactStore(this.autoCompactFillRate, j, SSL.SSL_OP_NO_SSLv2, this.mvStore);
    }

    public MVMap<String, String> start() {
        if (size() == 0) {
            initializeCommonHeaderAttributes(this.mvStore.getTimeAbsolute());
            initializeStoreHeader(this.mvStore.getTimeAbsolute());
        } else {
            this.saveChunkLock.lock();
            try {
                readStoreHeader(this.recoveryMode);
            } finally {
                this.saveChunkLock.unlock();
            }
        }
        this.lastCommitTime = getTimeSinceCreation();
        this.mvStore.resetLastMapId(lastMapId());
        this.mvStore.setCurrentVersion(lastChunkVersion());
        MVMap<String, String> openMetaMap = this.mvStore.openMetaMap();
        scrubLayoutMap(openMetaMap);
        return openMetaMap;
    }

    private int lastMapId() {
        C c = this.lastChunk;
        if (c == null) {
            return 0;
        }
        return c.mapId;
    }

    private MVStoreException getUnsupportedWriteFormatException(long j, int i, String str) {
        long readHexLong = DataUtils.readHexLong(this.storeHeader, HDR_FORMAT_READ, j);
        if (readHexLong >= 3 && readHexLong <= 3) {
            str = str + ", and the file was not opened in read-only mode";
        }
        return DataUtils.newMVStoreException(5, str, Long.valueOf(readHexLong), Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final C discoverChunk(long j) {
        int i;
        long j2 = Long.MAX_VALUE;
        C c = null;
        while (j != j2) {
            if (j == 2) {
                return null;
            }
            C readChunkFooter = readChunkFooter(j);
            if (readChunkFooter != null) {
                j2 = Long.MAX_VALUE;
                long j3 = readChunkFooter.block;
                i = readChunkFooter.id;
                C readChunkHeaderOptionally = readChunkHeaderOptionally(j3, i);
                if (readChunkHeaderOptionally != null) {
                    c = readChunkHeaderOptionally;
                    j2 = readChunkHeaderOptionally.block;
                }
            }
            long j4 = j - 1;
            j = i;
            if (j4 > j2 && readChunkHeaderOptionally(j) != null) {
                j2 = Long.MAX_VALUE;
            }
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    public final boolean findLastChunkWithCompleteValidChunkSet(Comparator<C> comparator, Map<Long, C> map, boolean z) {
        Chunk[] chunkArr = (Chunk[]) map.values().toArray(createChunksArray(map.size()));
        Arrays.sort(chunkArr, comparator);
        HashMap hashMap = new HashMap();
        for (Chunk chunk : chunkArr) {
            hashMap.put(Integer.valueOf(chunk.id), chunk);
        }
        for (Chunk chunk2 : chunkArr) {
            boolean z2 = true;
            try {
                setLastChunk(chunk2);
                for (Chunk chunk3 : getChunksFromLayoutMap()) {
                    C c = map.get(Long.valueOf(chunk3.block));
                    if (c == null || c.id != chunk3.id) {
                        Chunk chunk4 = (Chunk) hashMap.get(Integer.valueOf(chunk3.id));
                        if (chunk4 != null) {
                            chunk3.block = chunk4.block;
                        } else if (chunk3.isLive() && (z || readChunkHeaderAndFooter(chunk3.block, chunk3.id) == null)) {
                            z2 = false;
                            break;
                        }
                    }
                    if (!chunk3.isLive()) {
                        chunk3.block = 0L;
                        chunk3.len = 0;
                        if (chunk3.unused == 0) {
                            chunk3.unused = getCreationTime();
                        }
                        if (chunk3.unusedAtVersion == 0) {
                            chunk3.unusedAtVersion = -1L;
                        }
                    }
                }
            } catch (Exception e) {
                z2 = false;
            }
            if (z2) {
                return true;
            }
        }
        return false;
    }

    private C[] createChunksArray(int i) {
        return (C[]) new Chunk[i];
    }

    /* JADX WARN: Multi-variable type inference failed */
    private C readChunkHeader(long j) {
        long j2 = j * 4096;
        Throwable th = null;
        try {
            C c = (C) createChunk(Chunk.readChunkHeader(readFully((FileStore<C>) null, j2, 1024)));
            if (c.block == 0) {
                c.block = j;
            }
            if (c.block == j) {
                return c;
            }
        } catch (MVStoreException e) {
            th = e.getCause();
        } catch (Throwable th2) {
            th = th2;
        }
        throw DataUtils.newMVStoreException(6, "File corrupt reading chunk at position {0}", Long.valueOf(j2), th);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Iterable<C> getChunksFromLayoutMap() {
        return getChunksFromLayoutMap(this.layout);
    }

    private Iterable<C> getChunksFromLayoutMap(MVMap<String, String> mVMap) {
        return () -> {
            return new Iterator<C>() { // from class: org.h2.mvstore.FileStore.1
                private final Cursor cursor;
                private Chunk nextChunk;

                {
                    this.cursor = mVMap.cursor(DataUtils.META_CHUNK);
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    if (this.nextChunk == null && this.cursor.hasNext() && ((String) this.cursor.next()).startsWith(DataUtils.META_CHUNK)) {
                        this.nextChunk = FileStore.this.createChunk((String) this.cursor.getValue());
                        Chunk chunk = (Chunk) FileStore.this.chunks.putIfAbsent(Integer.valueOf(this.nextChunk.id), this.nextChunk);
                        if (chunk != null) {
                            this.nextChunk = chunk;
                        }
                    }
                    return this.nextChunk != null;
                }

                @Override // java.util.Iterator
                public C next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    C c = (C) this.nextChunk;
                    this.nextChunk = null;
                    return c;
                }
            };
        };
    }

    private boolean isValidChunk(C c) {
        return readChunkHeaderAndFooter(c.block, c.id) != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final C readChunkHeaderAndFooter(long j, int i) {
        C readChunkFooter;
        C readChunkHeaderOptionally = readChunkHeaderOptionally(j, i);
        if (readChunkHeaderOptionally != null && ((readChunkFooter = readChunkFooter(j + readChunkHeaderOptionally.len)) == null || readChunkFooter.id != i || readChunkFooter.block != readChunkHeaderOptionally.block)) {
            return null;
        }
        return readChunkHeaderOptionally;
    }

    protected final C readChunkHeaderOptionally(long j, int i) {
        C readChunkHeaderOptionally = readChunkHeaderOptionally(j);
        if (readChunkHeaderOptionally == null || readChunkHeaderOptionally.id != i) {
            return null;
        }
        return readChunkHeaderOptionally;
    }

    protected final C readChunkHeaderOptionally(long j) {
        try {
            C readChunkHeader = readChunkHeader(j);
            if (readChunkHeader.block != j) {
                return null;
            }
            return readChunkHeader;
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected final C readChunkFooter(long j) {
        try {
            long j2 = (j * 4096) - 128;
            if (j2 < 0) {
                return null;
            }
            ByteBuffer readFully = readFully((FileStore<C>) null, j2, 128);
            byte[] bArr = new byte[128];
            readFully.get(bArr);
            HashMap<String, String> parseChecksummedMap = DataUtils.parseChecksummedMap(bArr);
            if (parseChecksummedMap != null) {
                C c = (C) createChunk(parseChecksummedMap);
                if (c.block == 0) {
                    c.block = j - c.len;
                }
                return c;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public WriteBuffer getWriteBuffer() {
        WriteBuffer poll = this.writeBufferPool.poll();
        if (poll != null) {
            poll.clear();
        } else {
            poll = new WriteBuffer();
        }
        return poll;
    }

    public void releaseWriteBuffer(WriteBuffer writeBuffer) {
        if (writeBuffer.capacity() <= 4194304) {
            this.writeBufferPool.offer(writeBuffer);
        }
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getAutoCompactFillRate() {
        return this.autoCompactFillRate;
    }

    public void sync() {
    }

    public long size() {
        return this.size;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setSize(long j) {
        this.size = j;
    }

    public long getWriteCount() {
        return this.writeCount.get();
    }

    private long getWriteBytes() {
        return this.writeBytes.get();
    }

    public long getReadCount() {
        return this.readCount.get();
    }

    public long getReadBytes() {
        return this.readBytes.get();
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public int getDefaultRetentionTime() {
        return 45000;
    }

    public void clear() {
        this.saveChunkLock.lock();
        try {
            this.deadChunks.clear();
            this.lastChunk = null;
            this.readCount.set(0L);
            this.readBytes.set(0L);
            this.writeCount.set(0L);
            this.writeBytes.set(0L);
        } finally {
            this.saveChunkLock.unlock();
        }
    }

    public String getFileName() {
        return this.fileName;
    }

    protected final MVStore getMvStore() {
        return this.mvStore;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ConcurrentMap<Integer, C> getChunks() {
        return this.chunks;
    }

    protected Collection<C> getRewriteCandidates() {
        return null;
    }

    public boolean isSpaceReused() {
        return true;
    }

    public void setReuseSpace(boolean z) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void store() {
        this.serializationLock.unlock();
        try {
            this.mvStore.storeNow();
        } finally {
            this.serializationLock.lock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void storeIt(ArrayList<Page<?, ?>> arrayList, long j, boolean z) throws ExecutionException {
        this.lastCommitTime = getTimeSinceCreation();
        this.serializationExecutorHWM = submitOrRun(this.serializationExecutor, () -> {
            serializeAndStore(z, arrayList, this.lastCommitTime, j);
        }, z, 3, this.serializationExecutorHWM);
    }

    private static int submitOrRun(ThreadPoolExecutor threadPoolExecutor, Runnable runnable, boolean z, int i, int i2) throws ExecutionException {
        if (threadPoolExecutor != null) {
            try {
                Future<?> submit = threadPoolExecutor.submit(runnable);
                int size = threadPoolExecutor.getQueue().size();
                if (size > i2) {
                    i2 = size;
                }
                if (z || size > i) {
                    try {
                        submit.get();
                    } catch (InterruptedException e) {
                    }
                }
                return i2;
            } catch (RejectedExecutionException e2) {
                if (!$assertionsDisabled && !threadPoolExecutor.isShutdown()) {
                    throw new AssertionError();
                }
                Utils.shutdownExecutor(threadPoolExecutor);
            }
        }
        runnable.run();
        return i2;
    }

    private void serializeAndStore(boolean z, ArrayList<Page<?, ?>> arrayList, long j, long j2) {
        this.serializationLock.lock();
        try {
            try {
                C c = null;
                int i = this.lastChunkId;
                if (i != 0) {
                    i &= Chunk.MAX_ID;
                    c = this.chunks.get(Integer.valueOf(i));
                    if (!$assertionsDisabled && c == null) {
                        throw new AssertionError(this.lastChunkId + " (" + i + ") " + this.chunks);
                    }
                    j = Math.max(c.time, j);
                }
                try {
                    C createChunk = createChunk(j, j2);
                    WriteBuffer writeBuffer = getWriteBuffer();
                    serializeToBuffer(writeBuffer, arrayList, createChunk, c);
                    this.chunks.put(Integer.valueOf(createChunk.id), createChunk);
                    this.bufferSaveExecutorHWM = submitOrRun(this.bufferSaveExecutor, () -> {
                        storeBuffer(createChunk, writeBuffer);
                    }, z, 5, this.bufferSaveExecutorHWM);
                    Iterator<Page<?, ?>> it = arrayList.iterator();
                    while (it.hasNext()) {
                        it.next().releaseSavedPages();
                    }
                    this.serializationLock.unlock();
                } catch (Throwable th) {
                    this.lastChunkId = i;
                    throw th;
                }
            } catch (MVStoreException e) {
                this.mvStore.panic(e);
                this.serializationLock.unlock();
            } catch (Throwable th2) {
                this.mvStore.panic(DataUtils.newMVStoreException(3, "{0}", th2.toString(), th2));
                this.serializationLock.unlock();
            }
        } catch (Throwable th3) {
            this.serializationLock.unlock();
            throw th3;
        }
    }

    private void serializeToBuffer(WriteBuffer writeBuffer, ArrayList<Page<?, ?>> arrayList, C c, C c2) {
        int estimateHeaderSize = c.estimateHeaderSize();
        writeBuffer.position(estimateHeaderSize);
        c.next = estimateHeaderSize;
        long j = c.version;
        PageSerializationManager pageSerializationManager = new PageSerializationManager(c, writeBuffer);
        Iterator<Page<?, ?>> it = arrayList.iterator();
        while (it.hasNext()) {
            Page<?, ?> next = it.next();
            String mapRootKey = MVMap.getMapRootKey(next.getMapId());
            if (next.getTotalCount() == 0) {
                this.layout.remove(mapRootKey);
            } else {
                next.writeUnsavedRecursive(pageSerializationManager);
                this.layout.put(mapRootKey, Long.toHexString(next.getPos()));
            }
        }
        acceptChunkOccupancyChanges(c.time, j);
        if (c2 != null && !this.layout.containsKey(Chunk.getMetaKey(c2.id))) {
            saveChunkMetadataChanges(c2);
        }
        RootReference<String, String> writeVersion = this.layout.setWriteVersion(j);
        if (!$assertionsDisabled && writeVersion == null) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && writeVersion.version != j) {
            throw new AssertionError(writeVersion.version + " != " + j);
        }
        acceptChunkOccupancyChanges(c.time, j);
        this.mvStore.onVersionChange(j);
        Page<?, ?> page = writeVersion.root;
        page.writeUnsavedRecursive(pageSerializationManager);
        c.layoutRootPos = page.getPos();
        arrayList.add(page);
        c.mapId = this.mvStore.getLastMapId();
        c.tocPos = writeBuffer.position();
        pageSerializationManager.serializeToC();
        writeBuffer.limit(MathUtils.roundUpInt(writeBuffer.position() + 128, 4096));
        c.len = writeBuffer.limit() / 4096;
        c.buffer = writeBuffer.getBuffer();
    }

    private void storeBuffer(C c, WriteBuffer writeBuffer) {
        this.saveChunkLock.lock();
        try {
            try {
                if (this.closed) {
                    throw DataUtils.newMVStoreException(2, "This fileStore is closed", new Object[0]);
                }
                int i = (int) c.next;
                allocateChunkSpace(c, writeBuffer);
                writeBuffer.position(0);
                c.writeChunkHeader(writeBuffer, i);
                writeBuffer.position(writeBuffer.limit() - 128);
                writeBuffer.put(c.getFooterBytes());
                writeBuffer.position(0);
                writeChunk(c, writeBuffer);
                this.lastChunk = c;
                this.saveChunkLock.unlock();
                c.buffer = null;
                releaseWriteBuffer(writeBuffer);
            } catch (MVStoreException e) {
                this.mvStore.panic(e);
                this.saveChunkLock.unlock();
                c.buffer = null;
                releaseWriteBuffer(writeBuffer);
            } catch (Throwable th) {
                this.mvStore.panic(DataUtils.newMVStoreException(3, "{0}", th.toString(), th));
                this.saveChunkLock.unlock();
                c.buffer = null;
                releaseWriteBuffer(writeBuffer);
            }
        } catch (Throwable th2) {
            this.saveChunkLock.unlock();
            c.buffer = null;
            releaseWriteBuffer(writeBuffer);
            throw th2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void acceptChunkOccupancyChanges(long j, long j2) {
        if (!$assertionsDisabled && !this.serializationLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        }
        if (hasPersistentData()) {
            HashSet hashSet = new HashSet();
            while (true) {
                RemovedPageInfo peek = this.removedPages.peek();
                if (peek != null && peek.version < j2) {
                    RemovedPageInfo poll = this.removedPages.poll();
                    if (!$assertionsDisabled && poll == null) {
                        throw new AssertionError();
                    }
                    if (!$assertionsDisabled && poll.version >= j2) {
                        throw new AssertionError(poll + " < " + j2);
                    }
                    int pageChunkId = poll.getPageChunkId();
                    C c = this.chunks.get(Integer.valueOf(pageChunkId));
                    if (!$assertionsDisabled && this.mvStore.isOpen() && c == null) {
                        throw new AssertionError(pageChunkId);
                    }
                    if (c != null) {
                        hashSet.add(c);
                        if (c.accountForRemovedPage(poll.getPageNo(), poll.getPageLength(), poll.isPinned(), j, poll.version)) {
                            registerDeadChunk(c);
                        }
                    }
                } else {
                    if (hashSet.isEmpty()) {
                        return;
                    }
                    Iterator it = hashSet.iterator();
                    while (it.hasNext()) {
                        saveChunkMetadataChanges((Chunk) it.next());
                    }
                    hashSet.clear();
                }
            }
        }
    }

    public int getChunksFillRate() {
        return getChunksFillRate(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRewritableChunksFillRate() {
        return getChunksFillRate(false);
    }

    private int getChunksFillRate(boolean z) {
        long j = 1;
        long j2 = 1;
        long timeSinceCreation = getTimeSinceCreation();
        for (C c : this.chunks.values()) {
            if (z || isRewritable(c, timeSinceCreation)) {
                if (!$assertionsDisabled && c.maxLen < 0) {
                    throw new AssertionError();
                }
                j += c.maxLen;
                j2 += c.maxLenLive;
            }
        }
        return (int) ((100 * j2) / j);
    }

    private int getChunkCount() {
        return this.chunks.size();
    }

    private int getPageCount() {
        int i = 0;
        Iterator<C> it = this.chunks.values().iterator();
        while (it.hasNext()) {
            i += it.next().pageCount;
        }
        return i;
    }

    private int getLivePageCount() {
        int i = 0;
        Iterator<C> it = this.chunks.values().iterator();
        while (it.hasNext()) {
            i += it.next().pageCountLive;
        }
        return i;
    }

    void cachePage(Page<?, ?> page) {
        if (this.cache != null) {
            this.cache.put(page.getPos(), page, page.getMemory());
        }
    }

    public int getCacheSize() {
        if (this.cache == null) {
            return 0;
        }
        return (int) (this.cache.getMaxMemory() >> 20);
    }

    public int getCacheSizeUsed() {
        if (this.cache == null) {
            return 0;
        }
        return (int) (this.cache.getUsedMemory() >> 20);
    }

    public void setCacheSize(int i) {
        long j = i * FileSize.KB_COEFFICIENT * FileSize.KB_COEFFICIENT;
        if (this.cache != null) {
            this.cache.setMaxMemory(j);
            this.cache.clear();
        }
    }

    void cacheToC(C c, long[] jArr) {
        this.chunksToC.put(c.id, jArr, (jArr.length * 8) + 24);
    }

    private long[] cleanToCCache(C c) {
        return this.chunksToC.remove(c.id);
    }

    public void populateInfo(BiConsumer<String, String> biConsumer) {
        biConsumer.accept("info.FILE_WRITE", Long.toString(getWriteCount()));
        biConsumer.accept("info.FILE_WRITE_BYTES", Long.toString(getWriteBytes()));
        biConsumer.accept("info.FILE_READ", Long.toString(getReadCount()));
        biConsumer.accept("info.FILE_READ_BYTES", Long.toString(getReadBytes()));
        biConsumer.accept("info.FILL_RATE", Integer.toString(getFillRate()));
        biConsumer.accept("info.CHUNKS_FILL_RATE", Integer.toString(getChunksFillRate()));
        biConsumer.accept("info.CHUNKS_FILL_RATE_RW", Integer.toString(getRewritableChunksFillRate()));
        biConsumer.accept("info.FILE_SIZE", Long.toString(size()));
        biConsumer.accept("info.CHUNK_COUNT", Long.toString(getChunkCount()));
        biConsumer.accept("info.PAGE_COUNT", Long.toString(getPageCount()));
        biConsumer.accept("info.PAGE_COUNT_LIVE", Long.toString(getLivePageCount()));
        biConsumer.accept("info.PAGE_SIZE", Long.toString(getMaxPageSize()));
        biConsumer.accept("info.CACHE_MAX_SIZE", Integer.toString(getCacheSize()));
        biConsumer.accept("info.CACHE_SIZE", Integer.toString(getCacheSizeUsed()));
        biConsumer.accept("info.CACHE_HIT_RATIO", Integer.toString(getCacheHitRatio()));
        biConsumer.accept("info.TOC_CACHE_HIT_RATIO", Integer.toString(getTocCacheHitRatio()));
    }

    public int getCacheHitRatio() {
        return getCacheHitRatio(this.cache);
    }

    public int getTocCacheHitRatio() {
        return getCacheHitRatio(this.chunksToC);
    }

    private static int getCacheHitRatio(CacheLongKeyLIRS<?> cacheLongKeyLIRS) {
        if (cacheLongKeyLIRS == null) {
            return 0;
        }
        long hits = cacheLongKeyLIRS.getHits();
        return (int) ((100 * hits) / ((hits + cacheLongKeyLIRS.getMisses()) + 1));
    }

    boolean isBackgroundThread() {
        return Thread.currentThread() == this.backgroundWriterThread.get();
    }

    private void stopBackgroundThread(boolean z) {
        BackgroundWriterThread backgroundWriterThread;
        do {
            backgroundWriterThread = this.backgroundWriterThread.get();
            if (backgroundWriterThread == null) {
                return;
            }
        } while (!this.backgroundWriterThread.compareAndSet(backgroundWriterThread, null));
        if (backgroundWriterThread != Thread.currentThread()) {
            synchronized (backgroundWriterThread.sync) {
                backgroundWriterThread.sync.notifyAll();
            }
            if (z) {
                try {
                    backgroundWriterThread.join();
                } catch (Exception e) {
                }
            }
        }
        shutdownExecutors();
    }

    private void shutdownExecutors() {
        Utils.shutdownExecutor(this.serializationExecutor);
        this.serializationExecutor = null;
        Utils.shutdownExecutor(this.bufferSaveExecutor);
        this.bufferSaveExecutor = null;
    }

    private Iterable<C> findOldChunks(int i, int i2) {
        Chunk chunk;
        if (!$assertionsDisabled && !hasPersistentData()) {
            throw new AssertionError();
        }
        long timeSinceCreation = getTimeSinceCreation();
        PriorityQueue priorityQueue = new PriorityQueue((this.chunks.size() / 4) + 1, (chunk2, chunk3) -> {
            int compare = Integer.compare(chunk3.collectPriority, chunk2.collectPriority);
            if (compare == 0) {
                compare = Long.compare(chunk3.maxLenLive, chunk2.maxLenLive);
            }
            return compare;
        });
        long j = 0;
        long lastChunkVersion = lastChunkVersion() + 1;
        Collection<C> rewriteCandidates = getRewriteCandidates();
        if (rewriteCandidates == null) {
            rewriteCandidates = this.chunks.values();
        }
        for (C c : rewriteCandidates) {
            int fillRate = c.getFillRate();
            if (isRewritable(c, timeSinceCreation) && fillRate <= i2) {
                c.collectPriority = (int) ((fillRate * 1000) / Math.max(1L, lastChunkVersion - c.version));
                j += c.maxLenLive;
                priorityQueue.offer(c);
                while (j > i && (chunk = (Chunk) priorityQueue.poll()) != null) {
                    j -= chunk.maxLenLive;
                }
            }
        }
        if (priorityQueue.isEmpty()) {
            return null;
        }
        return priorityQueue;
    }

    void writeInBackground() {
        try {
            if (this.mvStore.isOpen() && !isReadOnly()) {
                if (getTimeSinceCreation() > this.lastCommitTime + this.autoCommitDelay) {
                    this.mvStore.tryCommit();
                }
                doHousekeeping(this.mvStore);
                this.autoCompactLastFileOpCount = getWriteCount() + getReadCount();
            }
        } catch (InterruptedException e) {
        } catch (Throwable th) {
            if (!this.mvStore.handleException(th)) {
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean rewriteChunks(int i, int i2) {
        boolean z;
        this.serializationLock.lock();
        try {
            MVStore.TxCounter registerVersionUsage = this.mvStore.registerVersionUsage();
            try {
                acceptChunkOccupancyChanges(getTimeSinceCreation(), this.mvStore.getCurrentVersion());
                Iterable<C> findOldChunks = findOldChunks(i, i2);
                if (findOldChunks != null) {
                    HashSet<Integer> createIdSet = createIdSet(findOldChunks);
                    if (!createIdSet.isEmpty()) {
                        if (compactRewrite(createIdSet) > 0) {
                            z = true;
                            boolean z2 = z;
                            this.serializationLock.unlock();
                            return z2;
                        }
                    }
                    z = false;
                    boolean z22 = z;
                    this.serializationLock.unlock();
                    return z22;
                }
                this.mvStore.deregisterVersionUsage(registerVersionUsage);
                return false;
            } finally {
                this.mvStore.deregisterVersionUsage(registerVersionUsage);
            }
        } finally {
            this.serializationLock.unlock();
        }
    }

    private static <C extends Chunk<C>> HashSet<Integer> createIdSet(Iterable<C> iterable) {
        HashSet<Integer> hashSet = new HashSet<>();
        Iterator<C> it = iterable.iterator();
        while (it.hasNext()) {
            hashSet.add(Integer.valueOf(it.next().id));
        }
        return hashSet;
    }

    public void executeFileStoreOperation(Runnable runnable) {
        Utils.flushExecutor(this.serializationExecutor);
        this.serializationLock.lock();
        try {
            Utils.flushExecutor(this.bufferSaveExecutor);
            runnable.run();
        } finally {
            this.serializationLock.unlock();
        }
    }

    private int compactRewrite(Set<Integer> set) {
        acceptChunkOccupancyChanges(getTimeSinceCreation(), this.mvStore.getCurrentVersion());
        int rewriteChunks = rewriteChunks(set, false);
        acceptChunkOccupancyChanges(getTimeSinceCreation(), this.mvStore.getCurrentVersion());
        return rewriteChunks + rewriteChunks(set, true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:52:0x000a, code lost:            continue;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int rewriteChunks(java.util.Set<java.lang.Integer> r5, boolean r6) {
        /*
            Method dump skipped, instructions count: 283
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.FileStore.rewriteChunks(java.util.Set, boolean):int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <K, V> Page<K, V> readPage(MVMap<K, V> mVMap, long j) {
        MVStoreException mVStoreException;
        boolean z;
        ByteBuffer slice;
        try {
            if (!DataUtils.isPageSaved(j)) {
                throw DataUtils.newMVStoreException(6, "Position 0", new Object[0]);
            }
            Page<K, V> readPageFromCache = readPageFromCache(j);
            if (readPageFromCache == null) {
                C chunk = getChunk(j);
                int pageOffset = DataUtils.getPageOffset(j);
                do {
                    mVStoreException = null;
                    ByteBuffer byteBuffer = chunk.buffer;
                    z = byteBuffer == null;
                    if (z) {
                        slice = chunk.readBufferForPage(this, pageOffset, j);
                    } else {
                        ByteBuffer duplicate = byteBuffer.duplicate();
                        duplicate.position(pageOffset);
                        slice = duplicate.slice();
                    }
                    try {
                        readPageFromCache = Page.read(slice, j, mVMap);
                    } catch (MVStoreException e) {
                        mVStoreException = e;
                    } catch (Exception e2) {
                        mVStoreException = DataUtils.newMVStoreException(6, "Unable to read the page at position 0x{0}, chunk {1}, offset 0x{3}", Long.toHexString(j), chunk, Long.toHexString(pageOffset), e2);
                    }
                } while (!z);
                if (mVStoreException != null) {
                    throw mVStoreException;
                }
                cachePage(readPageFromCache);
            }
            return readPageFromCache;
        } catch (MVStoreException e3) {
            if (this.recoveryMode) {
                return mVMap.createEmptyLeaf();
            }
            throw e3;
        }
    }

    private C getChunk(long j) {
        int pageChunkId = DataUtils.getPageChunkId(j);
        C c = this.chunks.get(Integer.valueOf(pageChunkId));
        if (c == null) {
            String str = this.layout.get(Chunk.getMetaKey(pageChunkId));
            if (str == null) {
                throw DataUtils.newMVStoreException(9, "Chunk {0} not found", Integer.valueOf(pageChunkId));
            }
            c = createChunk(str);
            if (!c.isSaved()) {
                throw DataUtils.newMVStoreException(6, "Chunk {0} is invalid", Integer.valueOf(pageChunkId));
            }
            this.chunks.put(Integer.valueOf(c.id), c);
        }
        return c;
    }

    private int calculatePageNo(long j) {
        int i = -1;
        long[] toC = getToC(getChunk(j));
        if (toC != null) {
            int pageOffset = DataUtils.getPageOffset(j);
            int i2 = 0;
            int length = toC.length - 1;
            while (true) {
                if (i2 > length) {
                    break;
                }
                int i3 = (i2 + length) >>> 1;
                long pageOffset2 = DataUtils.getPageOffset(toC[i3]);
                if (pageOffset2 >= pageOffset) {
                    if (pageOffset2 > pageOffset) {
                        length = i3 - 1;
                    } else {
                        i = i3;
                        break;
                    }
                } else {
                    i2 = i3 + 1;
                }
            }
        }
        return i;
    }

    private void clearCaches() {
        if (this.cache != null) {
            this.cache.clear();
        }
        if (this.chunksToC != null) {
            this.chunksToC.clear();
        }
        this.removedPages.clear();
    }

    private long[] getToC(C c) {
        if (c.tocPos == 0) {
            return null;
        }
        long[] jArr = this.chunksToC.get(c.id);
        if (jArr == null) {
            jArr = c.readToC(this);
            cacheToC(c, jArr);
        }
        if ($assertionsDisabled || jArr.length == c.pageCount) {
            return jArr;
        }
        throw new AssertionError(jArr.length + " != " + c.pageCount);
    }

    private <K, V> Page<K, V> readPageFromCache(long j) {
        if (this.cache == null) {
            return null;
        }
        return (Page) this.cache.get(j);
    }

    public void accountForRemovedPage(long j, long j2, boolean z, int i) {
        if (!$assertionsDisabled && !DataUtils.isPageSaved(j)) {
            throw new AssertionError();
        }
        if (i < 0) {
            i = calculatePageNo(j);
        }
        this.removedPages.add(new RemovedPageInfo(j, z, j2, i));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/FileStore$PageSerializationManager.class */
    public final class PageSerializationManager {
        private final C chunk;
        private final WriteBuffer buff;
        private final List<Long> toc = new ArrayList();

        PageSerializationManager(C c, WriteBuffer writeBuffer) {
            this.chunk = c;
            this.buff = writeBuffer;
        }

        public WriteBuffer getBuffer() {
            return this.buff;
        }

        private int getChunkId() {
            return this.chunk.id;
        }

        public int getPageNo() {
            return this.toc.size();
        }

        public long getPagePosition(int i, int i2, int i3, int i4) {
            long composeTocElement = DataUtils.composeTocElement(i, i2, i3, i4);
            this.toc.add(Long.valueOf(composeTocElement));
            long composePagePos = DataUtils.composePagePos(this.chunk.id, composeTocElement);
            this.buff.putInt(i2, i3).putShort(i2 + 4, (short) ((DataUtils.getCheckValue(getChunkId()) ^ DataUtils.getCheckValue(i2)) ^ DataUtils.getCheckValue(i3)));
            return composePagePos;
        }

        public void onPageSerialized(Page<?, ?> page, boolean z, int i, boolean z2) {
            FileStore.this.cachePage(page);
            if (!page.isLeaf()) {
                FileStore.this.cachePage(page);
            }
            this.chunk.accountForWrittenPage(i, z2);
            if (z) {
                FileStore.this.accountForRemovedPage(page.getPos(), this.chunk.version + 1, z2, page.pageNo);
            }
        }

        public void serializeToC() {
            long[] jArr = new long[this.toc.size()];
            int i = 0;
            Iterator<Long> it = this.toc.iterator();
            while (it.hasNext()) {
                long longValue = it.next().longValue();
                int i2 = i;
                i++;
                jArr[i2] = longValue;
                this.buff.putLong(longValue);
                FileStore.this.mvStore.countNewPage(DataUtils.isLeafPosition(longValue));
            }
            FileStore.this.cacheToC(this.chunk, jArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/FileStore$RemovedPageInfo.class */
    public static final class RemovedPageInfo implements Comparable<RemovedPageInfo> {
        final long version;
        final long removedPageInfo;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !FileStore.class.desiredAssertionStatus();
        }

        RemovedPageInfo(long j, boolean z, long j2, int i) {
            this.removedPageInfo = createRemovedPageInfo(j, z, i);
            this.version = j2;
        }

        @Override // java.lang.Comparable
        public int compareTo(RemovedPageInfo removedPageInfo) {
            return Long.compare(this.version, removedPageInfo.version);
        }

        int getPageChunkId() {
            return DataUtils.getPageChunkId(this.removedPageInfo);
        }

        int getPageNo() {
            return DataUtils.getPageOffset(this.removedPageInfo);
        }

        int getPageLength() {
            return DataUtils.getPageMaxLength(this.removedPageInfo);
        }

        boolean isPinned() {
            return (this.removedPageInfo & 1) == 1;
        }

        private static long createRemovedPageInfo(long j, boolean z, int i) {
            if (!$assertionsDisabled && i < 0) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && (i >> 26) != 0) {
                throw new AssertionError();
            }
            long j2 = (j & (-274877906882L)) | (i << 6);
            if (z) {
                j2 |= 1;
            }
            return j2;
        }

        public String toString() {
            return "RemovedPageInfo{version=" + this.version + ", chunk=" + getPageChunkId() + ", pageNo=" + getPageNo() + ", len=" + getPageLength() + (isPinned() ? ", pinned" : "") + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/FileStore$BackgroundWriterThread.class */
    public static final class BackgroundWriterThread extends Thread {
        public final Object sync;
        private final FileStore<?> store;
        private final int sleep;

        BackgroundWriterThread(FileStore<?> fileStore, int i, String str) {
            super("MVStore background writer " + str);
            this.sync = new Object();
            this.store = fileStore;
            this.sleep = i;
            setDaemon(true);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (this.store.isBackgroundThread()) {
                synchronized (this.sync) {
                    try {
                        this.sync.wait(this.sleep);
                    } catch (InterruptedException e) {
                    }
                }
                if (this.store.isBackgroundThread()) {
                    this.store.writeInBackground();
                } else {
                    return;
                }
            }
        }
    }
}

package org.h2.mvstore;

import cn.hutool.core.text.CharSequenceUtil;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import org.h2.mvstore.Chunk;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/RandomAccessStore.class */
public abstract class RandomAccessStore extends FileStore<SFChunk> {
    protected final FreeSpaceBitSet freeSpace;
    private volatile boolean reuseSpace;
    private long reservedLow;
    private long reservedHigh;
    static final /* synthetic */ boolean $assertionsDisabled;

    protected abstract void truncate(long j);

    @Override // org.h2.mvstore.FileStore
    protected /* bridge */ /* synthetic */ SFChunk createChunk(Map map) {
        return createChunk((Map<String, String>) map);
    }

    static {
        $assertionsDisabled = !RandomAccessStore.class.desiredAssertionStatus();
    }

    public RandomAccessStore(Map<String, Object> map) {
        super(map);
        this.freeSpace = new FreeSpaceBitSet(2, 4096);
        this.reuseSpace = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.FileStore
    public final SFChunk createChunk(int i) {
        return new SFChunk(i);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.FileStore
    public SFChunk createChunk(String str) {
        return new SFChunk(str);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.FileStore
    protected SFChunk createChunk(Map<String, String> map) {
        return new SFChunk(map);
    }

    @Override // org.h2.mvstore.FileStore
    public void markUsed(long j, int i) {
        this.freeSpace.markUsed(j, i);
    }

    private long allocate(int i, long j, long j2) {
        return this.freeSpace.allocate(i, j, j2);
    }

    private long predictAllocation(int i, long j, long j2) {
        return this.freeSpace.predictAllocation(i, j, j2);
    }

    @Override // org.h2.mvstore.FileStore
    public boolean shouldSaveNow(int i, int i2) {
        return i > i2;
    }

    private boolean isFragmented() {
        return this.freeSpace.isFragmented();
    }

    @Override // org.h2.mvstore.FileStore
    public boolean isSpaceReused() {
        return this.reuseSpace;
    }

    @Override // org.h2.mvstore.FileStore
    public void setReuseSpace(boolean z) {
        this.reuseSpace = z;
    }

    @Override // org.h2.mvstore.FileStore
    protected void freeChunkSpace(Iterable<SFChunk> iterable) {
        Iterator<SFChunk> it = iterable.iterator();
        while (it.hasNext()) {
            freeChunkSpace(it.next());
        }
        if (!$assertionsDisabled && !validateFileLength(String.valueOf(iterable))) {
            throw new AssertionError();
        }
    }

    private void freeChunkSpace(SFChunk sFChunk) {
        free(sFChunk.block * 4096, sFChunk.len * 4096);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void free(long j, int i) {
        this.freeSpace.free(j, i);
    }

    @Override // org.h2.mvstore.FileStore
    public int getFillRate() {
        this.saveChunkLock.lock();
        try {
            return this.freeSpace.getFillRate();
        } finally {
            this.saveChunkLock.unlock();
        }
    }

    @Override // org.h2.mvstore.FileStore
    protected final boolean validateFileLength(String str) {
        if (!$assertionsDisabled && !this.saveChunkLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && getFileLengthInUse() != measureFileLengthInUse()) {
            throw new AssertionError(getFileLengthInUse() + " != " + measureFileLengthInUse() + CharSequenceUtil.SPACE + str);
        }
        return true;
    }

    private long measureFileLengthInUse() {
        if (!$assertionsDisabled && !this.saveChunkLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        }
        long j = 2;
        for (SFChunk sFChunk : getChunks().values()) {
            if (sFChunk.isAllocated()) {
                j = Math.max(j, sFChunk.block + sFChunk.len);
            }
        }
        return j * 4096;
    }

    long getFirstFree() {
        return this.freeSpace.getFirstFree();
    }

    long getFileLengthInUse() {
        return this.freeSpace.getLastFree();
    }

    @Override // org.h2.mvstore.FileStore
    protected void readStoreHeader(boolean z) {
        SFChunk readChunkHeaderAndFooter;
        SFChunk sFChunk = null;
        boolean z2 = true;
        boolean z3 = false;
        ByteBuffer readFully = readFully((RandomAccessStore) null, 0L, 8192);
        byte[] bArr = new byte[4096];
        for (int i = 0; i <= 4096; i += 4096) {
            readFully.get(bArr);
            try {
                HashMap<String, String> parseChecksummedMap = DataUtils.parseChecksummedMap(bArr);
                if (parseChecksummedMap == null) {
                    z2 = false;
                } else {
                    long readHexLong = DataUtils.readHexLong(parseChecksummedMap, "version", 0L);
                    z2 = z2 && (sFChunk == null || readHexLong == sFChunk.version);
                    if (sFChunk == null || readHexLong > sFChunk.version) {
                        z3 = true;
                        this.storeHeader.putAll(parseChecksummedMap);
                        SFChunk readChunkHeaderAndFooter2 = readChunkHeaderAndFooter(DataUtils.readHexLong(parseChecksummedMap, "block", 2L), DataUtils.readHexInt(parseChecksummedMap, "chunk", 0));
                        if (readChunkHeaderAndFooter2 != null) {
                            sFChunk = readChunkHeaderAndFooter2;
                        }
                    }
                }
            } catch (Exception e) {
                z2 = false;
            }
        }
        if (!z3) {
            throw DataUtils.newMVStoreException(6, "Store header is corrupt: {0}", this);
        }
        processCommonHeaderAttributes();
        boolean z4 = (!z2 || sFChunk == null || z) ? false : true;
        if (z4) {
            z4 = DataUtils.readHexInt(this.storeHeader, "clean", 0) != 0;
        }
        long size = size() / 4096;
        Comparator comparator = (sFChunk2, sFChunk3) -> {
            int compare = Long.compare(sFChunk3.version, sFChunk2.version);
            if (compare == 0) {
                compare = Long.compare(sFChunk2.block, sFChunk3.block);
            }
            return compare;
        };
        HashMap hashMap = new HashMap();
        if (z4) {
            PriorityQueue priorityQueue = new PriorityQueue(20, Collections.reverseOrder(comparator));
            try {
                setLastChunk(sFChunk);
                Iterator<SFChunk> it = getChunksFromLayoutMap().iterator();
                while (it.hasNext()) {
                    priorityQueue.offer(it.next());
                    if (priorityQueue.size() == 20) {
                        priorityQueue.poll();
                    }
                }
                while (z4) {
                    SFChunk sFChunk4 = (SFChunk) priorityQueue.poll();
                    if (sFChunk4 == null) {
                        break;
                    }
                    SFChunk readChunkHeaderAndFooter3 = readChunkHeaderAndFooter(sFChunk4.block, sFChunk4.id);
                    z4 = readChunkHeaderAndFooter3 != null;
                    if (z4) {
                        hashMap.put(Long.valueOf(readChunkHeaderAndFooter3.block), readChunkHeaderAndFooter3);
                    }
                }
            } catch (IllegalStateException e2) {
                z4 = false;
            }
        } else {
            SFChunk discoverChunk = discoverChunk(size);
            if (discoverChunk != null) {
                size = discoverChunk.block;
                hashMap.put(Long.valueOf(size), discoverChunk);
                if (sFChunk == null || discoverChunk.version > sFChunk.version) {
                    sFChunk = discoverChunk;
                }
            }
            if (sFChunk != null) {
                while (true) {
                    hashMap.put(Long.valueOf(sFChunk.block), sFChunk);
                    if (sFChunk.next == 0 || sFChunk.next >= size || (readChunkHeaderAndFooter = readChunkHeaderAndFooter(sFChunk.next, sFChunk.id + 1)) == null || readChunkHeaderAndFooter.version <= sFChunk.version) {
                        break;
                    } else {
                        sFChunk = readChunkHeaderAndFooter;
                    }
                }
            }
        }
        if (!z4) {
            if (!(!z && findLastChunkWithCompleteValidChunkSet(comparator, hashMap, false))) {
                long j = size;
                while (true) {
                    SFChunk discoverChunk2 = discoverChunk(j);
                    if (discoverChunk2 == null) {
                        break;
                    }
                    j = discoverChunk2.block;
                    hashMap.put(Long.valueOf(j), discoverChunk2);
                }
                if (!findLastChunkWithCompleteValidChunkSet(comparator, hashMap, true) && hasPersistentData()) {
                    throw DataUtils.newMVStoreException(6, "File is corrupted - unable to recover a valid set of chunks", new Object[0]);
                }
            }
        }
        clear();
        for (SFChunk sFChunk5 : getChunks().values()) {
            if (sFChunk5.isAllocated()) {
                markUsed(sFChunk5.block * 4096, sFChunk5.len * 4096);
            }
            if (!sFChunk5.isLive()) {
                registerDeadChunk(sFChunk5);
            }
        }
        if (!$assertionsDisabled && !validateFileLength("on open")) {
            throw new AssertionError();
        }
    }

    @Override // org.h2.mvstore.FileStore
    protected void initializeStoreHeader(long j) {
        initializeCommonHeaderAttributes(j);
        writeStoreHeader();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.FileStore
    public final void allocateChunkSpace(SFChunk sFChunk, WriteBuffer writeBuffer) {
        long j = this.reservedLow;
        long afterLastBlock = this.reservedHigh > 0 ? this.reservedHigh : isSpaceReused() ? 0L : getAfterLastBlock();
        long allocate = allocate(writeBuffer.limit(), j, afterLastBlock);
        if (j > 0 || afterLastBlock == j) {
            sFChunk.next = predictAllocation(sFChunk.len, 0L, 0L);
        } else {
            sFChunk.next = 0L;
        }
        sFChunk.block = allocate / 4096;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.FileStore
    public final void writeChunk(SFChunk sFChunk, WriteBuffer writeBuffer) {
        long j = sFChunk.block * 4096;
        writeFully(sFChunk, j, writeBuffer.getBuffer());
        boolean z = j + ((long) writeBuffer.limit()) >= size();
        boolean shouldWriteStoreHeader = shouldWriteStoreHeader(sFChunk, z);
        this.lastChunk = sFChunk;
        if (shouldWriteStoreHeader) {
            writeStoreHeader();
        }
        if (!z) {
            shrinkStoreIfPossible(1);
        }
    }

    private boolean shouldWriteStoreHeader(SFChunk sFChunk, boolean z) {
        boolean z2 = false;
        if (!z) {
            SFChunk sFChunk2 = (SFChunk) this.lastChunk;
            if (sFChunk2 == null) {
                z2 = true;
            } else if (sFChunk2.next != sFChunk.block) {
                z2 = true;
            } else if (sFChunk2.version - DataUtils.readHexLong(this.storeHeader, "version", 0L) > 20) {
                z2 = true;
            } else {
                for (int readHexInt = DataUtils.readHexInt(this.storeHeader, "chunk", 0); !z2 && readHexInt <= sFChunk2.id; readHexInt++) {
                    z2 = !getChunks().containsKey(Integer.valueOf(readHexInt));
                }
            }
        }
        if (this.storeHeader.remove("clean") != null) {
            z2 = true;
        }
        return z2;
    }

    @Override // org.h2.mvstore.FileStore
    protected final void writeCleanShutdownMark() {
        shrinkStoreIfPossible(0);
        this.storeHeader.put("clean", 1);
        writeStoreHeader();
    }

    @Override // org.h2.mvstore.FileStore
    protected final void adjustStoreToLastChunk() {
        this.storeHeader.put("clean", 1);
        writeStoreHeader();
        readStoreHeader(false);
    }

    @Override // org.h2.mvstore.FileStore
    protected void compactStore(int i, long j, int i2, MVStore mVStore) {
        setRetentionTime(0);
        long nanoTime = System.nanoTime() + (j * 1000000);
        while (compact(i, i2)) {
            sync();
            compactMoveChunks(i, i2, mVStore);
            if (System.nanoTime() - nanoTime > 0) {
                return;
            }
        }
    }

    public void compactMoveChunks(int i, long j, MVStore mVStore) {
        if (isSpaceReused()) {
            mVStore.executeFilestoreOperation(() -> {
                dropUnusedChunks();
                this.saveChunkLock.lock();
                try {
                    if (hasPersistentData() && getFillRate() <= i) {
                        compactMoveChunks(j);
                    }
                } finally {
                    this.saveChunkLock.unlock();
                }
            });
        }
    }

    private void compactMoveChunks(long j) {
        Iterable<SFChunk> findChunksToMove = findChunksToMove(getFirstFree() / 4096, j);
        if (findChunksToMove != null) {
            compactMoveChunks(findChunksToMove);
        }
    }

    private Iterable<SFChunk> findChunksToMove(long j, long j2) {
        Chunk chunk;
        long j3 = j2 / 4096;
        ArrayList arrayList = null;
        if (j3 > 0) {
            PriorityQueue priorityQueue = new PriorityQueue((getChunks().size() / 2) + 1, (sFChunk, sFChunk2) -> {
                int compare = Integer.compare(sFChunk2.collectPriority, sFChunk.collectPriority);
                if (compare != 0) {
                    return compare;
                }
                return Long.signum(sFChunk2.block - sFChunk.block);
            });
            long j4 = 0;
            for (SFChunk sFChunk3 : getChunks().values()) {
                if (sFChunk3.isAllocated() && sFChunk3.block > j) {
                    sFChunk3.collectPriority = getMovePriority(sFChunk3);
                    priorityQueue.offer(sFChunk3);
                    long j5 = j4 + sFChunk3.len;
                    while (true) {
                        j4 = j5;
                        if (j4 > j3 && (chunk = (Chunk) priorityQueue.poll()) != null) {
                            j5 = j4 - chunk.len;
                        }
                    }
                }
            }
            if (!priorityQueue.isEmpty()) {
                ArrayList arrayList2 = new ArrayList(priorityQueue);
                arrayList2.sort(Chunk.PositionComparator.instance());
                arrayList = arrayList2;
            }
        }
        return arrayList;
    }

    private int getMovePriority(SFChunk sFChunk) {
        return getMovePriority((int) sFChunk.block);
    }

    private void compactMoveChunks(Iterable<SFChunk> iterable) {
        if (!$assertionsDisabled && !this.saveChunkLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        }
        if (iterable != null) {
            writeStoreHeader();
            sync();
            Iterator<SFChunk> it = iterable.iterator();
            if (!$assertionsDisabled && !it.hasNext()) {
                throw new AssertionError();
            }
            long j = it.next().block;
            long afterLastBlock = getAfterLastBlock();
            Iterator<SFChunk> it2 = iterable.iterator();
            while (it2.hasNext()) {
                moveChunk(it2.next(), j, afterLastBlock);
            }
            store(j, afterLastBlock);
            sync();
            SFChunk sFChunk = (SFChunk) this.lastChunk;
            if (!$assertionsDisabled && sFChunk == null) {
                throw new AssertionError();
            }
            long afterLastBlock2 = getAfterLastBlock();
            boolean z = sFChunk.block < j;
            boolean z2 = !z;
            for (SFChunk sFChunk2 : iterable) {
                if (sFChunk2.block >= afterLastBlock && moveChunk(sFChunk2, afterLastBlock, afterLastBlock2)) {
                    if (!$assertionsDisabled && sFChunk2.block >= afterLastBlock) {
                        throw new AssertionError();
                    }
                    z2 = true;
                }
            }
            if (!$assertionsDisabled && afterLastBlock2 < getAfterLastBlock()) {
                throw new AssertionError();
            }
            if (z2) {
                boolean moveChunkInside = moveChunkInside(sFChunk, afterLastBlock);
                store(afterLastBlock, afterLastBlock2);
                sync();
                long j2 = (moveChunkInside || z) ? afterLastBlock2 : sFChunk.block;
                boolean z3 = !moveChunkInside && moveChunkInside(sFChunk, j2);
                if (moveChunkInside((SFChunk) this.lastChunk, j2) || z3) {
                    store(j2, -1L);
                }
            }
            shrinkStoreIfPossible(0);
            sync();
        }
    }

    private void writeStoreHeader() {
        StringBuilder sb = new StringBuilder(112);
        if (hasPersistentData()) {
            this.storeHeader.put("block", Long.valueOf(((SFChunk) this.lastChunk).block));
            this.storeHeader.put("chunk", Integer.valueOf(((SFChunk) this.lastChunk).id));
            this.storeHeader.put("version", Long.valueOf(((SFChunk) this.lastChunk).version));
        }
        DataUtils.appendMap(sb, this.storeHeader);
        byte[] bytes = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        DataUtils.appendMap(sb, "fletcher", DataUtils.getFletcher32(bytes, 0, bytes.length));
        sb.append('\n');
        byte[] bytes2 = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        ByteBuffer allocate = ByteBuffer.allocate(8192);
        allocate.put(bytes2);
        allocate.position(4096);
        allocate.put(bytes2);
        allocate.rewind();
        writeFully(null, 0L, allocate);
    }

    private void store(long j, long j2) {
        this.reservedLow = j;
        this.reservedHigh = j2;
        this.saveChunkLock.unlock();
        try {
            store();
            this.saveChunkLock.lock();
            this.reservedLow = 0L;
            this.reservedHigh = 0L;
        } catch (Throwable th) {
            this.saveChunkLock.lock();
            this.reservedLow = 0L;
            this.reservedHigh = 0L;
            throw th;
        }
    }

    private boolean moveChunkInside(SFChunk sFChunk, long j) {
        boolean z = sFChunk.block >= j && predictAllocation(sFChunk.len, j, -1L) < j && moveChunk(sFChunk, j, -1L);
        if ($assertionsDisabled || !z || sFChunk.block + sFChunk.len <= j) {
            return z;
        }
        throw new AssertionError();
    }

    private boolean moveChunk(SFChunk sFChunk, long j, long j2) {
        if (!getChunks().containsKey(Integer.valueOf(sFChunk.id))) {
            return false;
        }
        long j3 = sFChunk.block * 4096;
        int i = sFChunk.len * 4096;
        long allocate = allocate(i, j, j2);
        long j4 = allocate / 4096;
        if (!$assertionsDisabled && j2 <= 0 && j4 > sFChunk.block) {
            throw new AssertionError(j4 + CharSequenceUtil.SPACE + sFChunk);
        }
        writeFully(null, allocate, readFully((RandomAccessStore) sFChunk, j3, i));
        free(j3, i);
        sFChunk.block = j4;
        sFChunk.next = 0L;
        saveChunkMetadataChanges(sFChunk);
        return true;
    }

    @Override // org.h2.mvstore.FileStore
    protected void shrinkStoreIfPossible(int i) {
        if (!$assertionsDisabled && !this.saveChunkLock.isHeldByCurrentThread()) {
            throw new AssertionError();
        }
        long fileLengthInUse = getFileLengthInUse();
        if (!$assertionsDisabled && fileLengthInUse != measureFileLengthInUse()) {
            throw new AssertionError(fileLengthInUse + " != " + measureFileLengthInUse());
        }
        shrinkIfPossible(i);
    }

    private void shrinkIfPossible(int i) {
        if (isReadOnly()) {
            return;
        }
        long fileLengthInUse = getFileLengthInUse();
        long size = size();
        if (fileLengthInUse >= size) {
            return;
        }
        if ((i > 0 && size - fileLengthInUse < 4096) || ((int) (100 - ((fileLengthInUse * 100) / size))) < i) {
            return;
        }
        sync();
        truncate(fileLengthInUse);
    }

    @Override // org.h2.mvstore.FileStore
    protected void doHousekeeping(MVStore mVStore) throws InterruptedException {
        int autoCommitMemory = mVStore.getAutoCommitMemory();
        int fillRate = getFillRate();
        if (isFragmented() && fillRate < getAutoCompactFillRate()) {
            mVStore.tryExecuteUnderStoreLock(() -> {
                int i = 2 * autoCommitMemory;
                if (isIdle()) {
                    i *= 4;
                }
                compactMoveChunks(101, i, mVStore);
                return true;
            });
        }
        int rewritableChunksFillRate = getRewritableChunksFillRate();
        int i = 100 - ((100 - rewritableChunksFillRate) / 2);
        if ((isIdle() ? rewritableChunksFillRate : i) < getTargetFillRate()) {
            mVStore.tryExecuteUnderStoreLock(() -> {
                int i2 = autoCommitMemory;
                if (!isIdle()) {
                    i2 /= 4;
                }
                if (rewriteChunks(i2, isIdle() ? i : rewritableChunksFillRate)) {
                    dropUnusedChunks();
                }
                return true;
            });
        }
    }

    private int getTargetFillRate() {
        int autoCompactFillRate = getAutoCompactFillRate();
        if (!isIdle()) {
            autoCompactFillRate /= 2;
        }
        return autoCompactFillRate;
    }

    @Override // org.h2.mvstore.FileStore
    public void clear() {
        this.freeSpace.clear();
    }

    public int getMovePriority(int i) {
        return this.freeSpace.getMovePriority(i);
    }

    private long getAfterLastBlock() {
        if ($assertionsDisabled || this.saveChunkLock.isHeldByCurrentThread()) {
            return getAfterLastBlock_();
        }
        throw new AssertionError();
    }

    protected long getAfterLastBlock_() {
        return this.freeSpace.getAfterLastBlock();
    }

    @Override // org.h2.mvstore.FileStore
    public Collection<SFChunk> getRewriteCandidates() {
        if (isSpaceReused()) {
            return null;
        }
        return Collections.emptyList();
    }
}

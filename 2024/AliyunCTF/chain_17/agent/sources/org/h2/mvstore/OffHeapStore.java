package org.h2.mvstore;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/OffHeapStore.class */
public class OffHeapStore extends RandomAccessStore {
    private final TreeMap<Long, ByteBuffer> memory;

    public OffHeapStore() {
        super(new HashMap());
        this.memory = new TreeMap<>();
    }

    @Override // org.h2.mvstore.FileStore
    public void open(String str, boolean z, char[] cArr) {
        init();
    }

    @Override // org.h2.mvstore.FileStore
    /* renamed from: open */
    public FileStore<SFChunk> open2(String str, boolean z) {
        OffHeapStore offHeapStore = new OffHeapStore();
        offHeapStore.init();
        return offHeapStore;
    }

    private void init() {
        this.memory.clear();
    }

    public String toString() {
        return this.memory.toString();
    }

    @Override // org.h2.mvstore.FileStore
    public ByteBuffer readFully(SFChunk sFChunk, long j, int i) {
        Map.Entry<Long, ByteBuffer> floorEntry = this.memory.floorEntry(Long.valueOf(j));
        if (floorEntry == null) {
            throw DataUtils.newMVStoreException(1, "Could not read from position {0}", Long.valueOf(j));
        }
        this.readCount.incrementAndGet();
        this.readBytes.addAndGet(i);
        ByteBuffer duplicate = floorEntry.getValue().duplicate();
        int longValue = (int) (j - floorEntry.getKey().longValue());
        duplicate.position(longValue);
        duplicate.limit(i + longValue);
        return duplicate.slice();
    }

    @Override // org.h2.mvstore.RandomAccessStore
    public void free(long j, int i) {
        super.free(j, i);
        ByteBuffer remove = this.memory.remove(Long.valueOf(j));
        if (remove != null && remove.remaining() != i) {
            throw DataUtils.newMVStoreException(1, "Partial remove is not supported at position {0}", Long.valueOf(j));
        }
    }

    @Override // org.h2.mvstore.FileStore
    public void writeFully(SFChunk sFChunk, long j, ByteBuffer byteBuffer) {
        setSize(Math.max(size(), j + byteBuffer.remaining()));
        Map.Entry<Long, ByteBuffer> floorEntry = this.memory.floorEntry(Long.valueOf(j));
        if (floorEntry == null) {
            writeNewEntry(j, byteBuffer);
            return;
        }
        long longValue = floorEntry.getKey().longValue();
        ByteBuffer value = floorEntry.getValue();
        int capacity = value.capacity();
        int remaining = byteBuffer.remaining();
        if (longValue != j) {
            if (longValue + capacity > j) {
                throw DataUtils.newMVStoreException(1, "Could not write to position {0}; partial overwrite is not supported", Long.valueOf(j));
            }
            writeNewEntry(j, byteBuffer);
        } else {
            if (capacity != remaining) {
                throw DataUtils.newMVStoreException(1, "Could not write to position {0}; partial overwrite is not supported", Long.valueOf(j));
            }
            this.writeCount.incrementAndGet();
            this.writeBytes.addAndGet(remaining);
            value.rewind();
            value.put(byteBuffer);
        }
    }

    private void writeNewEntry(long j, ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        this.writeCount.incrementAndGet();
        this.writeBytes.addAndGet(remaining);
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(remaining);
        allocateDirect.put(byteBuffer);
        allocateDirect.rewind();
        this.memory.put(Long.valueOf(j), allocateDirect);
    }

    @Override // org.h2.mvstore.RandomAccessStore
    public void truncate(long j) {
        this.writeCount.incrementAndGet();
        setSize(j);
        if (j == 0) {
            this.memory.clear();
            return;
        }
        Iterator<Long> it = this.memory.keySet().iterator();
        while (it.hasNext()) {
            long longValue = it.next().longValue();
            if (longValue < j) {
                return;
            }
            if (this.memory.get(Long.valueOf(longValue)).capacity() > j) {
                throw DataUtils.newMVStoreException(1, "Could not truncate to {0}; partial truncate is not supported", Long.valueOf(longValue));
            }
            it.remove();
        }
    }

    @Override // org.h2.mvstore.FileStore
    public int getDefaultRetentionTime() {
        return 0;
    }

    @Override // org.h2.mvstore.FileStore
    public void backup(ZipOutputStream zipOutputStream) {
        throw new UnsupportedOperationException();
    }
}

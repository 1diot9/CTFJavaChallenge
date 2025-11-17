package org.h2.store.fs.niomem;

import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.h2.compress.CompressLZF;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/niomem/FileNioMemData.class */
class FileNioMemData {
    private static final int CACHE_MIN_SIZE = 8;
    private static final int BLOCK_SIZE_SHIFT = 16;
    private static final int BLOCK_SIZE = 65536;
    private static final int BLOCK_SIZE_MASK = 65535;
    private static final ByteBuffer COMPRESSED_EMPTY_BLOCK;
    private static final ThreadLocal<CompressLZF> LZF_THREAD_LOCAL = ThreadLocal.withInitial(CompressLZF::new);
    private static final ThreadLocal<byte[]> COMPRESS_OUT_BUF_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        return new byte[131072];
    });
    final int nameHashCode;
    private String name;
    private final boolean compress;
    private final float compressLaterCachePercent;
    private volatile long length;
    private boolean isReadOnly;
    private boolean isLockedExclusive;
    private int sharedLockCount;
    private final CompressLaterCache<CompressItem, CompressItem> compressLaterCache = new CompressLaterCache<>(8);
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private volatile AtomicReference<ByteBuffer>[] buffers = new AtomicReference[0];
    private long lastModified = System.currentTimeMillis();

    static {
        byte[] bArr = new byte[131072];
        int compress = new CompressLZF().compress(new byte[65536], 0, 65536, bArr, 0);
        COMPRESSED_EMPTY_BLOCK = ByteBuffer.allocateDirect(compress);
        COMPRESSED_EMPTY_BLOCK.put(bArr, 0, compress);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileNioMemData(String str, boolean z, float f) {
        this.name = str;
        this.nameHashCode = str.hashCode();
        this.compress = z;
        this.compressLaterCachePercent = f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean lockExclusive() {
        if (this.sharedLockCount > 0 || this.isLockedExclusive) {
            return false;
        }
        this.isLockedExclusive = true;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean lockShared() {
        if (this.isLockedExclusive) {
            return false;
        }
        this.sharedLockCount++;
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void unlock() {
        if (this.isLockedExclusive) {
            this.isLockedExclusive = false;
        } else {
            this.sharedLockCount = Math.max(0, this.sharedLockCount - 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/niomem/FileNioMemData$CompressLaterCache.class */
    public static class CompressLaterCache<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1;
        private int size;

        CompressLaterCache(int i) {
            super(i, 0.75f, true);
            this.size = i;
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public synchronized V put(K k, V v) {
            return (V) super.put(k, v);
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
            if (size() < this.size) {
                return false;
            }
            CompressItem compressItem = (CompressItem) entry.getKey();
            compressItem.data.compressPage(compressItem.page);
            return true;
        }

        public void setCacheSize(int i) {
            this.size = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/niomem/FileNioMemData$CompressItem.class */
    public static class CompressItem {
        public final FileNioMemData data;
        public final int page;

        public CompressItem(FileNioMemData fileNioMemData, int i) {
            this.data = fileNioMemData;
            this.page = i;
        }

        public int hashCode() {
            return this.page ^ this.data.nameHashCode;
        }

        public boolean equals(Object obj) {
            if (obj instanceof CompressItem) {
                CompressItem compressItem = (CompressItem) obj;
                return compressItem.data == this.data && compressItem.page == this.page;
            }
            return false;
        }
    }

    private void addToCompressLaterCache(int i) {
        CompressItem compressItem = new CompressItem(this, i);
        this.compressLaterCache.put(compressItem, compressItem);
    }

    private ByteBuffer expandPage(int i) {
        ByteBuffer byteBuffer = this.buffers[i].get();
        if (byteBuffer.capacity() == 65536) {
            return byteBuffer;
        }
        synchronized (byteBuffer) {
            if (byteBuffer.capacity() == 65536) {
                return byteBuffer;
            }
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(65536);
            if (byteBuffer != COMPRESSED_EMPTY_BLOCK) {
                byteBuffer.position(0);
                CompressLZF.expand(byteBuffer, allocateDirect);
            }
            this.buffers[i].compareAndSet(byteBuffer, allocateDirect);
            return allocateDirect;
        }
    }

    void compressPage(int i) {
        ByteBuffer byteBuffer = this.buffers[i].get();
        synchronized (byteBuffer) {
            if (byteBuffer.capacity() != 65536) {
                return;
            }
            byte[] bArr = COMPRESS_OUT_BUF_THREAD_LOCAL.get();
            int compress = LZF_THREAD_LOCAL.get().compress(byteBuffer, 0, bArr, 0);
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(compress);
            allocateDirect.put(bArr, 0, compress);
            this.buffers[i].compareAndSet(byteBuffer, allocateDirect);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void touch(boolean z) {
        if (this.isReadOnly || z) {
            throw new NonWritableChannelException();
        }
        this.lastModified = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long length() {
        return this.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void truncate(long j) {
        this.rwLock.writeLock().lock();
        try {
            changeLength(j);
            if (MathUtils.roundUpLong(j, 65536L) != j) {
                int i = (int) (j >>> 16);
                ByteBuffer expandPage = expandPage(i);
                for (int i2 = (int) (j & 65535); i2 < 65536; i2++) {
                    expandPage.put(i2, (byte) 0);
                }
                if (this.compress) {
                    addToCompressLaterCache(i);
                }
            }
        } finally {
            this.rwLock.writeLock().unlock();
        }
    }

    private void changeLength(long j) {
        this.length = j;
        int roundUpLong = (int) (MathUtils.roundUpLong(j, 65536L) >>> 16);
        if (roundUpLong != this.buffers.length) {
            AtomicReference<ByteBuffer>[] atomicReferenceArr = new AtomicReference[roundUpLong];
            System.arraycopy(this.buffers, 0, atomicReferenceArr, 0, Math.min(this.buffers.length, atomicReferenceArr.length));
            for (int length = this.buffers.length; length < roundUpLong; length++) {
                atomicReferenceArr[length] = new AtomicReference<>(COMPRESSED_EMPTY_BLOCK);
            }
            this.buffers = atomicReferenceArr;
        }
        this.compressLaterCache.setCacheSize(Math.max(8, (int) ((roundUpLong * this.compressLaterCachePercent) / 100.0f)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long readWrite(long j, ByteBuffer byteBuffer, int i, int i2, boolean z) {
        Lock writeLock = z ? this.rwLock.writeLock() : this.rwLock.readLock();
        writeLock.lock();
        try {
            long j2 = j + i2;
            if (j2 > this.length) {
                if (z) {
                    changeLength(j2);
                } else {
                    i2 = (int) (this.length - j);
                }
            }
            while (i2 > 0) {
                int min = (int) Math.min(i2, 65536 - (j & 65535));
                int i3 = (int) (j >>> 16);
                ByteBuffer expandPage = expandPage(i3);
                int i4 = (int) (j & 65535);
                if (z) {
                    ByteBuffer slice = byteBuffer.slice();
                    ByteBuffer duplicate = expandPage.duplicate();
                    slice.position(i);
                    slice.limit(i + min);
                    duplicate.position(i4);
                    duplicate.put(slice);
                } else {
                    ByteBuffer duplicate2 = expandPage.duplicate();
                    duplicate2.position(i4);
                    duplicate2.limit(min + i4);
                    int position = byteBuffer.position();
                    byteBuffer.position(i);
                    byteBuffer.put(duplicate2);
                    byteBuffer.position(position);
                }
                if (this.compress) {
                    addToCompressLaterCache(i3);
                }
                i += min;
                j += min;
                i2 -= min;
            }
            return j;
        } finally {
            writeLock.unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setName(String str) {
        this.name = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getName() {
        return this.name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getLastModified() {
        return this.lastModified;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canWrite() {
        return !this.isReadOnly;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setReadOnly() {
        this.isReadOnly = true;
        return true;
    }
}

package org.h2.store.fs.mem;

import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.nio.channels.NonWritableChannelException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.compress.CompressLZF;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/mem/FileMemData.class */
class FileMemData {
    private static final int CACHE_SIZE = 8;
    private static final int BLOCK_SIZE_SHIFT = 10;
    private static final int BLOCK_SIZE = 1024;
    private static final int BLOCK_SIZE_MASK = 1023;
    private String name;
    private final int id;
    private final boolean compress;
    private volatile long length;
    private AtomicReference<byte[]>[] data = new AtomicReference[0];
    private long lastModified = System.currentTimeMillis();
    private boolean isReadOnly;
    private boolean isLockedExclusive;
    private int sharedLockCount;
    private static final CompressLZF LZF = new CompressLZF();
    private static final byte[] BUFFER = new byte[2048];
    private static final Cache<CompressItem, CompressItem> COMPRESS_LATER = new Cache<>(8);
    private static final byte[] COMPRESSED_EMPTY_BLOCK = Arrays.copyOf(BUFFER, LZF.compress(new byte[1024], 0, 1024, BUFFER, 0));

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileMemData(String str, boolean z) {
        this.name = str;
        this.id = str.hashCode();
        this.compress = z;
    }

    private byte[] getPage(int i) {
        AtomicReference<byte[]>[] atomicReferenceArr = this.data;
        if (i >= atomicReferenceArr.length) {
            return null;
        }
        return atomicReferenceArr[i].get();
    }

    private void setPage(int i, byte[] bArr, byte[] bArr2, boolean z) {
        AtomicReference<byte[]>[] atomicReferenceArr = this.data;
        if (i >= atomicReferenceArr.length) {
            return;
        }
        if (z) {
            atomicReferenceArr[i].set(bArr2);
        } else {
            atomicReferenceArr[i].compareAndSet(bArr, bArr2);
        }
    }

    int getId() {
        return this.id;
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
    public synchronized void unlock() throws IOException {
        if (this.isLockedExclusive) {
            this.isLockedExclusive = false;
        } else {
            if (this.sharedLockCount > 0) {
                this.sharedLockCount--;
                return;
            }
            throw new IOException("not locked");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/mem/FileMemData$Cache.class */
    public static class Cache<K, V> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1;
        private final int size;

        Cache(int i) {
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
            compressItem.file.compress(compressItem.page);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/mem/FileMemData$CompressItem.class */
    public static class CompressItem {
        FileMemData file;
        int page;

        CompressItem() {
        }

        public int hashCode() {
            return this.page ^ this.file.getId();
        }

        public boolean equals(Object obj) {
            if (obj instanceof CompressItem) {
                CompressItem compressItem = (CompressItem) obj;
                return compressItem.page == this.page && compressItem.file == this.file;
            }
            return false;
        }
    }

    private void compressLater(int i) {
        CompressItem compressItem = new CompressItem();
        compressItem.file = this;
        compressItem.page = i;
        synchronized (LZF) {
            COMPRESS_LATER.put(compressItem, compressItem);
        }
    }

    private byte[] expand(int i) {
        byte[] page = getPage(i);
        if (page.length == 1024) {
            return page;
        }
        byte[] bArr = new byte[1024];
        if (page != COMPRESSED_EMPTY_BLOCK) {
            synchronized (LZF) {
                LZF.expand(page, 0, page.length, bArr, 0, 1024);
            }
        }
        setPage(i, page, bArr, false);
        return bArr;
    }

    void compress(int i) {
        byte[] page = getPage(i);
        if (page == null || page.length != 1024) {
            return;
        }
        synchronized (LZF) {
            int compress = LZF.compress(page, 0, 1024, BUFFER, 0);
            if (compress <= 1024) {
                setPage(i, page, Arrays.copyOf(BUFFER, compress), false);
            }
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
        changeLength(j);
        if (MathUtils.roundUpLong(j, FileSize.KB_COEFFICIENT) != j) {
            int i = (int) (j >>> 10);
            byte[] expand = expand(i);
            byte[] copyOf = Arrays.copyOf(expand, expand.length);
            for (int i2 = (int) (j & 1023); i2 < 1024; i2++) {
                copyOf[i2] = 0;
            }
            setPage(i, expand, copyOf, true);
            if (this.compress) {
                compressLater(i);
            }
        }
    }

    private void changeLength(long j) {
        this.length = j;
        int roundUpLong = (int) (MathUtils.roundUpLong(j, FileSize.KB_COEFFICIENT) >>> 10);
        if (roundUpLong != this.data.length) {
            AtomicReference<byte[]>[] atomicReferenceArr = (AtomicReference[]) Arrays.copyOf(this.data, roundUpLong);
            for (int length = this.data.length; length < roundUpLong; length++) {
                atomicReferenceArr[length] = new AtomicReference<>(COMPRESSED_EMPTY_BLOCK);
            }
            this.data = atomicReferenceArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long readWrite(long j, byte[] bArr, int i, int i2, boolean z) {
        long j2 = j + i2;
        if (j2 > this.length) {
            if (z) {
                changeLength(j2);
            } else {
                i2 = (int) (this.length - j);
            }
        }
        while (i2 > 0) {
            int min = (int) Math.min(i2, FileSize.KB_COEFFICIENT - (j & 1023));
            int i3 = (int) (j >>> 10);
            byte[] expand = expand(i3);
            int i4 = (int) (j & 1023);
            if (z) {
                byte[] copyOf = Arrays.copyOf(expand, expand.length);
                System.arraycopy(bArr, i, copyOf, i4, min);
                setPage(i3, expand, copyOf, true);
            } else {
                System.arraycopy(expand, i4, bArr, i, min);
            }
            if (this.compress) {
                compressLater(i3);
            }
            i += min;
            j += min;
            i2 -= min;
        }
        return j;
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

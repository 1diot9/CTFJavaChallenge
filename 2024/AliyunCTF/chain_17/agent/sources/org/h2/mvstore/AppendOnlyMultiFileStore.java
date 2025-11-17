package org.h2.mvstore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.ZipOutputStream;
import org.h2.mvstore.cache.FilePathCache;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.encrypt.FileEncrypt;
import org.h2.store.fs.encrypt.FilePathEncrypt;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/AppendOnlyMultiFileStore.class */
public final class AppendOnlyMultiFileStore extends FileStore<MFChunk> {
    private final int maxFileCount;
    private long creationTime;
    private int volumeId;
    private int fileCount;
    private FileChannel fileChannel;
    private FileChannel originalFileChannel;
    private final FileChannel[] fileChannels;
    private FileLock fileLock;
    private final Map<String, Object> config;
    static final /* synthetic */ boolean $assertionsDisabled;

    @Override // org.h2.mvstore.FileStore
    protected /* bridge */ /* synthetic */ MFChunk createChunk(Map map) {
        return createChunk((Map<String, String>) map);
    }

    static {
        $assertionsDisabled = !AppendOnlyMultiFileStore.class.desiredAssertionStatus();
    }

    public AppendOnlyMultiFileStore(Map<String, Object> map) {
        super(map);
        this.config = map;
        this.maxFileCount = DataUtils.getConfigParam(map, "maxFileCount", 16);
        this.fileChannels = new FileChannel[this.maxFileCount];
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.FileStore
    public final MFChunk createChunk(int i) {
        return new MFChunk(i);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.FileStore
    public MFChunk createChunk(String str) {
        return new MFChunk(str);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.mvstore.FileStore
    protected MFChunk createChunk(Map<String, String> map) {
        return new MFChunk(map);
    }

    @Override // org.h2.mvstore.FileStore
    public boolean shouldSaveNow(int i, int i2) {
        return i > i2;
    }

    @Override // org.h2.mvstore.FileStore
    public void open(String str, boolean z, char[] cArr) {
        open(str, z, cArr == null ? null : fileChannel -> {
            return new FileEncrypt(str, FilePathEncrypt.getPasswordBytes(cArr), fileChannel);
        });
    }

    @Override // org.h2.mvstore.FileStore
    /* renamed from: open */
    public FileStore<MFChunk> open2(String str, boolean z) {
        AppendOnlyMultiFileStore appendOnlyMultiFileStore = new AppendOnlyMultiFileStore(this.config);
        appendOnlyMultiFileStore.open(str, z, this.originalFileChannel == null ? null : fileChannel -> {
            return new FileEncrypt(str, (FileEncrypt) this.fileChannel, fileChannel);
        });
        return appendOnlyMultiFileStore;
    }

    private void open(String str, boolean z, Function<FileChannel, FileChannel> function) {
        if (this.fileChannel != null && this.fileChannel.isOpen()) {
            return;
        }
        FilePathCache.INSTANCE.getScheme();
        FilePath filePath = FilePath.get(str);
        FilePath parent = filePath.getParent();
        if (parent != null && !parent.exists()) {
            throw DataUtils.newIllegalArgumentException("Directory does not exist: {0}", parent);
        }
        if (filePath.exists() && !filePath.canWrite()) {
            z = true;
        }
        init(str, z);
        try {
            this.fileChannel = filePath.open(z ? "r" : "rw");
            if (function != null) {
                this.originalFileChannel = this.fileChannel;
                this.fileChannel = function.apply(this.fileChannel);
            }
            try {
                this.fileLock = this.fileChannel.tryLock(0L, Long.MAX_VALUE, z);
                if (this.fileLock == null) {
                    try {
                        close();
                    } catch (Exception e) {
                    }
                    throw DataUtils.newMVStoreException(7, "The file is locked: {0}", str);
                }
                this.saveChunkLock.lock();
                try {
                    setSize(this.fileChannel.size());
                    this.saveChunkLock.unlock();
                } catch (Throwable th) {
                    this.saveChunkLock.unlock();
                    throw th;
                }
            } catch (OverlappingFileLockException e2) {
                throw DataUtils.newMVStoreException(7, "The file is locked: {0}", str, e2);
            }
        } catch (IOException e3) {
            try {
                close();
            } catch (Exception e4) {
            }
            throw DataUtils.newMVStoreException(1, "Could not open file {0}", str, e3);
        }
    }

    @Override // org.h2.mvstore.FileStore
    public void close() {
        try {
            try {
                if (this.fileChannel.isOpen()) {
                    if (this.fileLock != null) {
                        this.fileLock.release();
                    }
                    this.fileChannel.close();
                }
            } catch (Exception e) {
                throw DataUtils.newMVStoreException(2, "Closing failed for file {0}", getFileName(), e);
            }
        } finally {
            this.fileLock = null;
            super.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.FileStore
    public void writeFully(MFChunk mFChunk, long j, ByteBuffer byteBuffer) {
        if (!$assertionsDisabled && mFChunk.volumeId != this.volumeId) {
            throw new AssertionError();
        }
        int remaining = byteBuffer.remaining();
        setSize(Math.max(super.size(), j + remaining));
        DataUtils.writeFully(this.fileChannels[this.volumeId], j, byteBuffer);
        this.writeCount.incrementAndGet();
        this.writeBytes.addAndGet(remaining);
    }

    @Override // org.h2.mvstore.FileStore
    public ByteBuffer readFully(MFChunk mFChunk, long j, int i) {
        return readFully(this.fileChannels[mFChunk.volumeId], j, i);
    }

    @Override // org.h2.mvstore.FileStore
    protected void initializeStoreHeader(long j) {
    }

    @Override // org.h2.mvstore.FileStore
    protected void readStoreHeader(boolean z) {
        byte[] bArr = new byte[4096];
        readFully(new MFChunk(""), 0L, 4096).get(bArr);
        try {
            HashMap<String, String> parseChecksummedMap = DataUtils.parseChecksummedMap(bArr);
            if (parseChecksummedMap == null) {
                throw DataUtils.newMVStoreException(6, "Store header is corrupt: {0}", this);
            }
            this.storeHeader.putAll(parseChecksummedMap);
            processCommonHeaderAttributes();
            setLastChunk(discoverChunk(size() / 4096));
            for (MFChunk mFChunk : getChunksFromLayoutMap()) {
                if (!mFChunk.isLive()) {
                    registerDeadChunk(mFChunk);
                }
            }
        } catch (Exception e) {
            throw DataUtils.newMVStoreException(6, "Store header is corrupt: {0}", this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.FileStore
    public void allocateChunkSpace(MFChunk mFChunk, WriteBuffer writeBuffer) {
        mFChunk.block = size() / 4096;
        setSize((mFChunk.block + mFChunk.len) * 4096);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.FileStore
    public void writeChunk(MFChunk mFChunk, WriteBuffer writeBuffer) {
        writeFully(mFChunk, mFChunk.block * 4096, writeBuffer.getBuffer());
    }

    @Override // org.h2.mvstore.FileStore
    protected void writeCleanShutdownMark() {
    }

    @Override // org.h2.mvstore.FileStore
    protected void adjustStoreToLastChunk() {
    }

    @Override // org.h2.mvstore.FileStore
    protected void compactStore(int i, long j, int i2, MVStore mVStore) {
    }

    @Override // org.h2.mvstore.FileStore
    protected void doHousekeeping(MVStore mVStore) throws InterruptedException {
    }

    @Override // org.h2.mvstore.FileStore
    public int getFillRate() {
        return 0;
    }

    @Override // org.h2.mvstore.FileStore
    protected void shrinkStoreIfPossible(int i) {
    }

    @Override // org.h2.mvstore.FileStore
    public void markUsed(long j, int i) {
    }

    @Override // org.h2.mvstore.FileStore
    protected void freeChunkSpace(Iterable<MFChunk> iterable) {
    }

    @Override // org.h2.mvstore.FileStore
    protected boolean validateFileLength(String str) {
        return true;
    }

    @Override // org.h2.mvstore.FileStore
    public void backup(ZipOutputStream zipOutputStream) throws IOException {
    }
}

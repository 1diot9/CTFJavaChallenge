package org.h2.mvstore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.h2.mvstore.cache.FilePathCache;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.encrypt.FileEncrypt;
import org.h2.store.fs.encrypt.FilePathEncrypt;
import org.h2.util.IOUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/SingleFileStore.class */
public class SingleFileStore extends RandomAccessStore {
    private FileChannel fileChannel;
    private FileChannel originalFileChannel;
    private FileLock fileLock;
    private final Map<String, Object> config;

    public SingleFileStore(Map<String, Object> map) {
        super(map);
        this.config = map;
    }

    public String toString() {
        return getFileName();
    }

    @Override // org.h2.mvstore.FileStore
    public ByteBuffer readFully(SFChunk sFChunk, long j, int i) {
        return readFully(this.fileChannel, j, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.mvstore.FileStore
    public void writeFully(SFChunk sFChunk, long j, ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        setSize(Math.max(super.size(), j + remaining));
        DataUtils.writeFully(this.fileChannel, j, byteBuffer);
        this.writeCount.incrementAndGet();
        this.writeBytes.addAndGet(remaining);
    }

    @Override // org.h2.mvstore.FileStore
    public void open(String str, boolean z, char[] cArr) {
        open(str, z, cArr == null ? null : fileChannel -> {
            return new FileEncrypt(str, FilePathEncrypt.getPasswordBytes(cArr), fileChannel);
        });
    }

    @Override // org.h2.mvstore.FileStore
    /* renamed from: open, reason: merged with bridge method [inline-methods] */
    public FileStore<SFChunk> open2(String str, boolean z) {
        SingleFileStore singleFileStore = new SingleFileStore(this.config);
        singleFileStore.open(str, z, this.originalFileChannel == null ? null : fileChannel -> {
            return new FileEncrypt(str, (FileEncrypt) this.fileChannel, fileChannel);
        });
        return singleFileStore;
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
            this.fileLock = lockFileChannel(this.fileChannel, z, str);
            this.saveChunkLock.lock();
            try {
                setSize(this.fileChannel.size());
                this.saveChunkLock.unlock();
            } catch (Throwable th) {
                this.saveChunkLock.unlock();
                throw th;
            }
        } catch (IOException e) {
            try {
                close();
            } catch (Exception e2) {
            }
            throw DataUtils.newMVStoreException(1, "Could not open file {0}", str, e);
        }
    }

    private FileLock lockFileChannel(FileChannel fileChannel, boolean z, String str) throws IOException {
        try {
            FileLock tryLock = fileChannel.tryLock(0L, Long.MAX_VALUE, z);
            if (tryLock == null) {
                try {
                    close();
                } catch (Exception e) {
                }
                throw DataUtils.newMVStoreException(7, "The file is locked: {0}", str);
            }
            return tryLock;
        } catch (OverlappingFileLockException e2) {
            throw DataUtils.newMVStoreException(7, "The file is locked: {0}", str, e2);
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

    @Override // org.h2.mvstore.FileStore
    public void sync() {
        if (this.fileChannel.isOpen()) {
            try {
                this.fileChannel.force(true);
            } catch (IOException e) {
                throw DataUtils.newMVStoreException(2, "Could not sync file {0}", getFileName(), e);
            }
        }
    }

    @Override // org.h2.mvstore.RandomAccessStore
    public void truncate(long j) {
        int i = 0;
        while (true) {
            try {
                this.writeCount.incrementAndGet();
                this.fileChannel.truncate(j);
                setSize(Math.min(super.size(), j));
                return;
            } catch (IOException e) {
                i++;
                if (i == 10) {
                    throw DataUtils.newMVStoreException(2, "Could not truncate file {0} to size {1}", getFileName(), Long.valueOf(j), e);
                }
                System.gc();
                Thread.yield();
            }
        }
    }

    @Override // org.h2.mvstore.RandomAccessStore
    public int getMovePriority(int i) {
        return this.freeSpace.getMovePriority(i);
    }

    @Override // org.h2.mvstore.RandomAccessStore
    protected long getAfterLastBlock_() {
        return this.freeSpace.getAfterLastBlock();
    }

    @Override // org.h2.mvstore.FileStore
    public void backup(ZipOutputStream zipOutputStream) throws IOException {
        boolean isSpaceReused = isSpaceReused();
        setReuseSpace(false);
        try {
            backupFile(zipOutputStream, getFileName(), this.originalFileChannel != null ? this.originalFileChannel : this.fileChannel);
        } finally {
            setReuseSpace(isSpaceReused);
        }
    }

    private static void backupFile(ZipOutputStream zipOutputStream, String str, FileChannel fileChannel) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(correctFileName(FilePath.get(str).toRealPath().getName())));
        IOUtils.copy(fileChannel, zipOutputStream);
        zipOutputStream.closeEntry();
    }

    public static String correctFileName(String str) {
        String replace = str.replace('\\', '/');
        if (replace.startsWith("/")) {
            replace = replace.substring(1);
        }
        return replace;
    }
}

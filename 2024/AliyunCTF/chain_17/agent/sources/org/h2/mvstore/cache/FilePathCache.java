package org.h2.mvstore.cache;

import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.h2.mvstore.cache.CacheLongKeyLIRS;
import org.h2.store.fs.FileBase;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FilePathWrapper;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/cache/FilePathCache.class */
public class FilePathCache extends FilePathWrapper {
    public static final FilePathCache INSTANCE = new FilePathCache();

    static {
        FilePath.register(INSTANCE);
    }

    public static FileChannel wrap(FileChannel fileChannel) {
        return new FileCache(fileChannel);
    }

    @Override // org.h2.store.fs.FilePathWrapper, org.h2.store.fs.FilePath
    public FileChannel open(String str) throws IOException {
        return new FileCache(getBase().open(str));
    }

    @Override // org.h2.store.fs.FilePath
    public String getScheme() {
        return "cache";
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/cache/FilePathCache$FileCache.class */
    public static class FileCache extends FileBase {
        private static final int CACHE_BLOCK_SIZE = 4096;
        private final FileChannel base;
        private final CacheLongKeyLIRS<ByteBuffer> cache;

        FileCache(FileChannel fileChannel) {
            CacheLongKeyLIRS.Config config = new CacheLongKeyLIRS.Config();
            config.maxMemory = FileSize.MB_COEFFICIENT;
            this.cache = new CacheLongKeyLIRS<>(config);
            this.base = fileChannel;
        }

        @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
        protected void implCloseChannel() throws IOException {
            this.base.close();
        }

        @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
        public FileChannel position(long j) throws IOException {
            this.base.position(j);
            return this;
        }

        @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
        public long position() throws IOException {
            return this.base.position();
        }

        @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
        public int read(ByteBuffer byteBuffer) throws IOException {
            return this.base.read(byteBuffer);
        }

        @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
        public synchronized int read(ByteBuffer byteBuffer, long j) throws IOException {
            long cachePos = getCachePos(j);
            int i = (int) (j - cachePos);
            int min = Math.min(4096 - i, byteBuffer.remaining());
            ByteBuffer byteBuffer2 = this.cache.get(cachePos);
            if (byteBuffer2 == null) {
                byteBuffer2 = ByteBuffer.allocate(4096);
                long j2 = cachePos;
                while (true) {
                    long j3 = j2;
                    int read = this.base.read(byteBuffer2, j3);
                    if (read > 0 && byteBuffer2.remaining() != 0) {
                        j2 = j3 + read;
                    }
                }
                int position = byteBuffer2.position();
                if (position == 4096) {
                    this.cache.put(cachePos, byteBuffer2, 4176L);
                } else {
                    if (position <= 0) {
                        return -1;
                    }
                    min = Math.min(min, position - i);
                }
            }
            byteBuffer.put(byteBuffer2.array(), i, min);
            if (min == 0) {
                return -1;
            }
            return min;
        }

        private static long getCachePos(long j) {
            return (j / 4096) * 4096;
        }

        @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
        public long size() throws IOException {
            return this.base.size();
        }

        @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
        public synchronized FileChannel truncate(long j) throws IOException {
            this.cache.clear();
            this.base.truncate(j);
            return this;
        }

        @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
        public synchronized int write(ByteBuffer byteBuffer, long j) throws IOException {
            clearCache(byteBuffer, j);
            return this.base.write(byteBuffer, j);
        }

        @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
        public synchronized int write(ByteBuffer byteBuffer) throws IOException {
            clearCache(byteBuffer, position());
            return this.base.write(byteBuffer);
        }

        private void clearCache(ByteBuffer byteBuffer, long j) {
            if (this.cache.size() > 0) {
                long cachePos = getCachePos(j);
                for (int remaining = byteBuffer.remaining(); remaining > 0; remaining -= 4096) {
                    this.cache.remove(cachePos);
                    cachePos += 4096;
                }
            }
        }

        @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
        public void force(boolean z) throws IOException {
            this.base.force(z);
        }

        @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
        public FileLock tryLock(long j, long j2, boolean z) throws IOException {
            return this.base.tryLock(j, j2, z);
        }

        public String toString() {
            return "cache:" + this.base.toString();
        }
    }
}

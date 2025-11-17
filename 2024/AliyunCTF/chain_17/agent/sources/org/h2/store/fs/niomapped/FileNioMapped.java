package org.h2.store.fs.niomapped;

import java.io.EOFException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.Paths;
import org.h2.engine.SysProperties;
import org.h2.store.fs.FileBaseDefault;
import org.h2.store.fs.FileUtils;
import org.h2.util.MemoryUnmapper;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/niomapped/FileNioMapped.class */
class FileNioMapped extends FileBaseDefault {
    private static final int GC_TIMEOUT_MS = 10000;
    private final String name;
    private final FileChannel.MapMode mode;
    private FileChannel channel;
    private MappedByteBuffer mapped;
    private long fileLength;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileNioMapped(String str, String str2) throws IOException {
        if ("r".equals(str2)) {
            this.mode = FileChannel.MapMode.READ_ONLY;
        } else {
            this.mode = FileChannel.MapMode.READ_WRITE;
        }
        this.name = str;
        this.channel = FileChannel.open(Paths.get(str, new String[0]), FileUtils.modeToOptions(str2), FileUtils.NO_ATTRIBUTES);
        reMap();
    }

    private void unMap() throws IOException {
        if (this.mapped == null) {
            return;
        }
        this.mapped.force();
        if (SysProperties.NIO_CLEANER_HACK && MemoryUnmapper.unmap(this.mapped)) {
            this.mapped = null;
            return;
        }
        WeakReference weakReference = new WeakReference(this.mapped);
        this.mapped = null;
        long nanoTime = System.nanoTime() + 10000000000L;
        while (weakReference.get() != null) {
            if (System.nanoTime() - nanoTime > 0) {
                throw new IOException("Timeout (10000 ms) reached while trying to GC mapped buffer");
            }
            System.gc();
            Thread.yield();
        }
    }

    private void reMap() throws IOException {
        if (this.mapped != null) {
            unMap();
        }
        this.fileLength = this.channel.size();
        checkFileSizeLimit(this.fileLength);
        this.mapped = this.channel.map(this.mode, 0L, this.fileLength);
        int limit = this.mapped.limit();
        int capacity = this.mapped.capacity();
        if (limit < this.fileLength || capacity < this.fileLength) {
            throw new IOException("Unable to map: length=" + limit + " capacity=" + capacity + " length=" + this.fileLength);
        }
        if (SysProperties.NIO_LOAD_MAPPED) {
            this.mapped.load();
        }
    }

    private static void checkFileSizeLimit(long j) throws IOException {
        if (j > 2147483647L) {
            throw new IOException("File over 2GB is not supported yet when using this file system");
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    public void implCloseChannel() throws IOException {
        if (this.channel != null) {
            unMap();
            this.channel.close();
            this.channel = null;
        }
    }

    public String toString() {
        return "nioMapped:" + this.name;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public synchronized long size() throws IOException {
        return this.fileLength;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized int read(ByteBuffer byteBuffer, long j) throws IOException {
        checkFileSizeLimit(j);
        try {
            int remaining = byteBuffer.remaining();
            if (remaining == 0) {
                return 0;
            }
            int min = (int) Math.min(remaining, this.fileLength - j);
            if (min <= 0) {
                return -1;
            }
            this.mapped.position((int) j);
            this.mapped.get(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), min);
            byteBuffer.position(byteBuffer.position() + min);
            long j2 = j + min;
            return min;
        } catch (IllegalArgumentException | BufferUnderflowException e) {
            EOFException eOFException = new EOFException("EOF");
            eOFException.initCause(e);
            throw eOFException;
        }
    }

    @Override // org.h2.store.fs.FileBaseDefault
    protected void implTruncate(long j) throws IOException {
        if (this.mode == FileChannel.MapMode.READ_ONLY) {
            throw new NonWritableChannelException();
        }
        if (j < size()) {
            setFileLength(j);
        }
    }

    public synchronized void setFileLength(long j) throws IOException {
        if (this.mode == FileChannel.MapMode.READ_ONLY) {
            throw new NonWritableChannelException();
        }
        checkFileSizeLimit(j);
        unMap();
        int i = 0;
        while (true) {
            try {
                if (this.channel.size() >= j) {
                    this.channel.truncate(j);
                } else {
                    this.channel.write(ByteBuffer.wrap(new byte[1]), j - 1);
                }
                reMap();
                return;
            } catch (IOException e) {
                if (i > 16 || !e.toString().contains("user-mapped section open")) {
                    throw e;
                }
                System.gc();
                i++;
            }
        }
        throw e;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
        this.mapped.force();
        this.channel.force(z);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized int write(ByteBuffer byteBuffer, long j) throws IOException {
        checkFileSizeLimit(j);
        int remaining = byteBuffer.remaining();
        if (this.mapped.capacity() < j + remaining) {
            setFileLength(j + remaining);
        }
        this.mapped.position((int) j);
        this.mapped.put(byteBuffer);
        return remaining;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized FileLock tryLock(long j, long j2, boolean z) throws IOException {
        return this.channel.tryLock(j, j2, z);
    }
}

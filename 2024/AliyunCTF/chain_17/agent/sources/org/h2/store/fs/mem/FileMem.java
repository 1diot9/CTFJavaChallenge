package org.h2.store.fs.mem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import org.h2.store.fs.FakeFileChannel;
import org.h2.store.fs.FileBaseDefault;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/mem/FileMem.class */
class FileMem extends FileBaseDefault {
    final FileMemData data;
    private final boolean readOnly;
    private volatile boolean closed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileMem(FileMemData fileMemData, boolean z) {
        this.data = fileMemData;
        this.readOnly = z;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() {
        return this.data.length();
    }

    @Override // org.h2.store.fs.FileBaseDefault
    protected void implTruncate(long j) throws IOException {
        if (this.readOnly) {
            throw new NonWritableChannelException();
        }
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (j < size()) {
            this.data.touch(this.readOnly);
            this.data.truncate(j);
        }
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int write(ByteBuffer byteBuffer, long j) throws IOException {
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (this.readOnly) {
            throw new NonWritableChannelException();
        }
        int remaining = byteBuffer.remaining();
        if (remaining == 0) {
            return 0;
        }
        this.data.touch(this.readOnly);
        this.data.readWrite(j, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), remaining, true);
        byteBuffer.position(byteBuffer.position() + remaining);
        return remaining;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int read(ByteBuffer byteBuffer, long j) throws IOException {
        if (this.closed) {
            throw new ClosedChannelException();
        }
        int remaining = byteBuffer.remaining();
        if (remaining == 0) {
            return 0;
        }
        int readWrite = (int) (this.data.readWrite(j, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), remaining, false) - j);
        if (readWrite <= 0) {
            return -1;
        }
        byteBuffer.position(byteBuffer.position() + readWrite);
        return readWrite;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    public void implCloseChannel() throws IOException {
        this.closed = true;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public FileLock tryLock(long j, long j2, boolean z) throws IOException {
        if (this.closed) {
            throw new ClosedChannelException();
        }
        if (z) {
            if (!this.data.lockShared()) {
                return null;
            }
        } else if (!this.data.lockExclusive()) {
            return null;
        }
        return new FileLock(FakeFileChannel.INSTANCE, j, j2, z) { // from class: org.h2.store.fs.mem.FileMem.1
            @Override // java.nio.channels.FileLock
            public boolean isValid() {
                return true;
            }

            @Override // java.nio.channels.FileLock
            public void release() throws IOException {
                FileMem.this.data.unlock();
            }
        };
    }

    public String toString() {
        return this.closed ? "<closed>" : this.data.getName();
    }
}

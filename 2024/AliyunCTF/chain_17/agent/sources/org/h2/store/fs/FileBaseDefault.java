package org.h2.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/FileBaseDefault.class */
public abstract class FileBaseDefault extends FileBase {
    private long position = 0;

    protected abstract void implTruncate(long j) throws IOException;

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public final synchronized long position() throws IOException {
        return this.position;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public final synchronized FileChannel position(long j) throws IOException {
        if (j < 0) {
            throw new IllegalArgumentException();
        }
        this.position = j;
        return this;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public final synchronized int read(ByteBuffer byteBuffer) throws IOException {
        int read = read(byteBuffer, this.position);
        if (read > 0) {
            this.position += read;
        }
        return read;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public final synchronized int write(ByteBuffer byteBuffer) throws IOException {
        int write = write(byteBuffer, this.position);
        if (write > 0) {
            this.position += write;
        }
        return write;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public final synchronized FileChannel truncate(long j) throws IOException {
        implTruncate(j);
        if (j < this.position) {
            this.position = j;
        }
        return this;
    }
}

package org.h2.store.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/FileBase.class */
public abstract class FileBase extends FileChannel {
    @Override // java.nio.channels.FileChannel
    public synchronized int read(ByteBuffer byteBuffer, long j) throws IOException {
        long position = position();
        position(j);
        int read = read(byteBuffer);
        position(position);
        return read;
    }

    @Override // java.nio.channels.FileChannel
    public synchronized int write(ByteBuffer byteBuffer, long j) throws IOException {
        long position = position();
        position(j);
        int write = write(byteBuffer);
        position(position);
        return write;
    }

    @Override // java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
    }

    @Override // java.nio.channels.spi.AbstractInterruptibleChannel
    protected void implCloseChannel() throws IOException {
    }

    @Override // java.nio.channels.FileChannel
    public FileLock lock(long j, long j2, boolean z) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.channels.FileChannel
    public MappedByteBuffer map(FileChannel.MapMode mapMode, long j, long j2) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.ScatteringByteChannel
    public long read(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.channels.FileChannel
    public long transferFrom(ReadableByteChannel readableByteChannel, long j, long j2) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.channels.FileChannel
    public long transferTo(long j, long j2, WritableByteChannel writableByteChannel) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.channels.FileChannel
    public FileLock tryLock(long j, long j2, boolean z) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.GatheringByteChannel
    public long write(ByteBuffer[] byteBufferArr, int i, int i2) throws IOException {
        throw new UnsupportedOperationException();
    }
}

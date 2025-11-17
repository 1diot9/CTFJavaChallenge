package org.h2.store.fs.rec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import org.h2.store.fs.FileBase;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/rec/FileRec.class */
class FileRec extends FileBase {
    private final FilePathRec rec;
    private final FileChannel channel;
    private final String name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileRec(FilePathRec filePathRec, FileChannel fileChannel, String str) {
        this.rec = filePathRec;
        this.channel = fileChannel;
        this.name = str;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.spi.AbstractInterruptibleChannel
    public void implCloseChannel() throws IOException {
        this.channel.close();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long position() throws IOException {
        return this.channel.position();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public long size() throws IOException {
        return this.channel.size();
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer byteBuffer) throws IOException {
        return this.channel.read(byteBuffer);
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int read(ByteBuffer byteBuffer, long j) throws IOException {
        return this.channel.read(byteBuffer, j);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public FileChannel position(long j) throws IOException {
        this.channel.position(j);
        return this;
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel
    public FileChannel truncate(long j) throws IOException {
        this.rec.log(7, this.name, null, j);
        this.channel.truncate(j);
        return this;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public void force(boolean z) throws IOException {
        this.channel.force(z);
    }

    @Override // java.nio.channels.FileChannel, java.nio.channels.SeekableByteChannel, java.nio.channels.WritableByteChannel
    public int write(ByteBuffer byteBuffer) throws IOException {
        byte[] array = byteBuffer.array();
        int remaining = byteBuffer.remaining();
        if (byteBuffer.position() != 0 || remaining != array.length) {
            int arrayOffset = byteBuffer.arrayOffset() + byteBuffer.position();
            array = Arrays.copyOfRange(array, arrayOffset, arrayOffset + remaining);
        }
        int write = this.channel.write(byteBuffer);
        this.rec.log(8, this.name, array, this.channel.position());
        return write;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public int write(ByteBuffer byteBuffer, long j) throws IOException {
        byte[] array = byteBuffer.array();
        int remaining = byteBuffer.remaining();
        if (byteBuffer.position() != 0 || remaining != array.length) {
            int arrayOffset = byteBuffer.arrayOffset() + byteBuffer.position();
            array = Arrays.copyOfRange(array, arrayOffset, arrayOffset + remaining);
        }
        int write = this.channel.write(byteBuffer, j);
        this.rec.log(8, this.name, array, j);
        return write;
    }

    @Override // org.h2.store.fs.FileBase, java.nio.channels.FileChannel
    public synchronized FileLock tryLock(long j, long j2, boolean z) throws IOException {
        return this.channel.tryLock(j, j2, z);
    }

    public String toString() {
        return this.name;
    }
}

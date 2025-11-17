package org.springframework.boot.loader.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:org/springframework/boot/loader/zip/ByteArrayDataBlock.class */
class ByteArrayDataBlock implements CloseableDataBlock {
    private final byte[] bytes;
    private final int maxReadSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteArrayDataBlock(byte... bytes) {
        this(bytes, -1);
    }

    ByteArrayDataBlock(byte[] bytes, int maxReadSize) {
        this.bytes = bytes;
        this.maxReadSize = maxReadSize;
    }

    @Override // org.springframework.boot.loader.zip.DataBlock
    public long size() throws IOException {
        return this.bytes.length;
    }

    @Override // org.springframework.boot.loader.zip.DataBlock
    public int read(ByteBuffer dst, long pos) throws IOException {
        return read(dst, (int) pos);
    }

    private int read(ByteBuffer dst, int pos) {
        int remaining = dst.remaining();
        int length = Math.min(this.bytes.length - pos, remaining);
        if (this.maxReadSize > 0 && length > this.maxReadSize) {
            length = this.maxReadSize;
        }
        dst.put(this.bytes, pos, length);
        return length;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }
}

package org.springframework.boot.loader.zip;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:org/springframework/boot/loader/zip/DataBlockInputStream.class */
public class DataBlockInputStream extends InputStream {
    private final DataBlock dataBlock;
    private long pos;
    private long remaining;
    private volatile boolean closed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataBlockInputStream(DataBlock dataBlock) throws IOException {
        this.dataBlock = dataBlock;
        this.remaining = dataBlock.size();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        byte[] b = new byte[1];
        if (read(b, 0, 1) == 1) {
            return b[0] & 255;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        ByteBuffer dst = ByteBuffer.wrap(b, off, len);
        int count = this.dataBlock.read(dst, this.pos);
        if (count > 0) {
            this.pos += count;
            this.remaining -= count;
        }
        return count;
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        long count = n > 0 ? maxForwardSkip(n) : maxBackwardSkip(n);
        this.pos += count;
        this.remaining -= count;
        return count;
    }

    private long maxForwardSkip(long n) {
        boolean willCauseOverflow = this.pos + n < 0;
        return (willCauseOverflow || n > this.remaining) ? this.remaining : n;
    }

    private long maxBackwardSkip(long n) {
        return Math.max(-this.pos, n);
    }

    @Override // java.io.InputStream
    public int available() {
        if (this.closed) {
            return 0;
        }
        if (this.remaining < 2147483647L) {
            return (int) this.remaining;
        }
        return Integer.MAX_VALUE;
    }

    private void ensureOpen() throws IOException {
        if (this.closed) {
            throw new IOException("InputStream closed");
        }
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        DataBlock dataBlock = this.dataBlock;
        if (dataBlock instanceof Closeable) {
            Closeable closeable = (Closeable) dataBlock;
            closeable.close();
        }
    }
}

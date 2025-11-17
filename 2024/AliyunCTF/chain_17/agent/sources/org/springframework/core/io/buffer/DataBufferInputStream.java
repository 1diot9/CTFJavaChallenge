package org.springframework.core.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferInputStream.class */
public final class DataBufferInputStream extends InputStream {
    private final DataBuffer dataBuffer;
    private final int end;
    private final boolean releaseOnClose;
    private boolean closed;
    private int mark;

    public DataBufferInputStream(DataBuffer dataBuffer, boolean releaseOnClose) {
        Assert.notNull(dataBuffer, "DataBuffer must not be null");
        this.dataBuffer = dataBuffer;
        int start = this.dataBuffer.readPosition();
        this.end = start + this.dataBuffer.readableByteCount();
        this.mark = start;
        this.releaseOnClose = releaseOnClose;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        checkClosed();
        if (available() == 0) {
            return -1;
        }
        return this.dataBuffer.read() & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        checkClosed();
        int available = available();
        if (available == 0) {
            return -1;
        }
        int len2 = Math.min(available, len);
        this.dataBuffer.read(b, off, len2);
        return len2;
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.InputStream
    public void mark(int readLimit) {
        Assert.isTrue(readLimit > 0, "readLimit must be greater than 0");
        this.mark = this.dataBuffer.readPosition();
    }

    @Override // java.io.InputStream
    public int available() {
        return Math.max(0, this.end - this.dataBuffer.readPosition());
    }

    @Override // java.io.InputStream
    public void reset() {
        this.dataBuffer.readPosition(this.mark);
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        if (this.releaseOnClose) {
            DataBufferUtils.release(this.dataBuffer);
        }
        this.closed = true;
    }

    private void checkClosed() throws IOException {
        if (this.closed) {
            throw new IOException("DataBufferInputStream is closed");
        }
    }
}

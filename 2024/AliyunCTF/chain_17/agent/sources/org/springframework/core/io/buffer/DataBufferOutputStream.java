package org.springframework.core.io.buffer;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/buffer/DataBufferOutputStream.class */
public final class DataBufferOutputStream extends OutputStream {
    private final DataBuffer dataBuffer;
    private boolean closed;

    public DataBufferOutputStream(DataBuffer dataBuffer) {
        Assert.notNull(dataBuffer, "DataBuffer must not be null");
        this.dataBuffer = dataBuffer;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        checkClosed();
        this.dataBuffer.ensureWritable(1);
        this.dataBuffer.write((byte) b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        checkClosed();
        if (len > 0) {
            this.dataBuffer.ensureWritable(len);
            this.dataBuffer.write(b, off, len);
        }
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
    }

    private void checkClosed() throws IOException {
        if (this.closed) {
            throw new IOException("DataBufferOutputStream is closed");
        }
    }
}

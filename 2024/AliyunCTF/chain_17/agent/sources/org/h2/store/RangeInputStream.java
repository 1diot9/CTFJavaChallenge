package org.h2.store;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.h2.util.IOUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/RangeInputStream.class */
public final class RangeInputStream extends FilterInputStream {
    private long limit;

    public RangeInputStream(InputStream inputStream, long j, long j2) throws IOException {
        super(inputStream);
        this.limit = j2;
        IOUtils.skipFully(inputStream, j);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.limit <= 0) {
            return -1;
        }
        int read = this.in.read();
        if (read >= 0) {
            this.limit--;
        }
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (this.limit <= 0) {
            return -1;
        }
        if (i2 > this.limit) {
            i2 = (int) this.limit;
        }
        int read = this.in.read(bArr, i, i2);
        if (read > 0) {
            this.limit -= read;
        }
        return read;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long j) throws IOException {
        if (j > this.limit) {
            j = (int) this.limit;
        }
        long skip = this.in.skip(j);
        this.limit -= skip;
        return skip;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        int available = this.in.available();
        if (available > this.limit) {
            return (int) this.limit;
        }
        return available;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.in.close();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public void mark(int i) {
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }
}

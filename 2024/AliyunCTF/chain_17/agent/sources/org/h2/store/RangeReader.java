package org.h2.store;

import java.io.IOException;
import java.io.Reader;
import org.h2.util.IOUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/RangeReader.class */
public final class RangeReader extends Reader {
    private final Reader r;
    private long limit;

    public RangeReader(Reader reader, long j, long j2) throws IOException {
        this.r = reader;
        this.limit = j2;
        IOUtils.skipFully(reader, j);
    }

    @Override // java.io.Reader
    public int read() throws IOException {
        if (this.limit <= 0) {
            return -1;
        }
        int read = this.r.read();
        if (read >= 0) {
            this.limit--;
        }
        return read;
    }

    @Override // java.io.Reader
    public int read(char[] cArr, int i, int i2) throws IOException {
        if (this.limit <= 0) {
            return -1;
        }
        if (i2 > this.limit) {
            i2 = (int) this.limit;
        }
        int read = this.r.read(cArr, i, i2);
        if (read > 0) {
            this.limit -= read;
        }
        return read;
    }

    @Override // java.io.Reader
    public long skip(long j) throws IOException {
        if (j > this.limit) {
            j = (int) this.limit;
        }
        long skip = this.r.skip(j);
        this.limit -= skip;
        return skip;
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        if (this.limit > 0) {
            return this.r.ready();
        }
        return false;
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.Reader
    public void mark(int i) throws IOException {
        throw new IOException("mark() not supported");
    }

    @Override // java.io.Reader
    public void reset() throws IOException {
        throw new IOException("reset() not supported");
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.r.close();
    }
}

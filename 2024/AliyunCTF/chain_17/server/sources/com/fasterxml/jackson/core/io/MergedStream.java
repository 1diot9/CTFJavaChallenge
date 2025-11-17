package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/MergedStream.class */
public final class MergedStream extends InputStream {
    private final IOContext _ctxt;
    private final InputStream _in;
    private byte[] _b;
    private int _ptr;
    private final int _end;

    public MergedStream(IOContext ctxt, InputStream in, byte[] buf, int start, int end) {
        this._ctxt = ctxt;
        this._in = in;
        this._b = buf;
        this._ptr = start;
        this._end = end;
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        if (this._b != null) {
            return this._end - this._ptr;
        }
        return this._in.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        _free();
        this._in.close();
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        if (this._b == null) {
            this._in.mark(readlimit);
        }
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return this._b == null && this._in.markSupported();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this._b != null) {
            byte[] bArr = this._b;
            int i = this._ptr;
            this._ptr = i + 1;
            int c = bArr[i] & 255;
            if (this._ptr >= this._end) {
                _free();
            }
            return c;
        }
        return this._in.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this._b != null) {
            int avail = this._end - this._ptr;
            if (len > avail) {
                len = avail;
            }
            System.arraycopy(this._b, this._ptr, b, off, len);
            this._ptr += len;
            if (this._ptr >= this._end) {
                _free();
            }
            return len;
        }
        return this._in.read(b, off, len);
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        if (this._b == null) {
            this._in.reset();
        }
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        long count = 0;
        if (this._b != null) {
            int amount = this._end - this._ptr;
            if (amount > n) {
                this._ptr += (int) n;
                return n;
            }
            _free();
            count = 0 + amount;
            n -= amount;
        }
        if (n > 0) {
            count += this._in.skip(n);
        }
        return count;
    }

    private void _free() {
        byte[] buf = this._b;
        if (buf != null) {
            this._b = null;
            if (this._ctxt != null) {
                this._ctxt.releaseReadIOBuffer(buf);
            }
        }
    }
}

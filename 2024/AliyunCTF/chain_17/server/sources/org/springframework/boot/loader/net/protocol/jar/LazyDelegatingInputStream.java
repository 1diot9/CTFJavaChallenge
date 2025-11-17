package org.springframework.boot.loader.net.protocol.jar;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: server.jar:org/springframework/boot/loader/net/protocol/jar/LazyDelegatingInputStream.class */
abstract class LazyDelegatingInputStream extends InputStream {
    private volatile InputStream in;

    protected abstract InputStream getDelegateInputStream() throws IOException;

    @Override // java.io.InputStream
    public int read() throws IOException {
        return in().read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return in().read(b);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        return in().read(b, off, len);
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        return in().skip(n);
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return in().available();
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        try {
            return in().markSupported();
        } catch (IOException e) {
            return false;
        }
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        try {
            in().mark(readlimit);
        } catch (IOException e) {
        }
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        in().reset();
    }

    private InputStream in() throws IOException {
        InputStream in = this.in;
        if (in == null) {
            synchronized (this) {
                in = this.in;
                if (in == null) {
                    in = getDelegateInputStream();
                    this.in = in;
                }
            }
        }
        return in;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.in != null) {
            synchronized (this) {
                InputStream in = this.in;
                if (in != null) {
                    in.close();
                }
            }
        }
    }
}

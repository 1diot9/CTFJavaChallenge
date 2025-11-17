package org.apache.tomcat.util.http.fileupload.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/LimitedInputStream.class */
public abstract class LimitedInputStream extends FilterInputStream implements Closeable {
    private final long sizeMax;
    private long count;
    private boolean closed;

    protected abstract void raiseError(long j, long j2) throws IOException;

    public LimitedInputStream(InputStream inputStream, long pSizeMax) {
        super(inputStream);
        this.sizeMax = pSizeMax;
    }

    private void checkLimit() throws IOException {
        if (this.count > this.sizeMax) {
            raiseError(this.sizeMax, this.count);
        }
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int res = super.read();
        if (res != -1) {
            this.count++;
            checkLimit();
        }
        return res;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int res = super.read(b, off, len);
        if (res > 0) {
            this.count += res;
            checkLimit();
        }
        return res;
    }

    @Override // org.apache.tomcat.util.http.fileupload.util.Closeable
    public boolean isClosed() throws IOException {
        return this.closed;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable, org.apache.tomcat.util.http.fileupload.util.Closeable
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
}

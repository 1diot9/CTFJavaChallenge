package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/ThresholdingOutputStream.class */
public abstract class ThresholdingOutputStream extends OutputStream {
    private final int threshold;
    private long written;
    private boolean thresholdExceeded;

    protected abstract OutputStream getStream() throws IOException;

    protected abstract void thresholdReached() throws IOException;

    public ThresholdingOutputStream(int threshold) {
        this.threshold = threshold;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        checkThreshold(1);
        getStream().write(b);
        this.written++;
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        checkThreshold(b.length);
        getStream().write(b);
        this.written += b.length;
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        checkThreshold(len);
        getStream().write(b, off, len);
        this.written += len;
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        getStream().flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            flush();
        } catch (IOException e) {
        }
        getStream().close();
    }

    public boolean isThresholdExceeded() {
        return this.written > ((long) this.threshold);
    }

    protected void checkThreshold(int count) throws IOException {
        if (!this.thresholdExceeded && this.written + count > this.threshold) {
            this.thresholdExceeded = true;
            thresholdReached();
        }
    }
}

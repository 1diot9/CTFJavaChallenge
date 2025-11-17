package cn.hutool.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/LimitedInputStream.class */
public class LimitedInputStream extends FilterInputStream {
    private final long maxSize;
    private long currentPos;

    public LimitedInputStream(InputStream in, long maxSize) {
        super(in);
        this.maxSize = maxSize;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        int data = super.read();
        if (data != -1) {
            this.currentPos++;
            checkPos();
        }
        return data;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        int count = super.read(b, off, len);
        if (count > 0) {
            this.currentPos += count;
            checkPos();
        }
        return count;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        long skipped = super.skip(n);
        if (skipped != 0) {
            this.currentPos += skipped;
            checkPos();
        }
        return skipped;
    }

    private void checkPos() {
        if (this.currentPos > this.maxSize) {
            throw new IllegalStateException("Read limit exceeded");
        }
    }
}

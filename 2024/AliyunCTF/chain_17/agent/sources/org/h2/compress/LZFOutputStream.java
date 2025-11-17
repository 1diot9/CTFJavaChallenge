package org.h2.compress;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/compress/LZFOutputStream.class */
public class LZFOutputStream extends OutputStream {
    static final int MAGIC = 1211255123;
    private final OutputStream out;
    private final CompressLZF compress = new CompressLZF();
    private final byte[] buffer = new byte[131072];
    private int pos;
    private byte[] outBuffer;

    public LZFOutputStream(OutputStream outputStream) throws IOException {
        this.out = outputStream;
        ensureOutput(131072);
        writeInt(MAGIC);
    }

    private void ensureOutput(int i) {
        int i2 = (i < 100 ? i + 100 : i) * 2;
        if (this.outBuffer == null || this.outBuffer.length < i2) {
            this.outBuffer = new byte[i2];
        }
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        if (this.pos >= this.buffer.length) {
            flush();
        }
        byte[] bArr = this.buffer;
        int i2 = this.pos;
        this.pos = i2 + 1;
        bArr[i2] = (byte) i;
    }

    private void compressAndWrite(byte[] bArr, int i) throws IOException {
        if (i > 0) {
            ensureOutput(i);
            int compress = this.compress.compress(bArr, 0, i, this.outBuffer, 0);
            if (compress > i) {
                writeInt(-i);
                this.out.write(bArr, 0, i);
            } else {
                writeInt(compress);
                writeInt(i);
                this.out.write(this.outBuffer, 0, compress);
            }
        }
    }

    private void writeInt(int i) throws IOException {
        this.out.write((byte) (i >> 24));
        this.out.write((byte) (i >> 16));
        this.out.write((byte) (i >> 8));
        this.out.write((byte) i);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr, int i, int i2) throws IOException {
        while (i2 > 0) {
            int min = Math.min(this.buffer.length - this.pos, i2);
            System.arraycopy(bArr, i, this.buffer, this.pos, min);
            this.pos += min;
            if (this.pos >= this.buffer.length) {
                flush();
            }
            i += min;
            i2 -= min;
        }
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        compressAndWrite(this.buffer, this.pos);
        this.pos = 0;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        flush();
        this.out.close();
    }
}

package org.h2.compress;

import java.io.IOException;
import java.io.InputStream;
import org.h2.mvstore.DataUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/compress/LZFInputStream.class */
public class LZFInputStream extends InputStream {
    private final InputStream in;
    private CompressLZF decompress = new CompressLZF();
    private int pos;
    private int bufferLength;
    private byte[] inBuffer;
    private byte[] buffer;

    public LZFInputStream(InputStream inputStream) throws IOException {
        this.in = inputStream;
        if (readInt() != 1211255123) {
            throw new IOException("Not an LZFInputStream");
        }
    }

    private static byte[] ensureSize(byte[] bArr, int i) {
        return (bArr == null || bArr.length < i) ? Utils.newBytes(i) : bArr;
    }

    private void fillBuffer() throws IOException {
        if (this.buffer != null && this.pos < this.bufferLength) {
            return;
        }
        int readInt = readInt();
        if (this.decompress == null) {
            this.bufferLength = 0;
        } else if (readInt < 0) {
            int i = -readInt;
            this.buffer = ensureSize(this.buffer, i);
            readFully(this.buffer, i);
            this.bufferLength = i;
        } else {
            this.inBuffer = ensureSize(this.inBuffer, readInt);
            int readInt2 = readInt();
            readFully(this.inBuffer, readInt);
            this.buffer = ensureSize(this.buffer, readInt2);
            try {
                this.decompress.expand(this.inBuffer, 0, readInt, this.buffer, 0, readInt2);
                this.bufferLength = readInt2;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw DataUtils.convertToIOException(e);
            }
        }
        this.pos = 0;
    }

    private void readFully(byte[] bArr, int i) throws IOException {
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i > 0) {
                int read = this.in.read(bArr, i3, i);
                i -= read;
                i2 = i3 + read;
            } else {
                return;
            }
        }
    }

    private int readInt() throws IOException {
        int read = this.in.read();
        if (read < 0) {
            this.decompress = null;
            return 0;
        }
        return (read << 24) + (this.in.read() << 16) + (this.in.read() << 8) + this.in.read();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        fillBuffer();
        if (this.pos >= this.bufferLength) {
            return -1;
        }
        byte[] bArr = this.buffer;
        int i = this.pos;
        this.pos = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int readBlock;
        if (i2 == 0) {
            return 0;
        }
        int i3 = 0;
        while (i2 > 0 && (readBlock = readBlock(bArr, i, i2)) >= 0) {
            i3 += readBlock;
            i += readBlock;
            i2 -= readBlock;
        }
        if (i3 == 0) {
            return -1;
        }
        return i3;
    }

    private int readBlock(byte[] bArr, int i, int i2) throws IOException {
        fillBuffer();
        if (this.pos >= this.bufferLength) {
            return -1;
        }
        int min = Math.min(Math.min(i2, this.bufferLength - this.pos), bArr.length - i);
        System.arraycopy(this.buffer, this.pos, bArr, i, min);
        this.pos += min;
        return min;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.in.close();
    }
}

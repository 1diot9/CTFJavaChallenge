package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/ByteArrayOutputStream.class */
public class ByteArrayOutputStream extends OutputStream {
    static final int DEFAULT_SIZE = 1024;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final List<byte[]> buffers;
    private int currentBufferIndex;
    private int filledBufferSum;
    private byte[] currentBuffer;
    private int count;

    public ByteArrayOutputStream() {
        this(1024);
    }

    public ByteArrayOutputStream(int size) {
        this.buffers = new ArrayList();
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        synchronized (this) {
            needNewBuffer(size);
        }
    }

    private void needNewBuffer(int newcount) {
        int newBufferSize;
        if (this.currentBufferIndex < this.buffers.size() - 1) {
            this.filledBufferSum += this.currentBuffer.length;
            this.currentBufferIndex++;
            this.currentBuffer = this.buffers.get(this.currentBufferIndex);
            return;
        }
        if (this.currentBuffer == null) {
            newBufferSize = newcount;
            this.filledBufferSum = 0;
        } else {
            newBufferSize = Math.max(this.currentBuffer.length << 1, newcount - this.filledBufferSum);
            this.filledBufferSum += this.currentBuffer.length;
        }
        this.currentBufferIndex++;
        this.currentBuffer = new byte[newBufferSize];
        this.buffers.add(this.currentBuffer);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        synchronized (this) {
            int newcount = this.count + len;
            int remaining = len;
            int inBufferPos = this.count - this.filledBufferSum;
            while (remaining > 0) {
                int part = Math.min(remaining, this.currentBuffer.length - inBufferPos);
                System.arraycopy(b, (off + len) - remaining, this.currentBuffer, inBufferPos, part);
                remaining -= part;
                if (remaining > 0) {
                    needNewBuffer(newcount);
                    inBufferPos = 0;
                }
            }
            this.count = newcount;
        }
    }

    @Override // java.io.OutputStream
    public synchronized void write(int b) {
        int inBufferPos = this.count - this.filledBufferSum;
        if (inBufferPos == this.currentBuffer.length) {
            needNewBuffer(this.count + 1);
            inBufferPos = 0;
        }
        this.currentBuffer[inBufferPos] = (byte) b;
        this.count++;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        int remaining = this.count;
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            out.write(buf, 0, c);
            remaining -= c;
            if (remaining == 0) {
                return;
            }
        }
    }

    public synchronized byte[] toByteArray() {
        int remaining = this.count;
        if (remaining == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] newbuf = new byte[remaining];
        int pos = 0;
        for (byte[] buf : this.buffers) {
            int c = Math.min(buf.length, remaining);
            System.arraycopy(buf, 0, newbuf, pos, c);
            pos += c;
            remaining -= c;
            if (remaining == 0) {
                break;
            }
        }
        return newbuf;
    }
}

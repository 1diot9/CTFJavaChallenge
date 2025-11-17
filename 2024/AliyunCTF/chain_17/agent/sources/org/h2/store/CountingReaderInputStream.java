package org.h2.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/CountingReaderInputStream.class */
public class CountingReaderInputStream extends InputStream {
    private final Reader reader;
    private final CharBuffer charBuffer = CharBuffer.allocate(4096);
    private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
    private ByteBuffer byteBuffer = ByteBuffer.allocate(0);
    private long length;
    private long remaining;

    public CountingReaderInputStream(Reader reader, long j) {
        this.reader = reader;
        this.remaining = j;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (!fetch()) {
            return -1;
        }
        int min = Math.min(i2, this.byteBuffer.remaining());
        this.byteBuffer.get(bArr, i, min);
        return min;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (!fetch()) {
            return -1;
        }
        return this.byteBuffer.get() & 255;
    }

    private boolean fetch() throws IOException {
        if (this.byteBuffer != null && this.byteBuffer.remaining() == 0) {
            fillBuffer();
        }
        return this.byteBuffer != null;
    }

    private void fillBuffer() throws IOException {
        int min = (int) Math.min(this.charBuffer.capacity() - this.charBuffer.position(), this.remaining);
        if (min > 0) {
            min = this.reader.read(this.charBuffer.array(), this.charBuffer.position(), min);
        }
        if (min > 0) {
            this.remaining -= min;
        } else {
            min = 0;
            this.remaining = 0L;
        }
        this.length += min;
        this.charBuffer.limit(this.charBuffer.position() + min);
        this.charBuffer.rewind();
        this.byteBuffer = ByteBuffer.allocate(4096);
        boolean z = this.remaining == 0;
        this.encoder.encode(this.charBuffer, this.byteBuffer, z);
        if (z && this.byteBuffer.position() == 0) {
            this.byteBuffer = null;
            return;
        }
        this.byteBuffer.flip();
        this.charBuffer.compact();
        this.charBuffer.flip();
        this.charBuffer.position(this.charBuffer.limit());
    }

    public long getLength() {
        return this.length;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.reader.close();
    }
}

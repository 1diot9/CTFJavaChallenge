package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ByteBufferBackedInputStream.class */
public class ByteBufferBackedInputStream extends InputStream {
    protected final ByteBuffer _b;

    public ByteBufferBackedInputStream(ByteBuffer buf) {
        this._b = buf;
    }

    @Override // java.io.InputStream
    public int available() {
        return this._b.remaining();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this._b.hasRemaining()) {
            return this._b.get() & 255;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] bytes, int off, int len) throws IOException {
        if (!this._b.hasRemaining()) {
            return -1;
        }
        int len2 = Math.min(len, this._b.remaining());
        this._b.get(bytes, off, len2);
        return len2;
    }
}

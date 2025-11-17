package com.fasterxml.jackson.databind.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ByteBufferBackedOutputStream.class */
public class ByteBufferBackedOutputStream extends OutputStream {
    protected final ByteBuffer _b;

    public ByteBufferBackedOutputStream(ByteBuffer buf) {
        this._b = buf;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        this._b.put((byte) b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bytes, int off, int len) throws IOException {
        this._b.put(bytes, off, len);
    }
}

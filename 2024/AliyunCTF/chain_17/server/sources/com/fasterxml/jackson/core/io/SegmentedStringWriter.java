package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.io.Writer;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/SegmentedStringWriter.class */
public final class SegmentedStringWriter extends Writer {
    private final TextBuffer _buffer;

    public SegmentedStringWriter(BufferRecycler br) {
        this._buffer = new TextBuffer(br);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(CharSequence csq) throws IOException {
        String str = csq.toString();
        this._buffer.append(str, 0, str.length());
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        String str = csq.subSequence(start, end).toString();
        this._buffer.append(str, 0, str.length());
        return this;
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
        this._buffer.append(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        this._buffer.append(cbuf, off, len);
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        this._buffer.append((char) c);
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        this._buffer.append(str, 0, str.length());
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        this._buffer.append(str, off, len);
    }

    public String getAndClear() throws IOException {
        String result = this._buffer.contentsAsString();
        this._buffer.releaseBuffers();
        return result;
    }
}

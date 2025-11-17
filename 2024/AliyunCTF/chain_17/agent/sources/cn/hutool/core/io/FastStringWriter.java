package cn.hutool.core.io;

import cn.hutool.core.text.StrBuilder;
import java.io.Writer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/FastStringWriter.class */
public final class FastStringWriter extends Writer {
    private final StrBuilder builder;

    public FastStringWriter() {
        this(16);
    }

    public FastStringWriter(int initialSize) {
        this.builder = new StrBuilder(initialSize < 0 ? 16 : initialSize);
    }

    @Override // java.io.Writer
    public void write(int c) {
        this.builder.append((char) c);
    }

    @Override // java.io.Writer
    public void write(String str) {
        this.builder.append((CharSequence) str);
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) {
        this.builder.append((CharSequence) str, off, off + len);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) {
        this.builder.append(cbuf, 0, cbuf.length);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) {
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        this.builder.append(cbuf, off, len);
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    public String toString() {
        return this.builder.toString();
    }
}

package cn.hutool.core.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/AppendableWriter.class */
public class AppendableWriter extends Writer implements Appendable {
    private final Appendable appendable;
    private final boolean flushable;
    private boolean closed = false;

    public AppendableWriter(Appendable appendable) {
        this.appendable = appendable;
        this.flushable = appendable instanceof Flushable;
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        checkNotClosed();
        this.appendable.append(CharBuffer.wrap(cbuf), off, off + len);
    }

    @Override // java.io.Writer
    public void write(int c) throws IOException {
        checkNotClosed();
        this.appendable.append((char) c);
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(char c) throws IOException {
        checkNotClosed();
        this.appendable.append(c);
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        checkNotClosed();
        this.appendable.append(csq, start, end);
        return this;
    }

    @Override // java.io.Writer, java.lang.Appendable
    public Writer append(CharSequence csq) throws IOException {
        checkNotClosed();
        this.appendable.append(csq);
        return this;
    }

    @Override // java.io.Writer
    public void write(String str, int off, int len) throws IOException {
        checkNotClosed();
        this.appendable.append(str, off, off + len);
    }

    @Override // java.io.Writer
    public void write(String str) throws IOException {
        this.appendable.append(str);
    }

    @Override // java.io.Writer
    public void write(char[] cbuf) throws IOException {
        this.appendable.append(CharBuffer.wrap(cbuf));
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() throws IOException {
        checkNotClosed();
        if (this.flushable) {
            ((Flushable) this.appendable).flush();
        }
    }

    private void checkNotClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Writer is closed!" + this);
        }
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (false == this.closed) {
            flush();
            if (this.appendable instanceof Closeable) {
                ((Closeable) this.appendable).close();
            }
            this.closed = true;
        }
    }
}

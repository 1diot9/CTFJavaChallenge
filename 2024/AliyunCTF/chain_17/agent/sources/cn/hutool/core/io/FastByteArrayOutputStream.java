package cn.hutool.core.io;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/FastByteArrayOutputStream.class */
public class FastByteArrayOutputStream extends OutputStream {
    private final FastByteBuffer buffer;

    public FastByteArrayOutputStream() {
        this(1024);
    }

    public FastByteArrayOutputStream(int size) {
        this.buffer = new FastByteBuffer(size);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        this.buffer.append(b, off, len);
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        this.buffer.append((byte) b);
    }

    public int size() {
        return this.buffer.size();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    public void reset() {
        this.buffer.reset();
    }

    public void writeTo(OutputStream out) throws IORuntimeException {
        int index = this.buffer.index();
        if (index < 0) {
            return;
        }
        for (int i = 0; i < index; i++) {
            try {
                byte[] buf = this.buffer.array(i);
                out.write(buf);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        out.write(this.buffer.array(index), 0, this.buffer.offset());
    }

    public byte[] toByteArray() {
        return this.buffer.toArray();
    }

    public String toString() {
        return toString(CharsetUtil.defaultCharset());
    }

    public String toString(String charsetName) {
        return toString(CharsetUtil.charset(charsetName));
    }

    public String toString(Charset charset) {
        return new String(toByteArray(), (Charset) ObjectUtil.defaultIfNull(charset, CharsetUtil.defaultCharset()));
    }
}

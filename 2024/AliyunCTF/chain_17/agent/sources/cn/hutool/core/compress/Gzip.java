package cn.hutool.core.compress;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/compress/Gzip.class */
public class Gzip implements Closeable {
    private InputStream source;
    private OutputStream target;

    public static Gzip of(InputStream source, OutputStream target) {
        return new Gzip(source, target);
    }

    public Gzip(InputStream source, OutputStream target) {
        this.source = source;
        this.target = target;
    }

    public OutputStream getTarget() {
        return this.target;
    }

    public Gzip gzip() {
        try {
            this.target = this.target instanceof GZIPOutputStream ? (GZIPOutputStream) this.target : new GZIPOutputStream(this.target);
            IoUtil.copy(this.source, this.target);
            ((GZIPOutputStream) this.target).finish();
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public Gzip unGzip() {
        try {
            this.source = this.source instanceof GZIPInputStream ? (GZIPInputStream) this.source : new GZIPInputStream(this.source);
            IoUtil.copy(this.source, this.target);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.target);
        IoUtil.close((Closeable) this.source);
    }
}

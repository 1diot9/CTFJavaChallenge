package cn.hutool.http;

import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpInputStream.class */
public class HttpInputStream extends InputStream {
    private InputStream in;

    public HttpInputStream(HttpResponse response) {
        init(response);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return this.in.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        return this.in.read(b, off, len);
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        return this.in.skip(n);
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.in.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.in.close();
    }

    @Override // java.io.InputStream
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return this.in.markSupported();
    }

    private void init(HttpResponse response) {
        try {
            this.in = response.status < 400 ? response.httpConnection.getInputStream() : response.httpConnection.getErrorStream();
        } catch (IOException e) {
            if (false == (e instanceof FileNotFoundException)) {
                throw new HttpException(e);
            }
        }
        if (null == this.in) {
            this.in = new ByteArrayInputStream(StrUtil.format("Error request, response status: {}", Integer.valueOf(response.status)).getBytes());
            return;
        }
        if (response.isGzip() && false == (response.in instanceof GZIPInputStream)) {
            try {
                this.in = new GZIPInputStream(this.in);
            } catch (IOException e2) {
            }
        } else if (response.isDeflate() && false == (this.in instanceof InflaterInputStream)) {
            this.in = new InflaterInputStream(this.in, new Inflater(true));
        }
    }
}

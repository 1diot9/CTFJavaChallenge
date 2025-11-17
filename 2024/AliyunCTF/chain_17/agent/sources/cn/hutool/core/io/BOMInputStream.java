package cn.hutool.core.io;

import cn.hutool.core.util.CharsetUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/BOMInputStream.class */
public class BOMInputStream extends InputStream {
    private final PushbackInputStream in;
    private boolean isInited;
    private final String defaultCharset;
    private String charset;
    private static final int BOM_SIZE = 4;

    public BOMInputStream(InputStream in) {
        this(in, CharsetUtil.UTF_8);
    }

    public BOMInputStream(InputStream in, String defaultCharset) {
        this.isInited = false;
        this.in = new PushbackInputStream(in, 4);
        this.defaultCharset = defaultCharset;
    }

    public String getDefaultCharset() {
        return this.defaultCharset;
    }

    public String getCharset() {
        if (false == this.isInited) {
            try {
                init();
            } catch (IOException ex) {
                throw new IORuntimeException(ex);
            }
        }
        return this.charset;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.isInited = true;
        this.in.close();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        this.isInited = true;
        return this.in.read();
    }

    protected void init() throws IOException {
        int unread;
        if (this.isInited) {
            return;
        }
        byte[] bom = new byte[4];
        int n = this.in.read(bom, 0, bom.length);
        if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1) {
            this.charset = "UTF-32BE";
            unread = n - 4;
        } else if (bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
            this.charset = "UTF-32LE";
            unread = n - 4;
        } else if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
            this.charset = CharsetUtil.UTF_8;
            unread = n - 3;
        } else if (bom[0] == -2 && bom[1] == -1) {
            this.charset = "UTF-16BE";
            unread = n - 2;
        } else if (bom[0] == -1 && bom[1] == -2) {
            this.charset = "UTF-16LE";
            unread = n - 2;
        } else {
            this.charset = this.defaultCharset;
            unread = n;
        }
        if (unread > 0) {
            this.in.unread(bom, n - unread, unread);
        }
        this.isInited = true;
    }
}

package cn.hutool.core.io;

import cn.hutool.core.lang.Assert;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/BomReader.class */
public class BomReader extends Reader {
    private InputStreamReader reader;

    public BomReader(InputStream in) {
        Assert.notNull(in, "InputStream must be not null!", new Object[0]);
        BOMInputStream bin = in instanceof BOMInputStream ? (BOMInputStream) in : new BOMInputStream(in);
        try {
            this.reader = new InputStreamReader(bin, bin.getCharset());
        } catch (UnsupportedEncodingException e) {
        }
    }

    @Override // java.io.Reader
    public int read(char[] cbuf, int off, int len) throws IOException {
        return this.reader.read(cbuf, off, len);
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.reader.close();
    }
}

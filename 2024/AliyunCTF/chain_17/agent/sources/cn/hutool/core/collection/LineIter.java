package cn.hutool.core.collection;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/LineIter.class */
public class LineIter extends ComputeIter<String> implements IterableIter<String>, Closeable, Serializable {
    private static final long serialVersionUID = 1;
    private final BufferedReader bufferedReader;

    public LineIter(InputStream in, Charset charset) throws IllegalArgumentException {
        this(IoUtil.getReader(in, charset));
    }

    public LineIter(Reader reader) throws IllegalArgumentException {
        Assert.notNull(reader, "Reader must not be null", new Object[0]);
        this.bufferedReader = IoUtil.getReader(reader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.collection.ComputeIter
    public String computeNext() {
        String line;
        do {
            try {
                line = this.bufferedReader.readLine();
                if (line == null) {
                    return null;
                }
            } catch (IOException ioe) {
                close();
                throw new IORuntimeException(ioe);
            }
        } while (!isValidLine(line));
        return line;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        super.finish();
        IoUtil.close((Closeable) this.bufferedReader);
    }

    protected boolean isValidLine(String line) {
        return true;
    }
}

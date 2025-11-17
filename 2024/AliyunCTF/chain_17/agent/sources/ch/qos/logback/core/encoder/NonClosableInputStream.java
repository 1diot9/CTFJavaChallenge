package ch.qos.logback.core.encoder;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/encoder/NonClosableInputStream.class */
public class NonClosableInputStream extends FilterInputStream {
    NonClosableInputStream(InputStream is) {
        super(is);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    public void realClose() throws IOException {
        super.close();
    }
}

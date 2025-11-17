package ch.qos.logback.core.testUtil;

import java.io.IOException;
import java.io.PrintStream;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/XTeeOutputStream.class */
public class XTeeOutputStream extends TeeOutputStream {
    boolean closed;

    public XTeeOutputStream(PrintStream targetPS) {
        super(targetPS);
        this.closed = false;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }

    public boolean isClosed() {
        return this.closed;
    }
}

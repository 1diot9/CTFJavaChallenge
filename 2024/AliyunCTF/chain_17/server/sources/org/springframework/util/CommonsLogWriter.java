package org.springframework.util;

import java.io.Writer;
import org.apache.commons.logging.Log;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/CommonsLogWriter.class */
public class CommonsLogWriter extends Writer {
    private final Log logger;
    private final StringBuilder buffer = new StringBuilder();

    public CommonsLogWriter(Log logger) {
        Assert.notNull(logger, "Logger must not be null");
        this.logger = logger;
    }

    public void write(char ch2) {
        if (ch2 == '\n' && this.buffer.length() > 0) {
            this.logger.debug(this.buffer.toString());
            this.buffer.setLength(0);
        } else {
            this.buffer.append(ch2);
        }
    }

    @Override // java.io.Writer
    public void write(char[] buffer, int offset, int length) {
        for (int i = 0; i < length; i++) {
            write(buffer[offset + i]);
        }
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}

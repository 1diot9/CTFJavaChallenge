package org.jooq.exception;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/IOException.class */
public class IOException extends DataAccessException {
    public IOException(String message, java.io.IOException cause) {
        super(message, cause);
    }

    @Override // java.lang.Throwable
    public synchronized java.io.IOException getCause() {
        return (java.io.IOException) super.getCause();
    }

    @Override // java.lang.Throwable
    public synchronized Throwable initCause(Throwable cause) {
        if (!(cause instanceof java.io.IOException)) {
            throw new IllegalArgumentException("Can only wrap java.io.IOException: " + String.valueOf(cause));
        }
        return super.initCause(cause);
    }
}

package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/WrappedIOException.class */
public class WrappedIOException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public WrappedIOException(IOException cause) {
        super(cause);
    }

    @Override // java.lang.Throwable
    public IOException getCause() {
        return (IOException) super.getCause();
    }
}

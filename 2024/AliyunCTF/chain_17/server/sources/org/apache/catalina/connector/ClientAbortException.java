package org.apache.catalina.connector;

import org.apache.coyote.BadRequestException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/ClientAbortException.class */
public final class ClientAbortException extends BadRequestException {
    private static final long serialVersionUID = 1;

    public ClientAbortException() {
    }

    public ClientAbortException(String message) {
        super(message);
    }

    public ClientAbortException(Throwable throwable) {
        super(throwable);
    }

    public ClientAbortException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

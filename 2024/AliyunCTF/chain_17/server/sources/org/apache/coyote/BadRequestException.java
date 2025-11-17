package org.apache.coyote;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/BadRequestException.class */
public class BadRequestException extends IOException {
    private static final long serialVersionUID = 1;

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Throwable throwable) {
        super(throwable);
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

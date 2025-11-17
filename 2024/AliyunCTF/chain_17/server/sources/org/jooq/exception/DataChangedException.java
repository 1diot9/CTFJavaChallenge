package org.jooq.exception;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/DataChangedException.class */
public class DataChangedException extends DataAccessException {
    public DataChangedException(String message) {
        super(message);
    }

    public DataChangedException(String message, Throwable cause) {
        super(message, cause);
    }
}

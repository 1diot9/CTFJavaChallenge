package org.jooq.exception;

import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/exception/SQLDialectNotSupportedException.class */
public class SQLDialectNotSupportedException extends RuntimeException {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) SQLDialectNotSupportedException.class);

    public SQLDialectNotSupportedException(String message) {
        this(message, true);
    }

    public SQLDialectNotSupportedException(String message, boolean warn) {
        super(message);
        if (warn) {
            log.warn("Not supported by dialect", message);
        }
    }
}

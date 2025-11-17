package org.jooq;

import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CloseableDSLContext.class */
public interface CloseableDSLContext extends DSLContext, AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close() throws DataAccessException;
}

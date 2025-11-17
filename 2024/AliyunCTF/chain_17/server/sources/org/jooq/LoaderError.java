package org.jooq;

import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/LoaderError.class */
public interface LoaderError {
    DataAccessException exception();

    int rowIndex();

    String[] row();

    Query query();
}

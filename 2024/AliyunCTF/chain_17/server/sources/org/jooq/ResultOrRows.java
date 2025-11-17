package org.jooq;

import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ResultOrRows.class */
public interface ResultOrRows {
    Result<Record> result();

    int rows();

    DataAccessException exception();
}

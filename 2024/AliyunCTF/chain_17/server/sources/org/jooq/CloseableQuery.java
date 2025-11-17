package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CloseableQuery.class */
public interface CloseableQuery extends Query, AutoCloseable {
    @Override // org.jooq.Query
    @NotNull
    CloseableQuery bind(String str, Object obj) throws IllegalArgumentException, DataTypeException;

    @Override // org.jooq.Query
    @NotNull
    CloseableQuery bind(int i, Object obj) throws IllegalArgumentException, DataTypeException;

    @Override // org.jooq.Query
    @NotNull
    CloseableQuery poolable(boolean z);

    @Override // org.jooq.Query
    @NotNull
    CloseableQuery queryTimeout(int i);

    @Override // org.jooq.Query
    @NotNull
    CloseableQuery keepStatement(boolean z);

    @Override // java.lang.AutoCloseable
    void close() throws DataAccessException;
}

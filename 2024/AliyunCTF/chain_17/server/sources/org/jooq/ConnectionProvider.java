package org.jooq;

import java.sql.Connection;
import org.jetbrains.annotations.Nullable;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConnectionProvider.class */
public interface ConnectionProvider {
    @Nullable
    Connection acquire() throws DataAccessException;

    void release(Connection connection) throws DataAccessException;
}

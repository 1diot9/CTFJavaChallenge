package org.jooq;

import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TransactionProvider.class */
public interface TransactionProvider {
    void begin(TransactionContext transactionContext) throws DataAccessException;

    void commit(TransactionContext transactionContext) throws DataAccessException;

    void rollback(TransactionContext transactionContext) throws DataAccessException;
}

package org.jooq.impl;

import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoTransactionProvider.class */
public class NoTransactionProvider implements TransactionProvider {
    @Override // org.jooq.TransactionProvider
    public final void begin(TransactionContext ctx) {
        throw new UnsupportedOperationException("No transaction provider configured");
    }

    @Override // org.jooq.TransactionProvider
    public final void commit(TransactionContext ctx) {
        throw new UnsupportedOperationException("No transaction provider configured");
    }

    @Override // org.jooq.TransactionProvider
    public final void rollback(TransactionContext ctx) {
        throw new UnsupportedOperationException("No transaction provider configured");
    }
}

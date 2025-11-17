package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Transaction;
import org.jooq.TransactionContext;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultTransactionContext.class */
public class DefaultTransactionContext extends AbstractScope implements TransactionContext {
    Transaction transaction;
    Throwable cause;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultTransactionContext(Configuration configuration) {
        super(configuration);
    }

    @Override // org.jooq.TransactionContext
    public final Transaction transaction() {
        return this.transaction;
    }

    @Override // org.jooq.TransactionContext
    public final TransactionContext transaction(Transaction t) {
        this.transaction = t;
        return this;
    }

    @Override // org.jooq.TransactionContext
    public final Exception cause() {
        Throwable th = this.cause;
        if (!(th instanceof Exception)) {
            return null;
        }
        Exception e = (Exception) th;
        return e;
    }

    @Override // org.jooq.TransactionContext
    public final TransactionContext cause(Exception c) {
        this.cause = c;
        return this;
    }

    @Override // org.jooq.TransactionContext
    public final Throwable causeThrowable() {
        return this.cause;
    }

    @Override // org.jooq.TransactionContext
    public final TransactionContext causeThrowable(Throwable c) {
        this.cause = c;
        return this;
    }
}

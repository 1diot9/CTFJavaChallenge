package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TransactionContext.class */
public interface TransactionContext extends Scope {
    @Nullable
    Transaction transaction();

    @NotNull
    TransactionContext transaction(Transaction transaction);

    @Nullable
    Exception cause();

    @Nullable
    Throwable causeThrowable();

    @NotNull
    TransactionContext cause(Exception exc);

    @NotNull
    TransactionContext causeThrowable(Throwable th);
}

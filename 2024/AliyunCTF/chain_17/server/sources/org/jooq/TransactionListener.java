package org.jooq;

import java.util.function.Consumer;
import org.jooq.impl.CallbackTransactionListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TransactionListener.class */
public interface TransactionListener {
    default void beginStart(TransactionContext ctx) {
    }

    default void beginEnd(TransactionContext ctx) {
    }

    default void commitStart(TransactionContext ctx) {
    }

    default void commitEnd(TransactionContext ctx) {
    }

    default void rollbackStart(TransactionContext ctx) {
    }

    default void rollbackEnd(TransactionContext ctx) {
    }

    static CallbackTransactionListener onBeginStart(Consumer<? super TransactionContext> onBeginStart) {
        return new CallbackTransactionListener().onBeginStart(onBeginStart);
    }

    static CallbackTransactionListener onBeginEnd(Consumer<? super TransactionContext> onBeginEnd) {
        return new CallbackTransactionListener().onBeginEnd(onBeginEnd);
    }

    static CallbackTransactionListener onCommitStart(Consumer<? super TransactionContext> onCommitStart) {
        return new CallbackTransactionListener().onCommitStart(onCommitStart);
    }

    static CallbackTransactionListener onCommitEnd(Consumer<? super TransactionContext> onCommitEnd) {
        return new CallbackTransactionListener().onCommitEnd(onCommitEnd);
    }

    static CallbackTransactionListener onRollbackStart(Consumer<? super TransactionContext> onRollbackStart) {
        return new CallbackTransactionListener().onRollbackStart(onRollbackStart);
    }

    static CallbackTransactionListener onRollbackEnd(Consumer<? super TransactionContext> onRollbackEnd) {
        return new CallbackTransactionListener().onRollbackEnd(onRollbackEnd);
    }
}

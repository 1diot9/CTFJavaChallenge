package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.TransactionContext;
import org.jooq.TransactionListener;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CallbackTransactionListener.class */
public final class CallbackTransactionListener implements TransactionListener {
    private final Consumer<? super TransactionContext> onBeginStart;
    private final Consumer<? super TransactionContext> onBeginEnd;
    private final Consumer<? super TransactionContext> onCommitStart;
    private final Consumer<? super TransactionContext> onCommitEnd;
    private final Consumer<? super TransactionContext> onRollbackStart;
    private final Consumer<? super TransactionContext> onRollbackEnd;

    public CallbackTransactionListener() {
        this(null, null, null, null, null, null);
    }

    private CallbackTransactionListener(Consumer<? super TransactionContext> onBeginStart, Consumer<? super TransactionContext> onBeginEnd, Consumer<? super TransactionContext> onCommitStart, Consumer<? super TransactionContext> onCommitEnd, Consumer<? super TransactionContext> onRollbackStart, Consumer<? super TransactionContext> onRollbackEnd) {
        this.onBeginStart = onBeginStart;
        this.onBeginEnd = onBeginEnd;
        this.onCommitStart = onCommitStart;
        this.onCommitEnd = onCommitEnd;
        this.onRollbackStart = onRollbackStart;
        this.onRollbackEnd = onRollbackEnd;
    }

    @Override // org.jooq.TransactionListener
    public final void beginStart(TransactionContext ctx) {
        if (this.onBeginStart != null) {
            this.onBeginStart.accept(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void beginEnd(TransactionContext ctx) {
        if (this.onBeginEnd != null) {
            this.onBeginEnd.accept(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void commitStart(TransactionContext ctx) {
        if (this.onCommitStart != null) {
            this.onCommitStart.accept(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void commitEnd(TransactionContext ctx) {
        if (this.onCommitEnd != null) {
            this.onCommitEnd.accept(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void rollbackStart(TransactionContext ctx) {
        if (this.onRollbackStart != null) {
            this.onRollbackStart.accept(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void rollbackEnd(TransactionContext ctx) {
        if (this.onRollbackEnd != null) {
            this.onRollbackEnd.accept(ctx);
        }
    }

    public final CallbackTransactionListener onBeginStart(Consumer<? super TransactionContext> newOnBeginStart) {
        return new CallbackTransactionListener(newOnBeginStart, this.onBeginEnd, this.onCommitStart, this.onCommitEnd, this.onRollbackStart, this.onRollbackEnd);
    }

    public final CallbackTransactionListener onBeginEnd(Consumer<? super TransactionContext> newOnBeginEnd) {
        return new CallbackTransactionListener(this.onBeginStart, newOnBeginEnd, this.onCommitStart, this.onCommitEnd, this.onRollbackStart, this.onRollbackEnd);
    }

    public final CallbackTransactionListener onCommitStart(Consumer<? super TransactionContext> newOnCommitStart) {
        return new CallbackTransactionListener(this.onBeginStart, this.onBeginEnd, newOnCommitStart, this.onCommitEnd, this.onRollbackStart, this.onRollbackEnd);
    }

    public final CallbackTransactionListener onCommitEnd(Consumer<? super TransactionContext> newOnCommitEnd) {
        return new CallbackTransactionListener(this.onBeginStart, this.onBeginEnd, this.onCommitStart, newOnCommitEnd, this.onRollbackStart, this.onRollbackEnd);
    }

    public final CallbackTransactionListener onRollbackStart(Consumer<? super TransactionContext> newOnRollbackStart) {
        return new CallbackTransactionListener(this.onBeginStart, this.onBeginEnd, this.onCommitStart, this.onCommitEnd, newOnRollbackStart, this.onRollbackEnd);
    }

    public final CallbackTransactionListener onRollbackEnd(Consumer<? super TransactionContext> newOnRollbackEnd) {
        return new CallbackTransactionListener(this.onBeginStart, this.onBeginEnd, this.onCommitStart, this.onCommitEnd, this.onRollbackStart, newOnRollbackEnd);
    }
}

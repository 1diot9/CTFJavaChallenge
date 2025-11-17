package org.jooq.impl;

import java.util.Arrays;
import org.jooq.Configuration;
import org.jooq.TransactionContext;
import org.jooq.TransactionListener;
import org.jooq.conf.InvocationOrder;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TransactionListeners.class */
public class TransactionListeners implements TransactionListener {
    private final TransactionListener[] listeners;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransactionListeners(Configuration configuration) {
        this.listeners = (TransactionListener[]) Tools.map(configuration.transactionListenerProviders(), p -> {
            return p.provide();
        }, x$0 -> {
            return new TransactionListener[x$0];
        });
    }

    @Override // org.jooq.TransactionListener
    public final void beginStart(TransactionContext ctx) {
        Iterable<TransactionListener> reverseIterable;
        if (ctx.settings().getTransactionListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (TransactionListener listener : reverseIterable) {
            listener.beginStart(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void beginEnd(TransactionContext ctx) {
        Iterable<TransactionListener> reverseIterable;
        if (ctx.settings().getTransactionListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (TransactionListener listener : reverseIterable) {
            listener.beginEnd(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void commitStart(TransactionContext ctx) {
        Iterable<TransactionListener> reverseIterable;
        if (ctx.settings().getTransactionListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (TransactionListener listener : reverseIterable) {
            listener.commitStart(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void commitEnd(TransactionContext ctx) {
        Iterable<TransactionListener> reverseIterable;
        if (ctx.settings().getTransactionListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (TransactionListener listener : reverseIterable) {
            listener.commitEnd(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void rollbackStart(TransactionContext ctx) {
        Iterable<TransactionListener> reverseIterable;
        if (ctx.settings().getTransactionListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (TransactionListener listener : reverseIterable) {
            listener.rollbackStart(ctx);
        }
    }

    @Override // org.jooq.TransactionListener
    public final void rollbackEnd(TransactionContext ctx) {
        Iterable<TransactionListener> reverseIterable;
        if (ctx.settings().getTransactionListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
            reverseIterable = Arrays.asList(this.listeners);
        } else {
            reverseIterable = Tools.reverseIterable(this.listeners);
        }
        for (TransactionListener listener : reverseIterable) {
            listener.rollbackEnd(ctx);
        }
    }
}

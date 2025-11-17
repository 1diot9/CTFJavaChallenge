package org.jooq.impl;

import java.io.Serializable;
import org.jooq.TransactionListener;
import org.jooq.TransactionListenerProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultTransactionListenerProvider.class */
public class DefaultTransactionListenerProvider implements TransactionListenerProvider, Serializable {
    private final TransactionListener listener;

    public static TransactionListenerProvider[] providers(TransactionListener... listeners) {
        return (TransactionListenerProvider[]) Tools.map(listeners, DefaultTransactionListenerProvider::new, x$0 -> {
            return new TransactionListenerProvider[x$0];
        });
    }

    public DefaultTransactionListenerProvider(TransactionListener listener) {
        this.listener = listener;
    }

    @Override // org.jooq.TransactionListenerProvider
    public final TransactionListener provide() {
        return this.listener;
    }

    public String toString() {
        return this.listener.toString();
    }
}

package org.jooq.impl;

import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.impl.Tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThreadLocalTransactionProvider.class */
public class ThreadLocalTransactionProvider implements TransactionProvider {
    final DefaultTransactionProvider delegateTransactionProvider;
    final ThreadLocalConnectionProvider localConnectionProvider;
    final ThreadLocal<Connection> localTxConnection;
    final ThreadLocal<Deque<Configuration>> localConfigurations;

    public ThreadLocalTransactionProvider(ConnectionProvider connectionProvider) {
        this(connectionProvider, true);
    }

    public ThreadLocalTransactionProvider(ConnectionProvider connectionProvider, boolean nested) {
        this.localConnectionProvider = new ThreadLocalConnectionProvider(connectionProvider);
        this.delegateTransactionProvider = new DefaultTransactionProvider(this.localConnectionProvider, nested);
        this.localConfigurations = new ThreadLocal<>();
        this.localTxConnection = new ThreadLocal<>();
    }

    @Override // org.jooq.TransactionProvider
    public void begin(TransactionContext ctx) {
        this.delegateTransactionProvider.begin(ctx);
        configurations().push(ctx.configuration());
        if (this.delegateTransactionProvider.nestingLevel(ctx.configuration()) == 1) {
            this.localTxConnection.set(((DefaultConnectionProvider) ctx.configuration().data(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION)).connection);
        }
    }

    @Override // org.jooq.TransactionProvider
    public void commit(TransactionContext ctx) {
        if (this.delegateTransactionProvider.nestingLevel(ctx.configuration()) == 1) {
            this.localTxConnection.remove();
        }
        configurations().pop();
        this.delegateTransactionProvider.commit(ctx);
    }

    @Override // org.jooq.TransactionProvider
    public void rollback(TransactionContext ctx) {
        if (this.delegateTransactionProvider.nestingLevel(ctx.configuration()) == 1) {
            this.localTxConnection.remove();
        }
        configurations().pop();
        this.delegateTransactionProvider.rollback(ctx);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration configuration(Configuration fallback) {
        Deque<Configuration> configurations = configurations();
        return configurations.isEmpty() ? fallback : configurations.peek();
    }

    private Deque<Configuration> configurations() {
        Deque<Configuration> result = this.localConfigurations.get();
        if (result == null) {
            result = new ArrayDeque();
            this.localConfigurations.set(result);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ThreadLocalTransactionProvider$ThreadLocalConnectionProvider.class */
    public final class ThreadLocalConnectionProvider implements ConnectionProvider {
        final ConnectionProvider delegateConnectionProvider;

        public ThreadLocalConnectionProvider(ConnectionProvider delegate) {
            this.delegateConnectionProvider = delegate;
        }

        @Override // org.jooq.ConnectionProvider
        public final Connection acquire() {
            Connection local = ThreadLocalTransactionProvider.this.localTxConnection.get();
            if (local == null) {
                return this.delegateConnectionProvider.acquire();
            }
            return local;
        }

        @Override // org.jooq.ConnectionProvider
        public final void release(Connection connection) {
            Connection local = ThreadLocalTransactionProvider.this.localTxConnection.get();
            if (local == null) {
                this.delegateConnectionProvider.release(connection);
            } else if (local != connection) {
                throw new IllegalStateException("A different connection was released than the thread-bound one that was expected");
            }
        }
    }
}

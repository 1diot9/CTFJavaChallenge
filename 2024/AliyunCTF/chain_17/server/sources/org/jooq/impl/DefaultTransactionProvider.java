package org.jooq.impl;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.Tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultTransactionProvider.class */
public class DefaultTransactionProvider implements TransactionProvider {
    private static final java.sql.Savepoint IGNORED_SAVEPOINT = new DefaultSavepoint();
    private final ConnectionProvider connectionProvider;
    private final boolean nested;

    public DefaultTransactionProvider(ConnectionProvider connectionProvider) {
        this(connectionProvider, true);
    }

    public DefaultTransactionProvider(ConnectionProvider connectionProvider, boolean nested) {
        this.connectionProvider = connectionProvider;
        this.nested = nested;
    }

    public final boolean nested() {
        return this.nested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int nestingLevel(Configuration configuration) {
        return savepoints(configuration).size();
    }

    private final Deque<java.sql.Savepoint> savepoints(Configuration configuration) {
        Deque<java.sql.Savepoint> savepoints = (Deque) configuration.data(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS);
        if (savepoints == null) {
            savepoints = new ArrayDeque();
            configuration.data(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS, savepoints);
        }
        return savepoints;
    }

    private final boolean autoCommit(Configuration configuration) {
        Boolean autoCommit = (Boolean) configuration.data(Tools.BooleanDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT);
        if (!Boolean.TRUE.equals(autoCommit)) {
            autoCommit = Boolean.valueOf(connection(configuration).getAutoCommit());
            configuration.data(Tools.BooleanDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT, autoCommit);
        }
        return autoCommit.booleanValue();
    }

    private final DefaultConnectionProvider connection(Configuration configuration) {
        DefaultConnectionProvider connectionWrapper = (DefaultConnectionProvider) configuration.data(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION);
        if (connectionWrapper == null) {
            connectionWrapper = new DefaultConnectionProvider(this.connectionProvider.acquire());
            configuration.data(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION, connectionWrapper);
        }
        return connectionWrapper;
    }

    @Override // org.jooq.TransactionProvider
    public final void begin(TransactionContext ctx) {
        Deque<java.sql.Savepoint> savepoints = savepoints(ctx.configuration());
        boolean topLevel = savepoints.isEmpty();
        if (topLevel) {
            brace(ctx.configuration(), true);
        }
        savepoints.push(setSavepoint(ctx.configuration(), topLevel));
    }

    private final java.sql.Savepoint setSavepoint(Configuration configuration, boolean topLevel) {
        if (topLevel || !nested()) {
            return IGNORED_SAVEPOINT;
        }
        return connection(configuration).setSavepoint();
    }

    @Override // org.jooq.TransactionProvider
    public final void commit(TransactionContext ctx) {
        Deque<java.sql.Savepoint> savepoints = savepoints(ctx.configuration());
        java.sql.Savepoint savepoint = savepoints.pop();
        if (savepoint != null && savepoint != IGNORED_SAVEPOINT) {
            try {
                connection(ctx.configuration()).releaseSavepoint(savepoint);
            } catch (DataAccessException e) {
            }
        }
        if (savepoints.isEmpty()) {
            connection(ctx.configuration()).commit();
            brace(ctx.configuration(), false);
        }
    }

    @Override // org.jooq.TransactionProvider
    public final void rollback(TransactionContext ctx) {
        Deque<java.sql.Savepoint> savepoints = savepoints(ctx.configuration());
        java.sql.Savepoint savepoint = null;
        if (!savepoints.isEmpty()) {
            savepoint = savepoints.pop();
        }
        try {
            if (savepoint == null) {
                connection(ctx.configuration()).rollback();
            } else if (savepoint == IGNORED_SAVEPOINT) {
                if (savepoints.isEmpty()) {
                    connection(ctx.configuration()).rollback();
                }
            } else {
                connection(ctx.configuration()).rollback(savepoint);
            }
            if (savepoints.isEmpty()) {
                brace(ctx.configuration(), false);
            }
        } catch (Throwable th) {
            if (savepoints.isEmpty()) {
                brace(ctx.configuration(), false);
            }
            throw th;
        }
    }

    private final void brace(Configuration configuration, boolean start) {
        DefaultConnectionProvider connection = connection(configuration);
        try {
            boolean autoCommit = autoCommit(configuration);
            if (autoCommit) {
                connection.setAutoCommit(!start);
            }
        } finally {
            if (!start) {
                this.connectionProvider.release(connection.connection);
                configuration.data().remove(Tools.SimpleDataKey.DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultTransactionProvider$DefaultSavepoint.class */
    private static class DefaultSavepoint implements java.sql.Savepoint {
        private DefaultSavepoint() {
        }

        @Override // java.sql.Savepoint
        public int getSavepointId() throws SQLException {
            return 0;
        }

        @Override // java.sql.Savepoint
        public String getSavepointName() throws SQLException {
            return null;
        }
    }
}

package org.jooq.tools.r2dbc;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/r2dbc/DefaultConnection.class */
public class DefaultConnection implements Connection {
    private final Connection delegate;

    public DefaultConnection(Connection delegate) {
        this.delegate = delegate;
    }

    public Connection getDelegate() {
        return this.delegate;
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> beginTransaction() {
        return getDelegate().beginTransaction();
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> beginTransaction(TransactionDefinition definition) {
        return getDelegate().beginTransaction(definition);
    }

    @Override // io.r2dbc.spi.Connection, io.r2dbc.spi.Closeable
    public Publisher<Void> close() {
        return getDelegate().close();
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> commitTransaction() {
        return getDelegate().commitTransaction();
    }

    @Override // io.r2dbc.spi.Connection
    public Batch createBatch() {
        return getDelegate().createBatch();
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> createSavepoint(String name) {
        return getDelegate().createSavepoint(name);
    }

    @Override // io.r2dbc.spi.Connection
    public Statement createStatement(String sql) {
        return getDelegate().createStatement(sql);
    }

    @Override // io.r2dbc.spi.Connection
    public boolean isAutoCommit() {
        return getDelegate().isAutoCommit();
    }

    @Override // io.r2dbc.spi.Connection
    public ConnectionMetadata getMetadata() {
        return getDelegate().getMetadata();
    }

    @Override // io.r2dbc.spi.Connection
    public IsolationLevel getTransactionIsolationLevel() {
        return getDelegate().getTransactionIsolationLevel();
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> releaseSavepoint(String name) {
        return getDelegate().releaseSavepoint(name);
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> rollbackTransaction() {
        return getDelegate().rollbackTransaction();
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> rollbackTransactionToSavepoint(String name) {
        return getDelegate().rollbackTransactionToSavepoint(name);
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> setAutoCommit(boolean autoCommit) {
        return getDelegate().setAutoCommit(autoCommit);
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> setLockWaitTimeout(Duration timeout) {
        return getDelegate().setLockWaitTimeout(timeout);
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> setStatementTimeout(Duration timeout) {
        return getDelegate().setStatementTimeout(timeout);
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
        return getDelegate().setTransactionIsolationLevel(isolationLevel);
    }

    @Override // io.r2dbc.spi.Connection
    public Publisher<Boolean> validate(ValidationDepth depth) {
        return getDelegate().validate(depth);
    }
}

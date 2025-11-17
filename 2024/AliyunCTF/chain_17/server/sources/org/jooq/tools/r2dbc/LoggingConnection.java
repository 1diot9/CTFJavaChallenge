package org.jooq.tools.r2dbc;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import org.jooq.tools.JooqLogger;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/r2dbc/LoggingConnection.class */
public class LoggingConnection extends DefaultConnection {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) LoggingConnection.class);

    public LoggingConnection(Connection delegate) {
        super(delegate);
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> beginTransaction() {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::beginTransaction");
            }
            getDelegate().beginTransaction().subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> beginTransaction(TransactionDefinition definition) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::beginTransaction", definition);
            }
            getDelegate().beginTransaction(definition).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection, io.r2dbc.spi.Closeable
    public Publisher<Void> close() {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::close");
            }
            getDelegate().close().subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> commitTransaction() {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::commitTransaction");
            }
            getDelegate().commitTransaction().subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Batch createBatch() {
        return new LoggingBatch(getDelegate().createBatch());
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> createSavepoint(String name) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::createSavepoint", name);
            }
            getDelegate().createSavepoint(name).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Statement createStatement(String sql) {
        if (log.isDebugEnabled()) {
            log.debug("Connection::createStatement", sql);
        }
        return new LoggingStatement(getDelegate().createStatement(sql));
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> releaseSavepoint(String name) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::releaseSavepoint", name);
            }
            getDelegate().releaseSavepoint(name).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> rollbackTransaction() {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::rollbackTransaction");
            }
            getDelegate().rollbackTransaction().subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> rollbackTransactionToSavepoint(String name) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::rollbackTransactionToSavepoint", name);
            }
            getDelegate().rollbackTransactionToSavepoint(name).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> setAutoCommit(boolean autoCommit) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::setAutoCommit", Boolean.valueOf(autoCommit));
            }
            getDelegate().setAutoCommit(autoCommit).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> setLockWaitTimeout(Duration timeout) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::setLockWaitTimeout", timeout);
            }
            getDelegate().setLockWaitTimeout(timeout).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> setStatementTimeout(Duration timeout) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::setStatementTimeout", timeout);
            }
            getDelegate().setStatementTimeout(timeout).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::setTransactionIsolationLevel", isolationLevel);
            }
            getDelegate().setTransactionIsolationLevel(isolationLevel).subscribe(s);
        };
    }

    @Override // org.jooq.tools.r2dbc.DefaultConnection, io.r2dbc.spi.Connection
    public Publisher<Boolean> validate(ValidationDepth depth) {
        return s -> {
            if (log.isDebugEnabled()) {
                log.debug("Connection::validate", depth);
            }
            getDelegate().validate(depth).subscribe(s);
        };
    }
}

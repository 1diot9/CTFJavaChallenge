package org.jooq.impl;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import org.jooq.exception.DetachedException;
import org.jooq.impl.R2DBC;
import org.reactivestreams.Publisher;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConnectionFactory.class */
public final class DefaultConnectionFactory implements ConnectionFactory {
    Connection connection;
    final boolean finalize;
    final boolean nested;
    final AtomicInteger savepoints;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConnectionFactory(Connection connection) {
        this(connection, false, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultConnectionFactory(Connection connection, boolean finalize, boolean nested) {
        this.savepoints = new AtomicInteger();
        this.connection = connection;
        this.finalize = finalize;
        this.nested = nested;
    }

    final Connection connectionOrThrow() {
        if (this.connection == null) {
            throw new DetachedException("R2DBC Connection not available or already closed");
        }
        return this.connection;
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public final Publisher<? extends Connection> create() {
        return s -> {
            s.onSubscribe(R2DBC.AbstractSubscription.onRequest(s, x -> {
                x.onNext(new NonClosingConnection());
                x.onComplete();
            }));
        };
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public final ConnectionFactoryMetadata getMetadata() {
        return () -> {
            return connectionOrThrow().getMetadata().getDatabaseProductName();
        };
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConnectionFactory$NonClosingConnection.class */
    final class NonClosingConnection implements Connection {
        NonClosingConnection() {
        }

        private <T> T nest(IntSupplier level, Supplier<T> toplevelAction, IntFunction<T> nestedAction) {
            if (DefaultConnectionFactory.this.nested) {
                return nestedAction.apply(level.getAsInt());
            }
            return toplevelAction.get();
        }

        private String savepoint(int i) {
            return "S" + i;
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> beginTransaction() {
            AtomicInteger atomicInteger = DefaultConnectionFactory.this.savepoints;
            Objects.requireNonNull(atomicInteger);
            return (Publisher) nest(atomicInteger::getAndIncrement, () -> {
                return DefaultConnectionFactory.this.connectionOrThrow().beginTransaction();
            }, i -> {
                return DefaultConnectionFactory.this.connectionOrThrow().createSavepoint(savepoint(i));
            });
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> beginTransaction(TransactionDefinition definition) {
            AtomicInteger atomicInteger = DefaultConnectionFactory.this.savepoints;
            Objects.requireNonNull(atomicInteger);
            return (Publisher) nest(atomicInteger::getAndIncrement, () -> {
                return DefaultConnectionFactory.this.connectionOrThrow().beginTransaction(definition);
            }, i -> {
                return DefaultConnectionFactory.this.connectionOrThrow().createSavepoint(savepoint(i));
            });
        }

        @Override // io.r2dbc.spi.Connection, io.r2dbc.spi.Closeable
        public Publisher<Void> close() {
            return s -> {
                s.onSubscribe(R2DBC.AbstractSubscription.onRequest(s, x -> {
                    x.onComplete();
                }));
            };
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> commitTransaction() {
            AtomicInteger atomicInteger = DefaultConnectionFactory.this.savepoints;
            Objects.requireNonNull(atomicInteger);
            return (Publisher) nest(atomicInteger::decrementAndGet, () -> {
                return DefaultConnectionFactory.this.connectionOrThrow().commitTransaction();
            }, i -> {
                return DefaultConnectionFactory.this.connectionOrThrow().releaseSavepoint(savepoint(i));
            });
        }

        @Override // io.r2dbc.spi.Connection
        public Batch createBatch() {
            return DefaultConnectionFactory.this.connectionOrThrow().createBatch();
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> createSavepoint(String name) {
            return DefaultConnectionFactory.this.connectionOrThrow().createSavepoint(name);
        }

        @Override // io.r2dbc.spi.Connection
        public Statement createStatement(String sql) {
            return DefaultConnectionFactory.this.connectionOrThrow().createStatement(sql);
        }

        @Override // io.r2dbc.spi.Connection
        public boolean isAutoCommit() {
            return DefaultConnectionFactory.this.connectionOrThrow().isAutoCommit();
        }

        @Override // io.r2dbc.spi.Connection
        public ConnectionMetadata getMetadata() {
            return DefaultConnectionFactory.this.connectionOrThrow().getMetadata();
        }

        @Override // io.r2dbc.spi.Connection
        public IsolationLevel getTransactionIsolationLevel() {
            return DefaultConnectionFactory.this.connectionOrThrow().getTransactionIsolationLevel();
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> releaseSavepoint(String name) {
            return DefaultConnectionFactory.this.connectionOrThrow().releaseSavepoint(name);
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> rollbackTransaction() {
            AtomicInteger atomicInteger = DefaultConnectionFactory.this.savepoints;
            Objects.requireNonNull(atomicInteger);
            return (Publisher) nest(atomicInteger::decrementAndGet, () -> {
                return DefaultConnectionFactory.this.connectionOrThrow().rollbackTransaction();
            }, i -> {
                return DefaultConnectionFactory.this.connectionOrThrow().rollbackTransactionToSavepoint(savepoint(i));
            });
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> rollbackTransactionToSavepoint(String name) {
            return DefaultConnectionFactory.this.connectionOrThrow().rollbackTransactionToSavepoint(name);
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> setAutoCommit(boolean autoCommit) {
            return DefaultConnectionFactory.this.connectionOrThrow().setAutoCommit(autoCommit);
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
            return DefaultConnectionFactory.this.connectionOrThrow().setTransactionIsolationLevel(isolationLevel);
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Boolean> validate(ValidationDepth depth) {
            return DefaultConnectionFactory.this.connectionOrThrow().validate(depth);
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> setLockWaitTimeout(Duration timeout) {
            return DefaultConnectionFactory.this.connectionOrThrow().setLockWaitTimeout(timeout);
        }

        @Override // io.r2dbc.spi.Connection
        public Publisher<Void> setStatementTimeout(Duration timeout) {
            return DefaultConnectionFactory.this.connectionOrThrow().setStatementTimeout(timeout);
        }
    }
}

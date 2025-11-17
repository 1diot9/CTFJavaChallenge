package org.jooq.impl;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.exception.DetachedException;
import org.jooq.impl.DefaultRenderContext;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnectionFactory.class */
public final class ParsingConnectionFactory implements ConnectionFactory {
    final Configuration configuration;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParsingConnectionFactory(Configuration configuration) {
        if (configuration.connectionFactory() instanceof NoConnectionFactory) {
            throw new DetachedException("ConnectionProvider did not provide an R2DBC ConnectionFactory");
        }
        this.configuration = configuration.deriveSettings(s -> {
            return R2DBC.setParamType(configuration.dialect(), s);
        });
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public final Publisher<? extends Connection> create() {
        return subscriber -> {
            this.configuration.connectionFactory().create().subscribe(new ParsingR2DBCConnectionSubscriber(subscriber));
        };
    }

    @Override // io.r2dbc.spi.ConnectionFactory
    public final ConnectionFactoryMetadata getMetadata() {
        return this.configuration.connectionFactory().getMetadata();
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnectionFactory$ParsingR2DBCConnectionSubscriber.class */
    private final class ParsingR2DBCConnectionSubscriber implements Subscriber<Connection> {
        private final Subscriber<? super Connection> subscriber;

        private ParsingR2DBCConnectionSubscriber(Subscriber<? super Connection> subscriber) {
            this.subscriber = subscriber;
        }

        @Override // org.reactivestreams.Subscriber
        public final void onSubscribe(Subscription s) {
            this.subscriber.onSubscribe(s);
        }

        @Override // org.reactivestreams.Subscriber
        public final void onNext(Connection c) {
            this.subscriber.onNext(new ParsingR2DBCConnection(c));
        }

        @Override // org.reactivestreams.Subscriber
        public final void onError(Throwable t) {
            this.subscriber.onError(t);
        }

        @Override // org.reactivestreams.Subscriber
        public final void onComplete() {
            this.subscriber.onComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnectionFactory$ParsingR2DBCConnection.class */
    public final class ParsingR2DBCConnection implements Connection {
        private final Connection delegate;

        private ParsingR2DBCConnection(Connection delegate) {
            this.delegate = delegate;
        }

        @Override // io.r2dbc.spi.Connection
        public final Batch createBatch() {
            return new ParsingR2DBCBatch(this.delegate.createBatch());
        }

        @Override // io.r2dbc.spi.Connection
        public final Statement createStatement(String sql) {
            return new ParsingR2DBCStatement(this.delegate, sql);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> beginTransaction() {
            return this.delegate.beginTransaction();
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> beginTransaction(TransactionDefinition definition) {
            return this.delegate.beginTransaction(definition);
        }

        @Override // io.r2dbc.spi.Connection, io.r2dbc.spi.Closeable
        public final Publisher<Void> close() {
            return this.delegate.close();
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> commitTransaction() {
            return this.delegate.commitTransaction();
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> createSavepoint(String name) {
            return this.delegate.createSavepoint(name);
        }

        @Override // io.r2dbc.spi.Connection
        public final boolean isAutoCommit() {
            return this.delegate.isAutoCommit();
        }

        @Override // io.r2dbc.spi.Connection
        public final ConnectionMetadata getMetadata() {
            return this.delegate.getMetadata();
        }

        @Override // io.r2dbc.spi.Connection
        public final IsolationLevel getTransactionIsolationLevel() {
            return this.delegate.getTransactionIsolationLevel();
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> releaseSavepoint(String name) {
            return this.delegate.releaseSavepoint(name);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> rollbackTransaction() {
            return this.delegate.rollbackTransaction();
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> rollbackTransactionToSavepoint(String name) {
            return this.delegate.rollbackTransactionToSavepoint(name);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> setAutoCommit(boolean autoCommit) {
            return this.delegate.setAutoCommit(autoCommit);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) {
            return this.delegate.setTransactionIsolationLevel(isolationLevel);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Boolean> validate(ValidationDepth depth) {
            return this.delegate.validate(depth);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> setLockWaitTimeout(Duration timeout) {
            return this.delegate.setLockWaitTimeout(timeout);
        }

        @Override // io.r2dbc.spi.Connection
        public final Publisher<Void> setStatementTimeout(Duration timeout) {
            return this.delegate.setStatementTimeout(timeout);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnectionFactory$ParsingR2DBCBatch.class */
    private final class ParsingR2DBCBatch implements Batch {
        private final Batch delegate;

        private ParsingR2DBCBatch(Batch b) {
            this.delegate = b;
        }

        @Override // io.r2dbc.spi.Batch
        public final Batch add(String sql) {
            this.delegate.add(ParsingConnection.translate(ParsingConnectionFactory.this.configuration, sql, new Param[0]).sql);
            return this;
        }

        @Override // io.r2dbc.spi.Batch
        public final Publisher<? extends Result> execute() {
            return this.delegate.execute();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnectionFactory$ParsingR2DBCStatement.class */
    private final class ParsingR2DBCStatement implements Statement {
        private final Connection delegate;
        private final String input;
        private final List<List<Param<?>>> params = new ArrayList();

        private ParsingR2DBCStatement(Connection delegate, String input) {
            this.delegate = delegate;
            this.input = input;
            this.params.add(new ArrayList());
        }

        private final List<Param<?>> list(int index) {
            List<Param<?>> list = this.params.get(this.params.size() - 1);
            int reserve = (index + 1) - list.size();
            if (reserve > 0) {
                list.addAll(Collections.nCopies(reserve, null));
            }
            return list;
        }

        @Override // io.r2dbc.spi.Statement
        public final Statement add() {
            this.params.add(new ArrayList());
            return this;
        }

        @Override // io.r2dbc.spi.Statement
        public final Statement bind(int index, Object value) {
            list(index).set(index, DSL.val(value));
            return this;
        }

        @Override // io.r2dbc.spi.Statement
        public final Statement bind(String name, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override // io.r2dbc.spi.Statement
        public final Statement bindNull(int index, Class<?> type) {
            list(index).set(index, DSL.val((Object) null, type));
            return this;
        }

        @Override // io.r2dbc.spi.Statement
        public final Statement bindNull(String name, Class<?> type) {
            throw new UnsupportedOperationException();
        }

        @Override // io.r2dbc.spi.Statement
        public final Publisher<? extends Result> execute() {
            Statement statement = null;
            for (List<Param<?>> p : this.params) {
                if (statement != null) {
                    statement.add();
                }
                DefaultRenderContext.Rendered rendered = ParsingConnection.translate(ParsingConnectionFactory.this.configuration, this.input, (Param[]) p.toArray(Tools.EMPTY_PARAM));
                if (statement == null) {
                    statement = this.delegate.createStatement(rendered.sql);
                }
                int j = 0;
                Iterator<Param<?>> it = rendered.bindValues.iterator();
                while (it.hasNext()) {
                    Param<?> o = it.next();
                    if (o.getValue() == null) {
                        int i = j;
                        j++;
                        statement.bindNull(i, o.getType());
                    } else {
                        int i2 = j;
                        j++;
                        statement.bind(i2, o.getValue());
                    }
                }
            }
            return statement.execute();
        }
    }
}

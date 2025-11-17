package org.jooq.impl;

import ch.qos.logback.classic.ClassicConstants;
import io.r2dbc.spi.Batch;
import io.r2dbc.spi.ColumnMetadata;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import io.r2dbc.spi.R2dbcException;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jooq.Configuration;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.ExecuteContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.TransactionalPublishable;
import org.jooq.XML;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.CursorImpl;
import org.jooq.impl.DefaultConnectionFactory;
import org.jooq.impl.DefaultRenderContext;
import org.jooq.impl.ThreadGuard;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.DefaultPreparedStatement;
import org.jooq.tools.jdbc.DefaultResultSet;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.tools.jdbc.MockArray;
import org.jooq.types.Interval;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC.class */
public final class R2DBC {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) R2DBC.class);
    static volatile boolean is_0_9 = true;

    R2DBC() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$AbstractSubscription.class */
    public static abstract class AbstractSubscription<T> implements Subscription {
        final Subscriber<? super T> subscriber;
        final AtomicBoolean completed = new AtomicBoolean();
        final AtomicLong requested = new AtomicLong();
        final ThreadGuard.Guard guard = new ThreadGuard.Guard();

        abstract void request0();

        /* JADX INFO: Access modifiers changed from: package-private */
        public static <T> Subscription onRequest(Subscriber<? super T> s, final Consumer<? super Subscriber<? super T>> onRequest) {
            return new AbstractSubscription<T>(s) { // from class: org.jooq.impl.R2DBC.AbstractSubscription.1
                @Override // org.jooq.impl.R2DBC.AbstractSubscription
                void request0() {
                    onRequest.accept(this.subscriber);
                }
            };
        }

        AbstractSubscription(Subscriber<? super T> subscriber) {
            Objects.requireNonNull(subscriber);
            Consumer consumer = subscriber::onSubscribe;
            Objects.requireNonNull(subscriber);
            Consumer consumer2 = subscriber::onNext;
            Objects.requireNonNull(subscriber);
            this.subscriber = Internal.subscriber(consumer, consumer2, subscriber::onError, () -> {
                this.completed.set(true);
                subscriber.onComplete();
            });
        }

        @Override // org.reactivestreams.Subscription
        public final void request(long n) {
            if (n <= 0) {
                this.subscriber.onError(new IllegalArgumentException("Rule 3.9 non-positive request signals are illegal"));
            } else if (!this.completed.get()) {
                this.requested.accumulateAndGet(n, R2DBC::addNoOverflow);
                ThreadGuard.run(this.guard, this::request0, () -> {
                });
            }
        }

        @Override // org.reactivestreams.Subscription
        public final void cancel() {
            complete(() -> {
            });
        }

        final boolean moreRequested() {
            return !this.completed.get() && this.requested.getAndUpdate(l -> {
                return l == Long.MAX_VALUE ? l : Math.max(0L, l - 1);
            }) > 0;
        }

        final void complete(Runnable onComplete) {
            if (!this.completed.getAndSet(true)) {
                cancel0(false, onComplete);
            }
        }

        void cancel0(boolean closeAfterTransaction, Runnable onComplete) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$Forwarding.class */
    public static final class Forwarding<T> implements Subscriber<T> {
        final int forwarderIndex;
        final AbstractResultSubscriber<T> resultSubscriber;
        final AtomicReference<Subscription> subscription = new AtomicReference<>();

        Forwarding(int forwarderIndex, AbstractResultSubscriber<T> resultSubscriber) {
            this.forwarderIndex = forwarderIndex;
            this.resultSubscriber = resultSubscriber;
        }

        @Override // org.reactivestreams.Subscriber
        public final void onSubscribe(Subscription s) {
            this.subscription.set(s);
            this.resultSubscriber.downstream.request2(s);
        }

        @Override // org.reactivestreams.Subscriber
        public final void onNext(T value) {
            if (!this.resultSubscriber.downstream.completed.get()) {
                this.resultSubscriber.downstream.subscriber.onNext(value);
                this.resultSubscriber.downstream.request2(this.subscription.get());
            }
        }

        @Override // org.reactivestreams.Subscriber
        public final void onError(Throwable t) {
            complete(true, () -> {
                this.resultSubscriber.downstream.subscriber.onError(Tools.translate(this.resultSubscriber.downstream.sql(), t));
            });
        }

        @Override // org.reactivestreams.Subscriber
        public final void onComplete() {
            complete(false, () -> {
                this.resultSubscriber.downstream.subscriber.onComplete();
            });
        }

        private final void complete(boolean cancelled, Runnable onComplete) {
            this.resultSubscriber.downstream.forwarders.remove(Integer.valueOf(this.forwarderIndex));
            if (this.resultSubscriber.downstream.forwarders.isEmpty()) {
                if (cancelled || this.resultSubscriber.completionRequested.get()) {
                    this.resultSubscriber.complete(cancelled, onComplete);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$AbstractResultSubscriber.class */
    public static abstract class AbstractResultSubscriber<T> implements Subscriber<Result> {
        final AbstractNonBlockingSubscription<? super T> downstream;
        final AtomicBoolean completed = new AtomicBoolean();
        final AtomicBoolean completionRequested = new AtomicBoolean();

        AbstractResultSubscriber(AbstractNonBlockingSubscription<? super T> downstream) {
            this.downstream = downstream;
        }

        @Override // org.reactivestreams.Subscriber
        public final void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override // org.reactivestreams.Subscriber
        public final void onError(Throwable t) {
            complete(true, () -> {
                this.downstream.subscriber.onError(Tools.translate(this.downstream.sql(), t));
            });
        }

        @Override // org.reactivestreams.Subscriber
        public final void onComplete() {
            complete(false, () -> {
                this.downstream.subscriber.onComplete();
            });
        }

        final void complete(boolean cancelled, Runnable onComplete) {
            this.completionRequested.set(true);
            if ((cancelled || this.downstream.forwarders.isEmpty()) && !this.completed.getAndSet(true)) {
                this.downstream.complete(onComplete);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$RowCountSubscriber.class */
    public static final class RowCountSubscriber extends AbstractResultSubscriber<Integer> {
        /* JADX INFO: Access modifiers changed from: package-private */
        public RowCountSubscriber(AbstractNonBlockingSubscription<? super Integer> downstream) {
            super(downstream);
        }

        @Override // org.reactivestreams.Subscriber
        public void onNext(Result r) {
            Forwarding s = this.downstream.forwardingSubscriber(this);
            Publisher<Long> rowsUpdated = r.getRowsUpdated();
            Objects.requireNonNull(s);
            Consumer consumer = s::onSubscribe;
            Consumer consumer2 = t -> {
                if (t instanceof Long) {
                    Long l = (Long) t;
                    s.onNext(Integer.valueOf(l.intValue()));
                } else {
                    s.onNext(t);
                }
            };
            Objects.requireNonNull(s);
            Consumer consumer3 = s::onError;
            Objects.requireNonNull(s);
            rowsUpdated.subscribe(Internal.subscriber(consumer, consumer2, consumer3, s::onComplete));
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$ResultSubscriber.class */
    static final class ResultSubscriber<R extends Record, Q extends ResultQueryTrait<R>> extends AbstractResultSubscriber<R> {
        final Q query;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ResultSubscriber(Q query, AbstractNonBlockingSubscription<? super R> downstream) {
            super(downstream);
            this.query = query;
        }

        @Override // org.reactivestreams.Subscriber
        public final void onNext(Result r) {
            r.map((row, meta) -> {
                try {
                    Field<?>[] fields = this.query.getFields(() -> {
                        return new R2DBCResultSetMetaData(this.query.configuration(), meta);
                    });
                    RecordDelegate<AbstractRecord> delegate = Tools.newRecord(true, Tools.recordFactory(this.query.getRecordType(), Tools.row0(fields)), this.query.configuration());
                    DefaultBindingGetResultSetContext<?> ctx = new DefaultBindingGetResultSetContext<>(new SimpleExecuteContext(this.query.configuration(), this.query.configuration().data()), new R2DBCResultSet(this.query.configuration(), row, meta), 0);
                    return delegate.operate(new CursorImpl.CursorRecordInitialiser(new DefaultExecuteContext(this.query.configuration(), this.query), new DefaultExecuteListener(), ctx, Tools.row0(fields), 0, new boolean[0]));
                } catch (Throwable t) {
                    onError(t);
                    return null;
                }
            }).subscribe(this.downstream.forwardingSubscriber(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$ConnectionSubscriber.class */
    public static abstract class ConnectionSubscriber<T> implements Subscriber<Connection> {
        final AbstractNonBlockingSubscription<T> downstream;
        final AtomicReference<Connection> connection = new AtomicReference<>();

        abstract void onNext0(Connection connection);

        ConnectionSubscriber(AbstractNonBlockingSubscription<T> downstream) {
            this.downstream = downstream;
        }

        @Override // org.reactivestreams.Subscriber
        public final void onSubscribe(Subscription s) {
            s.request(1L);
        }

        @Override // org.reactivestreams.Subscriber
        public final void onNext(Connection c) {
            this.connection.set(c);
            onNext0(c);
        }

        @Override // org.reactivestreams.Subscriber
        public final void onError(Throwable t) {
            this.downstream.subscriber.onError(Tools.translate(this.downstream.sql(), t));
        }

        @Override // org.reactivestreams.Subscriber
        public final void onComplete() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$QueryExecutionSubscriber.class */
    public static final class QueryExecutionSubscriber<T, Q extends Query> extends ConnectionSubscriber<T> {
        final Q query;
        final Configuration configuration;
        final BiFunction<Q, AbstractNonBlockingSubscription<T>, Subscriber<Result>> resultSubscriber;
        volatile String sql;

        QueryExecutionSubscriber(Q query, QuerySubscription<T, Q> downstream, BiFunction<Q, AbstractNonBlockingSubscription<T>, Subscriber<Result>> resultSubscriber) {
            super(downstream);
            this.query = query;
            this.configuration = query.configuration();
            this.resultSubscriber = resultSubscriber;
        }

        @Override // org.jooq.impl.R2DBC.ConnectionSubscriber
        final void onNext0(Connection c) {
            int f;
            try {
                if (this.query.isExecutable()) {
                    DefaultRenderContext.Rendered rendered = R2DBC.rendered(this.configuration, this.query);
                    String str = rendered.sql;
                    this.sql = str;
                    Statement stmt = c.createStatement(str);
                    new DefaultBindContext(this.configuration, null, new R2DBCPreparedStatement(this.configuration, stmt)).visit(rendered.bindValues);
                    AbstractResultQuery<?> q1 = Tools.abstractResultQuery(this.query);
                    if (q1 != null && (f = SettingsTools.getFetchSize(q1.fetchSize(), this.configuration.settings())) != 0) {
                        if (R2DBC.log.isDebugEnabled()) {
                            R2DBC.log.debug("Setting fetch size", Integer.valueOf(f));
                        }
                        stmt.fetchSize(f);
                    }
                    AbstractDMLQuery<?> q2 = Tools.abstractDMLQuery(this.query);
                    if (q2 != null && !q2.returning.isEmpty() && !q2.nativeSupportReturningOrDataChangeDeltaTable(this.configuration.dsl())) {
                        stmt.returnGeneratedValues((String[]) Tools.map(q2.returningResolvedAsterisks, (v0) -> {
                            return v0.getName();
                        }, x$0 -> {
                            return new String[x$0];
                        }));
                    }
                    stmt.execute().subscribe(this.resultSubscriber.apply(this.query, this.downstream));
                } else {
                    if (R2DBC.log.isDebugEnabled()) {
                        R2DBC.log.debug("Query is not executable", this.query);
                    }
                    Subscriber<Result> s = this.resultSubscriber.apply(this.query, this.downstream);
                    s.onSubscribe(new NoOpSubscription(s));
                }
            } catch (Throwable t) {
                this.downstream.cancel();
                onError(t);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$NoOpSubscription.class */
    static final class NoOpSubscription extends Record implements Subscription {
        private final Subscriber<?> subscriber;

        NoOpSubscription(Subscriber<?> subscriber) {
            this.subscriber = subscriber;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, NoOpSubscription.class), NoOpSubscription.class, "subscriber", "FIELD:Lorg/jooq/impl/R2DBC$NoOpSubscription;->subscriber:Lorg/reactivestreams/Subscriber;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, NoOpSubscription.class), NoOpSubscription.class, "subscriber", "FIELD:Lorg/jooq/impl/R2DBC$NoOpSubscription;->subscriber:Lorg/reactivestreams/Subscriber;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, NoOpSubscription.class, Object.class), NoOpSubscription.class, "subscriber", "FIELD:Lorg/jooq/impl/R2DBC$NoOpSubscription;->subscriber:Lorg/reactivestreams/Subscriber;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Subscriber<?> subscriber() {
            return this.subscriber;
        }

        @Override // org.reactivestreams.Subscription
        public void request(long n) {
            this.subscriber.onComplete();
        }

        @Override // org.reactivestreams.Subscription
        public void cancel() {
            this.subscriber.onComplete();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$BatchMultipleSubscriber.class */
    static final class BatchMultipleSubscriber extends ConnectionSubscriber<Integer> {
        final BatchMultiple batch;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BatchMultipleSubscriber(BatchMultiple batch, BatchSubscription<BatchMultiple> downstream) {
            super(downstream);
            this.batch = batch;
        }

        @Override // org.jooq.impl.R2DBC.ConnectionSubscriber
        final void onNext0(Connection c) {
            try {
                Batch b = c.createBatch();
                for (int i = 0; i < this.batch.queries.length; i++) {
                    b = b.add(DSL.using(this.batch.configuration).renderInlined(this.batch.queries[i]));
                }
                b.execute().subscribe(new RowCountSubscriber(this.downstream));
            } catch (Throwable t) {
                this.downstream.cancel();
                onError(t);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$BatchSingleSubscriber.class */
    static final class BatchSingleSubscriber extends ConnectionSubscriber<Integer> {
        final BatchSingle batch;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BatchSingleSubscriber(BatchSingle batch, BatchSubscription<BatchSingle> downstream) {
            super(downstream);
            this.batch = batch;
        }

        @Override // org.jooq.impl.R2DBC.ConnectionSubscriber
        final void onNext0(Connection c) {
            List<Field<?>> fields;
            try {
                this.batch.checkBindValues();
                DefaultRenderContext.Rendered rendered = R2DBC.rendered(this.batch.configuration, this.batch.query);
                Statement stmt = c.createStatement(rendered.sql);
                Param<?>[] params = (Param[]) rendered.bindValues.toArray(Tools.EMPTY_PARAM);
                boolean first = true;
                for (Object[] bindValues : this.batch.allBindValues) {
                    if (first) {
                        first = false;
                    } else {
                        stmt = stmt.add();
                    }
                    DefaultBindContext defaultBindContext = new DefaultBindContext(this.batch.configuration, null, new R2DBCPreparedStatement(this.batch.query.configuration(), stmt));
                    if (params.length > 0) {
                        fields = Tools.fields(bindValues, params);
                    } else {
                        fields = Tools.fields(bindValues);
                    }
                    Tools.visitAll(defaultBindContext, fields);
                }
                stmt.execute().subscribe(new RowCountSubscriber(this.downstream));
            } catch (Throwable t) {
                this.downstream.cancel();
                onError(t);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$AbstractNonBlockingSubscription.class */
    public static abstract class AbstractNonBlockingSubscription<T> extends AbstractSubscription<T> {
        final Configuration configuration;
        final AtomicBoolean subscribed;
        final Publisher<? extends Connection> connection;
        final AtomicInteger nextForwarderIndex;
        final ConcurrentMap<Integer, Forwarding<T>> forwarders;

        abstract String sql();

        abstract ConnectionSubscriber<T> delegate();

        AbstractNonBlockingSubscription(Configuration configuration, Subscriber<? super T> subscriber) {
            super(subscriber);
            this.configuration = configuration;
            this.subscribed = new AtomicBoolean();
            this.connection = configuration.connectionFactory().create();
            this.nextForwarderIndex = new AtomicInteger();
            this.forwarders = new ConcurrentHashMap();
        }

        @Override // org.jooq.impl.R2DBC.AbstractSubscription
        final void request0() {
            if (!this.subscribed.getAndSet(true)) {
                ConnectionSubscriber<T> delegate = delegate();
                Publisher<? extends Connection> publisher = this.connection;
                Objects.requireNonNull(delegate);
                Consumer consumer = delegate::onSubscribe;
                Consumer consumer2 = c -> {
                    delegate.onNext(c);
                    request1();
                };
                Objects.requireNonNull(delegate);
                Consumer consumer3 = delegate::onError;
                Objects.requireNonNull(delegate);
                publisher.subscribe(Internal.subscriber(consumer, consumer2, consumer3, delegate::onComplete));
                return;
            }
            request1();
        }

        private final void forAllForwardingSubscriptions(Consumer<? super Subscription> consumer) {
            for (Forwarding<T> f : this.forwarders.values()) {
                Subscription s = f.subscription.get();
                if (s != null) {
                    consumer.accept(s);
                }
            }
        }

        private final void request1() {
            forAllForwardingSubscriptions(this::request2);
        }

        final void request2(Subscription s) {
            if (moreRequested()) {
                s.request(1L);
            }
        }

        @Override // org.jooq.impl.R2DBC.AbstractSubscription
        final void cancel0(boolean closeAfterTransaction, Runnable onComplete) {
            forAllForwardingSubscriptions((v0) -> {
                v0.cancel();
            });
            delegate().connection.updateAndGet(c -> {
                if (c == null || (c instanceof DefaultConnectionFactory.NonClosingConnection) || ((this instanceof TransactionSubscription) && !closeAfterTransaction)) {
                    onComplete.run();
                    return c;
                }
                c.close().subscribe(Internal.subscriber(s -> {
                    s.request(Long.MAX_VALUE);
                }, t -> {
                }, t2 -> {
                }, onComplete));
                return null;
            });
        }

        final Forwarding<T> forwardingSubscriber(AbstractResultSubscriber<T> resultSubscriber) {
            int i = this.nextForwarderIndex.getAndIncrement();
            Forwarding<T> f = new Forwarding<>(i, resultSubscriber);
            this.forwarders.put(Integer.valueOf(i), f);
            return f;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$QuerySubscription.class */
    static final class QuerySubscription<T, Q extends Query> extends AbstractNonBlockingSubscription<T> {
        final QueryExecutionSubscriber<T, Q> queryExecutionSubscriber;

        /* JADX INFO: Access modifiers changed from: package-private */
        public QuerySubscription(Q query, Subscriber<? super T> subscriber, BiFunction<Q, AbstractNonBlockingSubscription<T>, Subscriber<Result>> resultSubscriber) {
            super(query.configuration(), subscriber);
            this.queryExecutionSubscriber = new QueryExecutionSubscriber<>(query, this, resultSubscriber);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // org.jooq.impl.R2DBC.AbstractNonBlockingSubscription
        public final QueryExecutionSubscriber<T, Q> delegate() {
            return this.queryExecutionSubscriber;
        }

        @Override // org.jooq.impl.R2DBC.AbstractNonBlockingSubscription
        final String sql() {
            String result = this.queryExecutionSubscriber.sql;
            return result != null ? result : R2DBC.sql0(() -> {
                return String.valueOf(this.queryExecutionSubscriber.query);
            });
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$BatchSubscription.class */
    static final class BatchSubscription<B extends AbstractBatch> extends AbstractNonBlockingSubscription<Integer> {
        final ConnectionSubscriber<Integer> batchSubscriber;
        final B batch;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BatchSubscription(B batch, Subscriber<? super Integer> subscriber, java.util.function.Function<BatchSubscription<B>, ConnectionSubscriber<Integer>> batchSubscriber) {
            super(batch.configuration, subscriber);
            this.batchSubscriber = batchSubscriber.apply(this);
            this.batch = batch;
        }

        @Override // org.jooq.impl.R2DBC.AbstractNonBlockingSubscription
        final ConnectionSubscriber<Integer> delegate() {
            return this.batchSubscriber;
        }

        @Override // org.jooq.impl.R2DBC.AbstractNonBlockingSubscription
        final String sql() {
            return R2DBC.sql0(() -> {
                return this.batch.toString();
            });
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$TransactionSubscription.class */
    static final class TransactionSubscription<T> extends AbstractNonBlockingSubscription<T> {
        final TransactionalPublishable<T> transactional;
        final ConnectionSubscriber<T> delegate;

        /* JADX INFO: Access modifiers changed from: package-private */
        public TransactionSubscription(DSLContext ctx, final Subscriber<? super T> subscriber, final TransactionalPublishable<T> transactional) {
            super(ctx.configuration(), subscriber);
            this.transactional = transactional;
            this.delegate = new ConnectionSubscriber<T>(this) { // from class: org.jooq.impl.R2DBC.TransactionSubscription.1
                @Override // org.jooq.impl.R2DBC.ConnectionSubscriber
                void onNext0(Connection c) {
                    Publisher<Void> beginTransaction = c.beginTransaction();
                    Consumer consumer = s -> {
                        s.request(1L);
                    };
                    Consumer consumer2 = v -> {
                    };
                    Subscriber subscriber2 = subscriber;
                    Objects.requireNonNull(subscriber2);
                    Consumer consumer3 = subscriber2::onError;
                    TransactionalPublishable transactionalPublishable = transactional;
                    Subscriber subscriber3 = subscriber;
                    beginTransaction.subscribe(Internal.subscriber(consumer, consumer2, consumer3, () -> {
                        Configuration derive;
                        try {
                            if (c instanceof DefaultConnectionFactory.NonClosingConnection) {
                                derive = TransactionSubscription.this.configuration;
                            } else {
                                derive = TransactionSubscription.this.configuration.derive(new DefaultConnectionFactory(c));
                            }
                            Publisher<T> run = transactionalPublishable.run(derive);
                            Consumer consumer4 = s1 -> {
                                s1.request(Long.MAX_VALUE);
                            };
                            Objects.requireNonNull(subscriber3);
                            run.subscribe(Internal.subscriber(consumer4, subscriber3::onNext, e -> {
                                rollback(subscriber3, c, e);
                            }, () -> {
                                c.commitTransaction().subscribe(Internal.subscriber(s2 -> {
                                    s2.request(1L);
                                }, v2 -> {
                                }, t -> {
                                    TransactionSubscription.this.cancel0(true, () -> {
                                        subscriber3.onError(t);
                                    });
                                }, () -> {
                                    TransactionSubscription.this.cancel0(true, () -> {
                                        subscriber3.onComplete();
                                    });
                                }));
                            }));
                        } catch (Exception e2) {
                            rollback(subscriber3, c, e2);
                        }
                    }));
                }

                private final void rollback(Subscriber<? super T> s, Connection c, Throwable e) {
                    c.rollbackTransaction().subscribe(Internal.subscriber(s2 -> {
                        s2.request(1L);
                    }, v -> {
                    }, t -> {
                        TransactionSubscription.this.cancel0(true, () -> {
                            s.onError(t);
                        });
                    }, () -> {
                        TransactionSubscription.this.cancel0(true, () -> {
                            s.onError(e);
                        });
                    }));
                }
            };
        }

        @Override // org.jooq.impl.R2DBC.AbstractNonBlockingSubscription
        final String sql() {
            return "TransactionSubscription";
        }

        @Override // org.jooq.impl.R2DBC.AbstractNonBlockingSubscription
        final ConnectionSubscriber<T> delegate() {
            return this.delegate;
        }
    }

    static final DefaultRenderContext.Rendered rendered(Configuration configuration, Query query) {
        DefaultRenderContext render = new DefaultRenderContext(configuration.deriveSettings(s -> {
            return setParamType(configuration.dialect(), s);
        }), (ExecuteContext) null);
        return new DefaultRenderContext.Rendered(render.paramType(render.settings().getParamType()).visit(query).render(), render.bindValues(), render.skipUpdateCounts());
    }

    static final long addNoOverflow(long x, long y) {
        long r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            return Long.MAX_VALUE;
        }
        return r;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static final <T> T block(Publisher<? extends T> publisher) throws Throwable {
        Object obj = new Object();
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
        Consumer consumer = s -> {
            s.request(1L);
        };
        Objects.requireNonNull(linkedBlockingQueue);
        Consumer consumer2 = linkedBlockingQueue::add;
        Objects.requireNonNull(linkedBlockingQueue);
        publisher.subscribe(Internal.subscriber(consumer, consumer2, (v1) -> {
            r3.add(v1);
        }, () -> {
            linkedBlockingQueue.add(obj);
        }));
        try {
            T t = (T) linkedBlockingQueue.take();
            if (t instanceof Throwable) {
                throw ((Throwable) t);
            }
            if (t == obj) {
                return null;
            }
            return t;
        } catch (InterruptedException e) {
            throw new DataAccessException("Exception when blocking on publisher", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T blockWrappingExceptions(Publisher<? extends T> publisher) {
        try {
            return (T) block(publisher);
        } catch (Throwable th) {
            throw new DataAccessException("Exception when blocking on publisher", th);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Connection getConnection(String url) {
        return getConnection(url, new Properties());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Connection getConnection(String url, String username, String password) {
        Properties properties = new Properties();
        properties.setProperty(ClassicConstants.USER_MDC_KEY, username);
        properties.setProperty("password", password);
        return getConnection(url, properties);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Connection getConnection(String url, Properties properties) {
        if (properties.isEmpty()) {
            return (Connection) blockWrappingExceptions(ConnectionFactories.get(url).create());
        }
        ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.parse(url).mutate();
        properties.forEach((k, v) -> {
            if (ClassicConstants.USER_MDC_KEY.equals(k)) {
                setOption(builder, ConnectionFactoryOptions.USER, v);
                return;
            }
            if ("password".equals(k)) {
                setOption(builder, ConnectionFactoryOptions.PASSWORD, v);
                return;
            }
            if ("host".equals(k)) {
                setOption(builder, ConnectionFactoryOptions.HOST, v);
                return;
            }
            if ("port".equals(k)) {
                setOption(builder, ConnectionFactoryOptions.PORT, Integer.valueOf(Integer.parseInt(String.valueOf(v))));
                return;
            }
            if ("database".equals(k)) {
                setOption(builder, ConnectionFactoryOptions.DATABASE, v);
            } else if ("ssl".equals(k)) {
                setOption(builder, ConnectionFactoryOptions.SSL, v);
            } else {
                setOption(builder, Option.valueOf(String.valueOf(k)), v);
            }
        });
        return (Connection) blockWrappingExceptions(ConnectionFactories.get(builder.build()).create());
    }

    private static <T> ConnectionFactoryOptions.Builder setOption(ConnectionFactoryOptions.Builder builder, Option<T> option, Object v) {
        return builder.option(option, option.cast(v));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$R2DBCGenericException.class */
    public static final class R2DBCGenericException extends R2dbcException {
        R2DBCGenericException(Throwable cause) {
            super(cause);
        }
    }

    static final void wrapExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (R2dbcException e) {
            throw e;
        } catch (Exception e2) {
            throw new R2DBCGenericException(e2);
        }
    }

    static final <T> T wrapExceptions(Callable<T> callable) {
        try {
            return callable.call();
        } catch (R2dbcException e) {
            throw e;
        } catch (Exception e2) {
            throw new R2DBCGenericException(e2);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$R2DBCPreparedStatement.class */
    static final class R2DBCPreparedStatement extends DefaultPreparedStatement {
        final Configuration c;
        final Statement s;
        private static final Set<SQLDialect> NO_SUPPORT_UUID = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);

        R2DBCPreparedStatement(Configuration c, Statement s) {
            super(null, null, () -> {
                return new SQLFeatureNotSupportedException("Unsupported operation of the JDBC to R2DBC bridge.");
            });
            this.c = c;
            this.s = s;
        }

        private final void bindNonNull(int parameterIndex, Object x) {
            R2DBC.wrapExceptions(() -> {
                switch (this.c.family()) {
                    default:
                        this.s.bind(parameterIndex - 1, x);
                        return;
                }
            });
        }

        private final <T> void bindNull(int parameterIndex, Class<T> type) {
            R2DBC.wrapExceptions(() -> {
                switch (this.c.family()) {
                    default:
                        this.s.bindNull(parameterIndex - 1, (Class<?>) type);
                        return;
                }
            });
        }

        private final <T> void bindNullable(int parameterIndex, T x, Class<T> type) {
            bindNullable(parameterIndex, x, type, t -> {
                return t;
            });
        }

        private final <T, U> void bindNullable(int parameterIndex, T x, Class<U> type, java.util.function.Function<? super T, ? extends U> conversion) {
            if (x == null) {
                bindNull(parameterIndex, type);
            } else {
                bindNonNull(parameterIndex, conversion.apply(x));
            }
        }

        private final Class<?> type(int sqlType) {
            switch (sqlType) {
                case 91:
                    return LocalDate.class;
                case 92:
                    return LocalTime.class;
                case 93:
                    return LocalDateTime.class;
                default:
                    return DefaultDataType.getDataType(this.c.family(), sqlType).getType();
            }
        }

        private final Class<?> nullType(Class<?> type) {
            if (type == Date.class) {
                return LocalDate.class;
            }
            if (type == Time.class) {
                return LocalTime.class;
            }
            if (type == Timestamp.class) {
                return LocalDateTime.class;
            }
            if (type == Year.class) {
                return Integer.class;
            }
            if (type == XML.class || type == JSON.class || type == JSONB.class) {
                return String.class;
            }
            if ((type == UUID.class && NO_SUPPORT_UUID.contains(this.c.dialect())) || Enum.class.isAssignableFrom(type) || Interval.class.isAssignableFrom(type)) {
                return String.class;
            }
            return type;
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setNull(int parameterIndex, int sqlType) throws SQLException {
            bindNull(parameterIndex, type(sqlType));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
            bindNull(parameterIndex, type(sqlType));
        }

        public final void setNull(int parameterIndex, DataType<?> dataType) {
            bindNull(parameterIndex, nullType(dataType.getType()));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setBoolean(int parameterIndex, boolean x) throws SQLException {
            switch (this.c.family()) {
                case MYSQL:
                    bindNonNull(parameterIndex, Integer.valueOf(x ? 1 : 0));
                    return;
                default:
                    bindNonNull(parameterIndex, Boolean.valueOf(x));
                    return;
            }
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setByte(int parameterIndex, byte x) throws SQLException {
            bindNonNull(parameterIndex, Byte.valueOf(x));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setShort(int parameterIndex, short x) throws SQLException {
            bindNonNull(parameterIndex, Short.valueOf(x));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setInt(int parameterIndex, int x) throws SQLException {
            bindNonNull(parameterIndex, Integer.valueOf(x));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setLong(int parameterIndex, long x) throws SQLException {
            bindNonNull(parameterIndex, Long.valueOf(x));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setFloat(int parameterIndex, float x) throws SQLException {
            bindNonNull(parameterIndex, Float.valueOf(x));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setDouble(int parameterIndex, double x) throws SQLException {
            bindNonNull(parameterIndex, Double.valueOf(x));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
            bindNullable(parameterIndex, x, BigDecimal.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setString(int parameterIndex, String x) throws SQLException {
            bindNullable(parameterIndex, x, String.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setNString(int parameterIndex, String value) throws SQLException {
            bindNullable(parameterIndex, value, String.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setBytes(int parameterIndex, byte[] x) throws SQLException {
            bindNullable(parameterIndex, x, byte[].class);
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setDate(int parameterIndex, Date x) throws SQLException {
            bindNullable(parameterIndex, x, LocalDate.class, (v0) -> {
                return v0.toLocalDate();
            });
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setTime(int parameterIndex, Time x) throws SQLException {
            bindNullable(parameterIndex, x, LocalTime.class, (v0) -> {
                return v0.toLocalTime();
            });
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
            bindNullable(parameterIndex, x, LocalDateTime.class, (v0) -> {
                return v0.toLocalDateTime();
            });
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
            bindNullable(parameterIndex, x, type(targetSqlType));
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setObject(int parameterIndex, Object x) throws SQLException {
            bindNullable(parameterIndex, x, Object.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
        public final void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
            setObject(parameterIndex, x, targetSqlType);
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement
        public final void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
            setObject(parameterIndex, x, ((Integer) StringUtils.defaultIfNull(targetSqlType.getVendorTypeNumber(), 1111)).intValue());
        }

        @Override // org.jooq.tools.jdbc.DefaultPreparedStatement
        public final void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
            setObject(parameterIndex, x, ((Integer) StringUtils.defaultIfNull(targetSqlType.getVendorTypeNumber(), 1111)).intValue());
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$R2DBCResultSet.class */
    static final class R2DBCResultSet extends DefaultResultSet {
        final Configuration c;
        final Row r;
        final RowMetadata m;
        boolean wasNull;

        R2DBCResultSet(Configuration c, Row r, RowMetadata m) {
            super(null, null, () -> {
                return new SQLFeatureNotSupportedException("Unsupported operation of the JDBC to R2DBC bridge.");
            });
            this.c = c;
            this.r = new DefaultRow(c, r);
            this.m = m;
        }

        private final <T> T wasNull(T nullable) {
            this.wasNull = nullable == null;
            return nullable;
        }

        private final <T> T nullable(int i, Class<T> cls) {
            return (T) nullable(i, cls, t -> {
                return t;
            });
        }

        private final <T, U> U nullable(int i, Class<T> cls, java.util.function.Function<? super T, ? extends U> function) {
            return (U) R2DBC.wrapExceptions(() -> {
                Object wasNull = wasNull(this.r.get(i - 1, cls));
                if (this.wasNull) {
                    return null;
                }
                return function.apply(wasNull);
            });
        }

        private final <U> U nullable(int i, java.util.function.Function<? super Object, ? extends U> function) {
            return (U) R2DBC.wrapExceptions(() -> {
                Object t = wasNull(this.r.get(i - 1));
                if (this.wasNull) {
                    return null;
                }
                return function.apply(t);
            });
        }

        private final <T> T nonNull(int i, Class<T> cls, T t) {
            return (T) R2DBC.wrapExceptions(() -> {
                return this.wasNull ? t : wasNull(this.r.get(i - 1, cls));
            });
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final boolean wasNull() throws SQLException {
            return this.wasNull;
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final boolean getBoolean(int columnIndex) throws SQLException {
            return ((Boolean) nonNull(columnIndex, Boolean.class, false)).booleanValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final byte getByte(int columnIndex) throws SQLException {
            return ((Byte) nonNull(columnIndex, Byte.class, (byte) 0)).byteValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final short getShort(int columnIndex) throws SQLException {
            return ((Short) nonNull(columnIndex, Short.class, (short) 0)).shortValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final int getInt(int columnIndex) throws SQLException {
            return ((Integer) nonNull(columnIndex, Integer.class, 0)).intValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final long getLong(int columnIndex) throws SQLException {
            return ((Long) nonNull(columnIndex, Long.class, 0L)).longValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final float getFloat(int columnIndex) throws SQLException {
            return ((Float) nonNull(columnIndex, Float.class, Float.valueOf(0.0f))).floatValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final double getDouble(int columnIndex) throws SQLException {
            return ((Double) nonNull(columnIndex, Double.class, Double.valueOf(0.0d))).doubleValue();
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            return (BigDecimal) nullable(columnIndex, BigDecimal.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final String getString(int columnIndex) throws SQLException {
            return (String) nullable(columnIndex, String.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final byte[] getBytes(int columnIndex) throws SQLException {
            return (byte[]) nullable(columnIndex, byte[].class);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final Date getDate(int columnIndex) throws SQLException {
            return (Date) nullable(columnIndex, LocalDate.class, Date::valueOf);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final Time getTime(int columnIndex) throws SQLException {
            return (Time) nullable(columnIndex, LocalTime.class, Time::valueOf);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final Timestamp getTimestamp(int columnIndex) throws SQLException {
            return (Timestamp) nullable(columnIndex, LocalDateTime.class, Timestamp::valueOf);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final Object getObject(int columnIndex) throws SQLException {
            return getObject(columnIndex, Object.class);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, org.jooq.tools.jdbc.JDBC41ResultSet
        public final <T> T getObject(int i, Class<T> cls) throws SQLException {
            return (T) nullable(i, cls);
        }

        @Override // org.jooq.tools.jdbc.DefaultResultSet, java.sql.ResultSet
        public final java.sql.Array getArray(int columnIndex) throws SQLException {
            return new MockArray(this.c.dialect(), (Object[]) nullable(columnIndex, Object.class), Object[].class);
        }

        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow.class */
        private static final class DefaultRow extends Record implements Row {
            private final Configuration c;
            private final Row r;

            private DefaultRow(Configuration c, Row r) {
                this.c = c;
                this.r = r;
            }

            @Override // java.lang.Record
            public final String toString() {
                return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, DefaultRow.class), DefaultRow.class, "c;r", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow;->c:Lorg/jooq/Configuration;", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow;->r:Lio/r2dbc/spi/Row;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final int hashCode() {
                return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, DefaultRow.class), DefaultRow.class, "c;r", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow;->c:Lorg/jooq/Configuration;", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow;->r:Lio/r2dbc/spi/Row;").dynamicInvoker().invoke(this) /* invoke-custom */;
            }

            @Override // java.lang.Record
            public final boolean equals(Object o) {
                return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, DefaultRow.class, Object.class), DefaultRow.class, "c;r", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow;->c:Lorg/jooq/Configuration;", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSet$DefaultRow;->r:Lio/r2dbc/spi/Row;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
            }

            public Configuration c() {
                return this.c;
            }

            public Row r() {
                return this.r;
            }

            @Override // io.r2dbc.spi.Readable
            public final <T> T get(int i, Class<T> cls) {
                return (T) R2DBC.wrapExceptions(() -> {
                    switch (this.c.family()) {
                        case MYSQL:
                        case H2:
                            return get0(this.r.get(i), cls);
                        default:
                            return this.r.get(i, cls);
                    }
                });
            }

            @Override // io.r2dbc.spi.Readable
            public final <T> T get(String str, Class<T> cls) {
                return (T) R2DBC.wrapExceptions(() -> {
                    switch (this.c.family()) {
                        case MYSQL:
                        case H2:
                            return get0(this.r.get(str), cls);
                        default:
                            return this.r.get(str, cls);
                    }
                });
            }

            private final <T> T get0(Object obj, Class<T> cls) {
                if (obj == null) {
                    return null;
                }
                Converter provide = this.c.converterProvider().provide(obj.getClass(), cls);
                if (provide == null) {
                    throw new DataTypeException("Cannot convert from " + String.valueOf(obj.getClass()) + " to " + String.valueOf(cls) + ". Please report an issue here: https://jooq.org/bug. As a workaround, you can implement a ConverterProvider.");
                }
                return (T) ContextConverter.scoped(provide).from(obj, Tools.converterContext(this.c));
            }

            @Override // io.r2dbc.spi.Row
            public final RowMetadata getMetadata() {
                return this.r.getMetadata();
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$R2DBCResultSetMetaData.class */
    static final class R2DBCResultSetMetaData extends Record implements ResultSetMetaData {
        private final Configuration c;
        private final RowMetadata m;

        R2DBCResultSetMetaData(Configuration c, RowMetadata m) {
            this.c = c;
            this.m = m;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, R2DBCResultSetMetaData.class), R2DBCResultSetMetaData.class, "c;m", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSetMetaData;->c:Lorg/jooq/Configuration;", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSetMetaData;->m:Lio/r2dbc/spi/RowMetadata;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, R2DBCResultSetMetaData.class), R2DBCResultSetMetaData.class, "c;m", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSetMetaData;->c:Lorg/jooq/Configuration;", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSetMetaData;->m:Lio/r2dbc/spi/RowMetadata;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, R2DBCResultSetMetaData.class, Object.class), R2DBCResultSetMetaData.class, "c;m", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSetMetaData;->c:Lorg/jooq/Configuration;", "FIELD:Lorg/jooq/impl/R2DBC$R2DBCResultSetMetaData;->m:Lio/r2dbc/spi/RowMetadata;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Configuration c() {
            return this.c;
        }

        public RowMetadata m() {
            return this.m;
        }

        private final ColumnMetadata meta(int column) {
            return this.m.getColumnMetadata(column - 1);
        }

        @Override // java.sql.Wrapper
        public final <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLFeatureNotSupportedException("R2DBC can't unwrap JDBC types");
        }

        @Override // java.sql.Wrapper
        public final boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override // java.sql.ResultSetMetaData
        public final int getColumnCount() throws SQLException {
            return this.m.getColumnMetadatas().size();
        }

        @Override // java.sql.ResultSetMetaData
        public final int isNullable(int column) throws SQLException {
            switch (meta(column).getNullability()) {
                case NON_NULL:
                    return 0;
                case NULLABLE:
                    return 1;
                case UNKNOWN:
                    return 2;
                default:
                    throw new SQLFeatureNotSupportedException("Nullability: " + meta(column).getNullability().toString());
            }
        }

        @Override // java.sql.ResultSetMetaData
        public final String getCatalogName(int column) throws SQLException {
            return "";
        }

        @Override // java.sql.ResultSetMetaData
        public final String getSchemaName(int column) throws SQLException {
            return "";
        }

        @Override // java.sql.ResultSetMetaData
        public final String getTableName(int column) throws SQLException {
            return "";
        }

        @Override // java.sql.ResultSetMetaData
        public final String getColumnLabel(int column) throws SQLException {
            return getColumnName(column);
        }

        @Override // java.sql.ResultSetMetaData
        public final String getColumnName(int column) throws SQLException {
            return meta(column).getName();
        }

        @Override // java.sql.ResultSetMetaData
        public final int getPrecision(int column) throws SQLException {
            return ((Integer) StringUtils.defaultIfNull(meta(column).getPrecision(), 0)).intValue();
        }

        @Override // java.sql.ResultSetMetaData
        public final int getScale(int column) throws SQLException {
            return ((Integer) StringUtils.defaultIfNull(meta(column).getScale(), 0)).intValue();
        }

        private final Class<?> getType(int column) {
            return (Class) StringUtils.defaultIfNull(meta(column).getJavaType(), Object.class);
        }

        private final DataType<?> getDataType(int column) {
            return DefaultDataType.getDataType(this.c.family(), getType(column));
        }

        @Override // java.sql.ResultSetMetaData
        public final int getColumnType(int column) throws SQLException {
            return getDataType(column).getSQLType();
        }

        @Override // java.sql.ResultSetMetaData
        public final String getColumnClassName(int column) throws SQLException {
            return getType(column).getName();
        }

        @Override // java.sql.ResultSetMetaData
        public final String getColumnTypeName(int column) throws SQLException {
            if (R2DBC.is_0_9) {
                try {
                    return meta(column).getType().getName();
                } catch (AbstractMethodError e) {
                    R2DBC.is_0_9 = false;
                }
            }
            return getDataType(column).getName();
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isReadOnly(int column) throws SQLException {
            return false;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isWritable(int column) throws SQLException {
            return true;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isDefinitelyWritable(int column) throws SQLException {
            return true;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isSigned(int column) throws SQLException {
            return false;
        }

        @Override // java.sql.ResultSetMetaData
        public final int getColumnDisplaySize(int column) throws SQLException {
            return 0;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isAutoIncrement(int column) throws SQLException {
            return false;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isCaseSensitive(int column) throws SQLException {
            return false;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isSearchable(int column) throws SQLException {
            return false;
        }

        @Override // java.sql.ResultSetMetaData
        public final boolean isCurrency(int column) throws SQLException {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Settings setParamType(SQLDialect dialect, Settings settings) {
        switch (dialect.family()) {
            case MYSQL:
            case MARIADB:
                return settings;
            default:
                return settings.withParamType(ParamType.NAMED).withRenderNamedParamPrefix(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX).withParseNamedParamPrefix(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$BlockingRecordSubscription.class */
    static final class BlockingRecordSubscription<R extends Record> extends AbstractSubscription<R> {
        private final ResultQueryTrait<R> query;
        private volatile Cursor<R> c;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BlockingRecordSubscription(ResultQueryTrait<R> query, Subscriber<? super R> subscriber) {
            super(subscriber);
            this.query = query;
        }

        /* JADX WARN: Code restructure failed: missing block: B:12:0x0029, code lost:            r3.subscriber.onComplete();        org.jooq.tools.jdbc.JDBCUtils.safeClose(r3.c);     */
        @Override // org.jooq.impl.R2DBC.AbstractSubscription
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        final synchronized void request0() {
            /*
                r3 = this;
                r0 = r3
                org.jooq.Cursor<R extends org.jooq.Record> r0 = r0.c     // Catch: java.lang.Throwable -> L4c
                if (r0 != 0) goto L14
                r0 = r3
                r1 = r3
                org.jooq.impl.ResultQueryTrait<R extends org.jooq.Record> r1 = r1.query     // Catch: java.lang.Throwable -> L4c
                org.jooq.Cursor r1 = r1.fetchLazyNonAutoClosing()     // Catch: java.lang.Throwable -> L4c
                r0.c = r1     // Catch: java.lang.Throwable -> L4c
            L14:
                r0 = r3
                boolean r0 = r0.moreRequested()     // Catch: java.lang.Throwable -> L4c
                if (r0 == 0) goto L49
                r0 = r3
                org.jooq.Cursor<R extends org.jooq.Record> r0 = r0.c     // Catch: java.lang.Throwable -> L4c
                org.jooq.Record r0 = r0.fetchNext()     // Catch: java.lang.Throwable -> L4c
                r4 = r0
                r0 = r4
                if (r0 != 0) goto L3c
                r0 = r3
                org.reactivestreams.Subscriber<? super T> r0 = r0.subscriber     // Catch: java.lang.Throwable -> L4c
                r0.onComplete()     // Catch: java.lang.Throwable -> L4c
                r0 = r3
                org.jooq.Cursor<R extends org.jooq.Record> r0 = r0.c     // Catch: java.lang.Throwable -> L4c
                org.jooq.tools.jdbc.JDBCUtils.safeClose(r0)     // Catch: java.lang.Throwable -> L4c
                goto L49
            L3c:
                r0 = r3
                org.reactivestreams.Subscriber<? super T> r0 = r0.subscriber     // Catch: java.lang.Throwable -> L4c
                r1 = r4
                r0.onNext(r1)     // Catch: java.lang.Throwable -> L4c
                goto L14
            L49:
                goto L5e
            L4c:
                r4 = move-exception
                r0 = r3
                org.reactivestreams.Subscriber<? super T> r0 = r0.subscriber
                r1 = r4
                r0.onError(r1)
                r0 = r3
                org.jooq.Cursor<R extends org.jooq.Record> r0 = r0.c
                org.jooq.tools.jdbc.JDBCUtils.safeClose(r0)
            L5e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.R2DBC.BlockingRecordSubscription.request0():void");
        }

        @Override // org.jooq.impl.R2DBC.AbstractSubscription
        final void cancel0(boolean closeAfterTransaction, Runnable onComplete) {
            JDBCUtils.safeClose(this.c);
            onComplete.run();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$BlockingRowCountSubscription.class */
    static final class BlockingRowCountSubscription extends AbstractSubscription<Integer> {
        final AbstractRowCountQuery query;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BlockingRowCountSubscription(AbstractRowCountQuery query, Subscriber<? super Integer> subscriber) {
            super(subscriber);
            this.query = query;
        }

        @Override // org.jooq.impl.R2DBC.AbstractSubscription
        final void request0() {
            try {
                if (this.query.isExecutable()) {
                    this.subscriber.onNext(Integer.valueOf(this.query.execute()));
                } else if (R2DBC.log.isDebugEnabled()) {
                    R2DBC.log.debug("Query is not executable", this.query);
                }
                this.subscriber.onComplete();
            } catch (Throwable t) {
                this.subscriber.onError(t);
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/R2DBC$BlockingTransactionSubscription.class */
    static final class BlockingTransactionSubscription<T> extends AbstractSubscription<T> {
        final DSLContext ctx;
        final TransactionalPublishable<T> transactional;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BlockingTransactionSubscription(DSLContext ctx, Subscriber<? super T> subscriber, TransactionalPublishable<T> transactional) {
            super(subscriber);
            this.ctx = ctx;
            this.transactional = transactional;
        }

        @Override // org.jooq.impl.R2DBC.AbstractSubscription
        final void request0() {
            try {
                this.subscriber.onNext((Object) this.ctx.transactionResult(c -> {
                    return R2DBC.block(this.transactional.run(c));
                }));
                this.subscriber.onComplete();
            } catch (Throwable th) {
                this.subscriber.onError(th);
            }
        }
    }

    static final boolean isR2dbc(java.sql.Statement statement) {
        return statement instanceof R2DBCPreparedStatement;
    }

    static final String sql0(Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return "Error while rendering SQL: " + t.getMessage();
        }
    }
}

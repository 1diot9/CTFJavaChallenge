package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Query;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.impl.R2DBC;
import org.reactivestreams.Subscriber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BatchMultiple.class */
public final class BatchMultiple extends AbstractBatch {
    final Query[] queries;

    public BatchMultiple(Configuration configuration, Query... queries) {
        super(configuration);
        this.queries = queries;
    }

    @Override // org.jooq.Batch
    public final int size() {
        return this.queries.length;
    }

    @Override // org.reactivestreams.Publisher
    public final void subscribe(Subscriber<? super Integer> subscriber) {
        ConnectionFactory cf = this.configuration.connectionFactory();
        if (!(cf instanceof NoConnectionFactory)) {
            subscriber.onSubscribe(new R2DBC.BatchSubscription(this, subscriber, s -> {
                return new R2DBC.BatchMultipleSubscriber(this, s);
            }));
            return;
        }
        throw new UnsupportedOperationException("The blocking, JDBC backed implementation of reactive batching has not yet been implemented. Use the R2DBC backed implementation, instead, or avoid batching.");
    }

    @Override // org.jooq.Batch
    public final int[] execute() {
        return Tools.chunks(Arrays.asList(this.queries), SettingsTools.getBatchSize(Tools.settings(this.configuration))).stream().map(chunk -> {
            return execute(Tools.configuration(this.configuration), (Query[]) chunk.toArray(Tools.EMPTY_QUERY));
        }).flatMapToInt(IntStream::of).toArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] execute(Configuration configuration, Query[] queries) {
        if (NO_SUPPORT_BATCH.contains(configuration.dialect())) {
            Stream of = Stream.of((Object[]) queries);
            DSLContext dsl = configuration.dsl();
            Objects.requireNonNull(dsl);
            return of.mapToInt(dsl::execute).toArray();
        }
        DefaultExecuteContext defaultExecuteContext = new DefaultExecuteContext(configuration, ExecuteContext.BatchMode.MULTIPLE, queries);
        ExecuteListener executeListener = ExecuteListeners.get(defaultExecuteContext);
        try {
            try {
                try {
                    executeListener.start(defaultExecuteContext);
                    defaultExecuteContext.transformQueries(executeListener);
                    if (defaultExecuteContext.statement() == null) {
                        defaultExecuteContext.statement(new SettingsEnabledPreparedStatement(AbstractQuery.connection(defaultExecuteContext)));
                    }
                    int t = SettingsTools.getQueryTimeout(0, defaultExecuteContext.settings());
                    if (t != 0) {
                        defaultExecuteContext.statement().setQueryTimeout(t);
                    }
                    String[] batchSQL = defaultExecuteContext.batchSQL();
                    for (int i = 0; i < defaultExecuteContext.batchQueries().length; i++) {
                        defaultExecuteContext.sql(null);
                        executeListener.renderStart(defaultExecuteContext);
                        batchSQL[i] = DSL.using(configuration).renderInlined(defaultExecuteContext.batchQueries()[i]);
                        defaultExecuteContext.sql(batchSQL[i]);
                        executeListener.renderEnd(defaultExecuteContext);
                    }
                    for (int i2 = 0; i2 < defaultExecuteContext.batchQueries().length; i2++) {
                        defaultExecuteContext.sql(batchSQL[i2]);
                        executeListener.prepareStart(defaultExecuteContext);
                        defaultExecuteContext.statement().addBatch(batchSQL[i2]);
                        executeListener.prepareEnd(defaultExecuteContext);
                    }
                    executeListener.executeStart(defaultExecuteContext);
                    int[] result = defaultExecuteContext.statement().executeBatch();
                    int[] batchRows = defaultExecuteContext.batchRows();
                    for (int i3 = 0; i3 < batchRows.length && i3 < result.length; i3++) {
                        batchRows[i3] = result[i3];
                    }
                    executeListener.executeEnd(defaultExecuteContext);
                    Tools.safeClose(executeListener, defaultExecuteContext);
                    return result;
                } catch (RuntimeException e) {
                    defaultExecuteContext.exception(e);
                    executeListener.exception(defaultExecuteContext);
                    throw defaultExecuteContext.exception();
                } catch (SQLException e2) {
                    defaultExecuteContext.sqlException(e2);
                    executeListener.exception(defaultExecuteContext);
                    throw defaultExecuteContext.exception();
                }
            } catch (ControlFlowSignal e3) {
                throw e3;
            }
        } catch (Throwable th) {
            Tools.safeClose(executeListener, defaultExecuteContext);
            throw th;
        }
    }

    public String toString() {
        return this.dsl.queries(this.queries).toString();
    }
}

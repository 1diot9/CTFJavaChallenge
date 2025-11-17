package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import org.jooq.Batch;
import org.jooq.BatchBindStep;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.impl.R2DBC;
import org.jooq.tools.JooqLogger;
import org.reactivestreams.Subscriber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BatchSingle.class */
public final class BatchSingle extends AbstractBatch implements BatchBindStep {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) BatchSingle.class);
    final Query query;
    final Map<String, List<Integer>> nameToIndexMapping;
    final List<Object[]> allBindValues;
    final int expectedBindValues;

    @Override // org.jooq.BatchBindStep
    @SafeVarargs
    public /* bridge */ /* synthetic */ BatchBindStep bind(Map[] mapArr) {
        return bind((Map<String, Object>[]) mapArr);
    }

    @Override // org.jooq.BatchBindStep
    public /* bridge */ /* synthetic */ BatchBindStep bind(Map map) {
        return bind((Map<String, Object>) map);
    }

    public BatchSingle(Configuration configuration, Query query) {
        super(configuration);
        int i = 0;
        ParamCollector collector = new ParamCollector(configuration, false);
        collector.visit(query);
        this.query = query;
        this.allBindValues = new ArrayList();
        this.nameToIndexMapping = new LinkedHashMap();
        this.expectedBindValues = collector.resultList.size();
        for (Map.Entry<String, Param<?>> entry : collector.resultList) {
            int i2 = i;
            i++;
            this.nameToIndexMapping.computeIfAbsent(entry.getKey(), e -> {
                return new ArrayList();
            }).add(Integer.valueOf(i2));
        }
    }

    @Override // org.jooq.BatchBindStep
    public final BatchSingle bind(Object... bindValues) {
        this.allBindValues.add(bindValues);
        return this;
    }

    @Override // org.jooq.BatchBindStep
    public final BatchSingle bind(Object[]... bindValues) {
        for (Object[] v : bindValues) {
            bind(v);
        }
        return this;
    }

    @Override // org.jooq.BatchBindStep
    public final BatchSingle bind(Map<String, Object> namedBindValues) {
        return bind(namedBindValues);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.Object[], java.lang.Object[][]] */
    @Override // org.jooq.BatchBindStep
    @SafeVarargs
    public final BatchSingle bind(Map<String, Object>... namedBindValues) {
        List<Object> defaultValues = this.dsl.extractBindValues(this.query);
        ?? r0 = new Object[namedBindValues.length];
        for (int i = 0; i < r0.length; i++) {
            Object[] row = defaultValues.toArray();
            r0[i] = row;
            namedBindValues[i].forEach((k, v) -> {
                List<Integer> indexes = this.nameToIndexMapping.get(k);
                if (indexes != null) {
                    Iterator<Integer> it = indexes.iterator();
                    while (it.hasNext()) {
                        int index = it.next().intValue();
                        row[index] = v;
                    }
                }
            });
        }
        bind((Object[][]) r0);
        return this;
    }

    @Override // org.jooq.Batch
    public final int size() {
        return this.allBindValues.size();
    }

    @Override // org.reactivestreams.Publisher
    public final void subscribe(Subscriber<? super Integer> subscriber) {
        ConnectionFactory cf = this.configuration.connectionFactory();
        if (!(cf instanceof NoConnectionFactory)) {
            subscriber.onSubscribe(new R2DBC.BatchSubscription(this, subscriber, s -> {
                return new R2DBC.BatchSingleSubscriber(this, s);
            }));
            return;
        }
        throw new UnsupportedOperationException("The blocking, JDBC backed implementation of reactive batching has not yet been implemented. Use the R2DBC backed implementation, instead, or avoid batching.");
    }

    @Override // org.jooq.Batch
    public final int[] execute() {
        if (this.allBindValues.isEmpty()) {
            log.info("Single batch", "No bind variables have been provided with a single statement batch execution. This may be due to accidental API misuse");
            return BatchMultiple.execute(this.configuration, new Query[]{this.query});
        }
        checkBindValues();
        if (SettingsTools.executeStaticStatements(this.configuration.settings())) {
            return executeStatic();
        }
        return executePrepared();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void checkBindValues() {
        if (this.expectedBindValues > 0) {
            for (int i = 0; i < this.allBindValues.size(); i++) {
                if (this.allBindValues.get(i).length != this.expectedBindValues) {
                    log.info("Bind value count", "Batch bind value set " + i + " has " + this.allBindValues.get(i).length + " values when " + this.expectedBindValues + " values were expected");
                }
            }
        }
    }

    private final int[] executePrepared() {
        DefaultExecuteContext defaultExecuteContext = new DefaultExecuteContext(this.configuration, ExecuteContext.BatchMode.SINGLE, new Query[]{this.query});
        ExecuteListener executeListener = ExecuteListeners.get(defaultExecuteContext);
        Param<?>[] params = extractParams();
        try {
            try {
                try {
                    executeListener.start(defaultExecuteContext);
                    defaultExecuteContext.transformQueries(executeListener);
                    executeListener.renderStart(defaultExecuteContext);
                    defaultExecuteContext.sql(this.dsl.render(this.query));
                    executeListener.renderEnd(defaultExecuteContext);
                    executeListener.prepareStart(defaultExecuteContext);
                    if (defaultExecuteContext.statement() == null) {
                        defaultExecuteContext.statement(AbstractQuery.connection(defaultExecuteContext).prepareStatement(defaultExecuteContext.sql()));
                    }
                    executeListener.prepareEnd(defaultExecuteContext);
                    int t = SettingsTools.getQueryTimeout(0, defaultExecuteContext.settings());
                    if (t != 0) {
                        defaultExecuteContext.statement().setQueryTimeout(t);
                    }
                    if (NO_SUPPORT_BATCH.contains(defaultExecuteContext.dialect())) {
                        int size = this.allBindValues.size();
                        int[] result = new int[size];
                        for (int i = 0; i < size; i++) {
                            Object[] bindValues = this.allBindValues.get(i);
                            setBindValues(defaultExecuteContext, executeListener, params, bindValues);
                            executeListener.executeStart(defaultExecuteContext);
                            result[i] = defaultExecuteContext.statement().executeUpdate();
                            executeListener.executeEnd(defaultExecuteContext);
                        }
                        setBatchRows(defaultExecuteContext, result);
                        Tools.safeClose(executeListener, defaultExecuteContext);
                        return result;
                    }
                    AtomicBoolean reset = new AtomicBoolean();
                    int[] array = Tools.chunks(this.allBindValues, SettingsTools.getBatchSize(defaultExecuteContext.settings())).stream().map(Tools.checkedFunction(chunk -> {
                        if (reset.get()) {
                            defaultExecuteContext.statement().clearBatch();
                        }
                        Iterator it = chunk.iterator();
                        while (it.hasNext()) {
                            Object[] bindValues2 = (Object[]) it.next();
                            setBindValues(defaultExecuteContext, executeListener, params, bindValues2);
                            defaultExecuteContext.statement().addBatch();
                        }
                        executeListener.executeStart(defaultExecuteContext);
                        int[] result2 = defaultExecuteContext.statement().executeBatch();
                        setBatchRows(defaultExecuteContext, result2);
                        executeListener.executeEnd(defaultExecuteContext);
                        reset.set(true);
                        return result2;
                    })).flatMapToInt(IntStream::of).toArray();
                    Tools.safeClose(executeListener, defaultExecuteContext);
                    return array;
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

    private final void setBindValues(DefaultExecuteContext ctx, ExecuteListener listener, Param<?>[] params, Object[] bindValues) {
        List<Field<?>> fields;
        listener.bindStart(ctx);
        DefaultBindContext defaultBindContext = new DefaultBindContext(this.configuration, ctx, ctx.statement());
        if (params.length > 0) {
            fields = Tools.fields(bindValues, params);
        } else {
            fields = Tools.fields(bindValues);
        }
        Tools.visitAll(defaultBindContext, fields);
        listener.bindEnd(ctx);
    }

    private final void setBatchRows(DefaultExecuteContext ctx, int[] result) {
        int[] batchRows = ctx.batchRows();
        for (int i = 0; i < batchRows.length && i < result.length; i++) {
            batchRows[i] = result[i];
        }
    }

    final Param<?>[] extractParams() {
        ParamCollector collector = new ParamCollector(this.configuration, false);
        collector.visit(this.query);
        return (Param[]) Tools.map(collector.resultList, e -> {
            return (Param) e.getValue();
        }, x$0 -> {
            return new Param[x$0];
        });
    }

    private final int[] executeStatic() {
        return batchMultiple().execute();
    }

    private final Batch batchMultiple() {
        List<Query> queries = new ArrayList<>(this.allBindValues.size());
        for (Object[] bindValues : this.allBindValues) {
            for (int i = 0; i < bindValues.length; i++) {
                this.query.bind(i + 1, bindValues[i]);
            }
            queries.add(this.dsl.query(this.query.getSQL(ParamType.INLINED)));
        }
        return this.dsl.batch(queries);
    }

    public String toString() {
        return batchMultiple().toString();
    }
}

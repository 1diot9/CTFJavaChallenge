package org.jooq.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jooq.BatchBindStep;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;
import org.reactivestreams.Subscriber;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BatchCRUD.class */
final class BatchCRUD extends AbstractBatch {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) BatchCRUD.class);
    private final TableRecord<?>[] records;
    private final Action action;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BatchCRUD$Action.class */
    public enum Action {
        STORE,
        INSERT,
        UPDATE,
        MERGE,
        DELETE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BatchCRUD(Configuration configuration, Action action, TableRecord<?>[] records) {
        super(configuration);
        this.action = action;
        this.records = records;
    }

    @Override // org.jooq.Batch
    public final int size() {
        return this.records.length;
    }

    @Override // org.reactivestreams.Publisher
    public void subscribe(Subscriber<? super Integer> s) {
        throw new UnsupportedOperationException("BatchCRUD operations are not yet supported in a reactive way. Use ordinary batch operations, instead, or avoid batching. See https://github.com/jOOQ/jOOQ/issues/14874");
    }

    @Override // org.jooq.Batch
    public final int[] execute() throws DataAccessException {
        if (SettingsTools.executeStaticStatements(this.configuration.settings())) {
            return executeStatic();
        }
        return executePrepared();
    }

    private final Configuration deriveConfiguration(QueryCollector collector) {
        Configuration local = this.configuration.deriveAppending(collector);
        local.settings().withExecuteLogging(false).withReturnAllOnUpdatableRecord(false).withReturnDefaultOnUpdatableRecord(false).withReturnComputedOnUpdatableRecord(false).withReturnIdentityOnUpdatableRecord(false);
        return local;
    }

    private final int[] executePrepared() {
        boolean optimisticLocking = Boolean.TRUE.equals(this.configuration.settings().isExecuteWithOptimisticLocking());
        Map<String, List<Query>> queries = new LinkedHashMap<>();
        List<QueryCollectorSignal> signals = new ArrayList<>();
        QueryCollector collector = new QueryCollector();
        Configuration local = deriveConfiguration(collector);
        for (int i = 0; i < this.records.length; i++) {
            Configuration previous = this.records[i].configuration();
            try {
                try {
                    this.records[i].attach(local);
                    executeAction(i);
                    if (optimisticLocking) {
                        signals.add(null);
                    }
                    this.records[i].attach(previous);
                } catch (QueryCollectorSignal e) {
                    if (optimisticLocking) {
                        signals.add(e);
                    }
                    Query query = e.getQuery();
                    String sql = e.getSQL();
                    if (query.isExecutable()) {
                        queries.computeIfAbsent(sql, s -> {
                            return new ArrayList();
                        }).add(query);
                    }
                    this.records[i].attach(previous);
                }
            } catch (Throwable th) {
                this.records[i].attach(previous);
                throw th;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Batch " + String.valueOf(this.action) + " of " + this.records.length + " records using " + queries.size() + " distinct queries (lower is better) with an average number of bind variable sets of " + queries.values().stream().mapToInt((v0) -> {
                return v0.size();
            }).average().orElse(0.0d) + " (higher is better)");
        }
        List<Integer> result = new ArrayList<>();
        queries.forEach((k, v) -> {
            BatchBindStep batch = this.dsl.batch((Query) v.get(0));
            Iterator it = v.iterator();
            while (it.hasNext()) {
                Query query2 = (Query) it.next();
                batch.bind(query2.getBindValues().toArray());
            }
            int[] array = batch.execute();
            for (int i2 : array) {
                result.add(Integer.valueOf(i2));
            }
        });
        int[] array = new int[result.size()];
        for (int i2 = 0; i2 < result.size(); i2++) {
            array[i2] = result.get(i2).intValue();
        }
        if (optimisticLocking) {
            updateRecordVersionsAndTimestamps(signals, array);
        }
        updateChangedFlag();
        return array;
    }

    private final int[] executeStatic() {
        boolean optimisticLocking = Boolean.TRUE.equals(this.configuration.settings().isExecuteWithOptimisticLocking());
        List<Query> queries = new ArrayList<>();
        List<QueryCollectorSignal> signals = new ArrayList<>();
        QueryCollector collector = new QueryCollector();
        Configuration local = deriveConfiguration(collector);
        for (int i = 0; i < this.records.length; i++) {
            Configuration previous = this.records[i].configuration();
            try {
                try {
                    this.records[i].attach(local);
                    executeAction(i);
                    if (optimisticLocking) {
                        signals.add(null);
                    }
                    this.records[i].attach(previous);
                } catch (QueryCollectorSignal e) {
                    if (optimisticLocking) {
                        signals.add(e);
                    }
                    Query query = e.getQuery();
                    if (query.isExecutable()) {
                        queries.add(query);
                    }
                    this.records[i].attach(previous);
                }
            } catch (Throwable th) {
                this.records[i].attach(previous);
                throw th;
            }
        }
        int[] result = this.dsl.batch(queries).execute();
        if (optimisticLocking) {
            updateRecordVersionsAndTimestamps(signals, result);
        }
        updateChangedFlag();
        return result;
    }

    private final void updateRecordVersionsAndTimestamps(List<QueryCollectorSignal> signals, int[] array) {
        for (int i = 0; i < this.records.length && i < array.length; i++) {
            QueryCollectorSignal signal = signals.get(i);
            if (signal != null && array[i] > 0) {
                ((TableRecordImpl) this.records[i]).setRecordVersionAndTimestamp(signal.version, signal.timestamp);
            }
        }
    }

    private final void executeAction(int i) {
        switch (this.action) {
            case STORE:
                ((UpdatableRecord) this.records[i]).store();
                return;
            case INSERT:
                this.records[i].insert();
                return;
            case UPDATE:
                ((UpdatableRecord) this.records[i]).update();
                return;
            case MERGE:
                ((UpdatableRecord) this.records[i]).merge();
                return;
            case DELETE:
                ((UpdatableRecord) this.records[i]).delete();
                return;
            default:
                return;
        }
    }

    private final void updateChangedFlag() {
        for (Record record : this.records) {
            record.changed(this.action == Action.DELETE);
            if (record instanceof AbstractRecord) {
                AbstractRecord r = (AbstractRecord) record;
                r.fetched = this.action != Action.DELETE;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BatchCRUD$QueryCollector.class */
    public static class QueryCollector implements ExecuteListener {
        private QueryCollector() {
        }

        @Override // org.jooq.ExecuteListener
        public void renderEnd(ExecuteContext ctx) {
            throw new QueryCollectorSignal(ctx.sql(), ctx.query());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BatchCRUD$QueryCollectorSignal.class */
    public static class QueryCollectorSignal extends ControlFlowSignal {
        final String sql;
        final Query query;
        BigInteger version;
        Timestamp timestamp;

        QueryCollectorSignal(String sql, Query query) {
            this.sql = sql;
            this.query = query;
        }

        String getSQL() {
            return this.sql;
        }

        Query getQuery() {
            return this.query;
        }
    }
}

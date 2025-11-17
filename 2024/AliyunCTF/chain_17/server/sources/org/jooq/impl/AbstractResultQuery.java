package org.jooq.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.jooq.CloseableResultQuery;
import org.jooq.Configuration;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.SettingsTools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.MockResultSet;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractResultQuery.class */
public abstract class AbstractResultQuery<R extends Record> extends AbstractQuery<R> implements ResultQueryTrait<R> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) AbstractResultQuery.class);
    private static final Set<SQLDialect> REPORT_FETCH_SIZE_WITH_AUTOCOMMIT = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private int maxRows;
    private int fetchSize;
    private int resultSetConcurrency;
    private int resultSetType;
    private int resultSetHoldability;
    private Table<?> coerceTable;
    private Collection<? extends Field<?>> coerceFields;
    private transient boolean lazy;
    private transient boolean many;
    private transient Cursor<R> cursor;
    private transient boolean autoclosing;
    private Result<R> result;
    private ResultsImpl results;
    private final Intern intern;

    abstract Class<? extends R> getRecordType0();

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public /* bridge */ /* synthetic */ ResultQuery coerce(Collection collection) {
        return coerce((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public /* bridge */ /* synthetic */ ResultQuery intern(Field[] fieldArr) {
        return intern((Field<?>[]) fieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractResultQuery(Configuration configuration) {
        super(configuration);
        this.autoclosing = true;
        this.intern = new Intern();
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.CloseableQuery, org.jooq.Query
    public final CloseableResultQuery<R> bind(String param, Object value) {
        return (CloseableResultQuery) super.bind(param, value);
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.CloseableQuery, org.jooq.Query
    public final CloseableResultQuery<R> bind(int index, Object value) {
        return (CloseableResultQuery) super.bind(index, value);
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.CloseableQuery, org.jooq.Query
    public final CloseableResultQuery<R> poolable(boolean poolable) {
        return (CloseableResultQuery) super.poolable(poolable);
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.CloseableQuery, org.jooq.Query
    public final CloseableResultQuery<R> queryTimeout(int timeout) {
        return (CloseableResultQuery) super.queryTimeout(timeout);
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.CloseableQuery, org.jooq.Query
    public final CloseableResultQuery<R> keepStatement(boolean k) {
        return (CloseableResultQuery) super.keepStatement(k);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> maxRows(int rows) {
        this.maxRows = rows;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> fetchSize(int rows) {
        this.fetchSize = rows;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int fetchSize() {
        return this.fetchSize;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetConcurrency(int concurrency) {
        this.resultSetConcurrency = concurrency;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetType(int type) {
        this.resultSetType = type;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetHoldability(int holdability) {
        this.resultSetHoldability = holdability;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(Field<?>... fields) {
        this.intern.internFields = fields;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(int... fieldIndexes) {
        this.intern.internIndexes = fieldIndexes;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(String... fieldNameStrings) {
        this.intern.internNameStrings = fieldNameStrings;
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(Name... fieldNames) {
        this.intern.internNames = fieldNames;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.AbstractQuery
    public final void prepare(ExecuteContext ctx) throws SQLException {
        if (ctx.statement() == null) {
            if (this.resultSetConcurrency != 0 || this.resultSetType != 0 || this.resultSetHoldability != 0) {
                int type = this.resultSetType != 0 ? this.resultSetType : 1003;
                int concurrency = this.resultSetConcurrency != 0 ? this.resultSetConcurrency : 1007;
                if (this.resultSetHoldability == 0) {
                    ctx.statement(ctx.connection().prepareStatement(ctx.sql(), type, concurrency));
                } else {
                    ctx.statement(ctx.connection().prepareStatement(ctx.sql(), type, concurrency, this.resultSetHoldability));
                }
            } else {
                ctx.statement(ctx.connection().prepareStatement(ctx.sql()));
            }
        }
        Tools.setFetchSize(ctx, this.fetchSize);
        int m = SettingsTools.getMaxRows(this.maxRows, ctx.settings());
        if (m != 0) {
            ctx.statement().setMaxRows(m);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.AbstractQuery
    public final int execute(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);
        int f = SettingsTools.getFetchSize(this.fetchSize, ctx.settings());
        if (REPORT_FETCH_SIZE_WITH_AUTOCOMMIT.contains(ctx.dialect()) && f != 0 && ctx.connection().getAutoCommit()) {
            log.info("Fetch Size", "A fetch size of " + f + " was set on a auto-commit PostgreSQL connection, which is not recommended. See https://jdbc.postgresql.org/documentation/query/#getting-results-based-on-a-cursor");
        }
        SQLException e = Tools.executeStatementAndGetFirstResultSet(ctx, this.rendered.skipUpdateCounts);
        listener.executeEnd(ctx);
        if (!this.many) {
            if (e == null) {
                if (ctx.resultSet() == null) {
                    DSLContext dsl = DSL.using(ctx.configuration());
                    Field<Integer> c = DSL.field(DSL.name("UPDATE_COUNT"), Integer.TYPE);
                    Result<Record1<Integer>> r = dsl.newResult(c);
                    r.add(dsl.newRecord(c).values(Integer.valueOf(ctx.rows())));
                    ctx.resultSet(new MockResultSet(r));
                }
                Field<?>[] fields = getFields(() -> {
                    return ctx.resultSet().getMetaData();
                });
                this.cursor = new CursorImpl(ctx, listener, fields, this.intern.internIndexes(fields), keepStatement(), keepResultSet(), getRecordType(), SettingsTools.getMaxRows(this.maxRows, ctx.settings()), this.autoclosing);
                if (!this.lazy) {
                    this.result = this.cursor.fetch();
                    this.cursor = null;
                }
            }
        } else {
            this.results = new ResultsImpl(ctx.configuration());
            Tools.consumeResultSets(ctx, listener, this.results, this.intern, e);
        }
        if (this.result != null) {
            return this.result.size();
        }
        return 0;
    }

    @Override // org.jooq.impl.AbstractQuery
    protected final boolean keepResultSet() {
        return this.lazy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Collection<? extends Field<?>> coerce() {
        return this.coerceFields;
    }

    @Override // org.jooq.ResultQuery
    public final Result<R> fetch() {
        execute();
        return this.result;
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.jooq.ResultQuery
    public final Cursor<R> fetchLazy() {
        this.lazy = true;
        try {
            execute();
            return this.cursor;
        } finally {
            this.lazy = false;
        }
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Cursor<R> fetchLazyNonAutoClosing() {
        boolean previousAutoClosing = this.autoclosing;
        this.autoclosing = false;
        try {
            return fetchLazy();
        } finally {
            this.autoclosing = previousAutoClosing;
        }
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.jooq.ResultQuery
    public final Results fetchMany() {
        this.many = true;
        try {
            execute();
            return this.results;
        } finally {
            this.many = false;
        }
    }

    @Override // org.jooq.ResultQuery
    public final Class<? extends R> getRecordType() {
        if (this.coerceTable != null) {
            return (Class<? extends R>) this.coerceTable.getRecordType();
        }
        return getRecordType0();
    }

    @Override // org.jooq.ResultQuery
    public final Result<R> getResult() {
        return this.result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final <X extends Record> CloseableResultQuery<X> coerce(Table<X> table) {
        this.coerceTable = table;
        return (CloseableResultQuery<X>) coerce((Collection<? extends Field<?>>) Arrays.asList(table.fields()));
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<Record> coerce(Collection<? extends Field<?>> fields) {
        this.coerceFields = fields;
        return this;
    }
}

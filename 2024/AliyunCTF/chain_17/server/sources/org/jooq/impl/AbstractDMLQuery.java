package org.jooq.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jooq.Asterisk;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DMLQuery;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Delete;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Insert;
import org.jooq.QualifiedAsterisk;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;
import org.jooq.Update;
import org.jooq.conf.ExecuteWithoutWhere;
import org.jooq.conf.FetchTriggerValuesAfterReturning;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.util.sqlite.SQLiteDSL;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDMLQuery.class */
public abstract class AbstractDMLQuery<R extends Record> extends AbstractRowCountQuery implements DMLQuery<R> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) AbstractQuery.class);
    private static final Set<SQLDialect> NO_NATIVE_SUPPORT_INSERT_RETURNING = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MYSQL, SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_NATIVE_SUPPORT_UPDATE_RETURNING = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MYSQL, SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_NATIVE_SUPPORT_DELETE_RETURNING = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MYSQL, SQLDialect.TRINO);
    private static final Set<SQLDialect> NATIVE_SUPPORT_DATA_CHANGE_DELTA_TABLE = SQLDialect.supportedBy(SQLDialect.H2);
    private static final Set<SQLDialect> NO_SUPPORT_FETCHING_KEYS = SQLDialect.supportedBy(SQLDialect.IGNITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_SUPPORT_RETURNING_ASTERISK = SQLDialect.supportedUntil(SQLDialect.MARIADB);
    final WithImpl with;
    final Table<R> table;
    final SelectFieldList<SelectFieldOrAsterisk> returning;
    final List<Field<?>> returningResolvedAsterisks;
    Result<Record> returnedResult;
    Result<R> returned;

    abstract void accept1(Context<?> context);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDMLQuery(Configuration configuration, WithImpl with, Table<R> table) {
        super(configuration);
        this.with = with;
        this.table = table;
        this.returning = new SelectFieldList<>();
        this.returningResolvedAsterisks = new ArrayList();
    }

    public final void setReturning() {
        setReturning(this.table.fields());
    }

    public final void setReturning(Identity<R, ?> identity) {
        if (identity != null) {
            setReturning(identity.getField());
        }
    }

    public final void setReturning(SelectFieldOrAsterisk... fields) {
        setReturning(Arrays.asList(fields));
    }

    public final void setReturning(Collection<? extends SelectFieldOrAsterisk> fields) {
        this.returning.clear();
        this.returning.addAll(fields.isEmpty() ? Arrays.asList(this.table.fields()) : fields);
        this.returningResolvedAsterisks.clear();
        Iterator<T> it = this.returning.iterator();
        while (it.hasNext()) {
            SelectFieldOrAsterisk s = (SelectFieldOrAsterisk) it.next();
            if (s instanceof Field) {
                Field<?> f = (Field) s;
                this.returningResolvedAsterisks.add(f);
            } else if (s instanceof QualifiedAsterisk) {
                QualifiedAsterisk a = (QualifiedAsterisk) s;
                this.returningResolvedAsterisks.addAll(Arrays.asList(a.qualifier().fields()));
            } else if (s instanceof Asterisk) {
                this.returningResolvedAsterisks.addAll(Arrays.asList(this.table.fields()));
            } else if (s instanceof Row) {
                Row r = (Row) s;
                this.returningResolvedAsterisks.add(new RowAsField(r));
            } else if (s instanceof Table) {
                Table<?> t = (Table) s;
                this.returningResolvedAsterisks.add(new TableAsField(t));
            } else {
                throw new UnsupportedOperationException("Type not supported: " + String.valueOf(s));
            }
        }
    }

    public final R getReturnedRecord() {
        if (getReturnedRecords().isEmpty()) {
            return null;
        }
        return (R) getReturnedRecords().get(0);
    }

    public final Result<R> getReturnedRecords() {
        if (this.returned == null) {
            if (this.table.fields().length > 0) {
                warnOnAPIMisuse();
                this.returned = (Result<R>) getResult().into(this.table);
            } else {
                this.returned = (Result<R>) getResult();
            }
        }
        return this.returned;
    }

    private final void warnOnAPIMisuse() {
        for (Field<?> field : getResult().fields()) {
            if (this.table.field(field) == null) {
                log.warn("API misuse", "Column " + String.valueOf(field) + " has been requested through the returning() clause, which is not present in table " + String.valueOf(this.table) + ". Use StoreQuery.getResult() or the returningResult() clause instead.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<R> table() {
        return this.table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<?> table(Scope scope) {
        return table();
    }

    public final Result<?> getResult() {
        if (this.returnedResult == null) {
            this.returnedResult = new ResultImpl(configuration(), this.returningResolvedAsterisks);
        }
        return this.returnedResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void accept0(Context<?> ctx) {
        QOM.UnmodifiableList<? extends Table<?>> unmodifiableList;
        WithImpl w = this.with;
        ctx.data(Tools.SimpleDataKey.DATA_DML_TARGET_TABLE, this.table);
        Tools.SimpleDataKey simpleDataKey = Tools.SimpleDataKey.DATA_DML_USING_TABLES;
        if (this instanceof DeleteQueryImpl) {
            DeleteQueryImpl<?> d = (DeleteQueryImpl) this;
            unmodifiableList = d.$using();
        } else {
            unmodifiableList = null;
        }
        ctx.data(simpleDataKey, unmodifiableList);
        if (w != null) {
            ctx.visit(w);
        } else {
            CommonTableExpressionList.markTopLevelCteAndAccept(ctx, c -> {
            });
        }
        ctx.declareFields();
        if (NATIVE_SUPPORT_DATA_CHANGE_DELTA_TABLE.contains(ctx.dialect()) && !this.returning.isEmpty() && ctx.data(Tools.SimpleDataKey.DATA_RENDERING_DATA_CHANGE_DELTA_TABLE) == null) {
            Tools.increment(ctx.data(), Tools.SimpleDataKey.DATA_RENDERING_DATA_CHANGE_DELTA_TABLE, () -> {
                ctx.visit(DSL.select(this.returning).from(new DataChangeDeltaTable(this instanceof Delete ? QOM.ResultOption.OLD : QOM.ResultOption.FINAL, this).as(table().getUnqualifiedName())));
            });
        } else {
            accept1(ctx);
        }
        ctx.data().remove(Tools.SimpleDataKey.DATA_DML_USING_TABLES);
        ctx.data().remove(Tools.SimpleDataKey.DATA_DML_TARGET_TABLE);
    }

    private final boolean fetchTriggerValuesAfterReturning(Scope ctx) {
        if ((this instanceof Delete) || Boolean.FALSE.equals(ctx.settings().isFetchTriggerValuesAfterSQLServerOutput())) {
            return false;
        }
        switch ((FetchTriggerValuesAfterReturning) StringUtils.defaultIfNull(ctx.settings().getFetchTriggerValuesAfterReturning(), FetchTriggerValuesAfterReturning.WHEN_NEEDED)) {
            case ALWAYS:
                return true;
            case NEVER:
                return false;
            case WHEN_NEEDED:
                if (ctx.configuration().commercial()) {
                }
                return true;
            default:
                throw new IllegalStateException("Unsupported value: " + String.valueOf(ctx.settings().getFetchTriggerValuesAfterReturning()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void executeWithoutWhere(String message, ExecuteWithoutWhere executeWithoutWhere) {
        switch (executeWithoutWhere) {
            case IGNORE:
            default:
                return;
            case LOG_DEBUG:
                if (log.isDebugEnabled()) {
                    log.debug(message, "A statement is executed without WHERE clause");
                    return;
                }
                return;
            case LOG_INFO:
                if (log.isInfoEnabled()) {
                    log.info(message, "A statement is executed without WHERE clause");
                    return;
                }
                return;
            case LOG_WARN:
                log.warn(message, "A statement is executed without WHERE clause");
                return;
            case THROW:
                throw new DataAccessException("A statement is executed without WHERE clause");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    public final void toSQLReturning(Context<?> ctx) {
        if (!this.returning.isEmpty() && nativeSupportReturning(ctx)) {
            boolean qualify = ctx.qualify();
            boolean unqualify = ctx.family() == SQLDialect.MARIADB;
            if (unqualify) {
                ctx.qualify(false);
            }
            ctx.formatSeparator().visit(Keywords.K_RETURNING).sql(' ').declareFields(true, c -> {
                SelectFieldList<SelectFieldOrAsterisk> selectFieldList;
                if (NO_SUPPORT_RETURNING_ASTERISK.contains(c.dialect())) {
                    selectFieldList = new SelectFieldList<>(this.returningResolvedAsterisks);
                } else {
                    selectFieldList = this.returning;
                }
                c.visit(selectFieldList);
            });
            if (unqualify) {
                ctx.qualify(qualify);
            }
        }
    }

    final boolean nativeSupportReturning(Scope ctx) {
        return !(ctx.family() == SQLDialect.SQLITE && (this instanceof Insert) && fetchTriggerValuesAfterReturning(ctx)) && (((this instanceof Insert) && !NO_NATIVE_SUPPORT_INSERT_RETURNING.contains(ctx.dialect())) || (((this instanceof Update) && !NO_NATIVE_SUPPORT_UPDATE_RETURNING.contains(ctx.dialect())) || ((this instanceof Delete) && !NO_NATIVE_SUPPORT_DELETE_RETURNING.contains(ctx.dialect()))));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean nativeSupportReturningOrDataChangeDeltaTable(Scope ctx) {
        return NATIVE_SUPPORT_DATA_CHANGE_DELTA_TABLE.contains(ctx.dialect()) || nativeSupportReturning(ctx);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.AbstractQuery
    public final void prepare(ExecuteContext ctx) throws SQLException {
        prepare0(ctx);
        Tools.setFetchSize(ctx, 0);
    }

    private final void prepare0(ExecuteContext ctx) throws SQLException {
        ThrowingFunction throwingFunction;
        Connection connection = ctx.connection();
        if (this.returning.isEmpty()) {
            super.prepare(ctx);
            return;
        }
        if (NO_SUPPORT_FETCHING_KEYS.contains(ctx.dialect())) {
            super.prepare(ctx);
            return;
        }
        if (nativeSupportReturningOrDataChangeDeltaTable(ctx)) {
            super.prepare(ctx);
            return;
        }
        switch (ctx.family()) {
            case SQLITE:
            case CUBRID:
                super.prepare(ctx);
                return;
            case DERBY:
            case MARIADB:
            case MYSQL:
                if (ctx.statement() == null) {
                    ctx.statement(connection.prepareStatement(ctx.sql(), 1));
                    return;
                }
                return;
            case HSQLDB:
            default:
                if (ctx.statement() == null) {
                    RenderNameCase style = SettingsTools.getRenderNameCase(configuration().settings());
                    String sql = ctx.sql();
                    Iterable<Field<?>> flattenCollection = Tools.flattenCollection(this.returningResolvedAsterisks, false, true);
                    if (style == RenderNameCase.UPPER) {
                        throwingFunction = f -> {
                            return f.getName().toUpperCase(SettingsTools.renderLocale(configuration().settings()));
                        };
                    } else if (style == RenderNameCase.LOWER) {
                        throwingFunction = f2 -> {
                            return f2.getName().toLowerCase(SettingsTools.renderLocale(configuration().settings()));
                        };
                    } else {
                        throwingFunction = f3 -> {
                            return f3.getName();
                        };
                    }
                    ctx.statement(connection.prepareStatement(sql, (String[]) Tools.map(flattenCollection, throwingFunction).toArray(Tools.EMPTY_STRING)));
                    return;
                }
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.AbstractQuery
    public final int execute(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        this.returned = null;
        this.returnedResult = null;
        if (this.returning.isEmpty()) {
            return super.execute(ctx, listener);
        }
        if (NO_SUPPORT_FETCHING_KEYS.contains(ctx.dialect())) {
            return super.execute(ctx, listener);
        }
        int result = 0;
        switch (ctx.family()) {
            case SQLITE:
                if (!nativeSupportReturning(ctx)) {
                    listener.executeStart(ctx);
                    int result2 = executeImmediate(ctx.statement()).executeUpdate();
                    ctx.rows(result2);
                    listener.executeEnd(ctx);
                    DSLContext create = ctx.dsl();
                    this.returnedResult = create.select(this.returning).from(this.table).where(SQLiteDSL.rowid().eq(DSL.field("last_insert_rowid()", SQLiteDSL.rowid().getDataType()))).fetch();
                    this.returnedResult.attach(((DefaultExecuteContext) ctx).originalConfiguration());
                    return result2;
                }
                executeReturningQuery(ctx, listener);
                break;
            case CUBRID:
                listener.executeStart(ctx);
                int result3 = executeImmediate(ctx.statement()).executeUpdate();
                ctx.rows(result3);
                listener.executeEnd(ctx);
                selectReturning(((DefaultExecuteContext) ctx).originalConfiguration(), ctx.configuration(), ctx.dsl().lastID());
                return result3;
            case DERBY:
            case MYSQL:
                return executeReturningGeneratedKeysFetchAdditionalRows(ctx, listener);
            case MARIADB:
                if (!nativeSupportReturning(ctx)) {
                    return executeReturningGeneratedKeysFetchAdditionalRows(ctx, listener);
                }
                executeReturningQuery(ctx, listener);
                break;
            case HSQLDB:
            default:
                result = executeReturningGeneratedKeys(ctx, listener);
                break;
            case H2:
                executeReturningQuery(ctx, listener);
                break;
            case DUCKDB:
            case FIREBIRD:
            case POSTGRES:
            case YUGABYTEDB:
                executeReturningQuery(ctx, listener);
                break;
        }
        this.returnedResult = new CursorImpl(ctx, listener, (Field[]) this.returningResolvedAsterisks.toArray(Tools.EMPTY_FIELD), null, false, false).fetch();
        if (!this.returnedResult.isEmpty() || ctx.family() != SQLDialect.HSQLDB) {
            result = Math.max(Math.max(result, ctx.rows()), this.returnedResult.size());
            ctx.rows(result);
        }
        return result;
    }

    private final int executeReturningGeneratedKeys(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);
        int result = executeImmediate(ctx.statement()).executeUpdate();
        ctx.rows(result);
        ctx.resultSet(ctx.statement().getGeneratedKeys());
        listener.executeEnd(ctx);
        return result;
    }

    private final int executeReturningGeneratedKeysFetchAdditionalRows(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);
        int result = executeImmediate(ctx.statement()).executeUpdate();
        ctx.rows(result);
        listener.executeEnd(ctx);
        try {
            ResultSet rs = ctx.statement().getGeneratedKeys();
            try {
                List<Object> list = new ArrayList<>();
                if (rs != null) {
                    while (rs.next()) {
                        list.add(rs.getObject(1));
                    }
                }
                selectReturning(((DefaultExecuteContext) ctx).originalConfiguration(), ctx.configuration(), list.toArray());
                JDBCUtils.safeClose(rs);
                return result;
            } catch (Throwable th) {
                JDBCUtils.safeClose(rs);
                throw th;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    private final void executeReturningQuery(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);
        ctx.resultSet(ctx.statement().executeQuery());
        listener.executeEnd(ctx);
    }

    private final void selectReturning(Configuration originalConfiguration, Configuration derivedConfiguration, Object... values) {
        Field<?> returnedIdentity;
        if (values != null && values.length > 0 && (returnedIdentity = returnedIdentity()) != null) {
            DataType<Object> type = returnedIdentity.getDataType();
            Object[] ids = Tools.map(values, v -> {
                return type.convert(v);
            }, x$0 -> {
                return new Object[x$0];
            });
            if (this.returningResolvedAsterisks.size() == 1 && new FieldsImpl(this.returningResolvedAsterisks).field(returnedIdentity) != null) {
                AbstractRow<?> row0 = Tools.row0((Field<?>[]) this.returningResolvedAsterisks.toArray(Tools.EMPTY_FIELD));
                for (Object id : ids) {
                    getResult().add(Tools.newRecord(true, AbstractRecord.class, row0, originalConfiguration).operate(record -> {
                        record.values[0] = id;
                        record.originals[0] = id;
                        return record;
                    }));
                }
                return;
            }
            this.returnedResult = derivedConfiguration.dsl().select(this.returning).from(this.table).where(((Field) StringUtils.defaultIfNull(this.table.field(returnedIdentity), returnedIdentity)).in(ids)).fetch();
            this.returnedResult.attach(originalConfiguration);
        }
    }

    private final Field<?> returnedIdentity() {
        if (this.table.getIdentity() != null) {
            return this.table.getIdentity().getField();
        }
        return (Field) Tools.findAny(this.returningResolvedAsterisks, f -> {
            return f.getDataType().identity();
        });
    }

    public final Field<?>[] getFields(ThrowingSupplier<? extends ResultSetMetaData, SQLException> rs) throws SQLException {
        return (Field[]) this.returningResolvedAsterisks.toArray(Tools.EMPTY_FIELD);
    }

    public final Class<? extends Record> getRecordType() {
        return Tools.recordType(this.returningResolvedAsterisks.size());
    }
}

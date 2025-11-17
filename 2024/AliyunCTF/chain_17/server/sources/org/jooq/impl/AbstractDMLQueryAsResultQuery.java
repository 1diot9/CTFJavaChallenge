package org.jooq.impl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import org.jooq.CloseableResultQuery;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.AbstractDMLQuery;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDMLQueryAsResultQuery.class */
public abstract class AbstractDMLQueryAsResultQuery<R extends Record, Q extends AbstractDMLQuery<R>> extends AbstractQueryPart implements ResultQueryTrait<R> {
    final Q delegate;
    final boolean returningResult;
    Table<?> coerceTable;
    Collection<? extends Field<?>> coerceFields;

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public /* bridge */ /* synthetic */ ResultQuery coerce(Collection collection) {
        return coerce((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public /* bridge */ /* synthetic */ ResultQuery intern(Field[] fieldArr) {
        return intern((Field<?>[]) fieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDMLQueryAsResultQuery(Q delegate, boolean returningResult) {
        this.delegate = delegate;
        this.returningResult = returningResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Q getDelegate() {
        return this.delegate;
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields(ThrowingSupplier<? extends ResultSetMetaData, SQLException> rs) throws SQLException {
        Field<?>[] f = getFields();
        return f != null ? f : this.delegate.getFields(rs);
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields() {
        if (this.coerceFields != null && !this.coerceFields.isEmpty()) {
            return (Field[]) this.coerceFields.toArray(Tools.EMPTY_FIELD);
        }
        return (Field[]) this.delegate.returningResolvedAsterisks.toArray(Tools.EMPTY_FIELD);
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

    @Override // org.jooq.ResultQuery
    public final Class<? extends R> getRecordType() {
        if (this.coerceTable != null) {
            return (Class<? extends R>) this.coerceTable.getRecordType();
        }
        if (this.returningResult) {
            return (Class<? extends R>) this.delegate.getRecordType();
        }
        return this.delegate.table().getRecordType();
    }

    @Override // org.jooq.ResultQuery
    public final Result<R> fetch() {
        this.delegate.execute();
        return getResult();
    }

    @Override // org.jooq.Query
    public final int execute() throws DataAccessException {
        return this.delegate.execute();
    }

    @Override // org.jooq.Query
    public final CompletionStage<Integer> executeAsync() {
        return this.delegate.executeAsync();
    }

    @Override // org.jooq.Query
    public final CompletionStage<Integer> executeAsync(Executor executor) {
        return this.delegate.executeAsync(executor);
    }

    @Override // org.jooq.Query
    public final boolean isExecutable() {
        return this.delegate.isExecutable();
    }

    @Override // org.jooq.AttachableQueryPart
    public final String getSQL() {
        return this.delegate.getSQL();
    }

    @Override // org.jooq.AttachableQueryPart
    public final String getSQL(ParamType paramType) {
        return this.delegate.getSQL(paramType);
    }

    @Override // org.jooq.AttachableQueryPart
    public final List<Object> getBindValues() {
        return this.delegate.getBindValues();
    }

    @Override // org.jooq.AttachableQueryPart
    public final Map<String, Param<?>> getParams() {
        return this.delegate.getParams();
    }

    @Override // org.jooq.AttachableQueryPart
    public final Param<?> getParam(String name) {
        return this.delegate.getParam(name);
    }

    @Override // org.jooq.CloseableQuery, java.lang.AutoCloseable
    public final void close() throws DataAccessException {
        this.delegate.close();
    }

    @Override // org.jooq.Query
    public final void cancel() throws DataAccessException {
        this.delegate.cancel();
    }

    @Override // org.jooq.Attachable
    public final void attach(Configuration configuration) {
        this.delegate.attach(configuration);
    }

    @Override // org.jooq.Attachable
    public final void detach() {
        this.delegate.detach();
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.delegate);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.Attachable
    public final Configuration configuration() {
        return this.delegate.configuration();
    }

    @Override // org.jooq.ResultQuery
    public final Result<R> getResult() {
        return this.returningResult ? (Result<R>) this.delegate.getResult() : this.delegate.getReturnedRecords();
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery, org.jooq.Query
    public final CloseableResultQuery<R> bind(String param, Object value) throws IllegalArgumentException, DataTypeException {
        this.delegate.bind(param, value);
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery, org.jooq.Query
    public final CloseableResultQuery<R> bind(int index, Object value) throws IllegalArgumentException, DataTypeException {
        this.delegate.bind(index, value);
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery, org.jooq.Query
    public final CloseableResultQuery<R> poolable(boolean poolable) {
        this.delegate.poolable(poolable);
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery, org.jooq.Query
    public final CloseableResultQuery<R> queryTimeout(int timeout) {
        this.delegate.queryTimeout(timeout);
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery, org.jooq.Query
    public final CloseableResultQuery<R> keepStatement(boolean keepStatement) {
        this.delegate.keepStatement(keepStatement);
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> maxRows(int rows) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> fetchSize(int rows) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetConcurrency(int resultSetConcurrency) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetType(int resultSetType) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> resultSetHoldability(int resultSetHoldability) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(Field<?>... fields) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(int... fieldIndexes) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(String... fieldNames) {
        return this;
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    public final CloseableResultQuery<R> intern(Name... fieldNames) {
        return this;
    }
}

package org.jooq.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Map;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.ConverterContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteType;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Routine;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SimpleExecuteContext.class */
public final class SimpleExecuteContext extends AbstractScope implements ExecuteContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleExecuteContext(Configuration configuration, Map<Object, Object> data) {
        super(configuration, data);
    }

    @Override // org.jooq.ExecuteContext
    public final ConverterContext converterContext() {
        return Tools.converterContext(this.configuration);
    }

    @Override // org.jooq.ExecuteContext
    public final Connection connection() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final ExecuteType type() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final Query query() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final ExecuteContext.BatchMode batchMode() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final Query[] batchQueries() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final Routine<?> routine() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final String sql() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void sql(String sql) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final int skipUpdateCounts() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void skipUpdateCounts(int skip) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final String[] batchSQL() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void connectionProvider(ConnectionProvider connectionProvider) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final PreparedStatement statement() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void statement(PreparedStatement statement) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final int statementExecutionCount() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final ResultSet resultSet() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void resultSet(ResultSet resultSet) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final int recordLevel() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final Record record() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void record(Record record) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final int rows() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void rows(int rows) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final int[] batchRows() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final int resultLevel() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final Result<?> result() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void result(Result<?> result) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final RuntimeException exception() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void exception(RuntimeException e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final SQLException sqlException() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void sqlException(SQLException e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final SQLWarning sqlWarning() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void sqlWarning(SQLWarning e) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final String[] serverOutput() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override // org.jooq.ExecuteContext
    public final void serverOutput(String[] output) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

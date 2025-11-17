package org.jooq.tools.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/BatchedPreparedStatement.class */
public class BatchedPreparedStatement extends DefaultPreparedStatement {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) BatchedPreparedStatement.class);
    final String sql;
    int batches;
    boolean executeImmediate;
    boolean getMoreResults;

    public BatchedPreparedStatement(String sql, BatchedConnection connection, PreparedStatement delegate) {
        super(delegate, connection);
        this.getMoreResults = true;
        this.sql = sql;
    }

    public BatchedConnection getBatchedConnection() throws SQLException {
        return (BatchedConnection) super.getConnection();
    }

    public boolean getExecuteImmediate() {
        return this.executeImmediate;
    }

    public void setExecuteImmediate(boolean executeImmediate) {
        this.executeImmediate = executeImmediate;
    }

    private void resetBatches() {
        this.batches = 0;
    }

    private void resetMoreResults() {
        this.getMoreResults = true;
    }

    private void logExecuteImmediate() throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("BatchedStatement", "Skipped batching statement: " + getBatchedConnection().lastSQL);
        }
        resetMoreResults();
    }

    private void logBatch() throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("BatchedStatement", "Batched " + this.batches + " times: " + getBatchedConnection().lastSQL);
        }
        resetMoreResults();
    }

    private void logExecution() throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("BatchedStatement", "Executed with " + this.batches + " batched items: " + getBatchedConnection().lastSQL);
        }
        resetMoreResults();
        resetBatches();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        return BatchedPreparedStatement.class == cls ? this : (T) super.unwrap(cls);
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return BatchedPreparedStatement.class == iface || super.isWrapperFor(iface);
    }

    @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
    public int executeUpdate() throws SQLException {
        if (this.executeImmediate) {
            logExecuteImmediate();
            return super.executeUpdate();
        }
        addBatch();
        return 0;
    }

    @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
    public boolean execute() throws SQLException {
        resetMoreResults();
        if (this.executeImmediate) {
            logExecuteImmediate();
            return super.execute();
        }
        addBatch();
        return false;
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public int getUpdateCount() throws SQLException {
        return this.getMoreResults ? 0 : -1;
    }

    @Override // org.jooq.tools.jdbc.DefaultPreparedStatement
    public long executeLargeUpdate() throws SQLException {
        return executeUpdate();
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public long getLargeUpdateCount() throws SQLException {
        return getUpdateCount();
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement, java.lang.AutoCloseable
    public void close() throws SQLException {
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public boolean getMoreResults() throws SQLException {
        this.getMoreResults = false;
        return false;
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public boolean getMoreResults(int current) throws SQLException {
        return getMoreResults();
    }

    @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
    public void addBatch() throws SQLException {
        getBatchedConnection().setBatch(this);
        this.batches++;
        logBatch();
        super.addBatch();
        if (this.batches >= getBatchedConnection().batchSize) {
            getBatchedConnection().executeLastBatch();
            this.batches = 0;
            super.clearBatch();
        }
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public void clearBatch() throws SQLException {
        throw new UnsupportedOperationException("Clearing a batch is not yet supported");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public int[] executeBatch() throws SQLException {
        logExecution();
        return super.executeBatch();
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public long[] executeLargeBatch() throws SQLException {
        logExecution();
        return super.executeLargeBatch();
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public void addBatch(String s) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public boolean execute(String s) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public boolean execute(String s, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public boolean execute(String s, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public int executeUpdate(String s) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public int executeUpdate(String s, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public int executeUpdate(String s, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public int executeUpdate(String s, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public long executeLargeUpdate(String s) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public long executeLargeUpdate(String s, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public long executeLargeUpdate(String s, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public long executeLargeUpdate(String s, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public ResultSet executeQuery(String s) throws SQLException {
        throw new UnsupportedOperationException("No static statement methods can be called");
    }

    @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, java.sql.PreparedStatement
    public ResultSet executeQuery() throws SQLException {
        if (this.batches == 0) {
            logExecuteImmediate();
            return super.executeQuery();
        }
        throw new UnsupportedOperationException("Cannot batch result queries");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public ResultSet getResultSet() throws SQLException {
        throw new UnsupportedOperationException("Cannot batch result queries");
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException("Cannot batch result queries");
    }
}

package cn.hutool.db.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/StatementWrapper.class */
public class StatementWrapper implements PreparedStatement {
    private PreparedStatement rawStatement;

    public StatementWrapper(PreparedStatement rawStatement) {
        this.rawStatement = rawStatement;
    }

    @Override // java.sql.Statement
    public ResultSet executeQuery(String sql) throws SQLException {
        return this.rawStatement.executeQuery(sql);
    }

    @Override // java.sql.Statement
    public int executeUpdate(String sql) throws SQLException {
        return this.rawStatement.executeUpdate(sql);
    }

    @Override // java.sql.Statement, java.lang.AutoCloseable
    public void close() throws SQLException {
        this.rawStatement.close();
    }

    @Override // java.sql.Statement
    public int getMaxFieldSize() throws SQLException {
        return this.rawStatement.getMaxFieldSize();
    }

    @Override // java.sql.Statement
    public void setMaxFieldSize(int max) throws SQLException {
        this.rawStatement.setMaxFieldSize(max);
    }

    @Override // java.sql.Statement
    public int getMaxRows() throws SQLException {
        return this.rawStatement.getMaxRows();
    }

    @Override // java.sql.Statement
    public void setMaxRows(int max) throws SQLException {
        this.rawStatement.setMaxRows(max);
    }

    @Override // java.sql.Statement
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.rawStatement.setEscapeProcessing(enable);
    }

    @Override // java.sql.Statement
    public int getQueryTimeout() throws SQLException {
        return this.rawStatement.getQueryTimeout();
    }

    @Override // java.sql.Statement
    public void setQueryTimeout(int seconds) throws SQLException {
        this.rawStatement.setQueryTimeout(seconds);
    }

    @Override // java.sql.Statement
    public void cancel() throws SQLException {
        this.rawStatement.cancel();
    }

    @Override // java.sql.Statement
    public SQLWarning getWarnings() throws SQLException {
        return this.rawStatement.getWarnings();
    }

    @Override // java.sql.Statement
    public void clearWarnings() throws SQLException {
        this.rawStatement.clearWarnings();
    }

    @Override // java.sql.Statement
    public void setCursorName(String name) throws SQLException {
        this.rawStatement.setCursorName(name);
    }

    @Override // java.sql.Statement
    public boolean execute(String sql) throws SQLException {
        return this.rawStatement.execute(sql);
    }

    @Override // java.sql.Statement
    public ResultSet getResultSet() throws SQLException {
        return this.rawStatement.getResultSet();
    }

    @Override // java.sql.Statement
    public int getUpdateCount() throws SQLException {
        return this.rawStatement.getUpdateCount();
    }

    @Override // java.sql.Statement
    public boolean getMoreResults() throws SQLException {
        return this.rawStatement.getMoreResults();
    }

    @Override // java.sql.Statement
    public void setFetchDirection(int direction) throws SQLException {
        this.rawStatement.setFetchDirection(direction);
    }

    @Override // java.sql.Statement
    public int getFetchDirection() throws SQLException {
        return this.rawStatement.getFetchDirection();
    }

    @Override // java.sql.Statement
    public void setFetchSize(int rows) throws SQLException {
        this.rawStatement.setFetchSize(rows);
    }

    @Override // java.sql.Statement
    public int getFetchSize() throws SQLException {
        return this.rawStatement.getFetchSize();
    }

    @Override // java.sql.Statement
    public int getResultSetConcurrency() throws SQLException {
        return this.rawStatement.getResultSetConcurrency();
    }

    @Override // java.sql.Statement
    public int getResultSetType() throws SQLException {
        return this.rawStatement.getResultSetType();
    }

    @Override // java.sql.Statement
    public void addBatch(String sql) throws SQLException {
        this.rawStatement.addBatch(sql);
    }

    @Override // java.sql.Statement
    public void clearBatch() throws SQLException {
        this.rawStatement.clearBatch();
    }

    @Override // java.sql.Statement
    public int[] executeBatch() throws SQLException {
        return this.rawStatement.executeBatch();
    }

    @Override // java.sql.Statement
    public Connection getConnection() throws SQLException {
        return this.rawStatement.getConnection();
    }

    @Override // java.sql.Statement
    public boolean getMoreResults(int current) throws SQLException {
        return this.rawStatement.getMoreResults(current);
    }

    @Override // java.sql.Statement
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.rawStatement.getGeneratedKeys();
    }

    @Override // java.sql.Statement
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.rawStatement.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override // java.sql.Statement
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.rawStatement.executeUpdate(sql, columnIndexes);
    }

    @Override // java.sql.Statement
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.rawStatement.executeUpdate(sql, columnNames);
    }

    @Override // java.sql.Statement
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return this.rawStatement.execute(sql, autoGeneratedKeys);
    }

    @Override // java.sql.Statement
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return this.rawStatement.execute(sql, columnIndexes);
    }

    @Override // java.sql.Statement
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return this.rawStatement.execute(sql, columnNames);
    }

    @Override // java.sql.Statement
    public int getResultSetHoldability() throws SQLException {
        return this.rawStatement.getResultSetHoldability();
    }

    @Override // java.sql.Statement
    public boolean isClosed() throws SQLException {
        return this.rawStatement.isClosed();
    }

    @Override // java.sql.Statement
    public void setPoolable(boolean poolable) throws SQLException {
        this.rawStatement.setPoolable(poolable);
    }

    @Override // java.sql.Statement
    public boolean isPoolable() throws SQLException {
        return this.rawStatement.isPoolable();
    }

    public void closeOnCompletion() throws SQLException {
        this.rawStatement.closeOnCompletion();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return this.rawStatement.isCloseOnCompletion();
    }

    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        return (T) this.rawStatement.unwrap(cls);
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.rawStatement.isWrapperFor(iface);
    }

    @Override // java.sql.PreparedStatement
    public ResultSet executeQuery() throws SQLException {
        return this.rawStatement.executeQuery();
    }

    @Override // java.sql.PreparedStatement
    public int executeUpdate() throws SQLException {
        return this.rawStatement.executeUpdate();
    }

    @Override // java.sql.PreparedStatement
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.rawStatement.setNull(parameterIndex, sqlType);
    }

    @Override // java.sql.PreparedStatement
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.rawStatement.setBoolean(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.rawStatement.setByte(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.rawStatement.setShort(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.rawStatement.setInt(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.rawStatement.setLong(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.rawStatement.setFloat(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.rawStatement.setDouble(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.rawStatement.setBigDecimal(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setString(int parameterIndex, String x) throws SQLException {
        this.rawStatement.setString(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.rawStatement.setBytes(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.rawStatement.setDate(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.rawStatement.setTime(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.rawStatement.setTimestamp(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.rawStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.rawStatement.setUnicodeStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.rawStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void clearParameters() throws SQLException {
        this.rawStatement.clearParameters();
    }

    @Override // java.sql.PreparedStatement
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.rawStatement.setObject(parameterIndex, x, targetSqlType, targetSqlType);
    }

    @Override // java.sql.PreparedStatement
    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.rawStatement.setObject(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public boolean execute() throws SQLException {
        return this.rawStatement.execute();
    }

    @Override // java.sql.PreparedStatement
    public void addBatch() throws SQLException {
        this.rawStatement.addBatch();
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        this.rawStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        this.rawStatement.setRef(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        this.rawStatement.setBlob(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        this.rawStatement.setClob(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.rawStatement.setArray(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.rawStatement.getMetaData();
    }

    @Override // java.sql.PreparedStatement
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.rawStatement.setDate(parameterIndex, x, cal);
    }

    @Override // java.sql.PreparedStatement
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.rawStatement.setTime(parameterIndex, x, cal);
    }

    @Override // java.sql.PreparedStatement
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.rawStatement.setTimestamp(parameterIndex, x, cal);
    }

    @Override // java.sql.PreparedStatement
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.rawStatement.setNull(parameterIndex, sqlType, typeName);
    }

    @Override // java.sql.PreparedStatement
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.rawStatement.setURL(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.rawStatement.getParameterMetaData();
    }

    @Override // java.sql.PreparedStatement
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        this.rawStatement.setRowId(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setNString(int parameterIndex, String value) throws SQLException {
        this.rawStatement.setNString(parameterIndex, value);
    }

    @Override // java.sql.PreparedStatement
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        this.rawStatement.setCharacterStream(parameterIndex, value, length);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        this.rawStatement.setNClob(parameterIndex, value);
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.rawStatement.setClob(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        this.rawStatement.setBlob(parameterIndex, inputStream, length);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.rawStatement.setNClob(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        this.rawStatement.setSQLXML(parameterIndex, xmlObject);
    }

    @Override // java.sql.PreparedStatement
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        this.rawStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.rawStatement.setAsciiStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        this.rawStatement.setBinaryStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        this.rawStatement.setCharacterStream(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        this.rawStatement.setAsciiStream(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        this.rawStatement.setBinaryStream(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        this.rawStatement.setCharacterStream(parameterIndex, reader);
    }

    @Override // java.sql.PreparedStatement
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        this.rawStatement.setNCharacterStream(parameterIndex, value);
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        this.rawStatement.setClob(parameterIndex, reader);
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        this.rawStatement.setBlob(parameterIndex, inputStream);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        this.rawStatement.setNClob(parameterIndex, reader);
    }
}

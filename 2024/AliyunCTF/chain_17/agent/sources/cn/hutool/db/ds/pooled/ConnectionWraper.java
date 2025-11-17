package cn.hutool.db.ds.pooled;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/pooled/ConnectionWraper.class */
public abstract class ConnectionWraper implements Connection {
    protected Connection raw;

    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        return (T) this.raw.unwrap(cls);
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.raw.isWrapperFor(iface);
    }

    @Override // java.sql.Connection
    public Statement createStatement() throws SQLException {
        return this.raw.createStatement();
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.raw.prepareStatement(sql);
    }

    @Override // java.sql.Connection
    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.raw.prepareCall(sql);
    }

    @Override // java.sql.Connection
    public String nativeSQL(String sql) throws SQLException {
        return this.raw.nativeSQL(sql);
    }

    @Override // java.sql.Connection
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.raw.setAutoCommit(autoCommit);
    }

    @Override // java.sql.Connection
    public boolean getAutoCommit() throws SQLException {
        return this.raw.getAutoCommit();
    }

    @Override // java.sql.Connection
    public void commit() throws SQLException {
        this.raw.commit();
    }

    @Override // java.sql.Connection
    public void rollback() throws SQLException {
        this.raw.rollback();
    }

    @Override // java.sql.Connection
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.raw.getMetaData();
    }

    @Override // java.sql.Connection
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.raw.setReadOnly(readOnly);
    }

    @Override // java.sql.Connection
    public boolean isReadOnly() throws SQLException {
        return this.raw.isReadOnly();
    }

    @Override // java.sql.Connection
    public void setCatalog(String catalog) throws SQLException {
        this.raw.setCatalog(catalog);
    }

    @Override // java.sql.Connection
    public String getCatalog() throws SQLException {
        return this.raw.getCatalog();
    }

    @Override // java.sql.Connection
    public void setTransactionIsolation(int level) throws SQLException {
        this.raw.setTransactionIsolation(level);
    }

    @Override // java.sql.Connection
    public int getTransactionIsolation() throws SQLException {
        return this.raw.getTransactionIsolation();
    }

    @Override // java.sql.Connection
    public SQLWarning getWarnings() throws SQLException {
        return this.raw.getWarnings();
    }

    @Override // java.sql.Connection
    public void clearWarnings() throws SQLException {
        this.raw.clearWarnings();
    }

    @Override // java.sql.Connection
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.raw.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.raw.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override // java.sql.Connection
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.raw.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override // java.sql.Connection
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.raw.getTypeMap();
    }

    @Override // java.sql.Connection
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.raw.setTypeMap(map);
    }

    @Override // java.sql.Connection
    public void setHoldability(int holdability) throws SQLException {
        this.raw.setHoldability(holdability);
    }

    @Override // java.sql.Connection
    public int getHoldability() throws SQLException {
        return this.raw.getHoldability();
    }

    @Override // java.sql.Connection
    public Savepoint setSavepoint() throws SQLException {
        return this.raw.setSavepoint();
    }

    @Override // java.sql.Connection
    public Savepoint setSavepoint(String name) throws SQLException {
        return this.raw.setSavepoint(name);
    }

    @Override // java.sql.Connection
    public void rollback(Savepoint savepoint) throws SQLException {
        this.raw.rollback(savepoint);
    }

    @Override // java.sql.Connection
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.raw.releaseSavepoint(savepoint);
    }

    @Override // java.sql.Connection
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.raw.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.raw.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override // java.sql.Connection
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.raw.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.raw.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.raw.prepareStatement(sql, columnIndexes);
    }

    @Override // java.sql.Connection
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.raw.prepareStatement(sql, columnNames);
    }

    @Override // java.sql.Connection
    public Clob createClob() throws SQLException {
        return this.raw.createClob();
    }

    @Override // java.sql.Connection
    public Blob createBlob() throws SQLException {
        return this.raw.createBlob();
    }

    @Override // java.sql.Connection
    public NClob createNClob() throws SQLException {
        return this.raw.createNClob();
    }

    @Override // java.sql.Connection
    public SQLXML createSQLXML() throws SQLException {
        return this.raw.createSQLXML();
    }

    @Override // java.sql.Connection
    public boolean isValid(int timeout) throws SQLException {
        return this.raw.isValid(timeout);
    }

    @Override // java.sql.Connection
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.raw.setClientInfo(name, value);
    }

    @Override // java.sql.Connection
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.raw.setClientInfo(properties);
    }

    @Override // java.sql.Connection
    public String getClientInfo(String name) throws SQLException {
        return this.raw.getClientInfo(name);
    }

    @Override // java.sql.Connection
    public Properties getClientInfo() throws SQLException {
        return this.raw.getClientInfo();
    }

    @Override // java.sql.Connection
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.raw.createArrayOf(typeName, elements);
    }

    @Override // java.sql.Connection
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.raw.createStruct(typeName, attributes);
    }

    public void setSchema(String schema) throws SQLException {
        this.raw.setSchema(schema);
    }

    public String getSchema() throws SQLException {
        return this.raw.getSchema();
    }

    public void abort(Executor executor) throws SQLException {
        this.raw.abort(executor);
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.raw.setNetworkTimeout(executor, milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        return this.raw.getNetworkTimeout();
    }

    public Connection getRaw() {
        return this.raw;
    }
}

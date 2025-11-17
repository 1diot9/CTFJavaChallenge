package org.jooq.tools.jdbc;

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
import java.sql.ShardingKey;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/DefaultConnection.class */
public class DefaultConnection extends JDBC41Connection implements Connection {
    private final Connection delegate;

    public DefaultConnection(Connection delegate) {
        this.delegate = delegate;
    }

    public Connection getDelegate() {
        return this.delegate;
    }

    public <T> T unwrap(Class<T> cls) throws SQLException {
        return (T) getDelegate().unwrap(cls);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDelegate().isWrapperFor(iface);
    }

    public Statement createStatement() throws SQLException {
        return getDelegate().createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getDelegate().prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return getDelegate().prepareCall(sql);
    }

    @Override // java.sql.Connection
    public String nativeSQL(String sql) throws SQLException {
        return getDelegate().nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        getDelegate().setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return getDelegate().getAutoCommit();
    }

    public void commit() throws SQLException {
        getDelegate().commit();
    }

    public void rollback() throws SQLException {
        getDelegate().rollback();
    }

    public void close() throws SQLException {
        getDelegate().close();
    }

    @Override // java.sql.Connection
    public boolean isClosed() throws SQLException {
        return getDelegate().isClosed();
    }

    @Override // java.sql.Connection
    public DatabaseMetaData getMetaData() throws SQLException {
        return getDelegate().getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        getDelegate().setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return getDelegate().isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        getDelegate().setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return getDelegate().getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        getDelegate().setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return getDelegate().getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return getDelegate().getWarnings();
    }

    public void clearWarnings() throws SQLException {
        getDelegate().clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getDelegate().createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getDelegate().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getDelegate().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getDelegate().getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getDelegate().setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        getDelegate().setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return getDelegate().getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return getDelegate().setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return getDelegate().setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        getDelegate().rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getDelegate().releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getDelegate().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getDelegate().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getDelegate().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getDelegate().prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getDelegate().prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getDelegate().prepareStatement(sql, columnNames);
    }

    @Override // java.sql.Connection
    public Clob createClob() throws SQLException {
        return getDelegate().createClob();
    }

    @Override // java.sql.Connection
    public Blob createBlob() throws SQLException {
        return getDelegate().createBlob();
    }

    @Override // java.sql.Connection
    public NClob createNClob() throws SQLException {
        return getDelegate().createNClob();
    }

    @Override // java.sql.Connection
    public SQLXML createSQLXML() throws SQLException {
        return getDelegate().createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return getDelegate().isValid(timeout);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        getDelegate().setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        getDelegate().setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return getDelegate().getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return getDelegate().getClientInfo();
    }

    @Override // java.sql.Connection
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getDelegate().createArrayOf(typeName, elements);
    }

    @Override // java.sql.Connection
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getDelegate().createStruct(typeName, attributes);
    }

    @Override // org.jooq.tools.jdbc.JDBC41Connection
    public void setSchema(String s) throws SQLException {
        getDelegate().setSchema(s);
    }

    @Override // org.jooq.tools.jdbc.JDBC41Connection
    public String getSchema() throws SQLException {
        return getDelegate().getSchema();
    }

    @Override // org.jooq.tools.jdbc.JDBC41Connection
    public void abort(Executor executor) throws SQLException {
        getDelegate().abort(executor);
    }

    @Override // org.jooq.tools.jdbc.JDBC41Connection
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getDelegate().setNetworkTimeout(executor, milliseconds);
    }

    @Override // org.jooq.tools.jdbc.JDBC41Connection
    public int getNetworkTimeout() throws SQLException {
        return getDelegate().getNetworkTimeout();
    }

    public void beginRequest() throws SQLException {
        getDelegate().beginRequest();
    }

    public void endRequest() throws SQLException {
        getDelegate().endRequest();
    }

    public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
        return getDelegate().setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
    }

    public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
        return getDelegate().setShardingKeyIfValid(shardingKey, timeout);
    }

    public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
        getDelegate().setShardingKey(shardingKey, superShardingKey);
    }

    public void setShardingKey(ShardingKey shardingKey) throws SQLException {
        getDelegate().setShardingKey(shardingKey);
    }
}

package org.jooq.tools.jdbc;

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
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/DefaultPreparedStatement.class */
public class DefaultPreparedStatement extends DefaultStatement implements PreparedStatement {
    public DefaultPreparedStatement(PreparedStatement delegate) {
        super(delegate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DefaultPreparedStatement(Statement delegate) {
        super(delegate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DefaultPreparedStatement(Statement delegate, Connection creator) {
        super(delegate, creator);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DefaultPreparedStatement(Statement delegate, Connection creator, Supplier<? extends SQLException> errorIfUnsupported) {
        super(delegate, creator, errorIfUnsupported);
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement
    public PreparedStatement getDelegate() throws SQLException {
        return getDelegatePreparedStatement();
    }

    public final PreparedStatement getDelegatePreparedStatement() throws SQLException {
        return (PreparedStatement) getDelegateStatement();
    }

    public ResultSet executeQuery() throws SQLException {
        return wrap(getDelegatePreparedStatement().executeQuery());
    }

    public int executeUpdate() throws SQLException {
        return getDelegatePreparedStatement().executeUpdate();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        getDelegatePreparedStatement().setNull(parameterIndex, sqlType);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        getDelegatePreparedStatement().setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        getDelegatePreparedStatement().setByte(parameterIndex, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        getDelegatePreparedStatement().setShort(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        getDelegatePreparedStatement().setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        getDelegatePreparedStatement().setLong(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        getDelegatePreparedStatement().setFloat(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        getDelegatePreparedStatement().setDouble(parameterIndex, x);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        getDelegatePreparedStatement().setBigDecimal(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        getDelegatePreparedStatement().setString(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        getDelegatePreparedStatement().setBytes(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        getDelegatePreparedStatement().setDate(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        getDelegatePreparedStatement().setTime(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        getDelegatePreparedStatement().setTimestamp(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        getDelegatePreparedStatement().setAsciiStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        getDelegatePreparedStatement().setUnicodeStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        getDelegatePreparedStatement().setBinaryStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void clearParameters() throws SQLException {
        getDelegatePreparedStatement().clearParameters();
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        getDelegatePreparedStatement().setObject(parameterIndex, x);
    }

    public boolean execute() throws SQLException {
        return getDelegatePreparedStatement().execute();
    }

    public void addBatch() throws SQLException {
        getDelegatePreparedStatement().addBatch();
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        getDelegatePreparedStatement().setRef(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        getDelegatePreparedStatement().setBlob(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        getDelegatePreparedStatement().setClob(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setArray(int parameterIndex, Array x) throws SQLException {
        getDelegatePreparedStatement().setArray(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public ResultSetMetaData getMetaData() throws SQLException {
        return getDelegatePreparedStatement().getMetaData();
    }

    @Override // java.sql.PreparedStatement
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        getDelegatePreparedStatement().setDate(parameterIndex, x, cal);
    }

    @Override // java.sql.PreparedStatement
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        getDelegatePreparedStatement().setTime(parameterIndex, x, cal);
    }

    @Override // java.sql.PreparedStatement
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        getDelegatePreparedStatement().setTimestamp(parameterIndex, x, cal);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        getDelegatePreparedStatement().setNull(parameterIndex, sqlType, typeName);
    }

    @Override // java.sql.PreparedStatement
    public void setURL(int parameterIndex, URL x) throws SQLException {
        getDelegatePreparedStatement().setURL(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return getDelegatePreparedStatement().getParameterMetaData();
    }

    @Override // java.sql.PreparedStatement
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        getDelegatePreparedStatement().setRowId(parameterIndex, x);
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        getDelegatePreparedStatement().setNString(parameterIndex, value);
    }

    @Override // java.sql.PreparedStatement
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        getDelegatePreparedStatement().setNCharacterStream(parameterIndex, value, length);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        getDelegatePreparedStatement().setNClob(parameterIndex, value);
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        getDelegatePreparedStatement().setClob(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        getDelegatePreparedStatement().setBlob(parameterIndex, inputStream, length);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        getDelegatePreparedStatement().setNClob(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        getDelegatePreparedStatement().setSQLXML(parameterIndex, xmlObject);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        getDelegatePreparedStatement().setAsciiStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        getDelegatePreparedStatement().setBinaryStream(parameterIndex, x, length);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader, length);
    }

    @Override // java.sql.PreparedStatement
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        getDelegatePreparedStatement().setAsciiStream(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        getDelegatePreparedStatement().setBinaryStream(parameterIndex, x);
    }

    @Override // java.sql.PreparedStatement
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader);
    }

    @Override // java.sql.PreparedStatement
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        getDelegatePreparedStatement().setNCharacterStream(parameterIndex, value);
    }

    @Override // java.sql.PreparedStatement
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        getDelegatePreparedStatement().setClob(parameterIndex, reader);
    }

    @Override // java.sql.PreparedStatement
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        getDelegatePreparedStatement().setBlob(parameterIndex, inputStream);
    }

    @Override // java.sql.PreparedStatement
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        getDelegatePreparedStatement().setNClob(parameterIndex, reader);
    }

    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType);
    }

    public long executeLargeUpdate() throws SQLException {
        return getDelegatePreparedStatement().executeLargeUpdate();
    }
}

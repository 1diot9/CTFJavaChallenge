package org.jooq.tools.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/DefaultCallableStatement.class */
public class DefaultCallableStatement extends DefaultPreparedStatement implements CallableStatement {
    public DefaultCallableStatement(CallableStatement delegate) {
        super((PreparedStatement) delegate);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DefaultCallableStatement(Statement delegate) {
        super(delegate);
    }

    @Override // org.jooq.tools.jdbc.DefaultPreparedStatement, org.jooq.tools.jdbc.DefaultStatement
    public CallableStatement getDelegate() throws SQLException {
        return getDelegateCallableStatement();
    }

    public CallableStatement getDelegateCallableStatement() throws SQLException {
        return (CallableStatement) getDelegateStatement();
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType, scale);
    }

    @Override // java.sql.CallableStatement
    public boolean wasNull() throws SQLException {
        return getDelegateCallableStatement().wasNull();
    }

    @Override // java.sql.CallableStatement
    public String getString(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getString(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getBoolean(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public byte getByte(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getByte(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public short getShort(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getShort(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public int getInt(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getInt(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public long getLong(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getLong(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public float getFloat(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getFloat(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public double getDouble(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getDouble(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return getDelegateCallableStatement().getBigDecimal(parameterIndex, scale);
    }

    @Override // java.sql.CallableStatement
    public byte[] getBytes(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getBytes(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getDate(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getTime(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getTimestamp(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Object getObject(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getObject(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getBigDecimal(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        return getDelegateCallableStatement().getObject(parameterIndex, map);
    }

    @Override // java.sql.CallableStatement
    public Ref getRef(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getRef(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Blob getBlob(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getBlob(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Clob getClob(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getClob(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Array getArray(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getArray(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return getDelegateCallableStatement().getDate(parameterIndex, cal);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return getDelegateCallableStatement().getTime(parameterIndex, cal);
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return getDelegateCallableStatement().getTimestamp(parameterIndex, cal);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType, typeName);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        getDelegateCallableStatement().registerOutParameter(parameterName, sqlType);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, scale);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, typeName);
    }

    @Override // java.sql.CallableStatement
    public URL getURL(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getURL(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public void setURL(String parameterName, URL val) throws SQLException {
        getDelegateCallableStatement().setURL(parameterName, val);
    }

    @Override // java.sql.CallableStatement
    public void setNull(String parameterName, int sqlType) throws SQLException {
        getDelegateCallableStatement().setNull(parameterName, sqlType);
    }

    @Override // java.sql.CallableStatement
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        getDelegateCallableStatement().setBoolean(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setByte(String parameterName, byte x) throws SQLException {
        getDelegateCallableStatement().setByte(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setShort(String parameterName, short x) throws SQLException {
        getDelegateCallableStatement().setShort(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setInt(String parameterName, int x) throws SQLException {
        getDelegateCallableStatement().setInt(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setLong(String parameterName, long x) throws SQLException {
        getDelegateCallableStatement().setLong(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setFloat(String parameterName, float x) throws SQLException {
        getDelegateCallableStatement().setFloat(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setDouble(String parameterName, double x) throws SQLException {
        getDelegateCallableStatement().setDouble(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        getDelegateCallableStatement().setBigDecimal(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setString(String parameterName, String x) throws SQLException {
        getDelegateCallableStatement().setString(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        getDelegateCallableStatement().setBytes(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setDate(String parameterName, Date x) throws SQLException {
        getDelegateCallableStatement().setDate(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setTime(String parameterName, Time x) throws SQLException {
        getDelegateCallableStatement().setTime(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        getDelegateCallableStatement().setTimestamp(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        getDelegateCallableStatement().setAsciiStream(parameterName, x, length);
    }

    @Override // java.sql.CallableStatement
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        getDelegateCallableStatement().setBinaryStream(parameterName, x, length);
    }

    @Override // java.sql.CallableStatement
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        getDelegateCallableStatement().setObject(parameterName, x, targetSqlType, scale);
    }

    @Override // java.sql.CallableStatement
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        getDelegateCallableStatement().setObject(parameterName, x, targetSqlType);
    }

    @Override // java.sql.CallableStatement
    public void setObject(String parameterName, Object x) throws SQLException {
        getDelegateCallableStatement().setObject(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        getDelegateCallableStatement().setCharacterStream(parameterName, reader, length);
    }

    @Override // java.sql.CallableStatement
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        getDelegateCallableStatement().setDate(parameterName, x, cal);
    }

    @Override // java.sql.CallableStatement
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        getDelegateCallableStatement().setTime(parameterName, x, cal);
    }

    @Override // java.sql.CallableStatement
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        getDelegateCallableStatement().setTimestamp(parameterName, x, cal);
    }

    @Override // java.sql.CallableStatement
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        getDelegateCallableStatement().setNull(parameterName, sqlType, typeName);
    }

    @Override // java.sql.CallableStatement
    public String getString(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getString(parameterName);
    }

    @Override // java.sql.CallableStatement
    public boolean getBoolean(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getBoolean(parameterName);
    }

    @Override // java.sql.CallableStatement
    public byte getByte(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getByte(parameterName);
    }

    @Override // java.sql.CallableStatement
    public short getShort(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getShort(parameterName);
    }

    @Override // java.sql.CallableStatement
    public int getInt(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getInt(parameterName);
    }

    @Override // java.sql.CallableStatement
    public long getLong(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getLong(parameterName);
    }

    @Override // java.sql.CallableStatement
    public float getFloat(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getFloat(parameterName);
    }

    @Override // java.sql.CallableStatement
    public double getDouble(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getDouble(parameterName);
    }

    @Override // java.sql.CallableStatement
    public byte[] getBytes(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getBytes(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getDate(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getTime(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getTimestamp(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Object getObject(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getObject(parameterName);
    }

    @Override // java.sql.CallableStatement
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getBigDecimal(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        return getDelegateCallableStatement().getObject(parameterName, map);
    }

    @Override // java.sql.CallableStatement
    public Ref getRef(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getRef(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Blob getBlob(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getBlob(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Clob getClob(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getClob(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Array getArray(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getArray(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return getDelegateCallableStatement().getDate(parameterName, cal);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return getDelegateCallableStatement().getTime(parameterName, cal);
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return getDelegateCallableStatement().getTimestamp(parameterName, cal);
    }

    @Override // java.sql.CallableStatement
    public URL getURL(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getURL(parameterName);
    }

    @Override // java.sql.CallableStatement
    public RowId getRowId(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getRowId(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public RowId getRowId(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getRowId(parameterName);
    }

    @Override // java.sql.CallableStatement
    public void setRowId(String parameterName, RowId x) throws SQLException {
        getDelegateCallableStatement().setRowId(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setNString(String parameterName, String value) throws SQLException {
        getDelegateCallableStatement().setNString(parameterName, value);
    }

    @Override // java.sql.CallableStatement
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        getDelegateCallableStatement().setNCharacterStream(parameterName, value, length);
    }

    @Override // java.sql.CallableStatement
    public void setNClob(String parameterName, NClob value) throws SQLException {
        getDelegateCallableStatement().setNClob(parameterName, value);
    }

    @Override // java.sql.CallableStatement
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        getDelegateCallableStatement().setClob(parameterName, reader, length);
    }

    @Override // java.sql.CallableStatement
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        getDelegateCallableStatement().setBlob(parameterName, inputStream, length);
    }

    @Override // java.sql.CallableStatement
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        getDelegateCallableStatement().setNClob(parameterName, reader, length);
    }

    @Override // java.sql.CallableStatement
    public NClob getNClob(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getNClob(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public NClob getNClob(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getNClob(parameterName);
    }

    @Override // java.sql.CallableStatement
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        getDelegateCallableStatement().setSQLXML(parameterName, xmlObject);
    }

    @Override // java.sql.CallableStatement
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getSQLXML(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getSQLXML(parameterName);
    }

    @Override // java.sql.CallableStatement
    public String getNString(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getNString(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public String getNString(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getNString(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getNCharacterStream(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getNCharacterStream(parameterName);
    }

    @Override // java.sql.CallableStatement
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return getDelegateCallableStatement().getCharacterStream(parameterIndex);
    }

    @Override // java.sql.CallableStatement
    public Reader getCharacterStream(String parameterName) throws SQLException {
        return getDelegateCallableStatement().getCharacterStream(parameterName);
    }

    @Override // java.sql.CallableStatement
    public void setBlob(String parameterName, Blob x) throws SQLException {
        getDelegateCallableStatement().setBlob(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setClob(String parameterName, Clob x) throws SQLException {
        getDelegateCallableStatement().setClob(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        getDelegateCallableStatement().setAsciiStream(parameterName, x, length);
    }

    @Override // java.sql.CallableStatement
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        getDelegateCallableStatement().setBinaryStream(parameterName, x, length);
    }

    @Override // java.sql.CallableStatement
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        getDelegateCallableStatement().setCharacterStream(parameterName, reader, length);
    }

    @Override // java.sql.CallableStatement
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        getDelegateCallableStatement().setAsciiStream(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        getDelegateCallableStatement().setBinaryStream(parameterName, x);
    }

    @Override // java.sql.CallableStatement
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        getDelegateCallableStatement().setCharacterStream(parameterName, reader);
    }

    @Override // java.sql.CallableStatement
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        getDelegateCallableStatement().setNCharacterStream(parameterName, value);
    }

    @Override // java.sql.CallableStatement
    public void setClob(String parameterName, Reader reader) throws SQLException {
        getDelegateCallableStatement().setClob(parameterName, reader);
    }

    @Override // java.sql.CallableStatement
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        getDelegateCallableStatement().setBlob(parameterName, inputStream);
    }

    @Override // java.sql.CallableStatement
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        getDelegateCallableStatement().setNClob(parameterName, reader);
    }

    @Override // org.jooq.tools.jdbc.JDBC41Statement
    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        return (T) getDelegate().getObject(i, cls);
    }

    @Override // org.jooq.tools.jdbc.JDBC41Statement
    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        return (T) getDelegate().getObject(str, cls);
    }

    public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        getDelegate().setObject(parameterName, x, targetSqlType, scaleOrLength);
    }

    public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
        getDelegate().setObject(parameterName, x, targetSqlType);
    }

    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
        getDelegate().registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
        getDelegate().registerOutParameter(parameterIndex, sqlType, scale);
    }

    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
        getDelegate().registerOutParameter(parameterIndex, sqlType, typeName);
    }

    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
        getDelegate().registerOutParameter(parameterName, sqlType);
    }

    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
        getDelegate().registerOutParameter(parameterName, sqlType, scale);
    }

    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
        getDelegate().registerOutParameter(parameterName, sqlType, typeName);
    }
}

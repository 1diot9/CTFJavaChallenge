package org.jooq.tools.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.function.Supplier;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/DefaultResultSet.class */
public class DefaultResultSet extends JDBC41ResultSet implements ResultSet {
    private final ResultSet delegate;
    private final Statement creator;
    private final Supplier<? extends SQLException> errorIfUnsupported;

    public DefaultResultSet(ResultSet delegate) {
        this(delegate, null, null);
    }

    public DefaultResultSet(ResultSet delegate, Statement creator) {
        this(delegate, creator, null);
    }

    public DefaultResultSet(ResultSet delegate, Statement creator, Supplier<? extends SQLException> errorIfUnsupported) {
        this.delegate = delegate;
        this.creator = creator;
        this.errorIfUnsupported = errorIfUnsupported;
    }

    public ResultSet getDelegate() throws SQLException {
        if (this.delegate != null || this.errorIfUnsupported == null) {
            return this.delegate;
        }
        throw this.errorIfUnsupported.get();
    }

    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        return (T) getDelegate().unwrap(cls);
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDelegate().isWrapperFor(iface);
    }

    public boolean next() throws SQLException {
        return getDelegate().next();
    }

    public void close() throws SQLException {
        getDelegate().close();
    }

    public boolean wasNull() throws SQLException {
        return getDelegate().wasNull();
    }

    public String getString(int columnIndex) throws SQLException {
        return getDelegate().getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return getDelegate().getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws SQLException {
        return getDelegate().getByte(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        return getDelegate().getShort(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        return getDelegate().getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException {
        return getDelegate().getLong(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException {
        return getDelegate().getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException {
        return getDelegate().getDouble(columnIndex);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return getDelegate().getBigDecimal(columnIndex, scale);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return getDelegate().getBytes(columnIndex);
    }

    public Date getDate(int columnIndex) throws SQLException {
        return getDelegate().getDate(columnIndex);
    }

    public Time getTime(int columnIndex) throws SQLException {
        return getDelegate().getTime(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getDelegate().getTimestamp(columnIndex);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return getDelegate().getAsciiStream(columnIndex);
    }

    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return getDelegate().getUnicodeStream(columnIndex);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getDelegate().getBinaryStream(columnIndex);
    }

    public String getString(String columnLabel) throws SQLException {
        return getDelegate().getString(columnLabel);
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        return getDelegate().getBoolean(columnLabel);
    }

    public byte getByte(String columnLabel) throws SQLException {
        return getDelegate().getByte(columnLabel);
    }

    public short getShort(String columnLabel) throws SQLException {
        return getDelegate().getShort(columnLabel);
    }

    public int getInt(String columnLabel) throws SQLException {
        return getDelegate().getInt(columnLabel);
    }

    public long getLong(String columnLabel) throws SQLException {
        return getDelegate().getLong(columnLabel);
    }

    public float getFloat(String columnLabel) throws SQLException {
        return getDelegate().getFloat(columnLabel);
    }

    public double getDouble(String columnLabel) throws SQLException {
        return getDelegate().getDouble(columnLabel);
    }

    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getDelegate().getBigDecimal(columnLabel, scale);
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return getDelegate().getBytes(columnLabel);
    }

    public Date getDate(String columnLabel) throws SQLException {
        return getDelegate().getDate(columnLabel);
    }

    public Time getTime(String columnLabel) throws SQLException {
        return getDelegate().getTime(columnLabel);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getDelegate().getTimestamp(columnLabel);
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getDelegate().getAsciiStream(columnLabel);
    }

    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getDelegate().getUnicodeStream(columnLabel);
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getDelegate().getBinaryStream(columnLabel);
    }

    @Override // java.sql.ResultSet
    public SQLWarning getWarnings() throws SQLException {
        return getDelegate().getWarnings();
    }

    @Override // java.sql.ResultSet
    public void clearWarnings() throws SQLException {
        getDelegate().clearWarnings();
    }

    @Override // java.sql.ResultSet
    public String getCursorName() throws SQLException {
        return getDelegate().getCursorName();
    }

    @Override // java.sql.ResultSet
    public ResultSetMetaData getMetaData() throws SQLException {
        return getDelegate().getMetaData();
    }

    public Object getObject(int columnIndex) throws SQLException {
        return getDelegate().getObject(columnIndex);
    }

    public Object getObject(String columnLabel) throws SQLException {
        return getDelegate().getObject(columnLabel);
    }

    @Override // java.sql.ResultSet
    public int findColumn(String columnLabel) throws SQLException {
        return getDelegate().findColumn(columnLabel);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return getDelegate().getCharacterStream(columnIndex);
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getDelegate().getCharacterStream(columnLabel);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return getDelegate().getBigDecimal(columnIndex);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getDelegate().getBigDecimal(columnLabel);
    }

    public boolean isBeforeFirst() throws SQLException {
        return getDelegate().isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException {
        return getDelegate().isAfterLast();
    }

    public boolean isFirst() throws SQLException {
        return getDelegate().isFirst();
    }

    public boolean isLast() throws SQLException {
        return getDelegate().isLast();
    }

    public void beforeFirst() throws SQLException {
        getDelegate().beforeFirst();
    }

    public void afterLast() throws SQLException {
        getDelegate().afterLast();
    }

    public boolean first() throws SQLException {
        return getDelegate().first();
    }

    public boolean last() throws SQLException {
        return getDelegate().last();
    }

    @Override // java.sql.ResultSet
    public int getRow() throws SQLException {
        return getDelegate().getRow();
    }

    public boolean absolute(int row) throws SQLException {
        return getDelegate().absolute(row);
    }

    public boolean relative(int rows) throws SQLException {
        return getDelegate().relative(rows);
    }

    public boolean previous() throws SQLException {
        return getDelegate().previous();
    }

    public void setFetchDirection(int direction) throws SQLException {
        getDelegate().setFetchDirection(direction);
    }

    @Override // java.sql.ResultSet
    public int getFetchDirection() throws SQLException {
        return getDelegate().getFetchDirection();
    }

    public void setFetchSize(int rows) throws SQLException {
        getDelegate().setFetchSize(rows);
    }

    @Override // java.sql.ResultSet
    public int getFetchSize() throws SQLException {
        return getDelegate().getFetchSize();
    }

    @Override // java.sql.ResultSet
    public int getType() throws SQLException {
        return getDelegate().getType();
    }

    @Override // java.sql.ResultSet
    public int getConcurrency() throws SQLException {
        return getDelegate().getConcurrency();
    }

    public boolean rowUpdated() throws SQLException {
        return getDelegate().rowUpdated();
    }

    public boolean rowInserted() throws SQLException {
        return getDelegate().rowInserted();
    }

    public boolean rowDeleted() throws SQLException {
        return getDelegate().rowDeleted();
    }

    public void updateNull(int columnIndex) throws SQLException {
        getDelegate().updateNull(columnIndex);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        getDelegate().updateBoolean(columnIndex, x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        getDelegate().updateByte(columnIndex, x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        getDelegate().updateShort(columnIndex, x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        getDelegate().updateInt(columnIndex, x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        getDelegate().updateLong(columnIndex, x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        getDelegate().updateFloat(columnIndex, x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        getDelegate().updateDouble(columnIndex, x);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        getDelegate().updateBigDecimal(columnIndex, x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        getDelegate().updateString(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        getDelegate().updateBytes(columnIndex, x);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        getDelegate().updateDate(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        getDelegate().updateTime(columnIndex, x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        getDelegate().updateTimestamp(columnIndex, x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        getDelegate().updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        getDelegate().updateBinaryStream(columnIndex, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        getDelegate().updateCharacterStream(columnIndex, x, length);
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        getDelegate().updateObject(columnIndex, x, scaleOrLength);
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        getDelegate().updateObject(columnIndex, x);
    }

    public void updateNull(String columnLabel) throws SQLException {
        getDelegate().updateNull(columnLabel);
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        getDelegate().updateBoolean(columnLabel, x);
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        getDelegate().updateByte(columnLabel, x);
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        getDelegate().updateShort(columnLabel, x);
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        getDelegate().updateInt(columnLabel, x);
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        getDelegate().updateLong(columnLabel, x);
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        getDelegate().updateFloat(columnLabel, x);
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        getDelegate().updateDouble(columnLabel, x);
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        getDelegate().updateBigDecimal(columnLabel, x);
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        getDelegate().updateString(columnLabel, x);
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        getDelegate().updateBytes(columnLabel, x);
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        getDelegate().updateDate(columnLabel, x);
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        getDelegate().updateTime(columnLabel, x);
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        getDelegate().updateTimestamp(columnLabel, x);
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        getDelegate().updateAsciiStream(columnLabel, x, length);
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        getDelegate().updateBinaryStream(columnLabel, x, length);
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        getDelegate().updateCharacterStream(columnLabel, reader, length);
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        getDelegate().updateObject(columnLabel, x, scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        getDelegate().updateObject(columnLabel, x);
    }

    public void insertRow() throws SQLException {
        getDelegate().insertRow();
    }

    public void updateRow() throws SQLException {
        getDelegate().updateRow();
    }

    public void deleteRow() throws SQLException {
        getDelegate().deleteRow();
    }

    public void refreshRow() throws SQLException {
        getDelegate().refreshRow();
    }

    @Override // java.sql.ResultSet
    public void cancelRowUpdates() throws SQLException {
        getDelegate().cancelRowUpdates();
    }

    @Override // java.sql.ResultSet
    public void moveToInsertRow() throws SQLException {
        getDelegate().moveToInsertRow();
    }

    @Override // java.sql.ResultSet
    public void moveToCurrentRow() throws SQLException {
        getDelegate().moveToCurrentRow();
    }

    @Override // java.sql.ResultSet
    public Statement getStatement() throws SQLException {
        return this.creator != null ? this.creator : getDelegate().getStatement();
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return getDelegate().getObject(columnIndex, map);
    }

    public Ref getRef(int columnIndex) throws SQLException {
        return getDelegate().getRef(columnIndex);
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        return getDelegate().getBlob(columnIndex);
    }

    public Clob getClob(int columnIndex) throws SQLException {
        return getDelegate().getClob(columnIndex);
    }

    public Array getArray(int columnIndex) throws SQLException {
        return getDelegate().getArray(columnIndex);
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getDelegate().getObject(columnLabel, map);
    }

    public Ref getRef(String columnLabel) throws SQLException {
        return getDelegate().getRef(columnLabel);
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        return getDelegate().getBlob(columnLabel);
    }

    public Clob getClob(String columnLabel) throws SQLException {
        return getDelegate().getClob(columnLabel);
    }

    public Array getArray(String columnLabel) throws SQLException {
        return getDelegate().getArray(columnLabel);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return getDelegate().getDate(columnIndex, cal);
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDelegate().getDate(columnLabel, cal);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return getDelegate().getTime(columnIndex, cal);
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getDelegate().getTime(columnLabel, cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return getDelegate().getTimestamp(columnIndex, cal);
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getDelegate().getTimestamp(columnLabel, cal);
    }

    public URL getURL(int columnIndex) throws SQLException {
        return getDelegate().getURL(columnIndex);
    }

    public URL getURL(String columnLabel) throws SQLException {
        return getDelegate().getURL(columnLabel);
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        getDelegate().updateRef(columnIndex, x);
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        getDelegate().updateRef(columnLabel, x);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        getDelegate().updateBlob(columnIndex, x);
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        getDelegate().updateBlob(columnLabel, x);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        getDelegate().updateClob(columnIndex, x);
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        getDelegate().updateClob(columnLabel, x);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        getDelegate().updateArray(columnIndex, x);
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        getDelegate().updateArray(columnLabel, x);
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        return getDelegate().getRowId(columnIndex);
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        return getDelegate().getRowId(columnLabel);
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        getDelegate().updateRowId(columnIndex, x);
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        getDelegate().updateRowId(columnLabel, x);
    }

    @Override // java.sql.ResultSet
    public int getHoldability() throws SQLException {
        return getDelegate().getHoldability();
    }

    public boolean isClosed() throws SQLException {
        return getDelegate().isClosed();
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        getDelegate().updateNString(columnIndex, nString);
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        getDelegate().updateNString(columnLabel, nString);
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        getDelegate().updateNClob(columnIndex, nClob);
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        getDelegate().updateNClob(columnLabel, nClob);
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        return getDelegate().getNClob(columnIndex);
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        return getDelegate().getNClob(columnLabel);
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return getDelegate().getSQLXML(columnIndex);
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getDelegate().getSQLXML(columnLabel);
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        getDelegate().updateSQLXML(columnIndex, xmlObject);
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        getDelegate().updateSQLXML(columnLabel, xmlObject);
    }

    public String getNString(int columnIndex) throws SQLException {
        return getDelegate().getNString(columnIndex);
    }

    public String getNString(String columnLabel) throws SQLException {
        return getDelegate().getNString(columnLabel);
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getDelegate().getNCharacterStream(columnIndex);
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getDelegate().getNCharacterStream(columnLabel);
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        getDelegate().updateNCharacterStream(columnIndex, x, length);
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        getDelegate().updateNCharacterStream(columnLabel, reader, length);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        getDelegate().updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        getDelegate().updateBinaryStream(columnIndex, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        getDelegate().updateCharacterStream(columnIndex, x, length);
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        getDelegate().updateAsciiStream(columnLabel, x, length);
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        getDelegate().updateBinaryStream(columnLabel, x, length);
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        getDelegate().updateCharacterStream(columnLabel, reader, length);
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        getDelegate().updateBlob(columnIndex, inputStream, length);
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        getDelegate().updateBlob(columnLabel, inputStream, length);
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        getDelegate().updateClob(columnIndex, reader, length);
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        getDelegate().updateClob(columnLabel, reader, length);
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        getDelegate().updateNClob(columnIndex, reader, length);
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        getDelegate().updateNClob(columnLabel, reader, length);
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        getDelegate().updateNCharacterStream(columnIndex, x);
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        getDelegate().updateNCharacterStream(columnLabel, reader);
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        getDelegate().updateAsciiStream(columnIndex, x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        getDelegate().updateBinaryStream(columnIndex, x);
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        getDelegate().updateCharacterStream(columnIndex, x);
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        getDelegate().updateAsciiStream(columnLabel, x);
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        getDelegate().updateBinaryStream(columnLabel, x);
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        getDelegate().updateCharacterStream(columnLabel, reader);
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        getDelegate().updateBlob(columnIndex, inputStream);
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        getDelegate().updateBlob(columnLabel, inputStream);
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        getDelegate().updateClob(columnIndex, reader);
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        getDelegate().updateClob(columnLabel, reader);
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        getDelegate().updateNClob(columnIndex, reader);
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        getDelegate().updateNClob(columnLabel, reader);
    }

    @Override // org.jooq.tools.jdbc.JDBC41ResultSet
    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        return (T) getDelegate().getObject(i, cls);
    }

    @Override // org.jooq.tools.jdbc.JDBC41ResultSet
    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        return (T) getDelegate().getObject(str, cls);
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        getDelegate().updateObject(columnIndex, x, targetSqlType, scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        getDelegate().updateObject(columnLabel, x, targetSqlType, scaleOrLength);
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        getDelegate().updateObject(columnIndex, x, targetSqlType);
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        getDelegate().updateObject(columnLabel, x, targetSqlType);
    }
}

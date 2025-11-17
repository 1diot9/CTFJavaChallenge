package org.h2.jdbc;

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
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.value.ValueNull;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcCallableStatement.class */
public final class JdbcCallableStatement extends JdbcPreparedStatement implements CallableStatement {
    private BitSet outParameters;
    private int maxOutParameters;
    private HashMap<String, Integer> namedParameters;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcCallableStatement(JdbcConnection jdbcConnection, String str, int i, int i2, int i3) {
        super(jdbcConnection, str, i, i2, i3, null);
        setTrace(this.session.getTrace(), 0, i);
    }

    @Override // org.h2.jdbc.JdbcPreparedStatement, java.sql.PreparedStatement
    public int executeUpdate() throws SQLException {
        try {
            checkClosed();
            if (this.command.isQuery()) {
                super.executeQuery();
                return 0;
            }
            return super.executeUpdate();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // org.h2.jdbc.JdbcPreparedStatement
    public long executeLargeUpdate() throws SQLException {
        try {
            checkClosed();
            if (this.command.isQuery()) {
                super.executeQuery();
                return 0L;
            }
            return super.executeLargeUpdate();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(int i, int i2) throws SQLException {
        registerOutParameter(i);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(int i, int i2, String str) throws SQLException {
        registerOutParameter(i);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(int i, int i2, int i3) throws SQLException {
        registerOutParameter(i);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(String str, int i, String str2) throws SQLException {
        registerOutParameter(getIndexForName(str), i, str2);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(String str, int i, int i2) throws SQLException {
        registerOutParameter(getIndexForName(str), i, i2);
    }

    @Override // java.sql.CallableStatement
    public void registerOutParameter(String str, int i) throws SQLException {
        registerOutParameter(getIndexForName(str), i);
    }

    @Override // java.sql.CallableStatement
    public boolean wasNull() throws SQLException {
        return getOpenResultSet().wasNull();
    }

    @Override // java.sql.CallableStatement
    public URL getURL(int i) throws SQLException {
        throw unsupported("url");
    }

    @Override // java.sql.CallableStatement
    public String getString(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getString(i);
    }

    @Override // java.sql.CallableStatement
    public boolean getBoolean(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getBoolean(i);
    }

    @Override // java.sql.CallableStatement
    public byte getByte(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getByte(i);
    }

    @Override // java.sql.CallableStatement
    public short getShort(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getShort(i);
    }

    @Override // java.sql.CallableStatement
    public int getInt(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getInt(i);
    }

    @Override // java.sql.CallableStatement
    public long getLong(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getLong(i);
    }

    @Override // java.sql.CallableStatement
    public float getFloat(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getFloat(i);
    }

    @Override // java.sql.CallableStatement
    public double getDouble(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getDouble(i);
    }

    @Override // java.sql.CallableStatement
    @Deprecated
    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getBigDecimal(i, i2);
    }

    @Override // java.sql.CallableStatement
    public byte[] getBytes(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getBytes(i);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getDate(i);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getTime(i);
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getTimestamp(i);
    }

    @Override // java.sql.CallableStatement
    public Object getObject(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getObject(i);
    }

    @Override // java.sql.CallableStatement
    public BigDecimal getBigDecimal(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getBigDecimal(i);
    }

    @Override // java.sql.CallableStatement
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        throw unsupported(BeanDefinitionParserDelegate.MAP_ELEMENT);
    }

    @Override // java.sql.CallableStatement
    public Ref getRef(int i) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.CallableStatement
    public Blob getBlob(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getBlob(i);
    }

    @Override // java.sql.CallableStatement
    public Clob getClob(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getClob(i);
    }

    @Override // java.sql.CallableStatement
    public Array getArray(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getArray(i);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(int i, Calendar calendar) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getDate(i, calendar);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(int i, Calendar calendar) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getTime(i, calendar);
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getTimestamp(i, calendar);
    }

    @Override // java.sql.CallableStatement
    public URL getURL(String str) throws SQLException {
        throw unsupported("url");
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        return getTimestamp(getIndexForName(str), calendar);
    }

    @Override // java.sql.CallableStatement
    public Time getTime(String str, Calendar calendar) throws SQLException {
        return getTime(getIndexForName(str), calendar);
    }

    @Override // java.sql.CallableStatement
    public Date getDate(String str, Calendar calendar) throws SQLException {
        return getDate(getIndexForName(str), calendar);
    }

    @Override // java.sql.CallableStatement
    public Array getArray(String str) throws SQLException {
        return getArray(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Clob getClob(String str) throws SQLException {
        return getClob(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Blob getBlob(String str) throws SQLException {
        return getBlob(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Ref getRef(String str) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.CallableStatement
    public Object getObject(String str, Map<String, Class<?>> map) throws SQLException {
        throw unsupported(BeanDefinitionParserDelegate.MAP_ELEMENT);
    }

    @Override // java.sql.CallableStatement
    public BigDecimal getBigDecimal(String str) throws SQLException {
        return getBigDecimal(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Object getObject(String str) throws SQLException {
        return getObject(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Timestamp getTimestamp(String str) throws SQLException {
        return getTimestamp(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Time getTime(String str) throws SQLException {
        return getTime(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Date getDate(String str) throws SQLException {
        return getDate(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public byte[] getBytes(String str) throws SQLException {
        return getBytes(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public double getDouble(String str) throws SQLException {
        return getDouble(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public float getFloat(String str) throws SQLException {
        return getFloat(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public long getLong(String str) throws SQLException {
        return getLong(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public int getInt(String str) throws SQLException {
        return getInt(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public short getShort(String str) throws SQLException {
        return getShort(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public byte getByte(String str) throws SQLException {
        return getByte(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public boolean getBoolean(String str) throws SQLException {
        return getBoolean(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public String getString(String str) throws SQLException {
        return getString(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public RowId getRowId(int i) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.CallableStatement
    public RowId getRowId(String str) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.CallableStatement
    public NClob getNClob(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getNClob(i);
    }

    @Override // java.sql.CallableStatement
    public NClob getNClob(String str) throws SQLException {
        return getNClob(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public SQLXML getSQLXML(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getSQLXML(i);
    }

    @Override // java.sql.CallableStatement
    public SQLXML getSQLXML(String str) throws SQLException {
        return getSQLXML(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public String getNString(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getNString(i);
    }

    @Override // java.sql.CallableStatement
    public String getNString(String str) throws SQLException {
        return getNString(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Reader getNCharacterStream(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getNCharacterStream(i);
    }

    @Override // java.sql.CallableStatement
    public Reader getNCharacterStream(String str) throws SQLException {
        return getNCharacterStream(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public Reader getCharacterStream(int i) throws SQLException {
        checkRegistered(i);
        return getOpenResultSet().getCharacterStream(i);
    }

    @Override // java.sql.CallableStatement
    public Reader getCharacterStream(String str) throws SQLException {
        return getCharacterStream(getIndexForName(str));
    }

    @Override // java.sql.CallableStatement
    public void setNull(String str, int i, String str2) throws SQLException {
        setNull(getIndexForName(str), i, str2);
    }

    @Override // java.sql.CallableStatement
    public void setNull(String str, int i) throws SQLException {
        setNull(getIndexForName(str), i);
    }

    @Override // java.sql.CallableStatement
    public void setTimestamp(String str, Timestamp timestamp, Calendar calendar) throws SQLException {
        setTimestamp(getIndexForName(str), timestamp, calendar);
    }

    @Override // java.sql.CallableStatement
    public void setTime(String str, Time time, Calendar calendar) throws SQLException {
        setTime(getIndexForName(str), time, calendar);
    }

    @Override // java.sql.CallableStatement
    public void setDate(String str, Date date, Calendar calendar) throws SQLException {
        setDate(getIndexForName(str), date, calendar);
    }

    @Override // java.sql.CallableStatement
    public void setCharacterStream(String str, Reader reader, int i) throws SQLException {
        setCharacterStream(getIndexForName(str), reader, i);
    }

    @Override // java.sql.CallableStatement
    public void setObject(String str, Object obj) throws SQLException {
        setObject(getIndexForName(str), obj);
    }

    @Override // java.sql.CallableStatement
    public void setObject(String str, Object obj, int i) throws SQLException {
        setObject(getIndexForName(str), obj, i);
    }

    @Override // java.sql.CallableStatement
    public void setObject(String str, Object obj, int i, int i2) throws SQLException {
        setObject(getIndexForName(str), obj, i, i2);
    }

    public void setObject(String str, Object obj, SQLType sQLType) throws SQLException {
        setObject(getIndexForName(str), obj, sQLType);
    }

    public void setObject(String str, Object obj, SQLType sQLType, int i) throws SQLException {
        setObject(getIndexForName(str), obj, sQLType, i);
    }

    @Override // java.sql.CallableStatement
    public void setBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        setBinaryStream(getIndexForName(str), inputStream, i);
    }

    @Override // java.sql.CallableStatement
    public void setAsciiStream(String str, InputStream inputStream, long j) throws SQLException {
        setAsciiStream(getIndexForName(str), inputStream, j);
    }

    @Override // java.sql.CallableStatement
    public void setTimestamp(String str, Timestamp timestamp) throws SQLException {
        setTimestamp(getIndexForName(str), timestamp);
    }

    @Override // java.sql.CallableStatement
    public void setTime(String str, Time time) throws SQLException {
        setTime(getIndexForName(str), time);
    }

    @Override // java.sql.CallableStatement
    public void setDate(String str, Date date) throws SQLException {
        setDate(getIndexForName(str), date);
    }

    @Override // java.sql.CallableStatement
    public void setBytes(String str, byte[] bArr) throws SQLException {
        setBytes(getIndexForName(str), bArr);
    }

    @Override // java.sql.CallableStatement
    public void setString(String str, String str2) throws SQLException {
        setString(getIndexForName(str), str2);
    }

    @Override // java.sql.CallableStatement
    public void setBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        setBigDecimal(getIndexForName(str), bigDecimal);
    }

    @Override // java.sql.CallableStatement
    public void setDouble(String str, double d) throws SQLException {
        setDouble(getIndexForName(str), d);
    }

    @Override // java.sql.CallableStatement
    public void setFloat(String str, float f) throws SQLException {
        setFloat(getIndexForName(str), f);
    }

    @Override // java.sql.CallableStatement
    public void setLong(String str, long j) throws SQLException {
        setLong(getIndexForName(str), j);
    }

    @Override // java.sql.CallableStatement
    public void setInt(String str, int i) throws SQLException {
        setInt(getIndexForName(str), i);
    }

    @Override // java.sql.CallableStatement
    public void setShort(String str, short s) throws SQLException {
        setShort(getIndexForName(str), s);
    }

    @Override // java.sql.CallableStatement
    public void setByte(String str, byte b) throws SQLException {
        setByte(getIndexForName(str), b);
    }

    @Override // java.sql.CallableStatement
    public void setBoolean(String str, boolean z) throws SQLException {
        setBoolean(getIndexForName(str), z);
    }

    @Override // java.sql.CallableStatement
    public void setURL(String str, URL url) throws SQLException {
        throw unsupported("url");
    }

    @Override // java.sql.CallableStatement
    public void setRowId(String str, RowId rowId) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.CallableStatement
    public void setNString(String str, String str2) throws SQLException {
        setNString(getIndexForName(str), str2);
    }

    @Override // java.sql.CallableStatement
    public void setNCharacterStream(String str, Reader reader, long j) throws SQLException {
        setNCharacterStream(getIndexForName(str), reader, j);
    }

    @Override // java.sql.CallableStatement
    public void setNClob(String str, NClob nClob) throws SQLException {
        setNClob(getIndexForName(str), nClob);
    }

    @Override // java.sql.CallableStatement
    public void setClob(String str, Reader reader, long j) throws SQLException {
        setClob(getIndexForName(str), reader, j);
    }

    @Override // java.sql.CallableStatement
    public void setBlob(String str, InputStream inputStream, long j) throws SQLException {
        setBlob(getIndexForName(str), inputStream, j);
    }

    @Override // java.sql.CallableStatement
    public void setNClob(String str, Reader reader, long j) throws SQLException {
        setNClob(getIndexForName(str), reader, j);
    }

    @Override // java.sql.CallableStatement
    public void setBlob(String str, Blob blob) throws SQLException {
        setBlob(getIndexForName(str), blob);
    }

    @Override // java.sql.CallableStatement
    public void setClob(String str, Clob clob) throws SQLException {
        setClob(getIndexForName(str), clob);
    }

    @Override // java.sql.CallableStatement
    public void setAsciiStream(String str, InputStream inputStream) throws SQLException {
        setAsciiStream(getIndexForName(str), inputStream);
    }

    @Override // java.sql.CallableStatement
    public void setAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        setAsciiStream(getIndexForName(str), inputStream, i);
    }

    @Override // java.sql.CallableStatement
    public void setBinaryStream(String str, InputStream inputStream) throws SQLException {
        setBinaryStream(getIndexForName(str), inputStream);
    }

    @Override // java.sql.CallableStatement
    public void setBinaryStream(String str, InputStream inputStream, long j) throws SQLException {
        setBinaryStream(getIndexForName(str), inputStream, j);
    }

    @Override // java.sql.CallableStatement
    public void setBlob(String str, InputStream inputStream) throws SQLException {
        setBlob(getIndexForName(str), inputStream);
    }

    @Override // java.sql.CallableStatement
    public void setCharacterStream(String str, Reader reader) throws SQLException {
        setCharacterStream(getIndexForName(str), reader);
    }

    @Override // java.sql.CallableStatement
    public void setCharacterStream(String str, Reader reader, long j) throws SQLException {
        setCharacterStream(getIndexForName(str), reader, j);
    }

    @Override // java.sql.CallableStatement
    public void setClob(String str, Reader reader) throws SQLException {
        setClob(getIndexForName(str), reader);
    }

    @Override // java.sql.CallableStatement
    public void setNCharacterStream(String str, Reader reader) throws SQLException {
        setNCharacterStream(getIndexForName(str), reader);
    }

    @Override // java.sql.CallableStatement
    public void setNClob(String str, Reader reader) throws SQLException {
        setNClob(getIndexForName(str), reader);
    }

    @Override // java.sql.CallableStatement
    public void setSQLXML(String str, SQLXML sqlxml) throws SQLException {
        setSQLXML(getIndexForName(str), sqlxml);
    }

    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        return (T) getOpenResultSet().getObject(i, cls);
    }

    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        return (T) getObject(getIndexForName(str), cls);
    }

    private ResultSetMetaData getCheckedMetaData() throws SQLException {
        ResultSetMetaData metaData = getMetaData();
        if (metaData == null) {
            throw DbException.getUnsupportedException("Supported only for calling stored procedures");
        }
        return metaData;
    }

    private void checkIndexBounds(int i) {
        checkClosed();
        if (i < 1 || i > this.maxOutParameters) {
            throw DbException.getInvalidValueException("parameterIndex", Integer.valueOf(i));
        }
    }

    private void registerOutParameter(int i) throws SQLException {
        try {
            checkClosed();
            if (this.outParameters == null) {
                this.maxOutParameters = Math.min(getParameterMetaData().getParameterCount(), getCheckedMetaData().getColumnCount());
                this.outParameters = new BitSet();
            }
            checkIndexBounds(i);
            int i2 = i - 1;
            ParameterInterface parameterInterface = this.command.getParameters().get(i2);
            if (!parameterInterface.isValueSet()) {
                parameterInterface.setValue(ValueNull.INSTANCE, false);
            }
            this.outParameters.set(i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void checkRegistered(int i) throws SQLException {
        try {
            checkIndexBounds(i);
            if (!this.outParameters.get(i - 1)) {
                throw DbException.getInvalidValueException("parameterIndex", Integer.valueOf(i));
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private int getIndexForName(String str) throws SQLException {
        try {
            checkClosed();
            if (this.namedParameters == null) {
                ResultSetMetaData checkedMetaData = getCheckedMetaData();
                int columnCount = checkedMetaData.getColumnCount();
                HashMap<String, Integer> hashMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    hashMap.put(checkedMetaData.getColumnLabel(i), Integer.valueOf(i));
                }
                this.namedParameters = hashMap;
            }
            Integer num = this.namedParameters.get(str);
            if (num == null) {
                throw DbException.getInvalidValueException("parameterName", str);
            }
            return num.intValue();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private JdbcResultSet getOpenResultSet() throws SQLException {
        try {
            checkClosed();
            if (this.resultSet == null) {
                throw DbException.get(2000);
            }
            if (this.resultSet.isBeforeFirst()) {
                this.resultSet.next();
            }
            return this.resultSet;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}

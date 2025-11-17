package org.h2.jdbc;

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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.coyote.http11.Constants;
import org.h2.api.ErrorCode;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcLob;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.result.UpdatableRow;
import org.h2.util.IOUtils;
import org.h2.util.LegacyDateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.TextareaTag;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcResultSet.class */
public final class JdbcResultSet extends TraceObject implements ResultSet {
    private final boolean scrollable;
    private final boolean updatable;
    private final boolean triggerUpdatable;
    ResultInterface result;
    private JdbcConnection conn;
    private JdbcStatement stat;
    private int columnCount;
    private boolean wasNull;
    private Value[] insertRow;
    private Value[] updateRow;
    private HashMap<String, Integer> columnLabelMap;
    private HashMap<Long, Value[]> patchedRows;
    private JdbcPreparedStatement preparedStatement;
    private final CommandInterface command;

    public JdbcResultSet(JdbcConnection jdbcConnection, JdbcStatement jdbcStatement, CommandInterface commandInterface, ResultInterface resultInterface, int i, boolean z, boolean z2, boolean z3) {
        setTrace(jdbcConnection.getSession().getTrace(), 4, i);
        this.conn = jdbcConnection;
        this.stat = jdbcStatement;
        this.command = commandInterface;
        this.result = resultInterface;
        this.columnCount = resultInterface.getVisibleColumnCount();
        this.scrollable = z;
        this.updatable = z2;
        this.triggerUpdatable = z3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcResultSet(JdbcConnection jdbcConnection, JdbcPreparedStatement jdbcPreparedStatement, CommandInterface commandInterface, ResultInterface resultInterface, int i, boolean z, boolean z2, HashMap<String, Integer> hashMap) {
        this(jdbcConnection, (JdbcStatement) jdbcPreparedStatement, commandInterface, resultInterface, i, z, z2, false);
        this.columnLabelMap = hashMap;
        this.preparedStatement = jdbcPreparedStatement;
    }

    @Override // java.sql.ResultSet
    public boolean next() throws SQLException {
        try {
            debugCodeCall("next");
            checkClosed();
            return nextRow();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            int nextId = getNextId(5);
            debugCodeAssign("ResultSetMetaData", 5, nextId, "getMetaData()");
            checkClosed();
            return new JdbcResultSetMetaData(this, null, this.result, this.conn.getCatalog(), this.conn.getSession().getTrace(), nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean wasNull() throws SQLException {
        try {
            debugCodeCall("wasNull");
            checkClosed();
            return this.wasNull;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public int findColumn(String str) throws SQLException {
        try {
            debugCodeCall("findColumn", str);
            return getColumnIndex(str);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet, java.lang.AutoCloseable
    public void close() throws SQLException {
        try {
            debugCodeCall(Constants.CLOSE);
            closeInternal(false);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeInternal(boolean z) {
        if (this.result != null) {
            try {
                if (this.result.isLazy()) {
                    this.stat.onLazyResultSetClose(this.command, this.preparedStatement == null);
                }
                this.result.close();
            } finally {
                JdbcStatement jdbcStatement = this.stat;
                this.columnCount = 0;
                this.result = null;
                this.stat = null;
                this.conn = null;
                this.insertRow = null;
                this.updateRow = null;
                if (!z && jdbcStatement != null) {
                    jdbcStatement.closeIfCloseOnCompletion();
                }
            }
        }
    }

    @Override // java.sql.ResultSet
    public Statement getStatement() throws SQLException {
        try {
            debugCodeCall("getStatement");
            checkClosed();
            return this.stat;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public SQLWarning getWarnings() throws SQLException {
        try {
            debugCodeCall("getWarnings");
            checkClosed();
            return null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void clearWarnings() throws SQLException {
        try {
            debugCodeCall("clearWarnings");
            checkClosed();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public String getString(int i) throws SQLException {
        try {
            debugCodeCall("getString", i);
            return get(checkColumnIndex(i)).getString();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public String getString(String str) throws SQLException {
        try {
            debugCodeCall("getString", str);
            return get(getColumnIndex(str)).getString();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public int getInt(int i) throws SQLException {
        try {
            debugCodeCall("getInt", i);
            return getIntInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public int getInt(String str) throws SQLException {
        try {
            debugCodeCall("getInt", str);
            return getIntInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private int getIntInternal(int i) {
        int i2;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            i2 = internal.getInt();
        } else {
            this.wasNull = true;
            i2 = 0;
        }
        return i2;
    }

    @Override // java.sql.ResultSet
    public BigDecimal getBigDecimal(int i) throws SQLException {
        try {
            debugCodeCall("getBigDecimal", i);
            return get(checkColumnIndex(i)).getBigDecimal();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Date getDate(int i) throws SQLException {
        try {
            debugCodeCall("getDate", i);
            return LegacyDateTimeUtils.toDate(this.conn, null, get(checkColumnIndex(i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Time getTime(int i) throws SQLException {
        try {
            debugCodeCall("getTime", i);
            return LegacyDateTimeUtils.toTime(this.conn, null, get(checkColumnIndex(i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(int i) throws SQLException {
        try {
            debugCodeCall("getTimestamp", i);
            return LegacyDateTimeUtils.toTimestamp(this.conn, null, get(checkColumnIndex(i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public BigDecimal getBigDecimal(String str) throws SQLException {
        try {
            debugCodeCall("getBigDecimal", str);
            return get(getColumnIndex(str)).getBigDecimal();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Date getDate(String str) throws SQLException {
        try {
            debugCodeCall("getDate", str);
            return LegacyDateTimeUtils.toDate(this.conn, null, get(getColumnIndex(str)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Time getTime(String str) throws SQLException {
        try {
            debugCodeCall("getTime", str);
            return LegacyDateTimeUtils.toTime(this.conn, null, get(getColumnIndex(str)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(String str) throws SQLException {
        try {
            debugCodeCall("getTimestamp", str);
            return LegacyDateTimeUtils.toTimestamp(this.conn, null, get(getColumnIndex(str)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Object getObject(int i) throws SQLException {
        try {
            debugCodeCall("getObject", i);
            return ValueToObjectConverter.valueToDefaultObject(get(checkColumnIndex(i)), this.conn, true);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Object getObject(String str) throws SQLException {
        try {
            debugCodeCall("getObject", str);
            return ValueToObjectConverter.valueToDefaultObject(get(getColumnIndex(str)), this.conn, true);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean getBoolean(int i) throws SQLException {
        try {
            debugCodeCall("getBoolean", i);
            return getBooleanInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean getBoolean(String str) throws SQLException {
        try {
            debugCodeCall("getBoolean", str);
            return getBooleanInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private boolean getBooleanInternal(int i) {
        boolean z;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            z = internal.getBoolean();
        } else {
            this.wasNull = true;
            z = false;
        }
        return z;
    }

    @Override // java.sql.ResultSet
    public byte getByte(int i) throws SQLException {
        try {
            debugCodeCall("getByte", i);
            return getByteInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public byte getByte(String str) throws SQLException {
        try {
            debugCodeCall("getByte", str);
            return getByteInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private byte getByteInternal(int i) {
        byte b;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            b = internal.getByte();
        } else {
            this.wasNull = true;
            b = 0;
        }
        return b;
    }

    @Override // java.sql.ResultSet
    public short getShort(int i) throws SQLException {
        try {
            debugCodeCall("getShort", i);
            return getShortInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public short getShort(String str) throws SQLException {
        try {
            debugCodeCall("getShort", str);
            return getShortInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private short getShortInternal(int i) {
        short s;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            s = internal.getShort();
        } else {
            this.wasNull = true;
            s = 0;
        }
        return s;
    }

    @Override // java.sql.ResultSet
    public long getLong(int i) throws SQLException {
        try {
            debugCodeCall("getLong", i);
            return getLongInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public long getLong(String str) throws SQLException {
        try {
            debugCodeCall("getLong", str);
            return getLongInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private long getLongInternal(int i) {
        long j;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            j = internal.getLong();
        } else {
            this.wasNull = true;
            j = 0;
        }
        return j;
    }

    @Override // java.sql.ResultSet
    public float getFloat(int i) throws SQLException {
        try {
            debugCodeCall("getFloat", i);
            return getFloatInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public float getFloat(String str) throws SQLException {
        try {
            debugCodeCall("getFloat", str);
            return getFloatInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private float getFloatInternal(int i) {
        float f;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            f = internal.getFloat();
        } else {
            this.wasNull = true;
            f = 0.0f;
        }
        return f;
    }

    @Override // java.sql.ResultSet
    public double getDouble(int i) throws SQLException {
        try {
            debugCodeCall("getDouble", i);
            return getDoubleInternal(checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public double getDouble(String str) throws SQLException {
        try {
            debugCodeCall("getDouble", str);
            return getDoubleInternal(getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private double getDoubleInternal(int i) {
        double d;
        Value internal = getInternal(i);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            d = internal.getDouble();
        } else {
            this.wasNull = true;
            d = 0.0d;
        }
        return d;
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public BigDecimal getBigDecimal(String str, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getBigDecimal(" + quote(str) + ", " + i + ')');
            }
            if (i < 0) {
                throw DbException.getInvalidValueException("scale", Integer.valueOf(i));
            }
            BigDecimal bigDecimal = get(getColumnIndex(str)).getBigDecimal();
            if (bigDecimal == null) {
                return null;
            }
            return ValueNumeric.setScale(bigDecimal, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getBigDecimal(" + i + ", " + i2 + ')');
            }
            if (i2 < 0) {
                throw DbException.getInvalidValueException("scale", Integer.valueOf(i2));
            }
            BigDecimal bigDecimal = get(checkColumnIndex(i)).getBigDecimal();
            if (bigDecimal == null) {
                return null;
            }
            return ValueNumeric.setScale(bigDecimal, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public InputStream getUnicodeStream(int i) throws SQLException {
        throw unsupported("unicodeStream");
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public InputStream getUnicodeStream(String str) throws SQLException {
        throw unsupported("unicodeStream");
    }

    @Override // java.sql.ResultSet
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        throw unsupported(BeanDefinitionParserDelegate.MAP_ELEMENT);
    }

    @Override // java.sql.ResultSet
    public Object getObject(String str, Map<String, Class<?>> map) throws SQLException {
        throw unsupported(BeanDefinitionParserDelegate.MAP_ELEMENT);
    }

    @Override // java.sql.ResultSet
    public Ref getRef(int i) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.ResultSet
    public Ref getRef(String str) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.ResultSet
    public Date getDate(int i, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getDate(" + i + ", calendar)");
            }
            return LegacyDateTimeUtils.toDate(this.conn, calendar != null ? calendar.getTimeZone() : null, get(checkColumnIndex(i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Date getDate(String str, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getDate(" + quote(str) + ", calendar)");
            }
            return LegacyDateTimeUtils.toDate(this.conn, calendar != null ? calendar.getTimeZone() : null, get(getColumnIndex(str)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Time getTime(int i, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getTime(" + i + ", calendar)");
            }
            return LegacyDateTimeUtils.toTime(this.conn, calendar != null ? calendar.getTimeZone() : null, get(checkColumnIndex(i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Time getTime(String str, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getTime(" + quote(str) + ", calendar)");
            }
            return LegacyDateTimeUtils.toTime(this.conn, calendar != null ? calendar.getTimeZone() : null, get(getColumnIndex(str)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getTimestamp(" + i + ", calendar)");
            }
            return LegacyDateTimeUtils.toTimestamp(this.conn, calendar != null ? calendar.getTimeZone() : null, get(checkColumnIndex(i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getTimestamp(" + quote(str) + ", calendar)");
            }
            return LegacyDateTimeUtils.toTimestamp(this.conn, calendar != null ? calendar.getTimeZone() : null, get(getColumnIndex(str)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Blob getBlob(int i) throws SQLException {
        try {
            int nextId = getNextId(9);
            if (isDebugEnabled()) {
                debugCodeAssign("Blob", 9, nextId, "getBlob(" + i + ')');
            }
            return getBlob(nextId, checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Blob getBlob(String str) throws SQLException {
        try {
            int nextId = getNextId(9);
            if (isDebugEnabled()) {
                debugCodeAssign("Blob", 9, nextId, "getBlob(" + quote(str) + ')');
            }
            return getBlob(nextId, getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private JdbcBlob getBlob(int i, int i2) {
        JdbcBlob jdbcBlob;
        Value internal = getInternal(i2);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            jdbcBlob = new JdbcBlob(this.conn, internal, JdbcLob.State.WITH_VALUE, i);
        } else {
            this.wasNull = true;
            jdbcBlob = null;
        }
        return jdbcBlob;
    }

    @Override // java.sql.ResultSet
    public byte[] getBytes(int i) throws SQLException {
        try {
            debugCodeCall("getBytes", i);
            return get(checkColumnIndex(i)).getBytes();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public byte[] getBytes(String str) throws SQLException {
        try {
            debugCodeCall("getBytes", str);
            return get(getColumnIndex(str)).getBytes();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public InputStream getBinaryStream(int i) throws SQLException {
        try {
            debugCodeCall("getBinaryStream", i);
            return get(checkColumnIndex(i)).getInputStream();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public InputStream getBinaryStream(String str) throws SQLException {
        try {
            debugCodeCall("getBinaryStream", str);
            return get(getColumnIndex(str)).getInputStream();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Clob getClob(int i) throws SQLException {
        try {
            int nextId = getNextId(10);
            if (isDebugEnabled()) {
                debugCodeAssign("Clob", 10, nextId, "getClob(" + i + ')');
            }
            return getClob(nextId, checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Clob getClob(String str) throws SQLException {
        try {
            int nextId = getNextId(10);
            if (isDebugEnabled()) {
                debugCodeAssign("Clob", 10, nextId, "getClob(" + quote(str) + ')');
            }
            return getClob(nextId, getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Array getArray(int i) throws SQLException {
        try {
            int nextId = getNextId(16);
            if (isDebugEnabled()) {
                debugCodeAssign("Array", 16, nextId, "getArray(" + i + ')');
            }
            return getArray(nextId, checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Array getArray(String str) throws SQLException {
        try {
            int nextId = getNextId(16);
            if (isDebugEnabled()) {
                debugCodeAssign("Array", 16, nextId, "getArray(" + quote(str) + ')');
            }
            return getArray(nextId, getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private Array getArray(int i, int i2) {
        JdbcArray jdbcArray;
        Value internal = getInternal(i2);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            jdbcArray = new JdbcArray(this.conn, internal, i);
        } else {
            this.wasNull = true;
            jdbcArray = null;
        }
        return jdbcArray;
    }

    @Override // java.sql.ResultSet
    public InputStream getAsciiStream(int i) throws SQLException {
        try {
            debugCodeCall("getAsciiStream", i);
            String string = get(checkColumnIndex(i)).getString();
            if (string == null) {
                return null;
            }
            return IOUtils.getInputStreamFromString(string);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public InputStream getAsciiStream(String str) throws SQLException {
        try {
            debugCodeCall("getAsciiStream", str);
            return IOUtils.getInputStreamFromString(get(getColumnIndex(str)).getString());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Reader getCharacterStream(int i) throws SQLException {
        try {
            debugCodeCall("getCharacterStream", i);
            return get(checkColumnIndex(i)).getReader();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Reader getCharacterStream(String str) throws SQLException {
        try {
            debugCodeCall("getCharacterStream", str);
            return get(getColumnIndex(str)).getReader();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public URL getURL(int i) throws SQLException {
        throw unsupported("url");
    }

    @Override // java.sql.ResultSet
    public URL getURL(String str) throws SQLException {
        throw unsupported("url");
    }

    @Override // java.sql.ResultSet
    public void updateNull(int i) throws SQLException {
        try {
            debugCodeCall("updateNull", i);
            update(checkColumnIndex(i), ValueNull.INSTANCE);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNull(String str) throws SQLException {
        try {
            debugCodeCall("updateNull", str);
            update(getColumnIndex(str), ValueNull.INSTANCE);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBoolean(int i, boolean z) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBoolean(" + i + ", " + z + ')');
            }
            update(checkColumnIndex(i), ValueBoolean.get(z));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBoolean(String str, boolean z) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBoolean(" + quote(str) + ", " + z + ')');
            }
            update(getColumnIndex(str), ValueBoolean.get(z));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateByte(int i, byte b) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateByte(" + i + ", " + ((int) b) + ')');
            }
            update(checkColumnIndex(i), ValueTinyint.get(b));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateByte(String str, byte b) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateByte(" + quote(str) + ", " + ((int) b) + ')');
            }
            update(getColumnIndex(str), ValueTinyint.get(b));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBytes(int i, byte[] bArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBytes(" + i + ", x)");
            }
            update(checkColumnIndex(i), bArr == null ? ValueNull.INSTANCE : ValueVarbinary.get(bArr));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBytes(String str, byte[] bArr) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBytes(" + quote(str) + ", x)");
            }
            update(getColumnIndex(str), bArr == null ? ValueNull.INSTANCE : ValueVarbinary.get(bArr));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateShort(int i, short s) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateShort(" + i + ", (short) " + ((int) s) + ')');
            }
            update(checkColumnIndex(i), ValueSmallint.get(s));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateShort(String str, short s) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateShort(" + quote(str) + ", (short) " + ((int) s) + ')');
            }
            update(getColumnIndex(str), ValueSmallint.get(s));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateInt(int i, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateInt(" + i + ", " + i2 + ')');
            }
            update(checkColumnIndex(i), ValueInteger.get(i2));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateInt(String str, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateInt(" + quote(str) + ", " + i + ')');
            }
            update(getColumnIndex(str), ValueInteger.get(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateLong(int i, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateLong(" + i + ", " + j + "L)");
            }
            update(checkColumnIndex(i), ValueBigint.get(j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateLong(String str, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateLong(" + quote(str) + ", " + j + "L)");
            }
            update(getColumnIndex(str), ValueBigint.get(j));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateFloat(int i, float f) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateFloat(" + i + ", " + f + "f)");
            }
            update(checkColumnIndex(i), ValueReal.get(f));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateFloat(String str, float f) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateFloat(" + quote(str) + ", " + f + "f)");
            }
            update(getColumnIndex(str), ValueReal.get(f));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateDouble(int i, double d) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateDouble(" + i + ", " + d + "d)");
            }
            update(checkColumnIndex(i), ValueDouble.get(d));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateDouble(String str, double d) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateDouble(" + quote(str) + ", " + d + "d)");
            }
            update(getColumnIndex(str), ValueDouble.get(d));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBigDecimal(" + i + ", " + quoteBigDecimal(bigDecimal) + ')');
            }
            update(checkColumnIndex(i), bigDecimal == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(bigDecimal));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBigDecimal(" + quote(str) + ", " + quoteBigDecimal(bigDecimal) + ')');
            }
            update(getColumnIndex(str), bigDecimal == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(bigDecimal));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateString(int i, String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateString(" + i + ", " + quote(str) + ')');
            }
            update(checkColumnIndex(i), str == null ? ValueNull.INSTANCE : ValueVarchar.get(str, this.conn));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateString(String str, String str2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateString(" + quote(str) + ", " + quote(str2) + ')');
            }
            update(getColumnIndex(str), str2 == null ? ValueNull.INSTANCE : ValueVarchar.get(str2, this.conn));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateDate(int i, Date date) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateDate(" + i + ", " + quoteDate(date) + ')');
            }
            update(checkColumnIndex(i), date == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(this.conn, null, date));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateDate(String str, Date date) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateDate(" + quote(str) + ", " + quoteDate(date) + ')');
            }
            update(getColumnIndex(str), date == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(this.conn, null, date));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateTime(int i, Time time) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateTime(" + i + ", " + quoteTime(time) + ')');
            }
            update(checkColumnIndex(i), time == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(this.conn, null, time));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateTime(String str, Time time) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateTime(" + quote(str) + ", " + quoteTime(time) + ')');
            }
            update(getColumnIndex(str), time == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(this.conn, null, time));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateTimestamp(" + i + ", " + quoteTimestamp(timestamp) + ')');
            }
            update(checkColumnIndex(i), timestamp == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(this.conn, (TimeZone) null, timestamp));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateTimestamp(String str, Timestamp timestamp) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateTimestamp(" + quote(str) + ", " + quoteTimestamp(timestamp) + ')');
            }
            update(getColumnIndex(str), timestamp == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(this.conn, (TimeZone) null, timestamp));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateAsciiStream(" + i + ", x, " + i2 + ')');
            }
            updateAscii(checkColumnIndex(i), inputStream, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateAsciiStream(" + i + ", x)");
            }
            updateAscii(checkColumnIndex(i), inputStream, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int i, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateAsciiStream(" + i + ", x, " + j + "L)");
            }
            updateAscii(checkColumnIndex(i), inputStream, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateAsciiStream(" + quote(str) + ", x, " + i + ')');
            }
            updateAscii(getColumnIndex(str), inputStream, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String str, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateAsciiStream(" + quote(str) + ", x)");
            }
            updateAscii(getColumnIndex(str), inputStream, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String str, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateAsciiStream(" + quote(str) + ", x, " + j + "L)");
            }
            updateAscii(getColumnIndex(str), inputStream, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateAscii(int i, InputStream inputStream, long j) {
        update(i, this.conn.createClob(IOUtils.getAsciiReader(inputStream), j));
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBinaryStream(" + i + ", x, " + i2 + ')');
            }
            updateBlobImpl(checkColumnIndex(i), inputStream, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBinaryStream(" + i + ", x)");
            }
            updateBlobImpl(checkColumnIndex(i), inputStream, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int i, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBinaryStream(" + i + ", x, " + j + "L)");
            }
            updateBlobImpl(checkColumnIndex(i), inputStream, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String str, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBinaryStream(" + quote(str) + ", x)");
            }
            updateBlobImpl(getColumnIndex(str), inputStream, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBinaryStream(" + quote(str) + ", x, " + i + ')');
            }
            updateBlobImpl(getColumnIndex(str), inputStream, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String str, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBinaryStream(" + quote(str) + ", x, " + j + "L)");
            }
            updateBlobImpl(getColumnIndex(str), inputStream, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateCharacterStream(" + i + ", x, " + j + "L)");
            }
            updateClobImpl(checkColumnIndex(i), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateCharacterStream(" + i + ", x, " + i2 + ')');
            }
            updateClobImpl(checkColumnIndex(i), reader, i2);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateCharacterStream(" + i + ", x)");
            }
            updateClobImpl(checkColumnIndex(i), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String str, Reader reader, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateCharacterStream(" + quote(str) + ", x, " + i + ')');
            }
            updateClobImpl(getColumnIndex(str), reader, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String str, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateCharacterStream(" + quote(str) + ", x)");
            }
            updateClobImpl(getColumnIndex(str), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String str, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateCharacterStream(" + quote(str) + ", x, " + j + "L)");
            }
            updateClobImpl(getColumnIndex(str), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateObject(int i, Object obj, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + i + ", x, " + i2 + ')');
            }
            update(checkColumnIndex(i), convertToUnknownValue(obj));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateObject(String str, Object obj, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + quote(str) + ", x, " + i + ')');
            }
            update(getColumnIndex(str), convertToUnknownValue(obj));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateObject(int i, Object obj) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + i + ", x)");
            }
            update(checkColumnIndex(i), convertToUnknownValue(obj));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateObject(String str, Object obj) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + quote(str) + ", x)");
            }
            update(getColumnIndex(str), convertToUnknownValue(obj));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void updateObject(int i, Object obj, SQLType sQLType) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + i + ", x, " + DataType.sqlTypeToString(sQLType) + ')');
            }
            update(checkColumnIndex(i), convertToValue(obj, sQLType));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void updateObject(int i, Object obj, SQLType sQLType, int i2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + i + ", x, " + DataType.sqlTypeToString(sQLType) + ", " + i2 + ')');
            }
            update(checkColumnIndex(i), convertToValue(obj, sQLType));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void updateObject(String str, Object obj, SQLType sQLType) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + quote(str) + ", x, " + DataType.sqlTypeToString(sQLType) + ')');
            }
            update(getColumnIndex(str), convertToValue(obj, sQLType));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public void updateObject(String str, Object obj, SQLType sQLType, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateObject(" + quote(str) + ", x, " + DataType.sqlTypeToString(sQLType) + ", " + i + ')');
            }
            update(getColumnIndex(str), convertToValue(obj, sQLType));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateRef(int i, Ref ref) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.ResultSet
    public void updateRef(String str, Ref ref) throws SQLException {
        throw unsupported("ref");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBlob(" + i + ", (InputStream) x)");
            }
            updateBlobImpl(checkColumnIndex(i), inputStream, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int i, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBlob(" + i + ", (InputStream) x, " + j + "L)");
            }
            updateBlobImpl(checkColumnIndex(i), inputStream, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int i, Blob blob) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBlob(" + i + ", (Blob) x)");
            }
            updateBlobImpl(checkColumnIndex(i), blob, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String str, Blob blob) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBlob(" + quote(str) + ", (Blob) x)");
            }
            updateBlobImpl(getColumnIndex(str), blob, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateBlobImpl(int i, Blob blob, long j) throws SQLException {
        update(i, blob == null ? ValueNull.INSTANCE : this.conn.createBlob(blob.getBinaryStream(), j));
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String str, InputStream inputStream) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBlob(" + quote(str) + ", (InputStream) x)");
            }
            updateBlobImpl(getColumnIndex(str), inputStream, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String str, InputStream inputStream, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateBlob(" + quote(str) + ", (InputStream) x, " + j + "L)");
            }
            updateBlobImpl(getColumnIndex(str), inputStream, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateBlobImpl(int i, InputStream inputStream, long j) {
        update(i, this.conn.createBlob(inputStream, j));
    }

    @Override // java.sql.ResultSet
    public void updateClob(int i, Clob clob) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateClob(" + i + ", (Clob) x)");
            }
            updateClobImpl(checkColumnIndex(i), clob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateClob(int i, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateClob(" + i + ", (Reader) x)");
            }
            updateClobImpl(checkColumnIndex(i), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateClob(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateClob(" + i + ", (Reader) x, " + j + "L)");
            }
            updateClobImpl(checkColumnIndex(i), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateClob(String str, Clob clob) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateClob(" + quote(str) + ", (Clob) x)");
            }
            updateClobImpl(getColumnIndex(str), clob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateClob(String str, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateClob(" + quote(str) + ", (Reader) x)");
            }
            updateClobImpl(getColumnIndex(str), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateClob(String str, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateClob(" + quote(str) + ", (Reader) x, " + j + "L)");
            }
            updateClobImpl(getColumnIndex(str), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateArray(int i, Array array) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateArray(" + i + ", x)");
            }
            updateArrayImpl(checkColumnIndex(i), array);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateArray(String str, Array array) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateArray(" + quote(str) + ", x)");
            }
            updateArrayImpl(getColumnIndex(str), array);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateArrayImpl(int i, Array array) throws SQLException {
        update(i, array == null ? ValueNull.INSTANCE : ValueToObjectConverter.objectToValue(this.stat.session, array.getArray(), 40));
    }

    @Override // java.sql.ResultSet
    public String getCursorName() throws SQLException {
        throw unsupported("cursorName");
    }

    @Override // java.sql.ResultSet
    public int getRow() throws SQLException {
        try {
            debugCodeCall("getRow");
            checkClosed();
            if (this.result.isAfterLast()) {
                return 0;
            }
            long rowId = this.result.getRowId() + 1;
            if (rowId <= 2147483647L) {
                return (int) rowId;
            }
            return -2;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public int getConcurrency() throws SQLException {
        try {
            debugCodeCall("getConcurrency");
            checkClosed();
            if (this.updatable) {
                return new UpdatableRow(this.conn, this.result).isUpdatable() ? 1008 : 1007;
            }
            return 1007;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public int getFetchDirection() throws SQLException {
        try {
            debugCodeCall("getFetchDirection");
            checkClosed();
            return 1000;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public int getFetchSize() throws SQLException {
        try {
            debugCodeCall("getFetchSize");
            checkClosed();
            return this.result.getFetchSize();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void setFetchSize(int i) throws SQLException {
        try {
            debugCodeCall("setFetchSize", i);
            checkClosed();
            if (i < 0) {
                throw DbException.getInvalidValueException(TextareaTag.ROWS_ATTRIBUTE, Integer.valueOf(i));
            }
            if (i > 0) {
                if (this.stat != null) {
                    int maxRows = this.stat.getMaxRows();
                    if (maxRows > 0 && i > maxRows) {
                        throw DbException.getInvalidValueException(TextareaTag.ROWS_ATTRIBUTE, Integer.valueOf(i));
                    }
                }
            } else {
                i = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
            }
            this.result.setFetchSize(i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void setFetchDirection(int i) throws SQLException {
        debugCodeCall("setFetchDirection", i);
        if (i != 1000) {
            throw unsupported("setFetchDirection");
        }
    }

    @Override // java.sql.ResultSet
    public int getType() throws SQLException {
        try {
            debugCodeCall("getType");
            checkClosed();
            if (this.stat == null) {
                return 1003;
            }
            return this.stat.resultSetType;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean isBeforeFirst() throws SQLException {
        try {
            debugCodeCall("isBeforeFirst");
            checkClosed();
            if (this.result.getRowId() < 0) {
                if (this.result.hasNext()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean isAfterLast() throws SQLException {
        try {
            debugCodeCall("isAfterLast");
            checkClosed();
            if (this.result.getRowId() > 0) {
                if (this.result.isAfterLast()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean isFirst() throws SQLException {
        try {
            debugCodeCall("isFirst");
            checkClosed();
            if (this.result.getRowId() == 0) {
                if (!this.result.isAfterLast()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean isLast() throws SQLException {
        try {
            debugCodeCall("isLast");
            checkClosed();
            if (this.result.getRowId() >= 0 && !this.result.isAfterLast()) {
                if (!this.result.hasNext()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void beforeFirst() throws SQLException {
        try {
            debugCodeCall("beforeFirst");
            checkClosed();
            if (this.result.getRowId() >= 0) {
                resetResult();
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void afterLast() throws SQLException {
        try {
            debugCodeCall("afterLast");
            checkClosed();
            do {
            } while (nextRow());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean first() throws SQLException {
        try {
            debugCodeCall("first");
            checkClosed();
            if (this.result.getRowId() >= 0) {
                resetResult();
            }
            return nextRow();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean last() throws SQLException {
        try {
            debugCodeCall("last");
            checkClosed();
            if (this.result.isAfterLast()) {
                resetResult();
            }
            while (this.result.hasNext()) {
                nextRow();
            }
            return isOnValidRow();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean absolute(int i) throws SQLException {
        try {
            debugCodeCall("absolute", i);
            checkClosed();
            long rowCount = (i >= 0 ? i : (this.result.getRowCount() + i) + 1) - 1;
            if (rowCount < this.result.getRowId()) {
                resetResult();
            }
            while (this.result.getRowId() < rowCount) {
                if (!nextRow()) {
                    return false;
                }
            }
            return isOnValidRow();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean relative(int i) throws SQLException {
        long j;
        try {
            debugCodeCall("relative", i);
            checkClosed();
            if (i < 0) {
                j = this.result.getRowId() + i + 1;
                resetResult();
            } else {
                j = i;
            }
            do {
                long j2 = j;
                j = j2 - 1;
                if (j2 <= 0) {
                    return isOnValidRow();
                }
            } while (nextRow());
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean previous() throws SQLException {
        try {
            debugCodeCall("previous");
            checkClosed();
            return relative(-1);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void moveToInsertRow() throws SQLException {
        try {
            debugCodeCall("moveToInsertRow");
            checkUpdatable();
            this.insertRow = new Value[this.columnCount];
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void moveToCurrentRow() throws SQLException {
        try {
            debugCodeCall("moveToCurrentRow");
            checkUpdatable();
            this.insertRow = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean rowUpdated() throws SQLException {
        try {
            debugCodeCall("rowUpdated");
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean rowInserted() throws SQLException {
        try {
            debugCodeCall("rowInserted");
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean rowDeleted() throws SQLException {
        try {
            debugCodeCall("rowDeleted");
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void insertRow() throws SQLException {
        try {
            debugCodeCall("insertRow");
            checkUpdatable();
            if (this.insertRow == null) {
                throw DbException.get(ErrorCode.NOT_ON_UPDATABLE_ROW);
            }
            getUpdatableRow().insertRow(this.insertRow);
            this.insertRow = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateRow() throws SQLException {
        try {
            debugCodeCall("updateRow");
            checkUpdatable();
            if (this.insertRow != null) {
                throw DbException.get(ErrorCode.NOT_ON_UPDATABLE_ROW);
            }
            checkOnValidRow();
            if (this.updateRow != null) {
                UpdatableRow updatableRow = getUpdatableRow();
                Value[] valueArr = new Value[this.columnCount];
                for (int i = 0; i < this.updateRow.length; i++) {
                    valueArr[i] = getInternal(checkColumnIndex(i + 1));
                }
                updatableRow.updateRow(valueArr, this.updateRow);
                for (int i2 = 0; i2 < this.updateRow.length; i2++) {
                    if (this.updateRow[i2] == null) {
                        this.updateRow[i2] = valueArr[i2];
                    }
                }
                patchCurrentRow(updatableRow.readRow(this.updateRow));
                this.updateRow = null;
            }
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void deleteRow() throws SQLException {
        try {
            debugCodeCall("deleteRow");
            checkUpdatable();
            if (this.insertRow != null) {
                throw DbException.get(ErrorCode.NOT_ON_UPDATABLE_ROW);
            }
            checkOnValidRow();
            getUpdatableRow().deleteRow(this.result.currentRow());
            this.updateRow = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void refreshRow() throws SQLException {
        try {
            debugCodeCall("refreshRow");
            checkClosed();
            if (this.insertRow != null) {
                throw DbException.get(2000);
            }
            checkOnValidRow();
            patchCurrentRow(getUpdatableRow().readRow(this.result.currentRow()));
            this.updateRow = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void cancelRowUpdates() throws SQLException {
        try {
            debugCodeCall("cancelRowUpdates");
            checkClosed();
            if (this.insertRow != null) {
                throw DbException.get(2000);
            }
            this.updateRow = null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private UpdatableRow getUpdatableRow() throws SQLException {
        UpdatableRow updatableRow = new UpdatableRow(this.conn, this.result);
        if (!updatableRow.isUpdatable()) {
            throw DbException.get(ErrorCode.RESULT_SET_NOT_UPDATABLE);
        }
        return updatableRow;
    }

    private int getColumnIndex(String str) {
        checkClosed();
        if (str == null) {
            throw DbException.getInvalidValueException("columnLabel", null);
        }
        if (this.columnCount >= 3) {
            if (this.columnLabelMap == null) {
                HashMap<String, Integer> hashMap = new HashMap<>();
                for (int i = 0; i < this.columnCount; i++) {
                    hashMap.putIfAbsent(StringUtils.toUpperEnglish(this.result.getAlias(i)), Integer.valueOf(i));
                }
                for (int i2 = 0; i2 < this.columnCount; i2++) {
                    String columnName = this.result.getColumnName(i2);
                    if (columnName != null) {
                        String upperEnglish = StringUtils.toUpperEnglish(columnName);
                        hashMap.putIfAbsent(upperEnglish, Integer.valueOf(i2));
                        String tableName = this.result.getTableName(i2);
                        if (tableName != null) {
                            hashMap.putIfAbsent(StringUtils.toUpperEnglish(tableName) + '.' + upperEnglish, Integer.valueOf(i2));
                        }
                    }
                }
                this.columnLabelMap = hashMap;
                if (this.preparedStatement != null) {
                    this.preparedStatement.setCachedColumnLabelMap(this.columnLabelMap);
                }
            }
            Integer num = this.columnLabelMap.get(StringUtils.toUpperEnglish(str));
            if (num == null) {
                throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
            }
            return num.intValue() + 1;
        }
        for (int i3 = 0; i3 < this.columnCount; i3++) {
            if (str.equalsIgnoreCase(this.result.getAlias(i3))) {
                return i3 + 1;
            }
        }
        int indexOf = str.indexOf(46);
        if (indexOf > 0) {
            String substring = str.substring(0, indexOf);
            String substring2 = str.substring(indexOf + 1);
            for (int i4 = 0; i4 < this.columnCount; i4++) {
                if (substring.equalsIgnoreCase(this.result.getTableName(i4)) && substring2.equalsIgnoreCase(this.result.getColumnName(i4))) {
                    return i4 + 1;
                }
            }
        } else {
            for (int i5 = 0; i5 < this.columnCount; i5++) {
                if (str.equalsIgnoreCase(this.result.getColumnName(i5))) {
                    return i5 + 1;
                }
            }
        }
        throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
    }

    private int checkColumnIndex(int i) {
        checkClosed();
        if (i < 1 || i > this.columnCount) {
            throw DbException.getInvalidValueException("columnIndex", Integer.valueOf(i));
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkClosed() {
        if (this.result == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
        if (this.stat != null) {
            this.stat.checkClosed();
        }
        if (this.conn != null) {
            this.conn.checkClosed();
        }
    }

    private boolean isOnValidRow() {
        return this.result.getRowId() >= 0 && !this.result.isAfterLast();
    }

    private void checkOnValidRow() {
        if (!isOnValidRow()) {
            throw DbException.get(2000);
        }
    }

    private Value get(int i) {
        Value internal = getInternal(i);
        this.wasNull = internal == ValueNull.INSTANCE;
        return internal;
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x0023, code lost:            if (r0 == null) goto L6;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.value.Value getInternal(int r5) {
        /*
            r4 = this;
            r0 = r4
            r0.checkOnValidRow()
            r0 = r4
            java.util.HashMap<java.lang.Long, org.h2.value.Value[]> r0 = r0.patchedRows
            if (r0 == 0) goto L26
            r0 = r4
            java.util.HashMap<java.lang.Long, org.h2.value.Value[]> r0 = r0.patchedRows
            r1 = r4
            org.h2.result.ResultInterface r1 = r1.result
            long r1 = r1.getRowId()
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r1)
            org.h2.value.Value[] r0 = (org.h2.value.Value[]) r0
            r1 = r0
            r6 = r1
            if (r0 != 0) goto L30
        L26:
            r0 = r4
            org.h2.result.ResultInterface r0 = r0.result
            org.h2.value.Value[] r0 = r0.currentRow()
            r6 = r0
        L30:
            r0 = r6
            r1 = r5
            r2 = 1
            int r1 = r1 - r2
            r0 = r0[r1]
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.jdbc.JdbcResultSet.getInternal(int):org.h2.value.Value");
    }

    private void update(int i, Value value) {
        if (!this.triggerUpdatable) {
            checkUpdatable();
        }
        if (this.insertRow != null) {
            this.insertRow[i - 1] = value;
            return;
        }
        if (this.updateRow == null) {
            this.updateRow = new Value[this.columnCount];
        }
        this.updateRow[i - 1] = value;
    }

    private boolean nextRow() {
        boolean nextLazyRow = this.result.isLazy() ? nextLazyRow() : this.result.next();
        if (!nextLazyRow && !this.scrollable) {
            this.result.close();
        }
        return nextLazyRow;
    }

    private boolean nextLazyRow() {
        Session session;
        if (this.stat.isCancelled() || this.conn == null || (session = this.conn.getSession()) == null) {
            throw DbException.get(ErrorCode.STATEMENT_WAS_CANCELED);
        }
        Session threadLocalSession = session.setThreadLocalSession();
        try {
            boolean next = this.result.next();
            session.resetThreadLocalSession(threadLocalSession);
            return next;
        } catch (Throwable th) {
            session.resetThreadLocalSession(threadLocalSession);
            throw th;
        }
    }

    private void resetResult() {
        if (!this.scrollable) {
            throw DbException.get(ErrorCode.RESULT_SET_NOT_SCROLLABLE);
        }
        this.result.reset();
    }

    @Override // java.sql.ResultSet
    public RowId getRowId(int i) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.ResultSet
    public RowId getRowId(String str) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.ResultSet
    public void updateRowId(int i, RowId rowId) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.ResultSet
    public void updateRowId(String str, RowId rowId) throws SQLException {
        throw unsupported("rowId");
    }

    @Override // java.sql.ResultSet
    public int getHoldability() throws SQLException {
        try {
            debugCodeCall("getHoldability");
            checkClosed();
            return this.conn.getHoldability();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public boolean isClosed() throws SQLException {
        try {
            debugCodeCall("isClosed");
            return this.result == null;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNString(int i, String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNString(" + i + ", " + quote(str) + ')');
            }
            update(checkColumnIndex(i), str == null ? ValueNull.INSTANCE : ValueVarchar.get(str, this.conn));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNString(String str, String str2) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNString(" + quote(str) + ", " + quote(str2) + ')');
            }
            update(getColumnIndex(str), str2 == null ? ValueNull.INSTANCE : ValueVarchar.get(str2, this.conn));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int i, NClob nClob) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNClob(" + i + ", (NClob) x)");
            }
            updateClobImpl(checkColumnIndex(i), nClob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int i, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNClob(" + i + ", (Reader) x)");
            }
            updateClobImpl(checkColumnIndex(i), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNClob(" + i + ", (Reader) x, " + j + "L)");
            }
            updateClobImpl(checkColumnIndex(i), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String str, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNClob(" + quote(str) + ", (Reader) x)");
            }
            updateClobImpl(getColumnIndex(str), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String str, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNClob(" + quote(str) + ", (Reader) x, " + j + "L)");
            }
            updateClobImpl(getColumnIndex(str), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String str, NClob nClob) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNClob(" + quote(str) + ", (NClob) x)");
            }
            updateClobImpl(getColumnIndex(str), nClob);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateClobImpl(int i, Clob clob) throws SQLException {
        update(i, clob == null ? ValueNull.INSTANCE : this.conn.createClob(clob.getCharacterStream(), -1L));
    }

    @Override // java.sql.ResultSet
    public NClob getNClob(int i) throws SQLException {
        try {
            int nextId = getNextId(10);
            if (isDebugEnabled()) {
                debugCodeAssign("NClob", 10, nextId, "getNClob(" + i + ')');
            }
            return getClob(nextId, checkColumnIndex(i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public NClob getNClob(String str) throws SQLException {
        try {
            int nextId = getNextId(10);
            if (isDebugEnabled()) {
                debugCodeAssign("NClob", 10, nextId, "getNClob(" + quote(str) + ')');
            }
            return getClob(nextId, getColumnIndex(str));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private JdbcClob getClob(int i, int i2) {
        JdbcClob jdbcClob;
        Value internal = getInternal(i2);
        if (internal != ValueNull.INSTANCE) {
            this.wasNull = false;
            jdbcClob = new JdbcClob(this.conn, internal, JdbcLob.State.WITH_VALUE, i);
        } else {
            this.wasNull = true;
            jdbcClob = null;
        }
        return jdbcClob;
    }

    @Override // java.sql.ResultSet
    public SQLXML getSQLXML(int i) throws SQLException {
        try {
            int nextId = getNextId(17);
            if (isDebugEnabled()) {
                debugCodeAssign("SQLXML", 17, nextId, "getSQLXML(" + i + ')');
            }
            Value value = get(checkColumnIndex(i));
            if (value == ValueNull.INSTANCE) {
                return null;
            }
            return new JdbcSQLXML(this.conn, value, JdbcLob.State.WITH_VALUE, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public SQLXML getSQLXML(String str) throws SQLException {
        try {
            int nextId = getNextId(17);
            if (isDebugEnabled()) {
                debugCodeAssign("SQLXML", 17, nextId, "getSQLXML(" + quote(str) + ')');
            }
            Value value = get(getColumnIndex(str));
            if (value == ValueNull.INSTANCE) {
                return null;
            }
            return new JdbcSQLXML(this.conn, value, JdbcLob.State.WITH_VALUE, nextId);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateSQLXML(" + i + ", x)");
            }
            updateSQLXMLImpl(checkColumnIndex(i), sqlxml);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateSQLXML(String str, SQLXML sqlxml) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateSQLXML(" + quote(str) + ", x)");
            }
            updateSQLXMLImpl(getColumnIndex(str), sqlxml);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateSQLXMLImpl(int i, SQLXML sqlxml) throws SQLException {
        update(i, sqlxml == null ? ValueNull.INSTANCE : this.conn.createClob(sqlxml.getCharacterStream(), -1L));
    }

    @Override // java.sql.ResultSet
    public String getNString(int i) throws SQLException {
        try {
            debugCodeCall("getNString", i);
            return get(checkColumnIndex(i)).getString();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public String getNString(String str) throws SQLException {
        try {
            debugCodeCall("getNString", str);
            return get(getColumnIndex(str)).getString();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Reader getNCharacterStream(int i) throws SQLException {
        try {
            debugCodeCall("getNCharacterStream", i);
            return get(checkColumnIndex(i)).getReader();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public Reader getNCharacterStream(String str) throws SQLException {
        try {
            debugCodeCall("getNCharacterStream", str);
            return get(getColumnIndex(str)).getReader();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNCharacterStream(" + i + ", x)");
            }
            updateClobImpl(checkColumnIndex(i), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNCharacterStream(" + i + ", x, " + j + "L)");
            }
            updateClobImpl(checkColumnIndex(i), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(String str, Reader reader) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNCharacterStream(" + quote(str) + ", x)");
            }
            updateClobImpl(getColumnIndex(str), reader, -1L);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(String str, Reader reader, long j) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("updateNCharacterStream(" + quote(str) + ", x, " + j + "L)");
            }
            updateClobImpl(getColumnIndex(str), reader, j);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void updateClobImpl(int i, Reader reader, long j) {
        update(i, this.conn.createClob(reader, j));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        try {
            if (isWrapperFor(cls)) {
                return this;
            }
            throw DbException.getInvalidValueException("iface", cls);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        try {
            if (cls == null) {
                throw DbException.getInvalidValueException("type", cls);
            }
            debugCodeCall("getObject", i);
            return (T) ValueToObjectConverter.valueToObject(cls, get(checkColumnIndex(i)), this.conn);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        try {
            if (cls == null) {
                throw DbException.getInvalidValueException("type", cls);
            }
            debugCodeCall("getObject", str);
            return (T) ValueToObjectConverter.valueToObject(cls, get(getColumnIndex(str)), this.conn);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    public String toString() {
        return getTraceObjectName() + ": " + this.result;
    }

    private void patchCurrentRow(Value[] valueArr) {
        boolean z = false;
        Value[] currentRow = this.result.currentRow();
        CompareMode compareMode = this.conn.getCompareMode();
        int i = 0;
        while (true) {
            if (i >= valueArr.length) {
                break;
            }
            if (valueArr[i].compareTo(currentRow[i], this.conn, compareMode) == 0) {
                i++;
            } else {
                z = true;
                break;
            }
        }
        if (this.patchedRows == null) {
            this.patchedRows = new HashMap<>();
        }
        Long valueOf = Long.valueOf(this.result.getRowId());
        if (!z) {
            this.patchedRows.remove(valueOf);
        } else {
            this.patchedRows.put(valueOf, valueArr);
        }
    }

    private Value convertToValue(Object obj, SQLType sQLType) {
        if (obj == null) {
            return ValueNull.INSTANCE;
        }
        int convertSQLTypeToValueType = DataType.convertSQLTypeToValueType(sQLType);
        return ValueToObjectConverter.objectToValue(this.conn.getSession(), obj, convertSQLTypeToValueType).convertTo(convertSQLTypeToValueType, this.conn);
    }

    private Value convertToUnknownValue(Object obj) {
        return ValueToObjectConverter.objectToValue(this.conn.getSession(), obj, -1);
    }

    private void checkUpdatable() {
        checkClosed();
        if (!this.updatable) {
            throw DbException.get(ErrorCode.RESULT_SET_READONLY);
        }
    }

    public Value[] getUpdateRow() {
        return this.updateRow;
    }

    public ResultInterface getResult() {
        return this.result;
    }
}

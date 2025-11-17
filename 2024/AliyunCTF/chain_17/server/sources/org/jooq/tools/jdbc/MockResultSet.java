package org.jooq.tools.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
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
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.Converters;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.Internal;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockResultSet.class */
public class MockResultSet extends JDBC41ResultSet implements ResultSet, Serializable {
    private final int maxRows;
    Result<?> result;
    private final int size;
    private transient int index;
    private transient Record record;
    private transient boolean wasNull;
    private final Converter<?, ?>[] converters;

    public MockResultSet(Result<?> result) {
        this(result, 0);
    }

    public MockResultSet(Result<?> result, int maxRows) {
        this.result = result;
        this.maxRows = maxRows;
        if (result != null) {
            this.size = result.size();
            int l = result.fieldsRow().size();
            this.converters = new Converter[l];
            for (int i = 0; i < l; i++) {
                this.converters[i] = Converters.inverse((ContextConverter) result.field(i).getConverter());
            }
            return;
        }
        this.size = 0;
        this.converters = new Converter[0];
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return this;
        }
        throw new SQLException("MockResultSet does not implement " + String.valueOf(iface));
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    private int size() {
        if (this.maxRows == 0) {
            return this.size;
        }
        return Math.min(this.maxRows, this.size);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void checkNotClosed() throws SQLException {
        if (this.result == null) {
            throw new SQLException("ResultSet is already closed");
        }
    }

    private void checkInRange() throws SQLException {
        checkNotClosed();
        if (this.index <= 0 || this.index > this.size) {
            throw new SQLException("ResultSet index is at an illegal position : " + this.index);
        }
    }

    private Field<?> field(String columnLabel) throws SQLException {
        Field<?> field = this.result.field(columnLabel);
        if (field == null) {
            throw new SQLException("Unknown column label : " + columnLabel);
        }
        return field;
    }

    private Converter<?, ?> converter(int columnIndex) throws SQLException {
        if (columnIndex > 0 && columnIndex <= this.converters.length) {
            return this.converters[columnIndex - 1];
        }
        throw new SQLException("Unknown column index : " + columnIndex);
    }

    private long getMillis(Calendar cal, int year, int month, int day, int hour, int minute, int second, int millis) {
        Calendar cal2 = (Calendar) cal.clone();
        cal2.clear();
        cal2.setLenient(true);
        if (year <= 0) {
            cal2.set(0, 0);
            cal2.set(1, 1 - year);
        } else {
            cal2.set(0, 1);
            cal2.set(1, year);
        }
        cal2.set(2, month);
        cal2.set(5, day);
        cal2.set(11, hour);
        cal2.set(12, minute);
        cal2.set(13, second);
        cal2.set(14, millis);
        return cal2.getTimeInMillis();
    }

    private Timestamp withTZ(Timestamp timestamp, Calendar cal) {
        if (timestamp == null) {
            return null;
        }
        int year = timestamp.getYear() + 1900;
        int month = timestamp.getMonth();
        int day = timestamp.getDate();
        int hour = timestamp.getHours();
        int minute = timestamp.getMinutes();
        int second = timestamp.getSeconds();
        int nanos = timestamp.getNanos();
        int millis = nanos / 1000000;
        int nanos2 = nanos - (millis * 1000000);
        Timestamp r = new Timestamp(getMillis(cal, year, month, day, hour, minute, second, millis));
        r.setNanos(nanos2 + (millis * 1000000));
        return r;
    }

    private Time withTZ(Time time, Calendar cal) {
        if (time == null) {
            return null;
        }
        int hour = time.getHours();
        int minute = time.getMinutes();
        int second = time.getSeconds();
        int millis = (int) (time.getTime() % 1000);
        return new Time(getMillis(cal, 1970, 0, 1, hour, minute, second, millis));
    }

    private Date withTZ(Date date, Calendar cal) {
        if (date == null) {
            return null;
        }
        int year = date.getYear() + 1900;
        int month = date.getMonth();
        int day = date.getDate();
        return new Date(getMillis(cal, year, month, day, 0, 0, 0, 0));
    }

    @Override // java.sql.ResultSet
    public boolean next() throws SQLException {
        return relative(1);
    }

    @Override // java.sql.ResultSet
    public boolean previous() throws SQLException {
        return relative(-1);
    }

    @Override // java.sql.ResultSet
    public boolean absolute(int row) throws SQLException {
        if (size() > 0) {
            if (row > 0) {
                if (row <= size()) {
                    index(row);
                    return true;
                }
                afterLast();
                return false;
            }
            if (row == 0) {
                beforeFirst();
                return false;
            }
            if ((-row) <= size()) {
                index(size() + 1 + row);
                return true;
            }
            beforeFirst();
            return false;
        }
        return false;
    }

    @Override // java.sql.ResultSet
    public boolean relative(int rows) throws SQLException {
        checkNotClosed();
        return index(this.index + rows);
    }

    @Override // java.sql.ResultSet
    public int getRow() throws SQLException {
        if (this.index > size()) {
            return 0;
        }
        return this.index;
    }

    @Override // java.sql.ResultSet
    public void beforeFirst() throws SQLException {
        checkNotClosed();
        index(0);
    }

    @Override // java.sql.ResultSet
    public void afterLast() throws SQLException {
        checkNotClosed();
        index(size() + 1);
    }

    @Override // java.sql.ResultSet
    public boolean first() throws SQLException {
        return absolute(1);
    }

    @Override // java.sql.ResultSet
    public boolean last() throws SQLException {
        checkNotClosed();
        return absolute(size());
    }

    @Override // java.sql.ResultSet
    public boolean isFirst() throws SQLException {
        checkNotClosed();
        return size() > 0 && this.index == 1;
    }

    @Override // java.sql.ResultSet
    public boolean isBeforeFirst() throws SQLException {
        checkNotClosed();
        return size() > 0 && this.index == 0;
    }

    @Override // java.sql.ResultSet
    public boolean isLast() throws SQLException {
        checkNotClosed();
        return size() > 0 && this.index == size();
    }

    @Override // java.sql.ResultSet
    public boolean isAfterLast() throws SQLException {
        checkNotClosed();
        return size() > 0 && this.index > size();
    }

    @Override // java.sql.ResultSet, java.lang.AutoCloseable
    public void close() throws SQLException {
        this.result = null;
        this.index = 0;
    }

    @Override // java.sql.ResultSet
    public boolean isClosed() throws SQLException {
        return this.result == null;
    }

    @Override // java.sql.ResultSet
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override // java.sql.ResultSet
    public void clearWarnings() throws SQLException {
    }

    @Override // java.sql.ResultSet
    public String getCursorName() throws SQLException {
        throw new SQLFeatureNotSupportedException("jOOQ ResultSets don't have a cursor name");
    }

    @Override // java.sql.ResultSet
    public int findColumn(String columnLabel) throws SQLException {
        checkNotClosed();
        Field<?> field = this.result.field(columnLabel);
        if (field == null) {
            throw new SQLException("No such column : " + columnLabel);
        }
        return this.result.fieldsRow().indexOf(field) + 1;
    }

    @Override // java.sql.ResultSet
    public void setFetchDirection(int direction) throws SQLException {
        if (direction != 1000) {
            throw new SQLException("Fetch direction can only be FETCH_FORWARD");
        }
    }

    @Override // java.sql.ResultSet
    public int getFetchDirection() throws SQLException {
        return 1000;
    }

    @Override // java.sql.ResultSet
    public void setFetchSize(int rows) throws SQLException {
    }

    @Override // java.sql.ResultSet
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override // java.sql.ResultSet
    public int getType() throws SQLException {
        return 1004;
    }

    @Override // java.sql.ResultSet
    public int getConcurrency() throws SQLException {
        return 1007;
    }

    @Override // java.sql.ResultSet
    public int getHoldability() throws SQLException {
        return 2;
    }

    @Override // java.sql.ResultSet
    public boolean wasNull() throws SQLException {
        checkNotClosed();
        return this.wasNull;
    }

    @Override // java.sql.ResultSet
    public ResultSetMetaData getMetaData() throws SQLException {
        return new MockResultSetMetaData(this);
    }

    @Override // java.sql.ResultSet
    public Statement getStatement() throws SQLException {
        return null;
    }

    private <T> T get(String str, Class<T> cls) throws SQLException {
        checkInRange();
        return (T) get0(this.record.get(str, Converters.inverse((ContextConverter) field(str).getConverter())), cls);
    }

    private <T> T get(int i, Class<T> cls) throws SQLException {
        checkInRange();
        return (T) get0(this.record.get(i - 1, converter(i)), cls);
    }

    private <T> T get0(Object obj, Class<T> cls) {
        Converter provide = (this.record.configuration() == null ? new DefaultConfiguration() : this.record.configuration()).converterProvider().provide(obj == null ? Object.class : obj.getClass(), cls);
        T t = (T) (provide == null ? null : ContextConverter.scoped(provide).from(obj, Internal.converterContext()));
        this.wasNull = t == null;
        return t;
    }

    private boolean index(int newIndex) {
        int s = size();
        this.index = Math.min(Math.max(newIndex, 0), s + 1);
        boolean inRange = this.index > 0 && this.index <= s;
        this.record = inRange ? (Record) this.result.get(this.index - 1) : null;
        return inRange;
    }

    @Override // java.sql.ResultSet
    public String getString(int columnIndex) throws SQLException {
        return (String) get(columnIndex, String.class);
    }

    @Override // java.sql.ResultSet
    public String getString(String columnLabel) throws SQLException {
        return (String) get(columnLabel, String.class);
    }

    @Override // java.sql.ResultSet
    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    @Override // java.sql.ResultSet
    public String getNString(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    @Override // java.sql.ResultSet
    public boolean getBoolean(int columnIndex) throws SQLException {
        Boolean value = (Boolean) get(columnIndex, Boolean.class);
        if (this.wasNull) {
            return false;
        }
        return value.booleanValue();
    }

    @Override // java.sql.ResultSet
    public boolean getBoolean(String columnLabel) throws SQLException {
        Boolean value = (Boolean) get(columnLabel, Boolean.class);
        if (this.wasNull) {
            return false;
        }
        return value.booleanValue();
    }

    @Override // java.sql.ResultSet
    public byte getByte(int columnIndex) throws SQLException {
        Byte value = (Byte) get(columnIndex, Byte.class);
        if (this.wasNull) {
            return (byte) 0;
        }
        return value.byteValue();
    }

    @Override // java.sql.ResultSet
    public byte getByte(String columnLabel) throws SQLException {
        Byte value = (Byte) get(columnLabel, Byte.class);
        if (this.wasNull) {
            return (byte) 0;
        }
        return value.byteValue();
    }

    @Override // java.sql.ResultSet
    public short getShort(int columnIndex) throws SQLException {
        Short value = (Short) get(columnIndex, Short.class);
        if (this.wasNull) {
            return (short) 0;
        }
        return value.shortValue();
    }

    @Override // java.sql.ResultSet
    public short getShort(String columnLabel) throws SQLException {
        Short value = (Short) get(columnLabel, Short.class);
        if (this.wasNull) {
            return (short) 0;
        }
        return value.shortValue();
    }

    @Override // java.sql.ResultSet
    public int getInt(int columnIndex) throws SQLException {
        Integer value = (Integer) get(columnIndex, Integer.class);
        if (this.wasNull) {
            return 0;
        }
        return value.intValue();
    }

    @Override // java.sql.ResultSet
    public int getInt(String columnLabel) throws SQLException {
        Integer value = (Integer) get(columnLabel, Integer.class);
        if (this.wasNull) {
            return 0;
        }
        return value.intValue();
    }

    @Override // java.sql.ResultSet
    public long getLong(int columnIndex) throws SQLException {
        Long value = (Long) get(columnIndex, Long.class);
        if (this.wasNull) {
            return 0L;
        }
        return value.longValue();
    }

    @Override // java.sql.ResultSet
    public long getLong(String columnLabel) throws SQLException {
        Long value = (Long) get(columnLabel, Long.class);
        if (this.wasNull) {
            return 0L;
        }
        return value.longValue();
    }

    @Override // java.sql.ResultSet
    public float getFloat(int columnIndex) throws SQLException {
        Float value = (Float) get(columnIndex, Float.class);
        if (this.wasNull) {
            return 0.0f;
        }
        return value.floatValue();
    }

    @Override // java.sql.ResultSet
    public float getFloat(String columnLabel) throws SQLException {
        Float value = (Float) get(columnLabel, Float.class);
        if (this.wasNull) {
            return 0.0f;
        }
        return value.floatValue();
    }

    @Override // java.sql.ResultSet
    public double getDouble(int columnIndex) throws SQLException {
        Double value = (Double) get(columnIndex, Double.class);
        if (this.wasNull) {
            return 0.0d;
        }
        return value.doubleValue();
    }

    @Override // java.sql.ResultSet
    public double getDouble(String columnLabel) throws SQLException {
        Double value = (Double) get(columnLabel, Double.class);
        if (this.wasNull) {
            return 0.0d;
        }
        return value.doubleValue();
    }

    @Override // java.sql.ResultSet
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return (BigDecimal) get(columnIndex, BigDecimal.class);
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return (BigDecimal) get(columnIndex, BigDecimal.class);
    }

    @Override // java.sql.ResultSet
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return (BigDecimal) get(columnLabel, BigDecimal.class);
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return (BigDecimal) get(columnLabel, BigDecimal.class);
    }

    @Override // java.sql.ResultSet
    public byte[] getBytes(int columnIndex) throws SQLException {
        return (byte[]) get(columnIndex, byte[].class);
    }

    @Override // java.sql.ResultSet
    public byte[] getBytes(String columnLabel) throws SQLException {
        return (byte[]) get(columnLabel, byte[].class);
    }

    @Override // java.sql.ResultSet
    public Date getDate(int columnIndex) throws SQLException {
        return (Date) get(columnIndex, Date.class);
    }

    @Override // java.sql.ResultSet
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return withTZ((Date) get(columnIndex, Date.class), cal);
    }

    @Override // java.sql.ResultSet
    public Date getDate(String columnLabel) throws SQLException {
        return (Date) get(columnLabel, Date.class);
    }

    @Override // java.sql.ResultSet
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return withTZ((Date) get(columnLabel, Date.class), cal);
    }

    @Override // java.sql.ResultSet
    public Time getTime(int columnIndex) throws SQLException {
        return (Time) get(columnIndex, Time.class);
    }

    @Override // java.sql.ResultSet
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return withTZ((Time) get(columnIndex, Time.class), cal);
    }

    @Override // java.sql.ResultSet
    public Time getTime(String columnLabel) throws SQLException {
        return (Time) get(columnLabel, Time.class);
    }

    @Override // java.sql.ResultSet
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return withTZ((Time) get(columnLabel, Time.class), cal);
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return (Timestamp) get(columnIndex, Timestamp.class);
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return withTZ((Timestamp) get(columnIndex, Timestamp.class), cal);
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return (Timestamp) get(columnLabel, Timestamp.class);
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return withTZ((Timestamp) get(columnLabel, Timestamp.class), cal);
    }

    @Override // java.sql.ResultSet
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        byte[] bytes = getBytes(columnIndex);
        if (this.wasNull) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override // java.sql.ResultSet
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        byte[] bytes = getBytes(columnLabel);
        if (this.wasNull) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        String string = getString(columnIndex);
        if (this.wasNull) {
            return null;
        }
        return new ByteArrayInputStream(string.getBytes());
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        String string = getString(columnLabel);
        if (this.wasNull) {
            return null;
        }
        return new ByteArrayInputStream(string.getBytes());
    }

    @Override // java.sql.ResultSet
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        String string = getString(columnIndex);
        if (this.wasNull) {
            return null;
        }
        return new StringReader(string);
    }

    @Override // java.sql.ResultSet
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        String string = getString(columnLabel);
        if (this.wasNull) {
            return null;
        }
        return new StringReader(string);
    }

    @Override // java.sql.ResultSet
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return getCharacterStream(columnIndex);
    }

    @Override // java.sql.ResultSet
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(columnLabel);
    }

    @Override // java.sql.ResultSet
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        byte[] bytes = getBytes(columnIndex);
        if (this.wasNull) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override // java.sql.ResultSet
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        byte[] bytes = getBytes(columnLabel);
        if (this.wasNull) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    @Override // java.sql.ResultSet
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type");
    }

    @Override // java.sql.ResultSet
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type");
    }

    @Override // java.sql.ResultSet
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type");
    }

    @Override // java.sql.ResultSet
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type");
    }

    @Override // java.sql.ResultSet
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type");
    }

    @Override // java.sql.ResultSet
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type");
    }

    @Override // java.sql.ResultSet
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type. Use getBytes() instead");
    }

    @Override // java.sql.ResultSet
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type. Use getBytes() instead");
    }

    @Override // java.sql.ResultSet
    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type. Use getString() instead");
    }

    @Override // java.sql.ResultSet
    public Clob getClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type. Use getString() instead");
    }

    @Override // java.sql.ResultSet
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type. Use getString() instead");
    }

    @Override // java.sql.ResultSet
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported data type. Use getString() instead");
    }

    @Override // java.sql.ResultSet
    public Array getArray(int columnIndex) throws SQLException {
        return (Array) get(columnIndex, Array.class);
    }

    @Override // java.sql.ResultSet
    public Array getArray(String columnLabel) throws SQLException {
        return (Array) get(columnLabel, Array.class);
    }

    @Override // java.sql.ResultSet
    public URL getURL(int columnIndex) throws SQLException {
        return (URL) get(columnIndex, URL.class);
    }

    @Override // java.sql.ResultSet
    public URL getURL(String columnLabel) throws SQLException {
        return (URL) get(columnLabel, URL.class);
    }

    @Override // java.sql.ResultSet
    public Object getObject(int columnIndex) throws SQLException {
        return get(columnIndex, Object.class);
    }

    @Override // java.sql.ResultSet
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return get(columnIndex, Object.class);
    }

    @Override // java.sql.ResultSet
    public Object getObject(String columnLabel) throws SQLException {
        return get(columnLabel, Object.class);
    }

    @Override // java.sql.ResultSet
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return get(columnLabel, Object.class);
    }

    @Override // org.jooq.tools.jdbc.JDBC41ResultSet
    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        return (T) get(i, cls);
    }

    @Override // org.jooq.tools.jdbc.JDBC41ResultSet
    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        return (T) get(str, cls);
    }

    @Override // java.sql.ResultSet
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override // java.sql.ResultSet
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override // java.sql.ResultSet
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override // java.sql.ResultSet
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    @Override // java.sql.ResultSet
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Cannot update ResultSet");
    }

    public String toString() {
        if (this.result == null) {
            return "null";
        }
        if (this.result.size() == 0 || this.index == 0 || this.index > size()) {
            return this.result.toString();
        }
        String prefix = "row " + this.index + " -> ";
        String prefixEmpty = StringUtils.leftPad("", prefix.length());
        Result<Record> r = DSL.using(SQLDialect.DEFAULT).newResult(this.result.fields());
        r.addAll(this.result.subList(Math.max(0, this.index - 3), Math.min(this.size, this.index + 2)));
        StringBuilder sb = new StringBuilder();
        String[] split = r.toString().split("\n");
        for (int i = 0; i < split.length; i++) {
            sb.append(i - 2 == Math.min(3, this.index) ? prefix : prefixEmpty).append(split[i]).append('\n');
        }
        return sb.toString();
    }
}

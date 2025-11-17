package org.h2.tools;

import ch.qos.logback.core.joran.JoranConstants;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.SimpleColumnInfo;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/SimpleResultSet.class */
public class SimpleResultSet implements ResultSet, ResultSetMetaData {
    private ArrayList<Object[]> rows;
    private Object[] currentRow;
    private int rowId;
    private boolean wasNull;
    private SimpleRowSource source;
    private ArrayList<SimpleColumnInfo> columns;
    private boolean autoClose;

    public SimpleResultSet() {
        this.rowId = -1;
        this.columns = Utils.newSmallArrayList();
        this.autoClose = true;
        this.rows = Utils.newSmallArrayList();
    }

    public SimpleResultSet(SimpleRowSource simpleRowSource) {
        this.rowId = -1;
        this.columns = Utils.newSmallArrayList();
        this.autoClose = true;
        this.source = simpleRowSource;
    }

    public void addColumn(String str, int i, int i2, int i3) {
        addColumn(str, i, Value.getTypeName(DataType.convertSQLTypeToValueType(i)), i2, i3);
    }

    public void addColumn(String str, int i, String str2, int i2, int i3) {
        if (this.rows != null && !this.rows.isEmpty()) {
            throw new IllegalStateException("Cannot add a column after adding rows");
        }
        if (str == null) {
            str = "C" + (this.columns.size() + 1);
        }
        this.columns.add(new SimpleColumnInfo(str, i, str2, i2, i3));
    }

    public void addRow(Object... objArr) {
        if (this.rows == null) {
            throw new IllegalStateException("Cannot add a row when using RowSource");
        }
        this.rows.add(objArr);
    }

    @Override // java.sql.ResultSet
    public int getConcurrency() {
        return 1007;
    }

    @Override // java.sql.ResultSet
    public int getFetchDirection() {
        return 1000;
    }

    @Override // java.sql.ResultSet
    public int getFetchSize() {
        return 0;
    }

    @Override // java.sql.ResultSet
    public int getRow() {
        if (this.currentRow == null) {
            return 0;
        }
        return this.rowId + 1;
    }

    @Override // java.sql.ResultSet
    public int getType() {
        if (this.autoClose) {
            return 1003;
        }
        return 1004;
    }

    @Override // java.sql.ResultSet, java.lang.AutoCloseable
    public void close() {
        this.currentRow = null;
        this.rows = null;
        this.columns = null;
        this.rowId = -1;
        if (this.source != null) {
            this.source.close();
            this.source = null;
        }
    }

    @Override // java.sql.ResultSet
    public boolean next() throws SQLException {
        if (this.source != null) {
            this.rowId++;
            this.currentRow = this.source.readRow();
            if (this.currentRow != null) {
                return true;
            }
        } else if (this.rows != null && this.rowId < this.rows.size()) {
            this.rowId++;
            if (this.rowId < this.rows.size()) {
                this.currentRow = this.rows.get(this.rowId);
                return true;
            }
            this.currentRow = null;
        }
        if (this.autoClose) {
            close();
            return false;
        }
        return false;
    }

    @Override // java.sql.ResultSet
    public void beforeFirst() throws SQLException {
        if (this.autoClose) {
            throw DbException.getJdbcSQLException(ErrorCode.RESULT_SET_NOT_SCROLLABLE);
        }
        this.rowId = -1;
        if (this.source != null) {
            this.source.reset();
        }
    }

    @Override // java.sql.ResultSet
    public boolean wasNull() {
        return this.wasNull;
    }

    @Override // java.sql.ResultSet
    public int findColumn(String str) throws SQLException {
        if (str != null && this.columns != null) {
            int size = this.columns.size();
            for (int i = 0; i < size; i++) {
                if (str.equalsIgnoreCase(getColumn(i).name)) {
                    return i + 1;
                }
            }
        }
        throw DbException.getJdbcSQLException(ErrorCode.COLUMN_NOT_FOUND_1, str);
    }

    @Override // java.sql.ResultSet
    public ResultSetMetaData getMetaData() {
        return this;
    }

    @Override // java.sql.ResultSet
    public SQLWarning getWarnings() {
        return null;
    }

    @Override // java.sql.ResultSet
    public Statement getStatement() {
        return null;
    }

    @Override // java.sql.ResultSet
    public void clearWarnings() {
    }

    @Override // java.sql.ResultSet
    public Array getArray(int i) throws SQLException {
        Object[] objArr = (Object[]) get(i);
        if (objArr == null) {
            return null;
        }
        return new SimpleArray(objArr);
    }

    @Override // java.sql.ResultSet
    public Array getArray(String str) throws SQLException {
        return getArray(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public InputStream getAsciiStream(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public InputStream getAsciiStream(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public BigDecimal getBigDecimal(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof BigDecimal)) {
            obj = new BigDecimal(obj.toString());
        }
        return (BigDecimal) obj;
    }

    @Override // java.sql.ResultSet
    public BigDecimal getBigDecimal(String str) throws SQLException {
        return getBigDecimal(findColumn(str));
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public BigDecimal getBigDecimal(String str, int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public InputStream getBinaryStream(int i) throws SQLException {
        return asInputStream(get(i));
    }

    private static InputStream asInputStream(Object obj) throws SQLException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Blob) {
            return ((Blob) obj).getBinaryStream();
        }
        return (InputStream) obj;
    }

    @Override // java.sql.ResultSet
    public InputStream getBinaryStream(String str) throws SQLException {
        return getBinaryStream(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Blob getBlob(int i) throws SQLException {
        return (Blob) get(i);
    }

    @Override // java.sql.ResultSet
    public Blob getBlob(String str) throws SQLException {
        return getBlob(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public boolean getBoolean(int i) throws SQLException {
        Object obj = get(i);
        if (obj == null) {
            return false;
        }
        if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue();
        }
        if (obj instanceof Number) {
            Number number = (Number) obj;
            return ((number instanceof Double) || (number instanceof Float)) ? number.doubleValue() != 0.0d : number instanceof BigDecimal ? ((BigDecimal) number).signum() != 0 : number instanceof BigInteger ? ((BigInteger) number).signum() != 0 : number.longValue() != 0;
        }
        return Utils.parseBoolean(obj.toString(), false, true);
    }

    @Override // java.sql.ResultSet
    public boolean getBoolean(String str) throws SQLException {
        return getBoolean(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public byte getByte(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof Number)) {
            obj = Byte.decode(obj.toString());
        }
        if (obj == null) {
            return (byte) 0;
        }
        return ((Number) obj).byteValue();
    }

    @Override // java.sql.ResultSet
    public byte getByte(String str) throws SQLException {
        return getByte(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public byte[] getBytes(int i) throws SQLException {
        Object obj = get(i);
        if (obj == null || (obj instanceof byte[])) {
            return (byte[]) obj;
        }
        if (obj instanceof UUID) {
            return Bits.uuidToBytes((UUID) obj);
        }
        return JdbcUtils.serialize(obj, null);
    }

    @Override // java.sql.ResultSet
    public byte[] getBytes(String str) throws SQLException {
        return getBytes(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Reader getCharacterStream(int i) throws SQLException {
        return asReader(get(i));
    }

    private static Reader asReader(Object obj) throws SQLException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Clob) {
            return ((Clob) obj).getCharacterStream();
        }
        return (Reader) obj;
    }

    @Override // java.sql.ResultSet
    public Reader getCharacterStream(String str) throws SQLException {
        return getCharacterStream(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Clob getClob(int i) throws SQLException {
        return (Clob) get(i);
    }

    @Override // java.sql.ResultSet
    public Clob getClob(String str) throws SQLException {
        return getClob(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Date getDate(int i) throws SQLException {
        return (Date) get(i);
    }

    @Override // java.sql.ResultSet
    public Date getDate(String str) throws SQLException {
        return getDate(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Date getDate(int i, Calendar calendar) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Date getDate(String str, Calendar calendar) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public double getDouble(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof Number)) {
            return Double.parseDouble(obj.toString());
        }
        if (obj == null) {
            return 0.0d;
        }
        return ((Number) obj).doubleValue();
    }

    @Override // java.sql.ResultSet
    public double getDouble(String str) throws SQLException {
        return getDouble(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public float getFloat(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof Number)) {
            return Float.parseFloat(obj.toString());
        }
        if (obj == null) {
            return 0.0f;
        }
        return ((Number) obj).floatValue();
    }

    @Override // java.sql.ResultSet
    public float getFloat(String str) throws SQLException {
        return getFloat(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public int getInt(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof Number)) {
            obj = Integer.decode(obj.toString());
        }
        if (obj == null) {
            return 0;
        }
        return ((Number) obj).intValue();
    }

    @Override // java.sql.ResultSet
    public int getInt(String str) throws SQLException {
        return getInt(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public long getLong(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof Number)) {
            obj = Long.decode(obj.toString());
        }
        if (obj == null) {
            return 0L;
        }
        return ((Number) obj).longValue();
    }

    @Override // java.sql.ResultSet
    public long getLong(String str) throws SQLException {
        return getLong(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Reader getNCharacterStream(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Reader getNCharacterStream(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public NClob getNClob(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public NClob getNClob(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public String getNString(int i) throws SQLException {
        return getString(i);
    }

    @Override // java.sql.ResultSet
    public String getNString(String str) throws SQLException {
        return getString(str);
    }

    @Override // java.sql.ResultSet
    public Object getObject(int i) throws SQLException {
        return get(i);
    }

    @Override // java.sql.ResultSet
    public Object getObject(String str) throws SQLException {
        return getObject(findColumn(str));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T getObject(int i, Class<T> cls) throws SQLException {
        if (get(i) == null) {
            return null;
        }
        if (cls == BigDecimal.class) {
            return (T) getBigDecimal(i);
        }
        if (cls == BigInteger.class) {
            return (T) getBigDecimal(i).toBigInteger();
        }
        if (cls == String.class) {
            return (T) getString(i);
        }
        if (cls == Boolean.class) {
            return (T) Boolean.valueOf(getBoolean(i));
        }
        if (cls == Byte.class) {
            return (T) Byte.valueOf(getByte(i));
        }
        if (cls == Short.class) {
            return (T) Short.valueOf(getShort(i));
        }
        if (cls == Integer.class) {
            return (T) Integer.valueOf(getInt(i));
        }
        if (cls == Long.class) {
            return (T) Long.valueOf(getLong(i));
        }
        if (cls == Float.class) {
            return (T) Float.valueOf(getFloat(i));
        }
        if (cls == Double.class) {
            return (T) Double.valueOf(getDouble(i));
        }
        if (cls == Date.class) {
            return (T) getDate(i);
        }
        if (cls == Time.class) {
            return (T) getTime(i);
        }
        if (cls == Timestamp.class) {
            return (T) getTimestamp(i);
        }
        if (cls == UUID.class) {
            return (T) getObject(i);
        }
        if (cls == byte[].class) {
            return (T) getBytes(i);
        }
        if (cls == Array.class) {
            return (T) getArray(i);
        }
        if (cls == Blob.class) {
            return (T) getBlob(i);
        }
        if (cls == Clob.class) {
            return (T) getClob(i);
        }
        throw getUnsupportedException();
    }

    public <T> T getObject(String str, Class<T> cls) throws SQLException {
        return (T) getObject(findColumn(str), cls);
    }

    @Override // java.sql.ResultSet
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Object getObject(String str, Map<String, Class<?>> map) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Ref getRef(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Ref getRef(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public RowId getRowId(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public RowId getRowId(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public short getShort(int i) throws SQLException {
        Object obj = get(i);
        if (obj != null && !(obj instanceof Number)) {
            obj = Short.decode(obj.toString());
        }
        if (obj == null) {
            return (short) 0;
        }
        return ((Number) obj).shortValue();
    }

    @Override // java.sql.ResultSet
    public short getShort(String str) throws SQLException {
        return getShort(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public SQLXML getSQLXML(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public SQLXML getSQLXML(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public String getString(int i) throws SQLException {
        Object obj = get(i);
        if (obj == null) {
            return null;
        }
        switch (this.columns.get(i - 1).type) {
            case 2005:
                Clob clob = (Clob) obj;
                return clob.getSubString(1L, MathUtils.convertLongToInt(clob.length()));
            default:
                return obj.toString();
        }
    }

    @Override // java.sql.ResultSet
    public String getString(String str) throws SQLException {
        return getString(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Time getTime(int i) throws SQLException {
        return (Time) get(i);
    }

    @Override // java.sql.ResultSet
    public Time getTime(String str) throws SQLException {
        return getTime(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Time getTime(int i, Calendar calendar) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Time getTime(String str, Calendar calendar) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(int i) throws SQLException {
        return (Timestamp) get(i);
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(String str) throws SQLException {
        return getTimestamp(findColumn(str));
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public Timestamp getTimestamp(String str, Calendar calendar) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public InputStream getUnicodeStream(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    @Deprecated
    public InputStream getUnicodeStream(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public URL getURL(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public URL getURL(String str) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void updateArray(int i, Array array) throws SQLException {
        update(i, array);
    }

    @Override // java.sql.ResultSet
    public void updateArray(String str, Array array) throws SQLException {
        update(str, array);
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String str, InputStream inputStream) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String str, InputStream inputStream, int i) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(int i, InputStream inputStream, long j) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateAsciiStream(String str, InputStream inputStream, long j) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        update(i, bigDecimal);
    }

    @Override // java.sql.ResultSet
    public void updateBigDecimal(String str, BigDecimal bigDecimal) throws SQLException {
        update(str, bigDecimal);
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String str, InputStream inputStream) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String str, InputStream inputStream, int i) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(int i, InputStream inputStream, long j) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBinaryStream(String str, InputStream inputStream, long j) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int i, Blob blob) throws SQLException {
        update(i, blob);
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String str, Blob blob) throws SQLException {
        update(str, blob);
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String str, InputStream inputStream) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBlob(int i, InputStream inputStream, long j) throws SQLException {
        update(i, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBlob(String str, InputStream inputStream, long j) throws SQLException {
        update(str, inputStream);
    }

    @Override // java.sql.ResultSet
    public void updateBoolean(int i, boolean z) throws SQLException {
        update(i, Boolean.valueOf(z));
    }

    @Override // java.sql.ResultSet
    public void updateBoolean(String str, boolean z) throws SQLException {
        update(str, Boolean.valueOf(z));
    }

    @Override // java.sql.ResultSet
    public void updateByte(int i, byte b) throws SQLException {
        update(i, Byte.valueOf(b));
    }

    @Override // java.sql.ResultSet
    public void updateByte(String str, byte b) throws SQLException {
        update(str, Byte.valueOf(b));
    }

    @Override // java.sql.ResultSet
    public void updateBytes(int i, byte[] bArr) throws SQLException {
        update(i, bArr);
    }

    @Override // java.sql.ResultSet
    public void updateBytes(String str, byte[] bArr) throws SQLException {
        update(str, bArr);
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String str, Reader reader) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String str, Reader reader, int i) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(int i, Reader reader, long j) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateCharacterStream(String str, Reader reader, long j) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateClob(int i, Clob clob) throws SQLException {
        update(i, clob);
    }

    @Override // java.sql.ResultSet
    public void updateClob(String str, Clob clob) throws SQLException {
        update(str, clob);
    }

    @Override // java.sql.ResultSet
    public void updateClob(int i, Reader reader) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateClob(String str, Reader reader) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateClob(int i, Reader reader, long j) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateClob(String str, Reader reader, long j) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateDate(int i, Date date) throws SQLException {
        update(i, date);
    }

    @Override // java.sql.ResultSet
    public void updateDate(String str, Date date) throws SQLException {
        update(str, date);
    }

    @Override // java.sql.ResultSet
    public void updateDouble(int i, double d) throws SQLException {
        update(i, Double.valueOf(d));
    }

    @Override // java.sql.ResultSet
    public void updateDouble(String str, double d) throws SQLException {
        update(str, Double.valueOf(d));
    }

    @Override // java.sql.ResultSet
    public void updateFloat(int i, float f) throws SQLException {
        update(i, Float.valueOf(f));
    }

    @Override // java.sql.ResultSet
    public void updateFloat(String str, float f) throws SQLException {
        update(str, Float.valueOf(f));
    }

    @Override // java.sql.ResultSet
    public void updateInt(int i, int i2) throws SQLException {
        update(i, Integer.valueOf(i2));
    }

    @Override // java.sql.ResultSet
    public void updateInt(String str, int i) throws SQLException {
        update(str, Integer.valueOf(i));
    }

    @Override // java.sql.ResultSet
    public void updateLong(int i, long j) throws SQLException {
        update(i, Long.valueOf(j));
    }

    @Override // java.sql.ResultSet
    public void updateLong(String str, long j) throws SQLException {
        update(str, Long.valueOf(j));
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(String str, Reader reader) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(int i, Reader reader, long j) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNCharacterStream(String str, Reader reader, long j) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int i, NClob nClob) throws SQLException {
        update(i, nClob);
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String str, NClob nClob) throws SQLException {
        update(str, nClob);
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int i, Reader reader) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String str, Reader reader) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNClob(int i, Reader reader, long j) throws SQLException {
        update(i, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNClob(String str, Reader reader, long j) throws SQLException {
        update(str, reader);
    }

    @Override // java.sql.ResultSet
    public void updateNString(int i, String str) throws SQLException {
        update(i, str);
    }

    @Override // java.sql.ResultSet
    public void updateNString(String str, String str2) throws SQLException {
        update(str, str2);
    }

    @Override // java.sql.ResultSet
    public void updateNull(int i) throws SQLException {
        update(i, (Object) null);
    }

    @Override // java.sql.ResultSet
    public void updateNull(String str) throws SQLException {
        update(str, (Object) null);
    }

    @Override // java.sql.ResultSet
    public void updateObject(int i, Object obj) throws SQLException {
        update(i, obj);
    }

    @Override // java.sql.ResultSet
    public void updateObject(String str, Object obj) throws SQLException {
        update(str, obj);
    }

    @Override // java.sql.ResultSet
    public void updateObject(int i, Object obj, int i2) throws SQLException {
        update(i, obj);
    }

    @Override // java.sql.ResultSet
    public void updateObject(String str, Object obj, int i) throws SQLException {
        update(str, obj);
    }

    @Override // java.sql.ResultSet
    public void updateRef(int i, Ref ref) throws SQLException {
        update(i, ref);
    }

    @Override // java.sql.ResultSet
    public void updateRef(String str, Ref ref) throws SQLException {
        update(str, ref);
    }

    @Override // java.sql.ResultSet
    public void updateRowId(int i, RowId rowId) throws SQLException {
        update(i, rowId);
    }

    @Override // java.sql.ResultSet
    public void updateRowId(String str, RowId rowId) throws SQLException {
        update(str, rowId);
    }

    @Override // java.sql.ResultSet
    public void updateShort(int i, short s) throws SQLException {
        update(i, Short.valueOf(s));
    }

    @Override // java.sql.ResultSet
    public void updateShort(String str, short s) throws SQLException {
        update(str, Short.valueOf(s));
    }

    @Override // java.sql.ResultSet
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        update(i, sqlxml);
    }

    @Override // java.sql.ResultSet
    public void updateSQLXML(String str, SQLXML sqlxml) throws SQLException {
        update(str, sqlxml);
    }

    @Override // java.sql.ResultSet
    public void updateString(int i, String str) throws SQLException {
        update(i, str);
    }

    @Override // java.sql.ResultSet
    public void updateString(String str, String str2) throws SQLException {
        update(str, str2);
    }

    @Override // java.sql.ResultSet
    public void updateTime(int i, Time time) throws SQLException {
        update(i, time);
    }

    @Override // java.sql.ResultSet
    public void updateTime(String str, Time time) throws SQLException {
        update(str, time);
    }

    @Override // java.sql.ResultSet
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        update(i, timestamp);
    }

    @Override // java.sql.ResultSet
    public void updateTimestamp(String str, Timestamp timestamp) throws SQLException {
        update(str, timestamp);
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnCount() {
        return this.columns.size();
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnDisplaySize(int i) {
        return 15;
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnType(int i) throws SQLException {
        return getColumn(i - 1).type;
    }

    @Override // java.sql.ResultSetMetaData
    public int getPrecision(int i) throws SQLException {
        return getColumn(i - 1).precision;
    }

    @Override // java.sql.ResultSetMetaData
    public int getScale(int i) throws SQLException {
        return getColumn(i - 1).scale;
    }

    @Override // java.sql.ResultSetMetaData
    public int isNullable(int i) {
        return 2;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isAutoIncrement(int i) {
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isCaseSensitive(int i) {
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isCurrency(int i) {
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isDefinitelyWritable(int i) {
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isReadOnly(int i) {
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isSearchable(int i) {
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isSigned(int i) {
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isWritable(int i) {
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public String getCatalogName(int i) {
        return "";
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnClassName(int i) throws SQLException {
        return ValueToObjectConverter.getDefaultClass(DataType.getValueTypeFromResultSet(this, i), true).getName();
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnLabel(int i) throws SQLException {
        return getColumn(i - 1).name;
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnName(int i) throws SQLException {
        return getColumnLabel(i);
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnTypeName(int i) throws SQLException {
        return getColumn(i - 1).typeName;
    }

    @Override // java.sql.ResultSetMetaData
    public String getSchemaName(int i) {
        return "";
    }

    @Override // java.sql.ResultSetMetaData
    public String getTableName(int i) {
        return "";
    }

    @Override // java.sql.ResultSet
    public void afterLast() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void cancelRowUpdates() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void deleteRow() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void insertRow() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void moveToCurrentRow() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void moveToInsertRow() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void refreshRow() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void updateRow() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean first() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean isAfterLast() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean isBeforeFirst() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean isFirst() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean isLast() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean last() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean previous() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean rowDeleted() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean rowInserted() throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean rowUpdated() throws SQLException {
        return true;
    }

    @Override // java.sql.ResultSet
    public void setFetchDirection(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public void setFetchSize(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean absolute(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public boolean relative(int i) throws SQLException {
        throw getUnsupportedException();
    }

    @Override // java.sql.ResultSet
    public String getCursorName() throws SQLException {
        throw getUnsupportedException();
    }

    private void update(int i, Object obj) throws SQLException {
        checkClosed();
        checkColumnIndex(i);
        this.currentRow[i - 1] = obj;
    }

    private void update(String str, Object obj) throws SQLException {
        this.currentRow[findColumn(str) - 1] = obj;
    }

    static SQLException getUnsupportedException() {
        return DbException.getJdbcSQLException(ErrorCode.FEATURE_NOT_SUPPORTED_1);
    }

    private void checkClosed() throws SQLException {
        if (this.columns == null) {
            throw DbException.getJdbcSQLException(ErrorCode.OBJECT_CLOSED);
        }
    }

    private void checkColumnIndex(int i) throws SQLException {
        if (i < 1 || i > this.columns.size()) {
            throw DbException.getInvalidValueException("columnIndex", Integer.valueOf(i)).getSQLException();
        }
    }

    private Object get(int i) throws SQLException {
        if (this.currentRow == null) {
            throw DbException.getJdbcSQLException(2000);
        }
        checkColumnIndex(i);
        int i2 = i - 1;
        Object obj = i2 < this.currentRow.length ? this.currentRow[i2] : null;
        this.wasNull = obj == null;
        return obj;
    }

    private SimpleColumnInfo getColumn(int i) throws SQLException {
        checkColumnIndex(i + 1);
        return this.columns.get(i);
    }

    @Override // java.sql.ResultSet
    public int getHoldability() {
        return 1;
    }

    @Override // java.sql.ResultSet
    public boolean isClosed() {
        return this.rows == null && this.source == null;
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
            throw DbException.toSQLException(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    public void setAutoClose(boolean z) {
        this.autoClose = z;
    }

    public boolean getAutoClose() {
        return this.autoClose;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/SimpleResultSet$SimpleArray.class */
    public static class SimpleArray implements Array {
        private final Object[] value;

        SimpleArray(Object[] objArr) {
            this.value = objArr;
        }

        @Override // java.sql.Array
        public Object getArray() {
            return this.value;
        }

        @Override // java.sql.Array
        public Object getArray(Map<String, Class<?>> map) throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public Object getArray(long j, int i) throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public Object getArray(long j, int i, Map<String, Class<?>> map) throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public int getBaseType() {
            return 0;
        }

        @Override // java.sql.Array
        public String getBaseTypeName() {
            return JoranConstants.NULL;
        }

        @Override // java.sql.Array
        public ResultSet getResultSet() throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public ResultSet getResultSet(long j, int i) throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public ResultSet getResultSet(long j, int i, Map<String, Class<?>> map) throws SQLException {
            throw SimpleResultSet.getUnsupportedException();
        }

        @Override // java.sql.Array
        public void free() {
        }
    }
}

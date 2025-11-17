package org.h2.jdbc;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.command.CommandInterface;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcArray.class */
public final class JdbcArray extends TraceObject implements Array {
    private ValueArray value;
    private final JdbcConnection conn;

    public JdbcArray(JdbcConnection jdbcConnection, Value value, int i) {
        setTrace(jdbcConnection.getSession().getTrace(), 16, i);
        this.conn = jdbcConnection;
        this.value = value.convertToAnyArray(jdbcConnection);
    }

    @Override // java.sql.Array
    public Object getArray() throws SQLException {
        try {
            debugCodeCall("getArray");
            checkClosed();
            return get();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getArray(" + quoteMap(map) + ')');
            }
            JdbcConnection.checkMap(map);
            checkClosed();
            return get();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public Object getArray(long j, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getArray(" + j + ", " + i + ')');
            }
            checkClosed();
            return get(j, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public Object getArray(long j, int i, Map<String, Class<?>> map) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getArray(" + j + ", " + i + ", " + quoteMap(map) + ')');
            }
            checkClosed();
            JdbcConnection.checkMap(map);
            return get(j, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public int getBaseType() throws SQLException {
        try {
            debugCodeCall("getBaseType");
            checkClosed();
            return DataType.convertTypeToSQLType(this.value.getComponentType());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public String getBaseTypeName() throws SQLException {
        try {
            debugCodeCall("getBaseTypeName");
            checkClosed();
            return this.value.getComponentType().getDeclaredTypeName();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public ResultSet getResultSet() throws SQLException {
        try {
            debugCodeCall("getResultSet");
            checkClosed();
            return getResultSetImpl(1L, Integer.MAX_VALUE);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getResultSet(" + quoteMap(map) + ')');
            }
            checkClosed();
            JdbcConnection.checkMap(map);
            return getResultSetImpl(1L, Integer.MAX_VALUE);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public ResultSet getResultSet(long j, int i) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getResultSet(" + j + ", " + i + ')');
            }
            checkClosed();
            return getResultSetImpl(j, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public ResultSet getResultSet(long j, int i, Map<String, Class<?>> map) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getResultSet(" + j + ", " + i + ", " + quoteMap(map) + ')');
            }
            checkClosed();
            JdbcConnection.checkMap(map);
            return getResultSetImpl(j, i);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Array
    public void free() {
        debugCodeCall("free");
        this.value = null;
    }

    private ResultSet getResultSetImpl(long j, int i) {
        int nextId = getNextId(4);
        SimpleResult simpleResult = new SimpleResult();
        simpleResult.addColumn("INDEX", TypeInfo.TYPE_BIGINT);
        simpleResult.addColumn("VALUE", this.value.getComponentType());
        Value[] list = this.value.getList();
        int checkRange = checkRange(j, i, list.length);
        for (int i2 = (int) j; i2 < j + checkRange; i2++) {
            simpleResult.addRow(ValueBigint.get(i2), list[i2 - 1]);
        }
        return new JdbcResultSet(this.conn, (JdbcStatement) null, (CommandInterface) null, (ResultInterface) simpleResult, nextId, true, false, false);
    }

    private void checkClosed() {
        this.conn.checkClosed();
        if (this.value == null) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED);
        }
    }

    private Object get() {
        return ValueToObjectConverter.valueToDefaultArray(this.value, this.conn, true);
    }

    private Object get(long j, int i) {
        Value[] list = this.value.getList();
        int checkRange = checkRange(j, i, list.length);
        Object[] objArr = new Object[checkRange];
        int i2 = 0;
        int i3 = ((int) j) - 1;
        while (i2 < checkRange) {
            objArr[i2] = ValueToObjectConverter.valueToDefaultObject(list[i3], this.conn, true);
            i2++;
            i3++;
        }
        return objArr;
    }

    private static int checkRange(long j, int i, int i2) {
        if (j < 1 || (j != 1 && j > i2)) {
            throw DbException.getInvalidValueException("index (1.." + i2 + ')', Long.valueOf(j));
        }
        int i3 = (i2 - ((int) j)) + 1;
        if (i < 0) {
            throw DbException.getInvalidValueException("count (0.." + i3 + ')', Integer.valueOf(i));
        }
        return Math.min(i3, i);
    }

    public String toString() {
        return this.value == null ? "null" : getTraceObjectName() + ": " + this.value.getTraceSQL();
    }
}

package org.jooq.tools.jdbc;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockArray.class */
public class MockArray<T> implements Array {
    private final SQLDialect dialect;
    private final T[] array;
    private final Class<? extends T[]> type;

    @Override // java.sql.Array
    public /* bridge */ /* synthetic */ Object getArray(long j, int i, Map map) throws SQLException {
        return getArray(j, i, (Map<String, Class<?>>) map);
    }

    @Override // java.sql.Array
    public /* bridge */ /* synthetic */ Object getArray(Map map) throws SQLException {
        return getArray((Map<String, Class<?>>) map);
    }

    public MockArray(SQLDialect dialect, T[] array, Class<? extends T[]> type) {
        this.dialect = dialect;
        this.array = array;
        this.type = type;
    }

    @Override // java.sql.Array
    public String getBaseTypeName() {
        return DefaultDataType.getDataType(this.dialect, this.type.getComponentType()).getTypeName();
    }

    @Override // java.sql.Array
    public int getBaseType() {
        return DefaultDataType.getDataType(this.dialect, this.type.getComponentType()).getSQLType();
    }

    @Override // java.sql.Array
    public T[] getArray() {
        return this.array;
    }

    @Override // java.sql.Array
    public T[] getArray(Map<String, Class<?>> map) {
        return this.array;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Array
    public T[] getArray(long j, int i) throws SQLException {
        if (j - 1 > 2147483647L) {
            throw new SQLException("Cannot access array indexes beyond Integer.MAX_VALUE");
        }
        if (this.array == null) {
            return null;
        }
        return (T[]) Arrays.asList(this.array).subList(((int) j) - 1, (((int) j) - 1) + i).toArray((Object[]) java.lang.reflect.Array.newInstance(this.array.getClass().getComponentType(), i));
    }

    @Override // java.sql.Array
    public T[] getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return getArray(index, count);
    }

    @Override // java.sql.Array
    public ResultSet getResultSet() {
        return getResultSet0(this.array);
    }

    @Override // java.sql.Array
    public ResultSet getResultSet(Map<String, Class<?>> map) {
        return getResultSet();
    }

    @Override // java.sql.Array
    public ResultSet getResultSet(long index, int count) throws SQLException {
        return getResultSet0(getArray(index, count));
    }

    @Override // java.sql.Array
    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return getResultSet(index, count);
    }

    private ResultSet getResultSet0(T[] a) {
        DSLContext create = DSL.using(this.dialect);
        Field<Long> index = DSL.field(DSL.name("INDEX"), Long.class);
        Field<T> value = DSL.field(DSL.name("VALUE"), this.type.getComponentType());
        Result<Record2<Long, T>> result = create.newResult(index, value);
        for (int i = 0; i < a.length; i++) {
            Record2<Long, T> record = create.newRecord(index, value);
            record.setValue(index, Long.valueOf(i + 1));
            record.setValue(value, a[i]);
            result.add(record);
        }
        return new MockResultSet(result);
    }

    @Override // java.sql.Array
    public void free() {
    }
}

package org.h2.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.result.ResultInterface;
import org.h2.util.MathUtils;
import org.h2.value.DataType;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcResultSetMetaData.class */
public final class JdbcResultSetMetaData extends TraceObject implements ResultSetMetaData {
    private final String catalog;
    private final JdbcResultSet rs;
    private final JdbcPreparedStatement prep;
    private final ResultInterface result;
    private final int columnCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcResultSetMetaData(JdbcResultSet jdbcResultSet, JdbcPreparedStatement jdbcPreparedStatement, ResultInterface resultInterface, String str, Trace trace, int i) {
        setTrace(trace, 5, i);
        this.catalog = str;
        this.rs = jdbcResultSet;
        this.prep = jdbcPreparedStatement;
        this.result = resultInterface;
        this.columnCount = resultInterface.getVisibleColumnCount();
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnCount() throws SQLException {
        try {
            debugCodeCall("getColumnCount");
            checkClosed();
            return this.columnCount;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnLabel(int i) throws SQLException {
        try {
            return this.result.getAlias(getColumn("getColumnLabel", i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnName(int i) throws SQLException {
        try {
            return this.result.getColumnName(getColumn("getColumnName", i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnType(int i) throws SQLException {
        try {
            return DataType.convertTypeToSQLType(this.result.getColumnType(getColumn("getColumnType", i)));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnTypeName(int i) throws SQLException {
        try {
            return this.result.getColumnType(getColumn("getColumnTypeName", i)).getDeclaredTypeName();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getSchemaName(int i) throws SQLException {
        try {
            String schemaName = this.result.getSchemaName(getColumn("getSchemaName", i));
            return schemaName == null ? "" : schemaName;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getTableName(int i) throws SQLException {
        try {
            String tableName = this.result.getTableName(getColumn("getTableName", i));
            return tableName == null ? "" : tableName;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getCatalogName(int i) throws SQLException {
        try {
            getColumn("getCatalogName", i);
            return this.catalog == null ? "" : this.catalog;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isAutoIncrement(int i) throws SQLException {
        try {
            return this.result.isIdentity(getColumn("isAutoIncrement", i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isCaseSensitive(int i) throws SQLException {
        try {
            getColumn("isCaseSensitive", i);
            return true;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isSearchable(int i) throws SQLException {
        try {
            getColumn("isSearchable", i);
            return true;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isCurrency(int i) throws SQLException {
        try {
            getColumn("isCurrency", i);
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public int isNullable(int i) throws SQLException {
        try {
            return this.result.getNullable(getColumn("isNullable", i));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isSigned(int i) throws SQLException {
        try {
            return DataType.isNumericType(this.result.getColumnType(getColumn("isSigned", i)).getValueType());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isReadOnly(int i) throws SQLException {
        try {
            getColumn("isReadOnly", i);
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isWritable(int i) throws SQLException {
        try {
            getColumn("isWritable", i);
            return true;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isDefinitelyWritable(int i) throws SQLException {
        try {
            getColumn("isDefinitelyWritable", i);
            return false;
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnClassName(int i) throws SQLException {
        try {
            return ValueToObjectConverter.getDefaultClass(this.result.getColumnType(getColumn("getColumnClassName", i)).getValueType(), true).getName();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public int getPrecision(int i) throws SQLException {
        try {
            return MathUtils.convertLongToInt(this.result.getColumnType(getColumn("getPrecision", i)).getPrecision());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public int getScale(int i) throws SQLException {
        try {
            return this.result.getColumnType(getColumn("getScale", i)).getScale();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnDisplaySize(int i) throws SQLException {
        try {
            return this.result.getColumnType(getColumn("getColumnDisplaySize", i)).getDisplaySize();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    private void checkClosed() {
        if (this.rs != null) {
            this.rs.checkClosed();
        }
        if (this.prep != null) {
            this.prep.checkClosed();
        }
    }

    private int getColumn(String str, int i) {
        debugCodeCall(str, i);
        checkClosed();
        if (i < 1 || i > this.columnCount) {
            throw DbException.getInvalidValueException("columnIndex", Integer.valueOf(i));
        }
        return i - 1;
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

    public String toString() {
        return getTraceObjectName() + ": columns=" + this.columnCount;
    }
}

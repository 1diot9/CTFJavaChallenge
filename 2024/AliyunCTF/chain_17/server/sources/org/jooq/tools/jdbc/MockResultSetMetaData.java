package org.jooq.tools.jdbc;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.types.UNumber;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockResultSetMetaData.class */
public class MockResultSetMetaData implements ResultSetMetaData, Serializable {
    private final MockResultSet rs;

    public MockResultSetMetaData(MockResultSet rs) {
        this.rs = rs;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return this;
        }
        throw new SQLException("MockResultSetMetaData does not implement " + String.valueOf(iface));
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnCount() throws SQLException {
        this.rs.checkNotClosed();
        return this.rs.result.fieldsRow().size();
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isAutoIncrement(int column) throws SQLException {
        this.rs.checkNotClosed();
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isCaseSensitive(int column) throws SQLException {
        this.rs.checkNotClosed();
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isSearchable(int column) throws SQLException {
        this.rs.checkNotClosed();
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isCurrency(int column) throws SQLException {
        this.rs.checkNotClosed();
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public int isNullable(int column) throws SQLException {
        this.rs.checkNotClosed();
        return 2;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isSigned(int column) throws SQLException {
        this.rs.checkNotClosed();
        Field<?> field = this.rs.result.field(column - 1);
        Class<?> type = field.getType();
        return Number.class.isAssignableFrom(type) && !UNumber.class.isAssignableFrom(type);
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnLabel(int column) throws SQLException {
        return getColumnName(column);
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnName(int column) throws SQLException {
        this.rs.checkNotClosed();
        return this.rs.result.field(column - 1).getName();
    }

    @Override // java.sql.ResultSetMetaData
    public String getSchemaName(int column) throws SQLException {
        Schema schema;
        this.rs.checkNotClosed();
        Field<?> field = this.rs.result.field(column - 1);
        if (field instanceof TableField) {
            TableField<?, ?> f = (TableField) field;
            Table<?> table = f.getTable();
            if (table != null && (schema = table.getSchema()) != null) {
                Configuration configuration = this.rs.result.configuration();
                Schema mapped = null;
                if (configuration != null) {
                    mapped = DSL.using(configuration).map(schema);
                }
                if (mapped != null) {
                    return mapped.getName();
                }
                return schema.getName();
            }
            return "";
        }
        return "";
    }

    @Override // java.sql.ResultSetMetaData
    public int getPrecision(int column) throws SQLException {
        this.rs.checkNotClosed();
        return 0;
    }

    @Override // java.sql.ResultSetMetaData
    public int getScale(int column) throws SQLException {
        this.rs.checkNotClosed();
        return 0;
    }

    @Override // java.sql.ResultSetMetaData
    public String getTableName(int column) throws SQLException {
        this.rs.checkNotClosed();
        Field<?> field = this.rs.result.field(column - 1);
        if (field instanceof TableField) {
            TableField<?, ?> f = (TableField) field;
            Table<?> table = f.getTable();
            if (table != null) {
                return table.getName();
            }
            return "";
        }
        return "";
    }

    @Override // java.sql.ResultSetMetaData
    public String getCatalogName(int column) throws SQLException {
        this.rs.checkNotClosed();
        return "";
    }

    @Override // java.sql.ResultSetMetaData
    public int getColumnType(int column) throws SQLException {
        this.rs.checkNotClosed();
        return this.rs.result.field(column - 1).getDataType().getSQLType();
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnTypeName(int column) throws SQLException {
        this.rs.checkNotClosed();
        return this.rs.result.field(column - 1).getDataType().getTypeName();
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isReadOnly(int column) throws SQLException {
        this.rs.checkNotClosed();
        return true;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isWritable(int column) throws SQLException {
        this.rs.checkNotClosed();
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public boolean isDefinitelyWritable(int column) throws SQLException {
        this.rs.checkNotClosed();
        return false;
    }

    @Override // java.sql.ResultSetMetaData
    public String getColumnClassName(int column) throws SQLException {
        this.rs.checkNotClosed();
        return this.rs.result.field(column - 1).getType().getName();
    }
}

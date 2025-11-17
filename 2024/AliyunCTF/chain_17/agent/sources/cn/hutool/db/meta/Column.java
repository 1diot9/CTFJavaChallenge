package cn.hutool.db.meta;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.DbRuntimeException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/meta/Column.class */
public class Column implements Serializable, Cloneable {
    private static final long serialVersionUID = 577527740359719367L;
    private String tableName;
    private String name;
    private int type;
    private String typeName;
    private long size;
    private Integer digit;
    private boolean isNullable;
    private String comment;
    private boolean autoIncrement;
    private String columnDef;
    private boolean isPk;

    public static Column create(Table table, ResultSet columnMetaRs) {
        return new Column(table, columnMetaRs);
    }

    public Column() {
    }

    public Column(Table table, ResultSet columnMetaRs) {
        try {
            init(table, columnMetaRs);
        } catch (SQLException e) {
            throw new DbRuntimeException(e, "Get table [{}] meta info error!", this.tableName);
        }
    }

    public void init(Table table, ResultSet columnMetaRs) throws SQLException {
        this.tableName = table.getTableName();
        this.name = columnMetaRs.getString("COLUMN_NAME");
        this.isPk = table.isPk(this.name);
        this.type = columnMetaRs.getInt("DATA_TYPE");
        String typeName = columnMetaRs.getString("TYPE_NAME");
        this.typeName = ReUtil.delLast("\\(\\d+\\)", typeName);
        this.size = columnMetaRs.getLong("COLUMN_SIZE");
        this.isNullable = columnMetaRs.getBoolean("NULLABLE");
        this.comment = columnMetaRs.getString("REMARKS");
        this.columnDef = columnMetaRs.getString("COLUMN_DEF");
        try {
            this.digit = Integer.valueOf(columnMetaRs.getInt("DECIMAL_DIGITS"));
        } catch (SQLException e) {
        }
        try {
            String auto = columnMetaRs.getString("IS_AUTOINCREMENT");
            if (BooleanUtil.toBoolean(auto)) {
                this.autoIncrement = true;
            }
        } catch (SQLException e2) {
        }
    }

    public String getTableName() {
        return this.tableName;
    }

    public Column setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public JdbcType getTypeEnum() {
        return JdbcType.valueOf(this.type);
    }

    public int getType() {
        return this.type;
    }

    public Column setType(int type) {
        this.type = type;
        return this;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public Column setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public long getSize() {
        return this.size;
    }

    public Column setSize(int size) {
        this.size = size;
        return this;
    }

    public int getDigit() {
        return this.digit.intValue();
    }

    public Column setDigit(int digit) {
        this.digit = Integer.valueOf(digit);
        return this;
    }

    public boolean isNullable() {
        return this.isNullable;
    }

    public Column setNullable(boolean isNullable) {
        this.isNullable = isNullable;
        return this;
    }

    public String getComment() {
        return this.comment;
    }

    public Column setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    public Column setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
        return this;
    }

    public boolean isPk() {
        return this.isPk;
    }

    public Column setPk(boolean isPk) {
        this.isPk = isPk;
        return this;
    }

    public String getColumnDef() {
        return this.columnDef;
    }

    public Column setColumnDef(String columnDef) {
        this.columnDef = columnDef;
        return this;
    }

    public String toString() {
        return "Column [tableName=" + this.tableName + ", name=" + this.name + ", type=" + this.type + ", size=" + this.size + ", isNullable=" + this.isNullable + "]";
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Column m298clone() throws CloneNotSupportedException {
        return (Column) super.clone();
    }
}

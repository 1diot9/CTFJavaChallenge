package cn.hutool.db.meta;

import cn.hutool.db.DbRuntimeException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/meta/ColumnIndexInfo.class */
public class ColumnIndexInfo implements Serializable, Cloneable {
    private static final long serialVersionUID = 1;
    private String columnName;
    private String ascOrDesc;

    public static ColumnIndexInfo create(ResultSet rs) {
        try {
            return new ColumnIndexInfo(rs.getString("COLUMN_NAME"), rs.getString("ASC_OR_DESC"));
        } catch (SQLException e) {
            throw new DbRuntimeException(e);
        }
    }

    public ColumnIndexInfo(String columnName, String ascOrDesc) {
        this.columnName = columnName;
        this.ascOrDesc = ascOrDesc;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAscOrDesc() {
        return this.ascOrDesc;
    }

    public void setAscOrDesc(String ascOrDesc) {
        this.ascOrDesc = ascOrDesc;
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public ColumnIndexInfo m299clone() throws CloneNotSupportedException {
        return (ColumnIndexInfo) super.clone();
    }

    public String toString() {
        return "ColumnIndexInfo{columnName='" + this.columnName + "', ascOrDesc='" + this.ascOrDesc + "'}";
    }
}

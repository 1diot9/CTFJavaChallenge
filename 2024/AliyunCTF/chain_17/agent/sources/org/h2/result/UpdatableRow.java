package org.h2.result;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.engine.Session;
import org.h2.engine.SessionRemote;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcResultSet;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/UpdatableRow.class */
public class UpdatableRow {
    private final JdbcConnection conn;
    private final ResultInterface result;
    private final int columnCount;
    private String schemaName;
    private String tableName;
    private ArrayList<String> key;
    private boolean isUpdatable;

    public UpdatableRow(JdbcConnection jdbcConnection, ResultInterface resultInterface) throws SQLException {
        this.conn = jdbcConnection;
        this.result = resultInterface;
        this.columnCount = resultInterface.getVisibleColumnCount();
        if (this.columnCount == 0) {
            return;
        }
        for (int i = 0; i < this.columnCount; i++) {
            String tableName = resultInterface.getTableName(i);
            String schemaName = resultInterface.getSchemaName(i);
            if (tableName == null || schemaName == null) {
                return;
            }
            if (this.tableName == null) {
                this.tableName = tableName;
            } else if (!this.tableName.equals(tableName)) {
                return;
            }
            if (this.schemaName == null) {
                this.schemaName = schemaName;
            } else if (!this.schemaName.equals(schemaName)) {
                return;
            }
        }
        String str = "BASE TABLE";
        Session session = jdbcConnection.getSession();
        if ((session instanceof SessionRemote) && ((SessionRemote) session).getClientVersion() <= 19) {
            str = "TABLE";
        }
        DatabaseMetaData metaData = jdbcConnection.getMetaData();
        ResultSet tables = metaData.getTables(null, StringUtils.escapeMetaDataPattern(this.schemaName), StringUtils.escapeMetaDataPattern(this.tableName), new String[]{str});
        if (!tables.next()) {
            return;
        }
        String string = tables.getString("TABLE_NAME");
        boolean z = !string.equals(this.tableName) && string.equalsIgnoreCase(this.tableName);
        this.key = Utils.newSmallArrayList();
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, StringUtils.escapeMetaDataPattern(this.schemaName), this.tableName);
        while (primaryKeys.next()) {
            String string2 = primaryKeys.getString("COLUMN_NAME");
            this.key.add(z ? StringUtils.toUpperEnglish(string2) : string2);
        }
        if (isIndexUsable(this.key)) {
            this.isUpdatable = true;
            return;
        }
        this.key.clear();
        ResultSet indexInfo = metaData.getIndexInfo(null, StringUtils.escapeMetaDataPattern(this.schemaName), this.tableName, true, true);
        while (indexInfo.next()) {
            if (indexInfo.getShort("ORDINAL_POSITION") == 1) {
                if (isIndexUsable(this.key)) {
                    this.isUpdatable = true;
                    return;
                }
                this.key.clear();
            }
            String string3 = indexInfo.getString("COLUMN_NAME");
            this.key.add(z ? StringUtils.toUpperEnglish(string3) : string3);
        }
        if (isIndexUsable(this.key)) {
            this.isUpdatable = true;
        } else {
            this.key = null;
        }
    }

    private boolean isIndexUsable(ArrayList<String> arrayList) {
        if (arrayList.isEmpty()) {
            return false;
        }
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            if (findColumnIndex(it.next()) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isUpdatable() {
        return this.isUpdatable;
    }

    private int findColumnIndex(String str) {
        for (int i = 0; i < this.columnCount; i++) {
            if (this.result.getColumnName(i).equals(str)) {
                return i;
            }
        }
        return -1;
    }

    private int getColumnIndex(String str) {
        int findColumnIndex = findColumnIndex(str);
        if (findColumnIndex < 0) {
            throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
        }
        return findColumnIndex;
    }

    private void appendColumnList(StringBuilder sb, boolean z) {
        for (int i = 0; i < this.columnCount; i++) {
            if (i > 0) {
                sb.append(',');
            }
            StringUtils.quoteIdentifier(sb, this.result.getColumnName(i));
            if (z) {
                sb.append("=? ");
            }
        }
    }

    private void appendKeyCondition(StringBuilder sb) {
        sb.append(" WHERE ");
        for (int i = 0; i < this.key.size(); i++) {
            if (i > 0) {
                sb.append(" AND ");
            }
            StringUtils.quoteIdentifier(sb, this.key.get(i)).append("=?");
        }
    }

    private void setKey(PreparedStatement preparedStatement, int i, Value[] valueArr) throws SQLException {
        int size = this.key.size();
        for (int i2 = 0; i2 < size; i2++) {
            Value value = valueArr[getColumnIndex(this.key.get(i2))];
            if (value == null || value == ValueNull.INSTANCE) {
                throw DbException.get(2000);
            }
            JdbcUtils.set(preparedStatement, i + i2, value, this.conn);
        }
    }

    private void appendTableName(StringBuilder sb) {
        if (this.schemaName != null && this.schemaName.length() > 0) {
            StringUtils.quoteIdentifier(sb, this.schemaName).append('.');
        }
        StringUtils.quoteIdentifier(sb, this.tableName);
    }

    public Value[] readRow(Value[] valueArr) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT ");
        appendColumnList(sb, false);
        sb.append(" FROM ");
        appendTableName(sb);
        appendKeyCondition(sb);
        PreparedStatement prepareStatement = this.conn.prepareStatement(sb.toString());
        setKey(prepareStatement, 1, valueArr);
        JdbcResultSet jdbcResultSet = (JdbcResultSet) prepareStatement.executeQuery();
        if (!jdbcResultSet.next()) {
            throw DbException.get(2000);
        }
        Value[] valueArr2 = new Value[this.columnCount];
        for (int i = 0; i < this.columnCount; i++) {
            valueArr2[i] = ValueToObjectConverter.readValue(this.conn.getSession(), jdbcResultSet, i + 1);
        }
        return valueArr2;
    }

    public void deleteRow(Value[] valueArr) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        appendTableName(sb);
        appendKeyCondition(sb);
        PreparedStatement prepareStatement = this.conn.prepareStatement(sb.toString());
        setKey(prepareStatement, 1, valueArr);
        if (prepareStatement.executeUpdate() != 1) {
            throw DbException.get(2000);
        }
    }

    public void updateRow(Value[] valueArr, Value[] valueArr2) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        appendTableName(sb);
        sb.append(" SET ");
        appendColumnList(sb, true);
        appendKeyCondition(sb);
        PreparedStatement prepareStatement = this.conn.prepareStatement(sb.toString());
        int i = 1;
        for (int i2 = 0; i2 < this.columnCount; i2++) {
            Value value = valueArr2[i2];
            if (value == null) {
                value = valueArr[i2];
            }
            int i3 = i;
            i++;
            JdbcUtils.set(prepareStatement, i3, value, this.conn);
        }
        setKey(prepareStatement, i, valueArr);
        if (prepareStatement.executeUpdate() != 1) {
            throw DbException.get(2000);
        }
    }

    public void insertRow(Value[] valueArr) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        appendTableName(sb);
        sb.append('(');
        appendColumnList(sb, false);
        sb.append(")VALUES(");
        for (int i = 0; i < this.columnCount; i++) {
            if (i > 0) {
                sb.append(',');
            }
            if (valueArr[i] == null) {
                sb.append("DEFAULT");
            } else {
                sb.append('?');
            }
        }
        sb.append(')');
        PreparedStatement prepareStatement = this.conn.prepareStatement(sb.toString());
        int i2 = 0;
        for (int i3 = 0; i3 < this.columnCount; i3++) {
            Value value = valueArr[i3];
            if (value != null) {
                int i4 = i2;
                i2++;
                JdbcUtils.set(prepareStatement, i4 + 1, value, this.conn);
            }
        }
        if (prepareStatement.executeUpdate() != 1) {
            throw DbException.get(2000);
        }
    }
}

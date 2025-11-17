package org.h2.index;

import ch.qos.logback.core.joran.JoranConstants;
import cn.hutool.core.text.CharSequenceUtil;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.table.TableLink;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/LinkedIndex.class */
public class LinkedIndex extends Index {
    private final TableLink link;
    private final String targetTableName;
    private long rowCount;
    private final int sqlFlags = 1;

    public LinkedIndex(TableLink tableLink, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType) {
        super(tableLink, i, null, indexColumnArr, i2, indexType);
        this.sqlFlags = 1;
        this.link = tableLink;
        this.targetTableName = this.link.getQualifiedTable();
    }

    @Override // org.h2.index.Index, org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    private static boolean isNull(Value value) {
        return value == null || value == ValueNull.INSTANCE;
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        ArrayList<Value> newSmallArrayList = Utils.newSmallArrayList();
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(this.targetTableName).append(" VALUES(");
        for (int i = 0; i < row.getColumnCount(); i++) {
            Value value = row.getValue(i);
            if (i > 0) {
                sb.append(", ");
            }
            if (value == null) {
                sb.append("DEFAULT");
            } else if (isNull(value)) {
                sb.append(JoranConstants.NULL);
            } else {
                sb.append('?');
                newSmallArrayList.add(value);
            }
        }
        sb.append(')');
        String sb2 = sb.toString();
        try {
            this.link.execute(sb2, newSmallArrayList, true, sessionLocal);
            this.rowCount++;
        } catch (Exception e) {
            throw TableLink.wrapException(sb2, e);
        }
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        ArrayList<Value> newSmallArrayList = Utils.newSmallArrayList();
        StringBuilder append = new StringBuilder("SELECT * FROM ").append(this.targetTableName).append(" T");
        boolean z = false;
        for (int i = 0; searchRow != null && i < searchRow.getColumnCount(); i++) {
            Value value = searchRow.getValue(i);
            if (value != null) {
                append.append(z ? " AND " : " WHERE ");
                z = true;
                Column column = this.table.getColumn(i);
                addColumnName(append, column);
                if (value == ValueNull.INSTANCE) {
                    append.append(" IS NULL");
                } else {
                    append.append(">=");
                    addParameter(append, column);
                    newSmallArrayList.add(value);
                }
            }
        }
        for (int i2 = 0; searchRow2 != null && i2 < searchRow2.getColumnCount(); i2++) {
            Value value2 = searchRow2.getValue(i2);
            if (value2 != null) {
                append.append(z ? " AND " : " WHERE ");
                z = true;
                Column column2 = this.table.getColumn(i2);
                addColumnName(append, column2);
                if (value2 == ValueNull.INSTANCE) {
                    append.append(" IS NULL");
                } else {
                    append.append("<=");
                    addParameter(append, column2);
                    newSmallArrayList.add(value2);
                }
            }
        }
        String sb = append.toString();
        try {
            PreparedStatement execute = this.link.execute(sb, newSmallArrayList, false, sessionLocal);
            return new LinkedCursor(this.link, execute.getResultSet(), sessionLocal, sb, execute);
        } catch (Exception e) {
            throw TableLink.wrapException(sb, e);
        }
    }

    private void addColumnName(StringBuilder sb, Column column) {
        String identifierQuoteString = this.link.getIdentifierQuoteString();
        String name = column.getName();
        if (identifierQuoteString == null || identifierQuoteString.isEmpty() || identifierQuoteString.equals(CharSequenceUtil.SPACE)) {
            sb.append(name);
            return;
        }
        if (identifierQuoteString.equals("\"")) {
            sb.append('\"');
            int indexOf = name.indexOf(34);
            if (indexOf < 0) {
                sb.append(name);
            } else {
                int i = indexOf + 1;
                sb.append((CharSequence) name, 0, i).append('\"');
                int length = name.length();
                while (i < length) {
                    char charAt = name.charAt(i);
                    if (charAt == '\"') {
                        sb.append('\"');
                    }
                    sb.append(charAt);
                    i++;
                }
            }
            sb.append('\"');
            return;
        }
        sb.append(identifierQuoteString).append(name).append(identifierQuoteString);
    }

    private void addParameter(StringBuilder sb, Column column) {
        TypeInfo type = column.getType();
        if (type.getValueType() == 1 && this.link.isOracle()) {
            sb.append("CAST(? AS CHAR(").append(type.getPrecision()).append("))");
        } else {
            sb.append('?');
        }
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return 100 + getCostRangeIndex(iArr, this.rowCount + 1000, tableFilterArr, i, sortOrder, false, allColumnsForPlan);
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("LINKED");
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        return false;
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        ArrayList<Value> newSmallArrayList = Utils.newSmallArrayList();
        StringBuilder append = new StringBuilder("DELETE FROM ").append(this.targetTableName).append(" WHERE ");
        for (int i = 0; i < row.getColumnCount(); i++) {
            if (i > 0) {
                append.append("AND ");
            }
            Column column = this.table.getColumn(i);
            addColumnName(append, column);
            Value value = row.getValue(i);
            if (isNull(value)) {
                append.append(" IS NULL ");
            } else {
                append.append('=');
                addParameter(append, column);
                newSmallArrayList.add(value);
                append.append(' ');
            }
        }
        String sb = append.toString();
        try {
            PreparedStatement execute = this.link.execute(sb, newSmallArrayList, false, sessionLocal);
            int executeUpdate = execute.executeUpdate();
            this.link.reusePreparedStatement(execute, sb);
            this.rowCount -= executeUpdate;
        } catch (Exception e) {
            throw TableLink.wrapException(sb, e);
        }
    }

    public void update(Row row, Row row2, SessionLocal sessionLocal) {
        ArrayList<Value> newSmallArrayList = Utils.newSmallArrayList();
        StringBuilder append = new StringBuilder("UPDATE ").append(this.targetTableName).append(" SET ");
        for (int i = 0; i < row2.getColumnCount(); i++) {
            if (i > 0) {
                append.append(", ");
            }
            this.table.getColumn(i).getSQL(append, 1).append('=');
            Value value = row2.getValue(i);
            if (value == null) {
                append.append("DEFAULT");
            } else {
                append.append('?');
                newSmallArrayList.add(value);
            }
        }
        append.append(" WHERE ");
        for (int i2 = 0; i2 < row.getColumnCount(); i2++) {
            Column column = this.table.getColumn(i2);
            if (i2 > 0) {
                append.append(" AND ");
            }
            addColumnName(append, column);
            Value value2 = row.getValue(i2);
            if (isNull(value2)) {
                append.append(" IS NULL");
            } else {
                append.append('=');
                newSmallArrayList.add(value2);
                addParameter(append, column);
            }
        }
        String sb = append.toString();
        try {
            this.link.execute(sb, newSmallArrayList, true, sessionLocal);
        } catch (Exception e) {
            throw TableLink.wrapException(sb, e);
        }
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return this.rowCount;
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return this.rowCount;
    }
}

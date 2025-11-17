package org.h2.table;

import java.util.HashMap;
import org.h2.result.Row;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/GeneratedColumnResolver.class */
public class GeneratedColumnResolver implements ColumnResolver {
    private final Table table;
    private Column[] columns;
    private HashMap<String, Column> columnMap;
    private Row current;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GeneratedColumnResolver(Table table) {
        this.table = table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void set(Row row) {
        this.current = row;
    }

    @Override // org.h2.table.ColumnResolver
    public Column[] getColumns() {
        Column[] columnArr = this.columns;
        if (columnArr == null) {
            Column[] createColumns = createColumns();
            columnArr = createColumns;
            this.columns = createColumns;
        }
        return columnArr;
    }

    private Column[] createColumns() {
        Column[] columns = this.table.getColumns();
        int length = columns.length;
        for (Column column : columns) {
            if (column.isGenerated()) {
                length--;
            }
        }
        Column[] columnArr = new Column[length];
        int i = 0;
        for (Column column2 : columns) {
            if (!column2.isGenerated()) {
                int i2 = i;
                i++;
                columnArr[i2] = column2;
            }
        }
        return columnArr;
    }

    @Override // org.h2.table.ColumnResolver
    public Column findColumn(String str) {
        HashMap<String, Column> hashMap = this.columnMap;
        if (hashMap == null) {
            hashMap = this.table.getDatabase().newStringMap();
            for (Column column : getColumns()) {
                hashMap.put(column.getName(), column);
            }
            this.columnMap = hashMap;
        }
        return hashMap.get(str);
    }

    @Override // org.h2.table.ColumnResolver
    public Value getValue(Column column) {
        int columnId = column.getColumnId();
        if (columnId == -1) {
            return ValueBigint.get(this.current.getKey());
        }
        return this.current.getValue(columnId);
    }

    @Override // org.h2.table.ColumnResolver
    public Column getRowIdColumn() {
        return this.table.getRowIdColumn();
    }
}

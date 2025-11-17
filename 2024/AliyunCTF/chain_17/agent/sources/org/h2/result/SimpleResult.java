package org.h2.result;

import java.util.ArrayList;
import java.util.Comparator;
import org.h2.engine.Session;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/SimpleResult.class */
public class SimpleResult implements ResultInterface, ResultTarget {
    private final ArrayList<Column> columns;
    private final ArrayList<Value[]> rows;
    private final String schemaName;
    private final String tableName;
    private int rowId;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !SimpleResult.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/SimpleResult$Column.class */
    public static final class Column {
        final String alias;
        final String columnName;
        final TypeInfo columnType;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Column(String str, String str2, TypeInfo typeInfo) {
            if (str == null || str2 == null) {
                throw new NullPointerException();
            }
            this.alias = str;
            this.columnName = str2;
            this.columnType = typeInfo;
        }

        public int hashCode() {
            return (31 * ((31 * 1) + this.alias.hashCode())) + this.columnName.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Column column = (Column) obj;
            return this.alias.equals(column.alias) && this.columnName.equals(column.columnName);
        }

        public String toString() {
            if (this.alias.equals(this.columnName)) {
                return this.columnName;
            }
            return this.columnName + ' ' + this.alias;
        }
    }

    public SimpleResult() {
        this("", "");
    }

    public SimpleResult(String str, String str2) {
        this.columns = Utils.newSmallArrayList();
        this.rows = new ArrayList<>();
        this.schemaName = str;
        this.tableName = str2;
        this.rowId = -1;
    }

    private SimpleResult(ArrayList<Column> arrayList, ArrayList<Value[]> arrayList2, String str, String str2) {
        this.columns = arrayList;
        this.rows = arrayList2;
        this.schemaName = str;
        this.tableName = str2;
        this.rowId = -1;
    }

    public void addColumn(String str, String str2, int i, long j, int i2) {
        addColumn(str, str2, TypeInfo.getTypeInfo(i, j, i2, null));
    }

    public void addColumn(String str, TypeInfo typeInfo) {
        addColumn(new Column(str, str, typeInfo));
    }

    public void addColumn(String str, String str2, TypeInfo typeInfo) {
        addColumn(new Column(str, str2, typeInfo));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addColumn(Column column) {
        if (!$assertionsDisabled && !this.rows.isEmpty()) {
            throw new AssertionError();
        }
        this.columns.add(column);
    }

    @Override // org.h2.result.ResultTarget
    public void addRow(Value... valueArr) {
        if (!$assertionsDisabled && valueArr.length != this.columns.size()) {
            throw new AssertionError();
        }
        this.rows.add(valueArr);
    }

    @Override // org.h2.result.ResultInterface
    public void reset() {
        this.rowId = -1;
    }

    @Override // org.h2.result.ResultInterface
    public Value[] currentRow() {
        return this.rows.get(this.rowId);
    }

    @Override // org.h2.result.ResultInterface
    public boolean next() {
        int size = this.rows.size();
        if (this.rowId < size) {
            int i = this.rowId + 1;
            this.rowId = i;
            return i < size;
        }
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public long getRowId() {
        return this.rowId;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isAfterLast() {
        return this.rowId >= this.rows.size();
    }

    @Override // org.h2.result.ResultInterface
    public int getVisibleColumnCount() {
        return this.columns.size();
    }

    @Override // org.h2.result.ResultInterface
    public long getRowCount() {
        return this.rows.size();
    }

    @Override // org.h2.result.ResultInterface
    public boolean hasNext() {
        return this.rowId < this.rows.size() - 1;
    }

    @Override // org.h2.result.ResultInterface
    public boolean needToClose() {
        return false;
    }

    @Override // org.h2.result.ResultInterface, java.lang.AutoCloseable
    public void close() {
    }

    @Override // org.h2.result.ResultInterface
    public String getAlias(int i) {
        return this.columns.get(i).alias;
    }

    @Override // org.h2.result.ResultInterface
    public String getSchemaName(int i) {
        return this.schemaName;
    }

    @Override // org.h2.result.ResultInterface
    public String getTableName(int i) {
        return this.tableName;
    }

    @Override // org.h2.result.ResultInterface
    public String getColumnName(int i) {
        return this.columns.get(i).columnName;
    }

    @Override // org.h2.result.ResultInterface
    public TypeInfo getColumnType(int i) {
        return this.columns.get(i).columnType;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isIdentity(int i) {
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public int getNullable(int i) {
        return 2;
    }

    @Override // org.h2.result.ResultInterface
    public void setFetchSize(int i) {
    }

    @Override // org.h2.result.ResultInterface
    public int getFetchSize() {
        return 1;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isLazy() {
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public boolean isClosed() {
        return false;
    }

    @Override // org.h2.result.ResultInterface
    public SimpleResult createShallowCopy(Session session) {
        return new SimpleResult(this.columns, this.rows, this.schemaName, this.tableName);
    }

    @Override // org.h2.result.ResultTarget
    public void limitsWereApplied() {
    }

    public void sortRows(Comparator<? super Value[]> comparator) {
        this.rows.sort(comparator);
    }
}

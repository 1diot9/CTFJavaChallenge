package org.h2.table;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.MetaIndex;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;
import org.h2.value.ValueVarcharIgnoreCase;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/MetaTable.class */
public abstract class MetaTable extends Table {
    public static final long ROW_COUNT_APPROXIMATION = 1000;
    protected final int type;
    protected int indexColumn;
    protected MetaIndex metaIndex;

    public abstract ArrayList<Row> generateRows(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2);

    /* JADX INFO: Access modifiers changed from: protected */
    public MetaTable(Schema schema, int i, int i2) {
        super(schema, i, null, true, true);
        this.type = i2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setMetaTableName(String str) {
        setObjectName(this.database.sysIdentifier(str));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Column column(String str) {
        return new Column(this.database.sysIdentifier(str), this.database.getSettings().caseInsensitiveIdentifiers ? TypeInfo.TYPE_VARCHAR_IGNORECASE : TypeInfo.TYPE_VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Column column(String str, TypeInfo typeInfo) {
        return new Column(this.database.sysIdentifier(str), typeInfo);
    }

    @Override // org.h2.engine.DbObject
    public final String getCreateSQL() {
        return null;
    }

    @Override // org.h2.table.Table
    public final Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2) {
        throw DbException.getUnsupportedException("META");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String identifier(String str) {
        if (this.database.getSettings().databaseToLower) {
            str = str == null ? null : StringUtils.toLowerEnglish(str);
        }
        return str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean checkIndex(SessionLocal sessionLocal, String str, Value value, Value value2) {
        Value value3;
        if (str == null) {
            return true;
        }
        if (value == null && value2 == null) {
            return true;
        }
        if (this.database.getSettings().caseInsensitiveIdentifiers) {
            value3 = ValueVarcharIgnoreCase.get(str);
        } else {
            value3 = ValueVarchar.get(str);
        }
        if (value != null && sessionLocal.compare(value3, value) < 0) {
            return false;
        }
        if (value2 != null && sessionLocal.compare(value3, value2) > 0) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean hideTable(Table table, SessionLocal sessionLocal) {
        return table.isHidden() && sessionLocal != this.database.getSystemSession();
    }

    @Override // org.h2.table.Table
    public boolean isInsertable() {
        return false;
    }

    @Override // org.h2.table.Table
    public final void removeRow(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.table.Table
    public final void addRow(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.table.Table, org.h2.engine.DbObject
    public final void removeChildrenAndResources(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.table.Table
    public final void close(SessionLocal sessionLocal) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void add(SessionLocal sessionLocal, ArrayList<Row> arrayList, Object... objArr) {
        Value[] valueArr = new Value[objArr.length];
        for (int i = 0; i < objArr.length; i++) {
            Object obj = objArr[i];
            valueArr[i] = this.columns[i].convert(sessionLocal, obj == null ? ValueNull.INSTANCE : obj instanceof String ? ValueVarchar.get((String) obj) : (Value) obj);
        }
        arrayList.add(Row.get(valueArr, 1, arrayList.size()));
    }

    @Override // org.h2.engine.DbObject
    public final void checkRename() {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.table.Table
    public final void checkSupportAlter() {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.table.Table
    public final long truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return false;
    }

    @Override // org.h2.table.Table
    public final boolean canDrop() {
        return false;
    }

    @Override // org.h2.table.Table
    public final TableType getTableType() {
        return TableType.SYSTEM_TABLE;
    }

    @Override // org.h2.table.Table
    public final Index getScanIndex(SessionLocal sessionLocal) {
        return new MetaIndex(this, IndexColumn.wrap(this.columns), true);
    }

    @Override // org.h2.table.Table
    public final ArrayList<Index> getIndexes() {
        ArrayList<Index> arrayList = new ArrayList<>(2);
        if (this.metaIndex == null) {
            return arrayList;
        }
        arrayList.add(new MetaIndex(this, IndexColumn.wrap(this.columns), true));
        arrayList.add(this.metaIndex);
        return arrayList;
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return 1000L;
    }

    @Override // org.h2.table.Table
    public final boolean isDeterministic() {
        return true;
    }

    @Override // org.h2.table.Table
    public final boolean canReference() {
        return false;
    }
}

package org.h2.table;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.DualIndex;
import org.h2.index.Index;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/DualTable.class */
public class DualTable extends VirtualTable {
    public static final String NAME = "DUAL";

    public DualTable(Database database) {
        super(database.getMainSchema(), 0, NAME);
        setColumns(new Column[0]);
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(NAME);
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return true;
    }

    @Override // org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        return 1L;
    }

    @Override // org.h2.table.VirtualTable, org.h2.table.Table
    public TableType getTableType() {
        return TableType.SYSTEM_TABLE;
    }

    @Override // org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal) {
        return new DualIndex(this);
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return 0L;
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return 1L;
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return true;
    }
}

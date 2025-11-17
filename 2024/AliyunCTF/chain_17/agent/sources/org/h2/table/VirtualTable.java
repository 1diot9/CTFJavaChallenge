package org.h2.table;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/VirtualTable.class */
public abstract class VirtualTable extends Table {
    /* JADX INFO: Access modifiers changed from: protected */
    public VirtualTable(Schema schema, int i, String str) {
        super(schema, i, str, false, true);
    }

    @Override // org.h2.table.Table
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.table.Table
    public Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.table.Table
    public boolean isInsertable() {
        return false;
    }

    @Override // org.h2.table.Table
    public void removeRow(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.table.Table
    public long truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.table.Table
    public void addRow(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.table.Table
    public void checkSupportAlter() {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.table.Table
    public TableType getTableType() {
        return null;
    }

    @Override // org.h2.table.Table
    public ArrayList<Index> getIndexes() {
        return null;
    }

    @Override // org.h2.table.Table
    public boolean canReference() {
        return false;
    }

    @Override // org.h2.table.Table
    public boolean canDrop() {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("Virtual table");
    }
}

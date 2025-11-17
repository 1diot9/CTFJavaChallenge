package org.h2.index;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.table.IndexColumn;
import org.h2.table.VirtualTable;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/VirtualTableIndex.class */
public abstract class VirtualTableIndex extends Index {
    /* JADX INFO: Access modifiers changed from: protected */
    public VirtualTableIndex(VirtualTable virtualTable, String str, IndexColumn[] indexColumnArr) {
        super(virtualTable, 0, str, indexColumnArr, 0, IndexType.createNonUnique(true));
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        return false;
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("Virtual table");
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return this.table.getRowCount(sessionLocal);
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return this.table.getRowCountApproximation(sessionLocal);
    }
}

package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.VirtualConstructedTableIndex;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/VirtualConstructedTable.class */
public abstract class VirtualConstructedTable extends VirtualTable {
    public abstract ResultInterface getResult(SessionLocal sessionLocal);

    /* JADX INFO: Access modifiers changed from: protected */
    public VirtualConstructedTable(Schema schema, int i, String str) {
        super(schema, i, str);
    }

    @Override // org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal) {
        return new VirtualConstructedTableIndex(this, IndexColumn.wrap(this.columns));
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return Long.MAX_VALUE;
    }
}

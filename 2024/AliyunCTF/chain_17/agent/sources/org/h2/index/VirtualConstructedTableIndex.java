package org.h2.index;

import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.FunctionTable;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.table.VirtualConstructedTable;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/VirtualConstructedTableIndex.class */
public class VirtualConstructedTableIndex extends VirtualTableIndex {
    private final VirtualConstructedTable table;

    public VirtualConstructedTableIndex(VirtualConstructedTable virtualConstructedTable, IndexColumn[] indexColumnArr) {
        super(virtualConstructedTable, null, indexColumnArr);
        this.table = virtualConstructedTable;
    }

    @Override // org.h2.index.Index
    public boolean isFindUsingFullTableScan() {
        return true;
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return new VirtualTableCursor(this, searchRow, searchRow2, this.table.getResult(sessionLocal));
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        long j;
        if (iArr != null) {
            throw DbException.getUnsupportedException("Virtual table");
        }
        if (this.table.canGetRowCount(sessionLocal)) {
            j = this.table.getRowCountApproximation(sessionLocal);
        } else {
            j = this.database.getSettings().estimatedFunctionTableRows;
        }
        return j * 10;
    }

    @Override // org.h2.index.Index
    public String getPlanSQL() {
        return this.table instanceof FunctionTable ? "function" : "table scan";
    }

    @Override // org.h2.index.Index
    public boolean canScan() {
        return false;
    }
}

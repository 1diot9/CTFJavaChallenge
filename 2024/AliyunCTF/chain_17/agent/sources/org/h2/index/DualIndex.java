package org.h2.index;

import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.DualTable;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/DualIndex.class */
public class DualIndex extends VirtualTableIndex {
    public DualIndex(DualTable dualTable) {
        super(dualTable, "DUAL_INDEX", new IndexColumn[0]);
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return new DualCursor();
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return 1.0d;
    }

    @Override // org.h2.index.Index, org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.index.Index
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override // org.h2.index.Index
    public Cursor findFirstOrLast(SessionLocal sessionLocal, boolean z) {
        return new SingleRowCursor(Row.get(Value.EMPTY_VALUES, 1));
    }

    @Override // org.h2.index.Index
    public String getPlanSQL() {
        return "dual index";
    }
}

package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/VirtualTableCursor.class */
class VirtualTableCursor implements Cursor {
    private final VirtualTableIndex index;
    private final SearchRow first;
    private final SearchRow last;
    private final ResultInterface result;
    Value[] values;
    Row row;

    /* JADX INFO: Access modifiers changed from: package-private */
    public VirtualTableCursor(VirtualTableIndex virtualTableIndex, SearchRow searchRow, SearchRow searchRow2, ResultInterface resultInterface) {
        this.index = virtualTableIndex;
        this.first = searchRow;
        this.last = searchRow2;
        this.result = resultInterface;
    }

    @Override // org.h2.index.Cursor
    public Row get() {
        if (this.values == null) {
            return null;
        }
        if (this.row == null) {
            this.row = Row.get(this.values, 1);
        }
        return this.row;
    }

    @Override // org.h2.index.Cursor
    public SearchRow getSearchRow() {
        return get();
    }

    @Override // org.h2.index.Cursor
    public boolean next() {
        SearchRow searchRow = this.first;
        SearchRow searchRow2 = this.last;
        if (searchRow == null && searchRow2 == null) {
            return nextImpl();
        }
        while (nextImpl()) {
            Row row = get();
            if (searchRow == null || this.index.compareRows(row, searchRow) >= 0) {
                if (searchRow2 == null || this.index.compareRows(row, searchRow2) <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean nextImpl() {
        this.row = null;
        if (this.result != null && this.result.next()) {
            this.values = this.result.currentRow();
        } else {
            this.values = null;
        }
        return this.values != null;
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/DualCursor.class */
class DualCursor implements Cursor {
    private Row currentRow;

    @Override // org.h2.index.Cursor
    public Row get() {
        return this.currentRow;
    }

    @Override // org.h2.index.Cursor
    public SearchRow getSearchRow() {
        return this.currentRow;
    }

    @Override // org.h2.index.Cursor
    public boolean next() {
        if (this.currentRow == null) {
            this.currentRow = Row.get(Value.EMPTY_VALUES, 1);
            return true;
        }
        return false;
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

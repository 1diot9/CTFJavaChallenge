package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/SingleRowCursor.class */
public class SingleRowCursor implements Cursor {
    private Row row;
    private boolean end;

    public SingleRowCursor(Row row) {
        this.row = row;
    }

    @Override // org.h2.index.Cursor
    public Row get() {
        return this.row;
    }

    @Override // org.h2.index.Cursor
    public SearchRow getSearchRow() {
        return this.row;
    }

    @Override // org.h2.index.Cursor
    public boolean next() {
        if (this.row == null || this.end) {
            this.row = null;
            return false;
        }
        this.end = true;
        return true;
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

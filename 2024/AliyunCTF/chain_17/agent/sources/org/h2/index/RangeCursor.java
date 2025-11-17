package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/RangeCursor.class */
class RangeCursor implements Cursor {
    private boolean beforeFirst = true;
    private long current;
    private Row currentRow;
    private final long start;
    private final long end;
    private final long step;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RangeCursor(long j, long j2, long j3) {
        this.start = j;
        this.end = j2;
        this.step = j3;
    }

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
        if (this.beforeFirst) {
            this.beforeFirst = false;
            this.current = this.start;
        } else {
            this.current += this.step;
        }
        this.currentRow = Row.get(new Value[]{ValueBigint.get(this.current)}, 1);
        return this.step > 0 ? this.current <= this.end : this.current >= this.end;
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

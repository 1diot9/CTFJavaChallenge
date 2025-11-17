package org.h2.index;

import java.util.ArrayList;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/MetaCursor.class */
public class MetaCursor implements Cursor {
    private Row current;
    private final ArrayList<Row> rows;
    private int index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MetaCursor(ArrayList<Row> arrayList) {
        this.rows = arrayList;
    }

    @Override // org.h2.index.Cursor
    public Row get() {
        return this.current;
    }

    @Override // org.h2.index.Cursor
    public SearchRow getSearchRow() {
        return this.current;
    }

    @Override // org.h2.index.Cursor
    public boolean next() {
        Row row;
        if (this.index >= this.rows.size()) {
            row = null;
        } else {
            ArrayList<Row> arrayList = this.rows;
            int i = this.index;
            this.index = i + 1;
            row = arrayList.get(i);
        }
        this.current = row;
        return this.current != null;
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

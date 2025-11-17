package org.h2.index;

import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.Table;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/QueryExpressionCursor.class */
public class QueryExpressionCursor implements Cursor {
    private final Table table;
    private final QueryExpressionIndex index;
    private final ResultInterface result;
    private final SearchRow first;
    private final SearchRow last;
    private Row current;

    public QueryExpressionCursor(QueryExpressionIndex queryExpressionIndex, ResultInterface resultInterface, SearchRow searchRow, SearchRow searchRow2) {
        this.table = queryExpressionIndex.getTable();
        this.index = queryExpressionIndex;
        this.result = resultInterface;
        this.first = searchRow;
        this.last = searchRow2;
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
        while (this.result.next()) {
            this.current = this.table.getTemplateRow();
            Value[] currentRow = this.result.currentRow();
            int i = 0;
            int columnCount = this.current.getColumnCount();
            while (i < columnCount) {
                this.current.setValue(i, i < currentRow.length ? currentRow[i] : ValueNull.INSTANCE);
                i++;
            }
            if (this.first == null || this.index.compareRows(this.current, this.first) >= 0) {
                if (this.last == null || this.index.compareRows(this.current, this.last) <= 0) {
                    return true;
                }
            }
        }
        if (this.index.isRecursive()) {
            this.result.reset();
        } else {
            this.result.close();
        }
        this.current = null;
        return false;
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

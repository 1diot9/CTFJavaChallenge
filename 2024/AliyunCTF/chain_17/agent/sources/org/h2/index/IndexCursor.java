package org.h2.index;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/IndexCursor.class */
public class IndexCursor implements Cursor {
    private SessionLocal session;
    private Index index;
    private Table table;
    private IndexColumn[] indexColumns;
    private boolean alwaysFalse;
    private SearchRow start;
    private SearchRow end;
    private SearchRow intersects;
    private Cursor cursor;
    private Column inColumn;
    private int inListIndex;
    private Value[] inList;
    private ResultInterface inResult;

    public void setIndex(Index index) {
        this.index = index;
        this.table = index.getTable();
        Column[] columns = this.table.getColumns();
        this.indexColumns = new IndexColumn[columns.length];
        IndexColumn[] indexColumns = index.getIndexColumns();
        if (indexColumns != null) {
            int length = columns.length;
            for (int i = 0; i < length; i++) {
                int columnIndex = index.getColumnIndex(columns[i]);
                if (columnIndex >= 0) {
                    this.indexColumns[i] = indexColumns[columnIndex];
                }
            }
        }
    }

    public void prepare(SessionLocal sessionLocal, ArrayList<IndexCondition> arrayList) {
        IndexColumn indexColumn;
        this.session = sessionLocal;
        this.alwaysFalse = false;
        this.end = null;
        this.start = null;
        this.inList = null;
        this.inColumn = null;
        this.inResult = null;
        this.intersects = null;
        Iterator<IndexCondition> it = arrayList.iterator();
        while (true) {
            if (it.hasNext()) {
                IndexCondition next = it.next();
                if (next.isAlwaysFalse()) {
                    this.alwaysFalse = true;
                } else if (!this.index.isFindUsingFullTableScan()) {
                    Column column = next.getColumn();
                    switch (next.getCompareType()) {
                        case 10:
                        case 11:
                            if (this.start == null && this.end == null && canUseIndexForIn(column)) {
                                this.inColumn = column;
                                this.inList = next.getCurrentValueList(sessionLocal);
                                this.inListIndex = 0;
                                break;
                            }
                            break;
                        case 12:
                            if (this.start == null && this.end == null && canUseIndexForIn(column)) {
                                this.inColumn = column;
                                this.inResult = next.getCurrentResult();
                                break;
                            }
                            break;
                        default:
                            Value currentValue = next.getCurrentValue(sessionLocal);
                            boolean isStart = next.isStart();
                            boolean isEnd = next.isEnd();
                            boolean isSpatialIntersects = next.isSpatialIntersects();
                            int columnId = column.getColumnId();
                            if (columnId != -1 && (indexColumn = this.indexColumns[columnId]) != null && (indexColumn.sortType & 1) != 0) {
                                isStart = isEnd;
                                isEnd = isStart;
                            }
                            if (isStart) {
                                this.start = getSearchRow(this.start, columnId, currentValue, true);
                            }
                            if (isEnd) {
                                this.end = getSearchRow(this.end, columnId, currentValue, false);
                            }
                            if (isSpatialIntersects) {
                                this.intersects = getSpatialSearchRow(this.intersects, columnId, currentValue);
                            }
                            if (!isStart && !isEnd) {
                                break;
                            } else if (!canUseIndexFor(this.inColumn)) {
                                this.inColumn = null;
                                this.inList = null;
                                this.inResult = null;
                                break;
                            } else {
                                break;
                            }
                            break;
                    }
                }
            }
        }
        if (this.inColumn != null) {
            this.start = this.table.getTemplateRow();
        }
    }

    public void find(SessionLocal sessionLocal, ArrayList<IndexCondition> arrayList) {
        prepare(sessionLocal, arrayList);
        if (this.inColumn == null && !this.alwaysFalse) {
            if (this.intersects != null && (this.index instanceof SpatialIndex)) {
                this.cursor = ((SpatialIndex) this.index).findByGeometry(this.session, this.start, this.end, this.intersects);
            } else if (this.index != null) {
                this.cursor = this.index.find(this.session, this.start, this.end);
            }
        }
    }

    private boolean canUseIndexForIn(Column column) {
        if (this.inColumn != null) {
            return false;
        }
        return canUseIndexFor(column);
    }

    private boolean canUseIndexFor(Column column) {
        IndexColumn indexColumn;
        IndexColumn[] indexColumns = this.index.getIndexColumns();
        return indexColumns == null || (indexColumn = indexColumns[0]) == null || indexColumn.column == column;
    }

    private SearchRow getSpatialSearchRow(SearchRow searchRow, int i, Value value) {
        if (searchRow == null) {
            searchRow = this.table.getTemplateRow();
        } else if (searchRow.getValue(i) != null) {
            value = value.convertToGeometry(null).getEnvelopeUnion(searchRow.getValue(i).convertToGeometry(null));
        }
        if (i == -1) {
            searchRow.setKey(value == ValueNull.INSTANCE ? Long.MIN_VALUE : value.getLong());
        } else {
            searchRow.setValue(i, value);
        }
        return searchRow;
    }

    private SearchRow getSearchRow(SearchRow searchRow, int i, Value value, boolean z) {
        if (searchRow == null) {
            searchRow = this.table.getTemplateRow();
        } else {
            value = getMax(searchRow.getValue(i), value, z);
        }
        if (i == -1) {
            searchRow.setKey(value == ValueNull.INSTANCE ? Long.MIN_VALUE : value.getLong());
        } else {
            searchRow.setValue(i, value);
        }
        return searchRow;
    }

    private Value getMax(Value value, Value value2, boolean z) {
        if (value == null) {
            return value2;
        }
        if (value2 == null) {
            return value;
        }
        if (value == ValueNull.INSTANCE) {
            return value2;
        }
        if (value2 == ValueNull.INSTANCE) {
            return value;
        }
        int compare = this.session.compare(value, value2);
        if (compare == 0) {
            return value;
        }
        return (compare > 0) == z ? value : value2;
    }

    public boolean isAlwaysFalse() {
        return this.alwaysFalse;
    }

    public SearchRow getStart() {
        return this.start;
    }

    public SearchRow getEnd() {
        return this.end;
    }

    @Override // org.h2.index.Cursor
    public Row get() {
        if (this.cursor == null) {
            return null;
        }
        return this.cursor.get();
    }

    @Override // org.h2.index.Cursor
    public SearchRow getSearchRow() {
        return this.cursor.getSearchRow();
    }

    @Override // org.h2.index.Cursor
    public boolean next() {
        while (true) {
            if (this.cursor == null) {
                nextCursor();
                if (this.cursor == null) {
                    return false;
                }
            }
            if (this.cursor.next()) {
                return true;
            }
            this.cursor = null;
        }
    }

    private void nextCursor() {
        if (this.inList == null) {
            if (this.inResult == null) {
                return;
            }
            while (this.inResult.next()) {
                Value value = this.inResult.currentRow()[0];
                if (value != ValueNull.INSTANCE) {
                    find(value);
                    return;
                }
            }
            return;
        }
        while (this.inListIndex < this.inList.length) {
            Value[] valueArr = this.inList;
            int i = this.inListIndex;
            this.inListIndex = i + 1;
            Value value2 = valueArr[i];
            if (value2 != ValueNull.INSTANCE) {
                find(value2);
                return;
            }
        }
    }

    private void find(Value value) {
        Value convert = this.inColumn.convert(this.session, value);
        this.start.setValue(this.inColumn.getColumnId(), convert);
        this.cursor = this.index.find(this.session, this.start, this.start);
    }

    @Override // org.h2.index.Cursor
    public boolean previous() {
        throw DbException.getInternalError(toString());
    }
}

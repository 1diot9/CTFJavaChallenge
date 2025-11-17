package org.h2.mvstore.db;

import java.util.List;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVDelegateIndex.class */
public class MVDelegateIndex extends MVIndex<Long, SearchRow> {
    private final MVPrimaryIndex mainIndex;

    public MVDelegateIndex(MVTable mVTable, int i, String str, MVPrimaryIndex mVPrimaryIndex, IndexType indexType) {
        super(mVTable, i, str, IndexColumn.wrap(new Column[]{mVTable.getColumn(mVPrimaryIndex.getMainIndexColumn())}), 1, indexType);
        this.mainIndex = mVPrimaryIndex;
        if (i < 0) {
            throw DbException.getInternalError(str);
        }
    }

    @Override // org.h2.index.Index
    public RowFactory getRowFactory() {
        return this.mainIndex.getRowFactory();
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addRowsToBuffer(List<Row> list, String str) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addBufferedRows(List<String> list) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.mvstore.db.MVIndex
    public MVMap<Long, VersionedValue<SearchRow>> getMVMap() {
        return this.mainIndex.getMVMap();
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
    }

    @Override // org.h2.index.Index
    public Row getRow(SessionLocal sessionLocal, long j) {
        return this.mainIndex.getRow(sessionLocal, j);
    }

    @Override // org.h2.index.Index
    public boolean isRowIdIndex() {
        return true;
    }

    @Override // org.h2.index.Index
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return this.mainIndex.find(sessionLocal, searchRow, searchRow2);
    }

    @Override // org.h2.index.Index
    public Cursor findFirstOrLast(SessionLocal sessionLocal, boolean z) {
        return this.mainIndex.findFirstOrLast(sessionLocal, z);
    }

    @Override // org.h2.index.Index
    public int getColumnIndex(Column column) {
        if (column.getColumnId() == this.mainIndex.getMainIndexColumn()) {
            return 0;
        }
        return -1;
    }

    @Override // org.h2.index.Index
    public boolean isFirstColumn(Column column) {
        return getColumnIndex(column) == 0;
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return 10 * getCostRangeIndex(iArr, this.mainIndex.getRowCountApproximation(sessionLocal), tableFilterArr, i, sortOrder, true, allColumnsForPlan);
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        return false;
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
    }

    @Override // org.h2.index.Index
    public void update(SessionLocal sessionLocal, Row row, Row row2) {
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        this.mainIndex.setMainIndexColumn(-1);
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return this.mainIndex.getRowCount(sessionLocal);
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return this.mainIndex.getRowCountApproximation(sessionLocal);
    }
}

package org.h2.index;

import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.MetaTable;
import org.h2.table.TableFilter;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/MetaIndex.class */
public class MetaIndex extends Index {
    private final MetaTable meta;
    private final boolean scan;

    public MetaIndex(MetaTable metaTable, IndexColumn[] indexColumnArr, boolean z) {
        super(metaTable, 0, null, indexColumnArr, 0, IndexType.createNonUnique(true));
        this.meta = metaTable;
        this.scan = z;
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return new MetaCursor(this.meta.generateRows(sessionLocal, searchRow, searchRow2));
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        if (this.scan) {
            return 10000.0d;
        }
        return getCostRangeIndex(iArr, 1000L, tableFilterArr, i, sortOrder, false, allColumnsForPlan);
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.index.Index
    public int getColumnIndex(Column column) {
        if (this.scan) {
            return -1;
        }
        return super.getColumnIndex(column);
    }

    @Override // org.h2.index.Index
    public boolean isFirstColumn(Column column) {
        if (this.scan) {
            return false;
        }
        return super.isFirstColumn(column);
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("META");
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        return false;
    }

    @Override // org.h2.index.Index, org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return 1000L;
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return 1000L;
    }

    @Override // org.h2.index.Index
    public long getDiskSpaceUsed() {
        return this.meta.getDiskSpaceUsed();
    }

    @Override // org.h2.index.Index
    public String getPlanSQL() {
        return BeanDefinitionParserDelegate.META_ELEMENT;
    }
}

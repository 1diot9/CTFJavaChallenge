package org.h2.mvstore.db;

import java.util.Iterator;
import java.util.List;
import org.h2.api.ErrorCode;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.index.SpatialIndex;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.Page;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.Spatial;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.tx.VersionedValueType;
import org.h2.mvstore.type.DataType;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueNull;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSpatialIndex.class */
public class MVSpatialIndex extends MVIndex<Spatial, Value> implements SpatialIndex {
    final MVTable mvTable;
    private final TransactionMap<Spatial, Value> dataMap;
    private final MVRTreeMap<VersionedValue<Value>> spatialMap;

    public MVSpatialIndex(Database database, MVTable mVTable, int i, String str, IndexColumn[] indexColumnArr, int i2, IndexType indexType) {
        super(mVTable, i, str, indexColumnArr, i2, indexType);
        if (indexColumnArr.length != 1) {
            throw DbException.getUnsupportedException("Can only index one column");
        }
        IndexColumn indexColumn = indexColumnArr[0];
        if ((indexColumn.sortType & 1) != 0) {
            throw DbException.getUnsupportedException("Cannot index in descending order");
        }
        if ((indexColumn.sortType & 2) != 0) {
            throw DbException.getUnsupportedException("Nulls first is not supported");
        }
        if ((indexColumn.sortType & 4) != 0) {
            throw DbException.getUnsupportedException("Nulls last is not supported");
        }
        if (indexColumn.column.getType().getValueType() != 37) {
            throw DbException.getUnsupportedException("Spatial index on non-geometry column, " + indexColumn.column.getCreateSQL());
        }
        this.mvTable = mVTable;
        if (!this.database.isStarting()) {
            checkIndexColumnTypes(indexColumnArr);
        }
        this.spatialMap = (MVRTreeMap) database.getStore().getMvStore().openMap("index." + getId(), new MVRTreeMap.Builder().valueType((DataType) new VersionedValueType(NullValueDataType.INSTANCE)));
        Transaction transactionBegin = this.mvTable.getTransactionBegin();
        this.dataMap = transactionBegin.openMapX(this.spatialMap);
        this.dataMap.map.setVolatile((mVTable.isPersistData() && indexType.isPersistent()) ? false : true);
        transactionBegin.commit();
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addRowsToBuffer(List<Row> list, String str) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addBufferedRows(List<String> list) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        TransactionMap<Spatial, Value> map = getMap(sessionLocal);
        SpatialKey key = getKey(row);
        if (key.isNull()) {
            return;
        }
        if (this.uniqueColumnColumn > 0) {
            SpatialKeyIterator spatialKeyIterator = new SpatialKeyIterator(map, this.spatialMap.findContainedKeys(key), false);
            while (spatialKeyIterator.hasNext()) {
                if (spatialKeyIterator.next().equalsIgnoringId(key)) {
                    throw getDuplicateKeyException(key.toString());
                }
            }
        }
        try {
            map.put(key, ValueNull.INSTANCE);
            if (this.uniqueColumnColumn > 0) {
                SpatialKeyIterator spatialKeyIterator2 = new SpatialKeyIterator(map, this.spatialMap.findContainedKeys(key), true);
                while (spatialKeyIterator2.hasNext()) {
                    Spatial next = spatialKeyIterator2.next();
                    if (next.equalsIgnoringId(key) && !map.isSameTransaction(next)) {
                        map.remove(key);
                        if (map.getImmediate(next) != null) {
                            throw getDuplicateKeyException(next.toString());
                        }
                        throw DbException.get(ErrorCode.CONCURRENT_UPDATE_1, this.table.getName());
                    }
                }
            }
        } catch (MVStoreException e) {
            throw this.mvTable.convertException(e);
        }
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        SpatialKey key = getKey(row);
        if (key.isNull()) {
            return;
        }
        try {
            if (getMap(sessionLocal).remove(key) == null) {
                StringBuilder sb = new StringBuilder();
                getSQL(sb, 3).append(": ").append(row.getKey());
                throw DbException.get(ErrorCode.ROW_NOT_FOUND_WHEN_DELETING_1, sb.toString());
            }
        } catch (MVStoreException e) {
            throw this.mvTable.convertException(e);
        }
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return new MVStoreCursor(sessionLocal, new SpatialKeyIterator(getMap(sessionLocal), this.spatialMap.keyIterator(null), false), this.mvTable);
    }

    @Override // org.h2.index.SpatialIndex
    public Cursor findByGeometry(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2, SearchRow searchRow3) {
        if (searchRow3 == null) {
            return find(sessionLocal, searchRow, searchRow2);
        }
        return new MVStoreCursor(sessionLocal, new SpatialKeyIterator(getMap(sessionLocal), this.spatialMap.findIntersectingKeys(getKey(searchRow3)), false), this.mvTable);
    }

    public Value getBounds(SessionLocal sessionLocal) {
        FindBoundsCursor findBoundsCursor = new FindBoundsCursor(this.spatialMap.getRootPage(), new SpatialKey(0L, new float[0]), sessionLocal, getMap(sessionLocal), this.columnIds[0]);
        while (findBoundsCursor.hasNext()) {
            findBoundsCursor.next();
        }
        return findBoundsCursor.getBounds();
    }

    public Value getEstimatedBounds(SessionLocal sessionLocal) {
        Page<Spatial, VersionedValue<Value>> rootPage = this.spatialMap.getRootPage();
        int keyCount = rootPage.getKeyCount();
        if (keyCount > 0) {
            Spatial key = rootPage.getKey(0);
            float min = key.min(0);
            float max = key.max(0);
            float min2 = key.min(1);
            float max2 = key.max(1);
            for (int i = 1; i < keyCount; i++) {
                Spatial key2 = rootPage.getKey(i);
                float min3 = key2.min(0);
                float max3 = key2.max(0);
                float min4 = key2.min(1);
                float max4 = key2.max(1);
                if (min3 < min) {
                    min = min3;
                }
                if (max3 > max) {
                    max = max3;
                }
                if (min4 < min2) {
                    min2 = min4;
                }
                if (max4 > max2) {
                    max2 = max4;
                }
            }
            return ValueGeometry.fromEnvelope(new double[]{min, max, min2, max2});
        }
        return ValueNull.INSTANCE;
    }

    private SpatialKey getKey(SearchRow searchRow) {
        double[] envelopeNoCopy;
        Value value = searchRow.getValue(this.columnIds[0]);
        if (value == ValueNull.INSTANCE || (envelopeNoCopy = value.convertToGeometry(null).getEnvelopeNoCopy()) == null) {
            return new SpatialKey(searchRow.getKey(), new float[0]);
        }
        return new SpatialKey(searchRow.getKey(), (float) envelopeNoCopy[0], (float) envelopeNoCopy[1], (float) envelopeNoCopy[2], (float) envelopeNoCopy[3]);
    }

    @Override // org.h2.index.Index
    public MVTable getTable() {
        return this.mvTable;
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return getCostRangeIndex(iArr, this.columns);
    }

    public static long getCostRangeIndex(int[] iArr, Column[] columnArr) {
        if (columnArr.length == 0) {
            return Long.MAX_VALUE;
        }
        for (Column column : columnArr) {
            if ((iArr[column.getColumnId()] & 16) != 16) {
                return Long.MAX_VALUE;
            }
        }
        return 2L;
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        TransactionMap<Spatial, Value> map = getMap(sessionLocal);
        if (!map.isClosed()) {
            sessionLocal.getTransaction().removeMap(map);
        }
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
        getMap(sessionLocal).clear();
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        try {
            return this.dataMap.sizeAsLongMax() == 0;
        } catch (MVStoreException e) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED, e, new String[0]);
        }
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return getMap(sessionLocal).sizeAsLong();
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        try {
            return this.dataMap.sizeAsLongMax();
        } catch (MVStoreException e) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED, e, new String[0]);
        }
    }

    @Override // org.h2.index.Index
    public long getDiskSpaceUsed() {
        return 0L;
    }

    private TransactionMap<Spatial, Value> getMap(SessionLocal sessionLocal) {
        if (sessionLocal == null) {
            return this.dataMap;
        }
        return this.dataMap.getInstance(sessionLocal.getTransaction());
    }

    @Override // org.h2.mvstore.db.MVIndex
    public MVMap<Spatial, VersionedValue<Value>> getMVMap() {
        return this.dataMap.map;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSpatialIndex$MVStoreCursor.class */
    public static class MVStoreCursor implements Cursor {
        private final SessionLocal session;
        private final Iterator<Spatial> it;
        private final MVTable mvTable;
        private Spatial current;
        private SearchRow searchRow;
        private Row row;

        MVStoreCursor(SessionLocal sessionLocal, Iterator<Spatial> it, MVTable mVTable) {
            this.session = sessionLocal;
            this.it = it;
            this.mvTable = mVTable;
        }

        @Override // org.h2.index.Cursor
        public Row get() {
            SearchRow searchRow;
            if (this.row == null && (searchRow = getSearchRow()) != null) {
                this.row = this.mvTable.getRow(this.session, searchRow.getKey());
            }
            return this.row;
        }

        @Override // org.h2.index.Cursor
        public SearchRow getSearchRow() {
            if (this.searchRow == null && this.current != null) {
                this.searchRow = this.mvTable.getTemplateRow();
                this.searchRow.setKey(this.current.getId());
            }
            return this.searchRow;
        }

        @Override // org.h2.index.Cursor
        public boolean next() {
            this.current = this.it.hasNext() ? this.it.next() : null;
            this.searchRow = null;
            this.row = null;
            return this.current != null;
        }

        @Override // org.h2.index.Cursor
        public boolean previous() {
            throw DbException.getUnsupportedException("previous");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSpatialIndex$SpatialKeyIterator.class */
    public static class SpatialKeyIterator implements Iterator<Spatial> {
        private final TransactionMap<Spatial, Value> map;
        private final Iterator<Spatial> iterator;
        private final boolean includeUncommitted;
        private Spatial current;

        SpatialKeyIterator(TransactionMap<Spatial, Value> transactionMap, Iterator<Spatial> it, boolean z) {
            this.map = transactionMap;
            this.iterator = it;
            this.includeUncommitted = z;
            fetchNext();
        }

        private void fetchNext() {
            while (this.iterator.hasNext()) {
                this.current = this.iterator.next();
                if (this.includeUncommitted || this.map.containsKey(this.current)) {
                    return;
                }
            }
            this.current = null;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.current != null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Spatial next() {
            Spatial spatial = this.current;
            fetchNext();
            return spatial;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSpatialIndex$FindBoundsCursor.class */
    private final class FindBoundsCursor extends MVRTreeMap.RTreeCursor<VersionedValue<Value>> {
        private final SessionLocal session;
        private final TransactionMap<Spatial, Value> map;
        private final int columnId;
        private boolean hasBounds;
        private float bminxf;
        private float bmaxxf;
        private float bminyf;
        private float bmaxyf;
        private double bminxd;
        private double bmaxxd;
        private double bminyd;
        private double bmaxyd;

        FindBoundsCursor(Page<Spatial, VersionedValue<Value>> page, Spatial spatial, SessionLocal sessionLocal, TransactionMap<Spatial, Value> transactionMap, int i) {
            super(page, spatial);
            this.session = sessionLocal;
            this.map = transactionMap;
            this.columnId = i;
        }

        @Override // org.h2.mvstore.rtree.MVRTreeMap.RTreeCursor
        protected boolean check(boolean z, Spatial spatial, Spatial spatial2) {
            float min = spatial.min(0);
            float max = spatial.max(0);
            float min2 = spatial.min(1);
            float max2 = spatial.max(1);
            if (z) {
                if (this.hasBounds) {
                    if ((min <= this.bminxf || max >= this.bmaxxf || min2 <= this.bminyf || max2 >= this.bmaxyf) && this.map.containsKey(spatial)) {
                        double[] envelopeNoCopy = ((ValueGeometry) MVSpatialIndex.this.mvTable.getRow(this.session, spatial.getId()).getValue(this.columnId)).getEnvelopeNoCopy();
                        double d = envelopeNoCopy[0];
                        double d2 = envelopeNoCopy[1];
                        double d3 = envelopeNoCopy[2];
                        double d4 = envelopeNoCopy[3];
                        if (d < this.bminxd) {
                            this.bminxf = min;
                            this.bminxd = d;
                        }
                        if (d2 > this.bmaxxd) {
                            this.bmaxxf = max;
                            this.bmaxxd = d2;
                        }
                        if (d3 < this.bminyd) {
                            this.bminyf = min2;
                            this.bminyd = d3;
                        }
                        if (d4 > this.bmaxyd) {
                            this.bmaxyf = max2;
                            this.bmaxyd = d4;
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                if (this.map.containsKey(spatial)) {
                    this.hasBounds = true;
                    double[] envelopeNoCopy2 = ((ValueGeometry) MVSpatialIndex.this.mvTable.getRow(this.session, spatial.getId()).getValue(this.columnId)).getEnvelopeNoCopy();
                    this.bminxf = min;
                    this.bminxd = envelopeNoCopy2[0];
                    this.bmaxxf = max;
                    this.bmaxxd = envelopeNoCopy2[1];
                    this.bminyf = min2;
                    this.bminyd = envelopeNoCopy2[2];
                    this.bmaxyf = max2;
                    this.bmaxyd = envelopeNoCopy2[3];
                    return false;
                }
                return false;
            }
            if (!this.hasBounds || min <= this.bminxf || max >= this.bmaxxf || min2 <= this.bminyf || max2 >= this.bmaxyf) {
                return true;
            }
            return false;
        }

        Value getBounds() {
            return this.hasBounds ? ValueGeometry.fromEnvelope(new double[]{this.bminxd, this.bmaxxd, this.bminyd, this.bmaxyd}) : ValueNull.INSTANCE;
        }
    }
}

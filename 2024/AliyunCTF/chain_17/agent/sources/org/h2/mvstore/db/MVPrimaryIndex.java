package org.h2.mvstore.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.api.ErrorCode;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.index.SingleRowCursor;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.type.LongDataType;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVPrimaryIndex.class */
public class MVPrimaryIndex extends MVIndex<Long, SearchRow> {
    private final MVTable mvTable;
    private final String mapName;
    private final TransactionMap<Long, SearchRow> dataMap;
    private final AtomicLong lastKey;
    private int mainIndexColumn;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !MVPrimaryIndex.class.desiredAssertionStatus();
    }

    public MVPrimaryIndex(Database database, MVTable mVTable, int i, IndexColumn[] indexColumnArr, IndexType indexType) {
        super(mVTable, i, mVTable.getName() + "_DATA", indexColumnArr, 0, indexType);
        this.lastKey = new AtomicLong();
        this.mainIndexColumn = -1;
        this.mvTable = mVTable;
        RowDataType rowDataType = mVTable.getRowFactory().getRowDataType();
        this.mapName = "table." + getId();
        Transaction transactionBegin = this.mvTable.getTransactionBegin();
        this.dataMap = transactionBegin.openMap(this.mapName, LongDataType.INSTANCE, rowDataType);
        this.dataMap.map.setVolatile((mVTable.isPersistData() && indexType.isPersistent()) ? false : true);
        if (!database.isStarting()) {
            this.dataMap.clear();
        }
        transactionBegin.commit();
        Long lastKey = this.dataMap.map.lastKey();
        this.lastKey.set(lastKey == null ? 0L : lastKey.longValue());
    }

    @Override // org.h2.index.Index, org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.index.Index
    public String getPlanSQL() {
        return this.table.getSQL(new StringBuilder(), 3).append(".tableScan").toString();
    }

    public void setMainIndexColumn(int i) {
        this.mainIndexColumn = i;
    }

    public int getMainIndexColumn() {
        return this.mainIndexColumn;
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        if (this.mainIndexColumn != -1) {
            row.setKey(row.getValue(this.mainIndexColumn).getLong());
        } else if (row.getKey() == 0) {
            row.setKey(this.lastKey.incrementAndGet());
        }
        if (this.mvTable.getContainsLargeObject()) {
            int columnCount = row.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                Value value = row.getValue(i);
                if (value instanceof ValueLob) {
                    ValueLob copy = ((ValueLob) value).copy(this.database, getId());
                    sessionLocal.removeAtCommitStop(copy);
                    if (value != copy) {
                        row.setValue(i, copy);
                    }
                }
            }
        }
        TransactionMap<Long, SearchRow> map = getMap(sessionLocal);
        long key = row.getKey();
        try {
            Row row2 = (Row) map.putIfAbsent(Long.valueOf(key), row);
            if (row2 != null) {
                int i2 = 90131;
                if (map.getImmediate(Long.valueOf(key)) != null || map.getFromSnapshot(Long.valueOf(key)) != null) {
                    i2 = 23505;
                }
                DbException dbException = DbException.get(i2, getDuplicatePrimaryKeyMessage(this.mainIndexColumn).append(' ').append(row2).toString());
                dbException.setSource(this);
                throw dbException;
            }
            while (key > this.lastKey.get() && !this.lastKey.compareAndSet(key, key)) {
            }
        } catch (MVStoreException e) {
            throw this.mvTable.convertException(e);
        }
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        if (this.mvTable.getContainsLargeObject()) {
            int columnCount = row.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                Value value = row.getValue(i);
                if (value instanceof ValueLob) {
                    sessionLocal.removeAtCommit((ValueLob) value);
                }
            }
        }
        try {
            if (((Row) getMap(sessionLocal).remove(Long.valueOf(row.getKey()))) == null) {
                StringBuilder sb = new StringBuilder();
                getSQL(sb, 3).append(": ").append(row.getKey());
                throw DbException.get(ErrorCode.ROW_NOT_FOUND_WHEN_DELETING_1, sb.toString());
            }
        } catch (MVStoreException e) {
            throw this.mvTable.convertException(e);
        }
    }

    @Override // org.h2.index.Index
    public void update(SessionLocal sessionLocal, Row row, Row row2) {
        if (this.mainIndexColumn != -1) {
            row2.setKey(row2.getValue(this.mainIndexColumn).getLong());
        }
        long key = row.getKey();
        if (!$assertionsDisabled && this.mainIndexColumn == -1 && key == 0) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && key != row2.getKey()) {
            throw new AssertionError(key + " != " + row2.getKey());
        }
        if (this.mvTable.getContainsLargeObject()) {
            int columnCount = row.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                Value value = row.getValue(i);
                Value value2 = row2.getValue(i);
                if (value != value2) {
                    if (value instanceof ValueLob) {
                        sessionLocal.removeAtCommit((ValueLob) value);
                    }
                    if (value2 instanceof ValueLob) {
                        ValueLob copy = ((ValueLob) value2).copy(this.database, getId());
                        sessionLocal.removeAtCommitStop(copy);
                        if (value2 != copy) {
                            row2.setValue(i, copy);
                        }
                    }
                }
            }
        }
        try {
            if (((Row) getMap(sessionLocal).put(Long.valueOf(key), row2)) == null) {
                StringBuilder sb = new StringBuilder();
                getSQL(sb, 3).append(": ").append(key);
                throw DbException.get(ErrorCode.ROW_NOT_FOUND_WHEN_DELETING_1, sb.toString());
            }
            if (row2.getKey() > this.lastKey.get()) {
                this.lastKey.set(row2.getKey());
            }
        } catch (MVStoreException e) {
            throw this.mvTable.convertException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Row lockRow(SessionLocal sessionLocal, Row row, int i) {
        return lockRow(getMap(sessionLocal), row.getKey(), i);
    }

    private Row lockRow(TransactionMap<Long, SearchRow> transactionMap, long j, int i) {
        try {
            return setRowKey((Row) transactionMap.lock(Long.valueOf(j), i), j);
        } catch (MVStoreException e) {
            throw this.mvTable.convertLockException(e);
        }
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return find(sessionLocal, Long.valueOf(extractPKFromRow(searchRow, Long.MIN_VALUE)), Long.valueOf(extractPKFromRow(searchRow2, Long.MAX_VALUE)));
    }

    private long extractPKFromRow(SearchRow searchRow, long j) {
        long j2;
        if (searchRow == null) {
            j2 = j;
        } else if (this.mainIndexColumn == -1) {
            j2 = searchRow.getKey();
        } else {
            Value value = searchRow.getValue(this.mainIndexColumn);
            if (value == null) {
                j2 = searchRow.getKey();
            } else if (value == ValueNull.INSTANCE) {
                j2 = 0;
            } else {
                j2 = value.getLong();
            }
        }
        return j2;
    }

    @Override // org.h2.index.Index
    public MVTable getTable() {
        return this.mvTable;
    }

    @Override // org.h2.index.Index
    public Row getRow(SessionLocal sessionLocal, long j) {
        Row row = (Row) getMap(sessionLocal).getFromSnapshot(Long.valueOf(j));
        if (row == null) {
            throw DbException.get(ErrorCode.ROW_NOT_FOUND_IN_PRIMARY_INDEX, getTraceSQL(), String.valueOf(j));
        }
        return setRowKey(row, j);
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        try {
            return 10 * getCostRangeIndex(iArr, this.dataMap.sizeAsLongMax(), tableFilterArr, i, sortOrder, true, allColumnsForPlan);
        } catch (MVStoreException e) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED, e, new String[0]);
        }
    }

    @Override // org.h2.index.Index
    public int getColumnIndex(Column column) {
        return -1;
    }

    @Override // org.h2.index.Index
    public boolean isFirstColumn(Column column) {
        return false;
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        TransactionMap<Long, SearchRow> map = getMap(sessionLocal);
        if (!map.isClosed()) {
            sessionLocal.getTransaction().removeMap(map);
        }
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
        if (this.mvTable.getContainsLargeObject()) {
            this.database.getLobStorage().removeAllForTable(this.table.getId());
        }
        getMap(sessionLocal).clear();
    }

    @Override // org.h2.index.Index
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override // org.h2.index.Index
    public Cursor findFirstOrLast(SessionLocal sessionLocal, boolean z) {
        TransactionMap<Long, SearchRow> map = getMap(sessionLocal);
        Map.Entry<Long, SearchRow> firstEntry = z ? map.firstEntry() : map.lastEntry();
        return new SingleRowCursor(firstEntry != null ? setRowKey((Row) firstEntry.getValue(), firstEntry.getKey().longValue()) : null);
    }

    @Override // org.h2.index.Index
    public boolean needRebuild() {
        return false;
    }

    @Override // org.h2.index.Index
    public long getRowCount(SessionLocal sessionLocal) {
        return getMap(sessionLocal).sizeAsLong();
    }

    public long getRowCountMax() {
        return this.dataMap.sizeAsLongMax();
    }

    @Override // org.h2.index.Index
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return getRowCountMax();
    }

    @Override // org.h2.index.Index
    public long getDiskSpaceUsed() {
        return this.dataMap.map.getRootPage().getDiskSpaceUsed();
    }

    public String getMapName() {
        return this.mapName;
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addRowsToBuffer(List<Row> list, String str) {
        throw new UnsupportedOperationException();
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addBufferedRows(List<String> list) {
        throw new UnsupportedOperationException();
    }

    private Cursor find(SessionLocal sessionLocal, Long l, Long l2) {
        TransactionMap<Long, SearchRow> map = getMap(sessionLocal);
        if (l != null && l2 != null && l.longValue() == l2.longValue()) {
            return new SingleRowCursor(setRowKey((Row) map.getFromSnapshot(l), l.longValue()));
        }
        return new MVStoreCursor(map.entryIterator(l, l2));
    }

    @Override // org.h2.index.Index
    public boolean isRowIdIndex() {
        return true;
    }

    TransactionMap<Long, SearchRow> getMap(SessionLocal sessionLocal) {
        if (sessionLocal == null) {
            return this.dataMap;
        }
        return this.dataMap.getInstance(sessionLocal.getTransaction());
    }

    @Override // org.h2.mvstore.db.MVIndex
    public MVMap<Long, VersionedValue<SearchRow>> getMVMap() {
        return this.dataMap.map;
    }

    private static Row setRowKey(Row row, long j) {
        if (row != null && row.getKey() == 0) {
            row.setKey(j);
        }
        return row;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVPrimaryIndex$MVStoreCursor.class */
    public static final class MVStoreCursor implements Cursor {
        private final TransactionMap.TMIterator<Long, SearchRow, Map.Entry<Long, SearchRow>> it;
        private Map.Entry<Long, SearchRow> current;
        private Row row;

        public MVStoreCursor(TransactionMap.TMIterator<Long, SearchRow, Map.Entry<Long, SearchRow>> tMIterator) {
            this.it = tMIterator;
        }

        @Override // org.h2.index.Cursor
        public Row get() {
            if (this.row == null && this.current != null) {
                this.row = (Row) this.current.getValue();
                if (this.row.getKey() == 0) {
                    this.row.setKey(this.current.getKey().longValue());
                }
            }
            return this.row;
        }

        @Override // org.h2.index.Cursor
        public SearchRow getSearchRow() {
            return get();
        }

        @Override // org.h2.index.Cursor
        public boolean next() {
            this.current = this.it.fetchNext();
            this.row = null;
            return this.current != null;
        }

        @Override // org.h2.index.Cursor
        public boolean previous() {
            throw DbException.getUnsupportedException("previous");
        }
    }
}

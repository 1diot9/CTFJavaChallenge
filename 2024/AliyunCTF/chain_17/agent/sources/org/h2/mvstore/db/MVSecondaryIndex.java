package org.h2.mvstore.db;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import org.h2.api.ErrorCode;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.index.SingleRowCursor;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.type.DataType;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSecondaryIndex.class */
public final class MVSecondaryIndex extends MVIndex<SearchRow, Value> {
    private final MVTable mvTable;
    private final TransactionMap<SearchRow, Value> dataMap;

    public MVSecondaryIndex(Database database, MVTable mVTable, int i, String str, IndexColumn[] indexColumnArr, int i2, IndexType indexType) {
        super(mVTable, i, str, indexColumnArr, i2, indexType);
        this.mvTable = mVTable;
        if (!this.database.isStarting()) {
            checkIndexColumnTypes(indexColumnArr);
        }
        String str2 = "index." + getId();
        RowDataType rowDataType = getRowFactory().getRowDataType();
        Transaction transactionBegin = this.mvTable.getTransactionBegin();
        this.dataMap = transactionBegin.openMap(str2, rowDataType, NullValueDataType.INSTANCE);
        this.dataMap.map.setVolatile((mVTable.isPersistData() && indexType.isPersistent()) ? false : true);
        if (!database.isStarting()) {
            this.dataMap.clear();
        }
        transactionBegin.commit();
        if (!rowDataType.equals(this.dataMap.getKeyType())) {
            throw DbException.getInternalError("Incompatible key type, expected " + rowDataType + " but got " + this.dataMap.getKeyType() + " for index " + str);
        }
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addRowsToBuffer(List<Row> list, String str) {
        MVMap<SearchRow, Value> openMap = openMap(str);
        for (Row row : list) {
            SearchRow createRow = getRowFactory().createRow();
            createRow.copyFrom(row);
            openMap.append(createRow, ValueNull.INSTANCE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSecondaryIndex$Source.class */
    public static final class Source {
        private final Iterator<SearchRow> iterator;
        SearchRow currentRowData;
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !MVSecondaryIndex.class.desiredAssertionStatus();
        }

        public Source(Iterator<SearchRow> it) {
            if (!$assertionsDisabled && !it.hasNext()) {
                throw new AssertionError();
            }
            this.iterator = it;
            this.currentRowData = it.next();
        }

        public boolean hasNext() {
            boolean hasNext = this.iterator.hasNext();
            if (hasNext) {
                this.currentRowData = this.iterator.next();
            }
            return hasNext;
        }

        public SearchRow next() {
            return this.currentRowData;
        }

        /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSecondaryIndex$Source$Comparator.class */
        static final class Comparator implements java.util.Comparator<Source> {
            private final DataType<SearchRow> type;

            public Comparator(DataType<SearchRow> dataType) {
                this.type = dataType;
            }

            @Override // java.util.Comparator
            public int compare(Source source, Source source2) {
                return this.type.compare(source.currentRowData, source2.currentRowData);
            }
        }
    }

    @Override // org.h2.mvstore.db.MVIndex
    public void addBufferedRows(List<String> list) {
        PriorityQueue priorityQueue = new PriorityQueue(list.size(), new Source.Comparator(getRowFactory().getRowDataType()));
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            Iterator<SearchRow> keyIterator = openMap(it.next()).keyIterator(null);
            if (keyIterator.hasNext()) {
                priorityQueue.offer(new Source(keyIterator));
            }
        }
        while (!priorityQueue.isEmpty()) {
            try {
                Source source = (Source) priorityQueue.poll();
                SearchRow next = source.next();
                if (needsUniqueCheck(next)) {
                    checkUnique(false, this.dataMap, next, Long.MIN_VALUE);
                }
                this.dataMap.putCommitted(next, ValueNull.INSTANCE);
                if (source.hasNext()) {
                    priorityQueue.offer(source);
                }
            } finally {
                MVStore mvStore = this.database.getStore().getMvStore();
                Iterator<String> it2 = list.iterator();
                while (it2.hasNext()) {
                    mvStore.removeMap(it2.next());
                }
            }
        }
    }

    private MVMap<SearchRow, Value> openMap(String str) {
        RowDataType rowDataType = getRowFactory().getRowDataType();
        MVMap<SearchRow, Value> openMap = this.database.getStore().getMvStore().openMap(str, new MVMap.Builder().singleWriter().keyType((DataType) rowDataType).valueType((DataType) NullValueDataType.INSTANCE));
        if (!rowDataType.equals(openMap.getKeyType())) {
            throw DbException.getInternalError("Incompatible key type, expected " + rowDataType + " but got " + openMap.getKeyType() + " for map " + str);
        }
        return openMap;
    }

    @Override // org.h2.index.Index
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.index.Index
    public void add(SessionLocal sessionLocal, Row row) {
        TransactionMap<SearchRow, Value> map = getMap(sessionLocal);
        SearchRow convertToKey = convertToKey(row, null);
        boolean needsUniqueCheck = needsUniqueCheck(row);
        if (needsUniqueCheck) {
            checkUnique(!sessionLocal.getTransaction().allowNonRepeatableRead(), map, row, Long.MIN_VALUE);
        }
        try {
            map.put(convertToKey, ValueNull.INSTANCE);
            if (needsUniqueCheck) {
                checkUnique(false, map, row, row.getKey());
            }
        } catch (MVStoreException e) {
            throw this.mvTable.convertException(e);
        }
    }

    private void checkUnique(boolean z, TransactionMap<SearchRow, Value> transactionMap, SearchRow searchRow, long j) {
        SearchRow fetchNext;
        RowFactory uniqueRowFactory = getUniqueRowFactory();
        SearchRow createRow = uniqueRowFactory.createRow();
        createRow.copyFrom(searchRow);
        createRow.setKey(Long.MIN_VALUE);
        SearchRow createRow2 = uniqueRowFactory.createRow();
        createRow2.copyFrom(searchRow);
        createRow2.setKey(Long.MAX_VALUE);
        if (z) {
            TransactionMap.TMIterator<SearchRow, Value, SearchRow> keyIterator = transactionMap.keyIterator(createRow, createRow2);
            while (true) {
                SearchRow fetchNext2 = keyIterator.fetchNext();
                if (fetchNext2 == null) {
                    break;
                } else if (j != fetchNext2.getKey() && !transactionMap.isDeletedByCurrentTransaction(fetchNext2)) {
                    throw getDuplicateKeyException(fetchNext2.toString());
                }
            }
        }
        TransactionMap.TMIterator<SearchRow, Value, SearchRow> keyIteratorUncommitted = transactionMap.keyIteratorUncommitted(createRow, createRow2);
        do {
            fetchNext = keyIteratorUncommitted.fetchNext();
            if (fetchNext == null) {
                return;
            }
        } while (j == fetchNext.getKey());
        if (transactionMap.getImmediate(fetchNext) != null) {
            throw getDuplicateKeyException(fetchNext.toString());
        }
        throw DbException.get(ErrorCode.CONCURRENT_UPDATE_1, this.table.getName());
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal, Row row) {
        try {
            if (getMap(sessionLocal).remove(convertToKey(row, null)) == null) {
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
        if (!rowsAreEqual(convertToKey(row, null), convertToKey(row2, null))) {
            super.update(sessionLocal, row, row2);
        }
    }

    private boolean rowsAreEqual(SearchRow searchRow, SearchRow searchRow2) {
        if (searchRow == searchRow2) {
            return true;
        }
        for (int i : this.columnIds) {
            if (!Objects.equals(searchRow.getValue(i), searchRow2.getValue(i))) {
                return false;
            }
        }
        return searchRow.getKey() == searchRow2.getKey();
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return find(sessionLocal, searchRow, false, searchRow2);
    }

    private Cursor find(SessionLocal sessionLocal, SearchRow searchRow, boolean z, SearchRow searchRow2) {
        return new MVStoreCursor(sessionLocal, getMap(sessionLocal).keyIterator(convertToKey(searchRow, Boolean.valueOf(z)), convertToKey(searchRow2, Boolean.TRUE)), this.mvTable);
    }

    private SearchRow convertToKey(SearchRow searchRow, Boolean bool) {
        if (searchRow == null) {
            return null;
        }
        SearchRow createRow = getRowFactory().createRow();
        createRow.copyFrom(searchRow);
        if (bool != null) {
            createRow.setKey(bool.booleanValue() ? Long.MAX_VALUE : Long.MIN_VALUE);
        }
        return createRow;
    }

    @Override // org.h2.index.Index
    public MVTable getTable() {
        return this.mvTable;
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        try {
            return 10 * getCostRangeIndex(iArr, this.dataMap.sizeAsLongMax(), tableFilterArr, i, sortOrder, false, allColumnsForPlan);
        } catch (MVStoreException e) {
            throw DbException.get(ErrorCode.OBJECT_CLOSED, e, new String[0]);
        }
    }

    @Override // org.h2.index.Index
    public void remove(SessionLocal sessionLocal) {
        TransactionMap<SearchRow, Value> map = getMap(sessionLocal);
        if (!map.isClosed()) {
            sessionLocal.getTransaction().removeMap(map);
        }
    }

    @Override // org.h2.index.Index
    public void truncate(SessionLocal sessionLocal) {
        getMap(sessionLocal).clear();
    }

    @Override // org.h2.index.Index
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override // org.h2.index.Index
    public Cursor findFirstOrLast(SessionLocal sessionLocal, boolean z) {
        SearchRow fetchNext;
        TransactionMap.TMIterator<SearchRow, Value, SearchRow> keyIterator = getMap(sessionLocal).keyIterator((TransactionMap<SearchRow, Value>) null, !z);
        do {
            fetchNext = keyIterator.fetchNext();
            if (fetchNext == null) {
                return new SingleRowCursor(null);
            }
        } while (fetchNext.getValue(this.columnIds[0]) == ValueNull.INSTANCE);
        return new SingleRowCursor(this.mvTable.getRow(sessionLocal, fetchNext.getKey()));
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

    @Override // org.h2.index.Index
    public boolean canFindNext() {
        return true;
    }

    @Override // org.h2.index.Index
    public Cursor findNext(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        return find(sessionLocal, searchRow, true, searchRow2);
    }

    private TransactionMap<SearchRow, Value> getMap(SessionLocal sessionLocal) {
        if (sessionLocal == null) {
            return this.dataMap;
        }
        return this.dataMap.getInstance(sessionLocal.getTransaction());
    }

    @Override // org.h2.mvstore.db.MVIndex
    public MVMap<SearchRow, VersionedValue<Value>> getMVMap() {
        return this.dataMap.map;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVSecondaryIndex$MVStoreCursor.class */
    public static final class MVStoreCursor implements Cursor {
        private final SessionLocal session;
        private final TransactionMap.TMIterator<SearchRow, Value, SearchRow> it;
        private final MVTable mvTable;
        private SearchRow current;
        private Row row;

        MVStoreCursor(SessionLocal sessionLocal, TransactionMap.TMIterator<SearchRow, Value, SearchRow> tMIterator, MVTable mVTable) {
            this.session = sessionLocal;
            this.it = tMIterator;
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
            return this.current;
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

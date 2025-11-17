package org.h2.mvstore.db;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.api.ErrorCode;
import org.h2.command.ddl.CreateTableData;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintReferential;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.mode.DefaultNullOrdering;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableBase;
import org.h2.table.TableType;
import org.h2.util.DebuggingThreadLocal;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVTable.class */
public class MVTable extends TableBase {
    public static final DebuggingThreadLocal<String> WAITING_FOR_LOCK;
    public static final DebuggingThreadLocal<ArrayList<String>> EXCLUSIVE_LOCKS;
    public static final DebuggingThreadLocal<ArrayList<String>> SHARED_LOCKS;
    private static final String NO_EXTRA_INFO = "";
    private final boolean containsLargeObject;
    private volatile SessionLocal lockExclusiveSession;
    private final ConcurrentHashMap<SessionLocal, SessionLocal> lockSharedSessions;
    private Column rowIdColumn;
    private final MVPrimaryIndex primaryIndex;
    private final ArrayList<Index> indexes;
    private final AtomicLong lastModificationId;
    private final ArrayDeque<SessionLocal> waitingSessions;
    private final Trace traceLock;
    private final AtomicInteger changesUntilAnalyze;
    private int nextAnalyze;
    private final Store store;
    private final TransactionStore transactionStore;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/db/MVTable$TraceLockEvent.class */
    public enum TraceLockEvent {
        TRACE_LOCK_OK("ok"),
        TRACE_LOCK_WAITING_FOR("waiting for"),
        TRACE_LOCK_REQUESTING_FOR("requesting for"),
        TRACE_LOCK_TIMEOUT_AFTER("timeout after "),
        TRACE_LOCK_UNLOCK("unlock"),
        TRACE_LOCK_ADDED_FOR("added for"),
        TRACE_LOCK_ADD_UPGRADED_FOR("add (upgraded) for ");

        private final String eventText;

        TraceLockEvent(String str) {
            this.eventText = str;
        }

        public String getEventText() {
            return this.eventText;
        }
    }

    static {
        if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
            WAITING_FOR_LOCK = new DebuggingThreadLocal<>();
            EXCLUSIVE_LOCKS = new DebuggingThreadLocal<>();
            SHARED_LOCKS = new DebuggingThreadLocal<>();
        } else {
            WAITING_FOR_LOCK = null;
            EXCLUSIVE_LOCKS = null;
            SHARED_LOCKS = null;
        }
    }

    public MVTable(CreateTableData createTableData, Store store) {
        super(createTableData);
        this.lockSharedSessions = new ConcurrentHashMap<>();
        this.indexes = Utils.newSmallArrayList();
        this.lastModificationId = new AtomicLong();
        this.waitingSessions = new ArrayDeque<>();
        this.isHidden = createTableData.isHidden;
        boolean z = false;
        Column[] columns = getColumns();
        int length = columns.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (!DataType.isLargeObject(columns[i].getType().getValueType())) {
                i++;
            } else {
                z = true;
                break;
            }
        }
        this.containsLargeObject = z;
        this.nextAnalyze = this.database.getSettings().analyzeAuto;
        this.changesUntilAnalyze = this.nextAnalyze <= 0 ? null : new AtomicInteger(this.nextAnalyze);
        this.store = store;
        this.transactionStore = store.getTransactionStore();
        this.traceLock = this.database.getTrace(7);
        this.primaryIndex = new MVPrimaryIndex(this.database, this, getId(), IndexColumn.wrap(getColumns()), IndexType.createScan(true));
        this.indexes.add(this.primaryIndex);
    }

    public String getMapName() {
        return this.primaryIndex.getMapName();
    }

    @Override // org.h2.table.Table
    public boolean lock(SessionLocal sessionLocal, int i) {
        if (this.database.getLockMode() == 0) {
            sessionLocal.registerTableAsUpdated(this);
            return false;
        }
        if (i == 0 && this.lockExclusiveSession == null) {
            return false;
        }
        if (this.lockExclusiveSession == sessionLocal) {
            return true;
        }
        if (i != 2 && this.lockSharedSessions.containsKey(sessionLocal)) {
            return true;
        }
        synchronized (this) {
            if (i != 2) {
                if (this.lockSharedSessions.containsKey(sessionLocal)) {
                    return true;
                }
            }
            sessionLocal.setWaitForLock(this, Thread.currentThread());
            if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                WAITING_FOR_LOCK.set(getName());
            }
            this.waitingSessions.addLast(sessionLocal);
            try {
                doLock1(sessionLocal, i);
                sessionLocal.setWaitForLock(null, null);
                if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                    WAITING_FOR_LOCK.remove();
                }
                this.waitingSessions.remove(sessionLocal);
                return false;
            } catch (Throwable th) {
                sessionLocal.setWaitForLock(null, null);
                if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                    WAITING_FOR_LOCK.remove();
                }
                this.waitingSessions.remove(sessionLocal);
                throw th;
            }
        }
    }

    private void doLock1(SessionLocal sessionLocal, int i) {
        traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_REQUESTING_FOR, "");
        long j = 0;
        boolean z = false;
        while (true) {
            if (this.waitingSessions.getFirst() == sessionLocal && this.lockExclusiveSession == null && doLock2(sessionLocal, i)) {
                return;
            }
            if (z) {
                ArrayList<SessionLocal> checkDeadlock = checkDeadlock(sessionLocal, null, null);
                if (checkDeadlock != null) {
                    throw DbException.get(ErrorCode.DEADLOCK_1, getDeadlockDetails(checkDeadlock, i));
                }
            } else {
                z = true;
            }
            long nanoTime = System.nanoTime();
            if (j == 0) {
                j = Utils.nanoTimePlusMillis(nanoTime, sessionLocal.getLockTimeout());
            } else if (nanoTime - j >= 0) {
                traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_TIMEOUT_AFTER, Integer.toString(sessionLocal.getLockTimeout()));
                throw DbException.get(ErrorCode.LOCK_TIMEOUT_1, getName());
            }
            try {
                traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_WAITING_FOR, "");
                long min = Math.min(100L, (j - nanoTime) / 1000000);
                if (min == 0) {
                    min = 1;
                }
                wait(min);
            } catch (InterruptedException e) {
            }
        }
    }

    private boolean doLock2(SessionLocal sessionLocal, int i) {
        switch (i) {
            case 1:
                if (this.lockSharedSessions.putIfAbsent(sessionLocal, sessionLocal) == null) {
                    traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_OK, "");
                    sessionLocal.registerTableAsLocked(this);
                    if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                        addLockToDebugList(SHARED_LOCKS);
                        return true;
                    }
                    return true;
                }
                return true;
            case 2:
                int size = this.lockSharedSessions.size();
                if (size == 0) {
                    traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_ADDED_FOR, "");
                    sessionLocal.registerTableAsLocked(this);
                } else if (size == 1 && this.lockSharedSessions.containsKey(sessionLocal)) {
                    traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_ADD_UPGRADED_FOR, "");
                } else {
                    return false;
                }
                this.lockExclusiveSession = sessionLocal;
                if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
                    addLockToDebugList(EXCLUSIVE_LOCKS);
                    return true;
                }
                return true;
            default:
                return true;
        }
    }

    private void addLockToDebugList(DebuggingThreadLocal<ArrayList<String>> debuggingThreadLocal) {
        ArrayList<String> arrayList = debuggingThreadLocal.get();
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            debuggingThreadLocal.set(arrayList);
        }
        arrayList.add(getName());
    }

    private void traceLock(SessionLocal sessionLocal, int i, TraceLockEvent traceLockEvent, String str) {
        if (this.traceLock.isDebugEnabled()) {
            this.traceLock.debug("{0} {1} {2} {3} {4}", Integer.valueOf(sessionLocal.getId()), lockTypeToString(i), traceLockEvent.getEventText(), getName(), str);
        }
    }

    @Override // org.h2.table.Table
    public void unlock(SessionLocal sessionLocal) {
        int i;
        ArrayList<String> arrayList;
        ArrayList<String> arrayList2;
        if (this.database != null) {
            if (this.lockExclusiveSession == sessionLocal) {
                i = 2;
                this.lockSharedSessions.remove(sessionLocal);
                this.lockExclusiveSession = null;
                if (SysProperties.THREAD_DEADLOCK_DETECTOR && (arrayList2 = EXCLUSIVE_LOCKS.get()) != null) {
                    arrayList2.remove(getName());
                }
            } else {
                i = this.lockSharedSessions.remove(sessionLocal) != null ? 1 : 0;
                if (SysProperties.THREAD_DEADLOCK_DETECTOR && (arrayList = SHARED_LOCKS.get()) != null) {
                    arrayList.remove(getName());
                }
            }
            traceLock(sessionLocal, i, TraceLockEvent.TRACE_LOCK_UNLOCK, "");
            if (i != 0 && !this.waitingSessions.isEmpty()) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }

    @Override // org.h2.table.Table
    public void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.table.Table
    public Row getRow(SessionLocal sessionLocal, long j) {
        return this.primaryIndex.getRow(sessionLocal, j);
    }

    @Override // org.h2.table.Table
    public Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2) {
        MVIndex<?, ?> mVSecondaryIndex;
        IndexColumn[] prepareColumns = prepareColumns(this.database, indexColumnArr, indexType);
        boolean z2 = isTemporary() && !isGlobalTemporary();
        if (!z2) {
            this.database.lockMeta(sessionLocal);
        }
        int mainIndexColumn = this.primaryIndex.getMainIndexColumn() != -1 ? -1 : getMainIndexColumn(indexType, prepareColumns);
        if (this.database.isStarting()) {
            if (this.transactionStore.hasMap("index." + i)) {
                mainIndexColumn = -1;
            }
        } else if (this.primaryIndex.getRowCountMax() != 0) {
            mainIndexColumn = -1;
        }
        if (mainIndexColumn != -1) {
            this.primaryIndex.setMainIndexColumn(mainIndexColumn);
            mVSecondaryIndex = new MVDelegateIndex(this, i, str, this.primaryIndex, indexType);
        } else if (indexType.isSpatial()) {
            mVSecondaryIndex = new MVSpatialIndex(sessionLocal.getDatabase(), this, i, str, prepareColumns, i2, indexType);
        } else {
            mVSecondaryIndex = new MVSecondaryIndex(sessionLocal.getDatabase(), this, i, str, prepareColumns, i2, indexType);
        }
        if (mVSecondaryIndex.needRebuild()) {
            rebuildIndex(sessionLocal, mVSecondaryIndex, str);
        }
        mVSecondaryIndex.setTemporary(isTemporary());
        if (mVSecondaryIndex.getCreateSQL() != null) {
            mVSecondaryIndex.setComment(str2);
            if (z2) {
                sessionLocal.addLocalTempTableIndex(mVSecondaryIndex);
            } else {
                this.database.addSchemaObject(sessionLocal, mVSecondaryIndex);
            }
        }
        this.indexes.add(mVSecondaryIndex);
        setModified();
        return mVSecondaryIndex;
    }

    private void rebuildIndex(SessionLocal sessionLocal, MVIndex<?, ?> mVIndex, String str) {
        try {
            if (!sessionLocal.getDatabase().isPersistent() || (mVIndex instanceof MVSpatialIndex)) {
                rebuildIndexBuffered(sessionLocal, mVIndex);
            } else {
                rebuildIndexBlockMerge(sessionLocal, mVIndex);
            }
        } catch (DbException e) {
            getSchema().freeUniqueName(str);
            try {
                mVIndex.remove(sessionLocal);
                throw e;
            } catch (DbException e2) {
                this.trace.error(e2, "could not remove index");
                throw e2;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v48, types: [org.h2.engine.Database, int] */
    private void rebuildIndexBlockMerge(SessionLocal sessionLocal, MVIndex<?, ?> mVIndex) {
        Index scanIndex = getScanIndex(sessionLocal);
        long rowCount = scanIndex.getRowCount(sessionLocal);
        Cursor find = scanIndex.find(sessionLocal, null, null);
        long j = 0;
        Store store = sessionLocal.getDatabase().getStore();
        int min = (int) Math.min(rowCount, this.database.getMaxMemoryRows() / 2);
        ArrayList arrayList = new ArrayList(min);
        String str = getName() + ':' + mVIndex.getName();
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        while (find.next()) {
            arrayList.add(find.get());
            ?? r0 = this.database;
            long j2 = j;
            j = j2 + 1;
            r0.setProgress(r0, str, j2, rowCount);
            if (arrayList.size() >= min) {
                sortRows(arrayList, mVIndex);
                String nextTemporaryMapName = store.nextTemporaryMapName();
                mVIndex.addRowsToBuffer(arrayList, nextTemporaryMapName);
                newSmallArrayList.add(nextTemporaryMapName);
                arrayList.clear();
            }
            rowCount--;
        }
        sortRows(arrayList, mVIndex);
        if (!newSmallArrayList.isEmpty()) {
            String nextTemporaryMapName2 = store.nextTemporaryMapName();
            mVIndex.addRowsToBuffer(arrayList, nextTemporaryMapName2);
            newSmallArrayList.add(nextTemporaryMapName2);
            arrayList.clear();
            mVIndex.addBufferedRows(newSmallArrayList);
        } else {
            addRowsToIndex(sessionLocal, arrayList, mVIndex);
        }
        if (rowCount != 0) {
            throw DbException.getInternalError("rowcount remaining=" + rowCount + ' ' + getName());
        }
    }

    /* JADX WARN: Type inference failed for: r0v34, types: [org.h2.engine.Database, int] */
    private void rebuildIndexBuffered(SessionLocal sessionLocal, Index index) {
        Index scanIndex = getScanIndex(sessionLocal);
        long rowCount = scanIndex.getRowCount(sessionLocal);
        Cursor find = scanIndex.find(sessionLocal, null, null);
        long j = 0;
        int min = (int) Math.min(rowCount, this.database.getMaxMemoryRows());
        ArrayList arrayList = new ArrayList(min);
        String str = getName() + ':' + index.getName();
        while (find.next()) {
            arrayList.add(find.get());
            ?? r0 = this.database;
            long j2 = j;
            j = j2 + 1;
            r0.setProgress(r0, str, j2, rowCount);
            if (arrayList.size() >= min) {
                addRowsToIndex(sessionLocal, arrayList, index);
            }
            rowCount--;
        }
        addRowsToIndex(sessionLocal, arrayList, index);
        if (rowCount != 0) {
            throw DbException.getInternalError("rowcount remaining=" + rowCount + ' ' + getName());
        }
    }

    @Override // org.h2.table.Table
    public void removeRow(SessionLocal sessionLocal, Row row) {
        syncLastModificationIdWithDatabase();
        Transaction transaction = sessionLocal.getTransaction();
        long savepoint = transaction.setSavepoint();
        try {
            for (int size = this.indexes.size() - 1; size >= 0; size--) {
                this.indexes.get(size).remove(sessionLocal, row);
            }
            analyzeIfRequired(sessionLocal);
        } catch (Throwable th) {
            try {
                transaction.rollbackToSavepoint(savepoint);
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw DbException.convert(th);
        }
    }

    @Override // org.h2.table.Table
    public long truncate(SessionLocal sessionLocal) {
        syncLastModificationIdWithDatabase();
        long rowCountApproximation = getRowCountApproximation(sessionLocal);
        for (int size = this.indexes.size() - 1; size >= 0; size--) {
            this.indexes.get(size).truncate(sessionLocal);
        }
        if (this.changesUntilAnalyze != null) {
            this.changesUntilAnalyze.set(this.nextAnalyze);
        }
        return rowCountApproximation;
    }

    @Override // org.h2.table.Table
    public void addRow(SessionLocal sessionLocal, Row row) {
        syncLastModificationIdWithDatabase();
        Transaction transaction = sessionLocal.getTransaction();
        long savepoint = transaction.setSavepoint();
        try {
            Iterator<Index> it = this.indexes.iterator();
            while (it.hasNext()) {
                it.next().add(sessionLocal, row);
            }
            analyzeIfRequired(sessionLocal);
        } catch (Throwable th) {
            try {
                transaction.rollbackToSavepoint(savepoint);
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw DbException.convert(th);
        }
    }

    @Override // org.h2.table.Table
    public void updateRow(SessionLocal sessionLocal, Row row, Row row2) {
        row2.setKey(row.getKey());
        syncLastModificationIdWithDatabase();
        Transaction transaction = sessionLocal.getTransaction();
        long savepoint = transaction.setSavepoint();
        try {
            Iterator<Index> it = this.indexes.iterator();
            while (it.hasNext()) {
                it.next().update(sessionLocal, row, row2);
            }
            analyzeIfRequired(sessionLocal);
        } catch (Throwable th) {
            try {
                transaction.rollbackToSavepoint(savepoint);
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw DbException.convert(th);
        }
    }

    @Override // org.h2.table.Table
    public Row lockRow(SessionLocal sessionLocal, Row row, int i) {
        Row lockRow = this.primaryIndex.lockRow(sessionLocal, row, i);
        if (lockRow == null || !row.hasSharedData(lockRow)) {
            syncLastModificationIdWithDatabase();
        }
        return lockRow;
    }

    private void analyzeIfRequired(SessionLocal sessionLocal) {
        if (this.changesUntilAnalyze != null && this.changesUntilAnalyze.decrementAndGet() == 0) {
            if (this.nextAnalyze <= 1073741823) {
                this.nextAnalyze *= 2;
            }
            this.changesUntilAnalyze.set(this.nextAnalyze);
            sessionLocal.markTableForAnalyze(this);
        }
    }

    @Override // org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal) {
        return this.primaryIndex;
    }

    @Override // org.h2.table.Table
    public ArrayList<Index> getIndexes() {
        return this.indexes;
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return this.lastModificationId.get();
    }

    @Override // org.h2.table.Table, org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        if (this.containsLargeObject) {
            truncate(sessionLocal);
            this.database.getLobStorage().removeAllForTable(getId());
            this.database.lockMeta(sessionLocal);
        }
        this.database.getStore().removeTable(this);
        super.removeChildrenAndResources(sessionLocal);
        while (this.indexes.size() > 1) {
            Index index = this.indexes.get(1);
            index.remove(sessionLocal);
            if (index.getName() != null) {
                this.database.removeSchemaObject(sessionLocal, index);
            }
            this.indexes.remove(index);
        }
        this.primaryIndex.remove(sessionLocal);
        this.indexes.clear();
        close(sessionLocal);
        invalidate();
    }

    @Override // org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        return this.primaryIndex.getRowCount(sessionLocal);
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return this.primaryIndex.getRowCountApproximation(sessionLocal);
    }

    @Override // org.h2.table.Table
    public long getDiskSpaceUsed() {
        return this.primaryIndex.getDiskSpaceUsed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Transaction getTransactionBegin() {
        return this.transactionStore.begin();
    }

    @Override // org.h2.table.Table
    public boolean isRowLockable() {
        return true;
    }

    public void commit() {
        if (this.database != null) {
            syncLastModificationIdWithDatabase();
        }
    }

    private void syncLastModificationIdWithDatabase() {
        long j;
        long nextModificationDataId = this.database.getNextModificationDataId();
        do {
            j = this.lastModificationId.get();
            if (nextModificationDataId <= j) {
                return;
            }
        } while (!this.lastModificationId.compareAndSet(j, nextModificationDataId));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbException convertException(MVStoreException mVStoreException) {
        return convertException(mVStoreException, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbException convertLockException(MVStoreException mVStoreException) {
        return convertException(mVStoreException, true);
    }

    private DbException convertException(MVStoreException mVStoreException, boolean z) {
        int errorCode = mVStoreException.getErrorCode();
        if (errorCode == 101) {
            return DbException.get(z ? ErrorCode.LOCK_TIMEOUT_1 : ErrorCode.CONCURRENT_UPDATE_1, mVStoreException, getName());
        }
        if (errorCode == 105) {
            return DbException.get(ErrorCode.DEADLOCK_1, mVStoreException, getName());
        }
        return this.store.convertMVStoreException(mVStoreException);
    }

    @Override // org.h2.table.Table
    public int getMainIndexColumn() {
        return this.primaryIndex.getMainIndexColumn();
    }

    private static void addRowsToIndex(SessionLocal sessionLocal, ArrayList<Row> arrayList, Index index) {
        sortRows(arrayList, index);
        Iterator<Row> it = arrayList.iterator();
        while (it.hasNext()) {
            index.add(sessionLocal, it.next());
        }
        arrayList.clear();
    }

    private static String getDeadlockDetails(ArrayList<SessionLocal> arrayList, int i) {
        StringBuilder sb = new StringBuilder();
        Iterator<SessionLocal> it = arrayList.iterator();
        while (it.hasNext()) {
            SessionLocal next = it.next();
            sb.append("\nSession ").append(next).append(" on thread ").append(next.getWaitForLockThread().getName()).append(" is waiting to lock ").append(next.getWaitForLock().toString()).append(" (").append(lockTypeToString(i)).append(") while locking ");
            boolean z = false;
            for (Table table : next.getLocks()) {
                if (z) {
                    sb.append(", ");
                }
                z = true;
                sb.append(table.toString());
                if (table instanceof MVTable) {
                    if (((MVTable) table).lockExclusiveSession == next) {
                        sb.append(" (exclusive)");
                    } else {
                        sb.append(" (shared)");
                    }
                }
            }
            sb.append('.');
        }
        return sb.toString();
    }

    private static String lockTypeToString(int i) {
        return i == 0 ? "shared read" : i == 1 ? "shared write" : "exclusive";
    }

    private static void sortRows(ArrayList<? extends SearchRow> arrayList, Index index) {
        index.getClass();
        arrayList.sort(index::compareRows);
    }

    @Override // org.h2.table.Table
    public boolean canDrop() {
        return true;
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return true;
    }

    @Override // org.h2.table.Table
    public boolean canTruncate() {
        ArrayList<Constraint> constraints;
        if (getCheckForeignKeyConstraints() && this.database.getReferentialIntegrity() && (constraints = getConstraints()) != null) {
            Iterator<Constraint> it = constraints.iterator();
            while (it.hasNext()) {
                Constraint next = it.next();
                if (next.getConstraintType() == Constraint.Type.REFERENTIAL && ((ConstraintReferential) next).getRefTable() == this) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override // org.h2.table.Table
    public ArrayList<SessionLocal> checkDeadlock(SessionLocal sessionLocal, SessionLocal sessionLocal2, Set<SessionLocal> set) {
        Table waitForLock;
        synchronized (getClass()) {
            if (sessionLocal2 == null) {
                sessionLocal2 = sessionLocal;
                set = new HashSet();
            } else {
                if (sessionLocal2 == sessionLocal) {
                    return new ArrayList<>(0);
                }
                if (set.contains(sessionLocal)) {
                    return null;
                }
            }
            set.add(sessionLocal);
            ArrayList<SessionLocal> arrayList = null;
            Iterator it = this.lockSharedSessions.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SessionLocal sessionLocal3 = (SessionLocal) it.next();
                if (sessionLocal3 != sessionLocal) {
                    Table waitForLock2 = sessionLocal3.getWaitForLock();
                    if (waitForLock2 != null) {
                        arrayList = waitForLock2.checkDeadlock(sessionLocal3, sessionLocal2, set);
                        if (arrayList != null) {
                            arrayList.add(sessionLocal);
                            break;
                        }
                    }
                }
            }
            SessionLocal sessionLocal4 = this.lockExclusiveSession;
            if (arrayList == null && sessionLocal4 != null && (waitForLock = sessionLocal4.getWaitForLock()) != null) {
                arrayList = waitForLock.checkDeadlock(sessionLocal4, sessionLocal2, set);
                if (arrayList != null) {
                    arrayList.add(sessionLocal);
                }
            }
            return arrayList;
        }
    }

    @Override // org.h2.table.Table
    public void checkSupportAlter() {
    }

    public boolean getContainsLargeObject() {
        return this.containsLargeObject;
    }

    @Override // org.h2.table.Table
    public Column getRowIdColumn() {
        if (this.rowIdColumn == null) {
            this.rowIdColumn = new Column(Column.ROWID, TypeInfo.TYPE_BIGINT, this, -1);
            this.rowIdColumn.setRowId(true);
            this.rowIdColumn.setNullable(false);
        }
        return this.rowIdColumn;
    }

    @Override // org.h2.table.Table
    public TableType getTableType() {
        return TableType.TABLE;
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return true;
    }

    @Override // org.h2.table.Table
    public boolean isLockedExclusively() {
        return this.lockExclusiveSession != null;
    }

    @Override // org.h2.table.Table
    public boolean isLockedExclusivelyBy(SessionLocal sessionLocal) {
        return this.lockExclusiveSession == sessionLocal;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.engine.DbObject
    public void invalidate() {
        super.invalidate();
        this.lockExclusiveSession = null;
    }

    @Override // org.h2.engine.DbObject
    public String toString() {
        return getTraceSQL();
    }

    private static IndexColumn[] prepareColumns(Database database, IndexColumn[] indexColumnArr, IndexType indexType) {
        if (indexType.isPrimaryKey()) {
            for (IndexColumn indexColumn : indexColumnArr) {
                Column column = indexColumn.column;
                if (column.isNullable()) {
                    throw DbException.get(ErrorCode.COLUMN_MUST_NOT_BE_NULLABLE_1, column.getName());
                }
            }
            for (IndexColumn indexColumn2 : indexColumnArr) {
                indexColumn2.column.setPrimaryKey(true);
            }
        } else if (!indexType.isSpatial()) {
            int i = 0;
            int length = indexColumnArr.length;
            while (i < length && (indexColumnArr[i].sortType & 6) != 0) {
                i++;
            }
            if (i != length) {
                indexColumnArr = (IndexColumn[]) indexColumnArr.clone();
                DefaultNullOrdering defaultNullOrdering = database.getDefaultNullOrdering();
                while (i < length) {
                    IndexColumn indexColumn3 = indexColumnArr[i];
                    int i2 = indexColumn3.sortType;
                    int addExplicitNullOrdering = defaultNullOrdering.addExplicitNullOrdering(i2);
                    if (addExplicitNullOrdering != i2) {
                        IndexColumn indexColumn4 = new IndexColumn(indexColumn3.columnName, addExplicitNullOrdering);
                        indexColumn4.column = indexColumn3.column;
                        indexColumnArr[i] = indexColumn4;
                    }
                    i++;
                }
            }
        }
        return indexColumnArr;
    }
}

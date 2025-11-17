package org.h2.mvstore.tx;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.engine.IsolationLevel;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.DataType;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/Transaction.class */
public final class Transaction {
    public static final int STATUS_CLOSED = 0;
    public static final int STATUS_OPEN = 1;
    public static final int STATUS_PREPARED = 2;
    public static final int STATUS_COMMITTED = 3;
    private static final int STATUS_ROLLING_BACK = 4;
    private static final int STATUS_ROLLED_BACK = 5;
    private static final String[] STATUS_NAMES;
    static final int LOG_ID_BITS = 40;
    private static final int LOG_ID_BITS1 = 41;
    private static final long LOG_ID_LIMIT = 1099511627776L;
    private static final long LOG_ID_MASK = 2199023255551L;
    private static final int STATUS_BITS = 4;
    private static final int STATUS_MASK = 15;
    final TransactionStore store;
    final TransactionStore.RollbackListener listener;
    final int transactionId;
    final long sequenceNum;
    private final AtomicLong statusAndLogId;
    private MVStore.TxCounter txCounter;
    private String name;
    boolean wasStored;
    int timeoutMillis;
    private final int ownerId;
    private volatile Transaction blockingTransaction;
    private String blockingMapName;
    private Object blockingKey;
    private volatile boolean notificationRequested;
    private RootReference<Long, Record<?, ?>>[] undoLogRootReferences;
    private final Map<Integer, TransactionMap<?, ?>> transactionMaps = new HashMap();
    final IsolationLevel isolationLevel;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !Transaction.class.desiredAssertionStatus();
        STATUS_NAMES = new String[]{"CLOSED", "OPEN", "PREPARED", "COMMITTED", "ROLLING_BACK", "ROLLED_BACK"};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Transaction(TransactionStore transactionStore, int i, long j, int i2, String str, long j2, int i3, int i4, IsolationLevel isolationLevel, TransactionStore.RollbackListener rollbackListener) {
        this.store = transactionStore;
        this.transactionId = i;
        this.sequenceNum = j;
        this.statusAndLogId = new AtomicLong(composeState(i2, j2, false));
        this.name = str;
        setTimeoutMillis(i3);
        this.ownerId = i4;
        this.isolationLevel = isolationLevel;
        this.listener = rollbackListener;
    }

    public int getId() {
        return this.transactionId;
    }

    public long getSequenceNum() {
        return this.sequenceNum;
    }

    public int getStatus() {
        return getStatus(this.statusAndLogId.get());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<Long, Record<?, ?>>[] getUndoLogRootReferences() {
        return this.undoLogRootReferences;
    }

    private long setStatus(int i) {
        long j;
        long logId;
        boolean z;
        do {
            j = this.statusAndLogId.get();
            logId = getLogId(j);
            int status = getStatus(j);
            switch (i) {
                case 0:
                    z = status == 3 || status == 5;
                    break;
                case 1:
                default:
                    z = false;
                    break;
                case 2:
                    z = status == 1;
                    break;
                case 3:
                    z = status == 1 || status == 2 || status == 3;
                    break;
                case 4:
                    z = status == 1;
                    break;
                case 5:
                    z = status == 1 || status == 2 || status == 4;
                    break;
            }
            if (!z) {
                throw DataUtils.newMVStoreException(103, "Transaction was illegally transitioned from {0} to {1}", getStatusName(status), getStatusName(i));
            }
        } while (!this.statusAndLogId.compareAndSet(j, composeState(i, logId, hasRollback(j))));
        return j;
    }

    public boolean hasChanges() {
        return hasChanges(this.statusAndLogId.get());
    }

    public void setName(String str) {
        checkNotClosed();
        this.name = str;
        this.store.storeTransaction(this);
    }

    public String getName() {
        return this.name;
    }

    public int getBlockerId() {
        Transaction transaction = this.blockingTransaction;
        if (transaction == null) {
            return 0;
        }
        return transaction.ownerId;
    }

    public long setSavepoint() {
        return getLogId();
    }

    public boolean hasStatementDependencies() {
        return !this.transactionMaps.isEmpty();
    }

    public IsolationLevel getIsolationLevel() {
        return this.isolationLevel;
    }

    boolean isReadCommitted() {
        return this.isolationLevel == IsolationLevel.READ_COMMITTED;
    }

    public boolean allowNonRepeatableRead() {
        return this.isolationLevel.allowNonRepeatableRead();
    }

    public void markStatementStart(HashSet<MVMap<Object, VersionedValue<Object>>> hashSet) {
        BitSet bitSet;
        markStatementEnd();
        if (this.txCounter == null && this.store.store.isVersioningRequired()) {
            this.txCounter = this.store.store.registerVersionUsage();
        }
        if (hashSet == null || hashSet.isEmpty()) {
            return;
        }
        do {
            bitSet = this.store.committingTransactions.get();
            Iterator<MVMap<Object, VersionedValue<Object>>> it = hashSet.iterator();
            while (it.hasNext()) {
                MVMap<Object, VersionedValue<Object>> next = it.next();
                openMapX(next).setStatementSnapshot(new Snapshot(next.flushAndGetRoot(), bitSet));
            }
            if (isReadCommitted()) {
                this.undoLogRootReferences = this.store.collectUndoLogRootReferences();
            }
        } while (bitSet != this.store.committingTransactions.get());
        Iterator<MVMap<Object, VersionedValue<Object>>> it2 = hashSet.iterator();
        while (it2.hasNext()) {
            openMapX(it2.next()).promoteSnapshot();
        }
    }

    public void markStatementEnd() {
        if (allowNonRepeatableRead()) {
            releaseSnapshot();
        }
        Iterator<TransactionMap<?, ?>> it = this.transactionMaps.values().iterator();
        while (it.hasNext()) {
            it.next().setStatementSnapshot(null);
        }
    }

    private void markTransactionEnd() {
        if (!allowNonRepeatableRead()) {
            releaseSnapshot();
        }
    }

    private void releaseSnapshot() {
        this.transactionMaps.clear();
        this.undoLogRootReferences = null;
        MVStore.TxCounter txCounter = this.txCounter;
        if (txCounter != null) {
            this.txCounter = null;
            this.store.store.deregisterVersionUsage(txCounter);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long log(Record<?, ?> record) {
        long andIncrement = this.statusAndLogId.getAndIncrement();
        long logId = getLogId(andIncrement);
        if (logId >= LOG_ID_LIMIT) {
            throw DataUtils.newMVStoreException(104, "Transaction {0} has too many changes", Integer.valueOf(this.transactionId));
        }
        checkOpen(getStatus(andIncrement));
        return this.store.addUndoLogRecord(this.transactionId, logId, record);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void logUndo() {
        long decrementAndGet = this.statusAndLogId.decrementAndGet();
        if (getLogId(decrementAndGet) >= LOG_ID_LIMIT) {
            throw DataUtils.newMVStoreException(100, "Transaction {0} has internal error", Integer.valueOf(this.transactionId));
        }
        checkOpen(getStatus(decrementAndGet));
        this.store.removeUndoLogRecord(this.transactionId);
    }

    public <K, V> TransactionMap<K, V> openMap(String str) {
        return openMap(str, null, null);
    }

    public <K, V> TransactionMap<K, V> openMap(String str, DataType<K> dataType, DataType<V> dataType2) {
        return openMapX(this.store.openVersionedMap(str, dataType, dataType2));
    }

    public <K, V> TransactionMap<K, V> openMapX(MVMap<K, VersionedValue<V>> mVMap) {
        checkNotClosed();
        int id = mVMap.getId();
        TransactionMap<?, ?> transactionMap = this.transactionMaps.get(Integer.valueOf(id));
        if (transactionMap == null) {
            transactionMap = new TransactionMap<>(this, mVMap);
            this.transactionMaps.put(Integer.valueOf(id), transactionMap);
        }
        return (TransactionMap<K, V>) transactionMap;
    }

    public void prepare() {
        setStatus(2);
        this.store.storeTransaction(this);
    }

    public void commit() {
        if (!$assertionsDisabled && !this.store.openTransactions.get().get(this.transactionId)) {
            throw new AssertionError();
        }
        markTransactionEnd();
        Throwable th = null;
        boolean z = false;
        int i = 1;
        try {
            try {
                long status = setStatus(3);
                z = hasChanges(status);
                i = getStatus(status);
                if (z) {
                    this.store.commit(this, i == 3);
                }
                if (isActive(i)) {
                    try {
                        this.store.endTransaction(this, z);
                    } catch (Throwable th2) {
                        if (0 == 0) {
                            throw th2;
                        }
                        th.addSuppressed(th2);
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (isActive(i)) {
                try {
                    this.store.endTransaction(this, z);
                } catch (Throwable th5) {
                    if (th == null) {
                        throw th5;
                    }
                    th.addSuppressed(th5);
                }
            }
            throw th4;
        }
    }

    public void rollbackToSavepoint(long j) {
        boolean compareAndSet;
        long status = setStatus(4);
        long logId = getLogId(status);
        try {
            this.store.rollbackTo(this, logId, j);
            notifyAllWaitingTransactions();
            long composeState = composeState(4, logId, hasRollback(status));
            long composeState2 = composeState(1, j, true);
            do {
                compareAndSet = this.statusAndLogId.compareAndSet(composeState, composeState2);
                if (compareAndSet) {
                    break;
                }
            } while (this.statusAndLogId.get() == composeState);
            if (!compareAndSet) {
                throw DataUtils.newMVStoreException(103, "Transaction {0} concurrently modified while rollback to savepoint was in progress", Integer.valueOf(this.transactionId));
            }
        } catch (Throwable th) {
            notifyAllWaitingTransactions();
            long composeState3 = composeState(4, logId, hasRollback(status));
            long composeState4 = composeState(1, j, true);
            while (!this.statusAndLogId.compareAndSet(composeState3, composeState4) && this.statusAndLogId.get() == composeState3) {
            }
            throw th;
        }
    }

    public void rollback() {
        markTransactionEnd();
        Throwable th = null;
        int i = 1;
        try {
            try {
                long status = setStatus(5);
                i = getStatus(status);
                long logId = getLogId(status);
                if (logId > 0) {
                    this.store.rollbackTo(this, logId, 0L);
                }
                try {
                    if (isActive(i)) {
                        this.store.endTransaction(this, true);
                    }
                } catch (Throwable th2) {
                    if (0 == 0) {
                        throw th2;
                    }
                    th.addSuppressed(th2);
                }
            } catch (Throwable th3) {
                int status2 = getStatus();
                if (isActive(status2)) {
                    throw th3;
                }
                try {
                    if (isActive(status2)) {
                        this.store.endTransaction(this, true);
                    }
                } catch (Throwable th4) {
                    if (0 == 0) {
                        throw th4;
                    }
                    th.addSuppressed(th4);
                }
            }
        } catch (Throwable th5) {
            try {
                if (isActive(i)) {
                    this.store.endTransaction(this, true);
                }
            } catch (Throwable th6) {
                if (0 == 0) {
                    throw th6;
                }
                th.addSuppressed(th6);
            }
            throw th5;
        }
    }

    private static boolean isActive(int i) {
        return (i == 0 || i == 3 || i == 5) ? false : true;
    }

    public Iterator<TransactionStore.Change> getChanges(long j) {
        return this.store.getChanges(this, getLogId(), j);
    }

    public void setTimeoutMillis(int i) {
        this.timeoutMillis = i > 0 ? i : this.store.timeoutMillis;
    }

    private long getLogId() {
        return getLogId(this.statusAndLogId.get());
    }

    private void checkOpen(int i) {
        if (i != 1) {
            throw DataUtils.newMVStoreException(103, "Transaction {0} has status {1}, not OPEN", Integer.valueOf(this.transactionId), getStatusName(i));
        }
    }

    private void checkNotClosed() {
        if (getStatus() == 0) {
            throw DataUtils.newMVStoreException(4, "Transaction {0} is closed", Integer.valueOf(this.transactionId));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeIt() {
        this.transactionMaps.clear();
        long status = setStatus(0);
        this.store.store.deregisterVersionUsage(this.txCounter);
        if (hasChanges(status) || hasRollback(status)) {
            notifyAllWaitingTransactions();
        }
    }

    private void notifyAllWaitingTransactions() {
        if (this.notificationRequested) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public boolean waitFor(Transaction transaction, String str, Object obj, int i) {
        this.blockingTransaction = transaction;
        this.blockingMapName = str;
        this.blockingKey = obj;
        if (isDeadlocked(transaction)) {
            tryThrowDeadLockException(false);
        }
        boolean waitForThisToEnd = transaction.waitForThisToEnd(i == -1 ? this.timeoutMillis : i, this);
        this.blockingMapName = null;
        this.blockingKey = null;
        this.blockingTransaction = null;
        return waitForThisToEnd;
    }

    private boolean isDeadlocked(Transaction transaction) {
        Transaction transaction2 = transaction;
        int maxTransactionId = this.store.getMaxTransactionId();
        Transaction transaction3 = transaction;
        while (true) {
            Transaction transaction4 = transaction3.blockingTransaction;
            if (transaction4 != null && transaction3.getStatus() == 1 && maxTransactionId > 0) {
                if (transaction4.sequenceNum > transaction2.sequenceNum) {
                    transaction2 = transaction4;
                }
                if (transaction4 == this) {
                    if (transaction2 == this) {
                        return true;
                    }
                    Transaction transaction5 = transaction2.blockingTransaction;
                    if (transaction5 != null) {
                        transaction2.setStatus(4);
                        transaction5.notifyAllWaitingTransactions();
                        return false;
                    }
                }
                transaction3 = transaction4;
                maxTransactionId--;
            } else {
                return false;
            }
        }
    }

    private void tryThrowDeadLockException(boolean z) {
        Transaction transaction;
        BitSet bitSet = new BitSet();
        StringBuilder sb = new StringBuilder(String.format("Transaction %d has been chosen as a deadlock victim. Details:%n", Integer.valueOf(this.transactionId)));
        Transaction transaction2 = this;
        while (true) {
            Transaction transaction3 = transaction2;
            if (bitSet.get(transaction3.transactionId) || (transaction = transaction3.blockingTransaction) == null) {
                break;
            }
            bitSet.set(transaction3.transactionId);
            sb.append(String.format("Transaction %d attempts to update map <%s> entry with key <%s> modified by transaction %s%n", Integer.valueOf(transaction3.transactionId), transaction3.blockingMapName, transaction3.blockingKey, transaction3.blockingTransaction));
            if (transaction == this) {
                z = true;
            }
            transaction2 = transaction;
        }
        if (z) {
            throw DataUtils.newMVStoreException(105, "{0}", sb.toString());
        }
    }

    private synchronized boolean waitForThisToEnd(int i, Transaction transaction) {
        long nanoTime = System.nanoTime();
        this.notificationRequested = true;
        while (true) {
            long j = this.statusAndLogId.get();
            int status = getStatus(j);
            if (status != 0 && status != 5 && !hasRollback(j)) {
                if (transaction.getStatus() != 1) {
                    transaction.tryThrowDeadLockException(true);
                }
                int nanoTime2 = i - ((int) ((System.nanoTime() - nanoTime) / 1000000));
                if (nanoTime2 <= 0) {
                    return false;
                }
                try {
                    wait(nanoTime2);
                } catch (InterruptedException e) {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public <K, V> void removeMap(TransactionMap<K, V> transactionMap) {
        this.store.removeMap(transactionMap);
    }

    public String toString() {
        return this.transactionId + "(" + this.sequenceNum + ") " + stateToString();
    }

    private String stateToString() {
        return stateToString(this.statusAndLogId.get());
    }

    private static String stateToString(long j) {
        return getStatusName(getStatus(j)) + (hasRollback(j) ? "<" : "") + CharSequenceUtil.SPACE + getLogId(j);
    }

    private static int getStatus(long j) {
        return ((int) (j >>> 41)) & 15;
    }

    private static long getLogId(long j) {
        return j & LOG_ID_MASK;
    }

    private static boolean hasRollback(long j) {
        return (j & 35184372088832L) != 0;
    }

    private static boolean hasChanges(long j) {
        return getLogId(j) != 0;
    }

    private static long composeState(int i, long j, boolean z) {
        if (!$assertionsDisabled && j >= LOG_ID_LIMIT) {
            throw new AssertionError(j);
        }
        if (!$assertionsDisabled && (i & (-16)) != 0) {
            throw new AssertionError(i);
        }
        if (z) {
            i |= 16;
        }
        return (i << 41) | j;
    }

    private static String getStatusName(int i) {
        return (i < 0 || i >= STATUS_NAMES.length) ? "UNKNOWN_STATUS_" + i : STATUS_NAMES[i];
    }
}

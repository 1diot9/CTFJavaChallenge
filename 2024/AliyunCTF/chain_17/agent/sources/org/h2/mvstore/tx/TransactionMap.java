package org.h2.mvstore.tx;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import org.h2.engine.IsolationLevel;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.tx.TxDecisionMaker;
import org.h2.mvstore.type.DataType;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionMap.class */
public final class TransactionMap<K, V> extends AbstractMap<K, V> {
    public final MVMap<K, VersionedValue<V>> map;
    private final Transaction transaction;
    private Snapshot<K, VersionedValue<V>> snapshot;
    private Snapshot<K, VersionedValue<V>> statementSnapshot;
    private boolean hasChanges;
    private final TxDecisionMaker<K, V> txDecisionMaker;
    private final TxDecisionMaker<K, V> ifAbsentDecisionMaker;
    private final TxDecisionMaker<K, V> lockDecisionMaker;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !TransactionMap.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransactionMap(Transaction transaction, MVMap<K, VersionedValue<V>> mVMap) {
        TxDecisionMaker<K, V> repeatableReadLockDecisionMaker;
        this.transaction = transaction;
        this.map = mVMap;
        this.txDecisionMaker = new TxDecisionMaker<>(mVMap.getId(), transaction);
        this.ifAbsentDecisionMaker = new TxDecisionMaker.PutIfAbsentDecisionMaker(mVMap.getId(), transaction, this::getFromSnapshot);
        if (transaction.allowNonRepeatableRead()) {
            repeatableReadLockDecisionMaker = new TxDecisionMaker.LockDecisionMaker<>(mVMap.getId(), transaction);
        } else {
            repeatableReadLockDecisionMaker = new TxDecisionMaker.RepeatableReadLockDecisionMaker<>(mVMap.getId(), transaction, mVMap.getValueType(), this::getFromSnapshot);
        }
        this.lockDecisionMaker = repeatableReadLockDecisionMaker;
    }

    public TransactionMap<K, V> getInstance(Transaction transaction) {
        return transaction.openMapX(this.map);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        long sizeAsLong = sizeAsLong();
        if (sizeAsLong > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) sizeAsLong;
    }

    public long sizeAsLongMax() {
        return this.map.sizeAsLong();
    }

    public long sizeAsLong() {
        Snapshot<K, VersionedValue<V>> snapshot;
        RootReference<Long, Record<?, ?>>[] undoLogRootReferences;
        IsolationLevel isolationLevel = this.transaction.getIsolationLevel();
        if (!isolationLevel.allowNonRepeatableRead() && this.hasChanges) {
            return sizeAsLongRepeatableReadWithChanges();
        }
        do {
            snapshot = getSnapshot();
            undoLogRootReferences = getTransaction().getUndoLogRootReferences();
        } while (!snapshot.equals(getSnapshot()));
        RootReference<K, VersionedValue<V>> rootReference = snapshot.root;
        long totalCount = rootReference.getTotalCount();
        long calculateUndoLogsTotalSize = undoLogRootReferences == null ? totalCount : TransactionStore.calculateUndoLogsTotalSize(undoLogRootReferences);
        if (calculateUndoLogsTotalSize == 0) {
            return totalCount;
        }
        return adjustSize(undoLogRootReferences, rootReference, isolationLevel == IsolationLevel.READ_UNCOMMITTED ? null : snapshot.committingTransactions, totalCount, calculateUndoLogsTotalSize);
    }

    private long adjustSize(RootReference<Long, Record<?, ?>>[] rootReferenceArr, RootReference<K, VersionedValue<V>> rootReference, BitSet bitSet, long j, long j2) {
        VersionedValue<V> versionedValue;
        if (2 * j2 > j) {
            Cursor<K, VersionedValue<V>> cursor = this.map.cursor(rootReference, null, null, false);
            while (cursor.hasNext()) {
                cursor.next();
                VersionedValue<V> value = cursor.getValue();
                if (!$assertionsDisabled && value == null) {
                    throw new AssertionError();
                }
                long operationId = value.getOperationId();
                if (operationId != 0 && isIrrelevant(operationId, value, bitSet)) {
                    j--;
                }
            }
        } else {
            if (!$assertionsDisabled && rootReferenceArr == null) {
                throw new AssertionError();
            }
            for (RootReference<Long, Record<?, ?>> rootReference2 : rootReferenceArr) {
                if (rootReference2 != null) {
                    Cursor<Long, Record<?, ?>> cursor2 = rootReference2.root.map.cursor(rootReference2, null, null, false);
                    while (cursor2.hasNext()) {
                        cursor2.next();
                        Record<?, ?> value2 = cursor2.getValue();
                        if (value2.mapId == this.map.getId() && (versionedValue = this.map.get(rootReference.root, value2.key)) != null) {
                            long longValue = cursor2.getKey().longValue();
                            if (!$assertionsDisabled && longValue == 0) {
                                throw new AssertionError();
                            }
                            if (versionedValue.getOperationId() == longValue && isIrrelevant(longValue, versionedValue, bitSet)) {
                                j--;
                            }
                        }
                    }
                }
            }
        }
        return j;
    }

    private boolean isIrrelevant(long j, VersionedValue<?> versionedValue, BitSet bitSet) {
        Object currentValue;
        if (bitSet == null) {
            currentValue = versionedValue.getCurrentValue();
        } else {
            int transactionId = TransactionStore.getTransactionId(j);
            currentValue = (transactionId == this.transaction.transactionId || bitSet.get(transactionId)) ? versionedValue.getCurrentValue() : versionedValue.getCommittedValue();
        }
        return currentValue == null;
    }

    private long sizeAsLongRepeatableReadWithChanges() {
        long j = 0;
        while (new RepeatableIterator(this, null, null, false, false).fetchNext() != null) {
            j++;
        }
        return j;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public V remove(Object obj) {
        return set(obj, null);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        DataUtils.checkArgument(v != null, "The value may not be null", new Object[0]);
        return set(k, v);
    }

    @Override // java.util.Map
    public V putIfAbsent(K k, V v) {
        DataUtils.checkArgument(v != null, "The value may not be null", new Object[0]);
        this.ifAbsentDecisionMaker.initialize(k, v);
        V v2 = set(k, this.ifAbsentDecisionMaker, -1);
        if (this.ifAbsentDecisionMaker.getDecision() == MVMap.Decision.ABORT) {
            v2 = this.ifAbsentDecisionMaker.getLastValue();
        }
        return v2;
    }

    public void append(K k, V v) {
        this.map.append(k, VersionedValueUncommitted.getInstance(this.transaction.log(new Record<>(this.map.getId(), k, null)), v, null));
        this.hasChanges = true;
    }

    public V lock(K k) {
        return lock(k, -1);
    }

    public V lock(K k, int i) {
        this.lockDecisionMaker.initialize(k, null);
        return set(k, this.lockDecisionMaker, i);
    }

    public V putCommitted(K k, V v) {
        DataUtils.checkArgument(v != null, "The value may not be null", new Object[0]);
        VersionedValue<V> put = this.map.put(k, VersionedValueCommitted.getInstance(v));
        return put == null ? null : put.getCurrentValue();
    }

    private V set(K k, V v) {
        this.txDecisionMaker.initialize(k, v);
        return set(k, this.txDecisionMaker, -1);
    }

    private V set(Object obj, TxDecisionMaker<K, V> txDecisionMaker, int i) {
        VersionedValue<V> operate;
        Transaction blockingTransaction;
        String str = null;
        do {
            if (!$assertionsDisabled && this.transaction.getBlockerId() != 0) {
                throw new AssertionError();
            }
            operate = this.map.operate(obj, null, txDecisionMaker);
            MVMap.Decision decision = txDecisionMaker.getDecision();
            if (!$assertionsDisabled && decision == null) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && decision == MVMap.Decision.REPEAT) {
                throw new AssertionError();
            }
            blockingTransaction = txDecisionMaker.getBlockingTransaction();
            if (decision != MVMap.Decision.ABORT || blockingTransaction == null) {
                this.hasChanges |= decision != MVMap.Decision.ABORT;
                return operate == null ? null : operate.getCurrentValue();
            }
            txDecisionMaker.reset();
            if (i == -2) {
                return null;
            }
            if (str == null) {
                str = this.map.getName();
            }
            if (i == 0) {
                break;
            }
        } while (this.transaction.waitFor(blockingTransaction, str, obj, i));
        Object[] objArr = new Object[6];
        objArr[0] = str;
        objArr[1] = obj;
        objArr[2] = operate;
        objArr[3] = Integer.valueOf(blockingTransaction.transactionId);
        objArr[4] = Integer.valueOf(this.transaction.transactionId);
        objArr[5] = Integer.valueOf(i == -1 ? this.transaction.timeoutMillis : i);
        throw DataUtils.newMVStoreException(101, "Map entry <{0}> with key <{1}> and value {2} is locked by tx {3} and can not be updated by tx {4} within allocated time interval {5} ms.", objArr);
    }

    public boolean tryRemove(K k) {
        return trySet(k, null);
    }

    public boolean tryPut(K k, V v) {
        DataUtils.checkArgument(v != null, "The value may not be null", new Object[0]);
        return trySet(k, v);
    }

    public boolean trySet(K k, V v) {
        try {
            set(k, v);
            return true;
        } catch (MVStoreException e) {
            return false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public V get(Object obj) {
        return getImmediate(obj);
    }

    public V getFromSnapshot(K k) {
        switch (this.transaction.isolationLevel) {
            case READ_UNCOMMITTED:
                VersionedValue versionedValue = (VersionedValue) this.map.get(getStatementSnapshot().root.root, k);
                if (versionedValue != null) {
                    return (V) versionedValue.getCurrentValue();
                }
                return null;
            case REPEATABLE_READ:
            case SNAPSHOT:
            case SERIALIZABLE:
                if (this.transaction.hasChanges()) {
                    VersionedValue versionedValue2 = (VersionedValue) this.map.get(getStatementSnapshot().root.root, k);
                    if (versionedValue2 != null) {
                        long operationId = versionedValue2.getOperationId();
                        if (operationId != 0 && this.transaction.transactionId == TransactionStore.getTransactionId(operationId)) {
                            return (V) versionedValue2.getCurrentValue();
                        }
                    }
                }
                break;
        }
        Snapshot<K, VersionedValue<V>> snapshot = getSnapshot();
        return getFromSnapshot(snapshot.root, snapshot.committingTransactions, k);
    }

    private V getFromSnapshot(RootReference<K, VersionedValue<V>> rootReference, BitSet bitSet, K k) {
        int transactionId;
        VersionedValue<V> versionedValue = this.map.get(rootReference.root, k);
        if (versionedValue == null) {
            return null;
        }
        long operationId = versionedValue.getOperationId();
        if (operationId != 0 && (transactionId = TransactionStore.getTransactionId(operationId)) != this.transaction.transactionId && !bitSet.get(transactionId)) {
            return versionedValue.getCommittedValue();
        }
        return versionedValue.getCurrentValue();
    }

    public V getImmediate(K k) {
        return (V) useSnapshot((rootReference, bitSet) -> {
            return getFromSnapshot(rootReference, bitSet, k);
        });
    }

    Snapshot<K, VersionedValue<V>> getSnapshot() {
        return this.snapshot == null ? createSnapshot() : this.snapshot;
    }

    Snapshot<K, VersionedValue<V>> getStatementSnapshot() {
        return this.statementSnapshot == null ? createSnapshot() : this.statementSnapshot;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStatementSnapshot(Snapshot<K, VersionedValue<V>> snapshot) {
        this.statementSnapshot = snapshot;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void promoteSnapshot() {
        if (this.snapshot == null) {
            this.snapshot = this.statementSnapshot;
        }
    }

    Snapshot<K, VersionedValue<V>> createSnapshot() {
        return (Snapshot) useSnapshot(Snapshot::new);
    }

    <R> R useSnapshot(BiFunction<RootReference<K, VersionedValue<V>>, BitSet, R> biFunction) {
        BitSet bitSet;
        RootReference<K, VersionedValue<V>> root;
        AtomicReference<BitSet> atomicReference = this.transaction.store.committingTransactions;
        BitSet bitSet2 = atomicReference.get();
        do {
            bitSet = bitSet2;
            root = this.map.getRoot();
            bitSet2 = atomicReference.get();
        } while (bitSet2 != bitSet);
        return biFunction.apply(root, bitSet2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return getImmediate(obj) != null;
    }

    public boolean isDeletedByCurrentTransaction(K k) {
        VersionedValue<V> versionedValue = this.map.get(k);
        if (versionedValue != null) {
            long operationId = versionedValue.getOperationId();
            return operationId != 0 && TransactionStore.getTransactionId(operationId) == this.transaction.transactionId && versionedValue.getCurrentValue() == null;
        }
        return false;
    }

    public boolean isSameTransaction(K k) {
        VersionedValue<V> versionedValue = this.map.get(k);
        return versionedValue != null && TransactionStore.getTransactionId(versionedValue.getOperationId()) == this.transaction.transactionId;
    }

    public boolean isClosed() {
        return this.map.isClosed();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        this.map.clear();
        this.hasChanges = true;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>() { // from class: org.h2.mvstore.tx.TransactionMap.1
            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<K, V>> iterator() {
                return TransactionMap.this.entryIterator(null, null);
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                return TransactionMap.this.size();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public boolean contains(Object obj) {
                return TransactionMap.this.containsKey(obj);
            }
        };
    }

    public Map.Entry<K, V> firstEntry() {
        return (Map.Entry) chooseIterator(null, null, false, true).fetchNext();
    }

    public K firstKey() {
        return (K) chooseIterator(null, null, false, false).fetchNext();
    }

    public Map.Entry<K, V> lastEntry() {
        return (Map.Entry) chooseIterator(null, null, true, true).fetchNext();
    }

    public K lastKey() {
        return (K) chooseIterator(null, null, true, false).fetchNext();
    }

    public Map.Entry<K, V> higherEntry(K k) {
        return higherLowerEntry(k, false);
    }

    public K higherKey(K k) {
        return higherLowerKey(k, false);
    }

    public Map.Entry<K, V> ceilingEntry(K k) {
        return (Map.Entry) chooseIterator(k, null, false, true).fetchNext();
    }

    public K ceilingKey(K k) {
        return (K) chooseIterator(k, null, false, false).fetchNext();
    }

    public Map.Entry<K, V> floorEntry(K k) {
        return (Map.Entry) chooseIterator(k, null, true, true).fetchNext();
    }

    public K floorKey(K k) {
        return (K) chooseIterator(k, null, true, false).fetchNext();
    }

    public Map.Entry<K, V> lowerEntry(K k) {
        return higherLowerEntry(k, true);
    }

    public K lowerKey(K k) {
        return higherLowerKey(k, true);
    }

    private Map.Entry<K, V> higherLowerEntry(K k, boolean z) {
        TMIterator<K, V, X> chooseIterator = chooseIterator(k, null, z, true);
        Map.Entry<K, V> entry = (Map.Entry) chooseIterator.fetchNext();
        if (entry != null && this.map.getKeyType().compare(k, entry.getKey()) == 0) {
            entry = (Map.Entry) chooseIterator.fetchNext();
        }
        return entry;
    }

    private K higherLowerKey(K k, boolean z) {
        TMIterator<K, V, X> chooseIterator = chooseIterator(k, null, z, false);
        Object fetchNext = chooseIterator.fetchNext();
        if (fetchNext != null && this.map.getKeyType().compare(k, fetchNext) == 0) {
            fetchNext = chooseIterator.fetchNext();
        }
        return (K) fetchNext;
    }

    public Iterator<K> keyIterator(K k) {
        return chooseIterator(k, null, false, false);
    }

    public TMIterator<K, V, K> keyIterator(K k, boolean z) {
        return (TMIterator<K, V, K>) chooseIterator(k, null, z, false);
    }

    public TMIterator<K, V, K> keyIterator(K k, K k2) {
        return (TMIterator<K, V, K>) chooseIterator(k, k2, false, false);
    }

    public TMIterator<K, V, K> keyIteratorUncommitted(K k, K k2) {
        return new ValidationIterator(this, k, k2);
    }

    public TMIterator<K, V, Map.Entry<K, V>> entryIterator(K k, K k2) {
        return (TMIterator<K, V, Map.Entry<K, V>>) chooseIterator(k, k2, false, true);
    }

    private <X> TMIterator<K, V, X> chooseIterator(K k, K k2, boolean z, boolean z2) {
        switch (this.transaction.isolationLevel) {
            case READ_UNCOMMITTED:
                return new UncommittedIterator(this, k, k2, z, z2);
            case REPEATABLE_READ:
            case SNAPSHOT:
            case SERIALIZABLE:
                if (this.hasChanges) {
                    return new RepeatableIterator(this, k, k2, z, z2);
                }
                break;
        }
        return new CommittedIterator(this, k, k2, z, z2);
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public DataType<K> getKeyType() {
        return this.map.getKeyType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionMap$UncommittedIterator.class */
    public static class UncommittedIterator<K, V, X> extends TMIterator<K, V, X> {
        UncommittedIterator(TransactionMap<K, V> transactionMap, K k, K k2, boolean z, boolean z2) {
            super(transactionMap, k, k2, transactionMap.createSnapshot(), z, z2);
        }

        UncommittedIterator(TransactionMap<K, V> transactionMap, K k, K k2, Snapshot<K, VersionedValue<V>> snapshot, boolean z, boolean z2) {
            super(transactionMap, k, k2, snapshot, z, z2);
        }

        @Override // org.h2.mvstore.tx.TransactionMap.TMIterator
        public final X fetchNext() {
            V currentValue;
            while (this.cursor.hasNext()) {
                K next = this.cursor.next();
                VersionedValue<V> value = this.cursor.getValue();
                if (value != null && ((currentValue = value.getCurrentValue()) != null || shouldIgnoreRemoval(value))) {
                    return toElement(next, currentValue);
                }
            }
            return null;
        }

        boolean shouldIgnoreRemoval(VersionedValue<?> versionedValue) {
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionMap$ValidationIterator.class */
    private static final class ValidationIterator<K, V, X> extends UncommittedIterator<K, V, X> {
        static final /* synthetic */ boolean $assertionsDisabled;

        static {
            $assertionsDisabled = !TransactionMap.class.desiredAssertionStatus();
        }

        ValidationIterator(TransactionMap<K, V> transactionMap, K k, K k2) {
            super(transactionMap, k, k2, transactionMap.createSnapshot(), false, false);
        }

        @Override // org.h2.mvstore.tx.TransactionMap.UncommittedIterator
        boolean shouldIgnoreRemoval(VersionedValue<?> versionedValue) {
            int transactionId;
            if (!$assertionsDisabled && versionedValue.getCurrentValue() != null) {
                throw new AssertionError();
            }
            long operationId = versionedValue.getOperationId();
            return (operationId == 0 || this.transactionId == (transactionId = TransactionStore.getTransactionId(operationId)) || this.committingTransactions.get(transactionId)) ? false : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionMap$CommittedIterator.class */
    public static final class CommittedIterator<K, V, X> extends TMIterator<K, V, X> {
        CommittedIterator(TransactionMap<K, V> transactionMap, K k, K k2, boolean z, boolean z2) {
            super(transactionMap, k, k2, transactionMap.getSnapshot(), z, z2);
        }

        @Override // org.h2.mvstore.tx.TransactionMap.TMIterator
        public X fetchNext() {
            int transactionId;
            while (this.cursor.hasNext()) {
                K next = this.cursor.next();
                VersionedValue<V> value = this.cursor.getValue();
                if (value != null) {
                    long operationId = value.getOperationId();
                    if (operationId != 0 && (transactionId = TransactionStore.getTransactionId(operationId)) != this.transactionId && !this.committingTransactions.get(transactionId)) {
                        V committedValue = value.getCommittedValue();
                        if (committedValue != null) {
                            return toElement(next, committedValue);
                        }
                    } else {
                        V currentValue = value.getCurrentValue();
                        if (currentValue != null) {
                            return toElement(next, currentValue);
                        }
                    }
                }
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionMap$RepeatableIterator.class */
    public static final class RepeatableIterator<K, V, X> extends TMIterator<K, V, X> {
        private final DataType<K> keyType;
        private K snapshotKey;
        private Object snapshotValue;
        private final Cursor<K, VersionedValue<V>> uncommittedCursor;
        private K uncommittedKey;
        private V uncommittedValue;

        RepeatableIterator(TransactionMap<K, V> transactionMap, K k, K k2, boolean z, boolean z2) {
            super(transactionMap, k, k2, transactionMap.getSnapshot(), z, z2);
            this.keyType = transactionMap.map.getKeyType();
            this.uncommittedCursor = transactionMap.map.cursor(transactionMap.getStatementSnapshot().root, k, k2, z);
        }

        @Override // org.h2.mvstore.tx.TransactionMap.TMIterator
        public X fetchNext() {
            int compare;
            X x = null;
            while (true) {
                if (this.snapshotKey == null) {
                    fetchSnapshot();
                }
                if (this.uncommittedKey == null) {
                    fetchUncommitted();
                }
                if (this.snapshotKey == null && this.uncommittedKey == null) {
                    break;
                }
                if (this.snapshotKey == null) {
                    compare = 1;
                } else {
                    compare = this.uncommittedKey == null ? -1 : this.keyType.compare(this.snapshotKey, this.uncommittedKey);
                }
                int i = compare;
                if (i < 0) {
                    x = toElement(this.snapshotKey, this.snapshotValue);
                    this.snapshotKey = null;
                    break;
                }
                if (this.uncommittedValue != null) {
                    x = toElement(this.uncommittedKey, this.uncommittedValue);
                }
                if (i == 0) {
                    this.snapshotKey = null;
                }
                this.uncommittedKey = null;
                if (x != null) {
                    break;
                }
            }
            return x;
        }

        private void fetchSnapshot() {
            int transactionId;
            while (this.cursor.hasNext()) {
                K next = this.cursor.next();
                VersionedValue<V> value = this.cursor.getValue();
                if (value != null) {
                    V committedValue = value.getCommittedValue();
                    long operationId = value.getOperationId();
                    if (operationId != 0 && ((transactionId = TransactionStore.getTransactionId(operationId)) == this.transactionId || this.committingTransactions.get(transactionId))) {
                        committedValue = value.getCurrentValue();
                    }
                    if (committedValue != null) {
                        this.snapshotKey = next;
                        this.snapshotValue = committedValue;
                        return;
                    }
                }
            }
        }

        private void fetchUncommitted() {
            while (this.uncommittedCursor.hasNext()) {
                K next = this.uncommittedCursor.next();
                VersionedValue<V> value = this.uncommittedCursor.getValue();
                if (value != null) {
                    long operationId = value.getOperationId();
                    if (operationId != 0 && this.transactionId == TransactionStore.getTransactionId(operationId)) {
                        this.uncommittedKey = next;
                        this.uncommittedValue = value.getCurrentValue();
                        return;
                    }
                }
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionMap$TMIterator.class */
    public static abstract class TMIterator<K, V, X> implements Iterator<X> {
        final int transactionId;
        final BitSet committingTransactions;
        protected final Cursor<K, VersionedValue<V>> cursor;
        private final boolean forEntries;
        X current;

        public abstract X fetchNext();

        TMIterator(TransactionMap<K, V> transactionMap, K k, K k2, Snapshot<K, VersionedValue<V>> snapshot, boolean z, boolean z2) {
            this.transactionId = transactionMap.getTransaction().transactionId;
            this.forEntries = z2;
            this.cursor = transactionMap.map.cursor(snapshot.root, k, k2, z);
            this.committingTransactions = snapshot.committingTransactions;
        }

        /* JADX WARN: Multi-variable type inference failed */
        final X toElement(K k, Object obj) {
            return this.forEntries ? (X) new AbstractMap.SimpleImmutableEntry(k, obj) : k;
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.current == null) {
                X fetchNext = fetchNext();
                this.current = fetchNext;
                if (fetchNext == null) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Iterator
        public final X next() {
            X x = this.current;
            if (x == null) {
                X fetchNext = fetchNext();
                x = fetchNext;
                if (fetchNext == null) {
                    throw new NoSuchElementException();
                }
            } else {
                this.current = null;
            }
            return x;
        }
    }
}

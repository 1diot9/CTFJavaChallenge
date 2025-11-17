package org.h2.mvstore.tx;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.h2.engine.IsolationLevel;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.SpatialDataType;
import org.h2.mvstore.tx.Record;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.ObjectDataType;
import org.h2.mvstore.type.StringDataType;
import org.h2.util.StringUtils;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionStore.class */
public class TransactionStore {
    final MVStore store;
    final int timeoutMillis;
    private final MVMap<Integer, Object[]> preparedTransactions;
    private final MVMap<String, DataType<?>> typeRegistry;
    final MVMap<Long, Record<?, ?>>[] undoLogs;
    private final MVMap.Builder<Long, Record<?, ?>> undoLogBuilder;
    private final DataType<?> dataType;
    final AtomicReference<VersionedBitSet> openTransactions;
    final AtomicReference<BitSet> committingTransactions;
    private boolean init;
    private int maxTransactionId;
    private final AtomicReferenceArray<Transaction> transactions;
    private static final String TYPE_REGISTRY_NAME = "_";
    public static final String UNDO_LOG_NAME_PREFIX = "undoLog";
    private static final char UNDO_LOG_COMMITTED = '-';
    private static final char UNDO_LOG_OPEN = '.';
    private static final int MAX_OPEN_TRANSACTIONS = 65535;
    private static final int LOG_ID_BITS = 40;
    private static final long LOG_ID_MASK = 1099511627775L;
    private static final RollbackListener ROLLBACK_LISTENER_NONE;
    static final /* synthetic */ boolean $assertionsDisabled;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionStore$RollbackListener.class */
    public interface RollbackListener {
        void onRollback(MVMap<Object, VersionedValue<Object>> mVMap, Object obj, VersionedValue<Object> versionedValue, VersionedValue<Object> versionedValue2);
    }

    static {
        $assertionsDisabled = !TransactionStore.class.desiredAssertionStatus();
        ROLLBACK_LISTENER_NONE = (mVMap, obj, versionedValue, versionedValue2) -> {
        };
    }

    private static String getUndoLogName(int i) {
        return i > 0 ? "undoLog." + i : "undoLog.";
    }

    public TransactionStore(MVStore mVStore) {
        this(mVStore, new ObjectDataType());
    }

    public TransactionStore(MVStore mVStore, DataType<?> dataType) {
        this(mVStore, new MetaType(null, mVStore.backgroundExceptionHandler), dataType, 0);
    }

    public TransactionStore(MVStore mVStore, MetaType<?> metaType, DataType<?> dataType, int i) {
        this.undoLogs = new MVMap[65535];
        this.openTransactions = new AtomicReference<>(new VersionedBitSet());
        this.committingTransactions = new AtomicReference<>(new BitSet());
        this.maxTransactionId = 65535;
        this.transactions = new AtomicReferenceArray<>(65536);
        this.store = mVStore;
        this.dataType = dataType;
        this.timeoutMillis = i;
        this.typeRegistry = openTypeRegistry(mVStore, metaType);
        this.preparedTransactions = mVStore.openMap("openTransactions", new MVMap.Builder());
        this.undoLogBuilder = createUndoLogBuilder();
    }

    MVMap.Builder<Long, Record<?, ?>> createUndoLogBuilder() {
        return new MVMap.Builder().singleWriter().keyType((DataType) LongDataType.INSTANCE).valueType((DataType) new Record.Type(this));
    }

    private static MVMap<String, DataType<?>> openTypeRegistry(MVStore mVStore, MetaType<?> metaType) {
        return mVStore.openMap("_", new MVMap.Builder().keyType((DataType) StringDataType.INSTANCE).valueType((DataType) metaType));
    }

    public void init() {
        init(ROLLBACK_LISTENER_NONE);
    }

    public void init(RollbackListener rollbackListener) {
        int intValue;
        String str;
        if (!this.init) {
            for (String str2 : this.store.getMapNames()) {
                if (str2.startsWith(UNDO_LOG_NAME_PREFIX)) {
                    if (str2.length() > UNDO_LOG_NAME_PREFIX.length()) {
                        boolean z = str2.charAt(UNDO_LOG_NAME_PREFIX.length()) == '-';
                        if (this.store.hasData(str2)) {
                            int parseUInt31 = StringUtils.parseUInt31(str2, UNDO_LOG_NAME_PREFIX.length() + 1, str2.length());
                            if (!this.openTransactions.get().get(parseUInt31)) {
                                Object[] objArr = this.preparedTransactions.get(Integer.valueOf(parseUInt31));
                                if (objArr == null) {
                                    intValue = 1;
                                    str = null;
                                } else {
                                    intValue = ((Integer) objArr[0]).intValue();
                                    str = (String) objArr[1];
                                }
                                MVMap<Long, Record<?, ?>> openMap = this.store.openMap(str2, this.undoLogBuilder);
                                this.undoLogs[parseUInt31] = openMap;
                                Long lastKey = openMap.lastKey();
                                if (!$assertionsDisabled && lastKey == null) {
                                    throw new AssertionError();
                                }
                                if (!$assertionsDisabled && getTransactionId(lastKey.longValue()) != parseUInt31) {
                                    throw new AssertionError();
                                }
                                long logId = getLogId(lastKey.longValue()) + 1;
                                if (z) {
                                    this.store.renameMap(openMap, getUndoLogName(parseUInt31));
                                    markUndoLogAsCommitted(parseUInt31);
                                } else {
                                    z = logId > LOG_ID_MASK;
                                }
                                if (z) {
                                    intValue = 3;
                                    Long lowerKey = openMap.lowerKey(lastKey);
                                    if (!$assertionsDisabled && lowerKey != null && getTransactionId(lowerKey.longValue()) != parseUInt31) {
                                        throw new AssertionError();
                                    }
                                    logId = lowerKey == null ? 0L : getLogId(lowerKey.longValue()) + 1;
                                }
                                registerTransaction(parseUInt31, intValue, str, logId, this.timeoutMillis, 0, IsolationLevel.READ_COMMITTED, rollbackListener);
                            }
                        }
                    }
                    if (!this.store.isReadOnly()) {
                        this.store.removeMap(str2);
                    }
                }
            }
            this.init = true;
        }
    }

    private void markUndoLogAsCommitted(int i) {
        addUndoLogRecord(i, LOG_ID_MASK, Record.COMMIT_MARKER);
    }

    public void endLeftoverTransactions() {
        for (Transaction transaction : getOpenTransactions()) {
            int status = transaction.getStatus();
            if (status == 3) {
                transaction.commit();
            } else if (status != 2) {
                transaction.rollback();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getMaxTransactionId() {
        return this.maxTransactionId;
    }

    public void setMaxTransactionId(int i) {
        DataUtils.checkArgument(i <= 65535, "Concurrent transactions limit is too high: {0}", Integer.valueOf(i));
        this.maxTransactionId = i;
    }

    public boolean hasMap(String str) {
        return this.store.hasMap(str);
    }

    static long getOperationId(int i, long j) {
        DataUtils.checkArgument(i >= 0 && i < 16777216, "Transaction id out of range: {0}", Integer.valueOf(i));
        DataUtils.checkArgument(j >= 0 && j <= LOG_ID_MASK, "Transaction log id out of range: {0}", Long.valueOf(j));
        return (i << 40) | j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getTransactionId(long j) {
        return (int) (j >>> 40);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getLogId(long j) {
        return j & LOG_ID_MASK;
    }

    public List<Transaction> getOpenTransactions() {
        if (!this.init) {
            init();
        }
        ArrayList arrayList = new ArrayList();
        int i = 0;
        VersionedBitSet versionedBitSet = this.openTransactions.get();
        while (true) {
            int nextSetBit = versionedBitSet.nextSetBit(i + 1);
            i = nextSetBit;
            if (nextSetBit > 0) {
                Transaction transaction = getTransaction(i);
                if (transaction != null && transaction.getStatus() != 0) {
                    arrayList.add(transaction);
                }
            } else {
                return arrayList;
            }
        }
    }

    public synchronized void close() {
        this.store.commit();
    }

    public Transaction begin() {
        return begin(ROLLBACK_LISTENER_NONE, this.timeoutMillis, 0, IsolationLevel.READ_COMMITTED);
    }

    public Transaction begin(RollbackListener rollbackListener, int i, int i2, IsolationLevel isolationLevel) {
        return registerTransaction(0, 1, null, 0L, i, i2, isolationLevel, rollbackListener);
    }

    private Transaction registerTransaction(int i, int i2, String str, long j, int i3, int i4, IsolationLevel isolationLevel, RollbackListener rollbackListener) {
        VersionedBitSet versionedBitSet;
        int i5;
        VersionedBitSet clone;
        long version;
        do {
            versionedBitSet = this.openTransactions.get();
            if (i == 0) {
                i5 = versionedBitSet.nextClearBit(1);
            } else {
                i5 = i;
                if (!$assertionsDisabled && versionedBitSet.get(i5)) {
                    throw new AssertionError();
                }
            }
            if (i5 > this.maxTransactionId) {
                throw DataUtils.newMVStoreException(102, "There are {0} open transactions", Integer.valueOf(i5 - 1));
            }
            clone = versionedBitSet.clone();
            clone.set(i5);
            version = clone.getVersion() + 1;
            clone.setVersion(version);
        } while (!this.openTransactions.compareAndSet(versionedBitSet, clone));
        Transaction transaction = new Transaction(this, i5, version, i2, str, j, i3, i4, isolationLevel, rollbackListener);
        if (!$assertionsDisabled && this.transactions.get(i5) != null) {
            throw new AssertionError();
        }
        this.transactions.set(i5, transaction);
        if (this.undoLogs[i5] == null) {
            this.undoLogs[i5] = this.store.openMap(getUndoLogName(i5), this.undoLogBuilder);
        }
        return transaction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void storeTransaction(Transaction transaction) {
        if (transaction.getStatus() == 2 || transaction.getName() != null) {
            this.preparedTransactions.put(Integer.valueOf(transaction.getId()), new Object[]{Integer.valueOf(transaction.getStatus()), transaction.getName()});
            transaction.wasStored = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long addUndoLogRecord(int i, long j, Record<?, ?> record) {
        MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
        long operationId = getOperationId(i, j);
        if (j == 0 && !mVMap.isEmpty()) {
            throw DataUtils.newMVStoreException(102, "An old transaction with the same id is still open: {0}", Integer.valueOf(i));
        }
        mVMap.append(Long.valueOf(operationId), record);
        return operationId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUndoLogRecord(int i) {
        this.undoLogs[i].trimLast();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeMap(TransactionMap<?, ?> transactionMap) {
        this.store.removeMap(transactionMap.map);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void commit(Transaction transaction, boolean z) {
        Cursor<Long, Record<?, ?>> cursor;
        if (this.store.isClosed()) {
            return;
        }
        int i = transaction.transactionId;
        MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
        if (z) {
            removeUndoLogRecord(i);
            cursor = mVMap.cursor(null);
        } else {
            cursor = mVMap.cursor(null);
            markUndoLogAsCommitted(i);
        }
        flipCommittingTransactionsBit(i, true);
        CommitDecisionMaker commitDecisionMaker = new CommitDecisionMaker();
        while (cursor.hasNext()) {
            try {
                Long next = cursor.next();
                Record<?, ?> value = cursor.getValue();
                MVMap openMap = openMap(value.mapId);
                if (openMap != null && !openMap.isClosed()) {
                    K k = value.key;
                    commitDecisionMaker.setUndoKey(next.longValue());
                    openMap.operate(k, null, commitDecisionMaker);
                }
            } catch (Throwable th) {
                try {
                    mVMap.clear();
                    flipCommittingTransactionsBit(i, false);
                    throw th;
                } finally {
                }
            }
        }
        try {
            mVMap.clear();
            flipCommittingTransactionsBit(i, false);
        } finally {
        }
    }

    private void flipCommittingTransactionsBit(int i, boolean z) {
        BitSet bitSet;
        BitSet bitSet2;
        do {
            bitSet = this.committingTransactions.get();
            if (!$assertionsDisabled && bitSet.get(i) == z) {
                throw new AssertionError(z ? "Double commit" : "Mysterious bit's disappearance");
            }
            bitSet2 = (BitSet) bitSet.clone();
            bitSet2.set(i, z);
        } while (!this.committingTransactions.compareAndSet(bitSet, bitSet2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <K, V> MVMap<K, VersionedValue<V>> openVersionedMap(String str, DataType<K> dataType, DataType<V> dataType2) {
        return openMap(str, dataType, dataType2 == null ? null : new VersionedValueType(dataType2));
    }

    public <K, V> MVMap<K, V> openMap(String str, DataType<K> dataType, DataType<V> dataType2) {
        return this.store.openMap(str, new TxMapBuilder(this.typeRegistry, this.dataType).keyType((DataType) dataType).valueType((DataType) dataType2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <K, V> MVMap<K, VersionedValue<V>> openMap(int i) {
        MVMap<K, V> map = this.store.getMap(i);
        if (map == null) {
            if (this.store.getMapName(i) == null) {
                return null;
            }
            map = this.store.openMap(i, new TxMapBuilder(this.typeRegistry, this.dataType));
        }
        return (MVMap<K, VersionedValue<V>>) map;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <K, V> MVMap<K, VersionedValue<V>> getMap(int i) {
        MVMap<K, VersionedValue<V>> map = this.store.getMap(i);
        if (map == null && !this.init) {
            map = openMap(i);
        }
        if ($assertionsDisabled || map != null) {
            return map;
        }
        throw new AssertionError("map with id " + i + " is missing" + (this.init ? "" : " during initialization"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void endTransaction(Transaction transaction, boolean z) {
        VersionedBitSet versionedBitSet;
        VersionedBitSet clone;
        transaction.closeIt();
        int i = transaction.transactionId;
        this.transactions.set(i, null);
        do {
            versionedBitSet = this.openTransactions.get();
            if (!$assertionsDisabled && !versionedBitSet.get(i)) {
                throw new AssertionError();
            }
            clone = versionedBitSet.clone();
            clone.clear(i);
        } while (!this.openTransactions.compareAndSet(versionedBitSet, clone));
        if (z) {
            boolean z2 = transaction.wasStored;
            if (z2 && !this.preparedTransactions.isClosed()) {
                this.preparedTransactions.remove(Integer.valueOf(i));
            }
            if (this.store.isVersioningRequired()) {
                if (z2 || this.store.getAutoCommitDelay() == 0) {
                    this.store.commit();
                    return;
                }
                if (isUndoEmpty()) {
                    if (this.store.getUnsavedMemory() * 4 > this.store.getAutoCommitMemory() * 3) {
                        this.store.tryCommit();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RootReference<Long, Record<?, ?>>[] collectUndoLogRootReferences() {
        VersionedBitSet versionedBitSet = this.openTransactions.get();
        RootReference<Long, Record<?, ?>>[] rootReferenceArr = new RootReference[versionedBitSet.length()];
        int nextSetBit = versionedBitSet.nextSetBit(0);
        while (true) {
            int i = nextSetBit;
            if (i >= 0) {
                MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
                if (mVMap != null) {
                    RootReference<Long, Record<?, ?>> root = mVMap.getRoot();
                    if (root.needFlush()) {
                        return null;
                    }
                    rootReferenceArr[i] = root;
                }
                nextSetBit = versionedBitSet.nextSetBit(i + 1);
            } else {
                return rootReferenceArr;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long calculateUndoLogsTotalSize(RootReference<Long, Record<?, ?>>[] rootReferenceArr) {
        long j = 0;
        for (RootReference<Long, Record<?, ?>> rootReference : rootReferenceArr) {
            if (rootReference != null) {
                j += rootReference.getTotalCount();
            }
        }
        return j;
    }

    private boolean isUndoEmpty() {
        VersionedBitSet versionedBitSet = this.openTransactions.get();
        int nextSetBit = versionedBitSet.nextSetBit(0);
        while (true) {
            int i = nextSetBit;
            if (i >= 0) {
                MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[i];
                if (mVMap == null || mVMap.isEmpty()) {
                    nextSetBit = versionedBitSet.nextSetBit(i + 1);
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Transaction getTransaction(int i) {
        return this.transactions.get(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rollbackTo(Transaction transaction, long j, long j2) {
        int id = transaction.getId();
        MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[id];
        RollbackDecisionMaker rollbackDecisionMaker = new RollbackDecisionMaker(this, id, j2, transaction.listener);
        long j3 = j;
        while (true) {
            long j4 = j3 - 1;
            if (j4 >= j2) {
                mVMap.operate(Long.valueOf(getOperationId(id, j4)), null, rollbackDecisionMaker);
                rollbackDecisionMaker.reset();
                j3 = j4;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Iterator<Change> getChanges(final Transaction transaction, final long j, final long j2) {
        final MVMap<Long, Record<?, ?>> mVMap = this.undoLogs[transaction.getId()];
        return new Iterator<Change>() { // from class: org.h2.mvstore.tx.TransactionStore.1
            private long logId;
            private Change current;

            {
                this.logId = j - 1;
            }

            private void fetchNext() {
                int id = transaction.getId();
                while (this.logId >= j2) {
                    Long valueOf = Long.valueOf(TransactionStore.getOperationId(id, this.logId));
                    Record record = (Record) mVMap.get(valueOf);
                    this.logId--;
                    if (record == null) {
                        Long l = (Long) mVMap.floorKey(valueOf);
                        if (l == null || TransactionStore.getTransactionId(l.longValue()) != id) {
                            break;
                        } else {
                            this.logId = TransactionStore.getLogId(l.longValue());
                        }
                    } else {
                        MVMap openMap = TransactionStore.this.openMap(record.mapId);
                        if (openMap != null) {
                            VersionedValue<V> versionedValue = record.oldValue;
                            this.current = new Change(openMap.getName(), record.key, versionedValue == 0 ? null : versionedValue.getCurrentValue());
                            return;
                        }
                    }
                }
                this.current = null;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                if (this.current == null) {
                    fetchNext();
                }
                return this.current != null;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public Change next() {
                if (!hasNext()) {
                    throw DataUtils.newUnsupportedOperationException("no data");
                }
                Change change = this.current;
                this.current = null;
                return change;
            }
        };
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionStore$Change.class */
    public static class Change {
        public final String mapName;
        public final Object key;
        public final Object value;

        public Change(String str, Object obj, Object obj2) {
            this.mapName = str;
            this.key = obj;
            this.value = obj2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionStore$TxMapBuilder.class */
    public static final class TxMapBuilder<K, V> extends MVMap.Builder<K, V> {
        private final MVMap<String, DataType<?>> typeRegistry;
        private final DataType defaultDataType;

        TxMapBuilder(MVMap<String, DataType<?>> mVMap, DataType<?> dataType) {
            this.typeRegistry = mVMap;
            this.defaultDataType = dataType;
        }

        private void registerDataType(DataType<?> dataType) {
            if (this.typeRegistry.putIfAbsent(getDataTypeRegistrationKey(dataType), dataType) != null) {
            }
        }

        static String getDataTypeRegistrationKey(DataType<?> dataType) {
            return Integer.toHexString(Objects.hashCode(dataType));
        }

        @Override // org.h2.mvstore.MVMap.BasicBuilder, org.h2.mvstore.MVMap.MapBuilder
        public MVMap<K, V> create(MVStore mVStore, Map<String, Object> map) {
            DataType<K> keyType = getKeyType();
            if (keyType == null) {
                String str = (String) map.remove("key");
                if (str != null) {
                    DataType<? super K> dataType = (DataType) this.typeRegistry.get(str);
                    if (dataType == null) {
                        throw DataUtils.newMVStoreException(106, "Data type with hash {0} can not be found", str);
                    }
                    setKeyType(dataType);
                }
            } else {
                registerDataType(keyType);
            }
            DataType<V> valueType = getValueType();
            if (valueType == null) {
                String str2 = (String) map.remove("val");
                if (str2 != null) {
                    DataType<? super V> dataType2 = (DataType) this.typeRegistry.get(str2);
                    if (dataType2 == null) {
                        throw DataUtils.newMVStoreException(106, "Data type with hash {0} can not be found", str2);
                    }
                    setValueType(dataType2);
                }
            } else {
                registerDataType(valueType);
            }
            if (getKeyType() == null) {
                setKeyType(this.defaultDataType);
                registerDataType(getKeyType());
            }
            if (getValueType() == null) {
                setValueType(new VersionedValueType(this.defaultDataType));
                registerDataType(getValueType());
            }
            map.put("store", mVStore);
            map.put("key", getKeyType());
            map.put("val", getValueType());
            return create(map);
        }

        @Override // org.h2.mvstore.MVMap.Builder, org.h2.mvstore.MVMap.BasicBuilder
        protected MVMap<K, V> create(Map<String, Object> map) {
            if ("rtree".equals(map.get("type"))) {
                return new MVRTreeMap(map, (SpatialDataType) getKeyType(), getValueType());
            }
            return new TMVMap(map, getKeyType(), getValueType());
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TransactionStore$TxMapBuilder$TMVMap.class */
        public static final class TMVMap<K, V> extends MVMap<K, V> {
            private final String type;

            TMVMap(Map<String, Object> map, DataType<K> dataType, DataType<V> dataType2) {
                super(map, dataType, dataType2);
                this.type = (String) map.get("type");
            }

            private TMVMap(MVMap<K, V> mVMap) {
                super(mVMap);
                this.type = mVMap.getType();
            }

            @Override // org.h2.mvstore.MVMap
            protected MVMap<K, V> cloneIt() {
                return new TMVMap(this);
            }

            @Override // org.h2.mvstore.MVMap
            public String getType() {
                return this.type;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.h2.mvstore.MVMap
            public String asString(String str) {
                StringBuilder sb = new StringBuilder();
                sb.append(super.asString(str));
                DataUtils.appendMap(sb, "key", TxMapBuilder.getDataTypeRegistrationKey(getKeyType()));
                DataUtils.appendMap(sb, "val", TxMapBuilder.getDataTypeRegistrationKey(getValueType()));
                return sb.toString();
            }
        }
    }
}

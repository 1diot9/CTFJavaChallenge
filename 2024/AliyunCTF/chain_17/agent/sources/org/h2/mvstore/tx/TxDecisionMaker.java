package org.h2.mvstore.tx;

import java.util.function.Function;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.DataType;
import org.h2.value.VersionedValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TxDecisionMaker.class */
public class TxDecisionMaker<K, V> extends MVMap.DecisionMaker<VersionedValue<V>> {
    private final int mapId;
    protected K key;
    private V value;
    private final Transaction transaction;
    private long undoKey;
    private long lastOperationId;
    private Transaction blockingTransaction;
    private MVMap.Decision decision;
    private V lastValue;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !TxDecisionMaker.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TxDecisionMaker(int i, Transaction transaction) {
        this.mapId = i;
        this.transaction = transaction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initialize(K k, V v) {
        this.key = k;
        this.value = v;
        this.decision = null;
        reset();
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public MVMap.Decision decide(VersionedValue<V> versionedValue, VersionedValue<V> versionedValue2) {
        if (!$assertionsDisabled && this.decision != null) {
            throw new AssertionError();
        }
        if (versionedValue != null) {
            long operationId = versionedValue.getOperationId();
            if (operationId != 0) {
                int transactionId = TransactionStore.getTransactionId(operationId);
                if (!isThisTransaction(transactionId)) {
                    if (isCommitted(transactionId)) {
                        V currentValue = versionedValue.getCurrentValue();
                        logAndDecideToPut(currentValue == null ? null : VersionedValueCommitted.getInstance(currentValue), currentValue);
                    } else if (getBlockingTransaction() != null) {
                        this.lastValue = versionedValue.getCurrentValue();
                        this.decision = MVMap.Decision.ABORT;
                    } else if (isRepeatedOperation(operationId)) {
                        V committedValue = versionedValue.getCommittedValue();
                        logAndDecideToPut(committedValue == null ? null : VersionedValueCommitted.getInstance(committedValue), committedValue);
                    } else {
                        this.decision = MVMap.Decision.REPEAT;
                    }
                    return this.decision;
                }
            }
        }
        logAndDecideToPut(versionedValue, versionedValue == null ? null : versionedValue.getCommittedValue());
        return this.decision;
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public final void reset() {
        if (this.decision != MVMap.Decision.REPEAT) {
            this.lastOperationId = 0L;
            if (this.decision == MVMap.Decision.PUT) {
                this.transaction.logUndo();
            }
        }
        this.blockingTransaction = null;
        this.decision = null;
        this.lastValue = null;
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public <T extends VersionedValue<V>> T selectValue(T t, T t2) {
        return (T) VersionedValueUncommitted.getInstance(this.undoKey, getNewValue(t), this.lastValue);
    }

    V getNewValue(VersionedValue<V> versionedValue) {
        return this.value;
    }

    MVMap.Decision logAndDecideToPut(VersionedValue<V> versionedValue, V v) {
        this.undoKey = this.transaction.log(new Record<>(this.mapId, this.key, versionedValue));
        this.lastValue = v;
        return setDecision(MVMap.Decision.PUT);
    }

    final MVMap.Decision decideToAbort(V v) {
        this.lastValue = v;
        return setDecision(MVMap.Decision.ABORT);
    }

    final boolean allowNonRepeatableRead() {
        return this.transaction.allowNonRepeatableRead();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final MVMap.Decision getDecision() {
        return this.decision;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Transaction getBlockingTransaction() {
        return this.blockingTransaction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final V getLastValue() {
        return this.lastValue;
    }

    final boolean isThisTransaction(int i) {
        return i == this.transaction.transactionId;
    }

    final boolean isCommitted(int i) {
        Transaction transaction;
        boolean z;
        TransactionStore transactionStore = this.transaction.store;
        do {
            transaction = transactionStore.getTransaction(i);
            z = transactionStore.committingTransactions.get().get(i);
        } while (transaction != transactionStore.getTransaction(i));
        if (!z) {
            this.blockingTransaction = transaction;
        }
        return z;
    }

    final boolean isRepeatedOperation(long j) {
        if (j == this.lastOperationId) {
            return true;
        }
        this.lastOperationId = j;
        return false;
    }

    final MVMap.Decision setDecision(MVMap.Decision decision) {
        this.decision = decision;
        return decision;
    }

    public final String toString() {
        return "txdm " + this.transaction.transactionId;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TxDecisionMaker$PutIfAbsentDecisionMaker.class */
    public static final class PutIfAbsentDecisionMaker<K, V> extends TxDecisionMaker<K, V> {
        private final Function<K, V> oldValueSupplier;
        static final /* synthetic */ boolean $assertionsDisabled;

        @Override // org.h2.mvstore.tx.TxDecisionMaker
        public /* bridge */ /* synthetic */ VersionedValue selectValue(VersionedValue versionedValue, VersionedValue versionedValue2) {
            return super.selectValue(versionedValue, versionedValue2);
        }

        @Override // org.h2.mvstore.tx.TxDecisionMaker, org.h2.mvstore.MVMap.DecisionMaker
        public /* bridge */ /* synthetic */ Object selectValue(Object obj, Object obj2) {
            return super.selectValue((VersionedValue) obj, (VersionedValue) obj2);
        }

        static {
            $assertionsDisabled = !TxDecisionMaker.class.desiredAssertionStatus();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public PutIfAbsentDecisionMaker(int i, Transaction transaction, Function<K, V> function) {
            super(i, transaction);
            this.oldValueSupplier = function;
        }

        @Override // org.h2.mvstore.tx.TxDecisionMaker, org.h2.mvstore.MVMap.DecisionMaker
        public MVMap.Decision decide(VersionedValue<V> versionedValue, VersionedValue<V> versionedValue2) {
            V valueInSnapshot;
            if (!$assertionsDisabled && getDecision() != null) {
                throw new AssertionError();
            }
            if (versionedValue == null) {
                V valueInSnapshot2 = getValueInSnapshot();
                if (valueInSnapshot2 != null) {
                    return decideToAbort(valueInSnapshot2);
                }
                return logAndDecideToPut(null, null);
            }
            long operationId = versionedValue.getOperationId();
            if (operationId != 0) {
                int transactionId = TransactionStore.getTransactionId(operationId);
                if (!isThisTransaction(transactionId)) {
                    if (isCommitted(transactionId)) {
                        if (versionedValue.getCurrentValue() != null) {
                            return decideToAbort(versionedValue.getCurrentValue());
                        }
                        V valueInSnapshot3 = getValueInSnapshot();
                        if (valueInSnapshot3 != null) {
                            return decideToAbort(valueInSnapshot3);
                        }
                        return logAndDecideToPut(null, null);
                    }
                    if (getBlockingTransaction() != null) {
                        return decideToAbort(versionedValue.getCurrentValue());
                    }
                    if (isRepeatedOperation(operationId)) {
                        V committedValue = versionedValue.getCommittedValue();
                        if (committedValue != null) {
                            return decideToAbort(committedValue);
                        }
                        return logAndDecideToPut(null, null);
                    }
                    return setDecision(MVMap.Decision.REPEAT);
                }
            }
            if (versionedValue.getCurrentValue() != null) {
                return decideToAbort(versionedValue.getCurrentValue());
            }
            if (operationId == 0 && (valueInSnapshot = getValueInSnapshot()) != null) {
                return decideToAbort(valueInSnapshot);
            }
            return logAndDecideToPut(versionedValue, versionedValue.getCommittedValue());
        }

        private V getValueInSnapshot() {
            if (allowNonRepeatableRead()) {
                return null;
            }
            return this.oldValueSupplier.apply(this.key);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TxDecisionMaker$LockDecisionMaker.class */
    public static class LockDecisionMaker<K, V> extends TxDecisionMaker<K, V> {
        static final /* synthetic */ boolean $assertionsDisabled;

        @Override // org.h2.mvstore.tx.TxDecisionMaker
        public /* bridge */ /* synthetic */ VersionedValue selectValue(VersionedValue versionedValue, VersionedValue versionedValue2) {
            return super.selectValue(versionedValue, versionedValue2);
        }

        @Override // org.h2.mvstore.tx.TxDecisionMaker, org.h2.mvstore.MVMap.DecisionMaker
        public /* bridge */ /* synthetic */ Object selectValue(Object obj, Object obj2) {
            return super.selectValue((VersionedValue) obj, (VersionedValue) obj2);
        }

        static {
            $assertionsDisabled = !TxDecisionMaker.class.desiredAssertionStatus();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public LockDecisionMaker(int i, Transaction transaction) {
            super(i, transaction);
        }

        @Override // org.h2.mvstore.tx.TxDecisionMaker, org.h2.mvstore.MVMap.DecisionMaker
        public MVMap.Decision decide(VersionedValue<V> versionedValue, VersionedValue<V> versionedValue2) {
            MVMap.Decision decide = super.decide((VersionedValue) versionedValue, (VersionedValue) versionedValue2);
            if (versionedValue == null) {
                if (!$assertionsDisabled && decide != MVMap.Decision.PUT) {
                    throw new AssertionError();
                }
                decide = setDecision(MVMap.Decision.REMOVE);
            }
            return decide;
        }

        @Override // org.h2.mvstore.tx.TxDecisionMaker
        V getNewValue(VersionedValue<V> versionedValue) {
            if (versionedValue == null) {
                return null;
            }
            return versionedValue.getCurrentValue();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/TxDecisionMaker$RepeatableReadLockDecisionMaker.class */
    public static final class RepeatableReadLockDecisionMaker<K, V> extends LockDecisionMaker<K, V> {
        private final DataType<VersionedValue<V>> valueType;
        private final Function<K, V> snapshotValueSupplier;

        /* JADX INFO: Access modifiers changed from: package-private */
        public RepeatableReadLockDecisionMaker(int i, Transaction transaction, DataType<VersionedValue<V>> dataType, Function<K, V> function) {
            super(i, transaction);
            this.valueType = dataType;
            this.snapshotValueSupplier = function;
        }

        @Override // org.h2.mvstore.tx.TxDecisionMaker
        MVMap.Decision logAndDecideToPut(VersionedValue<V> versionedValue, V v) {
            V apply = this.snapshotValueSupplier.apply(this.key);
            if (apply != null && (versionedValue == null || this.valueType.compare(VersionedValueCommitted.getInstance(apply), versionedValue) != 0)) {
                throw DataUtils.newMVStoreException(105, "", new Object[0]);
            }
            return super.logAndDecideToPut(versionedValue, v);
        }
    }
}

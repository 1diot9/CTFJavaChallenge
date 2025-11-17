package org.h2.mvstore.tx;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.tx.TransactionStore;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/RollbackDecisionMaker.class */
final class RollbackDecisionMaker extends MVMap.DecisionMaker<Record<?, ?>> {
    private final TransactionStore store;
    private final long transactionId;
    private final long toLogId;
    private final TransactionStore.RollbackListener listener;
    private MVMap.Decision decision;
    static final /* synthetic */ boolean $assertionsDisabled;

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public /* bridge */ /* synthetic */ MVMap.Decision decide(Record<?, ?> record, Record<?, ?> record2) {
        return decide2((Record) record, (Record) record2);
    }

    static {
        $assertionsDisabled = !RollbackDecisionMaker.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RollbackDecisionMaker(TransactionStore transactionStore, long j, long j2, TransactionStore.RollbackListener rollbackListener) {
        this.store = transactionStore;
        this.transactionId = j;
        this.toLogId = j2;
        this.listener = rollbackListener;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0050, code lost:            if (org.h2.mvstore.tx.TransactionStore.getLogId(r0) >= r6.toLogId) goto L24;     */
    /* renamed from: decide, reason: avoid collision after fix types in other method */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.mvstore.MVMap.Decision decide2(org.h2.mvstore.tx.Record r7, org.h2.mvstore.tx.Record r8) {
        /*
            r6 = this;
            boolean r0 = org.h2.mvstore.tx.RollbackDecisionMaker.$assertionsDisabled
            if (r0 != 0) goto L15
            r0 = r6
            org.h2.mvstore.MVMap$Decision r0 = r0.decision
            if (r0 == 0) goto L15
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r1 = r0
            r1.<init>()
            throw r0
        L15:
            r0 = r7
            if (r0 != 0) goto L23
            r0 = r6
            org.h2.mvstore.MVMap$Decision r1 = org.h2.mvstore.MVMap.Decision.ABORT
            r0.decision = r1
            goto L9e
        L23:
            r0 = r7
            org.h2.value.VersionedValue<V> r0 = r0.oldValue
            r9 = r0
            r0 = r9
            if (r0 == 0) goto L53
            r0 = r9
            long r0 = r0.getOperationId()
            r1 = r0; r0 = r0; 
            r10 = r1
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L53
            r0 = r10
            int r0 = org.h2.mvstore.tx.TransactionStore.getTransactionId(r0)
            long r0 = (long) r0
            r1 = r6
            long r1 = r1.transactionId
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 != 0) goto L97
            r0 = r10
            long r0 = org.h2.mvstore.tx.TransactionStore.getLogId(r0)
            r1 = r6
            long r1 = r1.toLogId
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L97
        L53:
            r0 = r7
            int r0 = r0.mapId
            r12 = r0
            r0 = r6
            org.h2.mvstore.tx.TransactionStore r0 = r0.store
            r1 = r12
            org.h2.mvstore.MVMap r0 = r0.openMap(r1)
            r13 = r0
            r0 = r13
            if (r0 == 0) goto L97
            r0 = r13
            boolean r0 = r0.isClosed()
            if (r0 != 0) goto L97
            r0 = r7
            K r0 = r0.key
            r14 = r0
            r0 = r13
            r1 = r14
            r2 = r9
            org.h2.mvstore.MVMap$DecisionMaker<java.lang.Object> r3 = org.h2.mvstore.MVMap.DecisionMaker.DEFAULT
            java.lang.Object r0 = r0.operate(r1, r2, r3)
            org.h2.value.VersionedValue r0 = (org.h2.value.VersionedValue) r0
            r15 = r0
            r0 = r6
            org.h2.mvstore.tx.TransactionStore$RollbackListener r0 = r0.listener
            r1 = r13
            r2 = r14
            r3 = r15
            r4 = r9
            r0.onRollback(r1, r2, r3, r4)
        L97:
            r0 = r6
            org.h2.mvstore.MVMap$Decision r1 = org.h2.mvstore.MVMap.Decision.REMOVE
            r0.decision = r1
        L9e:
            r0 = r6
            org.h2.mvstore.MVMap$Decision r0 = r0.decision
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.mvstore.tx.RollbackDecisionMaker.decide2(org.h2.mvstore.tx.Record, org.h2.mvstore.tx.Record):org.h2.mvstore.MVMap$Decision");
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public void reset() {
        this.decision = null;
    }

    public String toString() {
        return "rollback-" + this.transactionId;
    }
}

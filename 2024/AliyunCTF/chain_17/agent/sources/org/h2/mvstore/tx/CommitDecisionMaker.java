package org.h2.mvstore.tx;

import org.h2.mvstore.MVMap;
import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/CommitDecisionMaker.class */
final class CommitDecisionMaker<V> extends MVMap.DecisionMaker<VersionedValue<V>> {
    private long undoKey;
    private MVMap.Decision decision;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !CommitDecisionMaker.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUndoKey(long j) {
        this.undoKey = j;
        reset();
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public MVMap.Decision decide(VersionedValue<V> versionedValue, VersionedValue<V> versionedValue2) {
        if (!$assertionsDisabled && this.decision != null) {
            throw new AssertionError();
        }
        if (versionedValue == null || versionedValue.getOperationId() != this.undoKey) {
            this.decision = MVMap.Decision.ABORT;
        } else if (versionedValue.getCurrentValue() == null) {
            this.decision = MVMap.Decision.REMOVE;
        } else {
            this.decision = MVMap.Decision.PUT;
        }
        return this.decision;
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public <T extends VersionedValue<V>> T selectValue(T t, T t2) {
        if (!$assertionsDisabled && this.decision != MVMap.Decision.PUT) {
            throw new AssertionError();
        }
        if ($assertionsDisabled || t != null) {
            return (T) VersionedValueCommitted.getInstance(t.getCurrentValue());
        }
        throw new AssertionError();
    }

    @Override // org.h2.mvstore.MVMap.DecisionMaker
    public void reset() {
        this.decision = null;
    }

    public String toString() {
        return "commit " + TransactionStore.getTransactionId(this.undoKey);
    }
}

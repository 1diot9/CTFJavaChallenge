package org.h2.mvstore.tx;

import cn.hutool.core.text.CharSequenceUtil;
import org.h2.value.VersionedValue;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/VersionedValueUncommitted.class */
public class VersionedValueUncommitted<T> extends VersionedValueCommitted<T> {
    private final long operationId;
    private final T committedValue;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !VersionedValueUncommitted.class.desiredAssertionStatus();
    }

    private VersionedValueUncommitted(long j, T t, T t2) {
        super(t);
        if (!$assertionsDisabled && j == 0) {
            throw new AssertionError();
        }
        this.operationId = j;
        this.committedValue = t2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <X> VersionedValue<X> getInstance(long j, X x, X x2) {
        return new VersionedValueUncommitted(j, x, x2);
    }

    @Override // org.h2.value.VersionedValue
    public boolean isCommitted() {
        return false;
    }

    @Override // org.h2.value.VersionedValue
    public long getOperationId() {
        return this.operationId;
    }

    @Override // org.h2.mvstore.tx.VersionedValueCommitted, org.h2.value.VersionedValue
    public T getCommittedValue() {
        return this.committedValue;
    }

    @Override // org.h2.mvstore.tx.VersionedValueCommitted
    public String toString() {
        return super.toString() + CharSequenceUtil.SPACE + TransactionStore.getTransactionId(this.operationId) + "/" + TransactionStore.getLogId(this.operationId) + CharSequenceUtil.SPACE + this.committedValue;
    }
}

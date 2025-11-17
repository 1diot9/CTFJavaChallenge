package org.h2.mvstore.tx;

import org.h2.value.VersionedValue;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mvstore/tx/VersionedValueCommitted.class */
class VersionedValueCommitted<T> extends VersionedValue<T> {
    public final T value;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !VersionedValueCommitted.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VersionedValueCommitted(T t) {
        this.value = t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static <X> VersionedValue<X> getInstance(X x) {
        if ($assertionsDisabled || x != 0) {
            return x instanceof VersionedValue ? (VersionedValue) x : new VersionedValueCommitted(x);
        }
        throw new AssertionError();
    }

    @Override // org.h2.value.VersionedValue
    public T getCurrentValue() {
        return this.value;
    }

    @Override // org.h2.value.VersionedValue
    public T getCommittedValue() {
        return this.value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }
}

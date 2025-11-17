package org.h2.value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/VersionedValue.class */
public class VersionedValue<T> {
    public boolean isCommitted() {
        return true;
    }

    public long getOperationId() {
        return 0L;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T getCurrentValue() {
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T getCommittedValue() {
        return this;
    }
}

package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.jooq.RowId;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowIdImpl.class */
final class RowIdImpl extends Record implements RowId {
    private final Object value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowIdImpl(Object value) {
        this.value = value;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, RowIdImpl.class), RowIdImpl.class, "value", "FIELD:Lorg/jooq/impl/RowIdImpl;->value:Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, RowIdImpl.class, Object.class), RowIdImpl.class, "value", "FIELD:Lorg/jooq/impl/RowIdImpl;->value:Ljava/lang/Object;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    @Override // org.jooq.RowId
    public Object value() {
        return this.value;
    }

    @Override // java.lang.Record
    public String toString() {
        return String.valueOf(this.value);
    }
}

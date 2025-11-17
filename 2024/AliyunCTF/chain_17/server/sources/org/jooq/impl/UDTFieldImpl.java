package org.jooq.impl;

import org.jooq.Binding;
import org.jooq.Comment;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.UDT;
import org.jooq.UDTField;
import org.jooq.UDTRecord;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UDTFieldImpl.class */
final class UDTFieldImpl<R extends UDTRecord<R>, T> extends AbstractField<T> implements UDTField<R, T>, QOM.UNotYetImplemented, TypedReference<T> {
    private final UDT<R> udt;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UDTFieldImpl(Name name, DataType<T> type, UDT<R> udt, Comment comment, Binding<?, T> binding) {
        super(name, type, comment, binding);
        this.udt = udt;
        if (udt instanceof UDTImpl) {
            UDTImpl<?> u = (UDTImpl) udt;
            u.fields0().add(this);
        }
    }

    @Override // org.jooq.UDTField
    public final UDT<R> getUDT() {
        return this.udt;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.literal(getName());
    }
}

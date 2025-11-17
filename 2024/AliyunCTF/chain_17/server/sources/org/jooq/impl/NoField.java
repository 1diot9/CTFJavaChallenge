package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoField.class */
public final class NoField<T> extends AbstractField<T> implements SortField<T>, QOM.UEmptyField<T> {
    static final NoField<?> INSTANCE = new NoField<>(SQLDataType.OTHER);

    /* JADX INFO: Access modifiers changed from: package-private */
    public NoField(DataType<T> type) {
        super(Names.N_NULL, type);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit((Field<?>) DSL.inline((Object) null, getDataType()));
    }

    @Override // org.jooq.SortField
    public final SortOrder getOrder() {
        return SortOrder.DEFAULT;
    }

    @Override // org.jooq.SortField
    public final Field<T> $field() {
        return this;
    }

    @Override // org.jooq.SortField
    public final <U> SortField<U> $field(Field<U> newField) {
        return newField.sortDefault();
    }

    @Override // org.jooq.SortField
    public final SortOrder $sortOrder() {
        return SortOrder.DEFAULT;
    }

    @Override // org.jooq.SortField
    public final SortField<T> $sortOrder(SortOrder newOrder) {
        return this;
    }

    @Override // org.jooq.SortField
    public final QOM.NullOrdering $nullOrdering() {
        return null;
    }

    @Override // org.jooq.SortField
    public final SortField<T> $nullOrdering(QOM.NullOrdering newOrdering) {
        return this;
    }
}

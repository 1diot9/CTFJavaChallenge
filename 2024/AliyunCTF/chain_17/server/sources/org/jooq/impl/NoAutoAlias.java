package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NoAutoAlias.class */
public final class NoAutoAlias<R extends Record> extends AbstractDelegatingTable<R> implements QOM.UTransient {
    NoAutoAlias(AbstractTable<R> delegate) {
        super(delegate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> noAutoAlias(Table<R> table) {
        return table instanceof AutoAlias ? new NoAutoAlias((AbstractTable) table) : table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractDelegatingTable
    public final <O extends Record> NoAutoAlias<O> construct(AbstractTable<O> newDelegate) {
        return new NoAutoAlias<>(newDelegate);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(this.delegate);
    }
}

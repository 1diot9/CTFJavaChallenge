package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Default.class */
public final class Default<T> extends AbstractField<T> implements QOM.Default<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Default(DataType<T> type) {
        super(Names.N_DEFAULT, type);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Keywords.K_DEFAULT);
    }
}

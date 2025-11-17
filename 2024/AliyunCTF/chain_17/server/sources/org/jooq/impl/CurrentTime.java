package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CurrentTime.class */
public final class CurrentTime<T> extends AbstractField<T> implements QOM.CurrentTime<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CurrentTime(DataType<T> type) {
        super(Names.N_CURRENT_TIME, type);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(Names.N_CURRENT_TIME).sql("()");
                return;
            default:
                ctx.visit(Keywords.K_CURRENT).sql('_').visit(Keywords.K_TIME);
                return;
        }
    }
}

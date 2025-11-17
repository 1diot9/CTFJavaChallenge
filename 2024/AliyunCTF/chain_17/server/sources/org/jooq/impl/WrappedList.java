package org.jooq.impl;

import org.jooq.Context;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WrappedList.class */
final class WrappedList extends AbstractQueryPart implements QOM.UTransient {
    private final QueryPartList<?> wrapped;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WrappedList(QueryPartList<?> wrapped) {
        this.wrapped = wrapped;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.sql('(').visit(this.wrapped).sql(')');
    }
}

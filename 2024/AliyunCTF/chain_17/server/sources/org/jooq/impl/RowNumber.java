package org.jooq.impl;

import org.jooq.Context;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowNumber.class */
public final class RowNumber extends AbstractWindowFunction<Integer> implements QOM.RowNumber {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RowNumber() {
        super(Names.N_ROW_NUMBER, SQLDataType.INTEGER.notNull());
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case HSQLDB:
                ctx.visit(Names.N_ROWNUM).sql("()");
                return;
            default:
                ctx.visit(Names.N_ROW_NUMBER).sql("()");
                acceptOverClause(ctx);
                return;
        }
    }
}

package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CumeDist.class */
public final class CumeDist extends AbstractWindowFunction<BigDecimal> implements QOM.CumeDist {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CumeDist() {
        super(Names.N_CUME_DIST, SQLDataType.NUMERIC.notNull());
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Names.N_CUME_DIST).sql("()");
        acceptOverClause(ctx);
    }
}

package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Statement;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/StartTransaction.class */
public final class StartTransaction extends AbstractRowCountQuery implements QOM.StartTransaction {
    /* JADX INFO: Access modifiers changed from: package-private */
    public StartTransaction(Configuration configuration) {
        super(configuration);
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                if (ctx.data(Tools.SimpleDataKey.DATA_BLOCK_NESTING) == null) {
                    ctx.visit(Keywords.K_START).sql(' ').visit(Keywords.K_TRANSACTION);
                    return;
                }
                return;
            case FIREBIRD:
                if (ctx.data(Tools.SimpleDataKey.DATA_BLOCK_NESTING) == null) {
                    ctx.visit(DSL.begin(new Statement[0]));
                    return;
                }
                return;
            case H2:
            case SQLITE:
                ctx.visit(Keywords.K_BEGIN).sql(' ').visit(Keywords.K_TRANSACTION);
                return;
            case HSQLDB:
                ctx.visit(Keywords.K_START).sql(' ').visit(Keywords.K_TRANSACTION).sql(' ').visit(Keywords.K_READ).sql(' ').visit(Keywords.K_WRITE);
                return;
            default:
                ctx.visit(Keywords.K_START).sql(' ').visit(Keywords.K_TRANSACTION);
                return;
        }
    }
}

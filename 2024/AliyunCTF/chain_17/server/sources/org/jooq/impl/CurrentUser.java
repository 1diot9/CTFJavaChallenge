package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function0;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CurrentUser.class */
public final class CurrentUser extends AbstractField<String> implements QOM.CurrentUser {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CurrentUser() {
        super(Names.N_CURRENT_USER, Tools.allNotNull(SQLDataType.VARCHAR));
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case FIREBIRD:
            case HSQLDB:
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(Names.N_CURRENT_USER);
                return;
            case SQLITE:
                ctx.visit((Field<?>) DSL.inline(""));
                return;
            default:
                ctx.visit(Names.N_CURRENT_USER).sql("()");
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.CurrentUser> $constructor() {
        return () -> {
            return new CurrentUser();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.CurrentUser) {
            return true;
        }
        return super.equals(that);
    }
}

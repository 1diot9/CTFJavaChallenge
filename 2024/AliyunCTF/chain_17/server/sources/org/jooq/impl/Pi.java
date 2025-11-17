package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function0;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Pi.class */
public final class Pi extends AbstractField<BigDecimal> implements QOM.Pi {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Pi() {
        super(Names.N_PI, Tools.allNotNull(SQLDataType.NUMERIC));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                ctx.visit(DSL.function(Names.N_PI, getDataType(), (Field<?>[]) new Field[0]));
                return;
            default:
                ctx.visit(DSL.function(Names.N_PI, getDataType(), (Field<?>[]) new Field[0]));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.Pi> $constructor() {
        return () -> {
            return new Pi();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Pi) {
            return true;
        }
        return super.equals(that);
    }
}

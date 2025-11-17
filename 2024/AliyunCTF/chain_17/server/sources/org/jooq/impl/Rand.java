package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function0;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Rand.class */
public final class Rand extends AbstractField<BigDecimal> implements QOM.Rand {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Rand() {
        super(Names.N_RAND, Tools.allNotNull(SQLDataType.NUMERIC));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                return true;
            case DERBY:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                return true;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                ctx.visit(DSL.function(Names.N_RANDOM, getDataType(), (Field<?>[]) new Field[0]));
                return;
            case DERBY:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                ctx.visit(DSL.function(Names.N_RANDOM, getDataType(), (Field<?>[]) new Field[0]));
                return;
            default:
                ctx.visit(DSL.function(Names.N_RAND, getDataType(), (Field<?>[]) new Field[0]));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.Rand> $constructor() {
        return () -> {
            return new Rand();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Rand) {
            return true;
        }
        return super.equals(that);
    }
}

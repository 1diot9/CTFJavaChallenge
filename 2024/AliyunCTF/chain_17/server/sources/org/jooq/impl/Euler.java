package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function0;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Euler.class */
final class Euler extends AbstractField<BigDecimal> implements QOM.Euler {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Euler() {
        super(Names.N_E, Tools.allNotNull(SQLDataType.NUMERIC));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case YUGABYTEDB:
                return false;
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit((Field<?>) DSL.exp(DSL.one()));
                return;
            case SQLITE:
                ctx.visit((Field<?>) DSL.exp(DSL.one()));
                return;
            default:
                ctx.visit(DSL.function(Names.N_E, getDataType(), (Field<?>[]) new Field[0]));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.Euler> $constructor() {
        return () -> {
            return new Euler();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Euler) {
            return true;
        }
        return super.equals(that);
    }
}

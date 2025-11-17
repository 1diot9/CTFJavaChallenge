package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Space.class */
public final class Space extends AbstractField<String> implements QOM.Space {
    final Field<? extends Number> count;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Space(Field<? extends Number> count) {
        super(Names.N_SPACE, Tools.allNotNull(SQLDataType.VARCHAR, count));
        this.count = Tools.nullSafeNotNull(count, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
            case SQLITE:
            case TRINO:
                return false;
            case DERBY:
            case DUCKDB:
            case HSQLDB:
            case POSTGRES:
            case YUGABYTEDB:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
            case SQLITE:
            case TRINO:
                ctx.visit((Field<?>) DSL.rpad(DSL.inline(' '), this.count));
                return;
            case DERBY:
            case DUCKDB:
            case HSQLDB:
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit((Field<?>) DSL.repeat(DSL.inline(" "), this.count));
                return;
            default:
                ctx.visit(DSL.function(Names.N_SPACE, getDataType(), this.count));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.count;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Space $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Space> $constructor() {
        return a1 -> {
            return new Space(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Space) {
            QOM.Space o = (QOM.Space) that;
            return StringUtils.equals($count(), o.$count());
        }
        return super.equals(that);
    }
}

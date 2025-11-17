package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Coth.class */
public final class Coth extends AbstractField<BigDecimal> implements QOM.Coth {
    final Field<? extends Number> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Coth(Field<? extends Number> value) {
        super(Names.N_COTH, Tools.allNotNull(SQLDataType.NUMERIC, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
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
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
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
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                ctx.visit(Internal.idiv(Internal.iadd(DSL.exp((Field<? extends Number>) Internal.imul(this.value, DSL.two())), DSL.one()), Internal.isub(DSL.exp((Field<? extends Number>) Internal.imul(this.value, DSL.two())), DSL.one())));
                return;
            default:
                ctx.visit(DSL.function(Names.N_COTH, getDataType(), this.value));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Coth $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Coth> $constructor() {
        return a1 -> {
            return new Coth(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Coth) {
            QOM.Coth o = (QOM.Coth) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Log.class */
public final class Log extends AbstractField<BigDecimal> implements QOM.Log {
    final Field<? extends Number> value;
    final Field<? extends Number> base;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Log(Field<? extends Number> value, Field<? extends Number> base) {
        super(Names.N_LOG, Tools.allNotNull(SQLDataType.NUMERIC, value, base));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.base = Tools.nullSafeNotNull(base, SQLDataType.INTEGER);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case DUCKDB:
            case HSQLDB:
            case IGNITE:
            case SQLITE:
                ctx.visit(Internal.idiv(DSL.ln(this.value), DSL.ln(this.base)));
                return;
            default:
                ctx.visit(DSL.function(Names.N_LOG, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{this.base, this.value}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.base;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Log $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Log $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.Log> $constructor() {
        return (a1, a2) -> {
            return new Log(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Log)) {
            return super.equals(that);
        }
        QOM.Log o = (QOM.Log) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($base(), o.$base());
    }
}

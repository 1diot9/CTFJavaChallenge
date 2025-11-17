package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Power.class */
public final class Power extends AbstractField<BigDecimal> implements QOM.Power {
    final Field<? extends Number> base;
    final Field<? extends Number> exponent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Power(Field<? extends Number> base, Field<? extends Number> exponent) {
        super(Names.N_POWER, Tools.allNotNull(SQLDataType.NUMERIC, base, exponent));
        this.base = Tools.nullSafeNotNull(base, SQLDataType.INTEGER);
        this.exponent = Tools.nullSafeNotNull(exponent, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                ctx.visit((Field<?>) DSL.exp((Field<? extends Number>) Internal.imul(DSL.ln(this.base), this.exponent)));
                return;
            default:
                ctx.visit(DSL.function(Names.N_POWER, getDataType(), (Field<?>[]) new Field[]{this.base, this.exponent}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg1() {
        return this.base;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.exponent;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Power $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Power $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.Power> $constructor() {
        return (a1, a2) -> {
            return new Power(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Power)) {
            return super.equals(that);
        }
        QOM.Power o = (QOM.Power) that;
        return StringUtils.equals($base(), o.$base()) && StringUtils.equals($exponent(), o.$exponent());
    }
}

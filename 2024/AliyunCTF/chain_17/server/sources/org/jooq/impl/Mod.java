package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Mod.class */
public final class Mod<T extends Number> extends AbstractField<T> implements QOM.Mod<T> {
    final Field<T> dividend;
    final Field<? extends Number> divisor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Mod(Field<T> dividend, Field<? extends Number> divisor) {
        super(Names.N_MOD, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, dividend, false), dividend, divisor));
        this.dividend = Tools.nullSafeNotNull(dividend, SQLDataType.INTEGER);
        this.divisor = Tools.nullSafeNotNull(divisor, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case SQLITE:
                ctx.sql('(').visit(this.dividend).sql(" % ").visit((Field<?>) this.divisor).sql(')');
                return;
            default:
                ctx.visit(DSL.function(Names.N_MOD, getDataType(), (Field<?>[]) new Field[]{this.dividend, this.divisor}));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.dividend;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.divisor;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Mod<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Mod<T> $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<? extends Number>, ? extends QOM.Mod<T>> $constructor() {
        return (a1, a2) -> {
            return new Mod(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Mod)) {
            return super.equals(that);
        }
        QOM.Mod<?> o = (QOM.Mod) that;
        return StringUtils.equals($dividend(), o.$dividend()) && StringUtils.equals($divisor(), o.$divisor());
    }
}

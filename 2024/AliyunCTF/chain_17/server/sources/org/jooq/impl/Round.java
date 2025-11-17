package org.jooq.impl;

import java.lang.Number;
import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Param;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Round.class */
public final class Round<T extends Number> extends AbstractField<T> implements QOM.Round<T> {
    final Field<T> value;
    final Field<Integer> decimals;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Round(Field<T> value) {
        super(Names.N_ROUND, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), (Field<?>) value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.decimals = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Round(Field<T> value, Field<Integer> decimals) {
        super(Names.N_ROUND, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), value, decimals));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.decimals = Tools.nullSafeNotNull(decimals, SQLDataType.INTEGER);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                if (this.decimals == null) {
                    ctx.visit(DSL.when(Internal.isub(this.value, DSL.floor(this.value)).lessThan((Field) DSL.inline(Double.valueOf(0.5d))), DSL.floor(this.value)).otherwise(DSL.ceil(this.value)));
                    return;
                }
                Field<Integer> field = this.decimals;
                if (field instanceof Param) {
                    Param<Integer> p = (Param) field;
                    Integer decimalsValue = p.getValue();
                    Field<?> factor = DSL.val(BigDecimal.ONE.movePointRight(decimalsValue.intValue()));
                    Field<T> mul = Internal.imul(this.value, factor);
                    ctx.visit(DSL.when(Internal.isub(mul, DSL.floor(mul)).lessThan((Field) DSL.inline(Double.valueOf(0.5d))), Internal.idiv(DSL.floor(mul), factor)).otherwise(Internal.idiv(DSL.ceil(mul), factor)));
                    return;
                }
                break;
            case POSTGRES:
            case YUGABYTEDB:
                break;
            default:
                if (this.decimals == null) {
                    ctx.visit(DSL.function(Names.N_ROUND, (DataType) getDataType(), (Field<?>) this.value));
                    return;
                } else {
                    ctx.visit(DSL.function(Names.N_ROUND, getDataType(), (Field<?>[]) new Field[]{this.value, this.decimals}));
                    return;
                }
        }
        if (this.decimals == null) {
            ctx.visit(DSL.function(Names.N_ROUND, (DataType) getDataType(), (Field<?>) this.value));
        } else {
            ctx.visit(DSL.function(Names.N_ROUND, getDataType(), (Field<?>[]) new Field[]{Tools.castIfNeeded((Field<?>) this.value, (DataType) SQLDataType.NUMERIC), this.decimals}));
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<Integer> $arg2() {
        return this.decimals;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Round<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Round<T> $arg2(Field<Integer> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<Integer>, ? extends QOM.Round<T>> $constructor() {
        return (a1, a2) -> {
            return new Round(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Round)) {
            return super.equals(that);
        }
        QOM.Round<?> o = (QOM.Round) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($decimals(), o.$decimals());
    }
}

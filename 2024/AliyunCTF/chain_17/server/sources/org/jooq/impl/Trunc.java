package org.jooq.impl;

import java.lang.Number;
import java.math.BigDecimal;
import java.math.MathContext;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Trunc.class */
public final class Trunc<T extends Number> extends AbstractField<T> implements QOM.Trunc<T> {
    final Field<T> value;
    final Field<Integer> decimals;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Trunc(Field<T> value, Field<Integer> decimals) {
        super(Names.N_TRUNC, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), value, decimals));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.decimals = Tools.nullSafeNotNull(decimals, SQLDataType.INTEGER);
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Field<?> power;
        switch (ctx.family()) {
            case DERBY:
                Integer decimalsVal = (Integer) Tools.extractParamValue(this.decimals);
                if (decimalsVal != null) {
                    power = DSL.inline(BigDecimal.TEN.pow(decimalsVal.intValue(), MathContext.DECIMAL128));
                } else {
                    power = DSL.power(DSL.inline(BigDecimal.TEN), this.decimals);
                }
                ctx.visit(DSL.decode().when(this.value.sign().greaterOrEqual(DSL.zero()), Internal.idiv(Internal.imul(this.value, power).floor(), power)).otherwise(Internal.idiv(Internal.imul(this.value, power).ceil(), power)));
                return;
            case H2:
            case MARIADB:
            case MYSQL:
                ctx.visit(Names.N_TRUNCATE).sql('(').visit((Field<?>) this.value).sql(", ").visit((Field<?>) this.decimals).sql(')');
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(Tools.castIfNeeded((Field<?>) DSL.function(Names.N_TRUNC, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.castIfNeeded((Field<?>) this.value, (DataType) SQLDataType.NUMERIC), this.decimals}), this.value.getDataType()));
                return;
            default:
                ctx.visit(Names.N_TRUNC).sql('(').visit((Field<?>) this.value).sql(", ").visit((Field<?>) this.decimals).sql(')');
                return;
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
    public final QOM.Trunc<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Trunc<T> $arg2(Field<Integer> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<Integer>, ? extends QOM.Trunc<T>> $constructor() {
        return (a1, a2) -> {
            return new Trunc(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Trunc)) {
            return super.equals(that);
        }
        QOM.Trunc<?> o = (QOM.Trunc) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($decimals(), o.$decimals());
    }
}

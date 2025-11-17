package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Sum.class */
public final class Sum extends AbstractAggregateFunction<BigDecimal> implements QOM.Sum {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Sum(Field<? extends Number> field, boolean distinct) {
        super(distinct, Names.N_SUM, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        super.accept(ctx);
    }

    @Override // org.jooq.impl.QOM.Sum
    public final Field<? extends Number> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.Sum
    public final QOM.Sum $field(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($distinct()));
    }

    @Override // org.jooq.impl.QOM.Sum
    public final QOM.Sum $distinct(boolean newValue) {
        return $constructor().apply($field(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Field<? extends Number>, ? super Boolean, ? extends QOM.Sum> $constructor() {
        return (a1, a2) -> {
            return new Sum(a1, a2.booleanValue());
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Sum)) {
            return super.equals(that);
        }
        QOM.Sum o = (QOM.Sum) that;
        return StringUtils.equals($field(), o.$field()) && $distinct() == o.$distinct();
    }
}

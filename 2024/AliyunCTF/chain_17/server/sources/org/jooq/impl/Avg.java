package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Avg.class */
public final class Avg extends AbstractAggregateFunction<BigDecimal> implements QOM.Avg {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Avg(Field<? extends Number> field, boolean distinct) {
        super(distinct, Names.N_AVG, SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        super.accept(ctx);
    }

    @Override // org.jooq.impl.QOM.Avg
    public final Field<? extends Number> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.Avg
    public final QOM.Avg $field(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($distinct()));
    }

    @Override // org.jooq.impl.QOM.Avg
    public final QOM.Avg $distinct(boolean newValue) {
        return $constructor().apply($field(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Field<? extends Number>, ? super Boolean, ? extends QOM.Avg> $constructor() {
        return (a1, a2) -> {
            return new Avg(a1, a2.booleanValue());
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Avg)) {
            return super.equals(that);
        }
        QOM.Avg o = (QOM.Avg) that;
        return StringUtils.equals($field(), o.$field()) && $distinct() == o.$distinct();
    }
}

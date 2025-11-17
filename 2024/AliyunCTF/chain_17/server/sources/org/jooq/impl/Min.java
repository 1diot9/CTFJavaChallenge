package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Min.class */
public final class Min<T> extends AbstractAggregateFunction<T> implements QOM.Min<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Min(Field<T> field, boolean distinct) {
        super(distinct, Names.N_MIN, Tools.nullSafeDataType(field), (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.OTHER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        super.accept(ctx);
    }

    @Override // org.jooq.impl.QOM.Min
    public final Field<T> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.Min
    public final QOM.Min<T> $field(Field<T> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($distinct()));
    }

    @Override // org.jooq.impl.QOM.Min
    public final QOM.Min<T> $distinct(boolean newValue) {
        return $constructor().apply($field(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Field<T>, ? super Boolean, ? extends QOM.Min<T>> $constructor() {
        return (a1, a2) -> {
            return new Min(a1, a2.booleanValue());
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Min)) {
            return super.equals(that);
        }
        QOM.Min<?> o = (QOM.Min) that;
        return StringUtils.equals($field(), o.$field()) && $distinct() == o.$distinct();
    }
}

package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Count.class */
public final class Count extends AbstractAggregateFunction<Integer> implements QOM.Count {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Count(Field<?> field, boolean distinct) {
        super(distinct, Names.N_COUNT, SQLDataType.INTEGER, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.OTHER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        super.accept(ctx);
    }

    @Override // org.jooq.impl.QOM.Count
    public final Field<?> $field() {
        return getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.Count
    public final QOM.Count $field(Field<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($distinct()));
    }

    @Override // org.jooq.impl.QOM.Count
    public final QOM.Count $distinct(boolean newValue) {
        return $constructor().apply($field(), Boolean.valueOf(newValue));
    }

    public final Function2<? super Field<?>, ? super Boolean, ? extends QOM.Count> $constructor() {
        return (a1, a2) -> {
            return new Count(a1, a2.booleanValue());
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Count)) {
            return super.equals(that);
        }
        QOM.Count o = (QOM.Count) that;
        return StringUtils.equals($field(), o.$field()) && $distinct() == o.$distinct();
    }
}

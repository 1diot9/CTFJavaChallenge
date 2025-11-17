package org.jooq.impl;

import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.QuantifiedSelect;
import org.jooq.Record1;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LtQuantified.class */
public final class LtQuantified<T> extends AbstractCondition implements QOM.LtQuantified<T> {
    final Field<T> arg1;
    final QuantifiedSelect<? extends Record1<T>> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LtQuantified(Field<T> arg1, QuantifiedSelect<? extends Record1<T>> arg2) {
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER);
        this.arg2 = arg2;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        EqQuantified.acceptCompareCondition(ctx, this, this.arg1, Comparator.LESS, this.arg2);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QuantifiedSelect<? extends Record1<T>> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.LtQuantified<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.LtQuantified<T> $arg2(QuantifiedSelect<? extends Record1<T>> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super QuantifiedSelect<? extends Record1<T>>, ? extends QOM.LtQuantified<T>> $constructor() {
        return (a1, a2) -> {
            return new LtQuantified(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.LtQuantified)) {
            return super.equals(that);
        }
        QOM.LtQuantified<?> o = (QOM.LtQuantified) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

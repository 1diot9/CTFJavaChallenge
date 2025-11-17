package org.jooq.impl;

import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.Function2;
import org.jooq.Row;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowNe.class */
public final class RowNe<T extends Row> extends AbstractCondition implements QOM.RowNe<T> {
    final T arg1;
    final T arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowNe(T arg1, T arg2) {
        this.arg1 = ((AbstractRow) arg1).convertTo(arg2);
        this.arg2 = ((AbstractRow) arg2).convertTo(arg1);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        RowEq.acceptCompareCondition(ctx, this, this.arg1, Comparator.NOT_EQUALS, this.arg2);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final T $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final T $arg2() {
        return this.arg2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.RowNe<T> $arg1(T t) {
        return $constructor().apply(t, $arg2());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.RowNe<T> $arg2(T t) {
        return $constructor().apply($arg1(), t);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super T, ? super T, ? extends QOM.RowNe<T>> $constructor() {
        return (a1, a2) -> {
            return new RowNe(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RowNe)) {
            return super.equals(that);
        }
        QOM.RowNe<?> o = (QOM.RowNe) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

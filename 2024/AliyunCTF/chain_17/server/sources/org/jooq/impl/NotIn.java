package org.jooq.impl;

import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Record1;
import org.jooq.RowN;
import org.jooq.Select;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NotIn.class */
public final class NotIn<T> extends AbstractCondition implements QOM.NotIn<T> {
    final Field<T> arg1;
    final Select<? extends Record1<T>> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotIn(Field<T> arg1, Select<? extends Record1<T>> arg2) {
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER);
        this.arg2 = arg2;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Eq.acceptCompareCondition(ctx, this, this.arg1, Comparator.NOT_IN, new ScalarSubquery(this.arg2, this.arg1.getDataType(), true), (v0, v1) -> {
            return v0.notIn(v1);
        }, (rec$, xva$0) -> {
            return ((RowN) rec$).notIn(xva$0);
        }, (c, a1, a2) -> {
            return c.visit((Field<?>) a1).sql(' ').visit(Keywords.K_NOT_IN).sql(' ').visit((Field<?>) a2);
        });
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Select<? extends Record1<T>> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.NotIn<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.NotIn<T> $arg2(Select<? extends Record1<T>> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Select<? extends Record1<T>>, ? extends QOM.NotIn<T>> $constructor() {
        return (a1, a2) -> {
            return new NotIn(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.NotIn)) {
            return super.equals(that);
        }
        QOM.NotIn<?> o = (QOM.NotIn) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

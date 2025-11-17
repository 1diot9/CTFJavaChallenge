package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Gt.class */
public final class Gt<T> extends AbstractCondition implements QOM.Gt<T> {
    final Field<T> arg1;
    final Field<T> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Gt(Field<T> arg1, Field<T> arg2) {
        this.arg1 = Tools.nullableIf(false, Tools.nullSafe((Field) arg1, (DataType<?>) arg2.getDataType()));
        this.arg2 = Tools.nullableIf(false, Tools.nullSafe((Field) arg2, (DataType<?>) arg1.getDataType()));
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Eq.acceptCompareCondition(ctx, this, this.arg1, Comparator.GREATER, this.arg2, (v0, v1) -> {
            return v0.gt(v1);
        }, (v0, v1) -> {
            return v0.gt(v1);
        }, (c, a1, a2) -> {
            return c.visit((Field<?>) a1).sql(" > ").visit((Field<?>) a2);
        });
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return Eq.CLAUSES;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Gt<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Gt<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.Gt<T>> $constructor() {
        return (a1, a2) -> {
            return new Gt(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Gt)) {
            return super.equals(that);
        }
        QOM.Gt<?> o = (QOM.Gt) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

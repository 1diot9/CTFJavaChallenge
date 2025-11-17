package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayRemove.class */
public final class ArrayRemove<T> extends AbstractField<T[]> implements QOM.ArrayRemove<T> {
    final Field<T[]> arg1;
    final Field<T> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayRemove(Field<T[]> arg1, Field<T> arg2) {
        super(Names.N_ARRAY_REMOVE, Tools.allNotNull(Tools.dataType(SQLDataType.OTHER.array(), arg1, false), arg1, arg2));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER.array());
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
                Field<T> x = DSL.field(DSL.name("x"), this.arg2.getDataType());
                Field<Long> o = DSL.field(DSL.name("o"), SQLDataType.BIGINT);
                ctx.visit(DSL.field(DSL.select(DSL.arrayAgg(x).orderBy(o)).from(DSL.unnest((Field<?>) this.arg1).withOrdinality().as("t", "x", "o")).where(x.ne((Field) this.arg2))));
                return;
            default:
                ctx.visit(DSL.function(Names.N_ARRAY_REMOVE, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T[]> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayRemove<T> $arg1(Field<T[]> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayRemove<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T[]>, ? super Field<T>, ? extends QOM.ArrayRemove<T>> $constructor() {
        return (a1, a2) -> {
            return new ArrayRemove(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ArrayRemove)) {
            return super.equals(that);
        }
        QOM.ArrayRemove<?> o = (QOM.ArrayRemove) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

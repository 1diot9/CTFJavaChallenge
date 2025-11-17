package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayOverlap.class */
public final class ArrayOverlap<T> extends AbstractCondition implements QOM.ArrayOverlap<T> {
    final Field<T[]> arg1;
    final Field<T[]> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayOverlap(Field<T[]> arg1, Field<T[]> arg2) {
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER.array());
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.OTHER.array());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                return false;
            case HSQLDB:
                return false;
            case TRINO:
                return true;
            default:
                return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                ctx.visit(DSL.exists(DSL.select(DSL.asterisk()).from(DSL.unnest((Field<?>) this.arg1)).intersect((Select<? extends Record>) DSL.select(DSL.asterisk()).from(DSL.unnest((Field<?>) this.arg2)))));
                return;
            case HSQLDB:
                ctx.visit(DSL.exists(DSL.select(DSL.asterisk()).from(DSL.unnest((Field<?>) this.arg1)).intersectAll((Select<? extends Record>) DSL.select(DSL.asterisk()).from(DSL.unnest((Field<?>) this.arg2)))));
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_ARRAYS_OVERLAP, SQLDataType.BOOLEAN, (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            default:
                ctx.sql('(').visit(this.arg1).sql(" && ").visit((Field<?>) this.arg2).sql(')');
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T[]> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T[]> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayOverlap<T> $arg1(Field<T[]> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayOverlap<T> $arg2(Field<T[]> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T[]>, ? super Field<T[]>, ? extends QOM.ArrayOverlap<T>> $constructor() {
        return (a1, a2) -> {
            return new ArrayOverlap(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ArrayOverlap)) {
            return super.equals(that);
        }
        QOM.ArrayOverlap<?> o = (QOM.ArrayOverlap) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

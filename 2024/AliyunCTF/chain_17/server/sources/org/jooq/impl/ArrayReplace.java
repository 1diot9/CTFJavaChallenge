package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayReplace.class */
public final class ArrayReplace<T> extends AbstractField<T[]> implements QOM.ArrayReplace<T> {
    final Field<T[]> arg1;
    final Field<T> arg2;
    final Field<T> arg3;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayReplace(Field<T[]> arg1, Field<T> arg2, Field<T> arg3) {
        super(Names.N_ARRAY_REPLACE, Tools.allNotNull(Tools.dataType(SQLDataType.OTHER.array(), arg1, false), arg1, arg2, arg3));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER.array());
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.OTHER);
        this.arg3 = Tools.nullSafeNotNull(arg3, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
                return false;
            case TRINO:
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
                ctx.visit(DSL.field(DSL.select(DSL.arrayAgg(DSL.when(x.isNotDistinctFrom((Field) this.arg2), (Field) this.arg3).else_((Field) x)).orderBy(o)).from(DSL.unnest((Field<?>) this.arg1).withOrdinality().as("t", "x", "o"))));
                return;
            case TRINO:
                Field<T> e = DSL.field(DSL.raw("e"), this.arg2.getDataType());
                ctx.visit(DSL.function(Names.N_TRANSFORM, this.arg1.getDataType(), (Field<?>[]) new Field[]{this.arg1, DSL.field("e -> {0}", DSL.when(e.isNotDistinctFrom((Field) this.arg2), (Field) this.arg3).else_((Field) e))}));
                return;
            default:
                ctx.visit(DSL.function(Names.N_ARRAY_REPLACE, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2, this.arg3}));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T[]> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg3() {
        return this.arg3;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.ArrayReplace<T> $arg1(Field<T[]> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.ArrayReplace<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.ArrayReplace<T> $arg3(Field<T> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<T[]>, ? super Field<T>, ? super Field<T>, ? extends QOM.ArrayReplace<T>> $constructor() {
        return (a1, a2, a3) -> {
            return new ArrayReplace(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ArrayReplace)) {
            return super.equals(that);
        }
        QOM.ArrayReplace<?> o = (QOM.ArrayReplace) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2()) && StringUtils.equals($arg3(), o.$arg3());
    }
}

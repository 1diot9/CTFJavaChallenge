package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayPrepend.class */
public final class ArrayPrepend<T> extends AbstractField<T[]> implements QOM.ArrayPrepend<T> {
    final Field<T> arg1;
    final Field<T[]> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayPrepend(Field<T> arg1, Field<T[]> arg2) {
        super(Names.N_ARRAY_PREPEND, Tools.allNotNull(Tools.dataType(SQLDataType.OTHER.array(), arg2, false), arg1, arg2));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.OTHER);
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.OTHER.array());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
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
            case TRINO:
                ctx.visit(DSL.arrayConcat(DSL.array(this.arg1), this.arg2));
                return;
            default:
                ctx.visit(DSL.function(Names.N_ARRAY_PREPEND, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T[]> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayPrepend<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayPrepend<T> $arg2(Field<T[]> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T[]>, ? extends QOM.ArrayPrepend<T>> $constructor() {
        return (a1, a2) -> {
            return new ArrayPrepend(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ArrayPrepend)) {
            return super.equals(that);
        }
        QOM.ArrayPrepend<?> o = (QOM.ArrayPrepend) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

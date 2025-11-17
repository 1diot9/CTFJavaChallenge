package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.TableField;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayGet.class */
public final class ArrayGet<T> extends AbstractField<T> implements QOM.ArrayGet<T> {
    final Field<T[]> array;
    final Field<Integer> index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayGet(Field<T[]> array, Field<Integer> index) {
        super(Names.N_ARRAY_GET, Tools.allNotNull((DataType) StringUtils.defaultIfNull(array.getDataType().getArrayComponentDataType(), SQLDataType.OTHER), array, index));
        this.array = Tools.nullSafeNotNull(array, SQLDataType.OTHER.array());
        this.index = Tools.nullSafeNotNull(index, SQLDataType.INTEGER);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case HSQLDB:
                if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_STORE_ASSIGNMENT))) {
                    ctx.visit((Field<?>) new Standard());
                    return;
                } else {
                    ctx.visit((Field<?>) DSL.when(DSL.cardinality(this.array).ge(this.index), (Field) new Standard()));
                    return;
                }
            case TRINO:
                ctx.visit(DSL.function(Names.N_ELEMENT_AT, getDataType(), (Field<?>[]) new Field[]{this.array, this.index}));
                return;
            default:
                ctx.visit((Field<?>) new Standard());
                return;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayGet$Standard.class */
    private class Standard extends AbstractField<T> implements QOM.UTransient {
        Standard() {
            super(ArrayGet.this.getQualifiedName(), ArrayGet.this.getDataType());
        }

        /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
        @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
        public void accept(Context<?> ctx) {
            if ((ArrayGet.this.array instanceof TableField) || Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_STORE_ASSIGNMENT))) {
                ctx.visit((Field<?>) ArrayGet.this.array).sql('[').visit((Field<?>) ArrayGet.this.index).sql(']');
            } else {
                ctx.sql('(').visit(ArrayGet.this.array).sql(')').sql('[').visit((Field<?>) ArrayGet.this.index).sql(']');
            }
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T[]> $arg1() {
        return this.array;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<Integer> $arg2() {
        return this.index;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayGet<T> $arg1(Field<T[]> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ArrayGet<T> $arg2(Field<Integer> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T[]>, ? super Field<Integer>, ? extends QOM.ArrayGet<T>> $constructor() {
        return (a1, a2) -> {
            return new ArrayGet(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ArrayGet)) {
            return super.equals(that);
        }
        QOM.ArrayGet<?> o = (QOM.ArrayGet) that;
        return StringUtils.equals($array(), o.$array()) && StringUtils.equals($index(), o.$index());
    }
}

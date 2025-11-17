package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitGet.class */
public final class BitGet<T extends Number> extends AbstractField<T> implements QOM.BitGet<T> {
    final Field<T> value;
    final Field<? extends Number> bit;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitGet(Field<T> value, Field<? extends Number> bit) {
        super(Names.N_BIT_GET, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), value, bit));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.bit = Tools.nullSafeNotNull(bit, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                return false;
            case CUBRID:
            case DUCKDB:
            case FIREBIRD:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                ctx.visit((Field<?>) DSL.case_(DSL.function(Names.N_BITGET, SQLDataType.BOOLEAN, (Field<?>[]) new Field[]{this.value, this.bit})).when((Field) DSL.trueCondition(), (Field) DSL.inline(1)).when((Field) DSL.falseCondition(), (Field) DSL.inline(0)));
                return;
            case CUBRID:
            case DUCKDB:
            case FIREBIRD:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                ctx.visit(this.value.bitAnd(DSL.one().shl(this.bit)).shr(this.bit));
                return;
            default:
                ctx.visit(DSL.function(Names.N_BIT_GET, getDataType(), (Field<?>[]) new Field[]{this.value, this.bit}));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.bit;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.BitGet<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.BitGet<T> $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<? extends Number>, ? extends QOM.BitGet<T>> $constructor() {
        return (a1, a2) -> {
            return new BitGet(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.BitGet)) {
            return super.equals(that);
        }
        QOM.BitGet<?> o = (QOM.BitGet) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($bit(), o.$bit());
    }
}

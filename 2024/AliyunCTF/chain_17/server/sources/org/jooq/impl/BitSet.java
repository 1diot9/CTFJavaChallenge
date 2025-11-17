package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitSet.class */
public final class BitSet<T extends Number> extends AbstractField<T> implements QOM.BitSet<T> {
    final Field<T> value;
    final Field<? extends Number> bit;
    final Field<T> newValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitSet(Field<T> value, Field<? extends Number> bit) {
        super(Names.N_BIT_SET, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), value, bit));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.bit = Tools.nullSafeNotNull(bit, SQLDataType.INTEGER);
        this.newValue = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitSet(Field<T> value, Field<? extends Number> bit, Field<T> newValue) {
        super(Names.N_BIT_SET, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), value, bit, newValue));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.bit = Tools.nullSafeNotNull(bit, SQLDataType.INTEGER);
        this.newValue = Tools.nullSafeNotNull(newValue, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DUCKDB:
            case FIREBIRD:
            case H2:
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
            case CUBRID:
            case DUCKDB:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case TRINO:
            case YUGABYTEDB:
                if (this.newValue == null) {
                    ctx.visit(this.value.bitOr(DSL.one().shl(this.bit)));
                    return;
                } else {
                    ctx.visit((Field<?>) DSL.case_((Field) this.newValue).when((Field) DSL.zero(), (Field) this.value.bitAnd(DSL.one().shl(this.bit).bitNot())).when((Field) DSL.one(), (Field) this.value.bitOr(DSL.one().shl(this.bit))));
                    return;
                }
            default:
                if (this.newValue != null) {
                    ctx.visit(DSL.function(Names.N_BIT_SET, getDataType(), (Field<?>[]) new Field[]{this.value, this.bit, this.newValue}));
                    return;
                } else {
                    ctx.visit(DSL.function(Names.N_BIT_SET, getDataType(), (Field<?>[]) new Field[]{this.value, this.bit}));
                    return;
                }
        }
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg2() {
        return this.bit;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<T> $arg3() {
        return this.newValue;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.BitSet<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.BitSet<T> $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.BitSet<T> $arg3(Field<T> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<T>, ? super Field<? extends Number>, ? super Field<T>, ? extends QOM.BitSet<T>> $constructor() {
        return (a1, a2, a3) -> {
            return new BitSet(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.BitSet)) {
            return super.equals(that);
        }
        QOM.BitSet<?> o = (QOM.BitSet) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($bit(), o.$bit()) && StringUtils.equals($newValue(), o.$newValue());
    }
}

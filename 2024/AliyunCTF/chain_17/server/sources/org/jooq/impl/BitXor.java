package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQL;
import org.jooq.impl.Expression;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitXor.class */
public final class BitXor<T extends Number> extends AbstractField<T> implements QOM.BitXor<T> {
    final Field<T> arg1;
    final Field<T> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitXor(Field<T> arg1, Field<T> arg2) {
        super(Names.N_BIT_XOR, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, arg1, false), arg1, arg2));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.INTEGER);
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
                return true;
            case FIREBIRD:
                return true;
            case TRINO:
                return true;
            case DUCKDB:
                return true;
            case SQLITE:
                return false;
            default:
                return false;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
                ctx.visit(DSL.function(Names.N_BITXOR, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_BIN_XOR, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_BITWISE_XOR, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            case DUCKDB:
                ctx.visit(DSL.function(Names.N_XOR, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            case SQLITE:
                ctx.visit(DSL.bitAnd(DSL.bitNot(DSL.bitAnd(this.arg1, this.arg2)), DSL.bitOr(this.arg1, this.arg2)));
                return;
            default:
                ctx.sql('(');
                SQL op = Expression.HASH_OP_FOR_BIT_XOR.contains(ctx.dialect()) ? Operators.OP_NUM : Operators.OP_HAT;
                Expression.acceptAssociative(ctx, this, q -> {
                    return new Expression.Expr(q.arg1, op, q.arg2);
                }, c -> {
                    c.sql(' ');
                });
                ctx.sql(')');
                return;
        }
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
    public final QOM.BitXor<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.BitXor<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.BitXor<T>> $constructor() {
        return (a1, a2) -> {
            return new BitXor(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.BitXor)) {
            return super.equals(that);
        }
        QOM.BitXor<?> o = (QOM.BitXor) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

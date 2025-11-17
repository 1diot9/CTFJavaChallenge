package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.Expression;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitAnd.class */
public final class BitAnd<T extends Number> extends AbstractField<T> implements QOM.BitAnd<T> {
    final Field<T> arg1;
    final Field<T> arg2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitAnd(Field<T> arg1, Field<T> arg2) {
        super(Names.N_BIT_AND, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, arg1, false), arg1, arg2));
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
            default:
                return false;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
            case HSQLDB:
                ctx.visit(DSL.function(Names.N_BITAND, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_BIN_AND, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_BITWISE_AND, getDataType(), (Field<?>[]) new Field[]{this.arg1, this.arg2}));
                return;
            default:
                ctx.sql('(');
                Expression.acceptAssociative(ctx, this, q -> {
                    return new Expression.Expr(q.arg1, Operators.OP_AMP, q.arg2);
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
    public final QOM.BitAnd<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.BitAnd<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.BitAnd<T>> $constructor() {
        return (a1, a2) -> {
            return new BitAnd(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.BitAnd)) {
            return super.equals(that);
        }
        QOM.BitAnd<?> o = (QOM.BitAnd) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

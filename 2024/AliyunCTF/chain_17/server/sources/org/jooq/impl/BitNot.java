package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitNot.class */
public final class BitNot<T extends Number> extends AbstractField<T> implements QOM.BitNot<T> {
    final Field<T> arg1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitNot(Field<T> arg1) {
        super(Names.N_BIT_NOT, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, arg1, false), (Field<?>) arg1));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case HSQLDB:
                return false;
            case H2:
                return true;
            case FIREBIRD:
                return true;
            case TRINO:
                return true;
            default:
                return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case HSQLDB:
                ctx.visit(Internal.isub(Internal.isub(DSL.zero(), this.arg1), DSL.one()));
                return;
            case H2:
                ctx.visit(DSL.function(Names.N_BITNOT, (DataType) getDataType(), (Field<?>) this.arg1));
                return;
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_BIN_NOT, (DataType) getDataType(), (Field<?>) this.arg1));
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_BITWISE_NOT, (DataType) getDataType(), (Field<?>) this.arg1));
                return;
            default:
                if ((this.arg1 instanceof AbstractField) && ((AbstractField) this.arg1).parenthesised(ctx)) {
                    ctx.sql('~').visit(this.arg1);
                    return;
                } else {
                    ctx.sql("~(").visit(this.arg1).sql(')');
                    return;
                }
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.BitNot<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.BitNot<T>> $constructor() {
        return a1 -> {
            return new BitNot(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.BitNot) {
            QOM.BitNot<?> o = (QOM.BitNot) that;
            return StringUtils.equals($arg1(), o.$arg1());
        }
        return super.equals(that);
    }
}

package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Log10.class */
public final class Log10 extends AbstractField<BigDecimal> implements QOM.Log10 {
    final Field<? extends Number> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Log10(Field<? extends Number> value) {
        super(Names.N_LOG10, Tools.allNotNull(SQLDataType.NUMERIC, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case YUGABYTEDB:
                return false;
            case DUCKDB:
            case POSTGRES:
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case YUGABYTEDB:
                ctx.visit((Field<?>) DSL.log(this.value, DSL.inline(10)));
                return;
            case DUCKDB:
            case POSTGRES:
            case SQLITE:
                ctx.visit(Names.N_LOG10).sql('(').visit((Field<?>) this.value).sql(')');
                return;
            default:
                ctx.visit(DSL.function(Names.N_LOG10, getDataType(), this.value));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Log10 $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Log10> $constructor() {
        return a1 -> {
            return new Log10(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Log10) {
            QOM.Log10 o = (QOM.Log10) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

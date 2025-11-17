package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Ceil.class */
public final class Ceil<T extends Number> extends AbstractField<T> implements QOM.Ceil<T> {
    final Field<T> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Ceil(Field<T> value) {
        super(Names.N_CEIL, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), (Field<?>) value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                return false;
            case H2:
                return true;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                ctx.visit(DSL.function(Names.N_CEIL, (DataType) getDataType(), (Field<?>) this.value));
                return;
            case H2:
                ctx.visit(DSL.function(Names.N_CEILING, (DataType) getDataType(), (Field<?>) this.value));
                return;
            default:
                ctx.visit(DSL.function(Names.N_CEIL, (DataType) getDataType(), (Field<?>) this.value));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Ceil<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.Ceil<T>> $constructor() {
        return a1 -> {
            return new Ceil(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Ceil) {
            QOM.Ceil<?> o = (QOM.Ceil) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

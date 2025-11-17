package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Sign.class */
public final class Sign extends AbstractField<Integer> implements QOM.Sign {
    final Field<? extends Number> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Sign(Field<? extends Number> value) {
        super(Names.N_SIGN, Tools.allNotNull(SQLDataType.INTEGER, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                ctx.visit((Field<?>) DSL.when(this.value.gt((Field<? extends Number>) DSL.zero()), (Field) DSL.inline(1)).when(this.value.lt((Field<? extends Number>) DSL.zero()), (Field) DSL.inline(-1)).when(this.value.eq((Field<? extends Number>) DSL.zero()), (Field) DSL.inline(0)));
                return;
            default:
                ctx.visit(DSL.function(Names.N_SIGN, getDataType(), this.value));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Sign $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Sign> $constructor() {
        return a1 -> {
            return new Sign(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Sign) {
            QOM.Sign o = (QOM.Sign) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

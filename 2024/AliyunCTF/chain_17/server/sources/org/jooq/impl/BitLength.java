package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitLength.class */
public final class BitLength extends AbstractField<Integer> implements QOM.BitLength {
    final Field<String> string;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitLength(Field<String> string) {
        super(Names.N_BIT_LENGTH, Tools.allNotNull(SQLDataType.INTEGER, string));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case SQLITE:
            case TRINO:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case SQLITE:
            case TRINO:
                ctx.visit(Internal.imul(DSL.inline(8), DSL.function(Names.N_LENGTH, getDataType(), this.string)));
                return;
            default:
                ctx.visit(DSL.function(Names.N_BIT_LENGTH, getDataType(), this.string));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<String> $arg1() {
        return this.string;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.BitLength $arg1(Field<String> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<String>, ? extends QOM.BitLength> $constructor() {
        return a1 -> {
            return new BitLength(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.BitLength) {
            QOM.BitLength o = (QOM.BitLength) that;
            return StringUtils.equals($string(), o.$string());
        }
        return super.equals(that);
    }
}

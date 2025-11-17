package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Ascii.class */
public final class Ascii extends AbstractField<Integer> implements QOM.Ascii {
    final Field<String> string;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Ascii(Field<String> string) {
        super(Names.N_ASCII, Tools.allNotNull(SQLDataType.INTEGER, string));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_ASCII_VAL, getDataType(), this.string));
                return;
            default:
                ctx.visit(DSL.function(Names.N_ASCII, getDataType(), this.string));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<String> $arg1() {
        return this.string;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Ascii $arg1(Field<String> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<String>, ? extends QOM.Ascii> $constructor() {
        return a1 -> {
            return new Ascii(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Ascii) {
            QOM.Ascii o = (QOM.Ascii) that;
            return StringUtils.equals($string(), o.$string());
        }
        return super.equals(that);
    }
}

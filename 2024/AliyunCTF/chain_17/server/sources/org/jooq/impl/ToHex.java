package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ToHex.class */
public final class ToHex extends AbstractField<String> implements QOM.ToHex {
    final Field<? extends Number> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToHex(Field<? extends Number> value) {
        super(Names.N_TO_HEX, Tools.allNotNull(SQLDataType.VARCHAR, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                return true;
            case H2:
                return false;
            case SQLITE:
                return false;
            case TRINO:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(DSL.function(Names.N_HEX, getDataType(), this.value));
                return;
            case H2:
                ctx.visit((Field<?>) DSL.trim(DSL.toChar((Field<?>) this.value, (Field<String>) DSL.inline("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"))));
                return;
            case SQLITE:
                ctx.visit(DSL.function(Names.N_PRINTF, getDataType(), (Field<?>[]) new Field[]{DSL.inline("%X"), this.value}));
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_TO_BASE, getDataType(), (Field<?>[]) new Field[]{this.value, DSL.inline(16)}));
                return;
            default:
                ctx.visit(DSL.function(Names.N_TO_HEX, getDataType(), this.value));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.ToHex $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.ToHex> $constructor() {
        return a1 -> {
            return new ToHex(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.ToHex) {
            QOM.ToHex o = (QOM.ToHex) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

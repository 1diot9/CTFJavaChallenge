package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Chr.class */
public final class Chr extends AbstractField<String> implements QOM.Chr {
    final Field<? extends Number> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Chr(Field<? extends Number> value) {
        super(Names.N_CHR, Tools.allNotNull(SQLDataType.VARCHAR, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case HSQLDB:
            case MARIADB:
            case MYSQL:
            case SQLITE:
            case YUGABYTEDB:
                ctx.visit(DSL.function(Names.N_CHAR, getDataType(), this.value));
                return;
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_ASCII_CHAR, getDataType(), this.value));
                return;
            default:
                ctx.visit(DSL.function(Names.N_CHR, getDataType(), this.value));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Chr $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Chr> $constructor() {
        return a1 -> {
            return new Chr(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Chr) {
            QOM.Chr o = (QOM.Chr) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

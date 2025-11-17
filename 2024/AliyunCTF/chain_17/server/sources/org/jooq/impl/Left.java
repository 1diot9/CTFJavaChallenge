package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Left.class */
public final class Left extends AbstractField<String> implements QOM.Left {
    final Field<String> string;
    final Field<? extends Number> length;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Left(Field<String> string, Field<? extends Number> length) {
        super(Names.N_LEFT, Tools.allNotNull(SQLDataType.VARCHAR, string, length));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.length = Tools.nullSafeNotNull(length, SQLDataType.INTEGER);
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
                ctx.visit((Field<?>) DSL.substring(this.string, DSL.inline(1), this.length));
                return;
            default:
                ctx.visit(DSL.function(Names.N_LEFT, getDataType(), (Field<?>[]) new Field[]{this.string, this.length}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg1() {
        return this.string;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.length;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Left $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Left $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<String>, ? super Field<? extends Number>, ? extends QOM.Left> $constructor() {
        return (a1, a2) -> {
            return new Left(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Left)) {
            return super.equals(that);
        }
        QOM.Left o = (QOM.Left) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($length(), o.$length());
    }
}

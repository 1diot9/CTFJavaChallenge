package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Repeat.class */
public final class Repeat extends AbstractField<String> implements QOM.Repeat {
    final Field<String> string;
    final Field<? extends Number> count;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Repeat(Field<String> string, Field<? extends Number> count) {
        super(Names.N_REPEAT, Tools.allNotNull(SQLDataType.VARCHAR, string, count));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.count = Tools.nullSafeNotNull(count, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
            case TRINO:
                return false;
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
            case FIREBIRD:
            case TRINO:
                ctx.visit((Field<?>) DSL.rpad(this.string, (Field<? extends Number>) Internal.imul(DSL.length(this.string), this.count), this.string));
                return;
            case SQLITE:
                ctx.visit(Names.N_REPLACE).sql('(').visit(Names.N_HEX).sql('(').visit(Names.N_ZEROBLOB).sql('(').visit((Field<?>) this.count).sql(")), '00', ").visit((Field<?>) this.string).sql(')');
                return;
            default:
                ctx.visit(DSL.function(Names.N_REPEAT, getDataType(), (Field<?>[]) new Field[]{this.string, this.count}));
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
        return this.count;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Repeat $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Repeat $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<String>, ? super Field<? extends Number>, ? extends QOM.Repeat> $constructor() {
        return (a1, a2) -> {
            return new Repeat(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Repeat)) {
            return super.equals(that);
        }
        QOM.Repeat o = (QOM.Repeat) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($count(), o.$count());
    }
}

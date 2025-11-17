package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Shr.class */
public final class Shr<T extends Number> extends AbstractField<T> implements QOM.Shr<T> {
    final Field<T> value;
    final Field<? extends Number> count;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Shr(Field<T> value, Field<? extends Number> count) {
        super(Names.N_SHR, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), value, count));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
        this.count = Tools.nullSafeNotNull(count, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                return true;
            case H2:
                return true;
            case TRINO:
                return true;
            case HSQLDB:
                return false;
            default:
                return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_BIN_SHR, getDataType(), (Field<?>[]) new Field[]{this.value, this.count}));
                return;
            case H2:
                ctx.visit(DSL.function(Names.N_RSHIFT, getDataType(), (Field<?>[]) new Field[]{this.value, this.count}));
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_BITWISE_RIGHT_SHIFT, getDataType(), (Field<?>[]) new Field[]{this.value, this.count}));
                return;
            case HSQLDB:
                ctx.visit(Internal.idiv(this.value, Tools.castIfNeeded(DSL.power(DSL.two(), this.count), this.value)));
                return;
            default:
                ctx.sql('(').visit(this.value).sql(" >> ").visit((Field<?>) this.count).sql(')');
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.count;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Shr<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Shr<T> $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<? extends Number>, ? extends QOM.Shr<T>> $constructor() {
        return (a1, a2) -> {
            return new Shr(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Shr)) {
            return super.equals(that);
        }
        QOM.Shr<?> o = (QOM.Shr) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($count(), o.$count());
    }
}

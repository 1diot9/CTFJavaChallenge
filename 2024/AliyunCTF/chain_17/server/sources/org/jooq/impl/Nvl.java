package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Nvl.class */
public final class Nvl<T> extends AbstractField<T> implements QOM.Nvl<T> {
    final Field<T> value;
    final Field<T> defaultValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Nvl(Field<T> value, Field<T> defaultValue) {
        super(Names.N_NVL, Tools.anyNotNull(Tools.dataType(value), value, defaultValue));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.OTHER);
        this.defaultValue = Tools.nullSafeNotNull(defaultValue, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                return false;
            case CUBRID:
            case DUCKDB:
            case FIREBIRD:
            case IGNITE:
            case POSTGRES:
            case TRINO:
            case YUGABYTEDB:
                return true;
            case MARIADB:
            case MYSQL:
            case SQLITE:
                return true;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                ctx.visit(DSL.coalesce((Field) this.value, (Field<?>[]) new Field[]{this.defaultValue}));
                return;
            case CUBRID:
            case DUCKDB:
            case FIREBIRD:
            case IGNITE:
            case POSTGRES:
            case TRINO:
            case YUGABYTEDB:
                ctx.visit(DSL.function(Names.N_COALESCE, getDataType(), (Field<?>[]) new Field[]{this.value, this.defaultValue}));
                return;
            case MARIADB:
            case MYSQL:
            case SQLITE:
                ctx.visit(DSL.function(Names.N_IFNULL, getDataType(), (Field<?>[]) new Field[]{this.value, this.defaultValue}));
                return;
            default:
                ctx.visit(DSL.function(Names.N_NVL, getDataType(), (Field<?>[]) new Field[]{this.value, this.defaultValue}));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.defaultValue;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Nvl<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Nvl<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.Nvl<T>> $constructor() {
        return (a1, a2) -> {
            return new Nvl(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Nvl)) {
            return super.equals(that);
        }
        QOM.Nvl<?> o = (QOM.Nvl) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($defaultValue(), o.$defaultValue());
    }
}

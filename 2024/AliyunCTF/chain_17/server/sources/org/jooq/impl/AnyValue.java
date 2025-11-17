package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AnyValue.class */
public final class AnyValue<T> extends AbstractAggregateFunction<T> implements QOM.AnyValue<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AnyValue(Field<T> field) {
        super(false, Names.N_ANY_VALUE, Tools.nullSafeDataType(field), (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.OTHER)});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAggregateFunction
    public void acceptFunctionName(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case FIREBIRD:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                ctx.visit(Names.N_MIN);
                return;
            case TRINO:
                ctx.visit(Names.N_ARBITRARY);
                return;
            default:
                super.acceptFunctionName(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.AnyValue
    public final Field<T> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.AnyValue
    public final QOM.AnyValue<T> $field(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Field<T>, ? extends QOM.AnyValue<T>> $constructor() {
        return a1 -> {
            return new AnyValue(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.AnyValue) {
            QOM.AnyValue<?> o = (QOM.AnyValue) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

package org.jooq.impl;

import java.lang.Number;
import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitXNorAgg.class */
public final class BitXNorAgg<T extends Number> extends AbstractAggregateFunction<T> implements QOM.BitXNorAgg<T> {
    public static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitXNorAgg(Field<T> value) {
        super(false, Names.N_BIT_XNOR_AGG, Tools.nullSafeDataType(value), (Field<?>[]) new Field[]{Tools.nullSafeNotNull(value, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            ctx.visit(DSL.bitNot(fo(DSL.bitXorAgg(getArguments().get(0)))));
        } else {
            super.accept(ctx);
        }
    }

    @Override // org.jooq.impl.QOM.BitXNorAgg
    public final Field<T> $value() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.BitXNorAgg
    public final QOM.BitXNorAgg<T> $value(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Field<T>, ? extends QOM.BitXNorAgg<T>> $constructor() {
        return a1 -> {
            return new BitXNorAgg(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.BitXNorAgg) {
            QOM.BitXNorAgg<?> o = (QOM.BitXNorAgg) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

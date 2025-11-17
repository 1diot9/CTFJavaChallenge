package org.jooq.impl;

import java.lang.Number;
import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitNandAgg.class */
public final class BitNandAgg<T extends Number> extends AbstractAggregateFunction<T> implements QOM.BitNandAgg<T> {
    public static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitNandAgg(Field<T> value) {
        super(false, Names.N_BIT_NAND_AGG, Tools.nullSafeDataType(value), (Field<?>[]) new Field[]{Tools.nullSafeNotNull(value, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            ctx.visit(DSL.bitNot(fo(DSL.bitAndAgg(getArguments().get(0)))));
        } else {
            super.accept(ctx);
        }
    }

    @Override // org.jooq.impl.QOM.BitNandAgg
    public final Field<T> $value() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.BitNandAgg
    public final QOM.BitNandAgg<T> $value(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Field<T>, ? extends QOM.BitNandAgg<T>> $constructor() {
        return a1 -> {
            return new BitNandAgg(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.BitNandAgg) {
            QOM.BitNandAgg<?> o = (QOM.BitNandAgg) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

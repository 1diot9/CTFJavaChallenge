package org.jooq.impl;

import java.math.BigDecimal;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegrCount.class */
public final class RegrCount extends AbstractAggregateFunction<BigDecimal> implements QOM.RegrCount {
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegrCount(Field<? extends Number> y, Field<? extends Number> x) {
        super(false, Names.N_REGR_COUNT, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(y, SQLDataType.INTEGER), Tools.nullSafeNotNull(x, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            acceptEmulation(ctx);
        } else {
            super.accept(ctx);
        }
    }

    private final void acceptEmulation(Context<?> context) {
        context.visit((Field<?>) fo(DSL.count(getArguments().get(0).plus(getArguments().get(1)))));
    }

    @Override // org.jooq.impl.QOM.RegrCount
    public final Field<? extends Number> $y() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.RegrCount
    public final Field<? extends Number> $x() {
        return (Field) getArguments().get(1);
    }

    @Override // org.jooq.impl.QOM.RegrCount
    public final QOM.RegrCount $y(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $x());
    }

    @Override // org.jooq.impl.QOM.RegrCount
    public final QOM.RegrCount $x(Field<? extends Number> newValue) {
        return $constructor().apply($y(), newValue);
    }

    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.RegrCount> $constructor() {
        return (a1, a2) -> {
            return new RegrCount(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RegrCount)) {
            return super.equals(that);
        }
        QOM.RegrCount o = (QOM.RegrCount) that;
        return StringUtils.equals($y(), o.$y()) && StringUtils.equals($x(), o.$x());
    }
}

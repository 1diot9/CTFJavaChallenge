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
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegrIntercept.class */
public final class RegrIntercept extends AbstractAggregateFunction<BigDecimal> implements QOM.RegrIntercept {
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegrIntercept(Field<? extends Number> y, Field<? extends Number> x) {
        super(false, Names.N_REGR_INTERCEPT, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(y, SQLDataType.INTEGER), Tools.nullSafeNotNull(x, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            acceptEmulation(ctx);
        } else {
            super.accept(ctx);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void acceptEmulation(Context<?> ctx) {
        Field<?> field = getArguments().get(0);
        Field<?> field2 = getArguments().get(1);
        ctx.visit(fo(DSL.regrAvgY(field, field2)).minus((Field<?>) fo(DSL.regrSlope(field, field2)).times((Field<? extends Number>) fo(DSL.regrAvgX(field, field2)))));
    }

    @Override // org.jooq.impl.QOM.RegrIntercept
    public final Field<? extends Number> $y() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.RegrIntercept
    public final Field<? extends Number> $x() {
        return (Field) getArguments().get(1);
    }

    @Override // org.jooq.impl.QOM.RegrIntercept
    public final QOM.RegrIntercept $y(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $x());
    }

    @Override // org.jooq.impl.QOM.RegrIntercept
    public final QOM.RegrIntercept $x(Field<? extends Number> newValue) {
        return $constructor().apply($y(), newValue);
    }

    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.RegrIntercept> $constructor() {
        return (a1, a2) -> {
            return new RegrIntercept(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RegrIntercept)) {
            return super.equals(that);
        }
        QOM.RegrIntercept o = (QOM.RegrIntercept) that;
        return StringUtils.equals($y(), o.$y()) && StringUtils.equals($x(), o.$x());
    }
}

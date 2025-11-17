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
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegrSxx.class */
public final class RegrSxx extends AbstractAggregateFunction<BigDecimal> implements QOM.RegrSxx {
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegrSxx(Field<? extends Number> y, Field<? extends Number> x) {
        super(false, Names.N_REGR_SXX, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(y, SQLDataType.INTEGER), Tools.nullSafeNotNull(x, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            acceptEmulation(ctx);
        } else {
            super.accept(ctx);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAggregateFunction
    public void acceptFunctionName(Context<?> ctx) {
        super.acceptFunctionName(ctx);
    }

    private final void acceptEmulation(Context<?> ctx) {
        Field<?> field = getArguments().get(0);
        Field<?> field2 = getArguments().get(1);
        ctx.visit(fo(DSL.regrCount(field, field2)).times((Field<? extends Number>) fo(DSL.varPop(y(field, field2)))));
    }

    @Override // org.jooq.impl.QOM.RegrSxx
    public final Field<? extends Number> $y() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.RegrSxx
    public final Field<? extends Number> $x() {
        return (Field) getArguments().get(1);
    }

    @Override // org.jooq.impl.QOM.RegrSxx
    public final QOM.RegrSxx $y(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $x());
    }

    @Override // org.jooq.impl.QOM.RegrSxx
    public final QOM.RegrSxx $x(Field<? extends Number> newValue) {
        return $constructor().apply($y(), newValue);
    }

    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.RegrSxx> $constructor() {
        return (a1, a2) -> {
            return new RegrSxx(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RegrSxx)) {
            return super.equals(that);
        }
        QOM.RegrSxx o = (QOM.RegrSxx) that;
        return StringUtils.equals($y(), o.$y()) && StringUtils.equals($x(), o.$x());
    }
}

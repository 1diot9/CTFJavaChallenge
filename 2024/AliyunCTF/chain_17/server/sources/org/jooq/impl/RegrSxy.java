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
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegrSxy.class */
public final class RegrSxy extends AbstractAggregateFunction<BigDecimal> implements QOM.RegrSxy {
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegrSxy(Field<? extends Number> y, Field<? extends Number> x) {
        super(false, Names.N_REGR_SXY, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(y, SQLDataType.INTEGER), Tools.nullSafeNotNull(x, SQLDataType.INTEGER)});
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
        Field<?> y = getArguments().get(1);
        ctx.visit(fo(DSL.sum(field.times((Field<? extends Number>) y))).minus(fo(DSL.sum(x(field, y))).times((Field<? extends Number>) fo(DSL.sum(y(field, y)))).div(fon(DSL.count(field.plus(y))).cast(d(ctx)))));
    }

    @Override // org.jooq.impl.QOM.RegrSxy
    public final Field<? extends Number> $y() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.RegrSxy
    public final Field<? extends Number> $x() {
        return (Field) getArguments().get(1);
    }

    @Override // org.jooq.impl.QOM.RegrSxy
    public final QOM.RegrSxy $y(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $x());
    }

    @Override // org.jooq.impl.QOM.RegrSxy
    public final QOM.RegrSxy $x(Field<? extends Number> newValue) {
        return $constructor().apply($y(), newValue);
    }

    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.RegrSxy> $constructor() {
        return (a1, a2) -> {
            return new RegrSxy(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RegrSxy)) {
            return super.equals(that);
        }
        QOM.RegrSxy o = (QOM.RegrSxy) that;
        return StringUtils.equals($y(), o.$y()) && StringUtils.equals($x(), o.$x());
    }
}

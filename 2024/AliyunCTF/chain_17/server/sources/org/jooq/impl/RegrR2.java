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
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegrR2.class */
public final class RegrR2 extends AbstractAggregateFunction<BigDecimal> implements QOM.RegrR2 {
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegrR2(Field<? extends Number> y, Field<? extends Number> x) {
        super(false, Names.N_REGR_R2, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(y, SQLDataType.INTEGER), Tools.nullSafeNotNull(x, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            acceptEmulation(ctx);
        } else {
            super.accept(ctx);
        }
    }

    private final void acceptEmulation(Context<?> ctx) {
        Field<?> field = getArguments().get(0);
        Field<?> field2 = getArguments().get(1);
        ctx.visit(DSL.when(fo(DSL.varPop(y(field, field2))).eq(DSL.inline(BigDecimal.ZERO)), (Field) DSL.inline((Object) null, d(ctx))).when(fo(DSL.varPop(x(field, field2))).eq(DSL.inline(BigDecimal.ZERO)), (Field) DSL.inline(BigDecimal.ONE)).else_(DSL.square(fo(DSL.corr(field, field2)))));
    }

    @Override // org.jooq.impl.QOM.RegrR2
    public final Field<? extends Number> $y() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.RegrR2
    public final Field<? extends Number> $x() {
        return (Field) getArguments().get(1);
    }

    @Override // org.jooq.impl.QOM.RegrR2
    public final QOM.RegrR2 $y(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $x());
    }

    @Override // org.jooq.impl.QOM.RegrR2
    public final QOM.RegrR2 $x(Field<? extends Number> newValue) {
        return $constructor().apply($y(), newValue);
    }

    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.RegrR2> $constructor() {
        return (a1, a2) -> {
            return new RegrR2(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RegrR2)) {
            return super.equals(that);
        }
        QOM.RegrR2 o = (QOM.RegrR2) that;
        return StringUtils.equals($y(), o.$y()) && StringUtils.equals($x(), o.$x());
    }
}

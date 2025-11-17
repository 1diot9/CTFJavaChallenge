package org.jooq.impl;

import java.math.BigDecimal;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Median.class */
public final class Median extends AbstractAggregateFunction<BigDecimal> implements QOM.Median {
    private static final Set<SQLDialect> EMULATE_WITH_PERCENTILES = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Median(Field<? extends Number> field) {
        super(false, Names.N_MEDIAN, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> context) {
        if (EMULATE_WITH_PERCENTILES.contains(context.dialect())) {
            context.visit((Field<?>) fo(DSL.percentileCont(DSL.inline(new BigDecimal("0.5"))).withinGroupOrderBy(this.arguments)));
        } else {
            super.accept(context);
        }
    }

    @Override // org.jooq.impl.QOM.Median
    public final Field<? extends Number> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.Median
    public final QOM.Median $field(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Median> $constructor() {
        return a1 -> {
            return new Median(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Median) {
            QOM.Median o = (QOM.Median) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

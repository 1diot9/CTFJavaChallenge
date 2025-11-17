package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/StddevPop.class */
public final class StddevPop extends AbstractAggregateFunction<BigDecimal> implements QOM.StddevPop {

    /* renamed from: org.jooq.impl.StddevPop$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/StddevPop$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StddevPop(Field<? extends Number> field) {
        super(false, Names.N_STDDEV_POP, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.INTEGER)});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAggregateFunction
    public void acceptFunctionName(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                super.acceptFunctionName(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.StddevPop
    public final Field<? extends Number> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.StddevPop
    public final QOM.StddevPop $field(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.StddevPop> $constructor() {
        return a1 -> {
            return new StddevPop(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.StddevPop) {
            QOM.StddevPop o = (QOM.StddevPop) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

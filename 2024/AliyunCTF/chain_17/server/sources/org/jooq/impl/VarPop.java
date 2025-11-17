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
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/VarPop.class */
public final class VarPop extends AbstractAggregateFunction<BigDecimal> implements QOM.VarPop {
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.IGNITE, SQLDialect.SQLITE);

    /* renamed from: org.jooq.impl.VarPop$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/VarPop$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VarPop(Field<? extends Number> field) {
        super(false, Names.N_VAR_POP, (DataType) SQLDataType.NUMERIC, (Field<?>[]) new Field[]{Tools.nullSafeNotNull(field, SQLDataType.INTEGER)});
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            Field<?> field = getArguments().get(0);
            ctx.visit(fo(DSL.avg(DSL.square(field).cast(d(ctx)))).minus(DSL.square(fo(DSL.avg(field.cast(d(ctx)))))));
        } else {
            super.accept(ctx);
        }
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

    @Override // org.jooq.impl.QOM.VarPop
    public final Field<? extends Number> $field() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.VarPop
    public final QOM.VarPop $field(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.VarPop> $constructor() {
        return a1 -> {
            return new VarPop(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.VarPop) {
            QOM.VarPop o = (QOM.VarPop) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

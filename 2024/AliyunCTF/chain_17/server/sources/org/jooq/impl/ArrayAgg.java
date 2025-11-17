package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayAgg.class */
public final class ArrayAgg<T> extends AbstractAggregateFunction<T[]> implements QOM.ArrayAgg<T> {

    /* renamed from: org.jooq.impl.ArrayAgg$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayAgg$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayAgg(boolean distinct, Field<T> arg) {
        super(distinct, Names.N_ARRAY_AGG, arg.getDataType().getArrayDataType(), (Field<?>[]) new Field[]{arg});
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Names.N_ARRAY_AGG).sql('(');
                acceptArguments1(ctx, new QueryPartListView(this.arguments.get(0)));
                acceptOrderBy(ctx);
                ctx.sql(')');
                acceptFilterClause(ctx);
                acceptOverClause(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return (Field) getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.ArrayAgg<T>> $constructor() {
        return f -> {
            return new ArrayAgg(this.distinct, f);
        };
    }
}

package org.jooq.impl;

import java.util.Set;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BoolOr.class */
public final class BoolOr extends AbstractAggregateFunction<Boolean> implements QOM.BoolOr {
    private static final Set<SQLDialect> EMULATE = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);

    /* renamed from: org.jooq.impl.BoolOr$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BoolOr$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BoolOr(Condition condition) {
        super(false, Names.N_BOOL_OR, (DataType) SQLDataType.BOOLEAN, (Field<?>[]) new Field[]{DSL.field(condition)});
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractAggregateFunction
    public final void acceptFunctionName(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                super.acceptFunctionName(ctx);
                return;
        }
    }

    final Condition condition() {
        return DSL.condition((Field<Boolean>) getArguments().get(0));
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (EMULATE.contains(ctx.dialect())) {
            ctx.visit((Field<?>) DSL.field(fo(DSL.max(DSL.when(condition(), (Field) DSL.one()).otherwise((Field) DSL.zero()))).eq(DSL.one())));
        } else {
            super.accept(ctx);
        }
    }

    @Override // org.jooq.impl.QOM.BoolOr
    public final Condition $condition() {
        return DSL.condition((Field<Boolean>) getArguments().get(0));
    }

    @Override // org.jooq.impl.QOM.BoolOr
    public final QOM.BoolOr $condition(Condition newValue) {
        return $constructor().apply(newValue);
    }

    public final org.jooq.Function1<? super Condition, ? extends QOM.BoolOr> $constructor() {
        return a1 -> {
            return new BoolOr(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.BoolOr) {
            QOM.BoolOr o = (QOM.BoolOr) that;
            return StringUtils.equals($condition(), o.$condition());
        }
        return super.equals(that);
    }
}

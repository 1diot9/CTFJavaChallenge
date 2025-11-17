package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Not.class */
public final class Not extends AbstractCondition implements QOM.Not {
    final Condition condition;
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_NOT};

    /* renamed from: org.jooq.impl.Not$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Not$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Not(Condition condition) {
        this.condition = condition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public boolean isNullable() {
        return !(this.condition instanceof AbstractCondition) || ((AbstractCondition) this.condition).isNullable();
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                if ((this.condition instanceof AbstractField) && ((AbstractField) this.condition).parenthesised(ctx)) {
                    ctx.visit(Keywords.K_NOT).sql(' ').visit(this.condition);
                    return;
                } else {
                    ctx.visit(Keywords.K_NOT).sql(" (").visit(this.condition).sql(')');
                    return;
                }
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Condition $arg1() {
        return this.condition;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Not $arg1(Condition newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Condition, ? extends QOM.Not> $constructor() {
        return a1 -> {
            return new Not(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Not) {
            QOM.Not o = (QOM.Not) that;
            return StringUtils.equals($condition(), o.$condition());
        }
        return super.equals(that);
    }
}

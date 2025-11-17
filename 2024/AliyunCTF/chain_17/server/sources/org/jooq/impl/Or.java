package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Function2;
import org.jooq.Operator;
import org.jooq.impl.Expression;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Or.class */
public final class Or extends AbstractCondition implements QOM.Or {
    final Condition arg1;
    final Condition arg2;
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_OR};

    /* JADX INFO: Access modifiers changed from: package-private */
    public Or(Condition arg1, Condition arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.sqlIndentStart('(');
        Expression.acceptAssociative(ctx, this, q -> {
            return new Expression.Expr(q.arg1, Operator.OR.toKeyword(), q.arg2);
        }, (v0) -> {
            v0.formatSeparator();
        });
        ctx.sqlIndentEnd(')');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return ((AbstractCondition) this.arg1).isNullable() || ((AbstractCondition) this.arg2).isNullable();
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Condition $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Condition $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Or $arg1(Condition newValue) {
        return new Or(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Or $arg2(Condition newValue) {
        return new Or($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Condition, ? super Condition, ? extends QOM.Or> $constructor() {
        return (a1, a2) -> {
            return new Or(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Or)) {
            return super.equals(that);
        }
        QOM.Or o = (QOM.Or) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

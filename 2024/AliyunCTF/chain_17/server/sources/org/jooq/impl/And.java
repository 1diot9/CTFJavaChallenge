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
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/And.class */
public final class And extends AbstractCondition implements QOM.And {
    final Condition arg1;
    final Condition arg2;
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_AND};

    /* JADX INFO: Access modifiers changed from: package-private */
    public And(Condition arg1, Condition arg2) {
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
            return new Expression.Expr(q.arg1, Operator.AND.toKeyword(), q.arg2);
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
    public final QOM.And $arg1(Condition newValue) {
        return new And(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.And $arg2(Condition newValue) {
        return new And($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Condition, ? super Condition, ? extends QOM.And> $constructor() {
        return (a1, a2) -> {
            return new And(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.And)) {
            return super.equals(that);
        }
        QOM.And o = (QOM.And) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

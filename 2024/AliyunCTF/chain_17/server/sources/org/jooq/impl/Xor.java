package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Operator;
import org.jooq.SQLDialect;
import org.jooq.impl.Expression;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Xor.class */
public final class Xor extends AbstractCondition implements QOM.Xor {
    final Condition arg1;
    final Condition arg2;
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_XOR};
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Xor(Condition arg1, Condition arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return !NO_SUPPORT_NATIVE.contains(ctx.dialect());
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            ctx.visit(this.arg1.ne((Field) this.arg2));
            return;
        }
        ctx.sqlIndentStart('(');
        Expression.acceptAssociative(ctx, this, q -> {
            return new Expression.Expr(q.arg1, Operator.XOR.toKeyword(), q.arg2);
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
    public final QOM.Xor $arg1(Condition newValue) {
        return new Xor(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Xor $arg2(Condition newValue) {
        return new Xor($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Condition, ? super Condition, ? extends QOM.Xor> $constructor() {
        return (a1, a2) -> {
            return new Xor(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Xor)) {
            return super.equals(that);
        }
        QOM.Xor o = (QOM.Xor) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

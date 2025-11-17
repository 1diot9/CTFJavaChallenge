package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.False;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FalseCondition.class */
public final class FalseCondition extends AbstractCondition implements False, QOM.False, QOM.UEmpty, SimpleQueryPart {
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};
    static final FalseCondition INSTANCE = new FalseCondition();

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return !TrueCondition.NO_SUPPORT_BOOLEAN.contains(ctx.dialect());
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (TrueCondition.NO_SUPPORT_BOOLEAN.contains(ctx.dialect())) {
            ctx.sql("1 = 0");
        } else {
            ctx.visit(Keywords.K_FALSE);
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private FalseCondition() {
    }
}

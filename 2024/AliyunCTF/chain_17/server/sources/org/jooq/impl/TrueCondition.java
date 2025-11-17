package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.True;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TrueCondition.class */
public final class TrueCondition extends AbstractCondition implements True, QOM.True, QOM.UEmpty, SimpleQueryPart {
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};
    static final TrueCondition INSTANCE = new TrueCondition();
    static final Set<SQLDialect> NO_SUPPORT_BOOLEAN = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.SQLITE);

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return !NO_SUPPORT_BOOLEAN.contains(ctx.dialect());
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_BOOLEAN.contains(ctx.dialect())) {
            ctx.sql("1 = 1");
        } else {
            ctx.visit(Keywords.K_TRUE);
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private TrueCondition() {
    }
}

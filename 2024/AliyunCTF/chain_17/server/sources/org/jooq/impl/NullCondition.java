package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Null;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NullCondition.class */
public final class NullCondition extends AbstractCondition implements Null, QOM.Null, QOM.UEmpty, SimpleQueryPart {
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};
    static final NullCondition INSTANCE = new NullCondition();
    static final Set<SQLDialect> NO_SUPPORT_UNTYPED_NULL = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.HSQLDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return (NO_SUPPORT_UNTYPED_NULL.contains(ctx.dialect()) || TrueCondition.NO_SUPPORT_BOOLEAN.contains(ctx.dialect())) ? false : true;
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (NO_SUPPORT_UNTYPED_NULL.contains(ctx.dialect())) {
            ctx.visit(DSL.castNull(SQLDataType.BOOLEAN));
        } else if (TrueCondition.NO_SUPPORT_BOOLEAN.contains(ctx.dialect())) {
            ctx.visit(Keywords.K_NULL).sql(" = ").visit(Keywords.K_NULL);
        } else {
            ctx.visit(Keywords.K_NULL);
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private NullCondition() {
    }
}

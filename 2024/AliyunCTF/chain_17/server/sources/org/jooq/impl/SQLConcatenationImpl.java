package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLConcatenationImpl.class */
final class SQLConcatenationImpl extends AbstractQueryPart implements SQL, QOM.UEmpty {
    private static final Clause[] CLAUSES = {Clause.TEMPLATE};
    private final QueryPart[] input;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLConcatenationImpl(QueryPart... input) {
        this.input = input;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        for (int i = 0; i < this.input.length; i++) {
            if (i > 0) {
                ctx.formatSeparator();
            }
            ctx.visit(this.input[i]);
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }
}

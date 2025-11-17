package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.QueryPartInternal;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLQuery.class */
final class SQLQuery extends AbstractRowCountQuery implements QOM.UEmptyQuery {
    private final SQL delegate;

    /* renamed from: org.jooq.impl.SQLQuery$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLQuery$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLQuery(Configuration configuration, SQL delegate) {
        super(configuration);
        this.delegate = delegate;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.delegate);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        SQL sql = this.delegate;
        if (sql instanceof QueryPartInternal) {
            QueryPartInternal q = (QueryPartInternal) sql;
            return q.clauses(ctx);
        }
        return null;
    }
}

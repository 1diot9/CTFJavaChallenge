package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONReturning.class */
final class JSONReturning extends AbstractQueryPart implements SimpleQueryPart, QOM.UTransient {
    static final Set<SQLDialect> NO_SUPPORT_RETURNING = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    final DataType<?> type;

    /* renamed from: org.jooq.impl.JSONReturning$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONReturning$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONReturning(DataType<?> type) {
        this.type = type;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return (NO_SUPPORT_RETURNING.contains(ctx.dialect()) || this.type == null) ? false : true;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                if (!NO_SUPPORT_RETURNING.contains(ctx.dialect())) {
                    ctx.visit(Keywords.K_RETURNING).sql(' ').sql(this.type.getCastTypeName(ctx.configuration()));
                    return;
                }
                return;
        }
    }
}

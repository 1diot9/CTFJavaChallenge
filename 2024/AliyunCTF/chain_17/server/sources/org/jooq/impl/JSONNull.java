package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONNull.class */
final class JSONNull extends AbstractQueryPart implements SimpleQueryPart, QOM.UTransient {
    static final Set<SQLDialect> NO_SUPPORT_ABSENT_ON_NULL = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> NO_SUPPORT_NULL_ON_EMPTY = SQLDialect.supportedBy(SQLDialect.TRINO);
    final QOM.JSONOnNull type;

    /* renamed from: org.jooq.impl.JSONNull$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONNull$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONNull(QOM.JSONOnNull type) {
        this.type = type;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return (NO_SUPPORT_ABSENT_ON_NULL.contains(ctx.dialect()) || this.type == null) ? false : true;
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                if (!NO_SUPPORT_ABSENT_ON_NULL.contains(ctx.dialect())) {
                    if (this.type == QOM.JSONOnNull.NULL_ON_NULL) {
                        ctx.visit(Keywords.K_NULL).sql(' ').visit(Keywords.K_ON).sql(' ').visit(Keywords.K_NULL);
                        return;
                    } else {
                        if (this.type == QOM.JSONOnNull.ABSENT_ON_NULL) {
                            ctx.visit(Keywords.K_ABSENT).sql(' ').visit(Keywords.K_ON).sql(' ').visit(Keywords.K_NULL);
                            return;
                        }
                        return;
                    }
                }
                return;
        }
    }
}

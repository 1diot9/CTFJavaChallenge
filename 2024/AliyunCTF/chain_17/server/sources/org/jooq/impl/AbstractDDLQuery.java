package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DDLQuery;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractDDLQuery.class */
abstract class AbstractDDLQuery extends AbstractRowCountQuery implements DDLQuery {
    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractDDLQuery(Configuration configuration) {
        super(configuration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    public static final void acceptCascade(Context<?> ctx, QOM.Cascade cascade) {
        if (cascade == QOM.Cascade.CASCADE) {
            ctx.sql(' ').visit(Keywords.K_CASCADE);
        } else if (cascade == QOM.Cascade.RESTRICT) {
            ctx.sql(' ').visit(Keywords.K_RESTRICT);
        }
    }
}

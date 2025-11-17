package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Commit.class */
final class Commit extends AbstractRowCountQuery implements QOM.Commit {

    /* renamed from: org.jooq.impl.Commit$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Commit$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Commit(Configuration configuration) {
        super(configuration);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_COMMIT);
                return;
        }
    }
}

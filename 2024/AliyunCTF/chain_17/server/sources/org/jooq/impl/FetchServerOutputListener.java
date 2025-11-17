package org.jooq.impl;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FetchServerOutputListener.class */
final class FetchServerOutputListener implements ExecuteListener {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) FetchServerOutputListener.class);

    /* renamed from: org.jooq.impl.FetchServerOutputListener$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FetchServerOutputListener$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.ExecuteListener
    public final void executeStart(ExecuteContext ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return;
        }
    }

    @Override // org.jooq.ExecuteListener
    public final void executeEnd(ExecuteContext ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return;
        }
    }
}

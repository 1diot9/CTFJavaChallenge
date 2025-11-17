package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.CommonTableExpression;
import org.jooq.Context;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CommonTableExpressionList.class */
public final class CommonTableExpressionList extends QueryPartList<CommonTableExpression<?>> {
    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        markTopLevelCteAndAccept(ctx, c -> {
            super.accept(c);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    public static void markTopLevelCteAndAccept(Context<?> ctx, Consumer<? super Context<?>> consumer) {
        if (ctx.subqueryLevel() == 0) {
            ctx.scopeMarkStart(ScopeMarker.TOP_LEVEL_CTE.beforeFirst).scopeMarkEnd(ScopeMarker.TOP_LEVEL_CTE.beforeFirst);
        }
        consumer.accept(ctx);
        if (ctx.subqueryLevel() == 0) {
            ctx.scopeMarkStart(ScopeMarker.TOP_LEVEL_CTE.afterLast).scopeMarkEnd(ScopeMarker.TOP_LEVEL_CTE.afterLast);
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresCTE() {
        return true;
    }
}

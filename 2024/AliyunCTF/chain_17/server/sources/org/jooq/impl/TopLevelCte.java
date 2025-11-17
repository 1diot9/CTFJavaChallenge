package org.jooq.impl;

import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.impl.ScopeMarker;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TopLevelCte.class */
public final class TopLevelCte extends QueryPartList<QueryPart> implements ScopeMarker.ScopeContent {
    boolean recursive;

    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        CommonTableExpressionList.markTopLevelCteAndAccept(ctx, c -> {
            super.accept(c);
        });
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresCTE() {
        return true;
    }
}

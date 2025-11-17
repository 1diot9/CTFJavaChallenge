package org.jooq.impl;

import java.util.Map;
import org.jooq.Context;
import org.jooq.QueryPart;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QueryPartMapView.class */
public final class QueryPartMapView<K extends QueryPart, V extends QueryPart> extends AbstractQueryPartMap<K, V> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartMapView(Map<K, V> map) {
        super(map);
    }

    @Override // org.jooq.impl.AbstractQueryPartMap, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(QueryPartCollectionView.wrap($tuples()));
    }

    @Override // org.jooq.impl.AbstractQueryPartMap
    final java.util.function.Function<? super Map<K, V>, ? extends AbstractQueryPartMap<K, V>> $construct() {
        return QueryPartMapView::new;
    }
}

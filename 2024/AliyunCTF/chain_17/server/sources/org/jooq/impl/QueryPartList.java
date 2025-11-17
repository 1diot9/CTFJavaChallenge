package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.jooq.QueryPart;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QueryPartList.class */
public class QueryPartList<T extends QueryPart> extends QueryPartListView<T> {
    private static final Lazy<QueryPartList> EMPTY_LIST = Lazy.of(() -> {
        return new QueryPartList();
    });

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartList() {
        this((Collection) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public QueryPartList(T... wrappedList) {
        this(Arrays.asList(wrappedList));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartList(Iterable<? extends T> wrappedList) {
        super(new ArrayList());
        addAll0(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    public QueryPartList<T> qualify(boolean newQualify) {
        return (QueryPartList) super.qualify(newQualify);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    public QueryPartList<T> map(java.util.function.Function<? super T, ? extends T> newMapper) {
        return (QueryPartList) super.map((java.util.function.Function) newMapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    public QueryPartList<T> map(ObjIntFunction<? super T, ? extends T> newMapper) {
        return (QueryPartList) super.map((ObjIntFunction) newMapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    public QueryPartList<T> separator(String newSeparator) {
        return (QueryPartList) super.separator(newSeparator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <Q extends QueryPart> QueryPartList<Q> emptyList() {
        return EMPTY_LIST.get();
    }
}

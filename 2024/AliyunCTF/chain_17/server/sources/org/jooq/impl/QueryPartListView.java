package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QueryPartListView.class */
public class QueryPartListView<T extends QueryPart> extends QueryPartCollectionView<T> implements QOM.UnmodifiableList<T> {
    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public static final <T extends QueryPart> QueryPartListView<T> wrap(T... wrappedList) {
        return new QueryPartListView<>(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T extends QueryPart> QueryPartListView<T> wrap(List<T> wrappedList) {
        return new QueryPartListView<>(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public QueryPartListView(T... wrappedList) {
        this(Arrays.asList(wrappedList));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartListView(List<T> wrappedList) {
        super(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public QueryPartListView<T> qualify(boolean newQualify) {
        return (QueryPartListView) super.qualify(newQualify);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public QueryPartListView<T> map(java.util.function.Function<? super T, ? extends T> newMapper) {
        return (QueryPartListView) super.map((java.util.function.Function) newMapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public QueryPartListView<T> map(ObjIntFunction<? super T, ? extends T> newMapper) {
        return (QueryPartListView) super.map((ObjIntFunction) newMapper);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public QueryPartListView<T> separator(String newSeparator) {
        return (QueryPartListView) super.separator(newSeparator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.QueryPartCollectionView
    public List<T> wrapped() {
        return (List) super.wrapped();
    }

    @Override // org.jooq.impl.QueryPartCollectionView
    protected void toSQLEmptyList(Context<?> context) {
    }

    @Override // java.util.List
    public final boolean addAll(int index, Collection<? extends T> c) {
        boolean modified = false;
        for (T e : c) {
            if (canAdd(e)) {
                boolean z = modified | true;
                modified = z;
                if (z) {
                    int i = index;
                    index++;
                    wrapped().add(i, e);
                }
            }
        }
        return modified;
    }

    @Override // java.util.List
    public final T get(int index) {
        return wrapped().get(index);
    }

    @Override // java.util.List
    public final T set(int index, T element) {
        if (canAdd(element)) {
            return wrapped().set(index, element);
        }
        return null;
    }

    @Override // java.util.List
    public final void add(int index, T element) {
        if (canAdd(element)) {
            wrapped().add(index, element);
        }
    }

    @Override // java.util.List
    public final T remove(int index) {
        return wrapped().remove(index);
    }

    @Override // java.util.List
    public final int indexOf(Object o) {
        return wrapped().indexOf(o);
    }

    @Override // java.util.List
    public final int lastIndexOf(Object o) {
        return wrapped().lastIndexOf(o);
    }

    @Override // java.util.List
    public final ListIterator<T> listIterator() {
        return wrapped().listIterator();
    }

    @Override // java.util.List
    public final ListIterator<T> listIterator(int index) {
        return wrapped().listIterator(index);
    }

    @Override // java.util.List
    public final List<T> subList(int fromIndex, int toIndex) {
        return wrapped().subList(fromIndex, toIndex);
    }

    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof List) {
            return this.wrapped.equals(that);
        }
        return false;
    }
}

package org.jooq.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QueryPartCollectionView.class */
public class QueryPartCollectionView<T extends QueryPart> extends AbstractQueryPart implements QOM.UnmodifiableCollection<T>, SimpleCheckQueryPart, SeparatedQueryPart {
    final Collection<T> wrapped;
    Boolean qualify;
    String separator;
    ObjIntFunction<? super T, ? extends T> mapper;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T extends QueryPart> QueryPartCollectionView<T> wrap(Collection<T> wrapped) {
        return new QueryPartCollectionView<>(wrapped);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartCollectionView(Collection<T> wrapped) {
        this.wrapped = wrapped != null ? wrapped : Collections.emptyList();
        if (wrapped instanceof QueryPartCollectionView) {
            QueryPartCollectionView<T> v = (QueryPartCollectionView) wrapped;
            this.qualify = v.qualify;
            this.separator = v.separator;
            this.mapper = v.mapper;
            return;
        }
        this.separator = ",";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartCollectionView<T> qualify(boolean newQualify) {
        this.qualify = Boolean.valueOf(newQualify);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartCollectionView<T> map(java.util.function.Function<? super T, ? extends T> newMapper) {
        return map((t, i) -> {
            return (QueryPart) newMapper.apply(t);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartCollectionView<T> map(ObjIntFunction<? super T, ? extends T> newMapper) {
        this.mapper = this.mapper == null ? newMapper : this.mapper.andThen(newMapper);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryPartCollectionView<T> separator(String newSeparator) {
        this.separator = newSeparator;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Collection<T> wrapped() {
        return this.wrapped;
    }

    @Override // org.jooq.impl.SimpleCheckQueryPart
    public boolean isSimple(Context<?> ctx) {
        return Tools.allMatch(this, e -> {
            return Tools.isSimple((Context<?>) ctx, e);
        });
    }

    @Override // org.jooq.impl.SeparatedQueryPart
    public boolean rendersSeparator() {
        if (isEmpty()) {
            return false;
        }
        return Tools.isRendersSeparator((QueryPart) Tools.last(this.wrapped));
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public boolean rendersContent(Context<?> ctx) {
        return !isEmpty();
    }

    /* JADX WARN: Type inference failed for: r0v79, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        java.util.BitSet rendersContent = new java.util.BitSet(size());
        int i = 0;
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            T e = it.next();
            int i2 = i;
            i++;
            rendersContent.set(i2, ((QueryPartInternal) e).rendersContent(ctx));
        }
        int size = rendersContent.cardinality();
        boolean format = ctx.format() && ((size >= 2 && !isSimple(ctx)) || size > 4);
        boolean previousQualify = ctx.qualify();
        boolean previousAlreadyIndented = Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED));
        boolean indent = format && !previousAlreadyIndented;
        if (previousAlreadyIndented) {
            ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, false);
        }
        if (this.qualify != null) {
            ctx.qualify(this.qualify.booleanValue());
        }
        if (indent) {
            ctx.formatIndentStart();
        }
        if (ctx.separatorRequired()) {
            if (format) {
                ctx.formatSeparator();
            } else {
                ctx.sql(' ');
            }
        }
        if (size == 0) {
            toSQLEmptyList(ctx);
        } else {
            int j = 0;
            int k = 0;
            T prev = null;
            Iterator<T> it2 = iterator();
            while (it2.hasNext()) {
                T part = it2.next();
                int j0 = j;
                int i3 = j;
                j++;
                if (rendersContent.get(i3)) {
                    if (this.mapper != null) {
                        part = this.mapper.apply(part, j0);
                    }
                    int i4 = k;
                    k++;
                    if (i4 > 0) {
                        if (!(prev instanceof SeparatedQueryPart) || !((SeparatedQueryPart) prev).rendersSeparator()) {
                            ctx.sql(this.separator);
                        }
                        if (format) {
                            ctx.formatSeparator();
                        } else {
                            ctx.sql(' ');
                        }
                    } else if (indent) {
                        ctx.formatNewLine();
                    }
                    if (indent) {
                        T t = part;
                        ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, Boolean.valueOf((t instanceof QueryPartCollectionView) && ((QueryPartCollectionView) t).size() > 1), c -> {
                            acceptElement(c, t);
                        });
                    } else {
                        acceptElement(ctx, part);
                    }
                    prev = part;
                } else {
                    prev = part;
                }
            }
        }
        if (indent) {
            ctx.formatIndentEnd().formatNewLine();
        }
        if (this.qualify != null) {
            ctx.qualify(previousQualify);
        }
        if (previousAlreadyIndented) {
            ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, Boolean.valueOf(previousAlreadyIndented));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void acceptElement(Context<?> ctx, T part) {
        if (part instanceof Condition) {
            Condition c = (Condition) part;
            ctx.visit((QueryPart) DSL.field(c));
        } else {
            ctx.visit(part);
        }
    }

    protected void toSQLEmptyList(Context<?> context) {
    }

    @Override // java.util.Collection
    public final int size() {
        return this.wrapped.size();
    }

    @Override // java.util.Collection
    public final boolean isEmpty() {
        return this.wrapped.isEmpty();
    }

    @Override // java.util.Collection
    public final boolean contains(Object o) {
        return this.wrapped.contains(o);
    }

    @Override // java.util.Collection, java.lang.Iterable
    public final Iterator<T> iterator() {
        return this.wrapped.iterator();
    }

    @Override // java.util.Collection
    public final Object[] toArray() {
        return this.wrapped.toArray();
    }

    @Override // java.util.Collection
    public final <E> E[] toArray(E[] eArr) {
        return (E[]) this.wrapped.toArray(eArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean canAdd(T e) {
        return e != null;
    }

    @Override // java.util.Collection
    public final boolean add(T e) {
        if (canAdd(e)) {
            return this.wrapped.add(e);
        }
        return false;
    }

    @Override // java.util.Collection
    public final boolean remove(Object o) {
        return this.wrapped.remove(o);
    }

    @Override // java.util.Collection
    public final boolean containsAll(Collection<?> c) {
        return this.wrapped.containsAll(c);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean addAll0(Iterable<? extends T> c) {
        boolean modified = false;
        if (c != null) {
            for (T e : c) {
                if (add((QueryPartCollectionView<T>) e)) {
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override // java.util.Collection
    public final boolean addAll(Collection<? extends T> c) {
        return addAll0(c);
    }

    @Override // java.util.Collection
    public final boolean removeAll(Collection<?> c) {
        return this.wrapped.removeAll(c);
    }

    @Override // java.util.Collection
    public final boolean retainAll(Collection<?> c) {
        return this.wrapped.retainAll(c);
    }

    @Override // java.util.Collection
    public final void clear() {
        this.wrapped.clear();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if ((that instanceof List) && !(this instanceof List)) {
            return false;
        }
        if (that instanceof QueryPartCollectionView) {
            QueryPartCollectionView<?> q = (QueryPartCollectionView) that;
            return this.wrapped.equals(q.wrapped);
        }
        return super.equals(that);
    }
}

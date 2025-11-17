package org.jooq.impl;

import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.SelectFieldOrAsterisk;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectFieldList.class */
public final class SelectFieldList<F extends SelectFieldOrAsterisk> extends QueryPartList<F> {
    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QueryPartCollectionView
    public /* bridge */ /* synthetic */ void acceptElement(Context context, QueryPart queryPart) {
        acceptElement((Context<?>) context, (Context) queryPart);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectFieldList() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectFieldList(Iterable<? extends F> wrappedList) {
        super(wrappedList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectFieldList(F[] wrappedList) {
        super(wrappedList);
    }

    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    protected final void toSQLEmptyList(Context<?> ctx) {
        ctx.visit(AsteriskImpl.INSTANCE.get());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return true;
    }

    protected void acceptElement(Context<?> ctx, F part) {
        if (part instanceof AbstractTable) {
            AbstractTable<?> t = (AbstractTable) part;
            acceptElement0(ctx, t.tf());
        } else if (part instanceof AbstractRow) {
            AbstractRow<?> r = (AbstractRow) part;
            acceptElement0(ctx, r.rf());
        } else {
            acceptElement0(ctx, part);
        }
    }

    private void acceptElement0(Context<?> ctx, F part) {
        Tools.visitAutoAliased(ctx, part, (v0) -> {
            return v0.declareFields();
        }, (c, t) -> {
            super.acceptElement((Context<?>) c, (Context) t);
        });
    }
}

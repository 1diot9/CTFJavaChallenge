package org.jooq.impl;

import org.jooq.Context;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedSelectFieldList.class */
public final class QualifiedSelectFieldList extends QueryPartList<SelectFieldOrAsterisk> {
    private final Table<?> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedSelectFieldList(Table<?> table, Iterable<SelectFieldOrAsterisk> fields) {
        this.table = table;
        for (SelectFieldOrAsterisk field : fields) {
            add((QualifiedSelectFieldList) Tools.qualify(table, field));
        }
    }

    @Override // org.jooq.impl.QueryPartCollectionView, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean rendersContent(Context<?> ctx) {
        return super.rendersContent(ctx);
    }

    @Override // org.jooq.impl.QueryPartListView, org.jooq.impl.QueryPartCollectionView
    protected final void toSQLEmptyList(Context<?> context) {
        this.table.asterisk();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return true;
    }
}

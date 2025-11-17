package org.jooq.impl;

import org.jooq.Context;
import org.jooq.RowId;
import org.jooq.Table;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedRowid.class */
public final class QualifiedRowid extends AbstractField<RowId> implements QOM.QualifiedRowid {
    final Table<?> table;

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedRowid(Table<?> table) {
        super(Names.N_ROWID, Tools.allNotNull(SQLDataType.ROWID));
        this.table = table;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                TableFieldImpl.accept2(ctx, this.table, DSL.systemName("_rowid_"));
                return;
            case POSTGRES:
                TableFieldImpl.accept2(ctx, this.table, DSL.systemName("ctid"));
                return;
            default:
                TableFieldImpl.accept2(ctx, this.table, DSL.systemName("rowid"));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Table<?> $arg1() {
        return this.table;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.QualifiedRowid $arg1(Table<?> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Table<?>, ? extends QOM.QualifiedRowid> $constructor() {
        return a1 -> {
            return new QualifiedRowid(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.QualifiedRowid) {
            QOM.QualifiedRowid o = (QOM.QualifiedRowid) that;
            return StringUtils.equals($table(), o.$table());
        }
        return super.equals(that);
    }
}

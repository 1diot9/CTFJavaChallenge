package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ScalarSubquery.class */
public final class ScalarSubquery<T> extends AbstractField<T> implements QOM.ScalarSubquery<T> {
    static final Set<SQLDialect> NO_SUPPORT_WITH_IN_SCALAR_SUBQUERY = SQLDialect.supportedBy(SQLDialect.HSQLDB);
    final Select<?> query;
    final boolean predicandSubquery;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScalarSubquery(Select<?> query, DataType<T> type, boolean predicandSubquery) {
        super(Names.NQ_SELECT, type);
        this.query = query;
        this.predicandSubquery = predicandSubquery;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        SelectQueryImpl<?> q = Tools.selectQueryImpl(this.query);
        if (q != null && q.with != null && NO_SUPPORT_WITH_IN_SCALAR_SUBQUERY.contains(ctx.dialect())) {
            Tools.visitSubquery(ctx, DSL.select(DSL.asterisk()).from(this.query.asTable("t")), this.predicandSubquery ? 256 : 0);
        } else {
            Tools.visitSubquery(ctx, this.query, this.predicandSubquery ? 256 : 0);
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Select<? extends Record1<T>>, ? extends QOM.ScalarSubquery<T>> $constructor() {
        return s -> {
            return new ScalarSubquery(s, Tools.scalarType(s), this.predicandSubquery);
        };
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Select<? extends Record1<T>> $arg1() {
        return (Select<? extends Record1<T>>) this.query;
    }
}

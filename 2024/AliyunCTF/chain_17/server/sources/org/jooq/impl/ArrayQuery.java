package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ArrayQuery.class */
public final class ArrayQuery<T> extends AbstractField<T[]> implements QOM.ArrayQuery<T> {
    private final Select<? extends Record1<T>> query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayQuery(Select<? extends Record1<T>> query) {
        super(Names.N_ARRAY, query.getSelect().get(0).getDataType().getArrayDataType());
        this.query = query;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                Table<?> t = this.query.asTable("t", "c");
                Field<?> c = t.field("c");
                Tools.visitSubquery(ctx, DSL.select(DSL.arrayAgg(c)).from(t));
                return;
            default:
                ctx.visit(Keywords.K_ARRAY).visitSubquery(this.query);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.ArrayQuery
    public final Select<? extends Record1<T>> $query() {
        return this.query;
    }
}

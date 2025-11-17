package org.jooq.impl;

import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FetchCount.class */
final class FetchCount extends AbstractResultQuery<Record1<Integer>> implements QOM.UEmpty {
    private final Field<?>[] count;
    private final Select<?> query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FetchCount(Configuration configuration, Select<?> query) {
        super(configuration);
        this.count = new Field[]{DSL.count().as("c")};
        this.query = query;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(DSL.select(this.count).from(new AliasedSelect(this.query, true, true, false).as("t")));
    }

    @Override // org.jooq.impl.AbstractResultQuery
    public final Class<? extends Record1<Integer>> getRecordType0() {
        return RecordImpl1.class;
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields() {
        return this.count;
    }
}

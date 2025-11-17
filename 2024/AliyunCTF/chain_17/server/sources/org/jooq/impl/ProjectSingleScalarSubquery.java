package org.jooq.impl;

import java.util.Arrays;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ProjectSingleScalarSubquery.class */
public final class ProjectSingleScalarSubquery<T> extends AbstractField<T> implements QOM.UTransient {
    final Select<?> query;
    final int index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProjectSingleScalarSubquery(Select<?> query, int index) {
        super(Names.NQ_SELECT, query.getSelect().get(index).getDataType());
        this.query = query;
        this.index = index;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        SelectQueryImpl<?> q = Tools.selectQueryImpl(this.query);
        if (q.$distinct() || !q.$orderBy().isEmpty() || q.hasUnions()) {
            Field<?>[] f = Tools.fields(this.query.getSelect().size());
            ctx.visit(DSL.field(DSL.select(f[this.index]).from(this.query.asTable(DSL.table(DSL.name("t")), f))));
        } else {
            ctx.visit(DSL.field(this.query.$select(Arrays.asList((SelectFieldOrAsterisk) this.query.$select().get(this.index)))));
        }
    }
}

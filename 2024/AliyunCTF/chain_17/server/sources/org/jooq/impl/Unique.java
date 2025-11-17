package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Unique.class */
public final class Unique extends AbstractCondition implements QOM.Unique {
    final Select<?> query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Unique(Select<?> query) {
        this.query = query;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case H2:
                ctx.visit(Keywords.K_UNIQUE).sql(' ');
                Tools.visitSubquery(ctx, this.query, 256);
                return;
            default:
                Table<?> queryTable = this.query.asTable("t");
                Field<?>[] queryFields = queryTable.fields();
                Select<?> subquery = DSL.select(DSL.one()).from(queryTable).where(DSL.row((SelectField<?>[]) queryFields).isNotNull()).groupBy(queryFields).having(DSL.count().gt(DSL.one()));
                ctx.visit(DSL.notExists(subquery));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Select<?> $arg1() {
        return this.query;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Unique $arg1(Select<?> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Select<?>, ? extends QOM.Unique> $constructor() {
        return a1 -> {
            return new Unique(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Unique) {
            QOM.Unique o = (QOM.Unique) that;
            return StringUtils.equals($query(), o.$query());
        }
        return super.equals(that);
    }
}

package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CustomQueryPart.class */
public abstract class CustomQueryPart extends AbstractQueryPart implements QOM.UOpaque {
    private static final Clause[] CLAUSES = {Clause.CUSTOM};

    @Override // org.jooq.QueryPartInternal
    public abstract void accept(Context<?> context);

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    protected CustomQueryPart() {
    }

    public static final CustomQueryPart of(final Consumer<? super Context<?>> consumer) {
        return new CustomQueryPart() { // from class: org.jooq.impl.CustomQueryPart.1
            @Override // org.jooq.impl.CustomQueryPart, org.jooq.QueryPartInternal
            public void accept(Context<?> ctx) {
                consumer.accept(ctx);
            }
        };
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return super.declaresTables();
    }
}

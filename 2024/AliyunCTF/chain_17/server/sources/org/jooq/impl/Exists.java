package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Exists.class */
public final class Exists extends AbstractCondition implements QOM.Exists {
    final Select<?> query;
    private static final Clause[] CLAUSES_EXISTS = {Clause.CONDITION, Clause.CONDITION_EXISTS};

    /* renamed from: org.jooq.impl.Exists$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Exists$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Exists(Select<?> query) {
        this.query = query;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_EXISTS).sql(' ');
                Tools.visitSubquery(ctx, this.query, 256);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES_EXISTS;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Select<?> $arg1() {
        return this.query;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Exists $arg1(Select<?> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Select<?>, ? extends QOM.Exists> $constructor() {
        return a1 -> {
            return new Exists(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Exists) {
            QOM.Exists o = (QOM.Exists) that;
            return StringUtils.equals($query(), o.$query());
        }
        return super.equals(that);
    }
}

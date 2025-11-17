package org.jooq.impl;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.TableLike;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectIsNotNull.class */
final class SelectIsNotNull extends AbstractCondition implements QOM.SelectIsNotNull {
    private final Select<?> select;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.SelectIsNotNull$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectIsNotNull$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectIsNotNull(Select<?> select) {
        this.select = select;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (SelectIsNull.EMULATE_NULL_QUERY.contains(ctx.dialect())) {
            if (this.select.getSelect().size() == 1) {
                acceptStandard(ctx);
                return;
            } else {
                TableLike<?> as = new AliasedSelect(this.select, true, true, false).as("t");
                ctx.visit(DSL.inline(1).eq(DSL.selectCount().from(as).where(Tools.allNotNull(as.fields()))));
                return;
            }
        }
        acceptStandard(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        Tools.visitSubquery(ctx, this.select, 256);
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.sql(' ').visit(Keywords.K_IS_NOT_NULL);
                return;
        }
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Select<?> $arg1() {
        return this.select;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Select<?>, ? extends QOM.SelectIsNotNull> $constructor() {
        return r -> {
            return new SelectIsNotNull(r);
        };
    }
}

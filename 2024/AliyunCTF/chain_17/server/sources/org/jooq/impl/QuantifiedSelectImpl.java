package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Function2;
import org.jooq.QuantifiedSelect;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QuantifiedSelectImpl.class */
public final class QuantifiedSelectImpl<R extends Record> extends AbstractQueryPart implements QuantifiedSelect<R>, QOM.QuantifiedSelect<R> {
    final QOM.Quantifier quantifier;
    final Select<R> query;

    /* renamed from: org.jooq.impl.QuantifiedSelectImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QuantifiedSelectImpl$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QuantifiedSelectImpl(QOM.Quantifier quantifier, Select<R> query) {
        this.quantifier = quantifier;
        this.query = query;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.quantifier.keyword);
                ctx.sql(0 != 0 ? " ((" : " (");
                Tools.visitSubquery(ctx, this.query, 256, false);
                ctx.sql(0 != 0 ? "))" : ")");
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super QOM.Quantifier, ? super Select<R>, ? extends QOM.QuantifiedSelect<R>> $constructor() {
        return (q, s) -> {
            return new QuantifiedSelectImpl(q, s);
        };
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Quantifier $arg1() {
        return this.quantifier;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Select<R> $arg2() {
        return this.query;
    }
}

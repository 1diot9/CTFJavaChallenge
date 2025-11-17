package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Function0;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tau.class */
final class Tau extends AbstractField<BigDecimal> implements QOM.Tau {

    /* renamed from: org.jooq.impl.Tau$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tau$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Tau() {
        super(Names.N_TAU, Tools.allNotNull(SQLDataType.NUMERIC));
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Internal.imul(DSL.pi(), DSL.two()));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.Tau> $constructor() {
        return () -> {
            return new Tau();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Tau) {
            return true;
        }
        return super.equals(that);
    }
}

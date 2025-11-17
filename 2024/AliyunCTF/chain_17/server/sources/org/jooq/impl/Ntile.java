package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Ntile.class */
public final class Ntile extends AbstractWindowFunction<Integer> implements QOM.Ntile {
    private final Field<Integer> tiles;

    /* renamed from: org.jooq.impl.Ntile$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Ntile$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Ntile(Field<Integer> tiles) {
        super(Names.N_NTILE, SQLDataType.INTEGER.notNull());
        this.tiles = tiles;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Names.N_NTILE).sql('(').visit((Field<?>) this.tiles).sql(')');
                acceptOverClause(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.Ntile
    public final Field<Integer> $tiles() {
        return this.tiles;
    }
}

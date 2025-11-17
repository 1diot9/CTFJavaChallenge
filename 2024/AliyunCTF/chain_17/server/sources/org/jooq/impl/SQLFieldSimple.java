package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLFieldSimple.class */
final class SQLFieldSimple<T> extends AbstractField<T> implements SimpleQueryPart, QOM.UEmptyField<T> {
    private final QueryPart delegate;

    /* renamed from: org.jooq.impl.SQLFieldSimple$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLFieldSimple$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    SQLFieldSimple(DataType<T> type, SQL delegate) {
        super(DSL.unquotedName(delegate.toString()), type);
        this.delegate = delegate;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(this.delegate);
                return;
        }
    }
}

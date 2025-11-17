package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLField.class */
public final class SQLField<T> extends AbstractField<T> implements QOM.UEmptyField<T>, TypedReference<T> {
    final SQLImpl delegate;

    /* renamed from: org.jooq.impl.SQLField$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SQLField$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLField(DataType<T> type, SQL delegate) {
        super(DSL.unquotedName(delegate.toString()), type);
        this.delegate = (SQLImpl) delegate;
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

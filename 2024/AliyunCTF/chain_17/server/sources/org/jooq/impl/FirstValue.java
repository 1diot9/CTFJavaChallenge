package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FirstValue.class */
public final class FirstValue<T> extends AbstractWindowFunction<T> implements QOM.FirstValue<T> {
    final Field<T> field;

    /* renamed from: org.jooq.impl.FirstValue$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FirstValue$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FirstValue(Field<T> field) {
        super(Names.N_FIRST_VALUE, field.getDataType().null_());
        this.field = field;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Names.N_FIRST_VALUE).sql('(').visit((Field<?>) this.field);
                ctx.sql(')');
                acceptNullTreatment(ctx);
                acceptOverClause(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.FirstValue
    public final Field<T> $field() {
        return this.field;
    }
}

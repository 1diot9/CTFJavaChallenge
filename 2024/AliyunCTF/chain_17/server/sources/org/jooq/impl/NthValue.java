package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NthValue.class */
public final class NthValue<T> extends AbstractWindowFunction<T> implements QOM.NthValue<T> {
    final Field<T> field;
    final Field<Integer> offset;

    /* renamed from: org.jooq.impl.NthValue$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NthValue$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NthValue(Field<T> field, Field<Integer> offset) {
        super(Names.N_NTH_VALUE, field.getDataType().null_());
        this.field = field;
        this.offset = offset;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Names.N_NTH_VALUE).sql('(').visit((Field<?>) this.field).sql(", ").visit((Field<?>) this.offset);
                ctx.sql(')');
                acceptFromFirstOrLast(ctx);
                acceptNullTreatmentStandard(ctx);
                acceptOverClause(ctx);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.NthValue
    public final Field<T> $field() {
        return this.field;
    }
}

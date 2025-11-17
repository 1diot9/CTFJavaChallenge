package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.Cast;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TryCast.class */
public final class TryCast<T> extends AbstractField<T> implements QOM.TryCast<T> {
    final Field<?> value;
    final DataType<T> dataType;

    /* renamed from: org.jooq.impl.TryCast$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TryCast$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TryCast(Field<?> value, DataType<T> dataType) {
        super(Names.N_TRY_CAST, dataType);
        this.value = Tools.nullSafeNotNull(value, SQLDataType.OTHER);
        this.dataType = dataType;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(new Cast.CastNative(this.value, this.dataType, true));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<?> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final DataType<T> $arg2() {
        return this.dataType;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.TryCast<T> $arg1(Field<?> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.TryCast<T> $arg2(DataType<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<?>, ? super DataType<T>, ? extends QOM.TryCast<T>> $constructor() {
        return (a1, a2) -> {
            return new TryCast(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.TryCast)) {
            return super.equals(that);
        }
        QOM.TryCast<?> o = (QOM.TryCast) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($dataType(), o.$dataType());
    }
}

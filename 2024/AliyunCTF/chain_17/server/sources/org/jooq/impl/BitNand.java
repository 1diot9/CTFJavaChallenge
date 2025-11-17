package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitNand.class */
public final class BitNand<T extends Number> extends AbstractField<T> implements QOM.BitNand<T> {
    final Field<T> arg1;
    final Field<T> arg2;

    /* renamed from: org.jooq.impl.BitNand$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BitNand$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BitNand(Field<T> arg1, Field<T> arg2) {
        super(Names.N_BIT_NAND, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, arg1, false), arg1, arg2));
        this.arg1 = Tools.nullSafeNotNull(arg1, SQLDataType.INTEGER);
        this.arg2 = Tools.nullSafeNotNull(arg2, SQLDataType.INTEGER);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.bitNot(DSL.bitAnd(this.arg1, this.arg2)));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.arg2;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.BitNand<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.BitNand<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.BitNand<T>> $constructor() {
        return (a1, a2) -> {
            return new BitNand(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.BitNand)) {
            return super.equals(that);
        }
        QOM.BitNand<?> o = (QOM.BitNand) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Atan2.class */
public final class Atan2 extends AbstractField<BigDecimal> implements QOM.Atan2 {
    final Field<? extends Number> x;
    final Field<? extends Number> y;

    /* renamed from: org.jooq.impl.Atan2$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Atan2$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Atan2(Field<? extends Number> x, Field<? extends Number> y) {
        super(Names.N_ATAN2, Tools.allNotNull(SQLDataType.NUMERIC, x, y));
        this.x = Tools.nullSafeNotNull(x, SQLDataType.INTEGER);
        this.y = Tools.nullSafeNotNull(y, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.function(Names.N_ATAN2, getDataType(), (Field<?>[]) new Field[]{this.x, this.y}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg1() {
        return this.x;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<? extends Number> $arg2() {
        return this.y;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Atan2 $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Atan2 $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends QOM.Atan2> $constructor() {
        return (a1, a2) -> {
            return new Atan2(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Atan2)) {
            return super.equals(that);
        }
        QOM.Atan2 o = (QOM.Atan2) that;
        return StringUtils.equals($x(), o.$x()) && StringUtils.equals($y(), o.$y());
    }
}

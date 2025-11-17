package org.jooq.impl;

import java.math.BigDecimal;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Acos.class */
public final class Acos extends AbstractField<BigDecimal> implements QOM.Acos {
    final Field<? extends Number> value;

    /* renamed from: org.jooq.impl.Acos$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Acos$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Acos(Field<? extends Number> value) {
        super(Names.N_ACOS, Tools.allNotNull(SQLDataType.NUMERIC, value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.function(Names.N_ACOS, getDataType(), this.value));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<? extends Number> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Acos $arg1(Field<? extends Number> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<? extends Number>, ? extends QOM.Acos> $constructor() {
        return a1 -> {
            return new Acos(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Acos) {
            QOM.Acos o = (QOM.Acos) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

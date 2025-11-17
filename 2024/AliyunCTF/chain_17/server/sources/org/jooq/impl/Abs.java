package org.jooq.impl;

import java.lang.Number;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Abs.class */
public final class Abs<T extends Number> extends AbstractField<T> implements QOM.Abs<T> {
    final Field<T> value;

    /* renamed from: org.jooq.impl.Abs$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Abs$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Abs(Field<T> value) {
        super(Names.N_ABS, Tools.allNotNull(Tools.dataType(SQLDataType.INTEGER, value, false), (Field<?>) value));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.INTEGER);
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
                ctx.visit(DSL.function(Names.N_ABS, (DataType) getDataType(), (Field<?>) this.value));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<T> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.Abs<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<T>, ? extends QOM.Abs<T>> $constructor() {
        return a1 -> {
            return new Abs(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Abs) {
            QOM.Abs<?> o = (QOM.Abs) that;
            return StringUtils.equals($value(), o.$value());
        }
        return super.equals(that);
    }
}

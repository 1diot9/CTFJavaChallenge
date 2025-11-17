package org.jooq.impl;

import java.sql.Timestamp;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ToTimestamp.class */
public final class ToTimestamp extends AbstractField<Timestamp> implements QOM.ToTimestamp {
    final Field<String> value;
    final Field<String> formatMask;

    /* renamed from: org.jooq.impl.ToTimestamp$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ToTimestamp$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ToTimestamp(Field<String> value, Field<String> formatMask) {
        super(Names.N_TO_TIMESTAMP, Tools.allNotNull(SQLDataType.TIMESTAMP, value, formatMask));
        this.value = Tools.nullSafeNotNull(value, SQLDataType.VARCHAR);
        this.formatMask = Tools.nullSafeNotNull(formatMask, SQLDataType.VARCHAR);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(DSL.function(Names.N_TO_TIMESTAMP, getDataType(), (Field<?>[]) new Field[]{this.value, this.formatMask}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg2() {
        return this.formatMask;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ToTimestamp $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ToTimestamp $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<String>, ? super Field<String>, ? extends QOM.ToTimestamp> $constructor() {
        return (a1, a2) -> {
            return new ToTimestamp(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ToTimestamp)) {
            return super.equals(that);
        }
        QOM.ToTimestamp o = (QOM.ToTimestamp) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($formatMask(), o.$formatMask());
    }
}

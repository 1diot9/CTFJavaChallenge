package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Translate.class */
public final class Translate extends AbstractField<String> implements QOM.Translate {
    final Field<String> string;
    final Field<String> from;
    final Field<String> to;

    /* renamed from: org.jooq.impl.Translate$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Translate$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Translate(Field<String> string, Field<String> from, Field<String> to) {
        super(Names.N_TRANSLATE, Tools.allNotNull(SQLDataType.VARCHAR, string, from, to));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.from = Tools.nullSafeNotNull(from, SQLDataType.VARCHAR);
        this.to = Tools.nullSafeNotNull(to, SQLDataType.VARCHAR);
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
                ctx.visit(DSL.function(Names.N_TRANSLATE, getDataType(), (Field<?>[]) new Field[]{this.string, this.from, this.to}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg1() {
        return this.string;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg2() {
        return this.from;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg3() {
        return this.to;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Translate $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Translate $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Translate $arg3(Field<String> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<String>, ? super Field<String>, ? extends QOM.Translate> $constructor() {
        return (a1, a2, a3) -> {
            return new Translate(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Translate)) {
            return super.equals(that);
        }
        QOM.Translate o = (QOM.Translate) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($from(), o.$from()) && StringUtils.equals($to(), o.$to());
    }
}

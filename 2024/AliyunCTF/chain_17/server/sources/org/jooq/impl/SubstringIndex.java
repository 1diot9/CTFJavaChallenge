package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SubstringIndex.class */
public final class SubstringIndex extends AbstractField<String> implements QOM.SubstringIndex {
    final Field<String> string;
    final Field<String> delimiter;
    final Field<? extends Number> n;

    /* renamed from: org.jooq.impl.SubstringIndex$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SubstringIndex$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SubstringIndex(Field<String> string, Field<String> delimiter, Field<? extends Number> n) {
        super(Names.N_SUBSTRING_INDEX, Tools.allNotNull(SQLDataType.VARCHAR, string, delimiter, n));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.delimiter = Tools.nullSafeNotNull(delimiter, SQLDataType.VARCHAR);
        this.n = Tools.nullSafeNotNull(n, SQLDataType.INTEGER);
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
                ctx.visit(DSL.function(Names.N_SUBSTRING_INDEX, getDataType(), (Field<?>[]) new Field[]{this.string, this.delimiter, this.n}));
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
        return this.delimiter;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg3() {
        return this.n;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SubstringIndex $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SubstringIndex $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SubstringIndex $arg3(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<String>, ? super Field<? extends Number>, ? extends QOM.SubstringIndex> $constructor() {
        return (a1, a2, a3) -> {
            return new SubstringIndex(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.SubstringIndex)) {
            return super.equals(that);
        }
        QOM.SubstringIndex o = (QOM.SubstringIndex) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($delimiter(), o.$delimiter()) && StringUtils.equals($n(), o.$n());
    }
}

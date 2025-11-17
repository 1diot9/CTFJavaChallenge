package org.jooq.impl;

import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.LikeEscapeStep;
import org.jooq.QuantifiedSelect;
import org.jooq.Record1;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LikeQuantified.class */
final class LikeQuantified extends AbstractCondition implements QOM.LikeQuantified, LikeEscapeStep {
    final Field<?> value;
    final QuantifiedSelect<? extends Record1<String>> pattern;
    Character escape;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LikeQuantified(Field<?> value, QuantifiedSelect<? extends Record1<String>> pattern) {
        this(value, pattern, null);
    }

    LikeQuantified(Field<?> value, QuantifiedSelect<? extends Record1<String>> pattern, Character escape) {
        this.value = Tools.nullSafeNotNull(value, SQLDataType.OTHER);
        this.pattern = pattern;
        this.escape = escape;
    }

    @Override // org.jooq.LikeEscapeStep
    public final LikeQuantified escape(char escape) {
        this.escape = Character.valueOf(escape);
        return this;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        EqQuantified.acceptCompareCondition(ctx, this, this.value, Comparator.LIKE, this.pattern, this.escape);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<?> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final QuantifiedSelect<? extends Record1<String>> $arg2() {
        return this.pattern;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Character $arg3() {
        return this.escape;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.LikeQuantified $arg1(Field<?> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.LikeQuantified $arg2(QuantifiedSelect<? extends Record1<String>> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.LikeQuantified $arg3(Character newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<?>, ? super QuantifiedSelect<? extends Record1<String>>, ? super Character, ? extends QOM.LikeQuantified> $constructor() {
        return (a1, a2, a3) -> {
            return new LikeQuantified(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.LikeQuantified)) {
            return super.equals(that);
        }
        QOM.LikeQuantified o = (QOM.LikeQuantified) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($pattern(), o.$pattern()) && StringUtils.equals($escape(), o.$escape());
    }
}

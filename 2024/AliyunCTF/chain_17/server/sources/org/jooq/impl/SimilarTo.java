package org.jooq.impl;

import org.jooq.Comparator;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.LikeEscapeStep;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SimilarTo.class */
public final class SimilarTo extends AbstractCondition implements QOM.SimilarTo, LikeEscapeStep {
    final Field<?> value;
    final Field<String> pattern;
    Character escape;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimilarTo(Field<?> value, Field<String> pattern) {
        this(value, pattern, null);
    }

    SimilarTo(Field<?> value, Field<String> pattern, Character escape) {
        this.value = Tools.nullableIf(false, Tools.nullSafe(value, pattern.getDataType()));
        this.pattern = Tools.nullableIf(false, Tools.nullSafe(pattern, value.getDataType()));
    }

    @Override // org.jooq.LikeEscapeStep
    public final SimilarTo escape(char escape) {
        this.escape = Character.valueOf(escape);
        return this;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Like.accept0(ctx, this.value, Comparator.SIMILAR_TO, this.pattern, this.escape);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<?> $arg1() {
        return this.value;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg2() {
        return this.pattern;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Character $arg3() {
        return this.escape;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SimilarTo $arg1(Field<?> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SimilarTo $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.SimilarTo $arg3(Character newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<?>, ? super Field<String>, ? super Character, ? extends QOM.SimilarTo> $constructor() {
        return (a1, a2, a3) -> {
            return new SimilarTo(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.SimilarTo)) {
            return super.equals(that);
        }
        QOM.SimilarTo o = (QOM.SimilarTo) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($pattern(), o.$pattern()) && StringUtils.equals($escape(), o.$escape());
    }
}

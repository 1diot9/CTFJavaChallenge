package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ContainsIgnoreCase.class */
public final class ContainsIgnoreCase<T> extends AbstractCondition implements QOM.ContainsIgnoreCase<T> {
    final Field<T> value;
    final Field<T> content;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ContainsIgnoreCase(Field<T> value, Field<T> content) {
        this.value = Tools.nullableIf(false, Tools.nullSafe((Field) value, (DataType<?>) content.getDataType()));
        this.content = Tools.nullableIf(false, Tools.nullSafe((Field) content, (DataType<?>) value.getDataType()));
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                ctx.visit(this.value.lower().contains(this.content.lower()));
                return;
            default:
                ctx.visit(this.value.likeIgnoreCase(DSL.concat((Field<?>[]) new Field[]{DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL), Tools.escapeForLike(this.content, ctx.configuration()), DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)}), '!'));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.content;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ContainsIgnoreCase<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.ContainsIgnoreCase<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.ContainsIgnoreCase<T>> $constructor() {
        return (a1, a2) -> {
            return new ContainsIgnoreCase(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.ContainsIgnoreCase)) {
            return super.equals(that);
        }
        QOM.ContainsIgnoreCase<?> o = (QOM.ContainsIgnoreCase) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($content(), o.$content());
    }
}

package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/StartsWithIgnoreCase.class */
final class StartsWithIgnoreCase<T> extends AbstractCondition implements QOM.StartsWithIgnoreCase<T> {
    final Field<T> string;
    final Field<T> prefix;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartsWithIgnoreCase(Field<T> string, Field<T> prefix) {
        this.string = Tools.nullableIf(false, Tools.nullSafe((Field) string, (DataType<?>) prefix.getDataType()));
        this.prefix = Tools.nullableIf(false, Tools.nullSafe((Field) prefix, (DataType<?>) string.getDataType()));
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case TRINO:
                ctx.visit(this.string.lower().startsWith(this.prefix.lower()));
                return;
            default:
                ctx.visit(this.string.likeIgnoreCase(DSL.concat((Field<?>[]) new Field[]{Tools.escapeForLike(this.prefix, ctx.configuration()), DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)}), '!'));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.string;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.prefix;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.StartsWithIgnoreCase<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.StartsWithIgnoreCase<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.StartsWithIgnoreCase<T>> $constructor() {
        return (a1, a2) -> {
            return new StartsWithIgnoreCase(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.StartsWithIgnoreCase)) {
            return super.equals(that);
        }
        QOM.StartsWithIgnoreCase<?> o = (QOM.StartsWithIgnoreCase) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($prefix(), o.$prefix());
    }
}

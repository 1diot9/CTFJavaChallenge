package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/EndsWithIgnoreCase.class */
final class EndsWithIgnoreCase<T> extends AbstractCondition implements QOM.EndsWithIgnoreCase<T> {
    final Field<T> string;
    final Field<T> suffix;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EndsWithIgnoreCase(Field<T> string, Field<T> suffix) {
        this.string = Tools.nullableIf(false, Tools.nullSafe((Field) string, (DataType<?>) suffix.getDataType()));
        this.suffix = Tools.nullableIf(false, Tools.nullSafe((Field) suffix, (DataType<?>) string.getDataType()));
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                ctx.visit(this.string.lower().endsWith(this.suffix.lower()));
                return;
            default:
                ctx.visit(this.string.likeIgnoreCase(DSL.concat((Field<?>[]) new Field[]{DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL), Tools.escapeForLike(this.suffix, ctx.configuration())}), '!'));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg1() {
        return this.string;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.suffix;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.EndsWithIgnoreCase<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.EndsWithIgnoreCase<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.EndsWithIgnoreCase<T>> $constructor() {
        return (a1, a2) -> {
            return new EndsWithIgnoreCase(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.EndsWithIgnoreCase)) {
            return super.equals(that);
        }
        QOM.EndsWithIgnoreCase<?> o = (QOM.EndsWithIgnoreCase) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($suffix(), o.$suffix());
    }
}

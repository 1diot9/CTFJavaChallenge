package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.JSONB;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Contains.class */
public final class Contains<T> extends AbstractCondition implements QOM.Contains<T> {
    final Field<T> value;
    final Field<T> content;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Contains(Field<T> value, Field<T> content) {
        this.value = Tools.nullableIf(false, Tools.nullSafe((Field) value, (DataType<?>) content.getDataType()));
        this.content = Tools.nullableIf(false, Tools.nullSafe((Field) content, (DataType<?>) value.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                return false;
            case DUCKDB:
                return true;
            default:
                return false;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                if (this.value.getDataType().isArray() || this.content.getDataType().isArray()) {
                    ctx.visit((Field<?>) this.value).sql(" @> ").visit((Field<?>) this.content);
                    return;
                } else if (this.value.getDataType().getType() == JSONB.class || this.content.getDataType().getType() == JSONB.class) {
                    ctx.visit((Field<?>) this.value).sql(" @> ").visit((Field<?>) this.content);
                    return;
                } else {
                    acceptDefault(ctx);
                    return;
                }
            case DUCKDB:
                ctx.visit(DSL.function(Names.N_CONTAINS, SQLDataType.BOOLEAN, (Field<?>[]) new Field[]{this.value, this.content}));
                return;
            default:
                acceptDefault(ctx);
                return;
        }
    }

    private final void acceptDefault(Context<?> ctx) {
        ctx.visit(this.value.like(DSL.concat((Field<?>[]) new Field[]{DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL), Tools.escapeForLike(this.content, ctx.configuration()), DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)}), '!'));
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
    public final QOM.Contains<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.Contains<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.Contains<T>> $constructor() {
        return (a1, a2) -> {
            return new Contains(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Contains)) {
            return super.equals(that);
        }
        QOM.Contains<?> o = (QOM.Contains) that;
        return StringUtils.equals($value(), o.$value()) && StringUtils.equals($content(), o.$content());
    }
}

package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/StartsWith.class */
final class StartsWith<T> extends AbstractCondition implements QOM.StartsWith<T> {
    final Field<T> string;
    final Field<T> prefix;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartsWith(Field<T> string, Field<T> prefix) {
        this.string = Tools.nullableIf(false, Tools.nullSafe((Field) string, (DataType<?>) prefix.getDataType()));
        this.prefix = Tools.nullableIf(false, Tools.nullSafe((Field) prefix, (DataType<?>) string.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                return false;
            case DUCKDB:
            case TRINO:
                return true;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case CUBRID:
            case DERBY:
            case FIREBIRD:
            case H2:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                ctx.visit(this.string.like(DSL.concat((Field<?>[]) new Field[]{Tools.escapeForLike(this.prefix, ctx.configuration()), DSL.inline(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)}), '!'));
                return;
            case DUCKDB:
            case TRINO:
                ctx.visit(DSL.function(Names.N_STARTS_WITH, SQLDataType.BOOLEAN, (Field<?>[]) new Field[]{this.string, this.prefix}));
                return;
            default:
                ctx.visit(DSL.function(Names.N_STARTS_WITH, SQLDataType.BOOLEAN, (Field<?>[]) new Field[]{this.string, this.prefix}));
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
    public final QOM.StartsWith<T> $arg1(Field<T> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.StartsWith<T> $arg2(Field<T> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<T>, ? super Field<T>, ? extends QOM.StartsWith<T>> $constructor() {
        return (a1, a2) -> {
            return new StartsWith(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.StartsWith)) {
            return super.equals(that);
        }
        QOM.StartsWith<?> o = (QOM.StartsWith) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($prefix(), o.$prefix());
    }
}

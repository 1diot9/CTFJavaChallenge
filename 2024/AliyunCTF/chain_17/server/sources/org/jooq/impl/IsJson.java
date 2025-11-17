package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IsJson.class */
final class IsJson extends AbstractCondition implements QOM.IsJson {
    final Field<?> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IsJson(Field<?> field) {
        this.field = Tools.nullSafeNotNull(field, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case MARIADB:
            case MYSQL:
                return true;
            default:
                return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case MARIADB:
            case MYSQL:
                ctx.visit(DSL.function(Names.N_JSON_VALID, SQLDataType.BOOLEAN, this.field));
                return;
            default:
                ctx.visit(this.field).sql(' ').visit(Keywords.K_IS_JSON);
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<?> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.IsJson $arg1(Field<?> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<?>, ? extends QOM.IsJson> $constructor() {
        return a1 -> {
            return new IsJson(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.IsJson) {
            QOM.IsJson o = (QOM.IsJson) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

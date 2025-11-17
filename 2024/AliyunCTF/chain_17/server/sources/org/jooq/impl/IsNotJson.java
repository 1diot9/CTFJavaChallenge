package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/IsNotJson.class */
final class IsNotJson extends AbstractCondition implements QOM.IsNotJson {
    final Field<?> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public IsNotJson(Field<?> field) {
        this.field = Tools.nullSafeNotNull(field, SQLDataType.OTHER);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
            case MARIADB:
            case MYSQL:
                ctx.visit(DSL.condition((Field<Boolean>) DSL.function(Names.N_JSON_VALID, SQLDataType.BOOLEAN, this.field)).not());
                return;
            default:
                ctx.visit(this.field).sql(' ').visit(Keywords.K_IS_NOT_JSON);
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<?> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.IsNotJson $arg1(Field<?> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<?>, ? extends QOM.IsNotJson> $constructor() {
        return a1 -> {
            return new IsNotJson(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.IsNotJson) {
            QOM.IsNotJson o = (QOM.IsNotJson) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

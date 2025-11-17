package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.JSON;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONGetAttribute.class */
public final class JSONGetAttribute extends AbstractField<JSON> implements QOM.JSONGetAttribute {
    final Field<JSON> field;
    final Field<String> attribute;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONGetAttribute(Field<JSON> field, Field<String> attribute) {
        super(Names.N_JSON_GET_ATTRIBUTE, Tools.allNotNull(SQLDataType.JSON, field, attribute));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSON);
        this.attribute = Tools.nullSafeNotNull(attribute, SQLDataType.VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
            case TRINO:
                return false;
            default:
                return false;
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
            case TRINO:
                ctx.visit(DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSON, (Field<?>[]) new Field[]{this.field, DSL.inline("$.").concat(this.attribute)}));
                return;
            default:
                ctx.sql('(').visit(this.field).sql("->").visit((Field<?>) this.attribute).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<JSON> $arg1() {
        return this.field;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg2() {
        return this.attribute;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONGetAttribute $arg1(Field<JSON> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONGetAttribute $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<JSON>, ? super Field<String>, ? extends QOM.JSONGetAttribute> $constructor() {
        return (a1, a2) -> {
            return new JSONGetAttribute(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONGetAttribute)) {
            return super.equals(that);
        }
        QOM.JSONGetAttribute o = (QOM.JSONGetAttribute) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($attribute(), o.$attribute());
    }
}

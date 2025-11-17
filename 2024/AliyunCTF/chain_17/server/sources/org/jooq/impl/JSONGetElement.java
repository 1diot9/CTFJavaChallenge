package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.JSON;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONGetElement.class */
public final class JSONGetElement extends AbstractField<JSON> implements QOM.JSONGetElement {
    final Field<JSON> field;
    final Field<Integer> index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONGetElement(Field<JSON> field, Field<Integer> index) {
        super(Names.N_JSON_GET_ELEMENT, Tools.allNotNull(SQLDataType.JSON, field, index));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSON);
        this.index = Tools.nullSafeNotNull(index, SQLDataType.INTEGER);
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
                ctx.visit(DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSON, (Field<?>[]) new Field[]{this.field, DSL.inline("$[").concat(this.index).concat(DSL.inline("]"))}));
                return;
            default:
                ctx.sql('(').visit(this.field).sql("->").visit((Field<?>) this.index).sql(')');
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
    public final Field<Integer> $arg2() {
        return this.index;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONGetElement $arg1(Field<JSON> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONGetElement $arg2(Field<Integer> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<JSON>, ? super Field<Integer>, ? extends QOM.JSONGetElement> $constructor() {
        return (a1, a2) -> {
            return new JSONGetElement(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONGetElement)) {
            return super.equals(that);
        }
        QOM.JSONGetElement o = (QOM.JSONGetElement) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($index(), o.$index());
    }
}

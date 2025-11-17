package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.JSONB;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONBGetAttributeAsText.class */
public final class JSONBGetAttributeAsText extends AbstractField<String> implements QOM.JSONBGetAttributeAsText {
    final Field<JSONB> field;
    final Field<String> attribute;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONBGetAttributeAsText(Field<JSONB> field, Field<String> attribute) {
        super(Names.N_JSONB_GET_ATTRIBUTE_AS_TEXT, Tools.allNotNull(SQLDataType.VARCHAR, field, attribute));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSONB);
        this.attribute = Tools.nullSafeNotNull(attribute, SQLDataType.VARCHAR);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MYSQL:
                ctx.visit(DSL.function(Names.N_JSON_UNQUOTE, SQLDataType.JSONB, (Field<?>) DSL.nullif(DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSONB, (Field<?>[]) new Field[]{this.field, DSL.inline("$.").concat(this.attribute)}), (Field) DSL.inline("null").cast(SQLDataType.JSONB))));
                return;
            case MARIADB:
                ctx.visit(DSL.function(Names.N_JSON_UNQUOTE, SQLDataType.JSONB, (Field<?>) DSL.nullif(DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSONB, (Field<?>[]) new Field[]{this.field, DSL.inline("$.").concat(this.attribute)}).cast(SQLDataType.VARCHAR), (Field) DSL.inline("null"))));
                return;
            case SQLITE:
                ctx.visit(DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSONB, (Field<?>[]) new Field[]{this.field, DSL.inline("$.").concat(this.attribute)}));
                return;
            default:
                ctx.sql('(').visit(this.field).sql("->>").visit((Field<?>) this.attribute).sql(')');
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<JSONB> $arg1() {
        return this.field;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<String> $arg2() {
        return this.attribute;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONBGetAttributeAsText $arg1(Field<JSONB> newValue) {
        return $constructor().apply(newValue, $arg2());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.JSONBGetAttributeAsText $arg2(Field<String> newValue) {
        return $constructor().apply($arg1(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super Field<JSONB>, ? super Field<String>, ? extends QOM.JSONBGetAttributeAsText> $constructor() {
        return (a1, a2) -> {
            return new JSONBGetAttributeAsText(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONBGetAttributeAsText)) {
            return super.equals(that);
        }
        QOM.JSONBGetAttributeAsText o = (QOM.JSONBGetAttributeAsText) that;
        return StringUtils.equals($field(), o.$field()) && StringUtils.equals($attribute(), o.$attribute());
    }
}

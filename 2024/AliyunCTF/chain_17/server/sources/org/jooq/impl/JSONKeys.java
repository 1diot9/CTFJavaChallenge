package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONKeys.class */
public final class JSONKeys extends AbstractField<JSON> implements QOM.JSONKeys {
    final Field<JSON> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONKeys(Field<JSON> field) {
        super(Names.N_JSON_KEYS, Tools.allNotNull(SQLDataType.JSON, field));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSON);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                return false;
            case SQLITE:
                return false;
            case TRINO:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(DSL.field(DSL.select(DSL.coalesce((Field) DSL.jsonArrayAgg(DSL.field(DSL.unquotedName("j"))), (Field<?>[]) new Field[]{DSL.jsonArray((Field<?>[]) new Field[0])})).from("json_object_keys({0}) as j(j)", this.field)));
                return;
            case SQLITE:
                ctx.visit(DSL.field(DSL.select(DSL.jsonArrayAgg(DSL.field(DSL.name("key")))).from("json_each({0})", this.field)));
                return;
            case TRINO:
                ctx.visit(DSL.cast((Field<?>) DSL.function(Names.N_MAP_KEYS, SQLDataType.OTHER, (Field<?>) DSL.field("cast({0} as map(varchar, json))", (DataType) SQLDataType.OTHER, this.field)), (DataType) SQLDataType.JSON));
                return;
            default:
                ctx.visit(DSL.function(Names.N_JSON_KEYS, getDataType(), this.field));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<JSON> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.JSONKeys $arg1(Field<JSON> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<JSON>, ? extends QOM.JSONKeys> $constructor() {
        return a1 -> {
            return new JSONKeys(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.JSONKeys) {
            QOM.JSONKeys o = (QOM.JSONKeys) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

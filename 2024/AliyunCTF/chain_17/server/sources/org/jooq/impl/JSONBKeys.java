package org.jooq.impl;

import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONBKeys.class */
public final class JSONBKeys extends AbstractField<JSONB> implements QOM.JSONBKeys {
    static final Set<SQLDialect> NO_SUPPORT_PATH_QUERY = SQLDialect.supportedUntil(new SQLDialect[0]);
    final Field<JSONB> field;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONBKeys(Field<JSONB> field) {
        super(Names.N_JSONB_KEYS, Tools.allNotNull(SQLDataType.JSONB, field));
        this.field = Tools.nullSafeNotNull(field, SQLDataType.JSONB);
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
                return false;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                if (NO_SUPPORT_PATH_QUERY.contains(ctx.dialect())) {
                    ctx.visit(DSL.field(DSL.select(DSL.coalesce((Field) DSL.jsonArrayAgg(DSL.field(DSL.unquotedName("j"))), (Field<?>[]) new Field[]{DSL.jsonArray((Field<?>[]) new Field[0])})).from("json_object_keys({0}) as j(j)", this.field)));
                    return;
                } else {
                    ctx.visit(DSL.function(Names.N_JSONB_PATH_QUERY_ARRAY, getDataType(), (Field<?>[]) new Field[]{this.field, DSL.inline("$.keyvalue().key")}));
                    return;
                }
            case SQLITE:
                ctx.visit(DSL.field(DSL.select(DSL.jsonbArrayAgg(DSL.field(DSL.name("key")))).from("json_each({0})", this.field)));
                return;
            case TRINO:
                ctx.visit(DSL.cast((Field<?>) DSL.function(Names.N_MAP_KEYS, SQLDataType.OTHER, (Field<?>) DSL.field("cast({0} as map(varchar, json))", (DataType) SQLDataType.OTHER, this.field)), (DataType) SQLDataType.JSON));
                return;
            default:
                ctx.visit(DSL.function(Names.N_JSON_KEYS, SQLDataType.JSONB, this.field));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<JSONB> $arg1() {
        return this.field;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final QOM.JSONBKeys $arg1(Field<JSONB> newValue) {
        return $constructor().apply(newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<JSONB>, ? extends QOM.JSONBKeys> $constructor() {
        return a1 -> {
            return new JSONBKeys(a1);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.JSONBKeys) {
            QOM.JSONBKeys o = (QOM.JSONBKeys) that;
            return StringUtils.equals($field(), o.$field());
        }
        return super.equals(that);
    }
}

package org.jooq.impl;

import java.util.UUID;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function0;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Uuid.class */
public final class Uuid extends AbstractField<UUID> implements QOM.Uuid {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Uuid() {
        super(Names.N_UUID, Tools.allNotNull(SQLDataType.UUID));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean parenthesised(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
                return true;
            case FIREBIRD:
                return false;
            case H2:
                return true;
            case DUCKDB:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case TRINO:
                return true;
            case SQLITE:
                return false;
            default:
                return true;
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
                ctx.visit(DSL.function(Names.N_GEN_RANDOM_UUID, getDataType(), (Field<?>[]) new Field[0]));
                return;
            case FIREBIRD:
                ctx.visit(DSL.function(Names.N_UUID_TO_CHAR, getDataType(), (Field<?>) DSL.function(Names.N_GEN_UUID, getDataType(), (Field<?>[]) new Field[0])));
                return;
            case H2:
                ctx.visit(DSL.function(Names.N_RANDOM_UUID, getDataType(), (Field<?>[]) new Field[0]));
                return;
            case DUCKDB:
            case HSQLDB:
            case IGNITE:
            case MARIADB:
            case MYSQL:
            case TRINO:
                ctx.visit(DSL.function(Names.N_UUID, getDataType(), (Field<?>[]) new Field[0]));
                return;
            case SQLITE:
                Field<String> u = DSL.field(DSL.name("u"), SQLDataType.VARCHAR);
                ctx.visit(DSL.field(DSL.select(DSL.substring(u, DSL.inline(1), DSL.inline(8)).concat(DSL.inline('-')).concat(DSL.substring(u, DSL.inline(9), DSL.inline(4)).concat(DSL.inline('-'))).concat(DSL.substring(u, DSL.inline(13), DSL.inline(4)).concat(DSL.inline('-'))).concat(DSL.substring(u, DSL.inline(17), DSL.inline(4)).concat(DSL.inline('-'))).concat(DSL.substring(u, DSL.inline(21)))).from(DSL.select(DSL.lower((Field<String>) DSL.function(Names.N_HEX, SQLDataType.VARCHAR, (Field<?>) DSL.function(Names.N_RANDOMBLOB, SQLDataType.BINARY, DSL.inline(16)))).as((Field<?>) u)).asTable(DSL.unquotedName("t")))));
                return;
            default:
                ctx.visit(DSL.function(Names.N_UUID, getDataType(), (Field<?>[]) new Field[0]));
                return;
        }
    }

    @Override // org.jooq.impl.QOM.UOperator0
    public final Function0<? extends QOM.Uuid> $constructor() {
        return () -> {
            return new Uuid();
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof QOM.Uuid) {
            return true;
        }
        return super.equals(that);
    }
}

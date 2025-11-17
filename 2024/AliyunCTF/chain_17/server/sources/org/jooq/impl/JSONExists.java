package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.JSONExistsOnStep;
import org.jooq.Keyword;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONExists.class */
public final class JSONExists extends AbstractCondition implements JSONExistsOnStep, QOM.UNotYetImplemented {
    private final Field<?> json;
    private final Field<String> path;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONExists(Field<?> json, Field<String> path) {
        this(json, path, null);
    }

    private JSONExists(Field<?> json, Field<String> path, Behaviour onError) {
        this.json = json;
        this.path = path;
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MYSQL:
                ctx.visit(Names.N_JSON_CONTAINS_PATH).sql('(').visit(this.json).sql(", 'one', ").visit((Field<?>) this.path).sql(')');
                return;
            case SQLITE:
                ctx.visit(DSL.function(Names.N_JSON_TYPE, SQLDataType.JSON, (Field<?>[]) new Field[]{this.json, this.path}).isNotNull());
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(Names.N_JSONB_PATH_EXISTS).sql('(').visit(Tools.castIfNeeded(this.json, SQLDataType.JSONB)).sql(", ");
                Cast.renderCast(ctx, c -> {
                    c.visit((Field<?>) this.path);
                }, c2 -> {
                    c2.visit(Names.N_JSONPATH);
                });
                ctx.sql(')');
                return;
            default:
                ctx.visit(Keywords.K_JSON_EXISTS).sql('(').visit(this.json).sql(", ");
                ctx.visit((Field<?>) this.path);
                ctx.sql(')');
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONExists$Behaviour.class */
    public enum Behaviour {
        ERROR,
        TRUE,
        FALSE,
        UNKNOWN;

        final Keyword keyword = DSL.keyword(name().toLowerCase());

        Behaviour() {
        }
    }
}

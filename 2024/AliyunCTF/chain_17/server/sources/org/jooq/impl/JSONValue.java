package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSONValueDefaultStep;
import org.jooq.JSONValueOnStep;
import org.jooq.Keyword;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONValue.class */
public final class JSONValue<J> extends AbstractField<J> implements JSONValueOnStep<J>, JSONValueDefaultStep<J>, QOM.UNotYetImplemented {
    private final Field<?> json;
    private final Field<String> path;
    private final DataType<?> returning;
    private final Behaviour onError;
    private final Field<?> onErrorDefault;
    private final Behaviour onEmpty;
    private final Field<?> onEmptyDefault;
    private final Field<?> default_;

    @Override // org.jooq.JSONValueReturningStep
    public /* bridge */ /* synthetic */ Field returning(DataType dataType) {
        return returning((DataType<?>) dataType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONValue(DataType<J> type, Field<?> json, Field<String> path, DataType<?> returning) {
        this(type, json, path, returning, null, null, null, null, null);
    }

    private JSONValue(DataType<J> type, Field<?> json, Field<String> path, DataType<?> returning, Behaviour onError, Field<?> onErrorDefault, Behaviour onEmpty, Field<?> onEmptyDefault, Field<?> default_) {
        super(Names.N_JSON_VALUE, type);
        this.json = json;
        this.path = path;
        this.returning = returning;
        this.onError = onError;
        this.onErrorDefault = onErrorDefault;
        this.onEmpty = onEmpty;
        this.onEmptyDefault = onEmptyDefault;
        this.default_ = default_;
    }

    @Override // org.jooq.JSONValueReturningStep
    public final JSONValue<J> returning(DataType<?> r) {
        return new JSONValue<>(getDataType(), this.json, this.path, r, this.onError, this.onErrorDefault, this.onEmpty, this.onEmptyDefault, null);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MYSQL:
            case SQLITE:
                ctx.visit(DSL.function(Names.N_JSON_EXTRACT, this.json.getDataType(), (Field<?>[]) new Field[]{this.json, this.path}));
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(DSL.function(Names.N_JSONB_PATH_QUERY_FIRST, this.json.getDataType(), (Field<?>[]) new Field[]{Tools.castIfNeeded(this.json, SQLDataType.JSONB), DSL.field("cast({0} as jsonpath)", this.path)}));
                return;
            default:
                acceptDefault(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v28, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void acceptDefault(Context<?> ctx) {
        boolean format = !Tools.isSimple(ctx, this.json, this.path);
        ctx.visit(Names.N_JSON_VALUE).sql('(');
        if (format) {
            ctx.sqlIndentStart();
        }
        ctx.visit(this.json).sql(",");
        if (format) {
            ctx.formatSeparator();
        } else {
            ctx.sql(' ');
        }
        ctx.visit((Field<?>) this.path);
        if (this.returning != null) {
            JSONReturning r = new JSONReturning(this.returning);
            if (r.rendersContent(ctx)) {
                if (format) {
                    ctx.formatNewLine();
                }
                ctx.separatorRequired(true).visit(r);
            }
        }
        if (format) {
            ctx.sqlIndentEnd();
        }
        ctx.sql(')');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONValue$Behaviour.class */
    public enum Behaviour {
        ERROR,
        NULL,
        DEFAULT;

        final Keyword keyword = DSL.keyword(name().toLowerCase());

        Behaviour() {
        }
    }
}

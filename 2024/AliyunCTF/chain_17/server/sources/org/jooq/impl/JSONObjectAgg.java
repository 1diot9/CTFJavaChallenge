package org.jooq.impl;

import java.util.function.Consumer;
import org.jooq.AggregateFilterStep;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONEntry;
import org.jooq.JSONObjectAggNullStep;
import org.jooq.JSONObjectNullStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONObjectAgg.class */
public final class JSONObjectAgg<J> extends AbstractAggregateFunction<J> implements JSONObjectAggNullStep<J>, QOM.JSONObjectAgg<J> {
    private final JSONEntry<?> entry;
    private QOM.JSONOnNull onNull;
    private DataType<?> returning;

    @Override // org.jooq.JSONObjectAggReturningStep
    public /* bridge */ /* synthetic */ AggregateFilterStep returning(DataType dataType) {
        return returning((DataType<?>) dataType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONObjectAgg(DataType<J> type, JSONEntry<?> entry) {
        super(false, Names.N_JSON_OBJECTAGG, (DataType) type, (Field<?>[]) new Field[]{entry.key(), entry.value()});
        this.entry = entry;
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                acceptPostgres(ctx);
                return;
            case MARIADB:
            case MYSQL:
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL || this.filter.hasWhere()) {
                    acceptGroupConcat(ctx);
                    return;
                } else {
                    acceptStandard(ctx);
                    return;
                }
            case SQLITE:
                acceptSQLite(ctx);
                return;
            case TRINO:
                acceptTrino(ctx);
                return;
            default:
                acceptStandard(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v30, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void acceptTrino(Context<?> ctx) {
        ctx.visit(Names.N_CAST).sql('(');
        boolean noAggregateFilter = this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL && !supportsFilter(ctx);
        if (noAggregateFilter) {
            ctx.visit(Names.N_MAP_FILTER).sql('(');
        }
        ctx.visit(Names.N_MAP).sql('(');
        acceptTrinoArrayAgg(ctx, this.entry.key(), this.entry.value());
        ctx.sql(", ");
        acceptTrinoArrayAgg(ctx, this.entry.value(), this.entry.value());
        ctx.sql(')');
        if (noAggregateFilter) {
            ctx.sql(", (k, v) -> v ").visit(Keywords.K_IS_NOT_NULL).sql(')');
        }
        ctx.sql(' ').visit(Keywords.K_AS).sql(' ').visit(SQLDataType.JSON);
        ctx.sql(')');
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptTrinoArrayAgg(Context<?> ctx, Field<?> f1, Field<?> f2) {
        ctx.visit(Names.N_ARRAY_AGG).sql('(');
        ctx.visit(JSONEntryImpl.jsonCast(ctx, f1));
        ctx.sql(")");
        if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
            acceptFilterClause(ctx, f(f2.isNotNull()));
        } else {
            acceptFilterClause(ctx);
        }
        acceptOverClause(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptPostgres(Context<?> ctx) {
        ctx.visit(getDataType() == SQLDataType.JSON ? Names.N_JSON_OBJECT_AGG : Names.N_JSONB_OBJECT_AGG).sql('(');
        ctx.visit(this.entry);
        ctx.sql(')');
        if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
            acceptFilterClause(ctx, f(this.entry.value().isNotNull()));
        } else {
            acceptFilterClause(ctx);
        }
        acceptOverClause(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptSQLite(Context<?> ctx) {
        ctx.visit(Names.N_JSON_GROUP_OBJECT).sql('(');
        ctx.visit(this.entry);
        ctx.sql(')');
        if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
            acceptFilterClause(ctx, f(this.entry.value().isNotNull()));
        } else {
            acceptFilterClause(ctx);
        }
        acceptOverClause(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptGroupConcat(Context<?> ctx) {
        ctx.sql('(').visit(groupConcatEmulation(ctx)).sql(')');
    }

    private final Field<?> groupConcatEmulation(Context<?> ctx) {
        Field<String> listagg = CustomField.of(Names.N_GROUP_CONCAT, SQLDataType.VARCHAR, (Consumer<? super Context<?>>) c1 -> {
            JSONObjectNullStep<JSON> jsonObject = DSL.jsonObject(this.entry.key(), this.entry.value());
            if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                jsonObject = DSL.when(this.entry.value().isNull(), (Field) DSL.inline((JSON) null)).else_((Field) jsonObject);
            }
            Field<JSON> o2 = jsonObject;
            c1.visit((Field<?>) DSL.groupConcat(DSL.concat((Field<?>[]) new Field[]{CustomField.of(Names.N_FIELD, SQLDataType.VARCHAR, (Consumer<? super Context<?>>) c2 -> {
                acceptArguments2(c2, QueryPartListView.wrap(DSL.regexpReplaceAll((Field<String>) o2.cast(SQLDataType.VARCHAR), DSL.inline("^\\{(.*)\\}$"), DSL.inline(RegexpReplace.replacement(ctx, 1)))));
            })})));
            acceptFilterClause(c1);
            acceptOverClause(c1);
        });
        Field<String> result = DSL.concat((Field<?>[]) new Field[]{DSL.inline('{'), listagg, DSL.inline('}')});
        switch (ctx.family()) {
            default:
                return result;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        ctx.visit(Names.N_JSON_OBJECTAGG).sql('(').visit(this.entry);
        JSONNull jsonNull = new JSONNull(this.onNull);
        if (jsonNull.rendersContent(ctx)) {
            ctx.sql(' ').visit(jsonNull);
        }
        JSONReturning jsonReturning = new JSONReturning(this.returning);
        if (jsonReturning.rendersContent(ctx)) {
            ctx.sql(' ').visit(jsonReturning);
        }
        ctx.sql(')');
        acceptFilterClause(ctx);
        acceptOverClause(ctx);
    }

    @Override // org.jooq.JSONObjectAggNullStep
    public final JSONObjectAgg<J> nullOnNull() {
        this.onNull = QOM.JSONOnNull.NULL_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONObjectAggNullStep
    public final JSONObjectAgg<J> absentOnNull() {
        this.onNull = QOM.JSONOnNull.ABSENT_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONObjectAggReturningStep
    public final JSONObjectAgg<J> returning(DataType<?> r) {
        this.returning = r;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final JSONEntry<?> $arg1() {
        return this.entry;
    }

    @Override // org.jooq.impl.QOM.JSONObjectAgg
    public final QOM.JSONOnNull $onNull() {
        return this.onNull;
    }

    @Override // org.jooq.impl.QOM.JSONObjectAgg
    public final DataType<?> $returning() {
        return this.returning;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super JSONEntry<?>, ? extends QOM.JSONObjectAgg<J>> $constructor() {
        return e -> {
            JSONObjectAgg<J> r = new JSONObjectAgg<>(getDataType(), e);
            r.onNull = this.onNull;
            r.returning = this.returning;
            return r;
        };
    }
}

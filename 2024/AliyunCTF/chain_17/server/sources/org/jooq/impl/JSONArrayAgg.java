package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import org.jooq.AggregateFilterStep;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSONArrayAggNullStep;
import org.jooq.JSONArrayAggOrderByStep;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Select;
import org.jooq.SelectGroupByStep;
import org.jooq.WindowRowsStep;
import org.jooq.impl.QOM;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONArrayAgg.class */
public final class JSONArrayAgg<J> extends AbstractAggregateFunction<J> implements JSONArrayAggOrderByStep<J>, QOM.JSONArrayAgg<J> {
    static final Set<SQLDialect> EMULATE_WITH_GROUP_CONCAT = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private QOM.JSONOnNull onNull;
    private DataType<?> returning;

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractAggregateFunction orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractAggregateFunction orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AggregateFilterStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AggregateFilterStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractWindowFunction orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractWindowFunction orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ JSONArrayAggNullStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ JSONArrayAggNullStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.JSONArrayAggReturningStep
    public /* bridge */ /* synthetic */ AggregateFilterStep returning(DataType dataType) {
        return returning((DataType<?>) dataType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONArrayAgg(DataType<J> type, Field<?> arg, boolean distinct) {
        super(distinct, Names.N_JSON_ARRAYAGG, type, (Field<?>[]) new Field[]{arg});
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v38, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v44, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v58, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                ctx.visit(JSONEntryImpl.jsonMerge(ctx, ClassUtils.ARRAY_SUFFIX, groupConcatEmulation(ctx)));
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(getDataType() == SQLDataType.JSON ? Names.N_JSON_AGG : Names.N_JSONB_AGG).sql('(');
                acceptDistinct(ctx);
                ctx.visit(this.arguments.get(0));
                acceptOrderBy(ctx);
                ctx.sql(')');
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                    acceptFilterClause(ctx, f(this.arguments.get(0).isNotNull()));
                } else {
                    acceptFilterClause(ctx);
                }
                acceptOverClause(ctx);
                return;
            case SQLITE:
                ctx.visit(Names.N_JSON_GROUP_ARRAY).sql('(');
                acceptDistinct(ctx);
                ctx.visit(this.arguments.get(0));
                acceptOrderBy(ctx);
                ctx.sql(')');
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                    acceptFilterClause(ctx, f(this.arguments.get(0).isNotNull()));
                } else {
                    acceptFilterClause(ctx);
                }
                acceptOverClause(ctx);
                return;
            case TRINO:
                boolean noAggregateFilter = this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL && !supportsFilter(ctx);
                ctx.visit(Names.N_CAST).sql('(');
                if (noAggregateFilter) {
                    ctx.visit(Names.N_FILTER).sql('(');
                }
                ctx.visit(Names.N_ARRAY_AGG).sql('(');
                acceptDistinct(ctx);
                ctx.visit(JSONEntryImpl.jsonCast(ctx, this.arguments.get(0)));
                acceptOrderBy(ctx);
                ctx.sql(')');
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                    acceptFilterClause(ctx, f(this.arguments.get(0).isNotNull()));
                } else {
                    acceptFilterClause(ctx);
                }
                acceptOverClause(ctx);
                if (noAggregateFilter) {
                    ctx.sql(", v -> v ").visit(Keywords.K_IS_NOT_NULL).sql(')');
                }
                ctx.sql(' ').visit(Keywords.K_AS).sql(' ').visit(SQLDataType.JSON);
                ctx.sql(')');
                return;
            default:
                acceptStandard(ctx);
                return;
        }
    }

    private final Field<?> groupConcatEmulation(Context<?> ctx) {
        Field<?> arg1 = this.arguments.get(0);
        if (arg1.getDataType().isString()) {
            switch (ctx.family()) {
                case MARIADB:
                case MYSQL:
                    arg1 = DSL.function(Names.N_JSON_QUOTE, getDataType(), arg1);
                    break;
            }
        }
        Field<?> arg2 = arg1;
        return DSL.concat((Field<?>[]) new Field[]{DSL.inline('['), CustomField.of(Names.N_GROUP_CONCAT, SQLDataType.VARCHAR, (Consumer<? super Context<?>>) c1 -> {
            c1.visit(groupConcatEmulationWithoutArrayWrappers(this.distinct, CustomField.of(Names.N_FIELD, SQLDataType.VARCHAR, (Consumer<? super Context<?>>) c2 -> {
                acceptArguments2(c2, QueryPartListView.wrap(arg2));
            }), this.withinGroupOrderBy));
            acceptFilterClause(ctx);
            acceptOverClause(c1);
        }), DSL.inline(']')});
    }

    static final Field<?> groupConcatEmulationWithoutArrayWrappers(boolean distinct, Field<?> field, SortFieldList orderBy) {
        return (Field) Tools.apply(distinct ? DSL.groupConcatDistinct(field) : DSL.groupConcat(field), agg -> {
            return Tools.isEmpty((Collection<?>) orderBy) ? agg : agg.orderBy(orderBy);
        });
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        ctx.visit(Names.N_JSON_ARRAYAGG).sql('(');
        acceptDistinct(ctx);
        acceptArguments3(ctx, this.arguments, JSONEntryImpl.jsonCastMapper(ctx));
        acceptOrderBy(ctx);
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

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    private void acceptDistinct(Context<?> ctx) {
        if (this.distinct) {
            ctx.visit(Keywords.K_DISTINCT).sql(' ');
        }
    }

    @Override // org.jooq.JSONArrayAggNullStep
    public final JSONArrayAgg<J> nullOnNull() {
        this.onNull = QOM.JSONOnNull.NULL_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONArrayAggNullStep
    public final JSONArrayAgg<J> absentOnNull() {
        this.onNull = QOM.JSONOnNull.ABSENT_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONArrayAggReturningStep
    public final JSONArrayAgg<J> returning(DataType<?> r) {
        this.returning = r;
        return this;
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public final JSONArrayAgg<J> orderBy(OrderField<?>... fields) {
        return (JSONArrayAgg) super.orderBy(fields);
    }

    @Override // org.jooq.impl.AbstractAggregateFunction, org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public final JSONArrayAgg<J> orderBy(Collection<? extends OrderField<?>> fields) {
        return (JSONArrayAgg) super.orderBy(fields);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Select<R> patchOracleArrayAggBug(Scope scope, SelectGroupByStep<R> select) {
        return select;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator1
    public final Field<?> $arg1() {
        return getArguments().get(0);
    }

    @Override // org.jooq.impl.QOM.JSONArrayAgg
    public final QOM.JSONOnNull $onNull() {
        return this.onNull;
    }

    @Override // org.jooq.impl.QOM.JSONArrayAgg
    public final DataType<?> $returning() {
        return this.returning;
    }

    @Override // org.jooq.impl.QOM.UOperator1
    public final org.jooq.Function1<? super Field<?>, ? extends QOM.JSONArrayAgg<J>> $constructor() {
        return f -> {
            JSONArrayAgg<J> r = new JSONArrayAgg<>(getDataType(), f, this.distinct);
            r.onNull = this.onNull;
            r.returning = this.returning;
            return r;
        };
    }
}

package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.jooq.AggregateFilterStep;
import org.jooq.AggregateFunction;
import org.jooq.ArrayAggOrderByStep;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.OrderedAggregateFunction;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.WindowBeforeOverStep;
import org.jooq.WindowDefinition;
import org.jooq.WindowRowsStep;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractAggregateFunction.class */
public abstract class AbstractAggregateFunction<T> extends AbstractWindowFunction<T> implements AggregateFunction<T>, OrderedAggregateFunction<T>, ArrayAggOrderByStep<T> {
    static final Set<SQLDialect> NO_SUPPORT_FILTER = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL);
    static final Set<SQLDialect> NO_SUPPORT_WINDOW_FILTER = SQLDialect.supportedBy(SQLDialect.TRINO);
    static final Set<SQLDialect> SUPPORT_DISTINCT_RVE = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.POSTGRES);
    static final Lazy<Field<Integer>> ASTERISK = Lazy.of(() -> {
        return DSL.field(DSL.raw("*"), Integer.class);
    });
    final QueryPartList<Field<?>> arguments;
    final boolean distinct;
    final ConditionProviderImpl filter;
    SortFieldList withinGroupOrderBy;
    SortFieldList keepDenseRankOrderBy;
    boolean first;

    @Override // org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractWindowFunction orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AbstractWindowFunction orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.OrderedAggregateFunction
    public /* bridge */ /* synthetic */ AggregateFilterStep withinGroupOrderBy(Collection collection) {
        return withinGroupOrderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.OrderedAggregateFunction
    public /* bridge */ /* synthetic */ AggregateFilterStep withinGroupOrderBy(OrderField[] orderFieldArr) {
        return withinGroupOrderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AggregateFilterStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.ArrayAggOrderByStep
    public /* bridge */ /* synthetic */ AggregateFilterStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAggregateFunction(String name, DataType<T> type, Field<?>... arguments) {
        this(false, name, (DataType) type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAggregateFunction(Name name, DataType<T> type, Field<?>... arguments) {
        this(false, name, (DataType) type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAggregateFunction(boolean distinct, String name, DataType<T> type, Field<?>... arguments) {
        this(distinct, DSL.unquotedName(name), type, arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractAggregateFunction(boolean distinct, Name name, DataType<T> type, Field<?>... arguments) {
        super(name, type);
        this.distinct = distinct;
        this.arguments = new QueryPartList<>(arguments);
        this.filter = new ConditionProviderImpl();
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        toSQLArguments(ctx);
        acceptKeepDenseRankOrderByClause(ctx);
        acceptWithinGroupClause(ctx);
        acceptFilterClause(ctx);
        acceptOverClause(ctx);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0053, code lost:            r1 = org.jooq.impl.Keywords.K_LAST;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0020, code lost:            r0 = r4.sql(' ').visit(org.jooq.impl.Keywords.K_KEEP).sql(" (").visit(org.jooq.impl.Keywords.K_DENSE_RANK).sql(' ');     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x004a, code lost:            if (r3.first == false) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x004d, code lost:            r1 = org.jooq.impl.Keywords.K_FIRST;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0056, code lost:            r0.visit(r1).sql(' ').visit(org.jooq.impl.Keywords.K_ORDER_BY).sql(' ').visit(r3.keepDenseRankOrderBy).sql(')');     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0082, code lost:            return;     */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void acceptKeepDenseRankOrderByClause(org.jooq.Context<?> r4) {
        /*
            r3 = this;
            r0 = r3
            org.jooq.impl.SortFieldList r0 = r0.keepDenseRankOrderBy
            boolean r0 = org.jooq.impl.Tools.isEmpty(r0)
            if (r0 != 0) goto L82
            int[] r0 = org.jooq.impl.AbstractAggregateFunction.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L20;
            }
        L20:
            r0 = r4
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_KEEP
            org.jooq.Context r0 = r0.visit(r1)
            java.lang.String r1 = " ("
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_DENSE_RANK
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r1 = r3
            boolean r1 = r1.first
            if (r1 == 0) goto L53
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_FIRST
            goto L56
        L53:
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_LAST
        L56:
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_ORDER_BY
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r1 = r3
            org.jooq.impl.SortFieldList r1 = r1.keepDenseRankOrderBy
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 41
            org.jooq.Context r0 = r0.sql(r1)
        L82:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractAggregateFunction.acceptKeepDenseRankOrderByClause(org.jooq.Context):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x005e, code lost:            r4.visit(r3.withinGroupOrderBy);     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0020, code lost:            r4.sql(' ').visit(org.jooq.impl.Keywords.K_WITHIN_GROUP).sql(" (").visit(org.jooq.impl.Keywords.K_ORDER_BY).sql(' ');     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x004e, code lost:            if (r3.withinGroupOrderBy.isEmpty() == false) goto L9;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0051, code lost:            r4.visit(org.jooq.impl.Keywords.K_NULL);     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0069, code lost:            r4.sql(')');     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0072, code lost:            return;     */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final void acceptWithinGroupClause(org.jooq.Context<?> r4) {
        /*
            r3 = this;
            r0 = r3
            org.jooq.impl.SortFieldList r0 = r0.withinGroupOrderBy
            if (r0 == 0) goto L72
            int[] r0 = org.jooq.impl.AbstractAggregateFunction.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L20;
            }
        L20:
            r0 = r4
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_WITHIN_GROUP
            org.jooq.Context r0 = r0.visit(r1)
            java.lang.String r1 = " ("
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_ORDER_BY
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r0 = r3
            org.jooq.impl.SortFieldList r0 = r0.withinGroupOrderBy
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L5e
            r0 = r4
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_NULL
            org.jooq.Context r0 = r0.visit(r1)
            goto L69
        L5e:
            r0 = r4
            r1 = r3
            org.jooq.impl.SortFieldList r1 = r1.withinGroupOrderBy
            org.jooq.Context r0 = r0.visit(r1)
        L69:
            r0 = r4
            r1 = 41
            org.jooq.Context r0 = r0.sql(r1)
        L72:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractAggregateFunction.acceptWithinGroupClause(org.jooq.Context):void");
    }

    private final void toSQLArguments(Context<?> ctx) {
        acceptFunctionName(ctx);
        ctx.sql('(');
        acceptArguments0(ctx);
        ctx.sql(')');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void acceptFunctionName(Context<?> ctx) {
        AbstractFunction.acceptFunctionName(ctx, true, getQualifiedName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void acceptArguments0(Context<?> ctx) {
        acceptArguments1(ctx, this.arguments);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    public final void acceptArguments1(Context<?> ctx, QueryPartCollectionView<Field<?>> args) {
        boolean parens = false;
        if (this.distinct) {
            ctx.visit(Keywords.K_DISTINCT).sql(' ');
            boolean z = false | (args.size() > 1 && SUPPORT_DISTINCT_RVE.contains(ctx.dialect()) && !(this instanceof ListAgg));
            parens = z;
            if (z) {
                ctx.sql('(');
            }
        }
        if (!args.isEmpty()) {
            acceptArguments2(ctx, args);
        }
        if (parens) {
            ctx.sql(')');
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void acceptArguments2(Context<?> ctx, QueryPartCollectionView<Field<?>> args) {
        acceptArguments3(ctx, args, f -> {
            return applyMap(ctx, f);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void acceptArguments3(Context<?> ctx, QueryPartCollectionView<Field<?>> args, java.util.function.Function<? super Field<?>, ? extends Field<?>> fun) {
        if (!this.filter.hasWhere() || supportsFilter(ctx)) {
            ctx.visit(QueryPartCollectionView.wrap(args).map(fun));
        } else {
            ctx.visit(QueryPartCollectionView.wrap(args).map((arg, i) -> {
                if (applyFilter(ctx, arg, i)) {
                    return DSL.when((Condition) this.filter, arg == ASTERISK.get() ? DSL.one() : arg);
                }
                return arg;
            }).map(fun));
        }
    }

    Field<?> applyMap(Context<?> ctx, Field<?> arg) {
        return arg;
    }

    boolean applyFilter(Context<?> ctx, Field<?> arg, int i) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void acceptFilterClause(Context<?> ctx) {
        if (this.filter.hasWhere()) {
            acceptFilterClause(ctx, this.filter);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    public final void acceptFilterClause(Context<?> ctx, Condition f) {
        switch (ctx.family()) {
            default:
                if (supportsFilter(ctx)) {
                    ctx.sql(' ').visit(Keywords.K_FILTER).sql(" (").visit(Keywords.K_WHERE).sql(' ').visit(f).sql(')');
                    return;
                }
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean supportsFilter(Context<?> ctx) {
        return (NO_SUPPORT_FILTER.contains(ctx.dialect()) || (NO_SUPPORT_WINDOW_FILTER.contains(ctx.dialect()) && isWindow())) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x0020, code lost:            r4.sql(' ').visit(org.jooq.impl.Keywords.K_ORDER_BY).sql(' ').visit(r3.withinGroupOrderBy);     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0041, code lost:            return;     */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void acceptOrderBy(org.jooq.Context<?> r4) {
        /*
            r3 = this;
            r0 = r3
            org.jooq.impl.SortFieldList r0 = r0.withinGroupOrderBy
            boolean r0 = org.jooq.impl.Tools.isEmpty(r0)
            if (r0 != 0) goto L41
            int[] r0 = org.jooq.impl.AbstractAggregateFunction.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L20;
            }
        L20:
            r0 = r4
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_ORDER_BY
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r1 = r3
            org.jooq.impl.SortFieldList r1 = r1.withinGroupOrderBy
            org.jooq.Context r0 = r0.visit(r1)
        L41:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractAggregateFunction.acceptOrderBy(org.jooq.Context):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final QueryPartList<Field<?>> getArguments() {
        return this.arguments;
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(Condition c) {
        this.filter.addConditions(c);
        return this;
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(Condition... conditions) {
        return filterWhere(DSL.and(conditions));
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(Collection<? extends Condition> conditions) {
        return filterWhere(DSL.and(conditions));
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(Field<Boolean> field) {
        return filterWhere(DSL.condition(field));
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(SQL sql) {
        return filterWhere(DSL.condition(sql));
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(String sql) {
        return filterWhere(DSL.condition(sql));
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(String sql, Object... bindings) {
        return filterWhere(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.AggregateFilterStep
    public final WindowBeforeOverStep<T> filterWhere(String sql, QueryPart... parts) {
        return filterWhere(DSL.condition(sql, parts));
    }

    @Override // org.jooq.OrderedAggregateFunction
    public final AggregateFunction<T> withinGroupOrderBy(OrderField<?>... fields) {
        return withinGroupOrderBy((Collection<? extends OrderField<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.OrderedAggregateFunction
    public final AggregateFunction<T> withinGroupOrderBy(Collection<? extends OrderField<?>> fields) {
        if (this.withinGroupOrderBy == null) {
            this.withinGroupOrderBy = new SortFieldList();
        }
        this.withinGroupOrderBy.addAll(Tools.sortFields(fields));
        return this;
    }

    @Override // org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public AbstractAggregateFunction<T> orderBy(OrderField<?>... fields) {
        if (this.windowSpecification != null) {
            super.orderBy(fields);
        } else {
            withinGroupOrderBy(fields);
        }
        return this;
    }

    @Override // org.jooq.impl.AbstractWindowFunction, org.jooq.WindowOrderByStep, org.jooq.ArrayAggOrderByStep
    public AbstractAggregateFunction<T> orderBy(Collection<? extends OrderField<?>> fields) {
        if (this.windowSpecification != null) {
            this.windowSpecification.orderBy(fields);
        } else {
            withinGroupOrderBy(fields);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Condition f(Condition c) {
        return this.filter.hasWhere() ? this.filter.and(c) : c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U> Field<U> fon(AggregateFunction<U> function) {
        return DSL.nullif((Field) fo(function), (Field) DSL.zero());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U> Field<U> ofo(AbstractAggregateFunction<U> function) {
        return fo(Tools.isEmpty((Collection<?>) this.withinGroupOrderBy) ? function : function.orderBy((Collection<? extends OrderField<?>>) this.withinGroupOrderBy));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U> Field<U> fo(AggregateFilterStep<U> function) {
        return o(this.filter.hasWhere() ? function.filterWhere((Condition) this.filter) : function);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U> Field<U> o(WindowBeforeOverStep<U> function) {
        if (this.windowSpecification != null) {
            return function.over(this.windowSpecification);
        }
        if (this.windowDefinition != null) {
            return function.over((WindowDefinition) this.windowDefinition);
        }
        if (this.windowName != null) {
            return function.over(this.windowName);
        }
        return function;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U extends Number> Field<U> x(Field<U> x, Field<? extends Number> y) {
        return DSL.nvl2((Field<?>) y, (Field) x, (Field) DSL.inline((Object) null, x.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U extends Number> Field<U> y(Field<? extends Number> x, Field<U> y) {
        return DSL.nvl2((Field<?>) x, (Field) y, (Field) DSL.inline((Object) null, y.getDataType()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DataType<? extends Number> d(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
            case FIREBIRD:
            case HSQLDB:
            case SQLITE:
            case TRINO:
                return SQLDataType.DOUBLE;
            default:
                return SQLDataType.NUMERIC;
        }
    }

    public final boolean $distinct() {
        return this.distinct;
    }

    @Override // org.jooq.AggregateFunction
    public final Condition $filterWhere() {
        return this.filter.getWhereOrNull();
    }
}

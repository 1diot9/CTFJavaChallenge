package org.jooq.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.runtime.ObjectMethods;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.jooq.Asterisk;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.GroupField;
import org.jooq.JoinType;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.QualifiedAsterisk;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectLimitPercentStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectOffsetStep;
import org.jooq.SelectQuery;
import org.jooq.SelectWithTiesStep;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOptionalOnStep;
import org.jooq.TablePartitionByStep;
import org.jooq.WindowDefinition;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.ForLock;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectQueryImpl.class */
public final class SelectQueryImpl<R extends Record> extends AbstractResultQuery<R> implements SelectQuery<R> {
    final WithImpl with;
    private final SelectFieldList<SelectFieldOrAsterisk> select;
    private Table<?> intoTable;
    private String hint;
    private String option;
    private boolean distinct;
    private final QueryPartList<SelectFieldOrAsterisk> distinctOn;
    private ForLock forLock;
    private boolean withCheckOption;
    private boolean withReadOnly;
    private final TableList from;
    private final ConditionProviderImpl condition;
    private final GroupFieldList groupBy;
    private boolean groupByDistinct;
    private final ConditionProviderImpl having;
    private WindowList window;
    private final ConditionProviderImpl qualify;
    private final SortFieldList orderBy;
    private final QueryPartList<Field<?>> seek;
    private boolean seekBefore;
    private final Limit limit;
    private final List<CombineOperator> unionOp;
    private final List<QueryPartList<Select<?>>> union;
    private final SortFieldList unionOrderBy;
    private final QueryPartList<Field<?>> unionSeek;
    private boolean unionSeekBefore;
    private final Limit unionLimit;
    private final Map<QueryPart, QueryPart> localQueryPartMapping;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) SelectQueryImpl.class);
    private static final Clause[] CLAUSES = {Clause.SELECT};
    static final Set<SQLDialect> EMULATE_SELECT_INTO_AS_CTAS = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_SELECT_INTO_TABLE = SQLDialect.supportedBy(SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_WINDOW_CLAUSE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB);
    private static final Set<SQLDialect> OPTIONAL_FROM_CLAUSE = SQLDialect.supportedBy(SQLDialect.DEFAULT, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> REQUIRES_DERIVED_TABLE_DML = SQLDialect.supportedUntil(SQLDialect.MYSQL);
    private static final Set<SQLDialect> NO_IMPLICIT_GROUP_BY_ON_HAVING = SQLDialect.supportedBy(SQLDialect.SQLITE);
    static final Set<SQLDialect> SUPPORT_FULL_WITH_TIES = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.TRINO);
    static final Set<SQLDialect> EMULATE_DISTINCT_ON = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE, SQLDialect.TRINO);
    static final Set<SQLDialect> NO_SUPPORT_FOR_UPDATE_OF_FIELDS = SQLDialect.supportedBy(SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_UNION_ORDER_BY_ALIAS = SQLDialect.supportedBy(SQLDialect.FIREBIRD);
    static final Set<SQLDialect> NO_SUPPORT_WITH_READ_ONLY = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_UNION_PARENTHESES = SQLDialect.supportedBy(SQLDialect.SQLITE);
    private static final Set<SQLDialect> NO_SUPPORT_CTE_IN_UNION = SQLDialect.supportedBy(SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.TRINO);
    private static final Set<SQLDialect> UNION_PARENTHESIS = SQLDialect.supportedUntil(SQLDialect.DERBY);

    @Override // org.jooq.Select
    public /* bridge */ /* synthetic */ Select $from(Collection collection) {
        return $from((Collection<? extends Table<?>>) collection);
    }

    @Override // org.jooq.Select
    public /* bridge */ /* synthetic */ Select $select(Collection collection) {
        return $select((Collection<? extends SelectFieldOrAsterisk>) collection);
    }

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -551054701:
                if (implMethodName.equals("lambda$asTable$434c563e$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("org/jooq/impl/LazySupplier") && lambda.getFunctionalInterfaceMethodName().equals(BeanUtil.PREFIX_GETTER_GET) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("org/jooq/impl/SelectQueryImpl") && lambda.getImplMethodSignature().equals("()Lorg/jooq/Name;")) {
                    SelectQueryImpl selectQueryImpl = (SelectQueryImpl) lambda.getCapturedArg(0);
                    return () -> {
                        return DSL.name(Tools.autoAlias(this));
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectQueryImpl(Configuration configuration, WithImpl with) {
        this(configuration, with, (TableLike) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectQueryImpl(Configuration configuration, WithImpl with, boolean distinct) {
        this(configuration, with, null, distinct);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SelectQueryImpl(Configuration configuration, WithImpl with, TableLike<? extends R> from) {
        this(configuration, with, from, false);
    }

    SelectQueryImpl(Configuration configuration, WithImpl with, TableLike<? extends R> from, boolean distinct) {
        super(configuration);
        this.with = with;
        this.distinct = distinct;
        this.distinctOn = new SelectFieldList();
        this.select = new SelectFieldList<>();
        this.from = new TableList();
        this.condition = new ConditionProviderImpl();
        this.groupBy = new GroupFieldList();
        this.having = new ConditionProviderImpl();
        this.qualify = new ConditionProviderImpl();
        this.orderBy = new SortFieldList();
        this.seek = new QueryPartList<>();
        this.limit = new Limit();
        this.unionOp = new ArrayList();
        this.union = new ArrayList();
        this.unionOrderBy = new SortFieldList();
        this.unionSeek = new QueryPartList<>();
        this.unionLimit = new Limit();
        if (from != null) {
            this.from.add((TableList) from.asTable());
        }
        this.localQueryPartMapping = new LinkedHashMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectQueryImpl$CopyClause.class */
    public enum CopyClause {
        START,
        WHERE,
        QUALIFY,
        END;

        final boolean between(CopyClause startInclusive, CopyClause endExclusive) {
            return compareTo(startInclusive) >= 0 && compareTo(endExclusive) < 0;
        }
    }

    private final SelectQueryImpl<R> copyTo(CopyClause clause, boolean scalarSelect, SelectQueryImpl<R> result) {
        return copyBetween(CopyClause.START, clause, scalarSelect, result);
    }

    private final SelectQueryImpl<R> copyAfter(CopyClause clause, boolean scalarSelect, SelectQueryImpl<R> result) {
        return copyBetween(clause, CopyClause.END, scalarSelect, result);
    }

    private final SelectQueryImpl<R> copyBetween(CopyClause start, CopyClause end, boolean scalarSelect, SelectQueryImpl<R> result) {
        if (CopyClause.START.between(start, end)) {
            result.from.addAll(this.from);
            result.condition.setWhere(this.condition.getWhere());
            if (scalarSelect) {
                result.select.addAll(this.select);
            }
        }
        if (CopyClause.WHERE.between(start, end)) {
            result.groupBy.addAll(this.groupBy);
            result.groupByDistinct = this.groupByDistinct;
            result.having.setWhere(this.having.getWhere());
            if (this.window != null) {
                result.addWindow0(this.window);
            }
            result.qualify.setWhere(this.qualify.getWhere());
        }
        if (CopyClause.QUALIFY.between(start, end)) {
            if (!scalarSelect) {
                result.select.addAll(this.select);
            }
            result.hint = this.hint;
            result.distinct = this.distinct;
            result.distinctOn.addAll(this.distinctOn);
            result.orderBy.addAll(this.orderBy);
            result.seek.addAll(this.seek);
            result.limit.from(this.limit);
            result.forLock = this.forLock;
            result.withCheckOption = this.withCheckOption;
            result.withReadOnly = this.withReadOnly;
            result.option = this.option;
            result.intoTable = this.intoTable;
            result.union.addAll(this.union);
            result.unionOp.addAll(this.unionOp);
            result.unionOrderBy.addAll(this.unionOrderBy);
            result.unionSeek.addAll(this.unionSeek);
            result.unionSeekBefore = this.unionSeekBefore;
            result.unionLimit.from(this.unionLimit);
        }
        return result;
    }

    private final SelectQueryImpl<R> copy(Consumer<? super SelectQueryImpl<R>> finisher) {
        return copy(finisher, this.with);
    }

    private final SelectQueryImpl<R> copy(Consumer<? super SelectQueryImpl<R>> finisher, WithImpl newWith) {
        SelectQueryImpl<R> result = copyTo(CopyClause.END, false, new SelectQueryImpl<>(configuration(), newWith));
        finisher.accept(result);
        return result;
    }

    @Override // org.jooq.FieldLike
    public final <T> Field<T> asField() {
        return new ScalarSubquery(this, Tools.scalarType(this), false);
    }

    @Override // org.jooq.FieldLike
    public final <T> Field<T> asField(String alias) {
        return asField().as(alias);
    }

    @Override // org.jooq.FieldLike
    public <T> Field<T> asField(java.util.function.Function<? super Field<T>, ? extends String> aliasFunction) {
        return asField().as(aliasFunction);
    }

    @Override // org.jooq.impl.ResultQueryTrait, org.jooq.Fields
    public final Row fieldsRow() {
        return asTable().fieldsRow();
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset() {
        return DSL.multiset(this);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(String alias) {
        return DSL.multiset(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(Name alias) {
        return DSL.multiset(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Field<Result<R>> asMultiset(Field<?> alias) {
        return DSL.multiset(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable() {
        return new DerivedTable(this).as((Name) new LazyName(() -> {
            return DSL.name(Tools.autoAlias(this));
        }));
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String str) {
        return (Table<R>) new DerivedTable(this).as(str);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String str, String... strArr) {
        return (Table<R>) new DerivedTable(this).as(str, strArr);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String str, Collection<? extends String> collection) {
        return (Table<R>) new DerivedTable(this).as(str, collection);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias) {
        return new DerivedTable(this).as(alias);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name alias, Name... fieldAliases) {
        return new DerivedTable(this).as(alias, fieldAliases);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Name name, Collection<? extends Name> collection) {
        return (Table<R>) new DerivedTable(this).as(name, collection);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> table) {
        return (Table<R>) new DerivedTable(this).as(table);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> table, Field<?>... fieldArr) {
        return (Table<R>) new DerivedTable(this).as(table, fieldArr);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(Table<?> table, Collection<? extends Field<?>> collection) {
        return (Table<R>) new DerivedTable(this).as(table, collection);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String str, java.util.function.Function<? super Field<?>, ? extends String> function) {
        return (Table<R>) new DerivedTable(this).as(str, function);
    }

    @Override // org.jooq.TableLike
    public final Table<R> asTable(String str, BiFunction<? super Field<?>, ? super Integer, ? extends String> biFunction) {
        return (Table<R>) new DerivedTable(this).as(str, biFunction);
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields(ThrowingSupplier<? extends ResultSetMetaData, SQLException> rs) throws SQLException {
        Field<?>[] fields = getFields();
        if (fields.length == 0) {
            return new MetaDataFieldProvider(configuration(), rs.get()).getFields();
        }
        return fields;
    }

    @Override // org.jooq.impl.ResultQueryTrait
    public final Field<?>[] getFields() {
        Collection<? extends Field<?>> fields = coerce();
        if (fields == null || fields.isEmpty()) {
            fields = getSelect();
        }
        return Tools.fieldArray(fields);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private final Select<?> distinctOnEmulation() {
        List<Field<?>> partitionBy = new ArrayList<>(this.distinctOn.size());
        Iterator<SelectFieldOrAsterisk> it = this.distinctOn.iterator();
        while (it.hasNext()) {
            SelectFieldOrAsterisk s = it.next();
            if (s instanceof Field) {
                Field<?> f = (Field) s;
                partitionBy.add(f);
            }
        }
        Field<Integer> rn = DSL.rowNumber().over(DSL.partitionBy(partitionBy).orderBy(this.orderBy)).as("rn");
        SelectQueryImpl<R> copy = copy(x -> {
        });
        copy.distinctOn.clear();
        copy.select.add((SelectFieldList<SelectFieldOrAsterisk>) rn);
        copy.orderBy.clear();
        copy.limit.clear();
        SelectLimitStep<?> s1 = DSL.select(new QualifiedSelectFieldList(DSL.table(DSL.name("t")), this.select)).from(copy.asTable("t")).where(rn.eq(DSL.one())).orderBy(Tools.map(this.orderBy, o -> {
            return Tools.unqualified(o);
        }));
        if (this.limit.limit == null) {
            return this.limit.offset != null ? s1.offset(this.limit.offset) : s1;
        }
        SelectLimitPercentStep<?> s2 = s1.limit(this.limit.limit);
        SelectWithTiesStep<?> s3 = this.limit.percent ? s2.percent() : s2;
        SelectOffsetStep<?> s4 = this.limit.withTies ? s3.withTies() : s3;
        return this.limit.offset != null ? s4.offset(this.limit.offset) : s4;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Table<?> dmlTable;
        List<Table<?>> dmlTables;
        Table<?> dmlTable2;
        if (ctx.subqueryLevel() == 1 && REQUIRES_DERIVED_TABLE_DML.contains(ctx.dialect()) && !Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_INSERT_SELECT)) && (dmlTable2 = (Table) ctx.data(Tools.SimpleDataKey.DATA_DML_TARGET_TABLE)) != null && Tools.containsUnaliasedTable(getFrom(), dmlTable2)) {
            ctx.visit(DSL.select(DSL.asterisk()).from(asTable("t")));
            return;
        }
        if (ctx.subqueryLevel() == 1 && ctx.family() == SQLDialect.MARIADB && !Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_INSERT_SELECT)) && (((dmlTable = (Table) ctx.data(Tools.SimpleDataKey.DATA_DML_TARGET_TABLE)) != null && Tools.aliased(dmlTable) != null && Tools.containsUnaliasedTable(getFrom(), dmlTable)) || ((dmlTables = (List) ctx.data(Tools.SimpleDataKey.DATA_DML_USING_TABLES)) != null && Tools.anyMatch(dmlTables, t -> {
            return Tools.containsUnaliasedTable(getFrom(), (Table<?>) t);
        })))) {
            ctx.visit(DSL.select(DSL.asterisk()).from(asTable("t")));
            return;
        }
        if (Tools.isNotEmpty((Collection<?>) this.distinctOn) && EMULATE_DISTINCT_ON.contains(ctx.dialect())) {
            ctx.visit(distinctOnEmulation());
            return;
        }
        if (!this.qualify.hasWhere() || !Transformations.transformQualify(ctx.configuration())) {
            if (this.withReadOnly && NO_SUPPORT_WITH_READ_ONLY.contains(ctx.dialect())) {
                ctx.visit(copy(s -> {
                    s.withReadOnly = false;
                    if (!s.distinct && s.groupBy.isEmpty() && !s.having.hasWhere() && !s.limit.isApplicable() && !s.hasUnions()) {
                        s.union((Select) DSL.select(Tools.map(s.getSelect(), f -> {
                            return DSL.inline((Object) null, f);
                        })).where((Condition) DSL.falseCondition()));
                    }
                }));
            } else {
                accept0(ctx);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v103, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v106, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v119, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v82, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v85, types: [org.jooq.Context] */
    final void accept0(Context<?> context) {
        boolean topLevelCte = false;
        if (context.subqueryLevel() == 0) {
            context.scopeStart(this);
            boolean z = false | (context.data(Tools.SimpleDataKey.DATA_TOP_LEVEL_CTE) == null);
            topLevelCte = z;
            if (z) {
                context.data(Tools.SimpleDataKey.DATA_TOP_LEVEL_CTE, new TopLevelCte());
            }
        }
        SQLDialect dialect = context.dialect();
        switch (SettingsTools.getRenderTable(context.settings())) {
            case NEVER:
                context.data(Tools.ExtendedDataKey.DATA_RENDER_TABLE, false);
                break;
            case WHEN_MULTIPLE_TABLES:
                int s = getFrom().size();
                if (knownTableSource() && (s == 0 || (s == 1 && !(getFrom().get(0) instanceof JoinTable)))) {
                    context.data(Tools.ExtendedDataKey.DATA_RENDER_TABLE, false);
                    break;
                }
                break;
            case WHEN_AMBIGUOUS_COLUMNS:
                if (knownTableSource() && !Tools.hasAmbiguousNames(getSelect())) {
                    context.data(Tools.ExtendedDataKey.DATA_RENDER_TABLE, false);
                    break;
                }
                break;
        }
        Object renderTrailingLimit = context.data(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE);
        Name[] selectAliases = (Name[]) context.data(Tools.SimpleDataKey.DATA_SELECT_ALIASES);
        List<Field<?>> originalFields = null;
        List<Field<?>> alternativeFields = null;
        if (selectAliases != null) {
            try {
                context.data().remove(Tools.SimpleDataKey.DATA_SELECT_ALIASES);
                List<Field<?>> select = getSelect();
                originalFields = select;
                alternativeFields = Tools.map(select, (f, i) -> {
                    return i < selectAliases.length ? f.as(selectAliases[i]) : f;
                });
            } finally {
                if (renderTrailingLimit != null) {
                    context.data(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, renderTrailingLimit);
                }
                if (selectAliases != null) {
                    context.data(Tools.SimpleDataKey.DATA_SELECT_ALIASES, selectAliases);
                }
            }
        }
        if (Boolean.TRUE.equals(renderTrailingLimit)) {
            context.data().remove(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE);
        }
        if (this.intoTable != null && !Boolean.TRUE.equals(context.data(Tools.BooleanDataKey.DATA_OMIT_INTO_CLAUSE)) && EMULATE_SELECT_INTO_AS_CTAS.contains(dialect)) {
            context.data(Tools.BooleanDataKey.DATA_OMIT_INTO_CLAUSE, true, c -> {
                c.visit(DSL.createTable(this.intoTable).as(this));
            });
            if (renderTrailingLimit != null) {
                context.data(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, renderTrailingLimit);
            }
            if (selectAliases == null) {
                return;
            }
            context.data(Tools.SimpleDataKey.DATA_SELECT_ALIASES, selectAliases);
            return;
        }
        if (this.with != null) {
            context.visit(this.with);
        } else if (topLevelCte) {
            CommonTableExpressionList.markTopLevelCteAndAccept(context, c2 -> {
            });
        }
        pushWindow(context);
        Boolean wrapDerivedTables = (Boolean) context.data(Tools.BooleanDataKey.DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES);
        if (Boolean.TRUE.equals(wrapDerivedTables)) {
            context.sqlIndentStart('(').data().remove(Tools.BooleanDataKey.DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES);
        }
        switch (context.family()) {
            case MARIADB:
                if (getLimit().isApplicable() && getLimit().isExpression()) {
                    toSQLReferenceLimitWithWindowFunctions(context, originalFields, alternativeFields);
                    break;
                } else {
                    toSQLReferenceLimitDefault(context, originalFields, alternativeFields);
                    break;
                }
            case POSTGRES:
                toSQLReferenceLimitDefault(context, originalFields, alternativeFields);
                break;
            case FIREBIRD:
            case MYSQL:
                if (getLimit().isApplicable() && (getLimit().withTies() || getLimit().isExpression())) {
                    toSQLReferenceLimitWithWindowFunctions(context, originalFields, alternativeFields);
                    break;
                } else {
                    toSQLReferenceLimitDefault(context, originalFields, alternativeFields);
                    break;
                }
                break;
            case TRINO:
                if (getLimit().isApplicable() && getLimit().isExpression()) {
                    toSQLReferenceLimitWithWindowFunctions(context, originalFields, alternativeFields);
                    break;
                } else {
                    toSQLReferenceLimitDefault(context, originalFields, alternativeFields);
                    break;
                }
            case CUBRID:
            case DUCKDB:
            case YUGABYTEDB:
                if (getLimit().isApplicable() && getLimit().withTies()) {
                    toSQLReferenceLimitWithWindowFunctions(context, originalFields, alternativeFields);
                    break;
                } else {
                    toSQLReferenceLimitDefault(context, originalFields, alternativeFields);
                    break;
                }
                break;
            default:
                toSQLReferenceLimitDefault(context, originalFields, alternativeFields);
                break;
        }
        if (this.forLock != null) {
            context.visit(this.forLock);
        } else if (this.withCheckOption) {
            context.formatSeparator().visit(Keywords.K_WITH_CHECK_OPTION);
        } else if (this.withReadOnly && !NO_SUPPORT_WITH_READ_ONLY.contains(context.dialect())) {
            context.formatSeparator().visit(Keywords.K_WITH_READ_ONLY);
        }
        if (!StringUtils.isBlank(this.option)) {
            context.formatSeparator().sql(this.option);
        }
        if (Boolean.TRUE.equals(wrapDerivedTables)) {
            context.sqlIndentEnd(')').data(Tools.BooleanDataKey.DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES, true);
        }
        if (context.subqueryLevel() == 0) {
            context.scopeEnd();
        }
    }

    private final void pushWindow(Context<?> context) {
        if (Tools.isNotEmpty((Collection<?>) this.window)) {
            context.data(Tools.SimpleDataKey.DATA_WINDOW_DEFINITIONS, this.window);
        }
    }

    private final void toSQLReferenceLimitDefault(Context<?> context, List<Field<?>> originalFields, List<Field<?>> alternativeFields) {
        context.data(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, true, c -> {
            toSQLReference0(context, originalFields, alternativeFields, null);
        });
    }

    private final void toSQLReferenceQualifyInsteadOfLimit(Context<?> context, List<Field<?>> originalFields, List<Field<?>> alternativeFields) {
        context.data(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, false, c -> {
            toSQLReference0(context, originalFields, alternativeFields, limitWindowFunctionCondition(limitWindowFunction(context)));
        });
    }

    private final void toSQLReferenceLimitWithWindowFunctions(Context<?> ctx, List<Field<?>> originalFields, List<Field<?>> alternativeFields) {
        if (Transformations.EMULATE_QUALIFY.contains(ctx.dialect()) || getQualify().hasWhere()) {
            toSQLReferenceLimitWithWindowFunctions0(ctx);
        } else {
            toSQLReferenceQualifyInsteadOfLimit(ctx, originalFields, alternativeFields);
        }
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v36, types: [org.jooq.Context] */
    private final void toSQLReferenceLimitWithWindowFunctions0(Context<?> ctx) {
        List<Field<?>> originalFields = getSelect();
        List<Field<?>> alternativeFields = new ArrayList<>(originalFields.size());
        if (originalFields.isEmpty()) {
            alternativeFields.add(DSL.field("*"));
        } else {
            alternativeFields.addAll(Tools.aliasedFields(originalFields));
        }
        alternativeFields.add(CustomField.of("rn", SQLDataType.INTEGER, (Consumer<? super Context<?>>) c -> {
            boolean wrapQueryExpressionBodyInDerivedTable = wrapQueryExpressionBodyInDerivedTable(c);
            c.data(Tools.BooleanDataKey.DATA_UNALIAS_ALIASED_EXPRESSIONS, Boolean.valueOf(!wrapQueryExpressionBodyInDerivedTable));
            boolean q = c.qualify();
            c.data(Tools.SimpleDataKey.DATA_OVERRIDE_ALIASES_IN_ORDER_BY, new Object[]{originalFields, alternativeFields});
            if (wrapQueryExpressionBodyInDerivedTable) {
                c.qualify(false);
            }
            c.visit(limitWindowFunction(c));
            c.data().remove(Tools.BooleanDataKey.DATA_UNALIAS_ALIASED_EXPRESSIONS);
            c.data().remove(Tools.SimpleDataKey.DATA_OVERRIDE_ALIASES_IN_ORDER_BY);
            if (wrapQueryExpressionBodyInDerivedTable) {
                c.qualify(q);
            }
        }).as("rn"));
        List<Field<?>> unaliasedFields = Tools.unaliasedFields(originalFields);
        ctx.visit(Keywords.K_SELECT).separatorRequired(true).declareFields(true, c2 -> {
            c2.visit(new SelectFieldList(unaliasedFields));
        }).formatSeparator().visit(Keywords.K_FROM).sqlIndentStart(" (").subquery(true);
        toSQLReference0(ctx, originalFields, alternativeFields, null);
        ctx.subquery(false).sqlIndentEnd(") ").visit(DSL.name("x")).formatSeparator().visit(Keywords.K_WHERE).sql(' ').visit(limitWindowFunctionCondition(DSL.field(DSL.name("rn"), SQLDataType.INTEGER)));
        if (!ctx.subquery() && !getOrderBy().isEmpty() && !Boolean.FALSE.equals(ctx.settings().isRenderOrderByRownumberForEmulatedPagination())) {
            ctx.formatSeparator().visit(Keywords.K_ORDER_BY).sql(' ').visit(DSL.name("rn"));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Condition limitWindowFunctionCondition(Field<Integer> field) {
        if (getLimit().limitZero()) {
            return field.gt((Field<Integer>) getLimit().getLowerRownum());
        }
        return field.between((Field<Integer>) getLimit().getLowerRownum().add(DSL.inline(1))).and((Field) getLimit().getUpperRownum());
    }

    private final Field<Integer> limitWindowFunction(Context<?> c) {
        if (this.distinct) {
            return DSL.denseRank().over(DSL.orderBy(getNonEmptyOrderByForDistinct(c.configuration())));
        }
        if (getLimit().withTies()) {
            return DSL.rank().over(DSL.orderBy(getNonEmptyOrderBy(c.configuration())));
        }
        return DSL.rowNumber().over(DSL.orderBy(getNonEmptyOrderBy(c.configuration())));
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x01a7  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x01ba  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x01ab  */
    /* JADX WARN: Type inference failed for: r0v105, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v112, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v115, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v134, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v144, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v149, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v196, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v216, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v221, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v230, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v241, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v267, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v287, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v293, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v327, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v330, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v335, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v344, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v348, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v43, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v57, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v73, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v86, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void toSQLReference0(org.jooq.Context<?> r11, java.util.List<org.jooq.Field<?>> r12, java.util.List<org.jooq.Field<?>> r13, org.jooq.Condition r14) {
        /*
            Method dump skipped, instructions count: 2196
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.SelectQueryImpl.toSQLReference0(org.jooq.Context, java.util.List, java.util.List, org.jooq.Condition):void");
    }

    private static final TableList prependPathJoins(Context<?> ctx, ConditionProviderImpl where, TableList tablelist) {
        TableList result = new TableList(tablelist);
        for (int i = 0; i < result.size(); i++) {
            Table<?> t0 = (Table) result.get(i);
            Table<?> t1 = prependPathJoins(ctx, where, t0, JoinType.CROSS_JOIN);
            if (t0 != t1) {
                result.set(i, (int) t1);
            }
        }
        return result;
    }

    private static final Table<?> prependPathJoins(Context<?> ctx, ConditionProviderImpl where, Table<?> t, JoinType joinType) {
        if (t instanceof TableImpl) {
            TableImpl<?> ti = (TableImpl) t;
            return prependPathJoins(ctx, where, ti, joinType);
        }
        if (t instanceof JoinTable) {
            JoinTable<?> j = (JoinTable) t;
            return prependPathJoins(ctx, where, j);
        }
        return t;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final JoinTable<?> prependPathJoins(Context<?> ctx, ConditionProviderImpl where, JoinTable<?> j) {
        if (j.type.qualified()) {
            where = new ConditionProviderImpl();
        }
        Table<?> lhs0 = j.lhs;
        Table<?> rhs0 = j.rhs;
        Table<?> lhs1 = lhs0;
        if (rhs0 instanceof TableImpl) {
            TableImpl<?> ti = (TableImpl) rhs0;
            if (ti.path != null && !ctx.inScope(ti.path)) {
                lhs1 = j.transform(lhs0, ti.path);
                ctx.scopeRegister(ti.path, true);
            }
        }
        Table<?> lhs12 = prependPathJoins(ctx, where, lhs1, j.type);
        Table<?> rhs1 = prependPathJoins(ctx, where, rhs0, j.type);
        if (lhs0 != lhs12 || rhs0 != rhs1) {
            j = j.transform(lhs12, rhs1);
        }
        if (j.type.qualified() && where.hasWhere()) {
            j.condition.addConditions(where.getWhere());
        }
        return j;
    }

    private static final Table<?> prependPathJoins(Context<?> ctx, ConditionProviderImpl where, TableImpl<?> ti, JoinType joinType) {
        if (ti.path != null && !ctx.inScope(ti.path)) {
            Table<?> curr = ti;
            List<Table<?>> tables = new ArrayList<>();
            while (true) {
                Table<?> next = TableImpl.path(curr);
                if (next == null) {
                    break;
                }
                tables.add(curr);
                if (!ctx.inScope(curr)) {
                    ctx.scopeRegister(curr, true);
                }
                if (ctx.inScope(next)) {
                    break;
                }
                curr = next;
            }
            Table<?> result = null;
            for (int i = tables.size() - 1; i >= 0; i--) {
                result = result == null ? tables.get(i) : result.join(tables.get(i), joinType);
            }
            return result;
        }
        return ti;
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v57, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    private final void toSQLOrderBy(Context<?> ctx, List<Field<?>> originalFields, List<Field<?>> alternativeFields, boolean wrapQueryExpressionInDerivedTable, boolean wrapQueryExpressionBodyInDerivedTable, boolean isUnionOrderBy, QueryPartListView<SortField<?>> actualOrderBy, Limit actualLimit) {
        ctx.start(Clause.SELECT_ORDER_BY);
        if ((!getLimit().withTies() || SUPPORT_FULL_WITH_TIES.contains(ctx.dialect())) && !actualOrderBy.isEmpty()) {
            ctx.formatSeparator().visit(Keywords.K_ORDER);
            ctx.sql(' ').visit(Keywords.K_BY).separatorRequired(true);
            if (RowAsField.NO_NATIVE_SUPPORT.contains(ctx.dialect()) && Tools.findAny(actualOrderBy, s -> {
                return s.$field() instanceof Val;
            }) != null) {
                SelectFieldIndexes s2 = getSelectFieldIndexes(ctx);
                if (s2.mapped) {
                    actualOrderBy = new QueryPartListView(actualOrderBy).map(t1 -> {
                        Field<?> field;
                        Field<?> in = t1.$field();
                        if ((in instanceof Val) && in.getDataType().isNumeric()) {
                            Val<?> val = (Val) in;
                            int x = ((Integer) Convert.convert(val.getValue(), Integer.TYPE)).intValue() - 1;
                            int mapped = s2.mapping[x];
                            if (s2.projectionSizes[x] == 1) {
                                field = val.copy(Integer.valueOf(mapped + 1));
                            } else {
                                field = DSL.field("{0}", DSL.list((QueryPart[]) IntStream.range(mapped, mapped + s2.projectionSizes[mapped]).mapToObj(i -> {
                                    return val.copy(Integer.valueOf(i + 1));
                                }).toArray(x$0 -> {
                                    return new SelectField[x$0];
                                })));
                            }
                            Field<?> out = field;
                            return t1.$field(out);
                        }
                        return t1;
                    });
                }
            }
            if (NO_SUPPORT_UNION_ORDER_BY_ALIAS.contains(ctx.dialect()) && hasUnions() && isUnionOrderBy) {
                List<String> n = Tools.map(getSelect(), (v0) -> {
                    return v0.getName();
                });
                actualOrderBy = new QueryPartListView(actualOrderBy).map(t12 -> {
                    int i = n.indexOf(t12.$field().getName());
                    return i >= 0 ? t12.$field(DSL.inline(i + 1)) : t12;
                });
            }
            ctx.visit(actualOrderBy);
        }
        ctx.end(Clause.SELECT_ORDER_BY);
        if (wrapQueryExpressionInDerivedTable) {
            ctx.formatIndentEnd().formatNewLine().sql(") x");
        }
        if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE))) {
            if (actualLimit.isApplicable()) {
                ctx.visit(actualLimit);
                return;
            }
            if (!actualOrderBy.isEmpty() && Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_FORCE_LIMIT_WITH_ORDER_BY))) {
                Limit l = new Limit();
                switch (ctx.family()) {
                    case TRINO:
                        l.setLimit((Number) Integer.MAX_VALUE);
                        break;
                    default:
                        l.setLimit((Number) Long.MAX_VALUE);
                        break;
                }
                ctx.visit(l);
            }
        }
    }

    private final boolean applySeekOnDerivedTable() {
        return (getSeek().isEmpty() || getOrderBy().isEmpty() || this.unionOp.isEmpty()) ? false : true;
    }

    private final boolean wrapQueryExpressionBodyInDerivedTable(Context<?> ctx) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean hasUnions() {
        return !this.unionOp.isEmpty();
    }

    private final boolean unionOpNesting() {
        if (this.unionOp.size() > 1) {
            return true;
        }
        for (QueryPartList<Select<?>> s1 : this.union) {
            Iterator<Select<?>> it = s1.iterator();
            while (it.hasNext()) {
                Select<?> s2 = it.next();
                SelectQueryImpl<?> s = Tools.selectQueryImpl(s2);
                if (s != null && !s.unionOp.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private final boolean derivedTableRequired(Context<?> context, Select<?> s1) {
        SelectQueryImpl<?> s;
        return (!NO_SUPPORT_CTE_IN_UNION.contains(context.dialect()) || (s = Tools.selectQueryImpl(s1)) == null || s.with == null) ? false : true;
    }

    private final boolean unionParensRequired(Context<?> context) {
        if (this.unionOp.isEmpty()) {
            return false;
        }
        if (unionParensRequired((SelectQueryImpl<?>) this) || context.settings().isRenderParenthesisAroundSetOperationQueries().booleanValue()) {
            return true;
        }
        CombineOperator op = this.unionOp.get(0);
        if ((op == CombineOperator.EXCEPT || op == CombineOperator.EXCEPT_ALL) && this.union.get(0).size() > 1) {
            return true;
        }
        for (QueryPartList<Select<?>> s1 : this.union) {
            Iterator<Select<?>> it = s1.iterator();
            while (it.hasNext()) {
                Select<?> s2 = it.next();
                SelectQueryImpl<?> s = Tools.selectQueryImpl(s2);
                if (s != null && unionParensRequired(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    private final boolean unionParensRequired(SelectQueryImpl<?> s) {
        return s.orderBy.size() > 0 || s.limit.isApplicable() || s.with != null;
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v37, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v44, types: [org.jooq.Context] */
    private final void unionParenthesis(Context<?> ctx, char parenthesis, List<Field<?>> fields, boolean derivedTableRequired, boolean parensRequired, QueryPart subquery) {
        if ('(' == parenthesis) {
            ((AbstractContext) ctx).subquery0(true, true, subquery);
        } else if (')' == parenthesis) {
            ((AbstractContext) ctx).subquery0(false, true, null);
        }
        boolean derivedTableRequired2 = derivedTableRequired | (derivedTableRequired || (parensRequired && NO_SUPPORT_UNION_PARENTHESES.contains(ctx.dialect())) || ((Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_NESTED_SET_OPERATIONS)) && UNION_PARENTHESIS.contains(ctx.dialect())) || Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST))));
        boolean parensRequired2 = parensRequired | derivedTableRequired2;
        if (parensRequired2 && ')' == parenthesis) {
            ctx.formatIndentEnd().formatNewLine();
        } else if (parensRequired2 && '(' == parenthesis && derivedTableRequired2) {
            ctx.formatNewLine().visit(Keywords.K_SELECT).sql(' ');
            if (ctx.family() == SQLDialect.DERBY) {
                ctx.visit(new SelectFieldList(Tools.map(fields, f -> {
                    return Tools.unqualified(f);
                })));
            } else {
                ctx.sql('*');
            }
            ctx.formatSeparator().visit(Keywords.K_FROM).sql(' ');
        }
        switch (ctx.family()) {
            case FIREBIRD:
                break;
            default:
                if (parensRequired2) {
                    ctx.sql(parenthesis);
                    break;
                }
                break;
        }
        if (parensRequired2 && '(' == parenthesis) {
            ctx.formatIndentStart().formatNewLine();
        } else if (parensRequired2 && ')' == parenthesis && derivedTableRequired2) {
            ctx.sql(" x");
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addSelect(Collection<? extends SelectFieldOrAsterisk> fields) {
        getSelectAsSpecified().addAll(fields);
    }

    @Override // org.jooq.SelectQuery
    public final void addSelect(SelectFieldOrAsterisk... fields) {
        addSelect(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override // org.jooq.SelectQuery
    public final void addDistinctOn(SelectFieldOrAsterisk... fields) {
        addDistinctOn(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void addDistinctOn(Collection<? extends SelectFieldOrAsterisk> fields) {
        this.distinctOn.addAll(fields);
    }

    @Override // org.jooq.SelectQuery
    public final void setInto(Table<?> table) {
        this.intoTable = table;
    }

    @Override // org.jooq.SelectQuery
    public final void addOffset(Number offset) {
        getLimit().setOffset(offset);
    }

    @Override // org.jooq.SelectQuery
    public final void addOffset(Field<? extends Number> offset) {
        getLimit().setOffset(offset);
    }

    @Override // org.jooq.SelectQuery
    public final void addLimit(Number l) {
        getLimit().setLimit(l);
    }

    @Override // org.jooq.SelectQuery
    public final void addLimit(Field<? extends Number> l) {
        getLimit().setLimit(l);
    }

    @Override // org.jooq.SelectQuery
    public final void addLimit(Number offset, Number l) {
        getLimit().setOffset(offset);
        getLimit().setLimit(l);
    }

    @Override // org.jooq.SelectQuery
    public final void addLimit(Number offset, Field<? extends Number> l) {
        getLimit().setOffset(offset);
        getLimit().setLimit(l);
    }

    @Override // org.jooq.SelectQuery
    public final void addLimit(Field<? extends Number> offset, Number l) {
        getLimit().setOffset(offset);
        getLimit().setLimit(l);
    }

    @Override // org.jooq.SelectQuery
    public final void addLimit(Field<? extends Number> offset, Field<? extends Number> l) {
        getLimit().setOffset(offset);
        getLimit().setLimit(l);
    }

    @Override // org.jooq.SelectQuery
    public final void setLimitPercent(boolean percent) {
        getLimit().setPercent(percent);
    }

    @Override // org.jooq.SelectQuery
    public final void setWithTies(boolean withTies) {
        getLimit().setWithTies(withTies);
    }

    final ForLock forLock() {
        if (this.forLock == null) {
            this.forLock = new ForLock();
        }
        return this.forLock;
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdate(boolean forUpdate) {
        if (forUpdate) {
            forLock().forLockMode = ForLock.ForLockMode.UPDATE;
        } else {
            this.forLock = null;
        }
    }

    @Override // org.jooq.SelectQuery
    public final void setForNoKeyUpdate(boolean forNoKeyUpdate) {
        if (forNoKeyUpdate) {
            forLock().forLockMode = ForLock.ForLockMode.NO_KEY_UPDATE;
        } else {
            this.forLock = null;
        }
    }

    @Override // org.jooq.SelectQuery
    public final void setForKeyShare(boolean forKeyShare) {
        if (forKeyShare) {
            forLock().forLockMode = ForLock.ForLockMode.KEY_SHARE;
        } else {
            this.forLock = null;
        }
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdateOf(Field<?>... fields) {
        setForLockModeOf(fields);
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdateOf(Collection<? extends Field<?>> fields) {
        setForLockModeOf(fields);
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdateOf(Table<?>... tables) {
        setForLockModeOf(tables);
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdateWait(int seconds) {
        setForLockModeWait(seconds);
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdateNoWait() {
        setForLockModeNoWait();
    }

    @Override // org.jooq.SelectQuery
    public final void setForUpdateSkipLocked() {
        setForLockModeSkipLocked();
    }

    @Override // org.jooq.SelectQuery
    public final void setForShare(boolean forShare) {
        if (forShare) {
            forLock().forLockMode = ForLock.ForLockMode.SHARE;
        } else {
            this.forLock = null;
        }
    }

    @Override // org.jooq.SelectQuery
    public final void setForLockModeOf(Field<?>... fields) {
        setForLockModeOf(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void setForLockModeOf(Collection<? extends Field<?>> fields) {
        initLockMode();
        forLock().forLockOf = new QueryPartList<>(fields);
        forLock().forLockOfTables = null;
    }

    @Override // org.jooq.SelectQuery
    public final void setForLockModeOf(Table<?>... tables) {
        initLockMode();
        forLock().forLockOf = null;
        forLock().forLockOfTables = new TableList((List<? extends Table<?>>) Arrays.asList(tables));
    }

    @Override // org.jooq.SelectQuery
    public final void setForLockModeWait(int seconds) {
        initLockMode();
        forLock().forLockWaitMode = ForLock.ForLockWaitMode.WAIT;
        forLock().forLockWait = seconds;
    }

    @Override // org.jooq.SelectQuery
    public final void setForLockModeNoWait() {
        initLockMode();
        forLock().forLockWaitMode = ForLock.ForLockWaitMode.NOWAIT;
        forLock().forLockWait = 0;
    }

    @Override // org.jooq.SelectQuery
    public final void setForLockModeSkipLocked() {
        initLockMode();
        forLock().forLockWaitMode = ForLock.ForLockWaitMode.SKIP_LOCKED;
        forLock().forLockWait = 0;
    }

    private final void initLockMode() {
        forLock().forLockMode = forLock().forLockMode == null ? ForLock.ForLockMode.UPDATE : forLock().forLockMode;
    }

    @Override // org.jooq.SelectQuery
    public final void setWithCheckOption() {
        this.withCheckOption = true;
        this.withReadOnly = false;
    }

    @Override // org.jooq.SelectQuery
    public final void setWithReadOnly() {
        this.withCheckOption = false;
        this.withReadOnly = true;
    }

    @Override // org.jooq.Select
    public final List<Field<?>> getSelect() {
        return getSelectResolveAllAsterisks(Tools.configuration(configuration()).dsl());
    }

    private static final Collection<? extends Field<?>> subtract(List<? extends Field<?>> left, List<? extends Field<?>> right) {
        FieldsImpl<?> e = new FieldsImpl<>(right);
        List<Field<?>> result = new ArrayList<>();
        for (Field<?> f : left) {
            if (e.field(f) == null) {
                result.add(f);
            }
        }
        return result;
    }

    final SelectFieldList<SelectFieldOrAsterisk> getSelectAsSpecified() {
        return this.select;
    }

    final SelectFieldList<SelectFieldOrAsterisk> getSelectResolveImplicitAsterisks() {
        if (getSelectAsSpecified().isEmpty()) {
            return (SelectFieldList) resolveAsterisk(new SelectFieldList());
        }
        return getSelectAsSpecified();
    }

    final SelectFieldList<SelectFieldOrAsterisk> getSelectResolveUnsupportedAsterisks(Context<?> ctx) {
        return getSelectResolveSomeAsterisks0(ctx, false);
    }

    final SelectFieldList<Field<?>> getSelectResolveAllAsterisks(Scope ctx) {
        return getSelectResolveSomeAsterisks0(ctx, true);
    }

    private final SelectFieldList<SelectFieldOrAsterisk> getSelectResolveSomeAsterisks0(Scope ctx, boolean resolveSupported) {
        SelectFieldList<SelectFieldOrAsterisk> result = new SelectFieldList<>();
        boolean knownTableSource = knownTableSource();
        boolean resolveExcept = resolveSupported || (knownTableSource && !AsteriskImpl.SUPPORT_NATIVE_EXCEPT.contains(ctx.dialect()));
        boolean resolveUnqualifiedCombined = resolveSupported || (knownTableSource && AsteriskImpl.NO_SUPPORT_UNQUALIFIED_COMBINED.contains(ctx.dialect()));
        SelectFieldList<SelectFieldOrAsterisk> list = getSelectResolveImplicitAsterisks();
        int size = 0;
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            SelectFieldOrAsterisk s = (SelectFieldOrAsterisk) it.next();
            appendResolveSomeAsterisks0(ctx, resolveSupported, result, resolveExcept, resolveUnqualifiedCombined, list, s);
            if (resolveSupported && size == result.size()) {
                return new SelectFieldList<>();
            }
            size = result.size();
        }
        return result;
    }

    private final void appendResolveSomeAsterisks0(Scope ctx, boolean resolveSupported, SelectFieldList<SelectFieldOrAsterisk> result, boolean resolveExcept, boolean resolveUnqualifiedCombined, SelectFieldList<SelectFieldOrAsterisk> list, SelectFieldOrAsterisk s) {
        if (s instanceof Field) {
            Field<?> f = (Field) s;
            result.add((SelectFieldList<SelectFieldOrAsterisk>) getResolveProjection(ctx, f));
            return;
        }
        if (s instanceof QualifiedAsterisk) {
            QualifiedAsterisk q = (QualifiedAsterisk) s;
            Table<?> qualifier = q.qualifier();
            if (qualifier instanceof QOM.JoinTable) {
                QOM.JoinTable<?, ?> j = (QOM.JoinTable) qualifier;
                appendResolveSomeAsterisks0(ctx, resolveSupported, result, resolveExcept, resolveUnqualifiedCombined, list, j.$table1().asterisk());
                appendResolveSomeAsterisks0(ctx, resolveSupported, result, resolveExcept, resolveUnqualifiedCombined, list, j.$table2().asterisk());
                return;
            } else {
                if (q.$except().isEmpty()) {
                    if (resolveSupported) {
                        result.addAll(Arrays.asList(q.qualifier().fields()));
                        return;
                    } else {
                        result.add((SelectFieldList<SelectFieldOrAsterisk>) s);
                        return;
                    }
                }
                if (resolveExcept) {
                    result.addAll(subtract(Arrays.asList(q.qualifier().fields()), q.$except()));
                    return;
                } else {
                    result.add((SelectFieldList<SelectFieldOrAsterisk>) s);
                    return;
                }
            }
        }
        if (s instanceof Asterisk) {
            Asterisk a = (Asterisk) s;
            if (a.$except().isEmpty()) {
                if (resolveSupported || (resolveUnqualifiedCombined && list.size() > 1)) {
                    result.addAll(resolveAsterisk(new QueryPartList()));
                    return;
                } else {
                    result.add((SelectFieldList<SelectFieldOrAsterisk>) s);
                    return;
                }
            }
            if (resolveExcept) {
                result.addAll(resolveAsterisk(new QueryPartList(), (QueryPartListView) a.$except()));
                return;
            } else {
                result.add((SelectFieldList<SelectFieldOrAsterisk>) s);
                return;
            }
        }
        if (s instanceof Row) {
            Row r = (Row) s;
            result.add((SelectFieldList<SelectFieldOrAsterisk>) getResolveProjection(ctx, new RowAsField(r)));
        } else {
            if (s instanceof Table) {
                Table<?> t = (Table) s;
                result.add((SelectFieldList<SelectFieldOrAsterisk>) getResolveProjection(ctx, new TableAsField(t)));
                return;
            }
            throw new AssertionError("Type not supported: " + String.valueOf(s));
        }
    }

    private final Field<?> getResolveProjection(Scope ctx, Field<?> f) {
        return f;
    }

    private final <Q extends QueryPartList<? super Field<?>>> Q resolveAsterisk(Q q) {
        return (Q) resolveAsterisk(q, null);
    }

    private final <Q extends QueryPartList<? super Field<?>>> Q resolveAsterisk(Q result, QueryPartCollectionView<? extends Field<?>> except) {
        FieldsImpl<?> e = except == null ? null : new FieldsImpl<>(except);
        if (knownTableSource()) {
            if (e == null) {
                Iterator<T> it = getFrom().iterator();
                while (it.hasNext()) {
                    TableLike<?> table = (TableLike) it.next();
                    for (Field<?> field : table.asTable().fields()) {
                        result.add(field);
                    }
                }
            } else {
                Iterator<T> it2 = getFrom().iterator();
                while (it2.hasNext()) {
                    TableLike<?> table2 = (TableLike) it2.next();
                    for (Field<?> field2 : table2.asTable().fields()) {
                        if (e.field(field2) == null) {
                            result.add(field2);
                        }
                    }
                }
            }
        }
        if (getFrom().isEmpty()) {
            result.add(DSL.one());
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SelectQueryImpl$SelectFieldIndexes.class */
    public static final class SelectFieldIndexes extends Record {
        private final boolean mapped;
        private final int[] mapping;
        private final int[] projectionSizes;

        private SelectFieldIndexes(boolean mapped, int[] mapping, int[] projectionSizes) {
            this.mapped = mapped;
            this.mapping = mapping;
            this.projectionSizes = projectionSizes;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, SelectFieldIndexes.class), SelectFieldIndexes.class, "mapped;mapping;projectionSizes", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->mapped:Z", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->mapping:[I", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->projectionSizes:[I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, SelectFieldIndexes.class), SelectFieldIndexes.class, "mapped;mapping;projectionSizes", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->mapped:Z", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->mapping:[I", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->projectionSizes:[I").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, SelectFieldIndexes.class, Object.class), SelectFieldIndexes.class, "mapped;mapping;projectionSizes", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->mapped:Z", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->mapping:[I", "FIELD:Lorg/jooq/impl/SelectQueryImpl$SelectFieldIndexes;->projectionSizes:[I").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public boolean mapped() {
            return this.mapped;
        }

        public int[] mapping() {
            return this.mapping;
        }

        public int[] projectionSizes() {
            return this.projectionSizes;
        }
    }

    private final SelectFieldIndexes getSelectFieldIndexes(Context<?> ctx) {
        List<Field<?>> s = getSelect();
        boolean mapped = false;
        int[] mapping = new int[s.size()];
        int[] projectionSizes = new int[s.size()];
        if (RowAsField.NO_NATIVE_SUPPORT.contains(ctx.dialect())) {
            for (int i = 0; i < mapping.length; i++) {
                projectionSizes[i] = ((AbstractField) s.get(i)).projectionSize();
                mapped |= projectionSizes[i] > 1;
                if (i < mapping.length - 1) {
                    mapping[i + 1] = mapping[i] + projectionSizes[i];
                }
            }
        } else {
            for (int i2 = 0; i2 < mapping.length; i2++) {
                mapping[i2] = i2;
            }
        }
        return new SelectFieldIndexes(mapped, mapping, projectionSizes);
    }

    private final boolean knownTableSource() {
        return ((Boolean) Tools.traverseJoins(getFrom(), true, (Predicate<? super boolean>) r -> {
            return !r.booleanValue();
        }, (Predicate<? super JoinTable<?>>) null, (Predicate<? super JoinTable<?>>) j -> {
            return (j.type == JoinType.LEFT_ANTI_JOIN || j.type == JoinType.LEFT_SEMI_JOIN) ? false : true;
        }, (BiFunction<? super boolean, ? super JoinType, ? extends boolean>) null, (BiFunction<? super boolean, ? super Table<?>, ? extends boolean>) (r2, t) -> {
            return Boolean.valueOf(r2.booleanValue() && t.fieldsRow().size() > 0);
        })).booleanValue();
    }

    @Override // org.jooq.impl.AbstractResultQuery
    final Class<? extends R> getRecordType0() {
        if (getFrom().size() == 1 && getSelectAsSpecified().isEmpty()) {
            return ((Table) getFrom().get(0)).asTable().getRecordType();
        }
        return Tools.recordType(getSelect().size());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final TableList getFrom() {
        return this.from;
    }

    @Deprecated
    final ConditionProviderImpl getWhere(Context<?> ctx, TableList tablelist) {
        return getWhere(ctx, tablelist, new ConditionProviderImpl());
    }

    final ConditionProviderImpl getWhere(Context<?> ctx, TableList tablelist, ConditionProviderImpl result) {
        if (this.condition.hasWhere()) {
            result.addConditions(this.condition.getWhere());
        }
        if (!isGrouping() && !getOrderBy().isEmpty() && !getSeek().isEmpty() && this.unionOp.isEmpty()) {
            result.addConditions(getSeekCondition(ctx));
        }
        Iterator<T> it = tablelist.iterator();
        while (it.hasNext()) {
            Table<?> t = (Table) it.next();
            addPathConditions(ctx, result, t);
        }
        Tools.traverseJoins(tablelist, (Object) null, (Predicate<? super Object>) null, (Predicate<? super JoinTable<?>>) t2 -> {
            if (t2 instanceof CrossJoin) {
                CrossJoin j = (CrossJoin) t2;
                addPathConditions(ctx, result, j.lhs);
                addPathConditions(ctx, result, j.rhs);
                return true;
            }
            return true;
        }, (Predicate<? super JoinTable<?>>) null, (BiFunction<? super Object, ? super JoinType, ? extends Object>) null, (BiFunction<? super Object, ? super Table<?>, ? extends Object>) null);
        return result;
    }

    private static final void addPathConditions(Context<?> ctx, ConditionProviderImpl result, Table<?> t) {
        if (t instanceof TableImpl) {
            TableImpl<?> ti = (TableImpl) t;
            if (ti.path != null && ctx.inScope(ti.path)) {
                result.addConditions(ti.pathCondition());
            }
        }
    }

    final Condition getSeekCondition(Context<?> ctx) {
        Condition c;
        SortFieldList o = getOrderBy();
        List<Field<?>> s = getSeek();
        if (o.nulls()) {
        }
        if (o.size() > 1 && o.uniform() && !Boolean.FALSE.equals(ctx.settings().isRenderRowConditionForSeekClause())) {
            List<Field<?>> l = o.fields();
            List<Field<?>> r = s;
            if (Tools.anyMatch(r, e -> {
                return e instanceof NoField;
            })) {
                l = new ArrayList(l);
                r = new ArrayList<>(r);
                for (int i = 0; i < r.size(); i++) {
                    if (r.get(i) instanceof NoField) {
                        l.remove(i);
                        r.remove(i);
                    }
                }
            }
            if (l.isEmpty()) {
                c = DSL.noCondition();
            } else if ((((SortField) o.get(0)).getOrder() != SortOrder.DESC) ^ this.seekBefore) {
                if (l.size() == 1) {
                    c = l.get(0).gt(r.get(0));
                } else {
                    c = DSL.row((Collection<?>) l).gt(DSL.row((Collection<?>) r));
                }
            } else if (l.size() == 1) {
                c = l.get(0).lt(r.get(0));
            } else {
                c = DSL.row((Collection<?>) l).lt(DSL.row((Collection<?>) r));
            }
        } else {
            ConditionProviderImpl or = new ConditionProviderImpl();
            for (int i2 = 0; i2 < o.size(); i2++) {
                if (!(s.get(i2) instanceof NoField)) {
                    ConditionProviderImpl and = new ConditionProviderImpl();
                    for (int j = 0; j < i2; j++) {
                        if (!(s.get(j) instanceof NoField)) {
                            and.addConditions(((SortField) o.get(j)).$field().eq((Field) s.get(j)));
                        }
                    }
                    SortField<?> sf = (SortField) o.get(i2);
                    if ((sf.getOrder() != SortOrder.DESC) ^ this.seekBefore) {
                        and.addConditions(sf.$field().gt(s.get(i2)));
                    } else {
                        and.addConditions(sf.$field().lt(s.get(i2)));
                    }
                    or.addConditions(Operator.OR, and);
                }
            }
            c = or.getWhere();
        }
        if (o.size() > 1 && Boolean.TRUE.equals(ctx.settings().isRenderRedundantConditionForSeekClause())) {
            if ((((SortField) o.get(0)).getOrder() != SortOrder.DESC) ^ this.seekBefore) {
                c = ((SortField) o.get(0)).$field().ge((Field) s.get(0)).and(c);
            } else {
                c = ((SortField) o.get(0)).$field().le((Field) s.get(0)).and(c);
            }
        }
        return c;
    }

    final boolean isGrouping() {
        return !this.groupBy.isEmpty() || this.having.hasWhere();
    }

    final GroupFieldList getGroupBy() {
        return this.groupBy;
    }

    final ConditionProviderImpl getHaving(Context<?> ctx) {
        ConditionProviderImpl result = new ConditionProviderImpl();
        if (this.having.hasWhere()) {
            result.addConditions(this.having.getWhere());
        }
        if (isGrouping() && !getOrderBy().isEmpty() && !getSeek().isEmpty() && this.unionOp.isEmpty()) {
            result.addConditions(getSeekCondition(ctx));
        }
        return result;
    }

    final ConditionProviderImpl getQualify() {
        return this.qualify;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SortFieldList getOrderBy() {
        return this.unionOp.size() == 0 ? this.orderBy : this.unionOrderBy;
    }

    final QueryPartList<Field<?>> getSeek() {
        return this.unionOp.size() == 0 ? this.seek : this.unionSeek;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Limit getLimit() {
        return this.unionOp.size() == 0 ? this.limit : this.unionLimit;
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0028, code lost:            r0.add((org.jooq.impl.SortFieldList) org.jooq.impl.DSL.field("({select} 0)").asc());     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0039, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final org.jooq.impl.SortFieldList getNonEmptyOrderBy(org.jooq.Configuration r4) {
        /*
            r3 = this;
            r0 = r3
            org.jooq.impl.SortFieldList r0 = r0.getOrderBy()
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L3a
            org.jooq.impl.SortFieldList r0 = new org.jooq.impl.SortFieldList
            r1 = r0
            r1.<init>()
            r5 = r0
            int[] r0 = org.jooq.impl.SelectQueryImpl.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L28;
            }
        L28:
            r0 = r5
            java.lang.String r1 = "({select} 0)"
            org.jooq.Field r1 = org.jooq.impl.DSL.field(r1)
            org.jooq.SortField r1 = r1.asc()
            boolean r0 = r0.add(r1)
            r0 = r5
            return r0
        L3a:
            r0 = r3
            org.jooq.impl.SortFieldList r0 = r0.getOrderBy()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.SelectQueryImpl.getNonEmptyOrderBy(org.jooq.Configuration):org.jooq.impl.SortFieldList");
    }

    final SortFieldList getNonEmptyOrderByForDistinct(Configuration configuration) {
        SortFieldList order = new SortFieldList();
        order.addAll(getNonEmptyOrderBy(configuration));
        Set<Field<?>> fields = new HashSet<>(Tools.map(order, o -> {
            return o.$field();
        }));
        for (Field<?> field : getSelect()) {
            if (!fields.contains(field)) {
                order.add((SortFieldList) field.asc());
            }
        }
        return order;
    }

    @Override // org.jooq.SelectQuery
    public final void addOrderBy(Collection<? extends OrderField<?>> fields) {
        getOrderBy().addAll(Tools.sortFields(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void addOrderBy(OrderField<?>... fields) {
        addOrderBy(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void addOrderBy(int... fieldIndexes) {
        addOrderBy(Tools.map(fieldIndexes, v -> {
            return DSL.inline(v);
        }));
    }

    @Override // org.jooq.SelectQuery
    public final void addSeekAfter(Field<?>... fields) {
        addSeekAfter(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void addSeekAfter(Collection<? extends Field<?>> fields) {
        if (this.unionOp.size() == 0) {
            this.seekBefore = false;
        } else {
            this.unionSeekBefore = false;
        }
        getSeek().addAll(fields);
    }

    @Override // org.jooq.SelectQuery
    @Deprecated
    public final void addSeekBefore(Field<?>... fields) {
        addSeekBefore(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    @Deprecated
    public final void addSeekBefore(Collection<? extends Field<?>> fields) {
        if (this.unionOp.size() == 0) {
            this.seekBefore = true;
        } else {
            this.unionSeekBefore = true;
        }
        getSeek().addAll(fields);
    }

    @Override // org.jooq.SelectQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition... conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery, org.jooq.ConditionProvider
    public final void addConditions(Collection<? extends Condition> conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition... conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        this.condition.addConditions(operator, conditions);
    }

    final void setHint(String hint) {
        this.hint = hint;
    }

    final void setOption(String option) {
        this.option = option;
    }

    @Override // org.jooq.SelectQuery
    public final void addFrom(Collection<? extends TableLike<?>> tables) {
        for (TableLike<?> t : tables) {
            addFrom(t);
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addFrom(TableLike<?> table) {
        if (!(table instanceof NoTable)) {
            if (table instanceof NoTableJoin) {
                NoTableJoin t = (NoTableJoin) table;
                addFrom(t.table);
            } else {
                getFrom().add((TableList) table.asTable());
            }
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addFrom(TableLike<?>... tables) {
        for (TableLike<?> t : tables) {
            addFrom(t);
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addGroupBy(Collection<? extends GroupField> fields) {
        if (fields.isEmpty()) {
            this.groupBy.add((GroupFieldList) DSL.emptyGroupingSet());
        } else {
            this.groupBy.addAll(fields);
        }
    }

    @Override // org.jooq.SelectQuery
    public final void setGroupByDistinct(boolean groupByDistinct) {
        this.groupByDistinct = groupByDistinct;
    }

    @Override // org.jooq.SelectQuery
    public final void addGroupBy(GroupField... fields) {
        addGroupBy(Arrays.asList(fields));
    }

    @Override // org.jooq.SelectQuery
    public final void addHaving(Condition conditions) {
        this.having.addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addHaving(Condition... conditions) {
        this.having.addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addHaving(Collection<? extends Condition> conditions) {
        this.having.addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addHaving(Operator operator, Condition conditions) {
        this.having.addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addHaving(Operator operator, Condition... conditions) {
        this.having.addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addHaving(Operator operator, Collection<? extends Condition> conditions) {
        this.having.addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addWindow(WindowDefinition... definitions) {
        addWindow0(Arrays.asList(definitions));
    }

    @Override // org.jooq.SelectQuery
    public final void addWindow(Collection<? extends WindowDefinition> definitions) {
        addWindow0(definitions);
    }

    final void addWindow0(Collection<? extends WindowDefinition> definitions) {
        if (this.window == null) {
            this.window = new WindowList();
        }
        this.window.addAll(definitions);
    }

    @Override // org.jooq.SelectQuery
    public final void addQualify(Condition conditions) {
        getQualify().addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addQualify(Condition... conditions) {
        getQualify().addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addQualify(Collection<? extends Condition> conditions) {
        getQualify().addConditions(conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addQualify(Operator operator, Condition conditions) {
        getQualify().addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addQualify(Operator operator, Condition... conditions) {
        getQualify().addConditions(operator, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addQualify(Operator operator, Collection<? extends Condition> conditions) {
        getQualify().addConditions(operator, conditions);
    }

    private final SelectQueryImpl<R> combine(CombineOperator op, Select<? extends R> other) {
        if (this == other || ((other instanceof SelectImpl) && this == ((SelectImpl) other).getDelegate())) {
            throw new IllegalArgumentException("In jOOQ 3.x's mutable DSL API, it is not possible to use the same instance of a Select query on both sides of a set operation like s.union(s)");
        }
        int index = this.unionOp.size() - 1;
        if (index == -1 || this.unionOp.get(index) != op || op == CombineOperator.EXCEPT || op == CombineOperator.EXCEPT_ALL) {
            this.unionOp.add(op);
            this.union.add(new QueryPartList<>());
            index++;
        }
        this.union.get(index).add((QueryPartList<Select<?>>) other);
        return this;
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> union(Select<? extends R> other) {
        return combine(CombineOperator.UNION, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> unionDistinct(Select<? extends R> other) {
        return combine(CombineOperator.UNION, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> unionAll(Select<? extends R> other) {
        return combine(CombineOperator.UNION_ALL, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> except(Select<? extends R> other) {
        return combine(CombineOperator.EXCEPT, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> exceptDistinct(Select<? extends R> other) {
        return combine(CombineOperator.EXCEPT, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> exceptAll(Select<? extends R> other) {
        return combine(CombineOperator.EXCEPT_ALL, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> intersect(Select<? extends R> other) {
        return combine(CombineOperator.INTERSECT, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> intersectDistinct(Select<? extends R> other) {
        return combine(CombineOperator.INTERSECT, other);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> intersectAll(Select<? extends R> other) {
        return combine(CombineOperator.INTERSECT_ALL, other);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoin(TableLike<?> table, Condition conditions) {
        addJoin(table, JoinType.JOIN, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoin(TableLike<?> table, Condition... conditions) {
        addJoin(table, JoinType.JOIN, conditions);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoin(TableLike<?> table, JoinType type, Condition conditions) {
        addJoin0(table, type, null, conditions, null);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoin(TableLike<?> table, JoinType type, Condition... conditions) {
        addJoin0(table, type, null, conditions, null);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoin(TableLike<?> table, JoinType type, QOM.JoinHint hint, Condition conditions) {
        addJoin0(table, type, hint, conditions, null);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoin(TableLike<?> table, JoinType type, QOM.JoinHint hint, Condition... conditions) {
        addJoin0(table, type, hint, conditions, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.TableOnConditionStep] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.TableOnConditionStep] */
    /* JADX WARN: Type inference failed for: r0v39, types: [org.jooq.TableOnConditionStep] */
    /* JADX WARN: Type inference failed for: r0v43, types: [org.jooq.TableOnConditionStep] */
    private final void addJoin0(TableLike<?> table, JoinType type, QOM.JoinHint hint, Object conditions, Field<?>[] partitionBy) {
        TableOptionalOnStep<Record> join;
        int index = getFrom().size() - 1;
        switch (type) {
            case JOIN:
            case STRAIGHT_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
            case FULL_OUTER_JOIN:
                TableOptionalOnStep<Record> o = ((Table) getFrom().get(index)).join(table, type, hint);
                if (conditions instanceof Condition) {
                    Condition c = (Condition) conditions;
                    join = o.on(c);
                    break;
                } else {
                    join = o.on((Condition[]) conditions);
                    break;
                }
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
                TablePartitionByStep<?> p = (TablePartitionByStep) ((Table) getFrom().get(index)).join(table, type, hint);
                if (conditions instanceof Condition) {
                    Condition c2 = (Condition) conditions;
                    join = p.on(c2);
                    break;
                } else {
                    join = p.on((Condition[]) conditions);
                    break;
                }
            case CROSS_JOIN:
            case NATURAL_JOIN:
            case NATURAL_LEFT_OUTER_JOIN:
            case NATURAL_RIGHT_OUTER_JOIN:
            case NATURAL_FULL_OUTER_JOIN:
            case CROSS_APPLY:
            case OUTER_APPLY:
                join = ((Table) getFrom().get(index)).join(table, type, hint);
                break;
            default:
                throw new IllegalArgumentException("Bad join type: " + String.valueOf(type));
        }
        getFrom().set(index, (int) join);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinOnKey(TableLike<?> table, JoinType type) throws DataAccessException {
        addJoinOnKey(table, type, (QOM.JoinHint) null);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinOnKey(TableLike<?> table, JoinType type, QOM.JoinHint hint) throws DataAccessException {
        int index = getFrom().size() - 1;
        switch (type) {
            case JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
                getFrom().set(index, (int) ((Table) getFrom().get(index)).join(table, type, hint).onKey());
                return;
            case STRAIGHT_JOIN:
            default:
                throw new IllegalArgumentException("JoinType " + String.valueOf(type) + " is not supported with the addJoinOnKey() method. Use INNER or OUTER JOINs only");
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinOnKey(TableLike<?> table, JoinType type, TableField<?, ?>... keyFields) throws DataAccessException {
        addJoinOnKey(table, type, (QOM.JoinHint) null, keyFields);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinOnKey(TableLike<?> table, JoinType type, QOM.JoinHint hint, TableField<?, ?>... keyFields) throws DataAccessException {
        int index = getFrom().size() - 1;
        switch (type) {
            case JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
                getFrom().set(index, (int) ((Table) getFrom().get(index)).join(table, type, hint).onKey(keyFields));
                return;
            case STRAIGHT_JOIN:
            default:
                throw new IllegalArgumentException("JoinType " + String.valueOf(type) + " is not supported with the addJoinOnKey() method. Use INNER or OUTER JOINs only");
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinOnKey(TableLike<?> table, JoinType type, ForeignKey<?, ?> key) {
        addJoinOnKey(table, type, (QOM.JoinHint) null, key);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinOnKey(TableLike<?> table, JoinType type, QOM.JoinHint hint, ForeignKey<?, ?> key) {
        int index = getFrom().size() - 1;
        switch (type) {
            case JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
                getFrom().set(index, (int) ((Table) getFrom().get(index)).join(table, type, hint).onKey(key));
                return;
            case STRAIGHT_JOIN:
            default:
                throw new IllegalArgumentException("JoinType " + String.valueOf(type) + " is not supported with the addJoinOnKey() method. Use INNER or OUTER JOINs only");
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinUsing(TableLike<?> table, Collection<? extends Field<?>> fields) {
        addJoinUsing(table, JoinType.JOIN, fields);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinUsing(TableLike<?> table, JoinType type, Collection<? extends Field<?>> fields) {
        addJoinUsing(table, type, null, fields);
    }

    @Override // org.jooq.SelectQuery
    public final void addJoinUsing(TableLike<?> table, JoinType type, QOM.JoinHint hint, Collection<? extends Field<?>> fields) {
        int index = getFrom().size() - 1;
        switch (type) {
            case JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
                getFrom().set(index, (int) ((Table) getFrom().get(index)).join(table, type, hint).using(fields));
                return;
            case STRAIGHT_JOIN:
            default:
                throw new IllegalArgumentException("JoinType " + String.valueOf(type) + " is not supported with the addJoinUsing() method. Use INNER or OUTER JOINs only");
        }
    }

    @Override // org.jooq.SelectQuery
    public final void addHint(String h) {
        setHint(h);
    }

    @Override // org.jooq.SelectQuery
    public final void addOption(String o) {
        setOption(o);
    }

    @Override // org.jooq.Select
    public final WithImpl $with() {
        return this.with;
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<?> $with(QOM.With with) {
        return copy(s -> {
        }, (WithImpl) with);
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<SelectFieldOrAsterisk> $select() {
        return QOM.unmodifiable((List) this.select);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<?> $select(Collection<? extends SelectFieldOrAsterisk> collection) {
        return copy(s -> {
            s.select.clear();
            s.select.addAll(collection);
        });
    }

    @Override // org.jooq.Select
    public final boolean $distinct() {
        return this.distinct;
    }

    @Override // org.jooq.Select
    public final Select<R> $distinct(boolean newDistinct) {
        if ($distinct() == newDistinct) {
            return this;
        }
        return copy(s -> {
            s.distinct = newDistinct;
        });
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<SelectFieldOrAsterisk> $distinctOn() {
        return QOM.unmodifiable((List) this.distinctOn);
    }

    @Override // org.jooq.Select
    public final Select<R> $distinctOn(Collection<? extends SelectFieldOrAsterisk> newDistinctOn) {
        return copy(s -> {
            s.distinctOn.clear();
            s.distinctOn.addAll(newDistinctOn);
        });
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<Table<?>> $from() {
        return QOM.unmodifiable((List) this.from);
    }

    @Override // org.jooq.Select
    public final SelectQueryImpl<R> $from(Collection<? extends Table<?>> newFrom) {
        return copy(s -> {
            s.from.clear();
            s.from.addAll(newFrom);
        });
    }

    @Override // org.jooq.Select
    public final Condition $where() {
        return this.condition.getWhereOrNull();
    }

    @Override // org.jooq.Select
    public final Select<R> $where(Condition newWhere) {
        if ($where() == newWhere) {
            return this;
        }
        return copy(s -> {
            s.condition.setWhere(newWhere);
        });
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<GroupField> $groupBy() {
        return QOM.unmodifiable((List) this.groupBy);
    }

    @Override // org.jooq.Select
    public final Select<R> $groupBy(Collection<? extends GroupField> newGroupBy) {
        return copy(s -> {
            s.groupBy.clear();
            s.groupBy.addAll(newGroupBy);
        });
    }

    @Override // org.jooq.Select
    public final boolean $groupByDistinct() {
        return this.groupByDistinct;
    }

    @Override // org.jooq.Select
    public final Select<R> $groupByDistinct(boolean newGroupByDistinct) {
        if ($groupByDistinct() == newGroupByDistinct) {
            return this;
        }
        return copy(s -> {
            s.groupByDistinct = newGroupByDistinct;
        });
    }

    @Override // org.jooq.Select
    public final Condition $having() {
        return this.having.getWhereOrNull();
    }

    @Override // org.jooq.Select
    public final Select<R> $having(Condition newHaving) {
        if ($having() == newHaving) {
            return this;
        }
        return copy(s -> {
            s.having.setWhere(newHaving);
        });
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<? extends WindowDefinition> $window() {
        return QOM.unmodifiable(this.window == null ? QueryPartList.emptyList() : this.window);
    }

    @Override // org.jooq.Select
    public final Select<R> $window(Collection<? extends WindowDefinition> newWindow) {
        return copy(s -> {
            s.window.clear();
            s.window.addAll(newWindow);
        });
    }

    @Override // org.jooq.Select
    public final Condition $qualify() {
        return this.qualify.getWhereOrNull();
    }

    @Override // org.jooq.Select
    public final Select<R> $qualify(Condition newQualify) {
        if ($qualify() == newQualify) {
            return this;
        }
        return copy(s -> {
            s.qualify.setWhere(newQualify);
        });
    }

    @Override // org.jooq.Select
    public final QOM.UnmodifiableList<SortField<?>> $orderBy() {
        return QOM.unmodifiable((List) this.orderBy);
    }

    @Override // org.jooq.Select
    public final Select<R> $orderBy(Collection<? extends SortField<?>> newOrderBy) {
        return copy(s -> {
            s.orderBy.clear();
            s.orderBy.addAll(newOrderBy);
        });
    }

    @Override // org.jooq.Select
    public final Field<? extends Number> $limit() {
        return getLimit().limit;
    }

    @Override // org.jooq.Select
    public final Select<R> $limit(Field<? extends Number> newLimit) {
        if ($limit() == newLimit) {
            return this;
        }
        return copy(s -> {
            s.getLimit().setLimit((Field<? extends Number>) newLimit);
        });
    }

    @Override // org.jooq.Select
    public final boolean $limitPercent() {
        return getLimit().percent;
    }

    @Override // org.jooq.Select
    public final Select<R> $limitPercent(boolean newLimitPercent) {
        if ($limitPercent() == newLimitPercent) {
            return this;
        }
        return copy(s -> {
            s.getLimit().setPercent(newLimitPercent);
        });
    }

    @Override // org.jooq.Select
    public final boolean $limitWithTies() {
        return getLimit().withTies;
    }

    @Override // org.jooq.Select
    public final Select<R> $limitWithTies(boolean newLimitWithTies) {
        if ($limitPercent() == newLimitWithTies) {
            return this;
        }
        return copy(s -> {
            s.getLimit().setWithTies(newLimitWithTies);
        });
    }

    @Override // org.jooq.Select
    public final Field<? extends Number> $offset() {
        return getLimit().offset;
    }

    @Override // org.jooq.Select
    public final Select<R> $offset(Field<? extends Number> newOffset) {
        if ($limit() == newOffset) {
            return this;
        }
        return copy(s -> {
            s.getLimit().setOffset((Field<? extends Number>) newOffset);
        });
    }
}

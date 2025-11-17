package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateIndexFinalStep;
import org.jooq.CreateIndexIncludeStep;
import org.jooq.CreateIndexStep;
import org.jooq.CreateIndexWhereStep;
import org.jooq.Field;
import org.jooq.Function8;
import org.jooq.Index;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateIndexImpl.class */
public final class CreateIndexImpl extends AbstractDDLQuery implements QOM.CreateIndex, CreateIndexStep, CreateIndexIncludeStep, CreateIndexWhereStep, CreateIndexFinalStep {
    final boolean unique;
    final Index index;
    final boolean ifNotExists;
    Table<?> table;
    QueryPartListView<? extends OrderField<?>> on;
    QueryPartListView<? extends Field<?>> include;
    Condition where;
    boolean excludeNullKeys;
    private static final Clause[] CLAUSES = {Clause.CREATE_INDEX};
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.MYSQL);
    private static final Set<SQLDialect> NO_SUPPORT_SORT_SPEC = SQLDialect.supportedBy(SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> SUPPORT_UNNAMED_INDEX = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_INCLUDE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_UNIQUE_INCLUDE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.CreateIndexStep
    public /* bridge */ /* synthetic */ CreateIndexIncludeStep on(Table table, Collection collection) {
        return on((Table<?>) table, (Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.CreateIndexStep
    public /* bridge */ /* synthetic */ CreateIndexIncludeStep on(Name name, Collection collection) {
        return on(name, (Collection<? extends Name>) collection);
    }

    @Override // org.jooq.CreateIndexStep
    public /* bridge */ /* synthetic */ CreateIndexIncludeStep on(String str, Collection collection) {
        return on(str, (Collection<? extends String>) collection);
    }

    @Override // org.jooq.CreateIndexStep
    public /* bridge */ /* synthetic */ CreateIndexIncludeStep on(Table table, OrderField[] orderFieldArr) {
        return on((Table<?>) table, (OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.CreateIndexIncludeStep
    public /* bridge */ /* synthetic */ CreateIndexWhereStep include(Collection collection) {
        return include((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CreateIndexIncludeStep
    public /* bridge */ /* synthetic */ CreateIndexWhereStep include(Field[] fieldArr) {
        return include((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.CreateIndexWhereStep
    public /* bridge */ /* synthetic */ CreateIndexFinalStep where(Collection collection) {
        return where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.CreateIndexWhereStep
    public /* bridge */ /* synthetic */ CreateIndexFinalStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateIndexImpl(Configuration configuration, boolean unique, Index index, boolean ifNotExists) {
        this(configuration, unique, index, ifNotExists, null, null, null, null, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateIndexImpl(Configuration configuration, boolean unique, boolean ifNotExists) {
        this(configuration, unique, null, ifNotExists);
    }

    CreateIndexImpl(Configuration configuration, boolean unique, Index index, boolean ifNotExists, Table<?> table, Collection<? extends OrderField<?>> on, Collection<? extends Field<?>> include, Condition where, boolean excludeNullKeys) {
        super(configuration);
        this.unique = unique;
        this.index = index;
        this.ifNotExists = ifNotExists;
        this.table = table;
        this.on = new QueryPartList(on);
        this.include = new QueryPartList(include);
        this.where = where;
        this.excludeNullKeys = excludeNullKeys;
    }

    @Override // org.jooq.CreateIndexStep
    public final CreateIndexImpl on(String table, String... on) {
        return on((Table<?>) DSL.table(DSL.name(table)), (Collection<? extends OrderField<?>>) Tools.fieldsByName(on));
    }

    @Override // org.jooq.CreateIndexStep
    public final CreateIndexImpl on(Name table, Name... on) {
        return on((Table<?>) DSL.table(table), (Collection<? extends OrderField<?>>) Tools.fieldsByName(on));
    }

    @Override // org.jooq.CreateIndexStep
    public final CreateIndexImpl on(Table<?> table, OrderField<?>... on) {
        return on(table, (Collection<? extends OrderField<?>>) Arrays.asList(on));
    }

    @Override // org.jooq.CreateIndexStep
    public final CreateIndexImpl on(String table, Collection<? extends String> on) {
        return on((Table<?>) DSL.table(DSL.name(table)), (Collection<? extends OrderField<?>>) Tools.fieldsByName((String[]) on.toArray(Tools.EMPTY_STRING)));
    }

    @Override // org.jooq.CreateIndexStep
    public final CreateIndexImpl on(Name table, Collection<? extends Name> on) {
        return on((Table<?>) DSL.table(table), (Collection<? extends OrderField<?>>) Tools.fieldsByName((Name[]) on.toArray(Tools.EMPTY_NAME)));
    }

    @Override // org.jooq.CreateIndexStep
    public final CreateIndexImpl on(Table<?> table, Collection<? extends OrderField<?>> on) {
        this.table = table;
        this.on = new QueryPartList(on);
        return this;
    }

    @Override // org.jooq.CreateIndexIncludeStep
    public final CreateIndexImpl include(String... include) {
        return include((Collection<? extends Field<?>>) Tools.fieldsByName(include));
    }

    @Override // org.jooq.CreateIndexIncludeStep
    public final CreateIndexImpl include(Name... include) {
        return include((Collection<? extends Field<?>>) Tools.fieldsByName(include));
    }

    @Override // org.jooq.CreateIndexIncludeStep
    public final CreateIndexImpl include(Field<?>... include) {
        return include((Collection<? extends Field<?>>) Arrays.asList(include));
    }

    @Override // org.jooq.CreateIndexIncludeStep
    public final CreateIndexImpl include(Collection<? extends Field<?>> include) {
        this.include = new QueryPartList(include);
        return this;
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(Field<Boolean> where) {
        return where(DSL.condition(where));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(Condition... where) {
        return where(DSL.condition(Operator.AND, where));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(Collection<? extends Condition> where) {
        return where(DSL.condition(Operator.AND, where));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(Condition where) {
        this.where = where;
        return this;
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(String where, QueryPart... parts) {
        return where(DSL.condition(where, parts));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(String where, Object... bindings) {
        return where(DSL.condition(where, bindings));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(String where) {
        return where(DSL.condition(where));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl where(SQL where) {
        return where(DSL.condition(where));
    }

    @Override // org.jooq.CreateIndexWhereStep
    public final CreateIndexImpl excludeNullKeys() {
        this.excludeNullKeys = true;
        return this;
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_INDEX, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v35, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v48, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v74, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v90, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v95, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v98, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        boolean contains;
        Condition condition;
        Condition isNotNull;
        ctx.visit(Keywords.K_CREATE);
        if (this.unique) {
            ctx.sql(' ').visit(Keywords.K_UNIQUE);
        }
        ctx.sql(' ').visit(Keywords.K_INDEX).sql(' ');
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.visit(Keywords.K_IF_NOT_EXISTS).sql(' ');
        }
        if (this.index != null) {
            ctx.visit(this.index).sql(' ');
        } else if (!SUPPORT_UNNAMED_INDEX.contains(ctx.dialect())) {
            ctx.visit(generatedName()).sql(' ');
        }
        if (this.unique) {
            contains = SUPPORT_UNIQUE_INCLUDE.contains(ctx.dialect());
        } else {
            contains = SUPPORT_INCLUDE.contains(ctx.dialect());
        }
        boolean supportsInclude = contains;
        QueryPartList<QueryPart> list = new QueryPartList().qualify(false);
        if (NO_SUPPORT_SORT_SPEC.contains(ctx.dialect())) {
            list.addAll(Tools.map(Tools.sortFields(this.on), s -> {
                return s.$field();
            }));
        } else {
            list.addAll(this.on);
        }
        if (!supportsInclude && !this.unique && this.include != null) {
            list.addAll(this.include);
        }
        ctx.visit(Keywords.K_ON).sql(' ').visit(this.table);
        ctx.sql('(').visit(list).sql(')');
        if (supportsInclude && !this.include.isEmpty()) {
            Keyword keyword = Keywords.K_INCLUDE;
            ctx.formatSeparator().visit(keyword).sql(" (").visit(QueryPartCollectionView.wrap(this.include).qualify(false)).sql(')');
        }
        if (this.excludeNullKeys && this.where == null) {
            if (this.on.size() == 1) {
                isNotNull = Tools.field((OrderField) Tools.first(this.on)).isNotNull();
            } else {
                isNotNull = DSL.row((Collection<?>) Tools.fields(this.on)).isNotNull();
            }
            condition = isNotNull;
        } else {
            condition = this.where;
        }
        if (condition != null && ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null) {
            Condition condition2 = condition;
            ctx.formatSeparator().visit(Keywords.K_WHERE).sql(' ').qualify(false, c -> {
                c.visit(condition2);
            });
        }
    }

    private final Name generatedName() {
        Name t = this.table.getQualifiedName();
        StringBuilder sb = new StringBuilder(this.table.getName());
        Iterator<? extends OrderField<?>> it = this.on.iterator();
        while (it.hasNext()) {
            OrderField<?> f = it.next();
            sb.append('_').append(Tools.field((OrderField) f).getName());
        }
        sb.append("_idx");
        if (t.qualified()) {
            return t.qualifier().append(sb.toString());
        }
        return DSL.name(sb.toString());
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final boolean $unique() {
        return this.unique;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final Index $index() {
        return this.index;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final Table<?> $table() {
        return this.table;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.UnmodifiableList<? extends OrderField<?>> $on() {
        return QOM.unmodifiable((List) this.on);
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.UnmodifiableList<? extends Field<?>> $include() {
        return QOM.unmodifiable((List) this.include);
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final Condition $where() {
        return this.where;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final boolean $excludeNullKeys() {
        return this.excludeNullKeys;
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $unique(boolean newValue) {
        return $constructor().apply(Boolean.valueOf(newValue), $index(), Boolean.valueOf($ifNotExists()), $table(), $on(), $include(), $where(), Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $index(Index newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), newValue, Boolean.valueOf($ifNotExists()), $table(), $on(), $include(), $where(), Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $ifNotExists(boolean newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), $index(), Boolean.valueOf(newValue), $table(), $on(), $include(), $where(), Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $table(Table<?> newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), $index(), Boolean.valueOf($ifNotExists()), newValue, $on(), $include(), $where(), Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $on(Collection<? extends OrderField<?>> newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), $index(), Boolean.valueOf($ifNotExists()), $table(), newValue, $include(), $where(), Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $include(Collection<? extends Field<?>> newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), $index(), Boolean.valueOf($ifNotExists()), $table(), $on(), newValue, $where(), Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $where(Condition newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), $index(), Boolean.valueOf($ifNotExists()), $table(), $on(), $include(), newValue, Boolean.valueOf($excludeNullKeys()));
    }

    @Override // org.jooq.impl.QOM.CreateIndex
    public final QOM.CreateIndex $excludeNullKeys(boolean newValue) {
        return $constructor().apply(Boolean.valueOf($unique()), $index(), Boolean.valueOf($ifNotExists()), $table(), $on(), $include(), $where(), Boolean.valueOf(newValue));
    }

    public final Function8<? super Boolean, ? super Index, ? super Boolean, ? super Table<?>, ? super Collection<? extends OrderField<?>>, ? super Collection<? extends Field<?>>, ? super Condition, ? super Boolean, ? extends QOM.CreateIndex> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7, a8) -> {
            return new CreateIndexImpl(configuration(), a1.booleanValue(), a2, a3.booleanValue(), a4, a5, a6, a7, a8.booleanValue());
        };
    }
}

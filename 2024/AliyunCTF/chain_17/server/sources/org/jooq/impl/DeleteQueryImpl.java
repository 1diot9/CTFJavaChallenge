package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectField;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.UniqueKey;
import org.jooq.conf.ParamType;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DeleteQueryImpl.class */
public final class DeleteQueryImpl<R extends Record> extends AbstractDMLQuery<R> implements DeleteQuery<R>, QOM.Delete<R> {
    private static final Clause[] CLAUSES = {Clause.DELETE};
    private static final Set<SQLDialect> SPECIAL_DELETE_AS_SYNTAX = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> NO_SUPPORT_LIMIT = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_ORDER_BY_LIMIT = SQLDialect.supportedBy(SQLDialect.IGNITE);
    private static final Set<SQLDialect> SUPPORT_MULTITABLE_DELETE = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> REQUIRE_REPEAT_FROM_IN_USING = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> NO_SUPPORT_REPEAT_FROM_IN_USING = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private final TableList using;
    private final ConditionProviderImpl condition;
    private final SortFieldList orderBy;
    private Field<? extends Number> limit;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeleteQueryImpl(Configuration configuration, WithImpl with, Table<R> table) {
        super(configuration, with, table);
        this.using = new TableList();
        this.condition = new ConditionProviderImpl();
        this.orderBy = new SortFieldList();
    }

    final Condition getWhere() {
        return this.condition.getWhere();
    }

    final boolean hasWhere() {
        return this.condition.hasWhere();
    }

    final TableList getUsing() {
        return this.using;
    }

    @Override // org.jooq.DeleteQuery
    public final void addUsing(Collection<? extends TableLike<?>> f) {
        for (TableLike<?> provider : f) {
            this.using.add((TableList) provider.asTable());
        }
    }

    @Override // org.jooq.DeleteQuery
    public final void addUsing(TableLike<?> f) {
        this.using.add((TableList) f.asTable());
    }

    @Override // org.jooq.DeleteQuery
    public final void addUsing(TableLike<?>... f) {
        for (TableLike<?> provider : f) {
            this.using.add((TableList) provider.asTable());
        }
    }

    @Override // org.jooq.DeleteQuery, org.jooq.ConditionProvider
    public final void addConditions(Collection<? extends Condition> conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.DeleteQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.DeleteQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition... conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.DeleteQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.DeleteQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition... conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.DeleteQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.DeleteQuery
    public final void addOrderBy(OrderField<?>... fields) {
        addOrderBy(Arrays.asList(fields));
    }

    @Override // org.jooq.DeleteQuery
    public final void addOrderBy(Collection<? extends OrderField<?>> fields) {
        this.orderBy.addAll(Tools.sortFields(fields));
    }

    @Override // org.jooq.DeleteQuery
    public final void addLimit(Number numberOfRows) {
        addLimit(DSL.val(numberOfRows));
    }

    @Override // org.jooq.DeleteQuery
    public final void addLimit(Field<? extends Number> numberOfRows) {
        if (numberOfRows instanceof NoField) {
            return;
        }
        this.limit = numberOfRows;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.scopeStart(this);
        Table<?> t = table(ctx);
        if (InlineDerivedTable.hasInlineDerivedTables(ctx, t) || InlineDerivedTable.hasInlineDerivedTables(ctx, this.using)) {
            ConditionProviderImpl where = new ConditionProviderImpl();
            TableList u = InlineDerivedTable.transformInlineDerivedTables(ctx, this.using, where);
            copy(d -> {
                if (u != this.using) {
                    d.using.clear();
                    d.using.addAll(u);
                }
                if (where.hasWhere()) {
                    d.addConditions(where);
                }
            }, InlineDerivedTable.transformInlineDerivedTables0(ctx, t, where, false)).accept0(ctx);
        } else {
            accept0(ctx);
        }
        ctx.scopeEnd();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v104, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v120, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v46, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v78, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractDMLQuery
    final void accept1(Context<?> ctx) {
        UniqueKey<R> uniqueKey;
        Field<?>[] fieldsArray;
        TableList u;
        ctx.start(Clause.DELETE_DELETE).visit(Keywords.K_DELETE).sql(' ');
        Table<?> t = table(ctx);
        boolean multiTableJoin = SUPPORT_MULTITABLE_DELETE.contains(ctx.dialect()) && (t instanceof JoinTable);
        boolean specialDeleteAsSyntax = SPECIAL_DELETE_AS_SYNTAX.contains(ctx.dialect());
        if (multiTableJoin) {
            ctx.visit(Keywords.K_FROM).sql(' ').visit((QueryPart) Tools.traverseJoins(t, new TableList(), (Predicate<? super TableList>) null, (BiFunction<? super TableList, ? super Table<?>, ? extends TableList>) (r, x) -> {
                r.add((TableList) x);
                return r;
            })).formatSeparator();
        } else {
            ctx.visit(Keywords.K_FROM).sql(' ').declareTables(!specialDeleteAsSyntax, c -> {
                c.visit(t);
            });
        }
        boolean hasUsing = !this.using.isEmpty() || multiTableJoin || (specialDeleteAsSyntax && Tools.alias(t) != null);
        Condition moreWhere = DSL.noCondition();
        if (hasUsing) {
            if (REQUIRE_REPEAT_FROM_IN_USING.contains(ctx.dialect()) && !Tools.containsDeclaredTable(this.using, t)) {
                u = new TableList((Table<?>[]) new Table[]{t});
                u.addAll(this.using);
            } else if (NO_SUPPORT_REPEAT_FROM_IN_USING.contains(ctx.dialect()) && Tools.containsDeclaredTable(this.using, t)) {
                u = new TableList(this.using);
                u.remove(t);
            } else {
                u = this.using;
            }
            TableList u0 = u;
            ctx.formatSeparator().visit(Keywords.K_USING).sql(' ').declareTables(true, c2 -> {
                c2.visit(u0);
            });
        }
        ctx.end(Clause.DELETE_DELETE);
        Condition where = DSL.and(getWhere(), moreWhere);
        if ((this.limit != null && NO_SUPPORT_LIMIT.contains(ctx.dialect())) || (!this.orderBy.isEmpty() && NO_SUPPORT_ORDER_BY_LIMIT.contains(ctx.dialect()))) {
            if (table().getKeys().isEmpty()) {
                fieldsArray = new Field[]{table().rowid()};
            } else {
                if (table().getPrimaryKey() != null) {
                    uniqueKey = table().getPrimaryKey();
                } else {
                    uniqueKey = table().getKeys().get(0);
                }
                fieldsArray = uniqueKey.getFieldsArray();
            }
            Field<?>[] keyFields = fieldsArray;
            ctx.start(Clause.DELETE_WHERE).formatSeparator().visit(Keywords.K_WHERE).sql(' ');
            ctx.paramTypeIf(ParamType.INLINED, false, c3 -> {
                if (keyFields.length == 1) {
                    c3.visit(keyFields[0].in(DSL.select(keyFields[0]).from(table()).where(where).orderBy(this.orderBy).limit(this.limit)));
                } else {
                    c3.visit(DSL.row((SelectField<?>[]) keyFields).in(DSL.select(keyFields).from(table()).where(where).orderBy(this.orderBy).limit(this.limit)));
                }
            });
            ctx.end(Clause.DELETE_WHERE);
        } else {
            ctx.start(Clause.DELETE_WHERE);
            if (!(where instanceof NoCondition)) {
                ctx.paramTypeIf(ParamType.INLINED, false, c4 -> {
                    c4.formatSeparator().visit(Keywords.K_WHERE).sql(' ').visit(where);
                });
            }
            ctx.end(Clause.DELETE_WHERE);
            if (!this.orderBy.isEmpty()) {
                ctx.formatSeparator().visit(Keywords.K_ORDER_BY).sql(' ').visit(this.orderBy);
            }
            acceptLimit(ctx, this.limit);
        }
        ctx.start(Clause.DELETE_RETURNING);
        toSQLReturning(ctx);
        ctx.end(Clause.DELETE_RETURNING);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    public static final void acceptLimit(Context<?> ctx, Field<? extends Number> limit) {
        if (limit != null) {
            if (ctx.family() == SQLDialect.FIREBIRD) {
                ctx.formatSeparator().visit(Keywords.K_ROWS).sql(' ').visit((Field<?>) limit);
            } else {
                ctx.formatSeparator().visit(Keywords.K_LIMIT).sql(' ').visit((Field<?>) limit);
            }
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.Query
    public final boolean isExecutable() {
        if (!this.condition.hasWhere()) {
            executeWithoutWhere("DELETE without WHERE", SettingsTools.getExecuteDeleteWithoutWhere(configuration().settings()));
        }
        return super.isExecutable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final DeleteQueryImpl<R> copy(Consumer<? super DeleteQueryImpl<?>> consumer) {
        return (DeleteQueryImpl<R>) copy(consumer, this.table);
    }

    final <O extends Record> DeleteQueryImpl<O> copy(Consumer<? super DeleteQueryImpl<?>> finisher, Table<O> t) {
        DeleteQueryImpl<O> r = new DeleteQueryImpl<>(configuration(), this.with, t);
        r.using.addAll(this.using);
        r.condition.addConditions(ConditionProviderImpl.extractCondition(this.condition));
        r.orderBy.addAll(this.orderBy);
        r.limit = this.limit;
        if (!this.returning.isEmpty()) {
            r.setReturning(this.returning);
        }
        finisher.accept(r);
        return r;
    }

    @Override // org.jooq.impl.QOM.Delete
    public final WithImpl $with() {
        return this.with;
    }

    @Override // org.jooq.impl.QOM.Delete
    public final Table<R> $from() {
        return this.table;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<?> $from(Table<?> table) {
        if ($from() == table) {
            return this;
        }
        return copy(d -> {
        }, table);
    }

    @Override // org.jooq.impl.QOM.Delete
    public final QOM.UnmodifiableList<? extends Table<?>> $using() {
        return QOM.unmodifiable((List) this.using);
    }

    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $using(Collection<? extends Table<?>> using) {
        return copy(d -> {
            d.using.clear();
            d.using.addAll(using);
        });
    }

    @Override // org.jooq.impl.QOM.Delete
    public final Condition $where() {
        return this.condition.getWhereOrNull();
    }

    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $where(Condition newWhere) {
        if ($where() == newWhere) {
            return this;
        }
        return copy(d -> {
            d.condition.setWhere(newWhere);
        });
    }

    @Override // org.jooq.impl.QOM.Delete
    public final QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return QOM.unmodifiable((List) this.orderBy);
    }

    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $orderBy(Collection<? extends SortField<?>> newOrderBy) {
        return copy(d -> {
            d.orderBy.clear();
            d.orderBy.addAll(newOrderBy);
        });
    }

    @Override // org.jooq.impl.QOM.Delete
    public final Field<? extends Number> $limit() {
        return this.limit;
    }

    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $limit(Field<? extends Number> newLimit) {
        if ($limit() == newLimit) {
            return this;
        }
        return copy(d -> {
            d.limit = newLimit;
        });
    }
}

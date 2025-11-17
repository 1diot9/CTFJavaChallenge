package org.jooq.impl;

import java.util.Iterator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Record;
import org.jooq.Scope;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InlineDerivedTable.class */
public final class InlineDerivedTable<R extends Record> extends DerivedTable<R> {
    final Table<R> table;
    final Condition condition;
    final boolean policyGenerated;

    InlineDerivedTable(TableImpl<R> t) {
        this(t.where((Condition) null), t.where, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InlineDerivedTable(Table<R> table, Condition condition, boolean policyGenerated) {
        super(Lazy.of(() -> {
            return DSL.selectFrom(table).where(condition);
        }), table.getUnqualifiedName());
        this.table = table;
        this.condition = condition;
        this.policyGenerated = policyGenerated;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean hasInlineDerivedTables(Context<?> ctx, Table<?> t) {
        return inlineDerivedTable(ctx, t) != null || ((t instanceof JoinTable) && (hasInlineDerivedTables(ctx, ((JoinTable) t).lhs) || hasInlineDerivedTables(ctx, ((JoinTable) t).rhs)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean hasInlineDerivedTables(Context<?> ctx, TableList tablelist) {
        return Tools.anyMatch(tablelist, t -> {
            return hasInlineDerivedTables((Context<?>) ctx, (Table<?>) t);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final TableList transformInlineDerivedTables(Context<?> ctx, TableList tablelist, ConditionProviderImpl where) {
        if (!hasInlineDerivedTables(ctx, tablelist)) {
            return tablelist;
        }
        TableList result = new TableList();
        Iterator<T> it = tablelist.iterator();
        while (it.hasNext()) {
            Table<?> table = (Table) it.next();
            transformInlineDerivedTable0(ctx, table, result, where);
        }
        return result;
    }

    static final void transformInlineDerivedTable0(Context<?> ctx, Table<?> table, TableList result, ConditionProviderImpl where) {
        Table<?> t = inlineDerivedTable(ctx, table);
        if (t != null) {
            if (t instanceof InlineDerivedTable) {
                InlineDerivedTable<?> i = (InlineDerivedTable) t;
                result.add((TableList) i.table);
                where.addConditions(i.condition);
                return;
            }
            result.add((TableList) t);
            return;
        }
        if (table instanceof JoinTable) {
            result.add((TableList) transformInlineDerivedTables0(ctx, table, where, false));
        } else {
            result.add((TableList) table);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Table<?> transformInlineDerivedTables0(Context<?> ctx, Table<?> table, ConditionProviderImpl where, boolean keepDerivedTable) {
        Table<?> lhs;
        Table<?> rhs;
        Table<?> t = inlineDerivedTable(ctx, table);
        if (t != null) {
            if (t instanceof InlineDerivedTable) {
                InlineDerivedTable<?> i = (InlineDerivedTable) t;
                if (keepDerivedTable) {
                    Table<?> m = DSL.table(((Table) StringUtils.defaultIfNull(ctx.dsl().map(i.table), i.table)).getUnqualifiedName());
                    if (TableImpl.path(i.table) != null) {
                        where.addConditions(((TableImpl) i.table).pathCondition());
                        return DSL.selectFrom(Tools.unwrap(i.table).as(m)).asTable(m);
                    }
                    return i.query().asTable(m);
                }
                where.addConditions(i.condition);
                return i.table;
            }
            return t;
        }
        if (table instanceof JoinTable) {
            JoinTable<?> j = (JoinTable) table;
            ConditionProviderImpl w = new ConditionProviderImpl();
            switch (j.type) {
                case LEFT_OUTER_JOIN:
                case LEFT_ANTI_JOIN:
                case LEFT_SEMI_JOIN:
                case STRAIGHT_JOIN:
                case CROSS_APPLY:
                case OUTER_APPLY:
                case NATURAL_LEFT_OUTER_JOIN:
                    lhs = transformInlineDerivedTables0(ctx, j.lhs, where, keepDerivedTable);
                    rhs = transformInlineDerivedTables0(ctx, j.rhs, w, true);
                    break;
                case RIGHT_OUTER_JOIN:
                case NATURAL_RIGHT_OUTER_JOIN:
                    lhs = transformInlineDerivedTables0(ctx, j.lhs, w, true);
                    rhs = transformInlineDerivedTables0(ctx, j.rhs, where, keepDerivedTable);
                    break;
                case FULL_OUTER_JOIN:
                case NATURAL_FULL_OUTER_JOIN:
                    lhs = transformInlineDerivedTables0(ctx, j.lhs, w, true);
                    rhs = transformInlineDerivedTables0(ctx, j.rhs, w, true);
                    break;
                default:
                    lhs = transformInlineDerivedTables0(ctx, j.lhs, where, keepDerivedTable);
                    rhs = transformInlineDerivedTables0(ctx, j.rhs, where, keepDerivedTable);
                    break;
            }
            return j.transform(lhs, rhs, w.hasWhere() ? w : j.condition);
        }
        return table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> inlineDerivedTable(Scope ctx, Table<R> t) {
        return derivedTable(t);
    }

    static final <R extends Record> Table<R> derivedTable(Table<R> t) {
        if (t instanceof InlineDerivedTable) {
            return (InlineDerivedTable) t;
        }
        if (t instanceof TableImpl) {
            TableImpl<R> i = (TableImpl) t;
            if (i.where != null) {
                return new InlineDerivedTable(i);
            }
            Table<R> unaliased = Tools.unalias(i);
            if (unaliased instanceof TableImpl) {
                TableImpl<R> u = (TableImpl) unaliased;
                if (u.where != null) {
                    return new InlineDerivedTable(u).query().asTable(i);
                }
                return null;
            }
            return null;
        }
        if (t instanceof TableAlias) {
            TableAlias<R> a = (TableAlias) t;
            Table<R> $aliased = a.$aliased();
            if ($aliased instanceof TableImpl) {
                TableImpl<R> u2 = (TableImpl) $aliased;
                if (u2.where != null) {
                    TableLike query = new InlineDerivedTable(u2).query();
                    if (a.hasFieldAliases()) {
                        return query.asTable(a.getUnqualifiedName(), a.alias.fieldAliases);
                    }
                    return query.asTable(a);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.DerivedTable, org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        if (this.table instanceof TableAlias) {
            return new FieldsImpl<>(this.table.fields());
        }
        return new FieldsImpl<>(Tools.qualify(DSL.table(this.table.getUnqualifiedName()), this.table.as((Table<?>) this.table).fields()));
    }
}

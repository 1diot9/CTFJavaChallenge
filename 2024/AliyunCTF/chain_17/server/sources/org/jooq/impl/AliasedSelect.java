package org.jooq.impl;

import java.util.function.Predicate;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AliasedSelect.class */
public final class AliasedSelect<R extends Record> extends AbstractTable<R> implements QOM.UTransient {
    private final Select<R> query;
    private final boolean subquery;
    private final boolean ignoreOrderBy;
    private final boolean forceLimit;
    private final Name[] aliases;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AliasedSelect(Select<R> query, boolean subquery, boolean ignoreOrderBy, boolean forceLimit) {
        this(query, subquery, ignoreOrderBy, forceLimit, Tools.fieldNames(Tools.degree(query)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AliasedSelect(Select<R> query, boolean subquery, boolean ignoreOrderBy, boolean forceLimit, Name... aliases) {
        super(TableOptions.expression(), Names.NQ_SELECT);
        this.query = query;
        this.subquery = subquery;
        this.ignoreOrderBy = ignoreOrderBy;
        this.forceLimit = forceLimit;
        this.aliases = aliases;
    }

    final Select<R> query() {
        return this.query;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<R> as(Name alias) {
        SelectQueryImpl<R> q = Tools.selectQueryImpl(this.query);
        if (q != null && ((this.ignoreOrderBy && !q.getOrderBy().isEmpty()) || Tools.hasEmbeddedFields(q.getSelect()))) {
            return this.query.asTable(alias, this.aliases);
        }
        return new TableAlias(this, alias, (Predicate<Context<?>>) c -> {
            return true;
        });
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<R> as(Name alias, Name... fieldAliases) {
        return new TableAlias(this, alias, fieldAliases, c -> {
            return true;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractTable
    public final FieldsImpl<R> fields0() {
        return new FieldsImpl<>(this.query.asTable(DSL.name("t"), this.aliases).fields());
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends R> getRecordType() {
        return this.query.getRecordType();
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.forceLimit) {
            ctx.data(Tools.BooleanDataKey.DATA_FORCE_LIMIT_WITH_ORDER_BY, true, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        SelectQueryImpl<R> q = Tools.selectQueryImpl(this.query);
        if (ctx.family() == SQLDialect.DERBY && q != null && q.hasUnions()) {
            Tools.visitSubquery(ctx, DSL.selectFrom(this.query.asTable(DSL.name("t"), this.aliases)), 1, false);
        } else {
            ctx.data(Tools.SimpleDataKey.DATA_SELECT_ALIASES, this.aliases, this.subquery ? c -> {
                Tools.visitSubquery(c, this.query, 1, false);
            } : c2 -> {
                c2.visit(this.query);
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean avoidAliasPushdown(Context<?> ctx, Select<?> query) {
        SelectQueryImpl<?> q = Tools.selectQueryImpl(query);
        return q != null && ((ctx.family() == SQLDialect.DERBY && q.hasUnions()) || !q.getOrderBy().isEmpty() || Tools.hasEmbeddedFields(q.getSelect()));
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }
}

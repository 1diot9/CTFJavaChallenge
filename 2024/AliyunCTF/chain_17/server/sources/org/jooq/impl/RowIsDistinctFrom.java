package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowIsDistinctFrom.class */
public final class RowIsDistinctFrom extends AbstractCondition implements QOM.UNotYetImplemented {
    private static final Set<SQLDialect> EMULATE_DISTINCT = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY);
    private static final Set<SQLDialect> EMULATE_DISTINCT_SELECT = SQLDialect.supportedBy(SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_DISTINCT_WITH_ARROW = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private final Row lhs;
    private final Row rhsRow;
    private final Select<?> rhsSelect;
    private final boolean not;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowIsDistinctFrom(Row lhs, Row rhs, boolean not) {
        this.lhs = lhs;
        this.rhsRow = rhs;
        this.rhsSelect = null;
        this.not = not;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowIsDistinctFrom(Row lhs, Select<?> rhs, boolean not) {
        this.lhs = lhs;
        this.rhsRow = null;
        this.rhsSelect = rhs;
        this.not = not;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v46, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Condition rowSubqueryCondition;
        if (EMULATE_DISTINCT.contains(ctx.dialect()) || (this.rhsSelect != null && EMULATE_DISTINCT_SELECT.contains(ctx.dialect()))) {
            Select<Record> intersect = DSL.select(this.lhs.fields()).intersect((Select) (this.rhsSelect != null ? this.rhsSelect : DSL.select(this.rhsRow.fields())));
            ctx.visit(this.not ? DSL.exists(intersect) : DSL.notExists(intersect));
            return;
        }
        if (SUPPORT_DISTINCT_WITH_ARROW.contains(ctx.dialect())) {
            if (!this.not) {
                ctx.visit(Keywords.K_NOT).sql('(');
            }
            ctx.visit(this.lhs).sql(" <=> ");
            if (this.rhsRow != null) {
                ctx.visit(this.rhsRow);
            } else {
                Tools.visitSubquery(ctx, this.rhsSelect, 256);
            }
            if (!this.not) {
                ctx.sql(')');
                return;
            }
            return;
        }
        if (SQLDialect.SQLITE == ctx.family()) {
            ctx.visit(this.lhs).sql(' ').visit(Keywords.K_IS).sql(' ');
            if (!this.not) {
                ctx.visit(Keywords.K_NOT).sql(' ');
            }
            if (this.rhsRow != null) {
                ctx.visit(this.rhsRow);
                return;
            } else {
                Tools.visitSubquery(ctx, this.rhsSelect, 256);
                return;
            }
        }
        if (this.rhsRow != null) {
            rowSubqueryCondition = new RowCondition(this.lhs, this.rhsRow, this.not ? Comparator.IS_NOT_DISTINCT_FROM : Comparator.IS_DISTINCT_FROM);
        } else {
            rowSubqueryCondition = new RowSubqueryCondition(this.lhs, this.rhsSelect, this.not ? Comparator.IS_NOT_DISTINCT_FROM : Comparator.IS_DISTINCT_FROM);
        }
        ctx.visit(rowSubqueryCondition);
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }
}

package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.QuantifiedSelect;
import org.jooq.QueryPartInternal;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOrderByStep;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowSubqueryCondition.class */
public final class RowSubqueryCondition extends AbstractCondition implements QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};
    private static final Set<SQLDialect> NO_SUPPORT_NATIVE = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> NO_SUPPORT_QUANTIFIED = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.SQLITE);
    private static final Set<SQLDialect> NO_SUPPORT_QUANTIFIED_OTHER_THAN_IN_NOT_IN = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private final Row left;
    private final Select<?> right;
    private final QuantifiedSelect<?> rightQuantified;
    private final Comparator comparator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowSubqueryCondition(Row left, Select<?> right, Comparator comparator) {
        this.left = left;
        this.right = right;
        this.rightQuantified = null;
        this.comparator = comparator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowSubqueryCondition(Row left, QuantifiedSelect<?> right, Comparator comparator) {
        this.left = left;
        this.right = null;
        this.rightQuantified = right;
        this.comparator = comparator;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(delegate(ctx));
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    private static final boolean inOrNotIn(Comparator comparator, QOM.Quantifier quantifier) {
        return (comparator == Comparator.EQUALS && quantifier == QOM.Quantifier.ANY) || (comparator == Comparator.NOT_EQUALS && quantifier == QOM.Quantifier.ALL);
    }

    private final QueryPartInternal delegate(Context<?> ctx) {
        Comparator comparator;
        if (this.rightQuantified != null) {
            QuantifiedSelectImpl<?> q = (QuantifiedSelectImpl) this.rightQuantified;
            boolean inOrNotIn = inOrNotIn(this.comparator, q.quantifier);
            if (NO_SUPPORT_QUANTIFIED.contains(ctx.dialect()) || (NO_SUPPORT_QUANTIFIED_OTHER_THAN_IN_NOT_IN.contains(ctx.dialect()) && !inOrNotIn)) {
                switch (this.comparator) {
                    case EQUALS:
                    case NOT_EQUALS:
                        if (inOrNotIn) {
                            return new RowSubqueryCondition(this.left, q.query, this.comparator == Comparator.EQUALS ? Comparator.IN : Comparator.NOT_IN);
                        }
                        return emulationUsingExists(ctx, this.left, q.query, this.comparator == Comparator.EQUALS ? Comparator.NOT_EQUALS : Comparator.EQUALS, this.comparator == Comparator.EQUALS);
                    case GREATER:
                    case GREATER_OR_EQUAL:
                    case LESS:
                    case LESS_OR_EQUAL:
                    default:
                        return emulationUsingExists(ctx, this.left, q.query, q.quantifier == QOM.Quantifier.ALL ? this.comparator.inverse() : this.comparator, q.quantifier == QOM.Quantifier.ALL);
                }
            }
            return new Native();
        }
        if (NO_SUPPORT_NATIVE.contains(ctx.dialect())) {
            Row row = this.left;
            Select<?> select = this.right;
            if (this.comparator == Comparator.GREATER || this.comparator == Comparator.GREATER_OR_EQUAL || this.comparator == Comparator.LESS || this.comparator == Comparator.LESS_OR_EQUAL || this.comparator == Comparator.IS_DISTINCT_FROM || this.comparator == Comparator.IS_NOT_DISTINCT_FROM) {
                comparator = this.comparator;
            } else {
                comparator = Comparator.EQUALS;
            }
            return emulationUsingExists(ctx, row, select, comparator, this.comparator == Comparator.NOT_IN || this.comparator == Comparator.NOT_EQUALS);
        }
        return new Native();
    }

    private static final QueryPartInternal emulationUsingExists(Context<?> ctx, Row row, Select<?> select, Comparator comparator, boolean notExists) {
        Select<Record> subselect = emulatedSubselect(ctx, row, select, comparator);
        return (QueryPartInternal) (notExists ? DSL.notExists(subselect) : DSL.exists(subselect));
    }

    private static final SelectOrderByStep<Record> emulatedSubselect(Context<?> ctx, Row row, Select<?> s, Comparator c) {
        RenderContext renderContext;
        Condition rowCondition;
        if (ctx instanceof RenderContext) {
            RenderContext r = (RenderContext) ctx;
            renderContext = r;
        } else {
            renderContext = null;
        }
        RenderContext render = renderContext;
        Row l = Tools.embeddedFieldsRow(row);
        Name table = DSL.name(render == null ? "t" : render.nextAlias());
        Name[] names = Tools.fieldNames(l.size());
        SelectJoinStep<Record> from = DSL.select(new SelectFieldOrAsterisk[0]).from(new AliasedSelect(s, true, true, false, names).as(table));
        if (c == null) {
            rowCondition = DSL.noCondition();
        } else {
            rowCondition = new RowCondition(l, DSL.row((Collection<?>) Tools.fieldsByName(table, names)), c);
        }
        return from.where(rowCondition);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowSubqueryCondition$Native.class */
    public class Native extends AbstractCondition implements QOM.UTransient {
        private Native() {
        }

        @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            SelectQueryImpl<?> s;
            SelectQueryImpl<?> s2;
            if ((RowSubqueryCondition.this.comparator == Comparator.IN || RowSubqueryCondition.this.comparator == Comparator.NOT_IN) && RowSubqueryCondition.this.right != null && (s = Transformations.subqueryWithLimit(RowSubqueryCondition.this.right)) != null && Transformations.NO_SUPPORT_IN_LIMIT.contains(ctx.dialect())) {
                ctx.visit((Condition) new RowSubqueryCondition(RowSubqueryCondition.this.left, DSL.select(DSL.asterisk()).from(s.asTable("t")), RowSubqueryCondition.this.comparator));
                return;
            }
            if ((RowSubqueryCondition.this.comparator == Comparator.EQUALS || RowSubqueryCondition.this.comparator == Comparator.NOT_EQUALS) && RowSubqueryCondition.this.rightQuantified != null && (s2 = Transformations.subqueryWithLimit(RowSubqueryCondition.this.rightQuantified)) != null && Transformations.NO_SUPPORT_IN_LIMIT.contains(ctx.dialect())) {
                ctx.visit((Condition) new RowSubqueryCondition(RowSubqueryCondition.this.left, (QuantifiedSelect<?>) Tools.quantify(((QuantifiedSelectImpl) RowSubqueryCondition.this.rightQuantified).quantifier, DSL.select(DSL.asterisk()).from(s2.asTable("t"))), RowSubqueryCondition.this.comparator));
            } else {
                accept0(ctx);
            }
        }

        /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
        final void accept0(Context<?> ctx) {
            switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
                default:
                    ctx.visit(RowSubqueryCondition.this.left).sql(' ').visit(RowSubqueryCondition.this.comparator.toKeyword()).sql(' ');
                    if (RowSubqueryCondition.this.rightQuantified == null) {
                        ctx.sql(0 != 0 ? "((" : "(").data(Tools.BooleanDataKey.DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY, true, c -> {
                            Tools.visitSubquery(c, RowSubqueryCondition.this.right, 256, false);
                        }).sql(0 != 0 ? "))" : ")");
                        return;
                    } else {
                        ctx.data(Tools.BooleanDataKey.DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY, true, c2 -> {
                            c2.visit(RowSubqueryCondition.this.rightQuantified);
                        });
                        return;
                    }
            }
        }

        @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
        public final Clause[] clauses(Context<?> ctx) {
            return RowSubqueryCondition.CLAUSES;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.RowSubqueryCondition$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowSubqueryCondition$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];

        static {
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.EQUALS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.NOT_EQUALS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.GREATER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.GREATER_OR_EQUAL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.LESS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$jooq$Comparator[Comparator.LESS_OR_EQUAL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }
}

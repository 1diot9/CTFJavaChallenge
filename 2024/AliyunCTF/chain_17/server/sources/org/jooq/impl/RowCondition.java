package org.jooq.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowCondition.class */
public final class RowCondition extends AbstractCondition implements QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES = {Clause.CONDITION, Clause.CONDITION_COMPARISON};
    private static final Set<SQLDialect> EMULATE_EQ_AND_NE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> EMULATE_RANGES = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD);
    private final Row left;
    private final Row right;
    private final Comparator comparator;
    private final boolean forceEmulation;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowCondition(Row left, Row right, Comparator comparator) {
        this(left, right, comparator, false);
    }

    RowCondition(Row left, Row right, Comparator comparator, boolean forceEmulation) {
        this.left = ((AbstractRow) left).convertTo(right);
        this.right = ((AbstractRow) right).convertTo(left);
        this.comparator = comparator;
        this.forceEmulation = forceEmulation;
    }

    /* JADX WARN: Type inference failed for: r0v91, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        Comparator comparator;
        Comparator comparator2;
        if ((this.comparator == Comparator.EQUALS || this.comparator == Comparator.NOT_EQUALS) && (this.forceEmulation || EMULATE_EQ_AND_NE.contains(ctx.dialect()))) {
            Field<?>[] rightFields = this.right.fields();
            Condition result = DSL.and(Tools.map(this.left.fields(), (f, i) -> {
                return f.equal(rightFields[i]);
            }));
            if (this.comparator == Comparator.NOT_EQUALS) {
                result = result.not();
            }
            ctx.visit(result);
            return;
        }
        if ((this.comparator == Comparator.GREATER || this.comparator == Comparator.GREATER_OR_EQUAL || this.comparator == Comparator.LESS || this.comparator == Comparator.LESS_OR_EQUAL) && (this.forceEmulation || EMULATE_RANGES.contains(ctx.dialect()))) {
            if (this.comparator == Comparator.GREATER) {
                comparator = Comparator.GREATER;
            } else if (this.comparator == Comparator.GREATER_OR_EQUAL) {
                comparator = Comparator.GREATER;
            } else if (this.comparator == Comparator.LESS) {
                comparator = Comparator.LESS;
            } else {
                comparator = this.comparator == Comparator.LESS_OR_EQUAL ? Comparator.LESS : null;
            }
            Comparator order = comparator;
            if (this.comparator == Comparator.GREATER) {
                comparator2 = Comparator.GREATER_OR_EQUAL;
            } else if (this.comparator == Comparator.GREATER_OR_EQUAL) {
                comparator2 = Comparator.GREATER_OR_EQUAL;
            } else if (this.comparator == Comparator.LESS) {
                comparator2 = Comparator.LESS_OR_EQUAL;
            } else {
                comparator2 = this.comparator == Comparator.LESS_OR_EQUAL ? Comparator.LESS_OR_EQUAL : null;
            }
            Comparator factoredOrder = comparator2;
            boolean equal = this.comparator == Comparator.GREATER_OR_EQUAL || this.comparator == Comparator.LESS_OR_EQUAL;
            Field<?>[] leftFields = this.left.fields();
            Field<?>[] rightFields2 = this.right.fields();
            List<Condition> outer = new ArrayList<>(1 + leftFields.length);
            int i2 = 0;
            while (i2 < leftFields.length) {
                List<Condition> inner = new ArrayList<>(1 + i2);
                for (int j = 0; j < i2; j++) {
                    inner.add(leftFields[j].equal(rightFields2[j]));
                }
                inner.add(leftFields[i2].compare((equal && i2 == leftFields.length - 1) ? this.comparator : order, rightFields2[i2]));
                outer.add(DSL.and(inner));
                i2++;
            }
            Condition result2 = DSL.or(outer);
            if (leftFields.length > 1) {
                result2 = leftFields[0].compare(factoredOrder, rightFields2[0]).and(result2);
            }
            ctx.visit(result2);
            return;
        }
        ctx.visit(this.left).sql(' ').sql(this.comparator.toSQL()).sql(' ').sql(0 != 0 ? "(" : "").visit(this.right).sql(0 != 0 ? ")" : "");
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }
}

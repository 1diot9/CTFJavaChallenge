package org.jooq.impl;

import java.util.Set;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowEq.class */
public final class RowEq<T extends Row> extends AbstractCondition implements QOM.RowEq<T> {
    final T arg1;
    final T arg2;
    private static final Set<SQLDialect> EMULATE_EQ_AND_NE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> EMULATE_RANGES = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD);

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowEq(T arg1, T arg2) {
        this.arg1 = ((AbstractRow) arg1).convertTo(arg2);
        this.arg2 = ((AbstractRow) arg2).convertTo(arg1);
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        acceptCompareCondition(ctx, this, this.arg1, Comparator.EQUALS, this.arg2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v49, types: [org.jooq.Context] */
    @Deprecated
    public static final <T extends Row> void acceptCompareCondition(Context<?> ctx, AbstractCondition condition, T arg1, Comparator op, T arg2) {
        Comparator comparator;
        Comparator comparator2;
        if ((op == Comparator.EQUALS || op == Comparator.NOT_EQUALS) && EMULATE_EQ_AND_NE.contains(ctx.dialect())) {
            Field<?>[] arg2Fields = arg2.fields();
            Condition result = DSL.and(Tools.map(arg1.fields(), (f, i) -> {
                return f.equal(arg2Fields[i]);
            }));
            if (op == Comparator.NOT_EQUALS) {
                result = result.not();
            }
            ctx.visit(result);
            return;
        }
        if ((op == Comparator.GREATER || op == Comparator.GREATER_OR_EQUAL || op == Comparator.LESS || op == Comparator.LESS_OR_EQUAL) && EMULATE_RANGES.contains(ctx.dialect())) {
            if (op == Comparator.GREATER) {
                comparator = Comparator.GREATER;
            } else if (op == Comparator.GREATER_OR_EQUAL) {
                comparator = Comparator.GREATER;
            } else if (op == Comparator.LESS) {
                comparator = Comparator.LESS;
            } else {
                comparator = op == Comparator.LESS_OR_EQUAL ? Comparator.LESS : null;
            }
            Comparator comp = comparator;
            if (op == Comparator.GREATER) {
                comparator2 = Comparator.GREATER_OR_EQUAL;
            } else if (op == Comparator.GREATER_OR_EQUAL) {
                comparator2 = Comparator.GREATER_OR_EQUAL;
            } else if (op == Comparator.LESS) {
                comparator2 = Comparator.LESS_OR_EQUAL;
            } else {
                comparator2 = op == Comparator.LESS_OR_EQUAL ? Comparator.LESS_OR_EQUAL : null;
            }
            Comparator factored = comparator2;
            Condition result2 = emulate(arg1, arg2, comp, op);
            if (arg1.size() > 1) {
                result2 = arg1.field(0).compare(factored, arg2.field(0)).and(result2);
            }
            ctx.visit(result2);
            return;
        }
        ctx.visit(arg1).sql(' ').sql(op.toSQL()).sql(' ').sql(0 != 0 ? "(" : "").visit(arg2).sql(0 != 0 ? ")" : "");
    }

    private static final Condition emulate(Row r1, Row r2, Comparator comp, Comparator last) {
        Condition result = r1.field(r1.size() - 1).compare(last, r2.field(r1.size() - 1));
        for (int i = r1.size() - 2; i >= 0; i--) {
            Field e1 = r1.field(i);
            Field e2 = r2.field(i);
            result = e1.compare(comp, e2).or(e1.eq(e2).and(result));
        }
        return result;
    }

    @Deprecated
    static final Comparator comparator(Condition condition) {
        if (condition instanceof RowEq) {
            return Comparator.EQUALS;
        }
        if (condition instanceof RowNe) {
            return Comparator.NOT_EQUALS;
        }
        if (condition instanceof RowGt) {
            return Comparator.GREATER;
        }
        if (condition instanceof RowGe) {
            return Comparator.GREATER_OR_EQUAL;
        }
        if (condition instanceof RowLt) {
            return Comparator.LESS;
        }
        if (condition instanceof RowLe) {
            return Comparator.LESS_OR_EQUAL;
        }
        return null;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final T $arg1() {
        return this.arg1;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final T $arg2() {
        return this.arg2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.RowEq<T> $arg1(T t) {
        return $constructor().apply(t, $arg2());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.RowEq<T> $arg2(T t) {
        return $constructor().apply($arg1(), t);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super T, ? super T, ? extends QOM.RowEq<T>> $constructor() {
        return (a1, a2) -> {
            return new RowEq(a1, a2);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.RowEq)) {
            return super.equals(that);
        }
        QOM.RowEq<?> o = (QOM.RowEq) that;
        return StringUtils.equals($arg1(), o.$arg1()) && StringUtils.equals($arg2(), o.$arg2());
    }
}

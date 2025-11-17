package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RowInCondition.class */
public final class RowInCondition extends AbstractCondition implements QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES_IN = {Clause.CONDITION, Clause.CONDITION_IN};
    private static final Clause[] CLAUSES_IN_NOT = {Clause.CONDITION, Clause.CONDITION_NOT_IN};
    private static final Set<SQLDialect> EMULATE_IN = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.SQLITE);
    private final Row left;
    private final QueryPartList<? extends Row> right;
    private final boolean not;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RowInCondition(Row left, QueryPartList<? extends Row> right, boolean not) {
        this.left = left;
        this.right = right;
        this.not = not;
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (EMULATE_IN.contains(ctx.dialect())) {
            Condition result = DSL.or(Tools.map(this.right, r -> {
                return AbstractRow.compare(this.left, Comparator.EQUALS, r);
            }));
            if (this.not) {
                result = result.not();
            }
            ctx.visit(result);
            return;
        }
        if (this.right.size() == 0) {
            if (this.not) {
                ctx.visit((Condition) DSL.trueCondition());
                return;
            } else {
                ctx.visit((Condition) DSL.falseCondition());
                return;
            }
        }
        ctx.visit(this.left).sql(' ').visit((this.not ? Comparator.NOT_IN : Comparator.IN).toKeyword()).sql(" (").visit(new QueryPartListView(AbstractInList.padded(ctx, this.right, AbstractInList.limit(ctx)))).sql(')');
    }

    @Override // org.jooq.impl.AbstractCondition, org.jooq.impl.AbstractField, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }
}

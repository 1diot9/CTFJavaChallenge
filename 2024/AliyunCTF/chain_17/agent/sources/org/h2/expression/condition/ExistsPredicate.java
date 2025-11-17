package org.h2.expression.condition;

import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ExistsPredicate.class */
public class ExistsPredicate extends PredicateWithSubquery {
    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ int getCost() {
        return super.getCost();
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ boolean isEverything(ExpressionVisitor expressionVisitor) {
        return super.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ void updateAggregate(SessionLocal sessionLocal, int i) {
        super.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ void setEvaluatable(TableFilter tableFilter, boolean z) {
        super.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ Expression optimize(SessionLocal sessionLocal) {
        return super.optimize(sessionLocal);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public /* bridge */ /* synthetic */ void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        super.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ExistsPredicate(Query query) {
        super(query);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        this.query.setSession(sessionLocal);
        return ValueBoolean.get(this.query.exists());
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return super.getUnenclosedSQL(sb.append("EXISTS"), i);
    }
}

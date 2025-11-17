package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/SimplePredicate.class */
public abstract class SimplePredicate extends Condition {
    Expression left;
    final boolean not;
    final boolean whenOperand;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimplePredicate(Expression expression, boolean z, boolean z2) {
        this.left = expression;
        this.not = z;
        this.whenOperand = z2;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (!this.whenOperand && this.left.isConstant()) {
            return ValueExpression.getBoolean(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public final boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost() + 1;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 1;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i == 0) {
            return this.left;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // org.h2.expression.Expression
    public final boolean isWhenConditionOperand() {
        return this.whenOperand;
    }
}

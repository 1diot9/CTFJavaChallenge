package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionNot.class */
public class ConditionNot extends Condition {
    private Expression condition;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ConditionNot(Expression expression) {
        this.condition = expression;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        return castToBoolean(sessionLocal, this.condition.optimize(sessionLocal));
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.condition.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return value;
        }
        return value.convertToBoolean().negate();
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.condition.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Expression notIfPossible = this.condition.getNotIfPossible(sessionLocal);
        if (notIfPossible != null) {
            return notIfPossible.optimize(sessionLocal);
        }
        Expression optimize = this.condition.optimize(sessionLocal);
        if (optimize.isConstant()) {
            Value value = optimize.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return TypedValueExpression.UNKNOWN;
            }
            return ValueExpression.getBoolean(!value.getBoolean());
        }
        this.condition = optimize;
        return this;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.condition.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return this.condition.getSQL(sb.append("NOT "), i, 0);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.condition.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.condition.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.condition.getCost();
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 1;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i == 0) {
            return this.condition;
        }
        throw new IndexOutOfBoundsException();
    }
}

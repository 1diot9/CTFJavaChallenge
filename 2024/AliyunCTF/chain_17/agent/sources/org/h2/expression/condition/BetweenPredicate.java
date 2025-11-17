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
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/BetweenPredicate.class */
public final class BetweenPredicate extends Condition {
    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private boolean symmetric;
    private Expression a;
    private Expression b;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public BetweenPredicate(Expression expression, boolean z, boolean z2, boolean z3, Expression expression2, Expression expression3) {
        this.left = expression;
        this.not = z;
        this.whenOperand = z2;
        this.symmetric = z3;
        this.a = expression2;
        this.b = expression3;
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return getWhenSQL(this.left.getSQL(sb, i, 0), i);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        if (this.not) {
            sb.append(" NOT");
        }
        sb.append(" BETWEEN ");
        if (this.symmetric) {
            sb.append("SYMMETRIC ");
        }
        this.a.getSQL(sb, i, 0).append(" AND ");
        return this.b.getSQL(sb, i, 0);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.a = this.a.optimize(sessionLocal);
        this.b = this.b.optimize(sessionLocal);
        TypeInfo type = this.left.getType();
        TypeInfo.checkComparable(type, this.a.getType());
        TypeInfo.checkComparable(type, this.b.getType());
        if (this.whenOperand) {
            return this;
        }
        Value value = this.left.isConstant() ? this.left.getValue(sessionLocal) : null;
        Value value2 = this.a.isConstant() ? this.a.getValue(sessionLocal) : null;
        Value value3 = this.b.isConstant() ? this.b.getValue(sessionLocal) : null;
        if (value != null) {
            if (value == ValueNull.INSTANCE) {
                return TypedValueExpression.UNKNOWN;
            }
            if (value2 != null && value3 != null) {
                return ValueExpression.getBoolean(getValue(sessionLocal, value, value2, value3));
            }
        }
        if (this.symmetric) {
            if (value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) {
                return TypedValueExpression.UNKNOWN;
            }
        } else if (value2 == ValueNull.INSTANCE && value3 == ValueNull.INSTANCE) {
            return TypedValueExpression.UNKNOWN;
        }
        if (value2 != null && value3 != null && sessionLocal.compareWithNull(value2, value3, false) == 0) {
            return new Comparison(this.not ? 1 : 0, this.left, this.a, false).optimize(sessionLocal);
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        return getValue(sessionLocal, value, this.a.getValue(sessionLocal), this.b.getValue(sessionLocal));
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        if (value == ValueNull.INSTANCE) {
            return false;
        }
        return getValue(sessionLocal, value, this.a.getValue(sessionLocal), this.b.getValue(sessionLocal)).isTrue();
    }

    private Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        int compareWithNull = sessionLocal.compareWithNull(value2, value, false);
        int compareWithNull2 = sessionLocal.compareWithNull(value, value3, false);
        if (compareWithNull == Integer.MIN_VALUE) {
            return (this.symmetric || compareWithNull2 <= 0) ? ValueNull.INSTANCE : ValueBoolean.get(this.not);
        }
        if (compareWithNull2 == Integer.MIN_VALUE) {
            return (this.symmetric || compareWithNull <= 0) ? ValueNull.INSTANCE : ValueBoolean.get(this.not);
        }
        return ValueBoolean.get(this.not ^ (this.symmetric ? (compareWithNull <= 0 && compareWithNull2 <= 0) || (compareWithNull >= 0 && compareWithNull2 >= 0) : compareWithNull <= 0 && compareWithNull2 <= 0));
    }

    @Override // org.h2.expression.Expression
    public boolean isWhenConditionOperand() {
        return this.whenOperand;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new BetweenPredicate(this.left, !this.not, false, this.symmetric, this.a, this.b);
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (!this.not && !this.whenOperand && !this.symmetric) {
            Comparison.createIndexConditions(tableFilter, this.a, this.left, 4);
            Comparison.createIndexConditions(tableFilter, this.left, this.b, 4);
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        this.a.setEvaluatable(tableFilter, z);
        this.b.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        this.a.updateAggregate(sessionLocal, i);
        this.b.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        this.a.mapColumns(columnResolver, i, i2);
        this.b.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor) && this.a.isEverything(expressionVisitor) && this.b.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost() + this.a.getCost() + this.b.getCost() + 1;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 3;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        switch (i) {
            case 0:
                return this.left;
            case 1:
                return this.a;
            case 2:
                return this.b;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}

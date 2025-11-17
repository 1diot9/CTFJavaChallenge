package org.h2.expression.condition;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/NullPredicate.class */
public final class NullPredicate extends SimplePredicate {
    private boolean optimized;

    public NullPredicate(Expression expression, boolean z, boolean z2) {
        super(expression, z, z2);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return getWhenSQL(this.left.getSQL(sb, i, 0), i);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        return sb.append(this.not ? " IS NOT NULL" : " IS NULL");
    }

    @Override // org.h2.expression.condition.SimplePredicate, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        if (this.optimized) {
            return this;
        }
        Expression optimize = super.optimize(sessionLocal);
        if (optimize != this) {
            return optimize;
        }
        this.optimized = true;
        if (!this.whenOperand && (this.left instanceof ExpressionList)) {
            ExpressionList expressionList = (ExpressionList) this.left;
            if (!expressionList.isArray()) {
                int i = 0;
                int subexpressionCount = expressionList.getSubexpressionCount();
                while (true) {
                    if (i >= subexpressionCount) {
                        break;
                    }
                    if (!expressionList.getSubexpression(i).isNullConstant()) {
                        i++;
                    } else {
                        if (this.not) {
                            return ValueExpression.FALSE;
                        }
                        ArrayList arrayList = new ArrayList(subexpressionCount - 1);
                        for (int i2 = 0; i2 < i; i2++) {
                            arrayList.add(expressionList.getSubexpression(i2));
                        }
                        for (int i3 = i + 1; i3 < subexpressionCount; i3++) {
                            Expression subexpression = expressionList.getSubexpression(i3);
                            if (!subexpression.isNullConstant()) {
                                arrayList.add(subexpression);
                            }
                        }
                        this.left = arrayList.size() == 1 ? (Expression) arrayList.get(0) : new ExpressionList((Expression[]) arrayList.toArray(new Expression[0]), false);
                    }
                }
            }
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return ValueBoolean.get(getValue(this.left.getValue(sessionLocal)));
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        return getValue(value);
    }

    private boolean getValue(Value value) {
        if (value.getType().getValueType() != 41) {
            return (value == ValueNull.INSTANCE) ^ this.not;
        }
        for (Value value2 : ((ValueRow) value).getList()) {
            if ((value2 != ValueNull.INSTANCE) ^ this.not) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        Expression optimize = optimize(sessionLocal);
        if (optimize != this) {
            return optimize.getNotIfPossible(sessionLocal);
        }
        switch (this.left.getType().getValueType()) {
            case -1:
            case 41:
                return null;
            default:
                return new NullPredicate(this.left, !this.not, false);
        }
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.not || this.whenOperand || !tableFilter.getTable().isQueryComparable()) {
            return;
        }
        if (this.left instanceof ExpressionColumn) {
            createNullIndexCondition(tableFilter, (ExpressionColumn) this.left);
            return;
        }
        if (this.left instanceof ExpressionList) {
            ExpressionList expressionList = (ExpressionList) this.left;
            if (!expressionList.isArray()) {
                int subexpressionCount = expressionList.getSubexpressionCount();
                for (int i = 0; i < subexpressionCount; i++) {
                    Expression subexpression = expressionList.getSubexpression(i);
                    if (subexpression instanceof ExpressionColumn) {
                        createNullIndexCondition(tableFilter, (ExpressionColumn) subexpression);
                    }
                }
            }
        }
    }

    private static void createNullIndexCondition(TableFilter tableFilter, ExpressionColumn expressionColumn) {
        if (tableFilter == expressionColumn.getTableFilter() && expressionColumn.getType().getValueType() != 41) {
            tableFilter.addIndexCondition(IndexCondition.get(6, expressionColumn, ValueExpression.NULL));
        }
    }
}

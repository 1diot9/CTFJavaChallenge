package org.h2.expression.condition;

import java.util.ArrayList;
import org.h2.engine.Constants;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/BooleanTest.class */
public final class BooleanTest extends SimplePredicate {
    private final Boolean right;

    public BooleanTest(Expression expression, boolean z, boolean z2, Boolean bool) {
        super(expression, z, z2);
        this.right = bool;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return getWhenSQL(this.left.getSQL(sb, i, 0), i);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        return sb.append(this.not ? " IS NOT " : " IS ").append(this.right == null ? "UNKNOWN" : this.right.booleanValue() ? Constants.CLUSTERING_ENABLED : "FALSE");
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
        return (value == ValueNull.INSTANCE ? this.right == null : this.right != null && this.right.booleanValue() == value.getBoolean()) ^ this.not;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new BooleanTest(this.left, !this.not, false, this.right);
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (!this.whenOperand && tableFilter.getTable().isQueryComparable() && (this.left instanceof ExpressionColumn)) {
            ExpressionColumn expressionColumn = (ExpressionColumn) this.left;
            if (expressionColumn.getType().getValueType() == 8 && tableFilter == expressionColumn.getTableFilter()) {
                if (this.not) {
                    if (this.right == null && expressionColumn.getColumn().isNullable()) {
                        ArrayList arrayList = new ArrayList(2);
                        arrayList.add(ValueExpression.FALSE);
                        arrayList.add(ValueExpression.TRUE);
                        tableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, arrayList));
                        return;
                    }
                    return;
                }
                tableFilter.addIndexCondition(IndexCondition.get(6, expressionColumn, this.right == null ? TypedValueExpression.UNKNOWN : ValueExpression.getBoolean(this.right.booleanValue())));
            }
        }
    }
}

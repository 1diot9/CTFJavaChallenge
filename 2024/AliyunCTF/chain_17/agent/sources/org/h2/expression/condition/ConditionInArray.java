package org.h2.expression.condition;

import java.util.AbstractList;
import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionInArray.class */
public class ConditionInArray extends Condition {
    private Expression left;
    private final boolean whenOperand;
    private Expression right;
    private final boolean all;
    private final int compareType;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionInArray$ParameterList.class */
    private static final class ParameterList extends AbstractList<Expression> {
        private final Parameter parameter;

        ParameterList(Parameter parameter) {
            this.parameter = parameter;
        }

        @Override // java.util.AbstractList, java.util.List
        public Expression get(int i) {
            Value paramValue = this.parameter.getParamValue();
            if (paramValue instanceof ValueArray) {
                return ValueExpression.get(((ValueArray) paramValue).getList()[i]);
            }
            if (i != 0) {
                throw new IndexOutOfBoundsException();
            }
            return ValueExpression.get(paramValue);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            if (!this.parameter.isValueSet()) {
                return 0;
            }
            Value paramValue = this.parameter.getParamValue();
            if (paramValue instanceof ValueArray) {
                return ((ValueArray) paramValue).getList().length;
            }
            return 1;
        }
    }

    public ConditionInArray(Expression expression, boolean z, Expression expression2, boolean z2, int i) {
        this.left = expression;
        this.whenOperand = z;
        this.right = expression2;
        this.all = z2;
        this.compareType = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return getValue(sessionLocal, this.left.getValue(sessionLocal));
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        return getValue(sessionLocal, value).isTrue();
    }

    private Value getValue(SessionLocal sessionLocal, Value value) {
        Value value2 = this.right.getValue(sessionLocal);
        if (value2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        Value[] list = value2.convertToAnyArray(sessionLocal).getList();
        if (list.length == 0) {
            return ValueBoolean.get(this.all);
        }
        if ((this.compareType & (-2)) == 6) {
            return getNullSafeValueSlow(sessionLocal, list, value);
        }
        if (value.containsNull()) {
            return ValueNull.INSTANCE;
        }
        return getValueSlow(sessionLocal, list, value);
    }

    private Value getValueSlow(SessionLocal sessionLocal, Value[] valueArr, Value value) {
        boolean z = false;
        ValueBoolean valueBoolean = ValueBoolean.get(!this.all);
        for (Value value2 : valueArr) {
            Value compare = Comparison.compare(sessionLocal, value, value2, this.compareType);
            if (compare == ValueNull.INSTANCE) {
                z = true;
            } else if (compare == valueBoolean) {
                return ValueBoolean.get(!this.all);
            }
        }
        if (z) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(this.all);
    }

    private Value getNullSafeValueSlow(SessionLocal sessionLocal, Value[] valueArr, Value value) {
        boolean z = this.all == (this.compareType == 7);
        for (Value value2 : valueArr) {
            if (sessionLocal.areEqual(value, value2) == z) {
                return ValueBoolean.get(!this.all);
            }
        }
        return ValueBoolean.get(this.all);
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
        return new ConditionInArray(this.left, false, this.right, !this.all, Comparison.getNotCompareType(this.compareType));
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        this.right.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.right = this.right.optimize(sessionLocal);
        this.left = this.left.optimize(sessionLocal);
        if (!this.whenOperand && this.left.isConstant() && this.right.isConstant()) {
            return ValueExpression.getBoolean(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.whenOperand || this.all || this.compareType != 0 || !(this.left instanceof ExpressionColumn)) {
            return;
        }
        ExpressionColumn expressionColumn = (ExpressionColumn) this.left;
        if (tableFilter != expressionColumn.getTableFilter()) {
            return;
        }
        if (this.right instanceof Parameter) {
            tableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, new ParameterList((Parameter) this.right)));
            return;
        }
        if (this.right.isConstant()) {
            Value value = this.right.getValue(null);
            if (value instanceof ValueArray) {
                Value[] list = ((ValueArray) value).getList();
                int length = list.length;
                if (length == 0) {
                    tableFilter.addIndexCondition(IndexCondition.get(9, expressionColumn, ValueExpression.FALSE));
                    return;
                }
                TypeInfo type = expressionColumn.getType();
                TypeInfo typeInfo = type;
                for (Value value2 : list) {
                    typeInfo = TypeInfo.getHigherType(typeInfo, value2.getType());
                }
                if (TypeInfo.haveSameOrdering(type, typeInfo)) {
                    Expression[] expressionArr = new Expression[length];
                    for (int i = 0; i < length; i++) {
                        expressionArr[i] = ValueExpression.get(list[i]);
                    }
                    tableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, Arrays.asList(expressionArr)));
                    return;
                }
                return;
            }
            return;
        }
        if (this.right.isEverything(ExpressionVisitor.getNotFromResolverVisitor(tableFilter))) {
            TypeInfo type2 = this.right.getType();
            if (type2.getValueType() == 40) {
                TypeInfo type3 = expressionColumn.getType();
                if (TypeInfo.haveSameOrdering(type3, TypeInfo.getHigherType(type3, (TypeInfo) type2.getExtTypeInfo()))) {
                    tableFilter.addIndexCondition(IndexCondition.getInArray(expressionColumn, this.right));
                }
            }
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        this.right.setEvaluatable(tableFilter, z);
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
        return this.right.getSQL(sb.append(' ').append(Comparison.COMPARE_TYPES[this.compareType]).append(this.all ? " ALL(" : " ANY("), i).append(')');
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        this.right.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor) && this.right.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost() + this.right.getCost() + 10;
    }
}

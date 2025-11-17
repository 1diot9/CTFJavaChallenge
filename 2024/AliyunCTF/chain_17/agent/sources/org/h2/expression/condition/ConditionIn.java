package org.h2.expression.condition;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionIn.class */
public final class ConditionIn extends Condition {
    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private final ArrayList<Expression> valueList;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ConditionIn(Expression expression, boolean z, boolean z2, ArrayList<Expression> arrayList) {
        this.left = expression;
        this.not = z;
        this.whenOperand = z2;
        this.valueList = arrayList;
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
        if (value.containsNull()) {
            return ValueNull.INSTANCE;
        }
        boolean z = false;
        Iterator<Expression> it = this.valueList.iterator();
        while (it.hasNext()) {
            Value compare = Comparison.compare(sessionLocal, value, it.next().getValue(sessionLocal), 0);
            if (compare == ValueNull.INSTANCE) {
                z = true;
            } else if (compare == ValueBoolean.TRUE) {
                return ValueBoolean.get(!this.not);
            }
        }
        if (z) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(this.not);
    }

    @Override // org.h2.expression.Expression
    public boolean isWhenConditionOperand() {
        return this.whenOperand;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        Iterator<Expression> it = this.valueList.iterator();
        while (it.hasNext()) {
            it.next().mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        boolean z = !this.whenOperand && this.left.isConstant();
        if (z && this.left.isNullConstant()) {
            return TypedValueExpression.UNKNOWN;
        }
        boolean z2 = true;
        boolean z3 = true;
        TypeInfo type = this.left.getType();
        int size = this.valueList.size();
        for (int i = 0; i < size; i++) {
            Expression optimize = this.valueList.get(i).optimize(sessionLocal);
            TypeInfo.checkComparable(type, optimize.getType());
            if (optimize.isConstant() && !optimize.getValue(sessionLocal).containsNull()) {
                z3 = false;
            }
            if (z2 && !optimize.isConstant()) {
                z2 = false;
            }
            if ((this.left instanceof ExpressionColumn) && (optimize instanceof Parameter)) {
                ((Parameter) optimize).setColumn(((ExpressionColumn) this.left).getColumn());
            }
            this.valueList.set(i, optimize);
        }
        return optimize2(sessionLocal, z, z2, z3, this.valueList);
    }

    private Expression optimize2(SessionLocal sessionLocal, boolean z, boolean z2, boolean z3, ArrayList<Expression> arrayList) {
        if (z && z2) {
            return ValueExpression.getBoolean(getValue(sessionLocal));
        }
        if (arrayList.size() == 1) {
            return new Comparison(this.not ? 1 : 0, this.left, arrayList.get(0), this.whenOperand).optimize(sessionLocal);
        }
        if (z2 && !z3) {
            int valueType = this.left.getType().getValueType();
            if (valueType == -1) {
                return this;
            }
            if (valueType == 36 && !(this.left instanceof ExpressionColumn)) {
                return this;
            }
            return new ConditionInConstantSet(sessionLocal, this.left, this.not, this.whenOperand, arrayList).optimize(sessionLocal);
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new ConditionIn(this.left, !this.not, false, this.valueList);
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.not || this.whenOperand || !sessionLocal.getDatabase().getSettings().optimizeInList) {
            return;
        }
        if (this.left instanceof ExpressionColumn) {
            ExpressionColumn expressionColumn = (ExpressionColumn) this.left;
            if (tableFilter == expressionColumn.getTableFilter()) {
                createIndexConditions(tableFilter, expressionColumn, this.valueList);
                return;
            }
            return;
        }
        if (this.left instanceof ExpressionList) {
            ExpressionList expressionList = (ExpressionList) this.left;
            if (!expressionList.isArray()) {
                createIndexConditions(tableFilter, expressionList);
            }
        }
    }

    private void createIndexConditions(TableFilter tableFilter, ExpressionList expressionList) {
        int subexpressionCount = expressionList.getSubexpressionCount();
        for (int i = 0; i < subexpressionCount; i++) {
            Expression subexpression = expressionList.getSubexpression(i);
            if (subexpression instanceof ExpressionColumn) {
                ExpressionColumn expressionColumn = (ExpressionColumn) subexpression;
                if (tableFilter == expressionColumn.getTableFilter()) {
                    ArrayList arrayList = new ArrayList(this.valueList.size());
                    Iterator<Expression> it = this.valueList.iterator();
                    while (it.hasNext()) {
                        Expression next = it.next();
                        if (next instanceof ExpressionList) {
                            ExpressionList expressionList2 = (ExpressionList) next;
                            if (expressionList2.isArray() || expressionList2.getSubexpressionCount() != subexpressionCount) {
                                return;
                            } else {
                                arrayList.add(expressionList2.getSubexpression(i));
                            }
                        } else if (next instanceof ValueExpression) {
                            Value value = next.getValue(null);
                            if (value.getValueType() != 41) {
                                return;
                            }
                            Value[] list = ((ValueRow) value).getList();
                            if (subexpressionCount != list.length) {
                                return;
                            } else {
                                arrayList.add(ValueExpression.get(list[i]));
                            }
                        } else {
                            return;
                        }
                    }
                    createIndexConditions(tableFilter, expressionColumn, arrayList);
                } else {
                    continue;
                }
            }
        }
    }

    private static void createIndexConditions(TableFilter tableFilter, ExpressionColumn expressionColumn, ArrayList<Expression> arrayList) {
        ExpressionVisitor notFromResolverVisitor = ExpressionVisitor.getNotFromResolverVisitor(tableFilter);
        TypeInfo type = expressionColumn.getType();
        Iterator<Expression> it = arrayList.iterator();
        while (it.hasNext()) {
            Expression next = it.next();
            if (!next.isEverything(notFromResolverVisitor) || !TypeInfo.haveSameOrdering(type, TypeInfo.getHigherType(type, next.getType()))) {
                return;
            }
        }
        tableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, arrayList));
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        Iterator<Expression> it = this.valueList.iterator();
        while (it.hasNext()) {
            it.next().setEvaluatable(tableFilter, z);
        }
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
        return writeExpressions(sb.append(" IN("), this.valueList, i).append(')');
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        Iterator<Expression> it = this.valueList.iterator();
        while (it.hasNext()) {
            it.next().updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!this.left.isEverything(expressionVisitor)) {
            return false;
        }
        return areAllValues(expressionVisitor);
    }

    private boolean areAllValues(ExpressionVisitor expressionVisitor) {
        Iterator<Expression> it = this.valueList.iterator();
        while (it.hasNext()) {
            if (!it.next().isEverything(expressionVisitor)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int cost = this.left.getCost();
        Iterator<Expression> it = this.valueList.iterator();
        while (it.hasNext()) {
            cost += it.next().getCost();
        }
        return cost;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Expression getAdditional(Comparison comparison) {
        Expression ifEquals;
        if (!this.not && !this.whenOperand && this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && (ifEquals = comparison.getIfEquals(this.left)) != null) {
            ArrayList arrayList = new ArrayList(this.valueList.size() + 1);
            arrayList.addAll(this.valueList);
            arrayList.add(ifEquals);
            return new ConditionIn(this.left, false, false, arrayList);
        }
        return null;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 1 + this.valueList.size();
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i == 0) {
            return this.left;
        }
        if (i > 0 && i <= this.valueList.size()) {
            return this.valueList.get(i - 1);
        }
        throw new IndexOutOfBoundsException();
    }
}

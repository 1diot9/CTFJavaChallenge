package org.h2.expression.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.index.IndexCondition;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionInConstantSet.class */
public final class ConditionInConstantSet extends Condition {
    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private final ArrayList<Expression> valueList;
    private final TreeSet<Value> valueSet;
    private boolean hasNull;
    private final TypeInfo type;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConditionInConstantSet(SessionLocal sessionLocal, Expression expression, boolean z, boolean z2, ArrayList<Expression> arrayList) {
        this.left = expression;
        this.not = z;
        this.whenOperand = z2;
        this.valueList = arrayList;
        this.valueSet = new TreeSet<>(sessionLocal.getDatabase().getCompareMode());
        TypeInfo type = expression.getType();
        Iterator<Expression> it = arrayList.iterator();
        while (it.hasNext()) {
            type = TypeInfo.getHigherType(type, it.next().getType());
        }
        this.type = type;
        Iterator<Expression> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            add(it2.next().getValue(sessionLocal), sessionLocal);
        }
    }

    private void add(Value value, SessionLocal sessionLocal) {
        Value convertTo = value.convertTo(this.type, sessionLocal);
        if (convertTo.containsNull()) {
            this.hasNull = true;
        } else {
            this.valueSet.add(convertTo);
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        return getValue(this.left.getValue(sessionLocal), sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        return getValue(value, sessionLocal).isTrue();
    }

    private Value getValue(Value value, SessionLocal sessionLocal) {
        Value convertTo = value.convertTo(this.type, sessionLocal);
        if (convertTo.containsNull()) {
            return ValueNull.INSTANCE;
        }
        boolean contains = this.valueSet.contains(convertTo);
        if (!contains && this.hasNull) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(this.not ^ contains);
    }

    @Override // org.h2.expression.Expression
    public boolean isWhenConditionOperand() {
        return this.whenOperand;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        return this;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new ConditionInConstantSet(sessionLocal, this.left, !this.not, false, this.valueList);
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.not || this.whenOperand || !sessionLocal.getDatabase().getSettings().optimizeInList) {
            return;
        }
        if (this.left instanceof ExpressionColumn) {
            ExpressionColumn expressionColumn = (ExpressionColumn) this.left;
            if (tableFilter == expressionColumn.getTableFilter()) {
                createIndexConditions(tableFilter, expressionColumn, this.valueList, this.type);
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
                    TypeInfo type = expressionColumn.getType();
                    Iterator it2 = arrayList.iterator();
                    while (it2.hasNext()) {
                        type = TypeInfo.getHigherType(type, ((Expression) it2.next()).getType());
                    }
                    createIndexConditions(tableFilter, expressionColumn, arrayList, type);
                } else {
                    continue;
                }
            }
        }
    }

    private static void createIndexConditions(TableFilter tableFilter, ExpressionColumn expressionColumn, ArrayList<Expression> arrayList, TypeInfo typeInfo) {
        TypeInfo type = expressionColumn.getType();
        if (TypeInfo.haveSameOrdering(type, TypeInfo.getHigherType(type, typeInfo))) {
            tableFilter.addIndexCondition(IndexCondition.getInList(expressionColumn, arrayList));
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
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
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Expression getAdditional(SessionLocal sessionLocal, Comparison comparison) {
        Expression ifEquals;
        if (!this.not && !this.whenOperand && this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR) && (ifEquals = comparison.getIfEquals(this.left)) != null && ifEquals.isConstant()) {
            ArrayList arrayList = new ArrayList(this.valueList.size() + 1);
            arrayList.addAll(this.valueList);
            arrayList.add(ifEquals);
            return new ConditionInConstantSet(sessionLocal, this.left, false, false, arrayList);
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

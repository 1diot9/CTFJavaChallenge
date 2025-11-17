package org.h2.expression.condition;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.expression.aggregate.Aggregate;
import org.h2.expression.aggregate.AggregateType;
import org.h2.index.IndexCondition;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/Comparison.class */
public final class Comparison extends Condition {
    public static final int EQUAL = 0;
    public static final int NOT_EQUAL = 1;
    public static final int SMALLER = 2;
    public static final int BIGGER = 3;
    public static final int SMALLER_EQUAL = 4;
    public static final int BIGGER_EQUAL = 5;
    public static final int EQUAL_NULL_SAFE = 6;
    public static final int NOT_EQUAL_NULL_SAFE = 7;
    public static final int SPATIAL_INTERSECTS = 8;
    static final String[] COMPARE_TYPES = {"=", "<>", "<", ">", "<=", ">=", "IS NOT DISTINCT FROM", "IS DISTINCT FROM", "&&"};
    public static final int FALSE = 9;
    public static final int IN_LIST = 10;
    public static final int IN_ARRAY = 11;
    public static final int IN_QUERY = 12;
    private int compareType;
    private Expression left;
    private Expression right;
    private final boolean whenOperand;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public Comparison(int i, Expression expression, Expression expression2, boolean z) {
        this.left = expression;
        this.right = expression2;
        this.compareType = i;
        this.whenOperand = z;
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
        sb.append(' ').append(COMPARE_TYPES[this.compareType]).append(' ');
        return this.right.getSQL(sb, i, ((this.right instanceof Aggregate) && ((Aggregate) this.right).getAggregateType() == AggregateType.ANY) ? 1 : 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x009e  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x00a0  */
    @Override // org.h2.expression.Expression
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.expression.Expression optimize(org.h2.engine.SessionLocal r7) {
        /*
            Method dump skipped, instructions count: 526
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.condition.Comparison.optimize(org.h2.engine.SessionLocal):org.h2.expression.Expression");
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE && (this.compareType & (-2)) != 6) {
            return ValueNull.INSTANCE;
        }
        return compare(sessionLocal, value, this.right.getValue(sessionLocal), this.compareType);
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        if (value == ValueNull.INSTANCE && (this.compareType & (-2)) != 6) {
            return false;
        }
        return compare(sessionLocal, value, this.right.getValue(sessionLocal), this.compareType).isTrue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Value compare(SessionLocal sessionLocal, Value value, Value value2, int i) {
        Value value3;
        switch (i) {
            case 0:
                int compareWithNull = sessionLocal.compareWithNull(value, value2, true);
                if (compareWithNull == 0) {
                    value3 = ValueBoolean.TRUE;
                    break;
                } else if (compareWithNull == Integer.MIN_VALUE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.FALSE;
                    break;
                }
            case 1:
                int compareWithNull2 = sessionLocal.compareWithNull(value, value2, true);
                if (compareWithNull2 == 0) {
                    value3 = ValueBoolean.FALSE;
                    break;
                } else if (compareWithNull2 == Integer.MIN_VALUE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.TRUE;
                    break;
                }
            case 2:
                int compareWithNull3 = sessionLocal.compareWithNull(value, value2, false);
                if (compareWithNull3 == Integer.MIN_VALUE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.get(compareWithNull3 < 0);
                    break;
                }
            case 3:
                int compareWithNull4 = sessionLocal.compareWithNull(value, value2, false);
                if (compareWithNull4 > 0) {
                    value3 = ValueBoolean.TRUE;
                    break;
                } else if (compareWithNull4 == Integer.MIN_VALUE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.FALSE;
                    break;
                }
            case 4:
                int compareWithNull5 = sessionLocal.compareWithNull(value, value2, false);
                if (compareWithNull5 == Integer.MIN_VALUE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.get(compareWithNull5 <= 0);
                    break;
                }
            case 5:
                int compareWithNull6 = sessionLocal.compareWithNull(value, value2, false);
                if (compareWithNull6 >= 0) {
                    value3 = ValueBoolean.TRUE;
                    break;
                } else if (compareWithNull6 == Integer.MIN_VALUE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.FALSE;
                    break;
                }
            case 6:
                value3 = ValueBoolean.get(sessionLocal.areEqual(value, value2));
                break;
            case 7:
                value3 = ValueBoolean.get(!sessionLocal.areEqual(value, value2));
                break;
            case 8:
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    value3 = ValueNull.INSTANCE;
                    break;
                } else {
                    value3 = ValueBoolean.get(value.convertToGeometry(null).intersectsBoundingBox(value2.convertToGeometry(null)));
                    break;
                }
                break;
            default:
                throw DbException.getInternalError("type=" + i);
        }
        return value3;
    }

    @Override // org.h2.expression.Expression
    public boolean isWhenConditionOperand() {
        return this.whenOperand;
    }

    private static int getReversedCompareType(int i) {
        switch (i) {
            case 0:
            case 1:
            case 6:
            case 7:
            case 8:
                return i;
            case 2:
                return 3;
            case 3:
                return 2;
            case 4:
                return 5;
            case 5:
                return 4;
            default:
                throw DbException.getInternalError("type=" + i);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.compareType == 8 || this.whenOperand) {
            return null;
        }
        return new Comparison(getNotCompareType(this.compareType), this.left, this.right, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getNotCompareType(int i) {
        switch (i) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return 5;
            case 3:
                return 4;
            case 4:
                return 3;
            case 5:
                return 2;
            case 6:
                return 7;
            case 7:
                return 6;
            default:
                throw DbException.getInternalError("type=" + i);
        }
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (!this.whenOperand) {
            createIndexConditions(tableFilter, this.left, this.right, this.compareType);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void createIndexConditions(TableFilter tableFilter, Expression expression, Expression expression2, int i) {
        if (i == 1 || i == 7 || !tableFilter.getTable().isQueryComparable()) {
            return;
        }
        if (i != 8) {
            boolean z = expression2 instanceof ExpressionList;
            if (expression instanceof ExpressionList) {
                if (z) {
                    createIndexConditions(tableFilter, (ExpressionList) expression, (ExpressionList) expression2, i);
                } else if (expression2 instanceof ValueExpression) {
                    createIndexConditions(tableFilter, (ExpressionList) expression, (ValueExpression) expression2, i);
                }
            } else if (z && (expression instanceof ValueExpression)) {
                createIndexConditions(tableFilter, (ExpressionList) expression2, (ValueExpression) expression, getReversedCompareType(i));
                return;
            }
        }
        ExpressionColumn expressionColumn = null;
        if (expression instanceof ExpressionColumn) {
            expressionColumn = (ExpressionColumn) expression;
            if (tableFilter != expressionColumn.getTableFilter()) {
                expressionColumn = null;
            }
        }
        ExpressionColumn expressionColumn2 = null;
        if (expression2 instanceof ExpressionColumn) {
            expressionColumn2 = (ExpressionColumn) expression2;
            if (tableFilter != expressionColumn2.getTableFilter()) {
                expressionColumn2 = null;
            }
        }
        if ((expressionColumn == null) == (expressionColumn2 == null)) {
            return;
        }
        if (expressionColumn == null) {
            if (!expression.isEverything(ExpressionVisitor.getNotFromResolverVisitor(tableFilter))) {
                return;
            }
        } else if (!expression2.isEverything(ExpressionVisitor.getNotFromResolverVisitor(tableFilter))) {
            return;
        }
        switch (i) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
                if (expressionColumn != null) {
                    TypeInfo type = expressionColumn.getType();
                    if (TypeInfo.haveSameOrdering(type, TypeInfo.getHigherType(type, expression2.getType()))) {
                        tableFilter.addIndexCondition(IndexCondition.get(i, expressionColumn, expression2));
                        return;
                    }
                    return;
                }
                TypeInfo type2 = expressionColumn2.getType();
                if (TypeInfo.haveSameOrdering(type2, TypeInfo.getHigherType(type2, expression.getType()))) {
                    tableFilter.addIndexCondition(IndexCondition.get(getReversedCompareType(i), expressionColumn2, expression));
                    return;
                }
                return;
            case 1:
            case 7:
            default:
                throw DbException.getInternalError("type=" + i);
        }
    }

    private static void createIndexConditions(TableFilter tableFilter, ExpressionList expressionList, ExpressionList expressionList2, int i) {
        int subexpressionCount = expressionList.getSubexpressionCount();
        if (subexpressionCount == 0 || subexpressionCount != expressionList2.getSubexpressionCount()) {
            return;
        }
        if (i != 0 && i != 6) {
            if (subexpressionCount > 1) {
                if (i == 3) {
                    i = 5;
                } else if (i == 2) {
                    i = 4;
                }
            }
            subexpressionCount = 1;
        }
        for (int i2 = 0; i2 < subexpressionCount; i2++) {
            createIndexConditions(tableFilter, expressionList.getSubexpression(i2), expressionList2.getSubexpression(i2), i);
        }
    }

    private static void createIndexConditions(TableFilter tableFilter, ExpressionList expressionList, ValueExpression valueExpression, int i) {
        int subexpressionCount = expressionList.getSubexpressionCount();
        if (subexpressionCount == 0) {
            return;
        }
        if (subexpressionCount == 1) {
            createIndexConditions(tableFilter, expressionList.getSubexpression(0), valueExpression, i);
            return;
        }
        if (subexpressionCount > 1) {
            Value value = valueExpression.getValue(null);
            if (value.getValueType() == 41) {
                Value[] list = ((ValueRow) value).getList();
                if (subexpressionCount != list.length) {
                    return;
                }
                if (i != 0 && i != 6) {
                    if (i == 3) {
                        i = 5;
                    } else if (i == 2) {
                        i = 4;
                    }
                    subexpressionCount = 1;
                }
                for (int i2 = 0; i2 < subexpressionCount; i2++) {
                    createIndexConditions(tableFilter, expressionList.getSubexpression(i2), ValueExpression.get(list[i2]), i);
                }
            }
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        if (this.right != null) {
            this.right.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        if (this.right != null) {
            this.right.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        this.right.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor) && this.right.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost() + this.right.getCost() + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Expression getIfEquals(Expression expression) {
        if (this.compareType == 0) {
            String sql = expression.getSQL(0);
            if (this.left.getSQL(0).equals(sql)) {
                return this.right;
            }
            if (this.right.getSQL(0).equals(sql)) {
                return this.left;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Expression getAdditionalAnd(SessionLocal sessionLocal, Comparison comparison) {
        if (this.compareType == 0 && comparison.compareType == 0 && !this.whenOperand) {
            boolean isConstant = this.left.isConstant();
            boolean isConstant2 = this.right.isConstant();
            boolean isConstant3 = comparison.left.isConstant();
            boolean isConstant4 = comparison.right.isConstant();
            String sql = this.left.getSQL(0);
            String sql2 = comparison.left.getSQL(0);
            String sql3 = this.right.getSQL(0);
            String sql4 = comparison.right.getSQL(0);
            if ((!isConstant2 || !isConstant4) && sql.equals(sql2)) {
                return new Comparison(0, this.right, comparison.right, false);
            }
            if ((!isConstant2 || !isConstant3) && sql.equals(sql4)) {
                return new Comparison(0, this.right, comparison.left, false);
            }
            if ((!isConstant || !isConstant4) && sql3.equals(sql2)) {
                return new Comparison(0, this.left, comparison.right, false);
            }
            if ((!isConstant || !isConstant3) && sql3.equals(sql4)) {
                return new Comparison(0, this.left, comparison.left, false);
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Expression optimizeOr(SessionLocal sessionLocal, Comparison comparison) {
        if (this.compareType == 0 && comparison.compareType == 0) {
            Expression expression = comparison.left;
            Expression expression2 = comparison.right;
            String sql = expression.getSQL(0);
            String sql2 = expression2.getSQL(0);
            if (this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
                String sql3 = this.left.getSQL(0);
                if (sql3.equals(sql)) {
                    return getConditionIn(this.left, this.right, expression2);
                }
                if (sql3.equals(sql2)) {
                    return getConditionIn(this.left, this.right, expression);
                }
            }
            if (this.right.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
                String sql4 = this.right.getSQL(0);
                if (sql4.equals(sql)) {
                    return getConditionIn(this.right, this.left, expression2);
                }
                if (sql4.equals(sql2)) {
                    return getConditionIn(this.right, this.left, expression);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private static ConditionIn getConditionIn(Expression expression, Expression expression2, Expression expression3) {
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(expression2);
        arrayList.add(expression3);
        return new ConditionIn(expression, false, false, arrayList);
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 2;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        switch (i) {
            case 0:
                return this.left;
            case 1:
                return this.right;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}

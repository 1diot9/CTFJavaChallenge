package org.h2.expression.condition;

import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.IndexCondition;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionInQuery.class */
public final class ConditionInQuery extends PredicateWithSubquery {
    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private final boolean all;
    private final int compareType;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ConditionInQuery(Expression expression, boolean z, boolean z2, Query query, boolean z3, int i) {
        super(query);
        this.left = expression;
        this.not = z;
        this.whenOperand = z2;
        query.setRandomAccessResult(true);
        query.setNeverLazy(true);
        query.setDistinctIfPossible();
        this.all = z3;
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
        this.query.setSession(sessionLocal);
        LocalResult localResult = (LocalResult) this.query.query(0L);
        if (!localResult.hasNext()) {
            return ValueBoolean.get(this.not ^ this.all);
        }
        if ((this.compareType & (-2)) == 6) {
            return getNullSafeValueSlow(sessionLocal, localResult, value);
        }
        if (value.containsNull()) {
            return ValueNull.INSTANCE;
        }
        if (this.all || this.compareType != 0 || !sessionLocal.getDatabase().getSettings().optimizeInSelect) {
            return getValueSlow(sessionLocal, localResult, value);
        }
        int columnCount = this.query.getColumnCount();
        if (columnCount != 1) {
            Value[] list = value.convertToAnyRow().getList();
            if (columnCount == list.length && localResult.containsDistinct(list)) {
                return ValueBoolean.get(!this.not);
            }
        } else {
            if (localResult.getColumnType(0).getValueType() == 0) {
                return ValueNull.INSTANCE;
            }
            if (value.getValueType() == 41) {
                value = ((ValueRow) value).getList()[0];
            }
            if (localResult.containsDistinct(new Value[]{value})) {
                return ValueBoolean.get(!this.not);
            }
        }
        if (localResult.containsNull()) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(this.not);
    }

    private Value getValueSlow(SessionLocal sessionLocal, ResultInterface resultInterface, Value value) {
        boolean z = value.getValueType() != 41 && this.query.getColumnCount() == 1;
        boolean z2 = false;
        ValueBoolean valueBoolean = ValueBoolean.get(!this.all);
        while (resultInterface.next()) {
            Value[] currentRow = resultInterface.currentRow();
            Value compare = Comparison.compare(sessionLocal, value, z ? currentRow[0] : ValueRow.get(currentRow), this.compareType);
            if (compare == ValueNull.INSTANCE) {
                z2 = true;
            } else if (compare == valueBoolean) {
                return ValueBoolean.get(this.not == this.all);
            }
        }
        if (z2) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(this.not ^ this.all);
    }

    private Value getNullSafeValueSlow(SessionLocal sessionLocal, ResultInterface resultInterface, Value value) {
        boolean z = value.getValueType() != 41 && this.query.getColumnCount() == 1;
        boolean z2 = this.all == (this.compareType == 7);
        while (resultInterface.next()) {
            Value[] currentRow = resultInterface.currentRow();
            if (sessionLocal.areEqual(value, z ? currentRow[0] : ValueRow.get(currentRow)) == z2) {
                return ValueBoolean.get(this.not == this.all);
            }
        }
        return ValueBoolean.get(this.not ^ this.all);
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
        return new ConditionInQuery(this.left, !this.not, false, this.query, this.all, this.compareType);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        super.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        super.optimize(sessionLocal);
        this.left = this.left.optimize(sessionLocal);
        TypeInfo.checkComparable(this.left.getType(), this.query.getRowDataType());
        return this;
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        super.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        boolean z = this.not && (this.all || this.compareType != 0);
        if (z) {
            sb.append("NOT (");
        }
        this.left.getSQL(sb, i, 0);
        getWhenSQL(sb, i);
        if (z) {
            sb.append(')');
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        if (this.all) {
            sb.append(Comparison.COMPARE_TYPES[this.compareType]).append(" ALL");
        } else if (this.compareType == 0) {
            if (this.not) {
                sb.append(" NOT");
            }
            sb.append(" IN");
        } else {
            sb.append(' ').append(Comparison.COMPARE_TYPES[this.compareType]).append(" ANY");
        }
        return super.getUnenclosedSQL(sb, i);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        super.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor) && super.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.condition.PredicateWithSubquery, org.h2.expression.Expression
    public int getCost() {
        return this.left.getCost() + super.getCost();
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.not || this.whenOperand || this.compareType != 0 || !sessionLocal.getDatabase().getSettings().optimizeInList || this.query.getColumnCount() != 1 || !(this.left instanceof ExpressionColumn)) {
            return;
        }
        TypeInfo type = this.left.getType();
        TypeInfo type2 = this.query.getExpressions().get(0).getType();
        if (!TypeInfo.haveSameOrdering(type, TypeInfo.getHigherType(type, type2))) {
            return;
        }
        int valueType = type.getValueType();
        if (!DataType.hasTotalOrdering(valueType) && valueType != type2.getValueType()) {
            return;
        }
        ExpressionColumn expressionColumn = (ExpressionColumn) this.left;
        if (tableFilter != expressionColumn.getTableFilter()) {
            return;
        }
        if (!this.query.isEverything(ExpressionVisitor.getNotFromResolverVisitor(tableFilter))) {
            return;
        }
        tableFilter.addIndexCondition(IndexCondition.getInQuery(expressionColumn, this.query));
    }
}

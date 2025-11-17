package org.h2.expression.condition;

import cn.hutool.core.text.CharSequenceUtil;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionAndOr.class */
public class ConditionAndOr extends Condition {
    public static final int AND = 0;
    public static final int OR = 1;
    private final int andOrType;
    private Expression left;
    private Expression right;
    private Expression added;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ConditionAndOr(int i, Expression expression, Expression expression2) {
        if (expression == null || expression2 == null) {
            throw DbException.getInternalError(expression + CharSequenceUtil.SPACE + expression2);
        }
        this.andOrType = i;
        this.left = expression;
        this.right = expression2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAndOrType() {
        return this.andOrType;
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.left.getSQL(sb, i, 0);
        switch (this.andOrType) {
            case 0:
                sb.append("\n    AND ");
                break;
            case 1:
                sb.append("\n    OR ");
                break;
            default:
                throw DbException.getInternalError("andOrType=" + this.andOrType);
        }
        return this.right.getSQL(sb, i, 0);
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.andOrType == 0) {
            this.left.createIndexConditions(sessionLocal, tableFilter);
            this.right.createIndexConditions(sessionLocal, tableFilter);
            if (this.added != null) {
                this.added.createIndexConditions(sessionLocal, tableFilter);
            }
        }
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        Expression notIfPossible = this.left.getNotIfPossible(sessionLocal);
        if (notIfPossible == null) {
            notIfPossible = new ConditionNot(this.left);
        }
        Expression notIfPossible2 = this.right.getNotIfPossible(sessionLocal);
        if (notIfPossible2 == null) {
            notIfPossible2 = new ConditionNot(this.right);
        }
        return new ConditionAndOr(this.andOrType == 0 ? 1 : 0, notIfPossible, notIfPossible2);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        switch (this.andOrType) {
            case 0:
                if (!value.isFalse()) {
                    Value value2 = this.right.getValue(sessionLocal);
                    if (!value2.isFalse()) {
                        if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                            return ValueNull.INSTANCE;
                        }
                        return ValueBoolean.TRUE;
                    }
                }
                return ValueBoolean.FALSE;
            case 1:
                if (!value.isTrue()) {
                    Value value3 = this.right.getValue(sessionLocal);
                    if (!value3.isTrue()) {
                        if (value == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) {
                            return ValueNull.INSTANCE;
                        }
                        return ValueBoolean.FALSE;
                    }
                }
                return ValueBoolean.TRUE;
            default:
                throw DbException.getInternalError("type=" + this.andOrType);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Expression optimizeConditionAndOr;
        Expression additionalAnd;
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        if (this.right.getCost() < this.left.getCost()) {
            Expression expression = this.left;
            this.left = this.right;
            this.right = expression;
        }
        switch (this.andOrType) {
            case 0:
                if (sessionLocal.getDatabase().getSettings().optimizeTwoEquals && (this.left instanceof Comparison) && (this.right instanceof Comparison) && (additionalAnd = ((Comparison) this.left).getAdditionalAnd(sessionLocal, (Comparison) this.right)) != null) {
                    this.added = additionalAnd.optimize(sessionLocal);
                    break;
                }
                break;
            case 1:
                if (sessionLocal.getDatabase().getSettings().optimizeOr) {
                    if ((this.left instanceof Comparison) && (this.right instanceof Comparison)) {
                        optimizeConditionAndOr = ((Comparison) this.left).optimizeOr(sessionLocal, (Comparison) this.right);
                    } else if ((this.left instanceof ConditionIn) && (this.right instanceof Comparison)) {
                        optimizeConditionAndOr = ((ConditionIn) this.left).getAdditional((Comparison) this.right);
                    } else if ((this.right instanceof ConditionIn) && (this.left instanceof Comparison)) {
                        optimizeConditionAndOr = ((ConditionIn) this.right).getAdditional((Comparison) this.left);
                    } else if ((this.left instanceof ConditionInConstantSet) && (this.right instanceof Comparison)) {
                        optimizeConditionAndOr = ((ConditionInConstantSet) this.left).getAdditional(sessionLocal, (Comparison) this.right);
                    } else if ((this.right instanceof ConditionInConstantSet) && (this.left instanceof Comparison)) {
                        optimizeConditionAndOr = ((ConditionInConstantSet) this.right).getAdditional(sessionLocal, (Comparison) this.left);
                    } else if ((this.left instanceof ConditionAndOr) && (this.right instanceof ConditionAndOr)) {
                        optimizeConditionAndOr = optimizeConditionAndOr((ConditionAndOr) this.left, (ConditionAndOr) this.right);
                    }
                    if (optimizeConditionAndOr != null) {
                        return optimizeConditionAndOr.optimize(sessionLocal);
                    }
                }
                break;
        }
        Expression optimizeIfConstant = optimizeIfConstant(sessionLocal, this.andOrType, this.left, this.right);
        if (optimizeIfConstant == null) {
            return optimizeN(this);
        }
        if (optimizeIfConstant instanceof ConditionAndOr) {
            return optimizeN((ConditionAndOr) optimizeIfConstant);
        }
        return optimizeIfConstant;
    }

    private static Expression optimizeN(ConditionAndOr conditionAndOr) {
        if (conditionAndOr.right instanceof ConditionAndOr) {
            ConditionAndOr conditionAndOr2 = (ConditionAndOr) conditionAndOr.right;
            if (conditionAndOr2.andOrType == conditionAndOr.andOrType) {
                return new ConditionAndOrN(conditionAndOr.andOrType, conditionAndOr.left, conditionAndOr2.left, conditionAndOr2.right);
            }
        }
        if (conditionAndOr.right instanceof ConditionAndOrN) {
            ConditionAndOrN conditionAndOrN = (ConditionAndOrN) conditionAndOr.right;
            if (conditionAndOrN.getAndOrType() == conditionAndOr.andOrType) {
                conditionAndOrN.addFirst(conditionAndOr.left);
                return conditionAndOrN;
            }
        }
        return conditionAndOr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Expression optimizeIfConstant(SessionLocal sessionLocal, int i, Expression expression, Expression expression2) {
        if (!expression.isConstant()) {
            if (!expression2.isConstant()) {
                return null;
            }
            return optimizeConstant(sessionLocal, i, expression2.getValue(sessionLocal), expression);
        }
        Value value = expression.getValue(sessionLocal);
        if (!expression2.isConstant()) {
            return optimizeConstant(sessionLocal, i, value, expression2);
        }
        Value value2 = expression2.getValue(sessionLocal);
        switch (i) {
            case 0:
                if (value.isFalse() || value2.isFalse()) {
                    return ValueExpression.FALSE;
                }
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return TypedValueExpression.UNKNOWN;
                }
                return ValueExpression.TRUE;
            case 1:
                if (value.isTrue() || value2.isTrue()) {
                    return ValueExpression.TRUE;
                }
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return TypedValueExpression.UNKNOWN;
                }
                return ValueExpression.FALSE;
            default:
                throw DbException.getInternalError("type=" + i);
        }
    }

    private static Expression optimizeConstant(SessionLocal sessionLocal, int i, Value value, Expression expression) {
        if (value != ValueNull.INSTANCE) {
            switch (i) {
                case 0:
                    return value.getBoolean() ? castToBoolean(sessionLocal, expression) : ValueExpression.FALSE;
                case 1:
                    return value.getBoolean() ? ValueExpression.TRUE : castToBoolean(sessionLocal, expression);
                default:
                    throw DbException.getInternalError("type=" + i);
            }
        }
        return null;
    }

    @Override // org.h2.expression.Expression
    public void addFilterConditions(TableFilter tableFilter) {
        if (this.andOrType == 0) {
            this.left.addFilterConditions(tableFilter);
            this.right.addFilterConditions(tableFilter);
        } else {
            super.addFilterConditions(tableFilter);
        }
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
        this.right.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        this.right.setEvaluatable(tableFilter, z);
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
        return this.left.getCost() + this.right.getCost();
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Expression optimizeConditionAndOr(ConditionAndOr conditionAndOr, ConditionAndOr conditionAndOr2) {
        if (conditionAndOr.andOrType != 0 || conditionAndOr2.andOrType != 0) {
            return null;
        }
        Expression subexpression = conditionAndOr.getSubexpression(0);
        Expression subexpression2 = conditionAndOr.getSubexpression(1);
        Expression subexpression3 = conditionAndOr2.getSubexpression(0);
        Expression subexpression4 = conditionAndOr2.getSubexpression(1);
        String sql = subexpression3.getSQL(0);
        String sql2 = subexpression4.getSQL(0);
        if (subexpression.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            String sql3 = subexpression.getSQL(0);
            if (sql3.equals(sql)) {
                return new ConditionAndOr(0, subexpression, new ConditionAndOr(1, subexpression2, subexpression4));
            }
            if (sql3.equals(sql2)) {
                return new ConditionAndOr(0, subexpression, new ConditionAndOr(1, subexpression2, subexpression3));
            }
        }
        if (subexpression2.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            String sql4 = subexpression2.getSQL(0);
            if (sql4.equals(sql)) {
                return new ConditionAndOr(0, subexpression2, new ConditionAndOr(1, subexpression, subexpression4));
            }
            if (sql4.equals(sql2)) {
                return new ConditionAndOr(0, subexpression2, new ConditionAndOr(1, subexpression, subexpression3));
            }
            return null;
        }
        return null;
    }
}

package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionLocalAndGlobal.class */
public class ConditionLocalAndGlobal extends Condition {
    private Expression local;
    private Expression global;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ConditionLocalAndGlobal(Expression expression, Expression expression2) {
        if (expression2 == null) {
            throw DbException.getInternalError();
        }
        this.local = expression;
        this.global = expression2;
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return this.local != null || this.global.needParentheses();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        if (this.local == null) {
            return this.global.getUnenclosedSQL(sb, i);
        }
        this.local.getSQL(sb, i, 0);
        sb.append("\n    _LOCAL_AND_GLOBAL_ ");
        return this.global.getSQL(sb, i, 0);
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.local != null) {
            this.local.createIndexConditions(sessionLocal, tableFilter);
        }
        this.global.createIndexConditions(sessionLocal, tableFilter);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        if (this.local == null) {
            return this.global.getValue(sessionLocal);
        }
        Value value = this.local.getValue(sessionLocal);
        if (!value.isFalse()) {
            Value value2 = this.global.getValue(sessionLocal);
            if (!value2.isFalse()) {
                if (value == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                return ValueBoolean.TRUE;
            }
        }
        return ValueBoolean.FALSE;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.global = this.global.optimize(sessionLocal);
        if (this.local != null) {
            this.local = this.local.optimize(sessionLocal);
            Expression optimizeIfConstant = ConditionAndOr.optimizeIfConstant(sessionLocal, 0, this.local, this.global);
            if (optimizeIfConstant != null) {
                return optimizeIfConstant;
            }
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public void addFilterConditions(TableFilter tableFilter) {
        if (this.local != null) {
            this.local.addFilterConditions(tableFilter);
        }
        this.global.addFilterConditions(tableFilter);
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        if (this.local != null) {
            this.local.mapColumns(columnResolver, i, i2);
        }
        this.global.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        if (this.local != null) {
            this.local.setEvaluatable(tableFilter, z);
        }
        this.global.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        if (this.local != null) {
            this.local.updateAggregate(sessionLocal, i);
        }
        this.global.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return (this.local == null || this.local.isEverything(expressionVisitor)) && this.global.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int cost = this.global.getCost();
        if (this.local != null) {
            cost += this.local.getCost();
        }
        return cost;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.local == null ? 1 : 2;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        switch (i) {
            case 0:
                return this.local != null ? this.local : this.global;
            case 1:
                if (this.local != null) {
                    return this.global;
                }
                break;
        }
        throw new IndexOutOfBoundsException();
    }
}

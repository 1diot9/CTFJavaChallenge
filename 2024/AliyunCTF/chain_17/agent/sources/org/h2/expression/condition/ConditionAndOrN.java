package org.h2.expression.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/ConditionAndOrN.class */
public class ConditionAndOrN extends Condition {
    private final int andOrType;
    private final List<Expression> expressions;
    private List<Expression> added;
    private static final Comparator<Expression> COMPARE_BY_COST = new Comparator<Expression>() { // from class: org.h2.expression.condition.ConditionAndOrN.1
        @Override // java.util.Comparator
        public int compare(Expression expression, Expression expression2) {
            return expression.getCost() - expression2.getCost();
        }
    };

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public ConditionAndOrN(int i, Expression expression, Expression expression2, Expression expression3) {
        this.andOrType = i;
        this.expressions = new ArrayList(3);
        this.expressions.add(expression);
        this.expressions.add(expression2);
        this.expressions.add(expression3);
    }

    public ConditionAndOrN(int i, List<Expression> list) {
        this.andOrType = i;
        this.expressions = list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAndOrType() {
        return this.andOrType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addFirst(Expression expression) {
        this.expressions.add(0, expression);
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        Iterator<Expression> it = this.expressions.iterator();
        it.next().getSQL(sb, i, 0);
        while (it.hasNext()) {
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
            it.next().getSQL(sb, i, 0);
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
        if (this.andOrType == 0) {
            Iterator<Expression> it = this.expressions.iterator();
            while (it.hasNext()) {
                it.next().createIndexConditions(sessionLocal, tableFilter);
            }
            if (this.added != null) {
                Iterator<Expression> it2 = this.added.iterator();
                while (it2.hasNext()) {
                    it2.next().createIndexConditions(sessionLocal, tableFilter);
                }
            }
        }
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        ArrayList arrayList = new ArrayList(this.expressions.size());
        for (Expression expression : this.expressions) {
            Expression notIfPossible = expression.getNotIfPossible(sessionLocal);
            if (notIfPossible == null) {
                notIfPossible = new ConditionNot(expression);
            }
            arrayList.add(notIfPossible);
        }
        return new ConditionAndOrN(this.andOrType == 0 ? 1 : 0, arrayList);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        boolean z = false;
        switch (this.andOrType) {
            case 0:
                Iterator<Expression> it = this.expressions.iterator();
                while (it.hasNext()) {
                    Value value = it.next().getValue(sessionLocal);
                    if (value == ValueNull.INSTANCE) {
                        z = true;
                    } else if (!value.getBoolean()) {
                        return ValueBoolean.FALSE;
                    }
                }
                return z ? ValueNull.INSTANCE : ValueBoolean.TRUE;
            case 1:
                Iterator<Expression> it2 = this.expressions.iterator();
                while (it2.hasNext()) {
                    Value value2 = it2.next().getValue(sessionLocal);
                    if (value2 == ValueNull.INSTANCE) {
                        z = true;
                    } else if (value2.getBoolean()) {
                        return ValueBoolean.TRUE;
                    }
                }
                return z ? ValueNull.INSTANCE : ValueBoolean.FALSE;
            default:
                throw DbException.getInternalError("type=" + this.andOrType);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Expression additionalAnd;
        Expression optimizeConditionAndOr;
        for (int i = 0; i < this.expressions.size(); i++) {
            this.expressions.set(i, this.expressions.get(i).optimize(sessionLocal));
        }
        Collections.sort(this.expressions, COMPARE_BY_COST);
        optimizeMerge(0);
        int i2 = 1;
        while (i2 < this.expressions.size()) {
            Expression expression = this.expressions.get(i2 - 1);
            Expression expression2 = this.expressions.get(i2);
            switch (this.andOrType) {
                case 0:
                    if (sessionLocal.getDatabase().getSettings().optimizeTwoEquals && (expression instanceof Comparison) && (expression2 instanceof Comparison) && (additionalAnd = ((Comparison) expression).getAdditionalAnd(sessionLocal, (Comparison) expression2)) != null) {
                        if (this.added == null) {
                            this.added = new ArrayList();
                        }
                        this.added.add(additionalAnd.optimize(sessionLocal));
                        break;
                    }
                    break;
                case 1:
                    if (sessionLocal.getDatabase().getSettings().optimizeOr) {
                        if ((expression instanceof Comparison) && (expression2 instanceof Comparison)) {
                            optimizeConditionAndOr = ((Comparison) expression).optimizeOr(sessionLocal, (Comparison) expression2);
                        } else if ((expression instanceof ConditionIn) && (expression2 instanceof Comparison)) {
                            optimizeConditionAndOr = ((ConditionIn) expression).getAdditional((Comparison) expression2);
                        } else if ((expression2 instanceof ConditionIn) && (expression instanceof Comparison)) {
                            optimizeConditionAndOr = ((ConditionIn) expression2).getAdditional((Comparison) expression);
                        } else if ((expression instanceof ConditionInConstantSet) && (expression2 instanceof Comparison)) {
                            optimizeConditionAndOr = ((ConditionInConstantSet) expression).getAdditional(sessionLocal, (Comparison) expression2);
                        } else if ((expression2 instanceof ConditionInConstantSet) && (expression instanceof Comparison)) {
                            optimizeConditionAndOr = ((ConditionInConstantSet) expression2).getAdditional(sessionLocal, (Comparison) expression);
                        } else if ((expression instanceof ConditionAndOr) && (expression2 instanceof ConditionAndOr)) {
                            optimizeConditionAndOr = ConditionAndOr.optimizeConditionAndOr((ConditionAndOr) expression, (ConditionAndOr) expression2);
                        }
                        if (optimizeConditionAndOr != null) {
                            this.expressions.remove(i2);
                            this.expressions.set(i2 - 1, optimizeConditionAndOr.optimize(sessionLocal));
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                    break;
            }
            Expression optimizeIfConstant = ConditionAndOr.optimizeIfConstant(sessionLocal, this.andOrType, expression, expression2);
            if (optimizeIfConstant != null) {
                this.expressions.remove(i2);
                this.expressions.set(i2 - 1, optimizeIfConstant);
            } else if (!optimizeMerge(i2)) {
                i2++;
            }
        }
        Collections.sort(this.expressions, COMPARE_BY_COST);
        if (this.expressions.size() == 1) {
            return Condition.castToBoolean(sessionLocal, this.expressions.get(0));
        }
        return this;
    }

    private boolean optimizeMerge(int i) {
        Expression expression = this.expressions.get(i);
        if (expression instanceof ConditionAndOrN) {
            ConditionAndOrN conditionAndOrN = (ConditionAndOrN) expression;
            if (this.andOrType == conditionAndOrN.andOrType) {
                this.expressions.remove(i);
                this.expressions.addAll(i, conditionAndOrN.expressions);
                return true;
            }
            return false;
        }
        if (expression instanceof ConditionAndOr) {
            ConditionAndOr conditionAndOr = (ConditionAndOr) expression;
            if (this.andOrType == conditionAndOr.getAndOrType()) {
                this.expressions.set(i, conditionAndOr.getSubexpression(0));
                this.expressions.add(i + 1, conditionAndOr.getSubexpression(1));
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // org.h2.expression.Expression
    public void addFilterConditions(TableFilter tableFilter) {
        if (this.andOrType == 0) {
            Iterator<Expression> it = this.expressions.iterator();
            while (it.hasNext()) {
                it.next().addFilterConditions(tableFilter);
            }
            return;
        }
        super.addFilterConditions(tableFilter);
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            it.next().mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            it.next().setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            it.next().updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            if (!it.next().isEverything(expressionVisitor)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 0;
        Iterator<Expression> it = this.expressions.iterator();
        while (it.hasNext()) {
            i += it.next().getCost();
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.expressions.size();
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        return this.expressions.get(i);
    }
}

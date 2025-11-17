package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Operation2.class */
public abstract class Operation2 extends Expression {
    protected Expression left;
    protected Expression right;
    protected TypeInfo type;

    /* JADX INFO: Access modifiers changed from: protected */
    public Operation2(Expression expression, Expression expression2) {
        this.left = expression;
        this.right = expression2;
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
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
        return this.left.getCost() + this.right.getCost() + 1;
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

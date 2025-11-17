package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Operation1.class */
public abstract class Operation1 extends Expression {
    protected Expression arg;
    protected TypeInfo type;

    /* JADX INFO: Access modifiers changed from: protected */
    public Operation1(Expression expression) {
        this.arg = expression;
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.arg.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.arg.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.arg.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.arg.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return this.arg.getCost() + 1;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 1;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i == 0) {
            return this.arg;
        }
        throw new IndexOutOfBoundsException();
    }
}

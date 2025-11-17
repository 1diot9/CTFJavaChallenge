package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/Function0_1.class */
public abstract class Function0_1 extends Expression implements NamedExpression {
    protected Expression arg;
    protected TypeInfo type;

    /* JADX INFO: Access modifiers changed from: protected */
    public Function0_1(Expression expression) {
        this.arg = expression;
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        if (this.arg != null) {
            this.arg.mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        if (this.arg != null) {
            this.arg.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        if (this.arg != null) {
            this.arg.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.arg == null || this.arg.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 1;
        if (this.arg != null) {
            i = 1 + this.arg.getCost();
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.arg != null ? 1 : 0;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i == 0 && this.arg != null) {
            return this.arg;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(getName()).append('(');
        if (this.arg != null) {
            this.arg.getUnenclosedSQL(sb, i);
        }
        return sb.append(')');
    }
}

package org.h2.expression;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/OperationN.class */
public abstract class OperationN extends Expression implements ExpressionWithVariableParameters {
    protected Expression[] args;
    protected int argsCount;
    protected TypeInfo type;

    /* JADX INFO: Access modifiers changed from: protected */
    public OperationN(Expression[] expressionArr) {
        this.args = expressionArr;
    }

    @Override // org.h2.expression.ExpressionWithVariableParameters
    public void addParameter(Expression expression) {
        int length = this.args.length;
        if (this.argsCount >= length) {
            this.args = (Expression[]) Arrays.copyOf(this.args, length * 2);
        }
        Expression[] expressionArr = this.args;
        int i = this.argsCount;
        this.argsCount = i + 1;
        expressionArr[i] = expression;
    }

    @Override // org.h2.expression.ExpressionWithVariableParameters
    public void doneWithParameters() throws DbException {
        if (this.args.length != this.argsCount) {
            this.args = (Expression[]) Arrays.copyOf(this.args, this.argsCount);
        }
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        for (Expression expression : this.args) {
            expression.mapColumns(columnResolver, i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean optimizeArguments(SessionLocal sessionLocal, boolean z) {
        int length = this.args.length;
        for (int i = 0; i < length; i++) {
            Expression optimize = this.args[i].optimize(sessionLocal);
            this.args[i] = optimize;
            if (z && !optimize.isConstant()) {
                z = false;
            }
        }
        return z;
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        for (Expression expression : this.args) {
            expression.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        for (Expression expression : this.args) {
            expression.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        for (Expression expression : this.args) {
            if (!expression.isEverything(expressionVisitor)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int length = this.args.length + 1;
        for (Expression expression : this.args) {
            length += expression.getCost();
        }
        return length;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return this.args.length;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        return this.args[i];
    }
}

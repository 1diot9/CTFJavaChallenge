package org.h2.expression.function.table;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionWithVariableParameters;
import org.h2.expression.function.NamedExpression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.util.HasSQL;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/table/TableFunction.class */
public abstract class TableFunction implements HasSQL, NamedExpression, ExpressionWithVariableParameters {
    protected Expression[] args;
    private int argsCount;

    public abstract ResultInterface getValue(SessionLocal sessionLocal);

    public abstract ResultInterface getValueTemplate(SessionLocal sessionLocal);

    public abstract boolean isDeterministic();

    /* JADX INFO: Access modifiers changed from: protected */
    public TableFunction(Expression[] expressionArr) {
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

    public void optimize(SessionLocal sessionLocal) {
        int length = this.args.length;
        for (int i = 0; i < length; i++) {
            this.args[i] = this.args[i].optimize(sessionLocal);
        }
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return Expression.writeExpressions(sb.append(getName()).append('('), this.args, i).append(')');
    }
}

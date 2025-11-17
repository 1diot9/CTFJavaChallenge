package org.h2.expression.function;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Variable;
import org.h2.message.DbException;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/SetFunction.class */
public final class SetFunction extends Function2 {
    public SetFunction(Expression expression, Expression expression2) {
        super(expression, expression2);
    }

    @Override // org.h2.expression.function.Function2, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Variable variable = (Variable) this.left;
        Value value = this.right.getValue(sessionLocal);
        sessionLocal.setVariable(variable.getName(), value);
        return value;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        this.type = this.right.getType();
        if (!(this.left instanceof Variable)) {
            throw DbException.get(ErrorCode.CAN_ONLY_ASSIGN_TO_VARIABLE_1, this.left.getTraceSQL());
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "SET";
    }

    @Override // org.h2.expression.Operation2, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!super.isEverything(expressionVisitor)) {
            return false;
        }
        switch (expressionVisitor.getType()) {
            case 2:
            case 5:
            case 8:
                return false;
            default:
                return true;
        }
    }
}

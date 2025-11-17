package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.OperationN;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/FunctionN.class */
public abstract class FunctionN extends OperationN implements NamedExpression {
    /* JADX INFO: Access modifiers changed from: protected */
    public FunctionN(Expression[] expressionArr) {
        super(expressionArr);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        Value value2;
        Value value3;
        int length = this.args.length;
        if (length >= 1) {
            value = this.args[0].getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            if (length >= 2) {
                value2 = this.args[1].getValue(sessionLocal);
                if (value2 == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                if (length >= 3) {
                    value3 = this.args[2].getValue(sessionLocal);
                    if (value3 == ValueNull.INSTANCE) {
                        return ValueNull.INSTANCE;
                    }
                } else {
                    value3 = null;
                }
            } else {
                value2 = null;
                value3 = null;
            }
        } else {
            value = null;
            value2 = null;
            value3 = null;
        }
        return getValue(sessionLocal, value, value2, value3);
    }

    protected Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return writeExpressions(sb.append(getName()).append('('), this.args, i).append(')');
    }
}

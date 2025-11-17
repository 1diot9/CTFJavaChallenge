package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Operation1_2;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/Function1_2.class */
public abstract class Function1_2 extends Operation1_2 implements NamedExpression {
    /* JADX INFO: Access modifiers changed from: protected */
    public Function1_2(Expression expression, Expression expression2) {
        super(expression, expression2);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        Value value2 = this.left.getValue(sessionLocal);
        if (value2 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        if (this.right != null) {
            value = this.right.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
        } else {
            value = null;
        }
        return getValue(sessionLocal, value2, value);
    }

    protected Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.left.getUnenclosedSQL(sb.append(getName()).append('('), i);
        if (this.right != null) {
            this.right.getUnenclosedSQL(sb.append(", "), i);
        }
        return sb.append(')');
    }
}

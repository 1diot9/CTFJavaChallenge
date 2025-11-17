package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/UnaryOperation.class */
public class UnaryOperation extends Operation1 {
    public UnaryOperation(Expression expression) {
        super(expression);
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return this.arg.getSQL(sb.append("- "), i, 0);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value convertTo = this.arg.getValue(sessionLocal).convertTo(this.type, sessionLocal);
        return convertTo == ValueNull.INSTANCE ? convertTo : convertTo.negate();
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        this.type = this.arg.getType();
        if (this.type.getValueType() == -1) {
            this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
        } else if (this.type.getValueType() == 36) {
            this.type = TypeInfo.TYPE_INTEGER;
        }
        if (this.arg.isConstant()) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }
}

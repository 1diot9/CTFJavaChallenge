package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/NullIfFunction.class */
public final class NullIfFunction extends Function2 {
    public NullIfFunction(Expression expression, Expression expression2) {
        super(expression, expression2);
    }

    @Override // org.h2.expression.function.Function2, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        if (sessionLocal.compareWithNull(value, this.right.getValue(sessionLocal), true) == 0) {
            value = ValueNull.INSTANCE;
        }
        return value;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        this.type = this.left.getType();
        TypeInfo.checkComparable(this.type, this.right.getType());
        if (this.left.isConstant() && this.right.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "NULLIF";
    }
}

package org.h2.expression.function;

import org.h2.expression.Expression;
import org.h2.expression.Operation1;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/Function1.class */
public abstract class Function1 extends Operation1 implements NamedExpression {
    /* JADX INFO: Access modifiers changed from: protected */
    public Function1(Expression expression) {
        super(expression);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return this.arg.getUnenclosedSQL(sb.append(getName()).append('('), i).append(')');
    }
}

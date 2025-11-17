package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Variable.class */
public final class Variable extends Operation0 {
    private final String name;
    private Value lastValue;

    public Variable(SessionLocal sessionLocal, String str) {
        this.name = str;
        this.lastValue = sessionLocal.getVariable(str);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        return 0;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return ParserUtil.quoteIdentifier(sb.append('@'), this.name, i);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.lastValue.getType();
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        this.lastValue = sessionLocal.getVariable(this.name);
        return this.lastValue;
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return true;
        }
    }

    public String getName() {
        return this.name;
    }
}

package org.h2.expression.function;

import java.util.regex.Pattern;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/SignalFunction.class */
public final class SignalFunction extends Function2 {
    private static final Pattern SIGNAL_PATTERN = Pattern.compile("[0-9A-Z]{5}");

    public SignalFunction(Expression expression, Expression expression2) {
        super(expression, expression2);
    }

    @Override // org.h2.expression.function.Function2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        String string = value.getString();
        if (string.startsWith("00") || !SIGNAL_PATTERN.matcher(string).matches()) {
            throw DbException.getInvalidValueException("SQLSTATE", string);
        }
        throw DbException.fromUser(string, value2.getString());
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        this.type = TypeInfo.TYPE_NULL;
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "SIGNAL";
    }
}

package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/LengthFunction.class */
public final class LengthFunction extends Function1 {
    public static final int CHAR_LENGTH = 0;
    public static final int OCTET_LENGTH = 1;
    public static final int BIT_LENGTH = 2;
    private static final String[] NAMES = {"CHAR_LENGTH", "OCTET_LENGTH", "BIT_LENGTH"};
    private final int function;

    public LengthFunction(Expression expression, int i) {
        super(expression);
        this.function = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        long octetLength;
        Value value = this.arg.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        switch (this.function) {
            case 0:
                octetLength = value.charLength();
                break;
            case 1:
                octetLength = value.octetLength();
                break;
            case 2:
                octetLength = value.octetLength() * 8;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return ValueBigint.get(octetLength);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        this.type = TypeInfo.TYPE_BIGINT;
        if (this.arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}

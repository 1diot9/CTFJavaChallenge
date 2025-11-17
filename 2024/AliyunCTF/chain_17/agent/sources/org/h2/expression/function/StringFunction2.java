package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/StringFunction2.class */
public final class StringFunction2 extends Function2 {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int REPEAT = 2;
    private static final String[] NAMES = {"LEFT", "RIGHT", "REPEAT"};
    private final int function;

    public StringFunction2(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.function = i;
    }

    @Override // org.h2.expression.function.Function2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        String sb;
        String string = value.getString();
        int i = value2.getInt();
        if (i <= 0) {
            return ValueVarchar.get("", sessionLocal);
        }
        int length = string.length();
        switch (this.function) {
            case 0:
                if (i > length) {
                    i = length;
                }
                sb = string.substring(0, i);
                break;
            case 1:
                if (i > length) {
                    i = length;
                }
                sb = string.substring(length - i);
                break;
            case 2:
                StringBuilder sb2 = new StringBuilder(length * i);
                while (true) {
                    int i2 = i;
                    i--;
                    if (i2 > 0) {
                        sb2.append(string);
                    } else {
                        sb = sb2.toString();
                        break;
                    }
                }
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return ValueVarchar.get(sb, sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        switch (this.function) {
            case 0:
            case 1:
                this.type = TypeInfo.getTypeInfo(2, this.left.getType().getPrecision(), 0, null);
                break;
            case 2:
                this.type = TypeInfo.TYPE_VARCHAR;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (this.left.isConstant() && this.right.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}

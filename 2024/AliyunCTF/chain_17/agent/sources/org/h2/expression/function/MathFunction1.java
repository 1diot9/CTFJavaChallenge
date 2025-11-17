package org.h2.expression.function;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/MathFunction1.class */
public final class MathFunction1 extends Function1 {
    public static final int SIN = 0;
    public static final int COS = 1;
    public static final int TAN = 2;
    public static final int COT = 3;
    public static final int SINH = 4;
    public static final int COSH = 5;
    public static final int TANH = 6;
    public static final int ASIN = 7;
    public static final int ACOS = 8;
    public static final int ATAN = 9;
    public static final int LOG10 = 10;
    public static final int LN = 11;
    public static final int EXP = 12;
    public static final int SQRT = 13;
    public static final int DEGREES = 14;
    public static final int RADIANS = 15;
    private static final String[] NAMES = {"SIN", "COS", "TAN", "COT", "SINH", "COSH", "TANH", "ASIN", "ACOS", "ATAN", "LOG10", "LN", "EXP", "SQRT", "DEGREES", "RADIANS"};
    private final int function;

    public MathFunction1(Expression expression, int i) {
        super(expression);
        this.function = i;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        double radians;
        Value value = this.arg.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        double d = value.getDouble();
        switch (this.function) {
            case 0:
                radians = Math.sin(d);
                break;
            case 1:
                radians = Math.cos(d);
                break;
            case 2:
                radians = Math.tan(d);
                break;
            case 3:
                double tan = Math.tan(d);
                if (tan == 0.0d) {
                    throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
                }
                radians = 1.0d / tan;
                break;
            case 4:
                radians = Math.sinh(d);
                break;
            case 5:
                radians = Math.cosh(d);
                break;
            case 6:
                radians = Math.tanh(d);
                break;
            case 7:
                radians = Math.asin(d);
                break;
            case 8:
                radians = Math.acos(d);
                break;
            case 9:
                radians = Math.atan(d);
                break;
            case 10:
                if (d <= 0.0d) {
                    throw DbException.getInvalidValueException("LOG10() argument", Double.valueOf(d));
                }
                radians = Math.log10(d);
                break;
            case 11:
                if (d <= 0.0d) {
                    throw DbException.getInvalidValueException("LN() argument", Double.valueOf(d));
                }
                radians = Math.log(d);
                break;
            case 12:
                radians = Math.exp(d);
                break;
            case 13:
                radians = Math.sqrt(d);
                break;
            case 14:
                radians = Math.toDegrees(d);
                break;
            case 15:
                radians = Math.toRadians(d);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return ValueDouble.get(radians);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        this.type = TypeInfo.TYPE_DOUBLE;
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

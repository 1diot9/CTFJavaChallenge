package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/MathFunction2.class */
public final class MathFunction2 extends Function2 {
    public static final int ATAN2 = 0;
    public static final int LOG = 1;
    public static final int POWER = 2;
    private static final String[] NAMES = {"ATAN2", "LOG", "POWER"};
    private final int function;

    public MathFunction2(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.function = i;
    }

    @Override // org.h2.expression.function.Function2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        double pow;
        double d = value.getDouble();
        double d2 = value2.getDouble();
        switch (this.function) {
            case 0:
                pow = Math.atan2(d, d2);
                break;
            case 1:
                if (sessionLocal.getMode().swapLogFunctionParameters) {
                    d2 = d;
                    d = d2;
                }
                if (d2 > 0.0d) {
                    if (d > 0.0d && d != 1.0d) {
                        if (d != 2.718281828459045d) {
                            if (d == 10.0d) {
                                pow = Math.log10(d2);
                                break;
                            } else {
                                pow = Math.log(d2) / Math.log(d);
                                break;
                            }
                        } else {
                            pow = Math.log(d2);
                            break;
                        }
                    } else {
                        throw DbException.getInvalidValueException("LOG() base", Double.valueOf(d));
                    }
                } else {
                    throw DbException.getInvalidValueException("LOG() argument", Double.valueOf(d2));
                }
                break;
            case 2:
                pow = Math.pow(d, d2);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return ValueDouble.get(pow);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        this.type = TypeInfo.TYPE_DOUBLE;
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

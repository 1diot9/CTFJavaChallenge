package org.h2.expression.function;

import java.math.RoundingMode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/MathFunction.class */
public final class MathFunction extends Function1_2 {
    public static final int ABS = 0;
    public static final int MOD = 1;
    public static final int FLOOR = 2;
    public static final int CEIL = 3;
    public static final int ROUND = 4;
    public static final int ROUNDMAGIC = 5;
    public static final int SIGN = 6;
    public static final int TRUNC = 7;
    private static final String[] NAMES = {"ABS", "MOD", "FLOOR", "CEIL", "ROUND", "ROUNDMAGIC", "SIGN", "TRUNC"};
    private final int function;
    private TypeInfo commonType;

    public MathFunction(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.function = i;
    }

    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        switch (this.function) {
            case 0:
                if (value.getSignum() < 0) {
                    value = value.negate();
                    break;
                }
                break;
            case 1:
                value = value.convertTo(this.commonType, sessionLocal).modulus(value2.convertTo(this.commonType, sessionLocal)).convertTo(this.type, sessionLocal);
                break;
            case 2:
                value = round(value, value2, RoundingMode.FLOOR);
                break;
            case 3:
                value = round(value, value2, RoundingMode.CEILING);
                break;
            case 4:
                value = round(value, value2, RoundingMode.HALF_UP);
                break;
            case 5:
                value = ValueDouble.get(roundMagic(value.getDouble()));
                break;
            case 6:
                value = ValueInteger.get(value.getSignum());
                break;
            case 7:
                value = round(value, value2, RoundingMode.DOWN);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return value;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0111  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x011a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.h2.value.Value round(org.h2.value.Value r6, org.h2.value.Value r7, java.math.RoundingMode r8) {
        /*
            Method dump skipped, instructions count: 349
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.function.MathFunction.round(org.h2.value.Value, org.h2.value.Value, java.math.RoundingMode):org.h2.value.Value");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.h2.expression.function.MathFunction$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/MathFunction$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$math$RoundingMode = new int[RoundingMode.values().length];

        static {
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.DOWN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.CEILING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.FLOOR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private static double roundMagic(double d) {
        if (d < 1.0E-13d && d > -1.0E-13d) {
            return 0.0d;
        }
        if (d > 1.0E12d || d < -1.0E12d) {
            return d;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(d);
        if (sb.toString().indexOf(69) >= 0) {
            return d;
        }
        int length = sb.length();
        if (length < 16) {
            return d;
        }
        if (sb.toString().indexOf(46) > length - 3) {
            return d;
        }
        sb.delete(length - 2, length);
        int i = length - 2;
        char charAt = sb.charAt(i - 2);
        char charAt2 = sb.charAt(i - 3);
        char charAt3 = sb.charAt(i - 4);
        if (charAt == '0' && charAt2 == '0' && charAt3 == '0') {
            sb.setCharAt(i - 1, '0');
        } else if (charAt == '9' && charAt2 == '9' && charAt3 == '9') {
            sb.setCharAt(i - 1, '9');
            sb.append('9');
            sb.append('9');
            sb.append('9');
        }
        return Double.parseDouble(sb.toString());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:47:0x011a. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0168  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x017d  */
    @Override // org.h2.expression.Expression
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.expression.Expression optimize(org.h2.engine.SessionLocal r11) {
        /*
            Method dump skipped, instructions count: 541
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.function.MathFunction.optimize(org.h2.engine.SessionLocal):org.h2.expression.Expression");
    }

    private Expression optimizeRoundWithScale(SessionLocal sessionLocal, boolean z) {
        int i;
        boolean z2 = false;
        boolean z3 = false;
        if (this.right != null) {
            if (this.right.isConstant()) {
                Value value = this.right.getValue(sessionLocal);
                z2 = true;
                if (value != ValueNull.INSTANCE) {
                    i = value.getInt();
                } else {
                    i = -1;
                    z3 = true;
                }
            } else {
                i = -1;
            }
        } else {
            i = 0;
            z2 = true;
        }
        return optimizeRound(i, z2, z3, z);
    }

    private Expression optimizeRound(int i, boolean z, boolean z2, boolean z3) {
        long precision;
        TypeInfo type = this.left.getType();
        switch (type.getValueType()) {
            case 0:
                this.type = TypeInfo.TYPE_NUMERIC_SCALE_0;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            default:
                throw Store.getInvalidExpressionTypeException(getName() + " argument", this.left);
            case 9:
            case 10:
            case 11:
            case 12:
                if (z && i >= 0) {
                    return this.left;
                }
                this.type = type;
                break;
            case 13:
                int scale = type.getScale();
                if (z) {
                    if (scale <= i) {
                        return this.left;
                    }
                    if (i < 0) {
                        i = 0;
                    } else if (i > 100000) {
                        i = 100000;
                    }
                    precision = (type.getPrecision() - scale) + i;
                    if (z3) {
                        precision++;
                    }
                } else {
                    precision = type.getPrecision();
                    if (z3) {
                        precision++;
                    }
                    i = scale;
                }
                this.type = TypeInfo.getTypeInfo(13, precision, i, null);
                break;
            case 14:
            case 15:
            case 16:
                this.type = type;
                break;
        }
        if (z2) {
            return TypedValueExpression.get(ValueNull.INSTANCE, this.type);
        }
        return null;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}

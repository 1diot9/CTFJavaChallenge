package org.h2.expression.function;

import java.nio.charset.StandardCharsets;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/SoundexFunction.class */
public final class SoundexFunction extends Function1_2 {
    public static final int SOUNDEX = 0;
    public static final int DIFFERENCE = 1;
    private static final String[] NAMES = {"SOUNDEX", "DIFFERENCE"};
    private static final byte[] SOUNDEX_INDEX = "71237128722455712623718272������������71237128722455712623718272".getBytes(StandardCharsets.ISO_8859_1);
    private final int function;

    public SoundexFunction(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.function = i;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [org.h2.value.Value] */
    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        ValueInteger valueInteger;
        switch (this.function) {
            case 0:
                valueInteger = ValueVarchar.get(new String(getSoundex(value.getString()), StandardCharsets.ISO_8859_1), sessionLocal);
                break;
            case 1:
                valueInteger = ValueInteger.get(getDifference(value.getString(), value2.getString()));
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return valueInteger;
    }

    private static int getDifference(String str, String str2) {
        byte[] soundex = getSoundex(str);
        byte[] soundex2 = getSoundex(str2);
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            if (soundex[i2] == soundex2[i2]) {
                i++;
            }
        }
        return i;
    }

    private static byte[] getSoundex(String str) {
        byte b;
        byte[] bArr = new byte[4];
        bArr[0] = 48;
        bArr[1] = 48;
        bArr[2] = 48;
        bArr[3] = 48;
        byte b2 = 48;
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length && i < 4; i2++) {
            char charAt = str.charAt(i2);
            if (charAt >= 'A' && charAt <= 'z' && (b = SOUNDEX_INDEX[charAt - 'A']) != 0) {
                if (i == 0) {
                    int i3 = i;
                    i++;
                    bArr[i3] = (byte) (charAt & 223);
                    b2 = b;
                } else if (b <= 54) {
                    if (b != b2) {
                        int i4 = i;
                        i++;
                        b2 = b;
                        bArr[i4] = b;
                    }
                } else if (b == 55) {
                    b2 = b;
                }
            }
        }
        return bArr;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        switch (this.function) {
            case 0:
                this.type = TypeInfo.getTypeInfo(2, 4L, 0, null);
                break;
            case 1:
                this.type = TypeInfo.TYPE_INTEGER;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}

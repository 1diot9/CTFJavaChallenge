package org.h2.value;

import java.math.BigDecimal;
import org.apache.tomcat.util.bcel.Const;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueInteger.class */
public final class ValueInteger extends Value {
    public static final int PRECISION = 32;
    public static final int DECIMAL_PRECISION = 10;
    public static final int DISPLAY_SIZE = 11;
    private static final int STATIC_SIZE = 128;
    private static final int DYNAMIC_SIZE = 256;
    private static final ValueInteger[] STATIC_CACHE = new ValueInteger[128];
    private static final ValueInteger[] DYNAMIC_CACHE = new ValueInteger[256];
    private final int value;

    static {
        for (int i = 0; i < 128; i++) {
            STATIC_CACHE[i] = new ValueInteger(i);
        }
    }

    private ValueInteger(int i) {
        this.value = i;
    }

    public static ValueInteger get(int i) {
        if (i >= 0 && i < 128) {
            return STATIC_CACHE[i];
        }
        ValueInteger valueInteger = DYNAMIC_CACHE[i & Const.MAX_ARRAY_DIMENSIONS];
        if (valueInteger == null || valueInteger.value != i) {
            valueInteger = new ValueInteger(i);
            DYNAMIC_CACHE[i & Const.MAX_ARRAY_DIMENSIONS] = valueInteger;
        }
        return valueInteger;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return checkRange(this.value + ((ValueInteger) value).value);
    }

    private static ValueInteger checkRange(long j) {
        if (((int) j) != j) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, Long.toString(j));
        }
        return get((int) j);
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        return Integer.signum(this.value);
    }

    @Override // org.h2.value.Value
    public Value negate() {
        return checkRange(-this.value);
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return checkRange(this.value - ((ValueInteger) value).value);
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return checkRange(this.value * ((ValueInteger) value).value);
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        int i = ((ValueInteger) value).value;
        if (i == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        int i2 = this.value;
        if (i2 == Integer.MIN_VALUE && i == -1) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, "2147483648");
        }
        return get(i2 / i);
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        ValueInteger valueInteger = (ValueInteger) value;
        if (valueInteger.value == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value % valueInteger.value);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(this.value);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_INTEGER;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 11;
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        byte[] bArr = new byte[4];
        Bits.writeInt(bArr, 0, getInt());
        return bArr;
    }

    @Override // org.h2.value.Value
    public int getInt() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public long getLong() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        return BigDecimal.valueOf(this.value);
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Integer.compare(this.value, ((ValueInteger) value).value);
    }

    @Override // org.h2.value.Value
    public String getString() {
        return Integer.toString(this.value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueInteger) && this.value == ((ValueInteger) obj).value;
    }
}

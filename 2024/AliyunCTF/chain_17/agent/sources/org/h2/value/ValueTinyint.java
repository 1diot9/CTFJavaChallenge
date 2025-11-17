package org.h2.value;

import java.math.BigDecimal;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueTinyint.class */
public final class ValueTinyint extends Value {
    static final int PRECISION = 8;
    public static final int DECIMAL_PRECISION = 3;
    static final int DISPLAY_SIZE = 4;
    private static final ValueTinyint[] STATIC_CACHE;
    private final byte value;

    static {
        ValueTinyint[] valueTinyintArr = new ValueTinyint[256];
        for (int i = 0; i < 256; i++) {
            valueTinyintArr[i] = new ValueTinyint((byte) (i - 128));
        }
        STATIC_CACHE = valueTinyintArr;
    }

    private ValueTinyint(byte b) {
        this.value = b;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return checkRange(this.value + ((ValueTinyint) value).value);
    }

    private static ValueTinyint checkRange(int i) {
        if (((byte) i) != i) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, Integer.toString(i));
        }
        return get((byte) i);
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
        return checkRange(this.value - ((ValueTinyint) value).value);
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return checkRange(this.value * ((ValueTinyint) value).value);
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        ValueTinyint valueTinyint = (ValueTinyint) value;
        if (valueTinyint.value == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return checkRange(this.value / valueTinyint.value);
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        ValueTinyint valueTinyint = (ValueTinyint) value;
        if (valueTinyint.value == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get((byte) (this.value % valueTinyint.value));
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return sb.append("CAST(").append((int) this.value).append(" AS TINYINT)");
        }
        return sb.append((int) this.value);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_TINYINT;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 9;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 0;
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        return new byte[]{this.value};
    }

    @Override // org.h2.value.Value
    public byte getByte() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public short getShort() {
        return this.value;
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
        return Integer.compare(this.value, ((ValueTinyint) value).value);
    }

    @Override // org.h2.value.Value
    public String getString() {
        return Integer.toString(this.value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return this.value;
    }

    public static ValueTinyint get(byte b) {
        return STATIC_CACHE[b + 128];
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueTinyint) && this.value == ((ValueTinyint) obj).value;
    }
}

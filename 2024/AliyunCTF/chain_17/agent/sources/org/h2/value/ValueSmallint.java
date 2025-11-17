package org.h2.value;

import java.math.BigDecimal;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueSmallint.class */
public final class ValueSmallint extends Value {
    static final int PRECISION = 16;
    public static final int DECIMAL_PRECISION = 5;
    static final int DISPLAY_SIZE = 6;
    private final short value;

    private ValueSmallint(short s) {
        this.value = s;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return checkRange(this.value + ((ValueSmallint) value).value);
    }

    private static ValueSmallint checkRange(int i) {
        if (((short) i) != i) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, Integer.toString(i));
        }
        return get((short) i);
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
        return checkRange(this.value - ((ValueSmallint) value).value);
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return checkRange(this.value * ((ValueSmallint) value).value);
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        ValueSmallint valueSmallint = (ValueSmallint) value;
        if (valueSmallint.value == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return checkRange(this.value / valueSmallint.value);
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        ValueSmallint valueSmallint = (ValueSmallint) value;
        if (valueSmallint.value == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get((short) (this.value % valueSmallint.value));
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return sb.append("CAST(").append((int) this.value).append(" AS SMALLINT)");
        }
        return sb.append((int) this.value);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_SMALLINT;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 10;
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        short s = this.value;
        return new byte[]{(byte) (s >> 8), (byte) s};
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
        return Integer.compare(this.value, ((ValueSmallint) value).value);
    }

    @Override // org.h2.value.Value
    public String getString() {
        return Integer.toString(this.value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return this.value;
    }

    public static ValueSmallint get(short s) {
        return (ValueSmallint) Value.cache(new ValueSmallint(s));
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueSmallint) && this.value == ((ValueSmallint) obj).value;
    }
}

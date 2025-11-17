package org.h2.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueBigint.class */
public final class ValueBigint extends Value {
    static final int PRECISION = 64;
    public static final int DECIMAL_PRECISION = 19;
    public static final int DISPLAY_SIZE = 20;
    private static final int STATIC_SIZE = 100;
    private final long value;
    public static final ValueBigint MIN = get(Long.MIN_VALUE);
    public static final ValueBigint MAX = get(Long.MAX_VALUE);
    public static final BigInteger MAX_BI = BigInteger.valueOf(Long.MAX_VALUE);
    private static final ValueBigint[] STATIC_CACHE = new ValueBigint[100];

    static {
        for (int i = 0; i < 100; i++) {
            STATIC_CACHE[i] = new ValueBigint(i);
        }
    }

    private ValueBigint(long j) {
        this.value = j;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        long j = this.value;
        long j2 = ((ValueBigint) value).value;
        long j3 = j + j2;
        if (((j ^ j3) & (j2 ^ j3)) < 0) {
            throw getOverflow();
        }
        return get(j3);
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        return Long.signum(this.value);
    }

    @Override // org.h2.value.Value
    public Value negate() {
        if (this.value == Long.MIN_VALUE) {
            throw getOverflow();
        }
        return get(-this.value);
    }

    private DbException getOverflow() {
        return DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, Long.toString(this.value));
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        long j = this.value;
        long j2 = ((ValueBigint) value).value;
        long j3 = j - j2;
        if (((j ^ j2) & (j ^ j3)) < 0) {
            throw getOverflow();
        }
        return get(j3);
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        long j = this.value;
        long j2 = ((ValueBigint) value).value;
        long j3 = j * j2;
        if (((Math.abs(j) | Math.abs(j2)) >>> 31) != 0 && j2 != 0 && (j3 / j2 != j || (j == Long.MIN_VALUE && j2 == -1))) {
            throw getOverflow();
        }
        return get(j3);
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        long j = ((ValueBigint) value).value;
        if (j == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        long j2 = this.value;
        if (j2 == Long.MIN_VALUE && j == -1) {
            throw getOverflow();
        }
        return get(j2 / j);
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        ValueBigint valueBigint = (ValueBigint) value;
        if (valueBigint.value == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value % valueBigint.value);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0 && this.value == ((int) this.value)) {
            return sb.append("CAST(").append(this.value).append(" AS BIGINT)");
        }
        return sb.append(this.value);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_BIGINT;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 12;
    }

    @Override // org.h2.value.Value
    public byte[] getBytes() {
        byte[] bArr = new byte[8];
        Bits.writeLong(bArr, 0, getLong());
        return bArr;
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
        return (float) this.value;
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Long.compare(this.value, ((ValueBigint) value).value);
    }

    @Override // org.h2.value.Value
    public String getString() {
        return Long.toString(this.value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) (this.value ^ (this.value >> 32));
    }

    public static ValueBigint get(long j) {
        if (j >= 0 && j < 100) {
            return STATIC_CACHE[(int) j];
        }
        return (ValueBigint) Value.cache(new ValueBigint(j));
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueBigint) && this.value == ((ValueBigint) obj).value;
    }
}

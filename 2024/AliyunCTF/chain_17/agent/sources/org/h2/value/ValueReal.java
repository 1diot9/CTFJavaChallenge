package org.h2.value;

import java.math.BigDecimal;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueReal.class */
public final class ValueReal extends Value {
    static final int PRECISION = 24;
    static final int DECIMAL_PRECISION = 7;
    static final int DISPLAY_SIZE = 15;
    public static final int ZERO_BITS = 0;
    public static final ValueReal ZERO = new ValueReal(0.0f);
    public static final ValueReal ONE = new ValueReal(1.0f);
    private static final ValueReal NAN = new ValueReal(Float.NaN);
    private final float value;

    private ValueReal(float f) {
        this.value = f;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return get(this.value + ((ValueReal) value).value);
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return get(this.value - ((ValueReal) value).value);
    }

    @Override // org.h2.value.Value
    public Value negate() {
        return get(-this.value);
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return get(this.value * ((ValueReal) value).value);
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        ValueReal valueReal = (ValueReal) value;
        if (valueReal.value == 0.0d) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value / valueReal.value);
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        ValueReal valueReal = (ValueReal) value;
        if (valueReal.value == 0.0f) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value % valueReal.value);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return getSQL(sb.append("CAST(")).append(" AS REAL)");
        }
        return getSQL(sb);
    }

    private StringBuilder getSQL(StringBuilder sb) {
        if (this.value == Float.POSITIVE_INFINITY) {
            return sb.append("'Infinity'");
        }
        if (this.value == Float.NEGATIVE_INFINITY) {
            return sb.append("'-Infinity'");
        }
        if (Float.isNaN(this.value)) {
            return sb.append("'NaN'");
        }
        return sb.append(this.value);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_REAL;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 14;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Float.compare(this.value, ((ValueReal) value).value);
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        if (this.value == 0.0f || Float.isNaN(this.value)) {
            return 0;
        }
        return this.value < 0.0f ? -1 : 1;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        if (Float.isFinite(this.value)) {
            return new BigDecimal(Float.toString(this.value));
        }
        throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, Float.toString(this.value));
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
    public String getString() {
        return Float.toString(this.value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return Float.floatToRawIntBits(this.value);
    }

    public static ValueReal get(float f) {
        if (f == 1.0f) {
            return ONE;
        }
        if (f == 0.0f) {
            return ZERO;
        }
        if (Float.isNaN(f)) {
            return NAN;
        }
        return (ValueReal) Value.cache(new ValueReal(f));
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueReal) && compareTypeSafe((ValueReal) obj, null, null) == 0;
    }
}

package org.h2.value;

import java.math.BigDecimal;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueDouble.class */
public final class ValueDouble extends Value {
    static final int PRECISION = 53;
    public static final int DECIMAL_PRECISION = 17;
    public static final int DISPLAY_SIZE = 24;
    public static final long ZERO_BITS = 0;
    public static final ValueDouble ZERO = new ValueDouble(0.0d);
    public static final ValueDouble ONE = new ValueDouble(1.0d);
    private static final ValueDouble NAN = new ValueDouble(Double.NaN);
    private final double value;

    private ValueDouble(double d) {
        this.value = d;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return get(this.value + ((ValueDouble) value).value);
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return get(this.value - ((ValueDouble) value).value);
    }

    @Override // org.h2.value.Value
    public Value negate() {
        return get(-this.value);
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return get(this.value * ((ValueDouble) value).value);
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        ValueDouble valueDouble = (ValueDouble) value;
        if (valueDouble.value == 0.0d) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value / valueDouble.value);
    }

    @Override // org.h2.value.Value
    public ValueDouble modulus(Value value) {
        ValueDouble valueDouble = (ValueDouble) value;
        if (valueDouble.value == 0.0d) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value % valueDouble.value);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return getSQL(sb.append("CAST(")).append(" AS DOUBLE PRECISION)");
        }
        return getSQL(sb);
    }

    private StringBuilder getSQL(StringBuilder sb) {
        if (this.value == Double.POSITIVE_INFINITY) {
            return sb.append("'Infinity'");
        }
        if (this.value == Double.NEGATIVE_INFINITY) {
            return sb.append("'-Infinity'");
        }
        if (Double.isNaN(this.value)) {
            return sb.append("'NaN'");
        }
        return sb.append(this.value);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_DOUBLE;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 15;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Double.compare(this.value, ((ValueDouble) value).value);
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        if (this.value == 0.0d || Double.isNaN(this.value)) {
            return 0;
        }
        return this.value < 0.0d ? -1 : 1;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        if (Double.isFinite(this.value)) {
            return BigDecimal.valueOf(this.value);
        }
        throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, Double.toString(this.value));
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
    public String getString() {
        return Double.toString(this.value);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        long doubleToRawLongBits = Double.doubleToRawLongBits(this.value);
        return (int) (doubleToRawLongBits ^ (doubleToRawLongBits >>> 32));
    }

    public static ValueDouble get(double d) {
        if (d == 1.0d) {
            return ONE;
        }
        if (d == 0.0d) {
            return ZERO;
        }
        if (Double.isNaN(d)) {
            return NAN;
        }
        return (ValueDouble) Value.cache(new ValueDouble(d));
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueDouble) && compareTypeSafe((ValueDouble) obj, null, null) == 0;
    }
}

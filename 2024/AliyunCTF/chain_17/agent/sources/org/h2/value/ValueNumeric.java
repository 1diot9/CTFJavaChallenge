package org.h2.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueNumeric.class */
public final class ValueNumeric extends ValueBigDecimalBase {
    public static final ValueNumeric ZERO = new ValueNumeric(BigDecimal.ZERO);
    public static final ValueNumeric ONE = new ValueNumeric(BigDecimal.ONE);
    public static final int DEFAULT_SCALE = 0;
    public static final int MAXIMUM_SCALE = 100000;

    private ValueNumeric(BigDecimal bigDecimal) {
        super(bigDecimal);
        if (bigDecimal == null) {
            throw new IllegalArgumentException("null");
        }
        int scale = bigDecimal.scale();
        if (scale < 0 || scale > 100000) {
            throw DbException.get(ErrorCode.INVALID_VALUE_SCALE, Integer.toString(scale), CustomBooleanEditor.VALUE_0, "100000");
        }
    }

    @Override // org.h2.value.Value
    public String getString() {
        return this.value.toPlainString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        String string = getString();
        if ((i & 4) == 0 && string.indexOf(46) < 0 && this.value.compareTo(MAX_LONG_DECIMAL) <= 0 && this.value.compareTo(MIN_LONG_DECIMAL) >= 0) {
            return sb.append("CAST(").append(this.value).append(" AS NUMERIC(").append(this.value.precision()).append("))");
        }
        return sb.append(string);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = new TypeInfo(13, this.value.precision(), this.value.scale(), null);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 13;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return get(this.value.add(((ValueNumeric) value).value));
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return get(this.value.subtract(((ValueNumeric) value).value));
    }

    @Override // org.h2.value.Value
    public Value negate() {
        return get(this.value.negate());
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return get(this.value.multiply(((ValueNumeric) value).value));
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        BigDecimal bigDecimal = ((ValueNumeric) value).value;
        if (bigDecimal.signum() == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value.divide(bigDecimal, typeInfo.getScale(), RoundingMode.HALF_DOWN));
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        ValueNumeric valueNumeric = (ValueNumeric) value;
        if (valueNumeric.value.signum() == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        return get(this.value.remainder(valueNumeric.value));
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return this.value.compareTo(((ValueNumeric) value).value);
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        return this.value.signum();
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        return this.value;
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        return this.value.floatValue();
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        return this.value.doubleValue();
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (getClass().hashCode() * 31) + this.value.hashCode();
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return (obj instanceof ValueNumeric) && this.value.equals(((ValueNumeric) obj).value);
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return this.value.precision() + 120;
    }

    public static ValueNumeric get(BigDecimal bigDecimal) {
        if (BigDecimal.ZERO.equals(bigDecimal)) {
            return ZERO;
        }
        if (BigDecimal.ONE.equals(bigDecimal)) {
            return ONE;
        }
        return (ValueNumeric) Value.cache(new ValueNumeric(bigDecimal));
    }

    public static ValueNumeric getAnyScale(BigDecimal bigDecimal) {
        if (bigDecimal.scale() < 0) {
            bigDecimal = bigDecimal.setScale(0, RoundingMode.UNNECESSARY);
        }
        return get(bigDecimal);
    }

    public static ValueNumeric get(BigInteger bigInteger) {
        if (bigInteger.signum() == 0) {
            return ZERO;
        }
        if (BigInteger.ONE.equals(bigInteger)) {
            return ONE;
        }
        return (ValueNumeric) Value.cache(new ValueNumeric(new BigDecimal(bigInteger)));
    }

    public static BigDecimal setScale(BigDecimal bigDecimal, int i) {
        if (i < 0 || i > 100000) {
            throw DbException.getInvalidValueException("scale", Integer.valueOf(i));
        }
        return bigDecimal.setScale(i, RoundingMode.HALF_UP);
    }
}

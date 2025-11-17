package org.h2.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.h2.api.Interval;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueInterval.class */
public final class ValueInterval extends Value {
    public static final int DEFAULT_PRECISION = 2;
    public static final int MAXIMUM_PRECISION = 18;
    public static final int DEFAULT_SCALE = 6;
    public static final int MAXIMUM_SCALE = 9;
    private static final long[] MULTIPLIERS = {DateTimeUtils.NANOS_PER_SECOND, 12, 24, 1440, DateTimeUtils.NANOS_PER_DAY, 60, DateTimeUtils.NANOS_PER_HOUR, DateTimeUtils.NANOS_PER_MINUTE};
    private final int valueType;
    private final boolean negative;
    private final long leading;
    private final long remaining;

    public static ValueInterval from(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        return (ValueInterval) Value.cache(new ValueInterval(intervalQualifier.ordinal() + 22, IntervalUtils.validateInterval(intervalQualifier, z, j, j2), j, j2));
    }

    public static int getDisplaySize(int i, int i2, int i3) {
        switch (i) {
            case 22:
            case 25:
                return 17 + i2;
            case 23:
                return 18 + i2;
            case 24:
                return 16 + i2;
            case 26:
                return 19 + i2;
            case 27:
                return i3 > 0 ? 20 + i2 + i3 : 19 + i2;
            case 28:
                return 29 + i2;
            case 29:
                return 27 + i2;
            case 30:
                return 32 + i2;
            case 31:
                return i3 > 0 ? 36 + i2 + i3 : 35 + i2;
            case 32:
                return 30 + i2;
            case 33:
                return i3 > 0 ? 34 + i2 + i3 : 33 + i2;
            case 34:
                return i3 > 0 ? 33 + i2 + i3 : 32 + i2;
            default:
                throw DbException.getUnsupportedException(Integer.toString(i));
        }
    }

    private ValueInterval(int i, boolean z, long j, long j2) {
        this.valueType = i;
        this.negative = z;
        this.leading = j;
        this.remaining = j2;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return IntervalUtils.appendInterval(sb, getQualifier(), this.negative, this.leading, this.remaining);
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.getTypeInfo(this.valueType);
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return this.valueType;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 48;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean checkPrecision(long j) {
        if (j < 18) {
            long j2 = this.leading;
            long j3 = 0;
            for (long j4 = 1; j2 >= j4; j4 *= 10) {
                long j5 = j3 + 1;
                j3 = j5;
                if (j5 > j) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ValueInterval setPrecisionAndScale(TypeInfo typeInfo, Object obj) {
        long j;
        int scale = typeInfo.getScale();
        ValueInterval valueInterval = this;
        if (scale < 9) {
            switch (this.valueType) {
                case 27:
                    j = 1000000000;
                    break;
                case 31:
                    j = 86400000000000L;
                    break;
                case 33:
                    j = 3600000000000L;
                    break;
                case 34:
                    j = 60000000000L;
                    break;
            }
            long j2 = this.leading;
            long convertScale = DateTimeUtils.convertScale(this.remaining, scale, j2 == 999999999999999999L ? j : Long.MAX_VALUE);
            if (convertScale != this.remaining) {
                if (convertScale >= j) {
                    j2++;
                    convertScale -= j;
                }
                valueInterval = from(valueInterval.getQualifier(), valueInterval.isNegative(), j2, convertScale);
            }
        }
        if (!valueInterval.checkPrecision(typeInfo.getPrecision())) {
            throw valueInterval.getValueTooLongException(typeInfo, obj);
        }
        return valueInterval;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return IntervalUtils.appendInterval(new StringBuilder(), getQualifier(), this.negative, this.leading, this.remaining).toString();
    }

    @Override // org.h2.value.Value
    public long getLong() {
        long j = this.leading;
        if (this.valueType >= 27 && this.remaining != 0 && this.remaining >= (MULTIPLIERS[this.valueType - 27] >> 1)) {
            j++;
        }
        return this.negative ? -j : j;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        if (this.valueType < 27 || this.remaining == 0) {
            return BigDecimal.valueOf(this.negative ? -this.leading : this.leading);
        }
        BigDecimal valueOf = BigDecimal.valueOf(MULTIPLIERS[this.valueType - 27]);
        BigDecimal stripTrailingZeros = BigDecimal.valueOf(this.leading).add(BigDecimal.valueOf(this.remaining).divide(valueOf, valueOf.precision(), RoundingMode.HALF_DOWN)).stripTrailingZeros();
        return this.negative ? stripTrailingZeros.negate() : stripTrailingZeros;
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        if (this.valueType < 27 || this.remaining == 0) {
            return this.negative ? (float) (-this.leading) : (float) this.leading;
        }
        return getBigDecimal().floatValue();
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        if (this.valueType < 27 || this.remaining == 0) {
            return this.negative ? -this.leading : this.leading;
        }
        return getBigDecimal().doubleValue();
    }

    public Interval getInterval() {
        return new Interval(getQualifier(), this.negative, this.leading, this.remaining);
    }

    public IntervalQualifier getQualifier() {
        return IntervalQualifier.valueOf(this.valueType - 22);
    }

    public boolean isNegative() {
        return this.negative;
    }

    public long getLeading() {
        return this.leading;
    }

    public long getRemaining() {
        return this.remaining;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (31 * ((31 * ((31 * ((31 * 1) + this.valueType)) + (this.negative ? 1231 : 1237))) + ((int) (this.leading ^ (this.leading >>> 32))))) + ((int) (this.remaining ^ (this.remaining >>> 32)));
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ValueInterval)) {
            return false;
        }
        ValueInterval valueInterval = (ValueInterval) obj;
        return this.valueType == valueInterval.valueType && this.negative == valueInterval.negative && this.leading == valueInterval.leading && this.remaining == valueInterval.remaining;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        ValueInterval valueInterval = (ValueInterval) value;
        if (this.negative != valueInterval.negative) {
            return this.negative ? -1 : 1;
        }
        int compare = Long.compare(this.leading, valueInterval.leading);
        if (compare == 0) {
            compare = Long.compare(this.remaining, valueInterval.remaining);
        }
        return this.negative ? -compare : compare;
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        if (this.negative) {
            return -1;
        }
        return (this.leading == 0 && this.remaining == 0) ? 0 : 1;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return IntervalUtils.intervalFromAbsolute(getQualifier(), IntervalUtils.intervalToAbsolute(this).add(IntervalUtils.intervalToAbsolute((ValueInterval) value)));
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return IntervalUtils.intervalFromAbsolute(getQualifier(), IntervalUtils.intervalToAbsolute(this).subtract(IntervalUtils.intervalToAbsolute((ValueInterval) value)));
    }

    @Override // org.h2.value.Value
    public Value negate() {
        if (this.leading == 0 && this.remaining == 0) {
            return this;
        }
        return Value.cache(new ValueInterval(this.valueType, !this.negative, this.leading, this.remaining));
    }
}

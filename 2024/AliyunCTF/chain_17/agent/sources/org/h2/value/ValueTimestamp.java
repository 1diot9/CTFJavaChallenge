package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueTimestamp.class */
public final class ValueTimestamp extends Value {
    public static final int DEFAULT_PRECISION = 26;
    public static final int MAXIMUM_PRECISION = 29;
    public static final int DEFAULT_SCALE = 6;
    public static final int MAXIMUM_SCALE = 9;
    private final long dateValue;
    private final long timeNanos;

    private ValueTimestamp(long j, long j2) {
        if (j < DateTimeUtils.MIN_DATE_VALUE || j > DateTimeUtils.MAX_DATE_VALUE) {
            throw new IllegalArgumentException("dateValue out of range " + j);
        }
        if (j2 < 0 || j2 >= DateTimeUtils.NANOS_PER_DAY) {
            throw new IllegalArgumentException("timeNanos out of range " + j2);
        }
        this.dateValue = j;
        this.timeNanos = j2;
    }

    public static ValueTimestamp fromDateValueAndNanos(long j, long j2) {
        return (ValueTimestamp) Value.cache(new ValueTimestamp(j, j2));
    }

    public static ValueTimestamp parse(String str, CastDataProvider castDataProvider) {
        try {
            return (ValueTimestamp) DateTimeUtils.parseTimestamp(str, castDataProvider, false);
        } catch (Exception e) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "TIMESTAMP", str);
        }
    }

    public long getDateValue() {
        return this.dateValue;
    }

    public long getTimeNanos() {
        return this.timeNanos;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_TIMESTAMP;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 20;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 32;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return toString(new StringBuilder(29), false).toString();
    }

    public String getISOString() {
        return toString(new StringBuilder(29), true).toString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return toString(sb.append("TIMESTAMP '"), false).append('\'');
    }

    private StringBuilder toString(StringBuilder sb, boolean z) {
        DateTimeUtils.appendDate(sb, this.dateValue).append(z ? 'T' : ' ');
        return DateTimeUtils.appendTime(sb, this.timeNanos);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        ValueTimestamp valueTimestamp = (ValueTimestamp) value;
        int compare = Long.compare(this.dateValue, valueTimestamp.dateValue);
        if (compare != 0) {
            return compare;
        }
        return Long.compare(this.timeNanos, valueTimestamp.timeNanos);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ValueTimestamp)) {
            return false;
        }
        ValueTimestamp valueTimestamp = (ValueTimestamp) obj;
        return this.dateValue == valueTimestamp.dateValue && this.timeNanos == valueTimestamp.timeNanos;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) (((this.dateValue ^ (this.dateValue >>> 32)) ^ this.timeNanos) ^ (this.timeNanos >>> 32));
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        ValueTimestamp valueTimestamp = (ValueTimestamp) value;
        long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(this.dateValue) + DateTimeUtils.absoluteDayFromDateValue(valueTimestamp.dateValue);
        long j = this.timeNanos + valueTimestamp.timeNanos;
        if (j >= DateTimeUtils.NANOS_PER_DAY) {
            j -= DateTimeUtils.NANOS_PER_DAY;
            absoluteDayFromDateValue++;
        }
        return fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(absoluteDayFromDateValue), j);
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        ValueTimestamp valueTimestamp = (ValueTimestamp) value;
        long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(this.dateValue) - DateTimeUtils.absoluteDayFromDateValue(valueTimestamp.dateValue);
        long j = this.timeNanos - valueTimestamp.timeNanos;
        if (j < 0) {
            j += DateTimeUtils.NANOS_PER_DAY;
            absoluteDayFromDateValue--;
        }
        return fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(absoluteDayFromDateValue), j);
    }
}

package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueTimestampTimeZone.class */
public final class ValueTimestampTimeZone extends Value {
    public static final int DEFAULT_PRECISION = 32;
    public static final int MAXIMUM_PRECISION = 35;
    private final long dateValue;
    private final long timeNanos;
    private final int timeZoneOffsetSeconds;

    private ValueTimestampTimeZone(long j, long j2, int i) {
        if (j < DateTimeUtils.MIN_DATE_VALUE || j > DateTimeUtils.MAX_DATE_VALUE) {
            throw new IllegalArgumentException("dateValue out of range " + j);
        }
        if (j2 < 0 || j2 >= DateTimeUtils.NANOS_PER_DAY) {
            throw new IllegalArgumentException("timeNanos out of range " + j2);
        }
        if (i < -64800 || i > 64800) {
            throw new IllegalArgumentException("timeZoneOffsetSeconds out of range " + i);
        }
        this.dateValue = j;
        this.timeNanos = j2;
        this.timeZoneOffsetSeconds = i;
    }

    public static ValueTimestampTimeZone fromDateValueAndNanos(long j, long j2, int i) {
        return (ValueTimestampTimeZone) Value.cache(new ValueTimestampTimeZone(j, j2, i));
    }

    public static ValueTimestampTimeZone parse(String str, CastDataProvider castDataProvider) {
        try {
            return (ValueTimestampTimeZone) DateTimeUtils.parseTimestamp(str, castDataProvider, true);
        } catch (Exception e) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "TIMESTAMP WITH TIME ZONE", str);
        }
    }

    public long getDateValue() {
        return this.dateValue;
    }

    public long getTimeNanos() {
        return this.timeNanos;
    }

    public int getTimeZoneOffsetSeconds() {
        return this.timeZoneOffsetSeconds;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_TIMESTAMP_TZ;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 21;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 40;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return toString(new StringBuilder(35), false).toString();
    }

    public String getISOString() {
        return toString(new StringBuilder(35), true).toString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return toString(sb.append("TIMESTAMP WITH TIME ZONE '"), false).append('\'');
    }

    private StringBuilder toString(StringBuilder sb, boolean z) {
        DateTimeUtils.appendDate(sb, this.dateValue).append(z ? 'T' : ' ');
        DateTimeUtils.appendTime(sb, this.timeNanos);
        return DateTimeUtils.appendTimeZone(sb, this.timeZoneOffsetSeconds);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
        long j = this.dateValue;
        long j2 = this.timeNanos - (this.timeZoneOffsetSeconds * DateTimeUtils.NANOS_PER_SECOND);
        if (j2 < 0) {
            j2 += DateTimeUtils.NANOS_PER_DAY;
            j = DateTimeUtils.decrementDateValue(j);
        } else if (j2 >= DateTimeUtils.NANOS_PER_DAY) {
            j2 -= DateTimeUtils.NANOS_PER_DAY;
            j = DateTimeUtils.incrementDateValue(j);
        }
        long j3 = valueTimestampTimeZone.dateValue;
        long j4 = valueTimestampTimeZone.timeNanos - (valueTimestampTimeZone.timeZoneOffsetSeconds * DateTimeUtils.NANOS_PER_SECOND);
        if (j4 < 0) {
            j4 += DateTimeUtils.NANOS_PER_DAY;
            j3 = DateTimeUtils.decrementDateValue(j3);
        } else if (j4 >= DateTimeUtils.NANOS_PER_DAY) {
            j4 -= DateTimeUtils.NANOS_PER_DAY;
            j3 = DateTimeUtils.incrementDateValue(j3);
        }
        int compare = Long.compare(j, j3);
        if (compare != 0) {
            return compare;
        }
        return Long.compare(j2, j4);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ValueTimestampTimeZone)) {
            return false;
        }
        ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) obj;
        return this.dateValue == valueTimestampTimeZone.dateValue && this.timeNanos == valueTimestampTimeZone.timeNanos && this.timeZoneOffsetSeconds == valueTimestampTimeZone.timeZoneOffsetSeconds;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) ((((this.dateValue ^ (this.dateValue >>> 32)) ^ this.timeNanos) ^ (this.timeNanos >>> 32)) ^ this.timeZoneOffsetSeconds);
    }
}

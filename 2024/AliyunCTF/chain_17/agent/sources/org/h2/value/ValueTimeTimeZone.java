package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueTimeTimeZone.class */
public final class ValueTimeTimeZone extends Value {
    public static final int DEFAULT_PRECISION = 14;
    public static final int MAXIMUM_PRECISION = 24;
    private final long nanos;
    private final int timeZoneOffsetSeconds;

    private ValueTimeTimeZone(long j, int i) {
        this.nanos = j;
        this.timeZoneOffsetSeconds = i;
    }

    public static ValueTimeTimeZone fromNanos(long j, int i) {
        if (j < 0 || j >= DateTimeUtils.NANOS_PER_DAY) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, "TIME WITH TIME ZONE", DateTimeUtils.appendTime(new StringBuilder(), j).toString());
        }
        if (i < -64800 || i > 64800) {
            throw new IllegalArgumentException("timeZoneOffsetSeconds " + i);
        }
        return (ValueTimeTimeZone) Value.cache(new ValueTimeTimeZone(j, i));
    }

    public static ValueTimeTimeZone parse(String str, CastDataProvider castDataProvider) {
        try {
            return (ValueTimeTimeZone) DateTimeUtils.parseTime(str, castDataProvider, true);
        } catch (Exception e) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "TIME WITH TIME ZONE", str);
        }
    }

    public long getNanos() {
        return this.nanos;
    }

    public int getTimeZoneOffsetSeconds() {
        return this.timeZoneOffsetSeconds;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_TIME_TZ;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 19;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        return 32;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return toString(new StringBuilder(24)).toString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return toString(sb.append("TIME WITH TIME ZONE '")).append('\'');
    }

    private StringBuilder toString(StringBuilder sb) {
        return DateTimeUtils.appendTimeZone(DateTimeUtils.appendTime(sb, this.nanos), this.timeZoneOffsetSeconds);
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Long.compare(this.nanos - (this.timeZoneOffsetSeconds * DateTimeUtils.NANOS_PER_SECOND), ((ValueTimeTimeZone) value).nanos - (r0.timeZoneOffsetSeconds * DateTimeUtils.NANOS_PER_SECOND));
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ValueTimeTimeZone)) {
            return false;
        }
        ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) obj;
        return this.nanos == valueTimeTimeZone.nanos && this.timeZoneOffsetSeconds == valueTimeTimeZone.timeZoneOffsetSeconds;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) ((this.nanos ^ (this.nanos >>> 32)) ^ this.timeZoneOffsetSeconds);
    }
}

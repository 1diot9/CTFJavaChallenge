package org.h2.value;

import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueTime.class */
public final class ValueTime extends Value {
    public static final int DEFAULT_PRECISION = 8;
    public static final int MAXIMUM_PRECISION = 18;
    public static final int DEFAULT_SCALE = 0;
    public static final int MAXIMUM_SCALE = 9;
    private static final ValueTime[] STATIC_CACHE;
    private final long nanos;

    static {
        ValueTime[] valueTimeArr = new ValueTime[24];
        for (int i = 0; i < 24; i++) {
            valueTimeArr[i] = new ValueTime(i * DateTimeUtils.NANOS_PER_HOUR);
        }
        STATIC_CACHE = valueTimeArr;
    }

    private ValueTime(long j) {
        this.nanos = j;
    }

    public static ValueTime fromNanos(long j) {
        if (j < 0 || j >= DateTimeUtils.NANOS_PER_DAY) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, "TIME", DateTimeUtils.appendTime(new StringBuilder(), j).toString());
        }
        if (j % DateTimeUtils.NANOS_PER_HOUR == 0) {
            return STATIC_CACHE[(int) (j / DateTimeUtils.NANOS_PER_HOUR)];
        }
        return (ValueTime) Value.cache(new ValueTime(j));
    }

    public static ValueTime parse(String str, CastDataProvider castDataProvider) {
        try {
            return (ValueTime) DateTimeUtils.parseTime(str, castDataProvider, false);
        } catch (Exception e) {
            throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, e, "TIME", str);
        }
    }

    public long getNanos() {
        return this.nanos;
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_TIME;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 18;
    }

    @Override // org.h2.value.Value
    public String getString() {
        return DateTimeUtils.appendTime(new StringBuilder(18), this.nanos).toString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return DateTimeUtils.appendTime(sb.append("TIME '"), this.nanos).append('\'');
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        return Long.compare(this.nanos, ((ValueTime) value).nanos);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        return this == obj || ((obj instanceof ValueTime) && this.nanos == ((ValueTime) obj).nanos);
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return (int) (this.nanos ^ (this.nanos >>> 32));
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        return fromNanos(this.nanos + ((ValueTime) value).getNanos());
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        return fromNanos(this.nanos - ((ValueTime) value).getNanos());
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        return fromNanos((long) (this.nanos * value.getDouble()));
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        return fromNanos((long) (this.nanos / value.getDouble()));
    }
}

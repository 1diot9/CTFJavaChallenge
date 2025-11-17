package org.h2.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.h2.engine.CastDataProvider;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueNull;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/LegacyDateTimeUtils.class */
public final class LegacyDateTimeUtils {
    public static final Date PROLEPTIC_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private LegacyDateTimeUtils() {
    }

    public static ValueDate fromDate(CastDataProvider castDataProvider, TimeZone timeZone, Date date) {
        return ValueDate.fromDateValue(dateValueFromLocalMillis(date.getTime() + (timeZone == null ? getTimeZoneOffsetMillis(castDataProvider, r0) : timeZone.getOffset(r0))));
    }

    public static ValueTime fromTime(CastDataProvider castDataProvider, TimeZone timeZone, Time time) {
        return ValueTime.fromNanos(nanosFromLocalMillis(time.getTime() + (timeZone == null ? getTimeZoneOffsetMillis(castDataProvider, r0) : timeZone.getOffset(r0))));
    }

    public static ValueTimestamp fromTimestamp(CastDataProvider castDataProvider, TimeZone timeZone, Timestamp timestamp) {
        return timestampFromLocalMillis(timestamp.getTime() + (timeZone == null ? getTimeZoneOffsetMillis(castDataProvider, r0) : timeZone.getOffset(r0)), timestamp.getNanos() % 1000000);
    }

    public static ValueTimestamp fromTimestamp(CastDataProvider castDataProvider, long j, int i) {
        return timestampFromLocalMillis(j + getTimeZoneOffsetMillis(castDataProvider, j), i);
    }

    private static ValueTimestamp timestampFromLocalMillis(long j, int i) {
        return ValueTimestamp.fromDateValueAndNanos(dateValueFromLocalMillis(j), i + nanosFromLocalMillis(j));
    }

    public static long dateValueFromLocalMillis(long j) {
        long j2 = j / 86400000;
        if (j < 0 && j2 * 86400000 != j) {
            j2--;
        }
        return DateTimeUtils.dateValueFromAbsoluteDay(j2);
    }

    public static long nanosFromLocalMillis(long j) {
        long j2 = j % 86400000;
        if (j2 < 0) {
            j2 += 86400000;
        }
        return j2 * 1000000;
    }

    public static Date toDate(CastDataProvider castDataProvider, TimeZone timeZone, Value value) {
        if (value != ValueNull.INSTANCE) {
            return new Date(getMillis(castDataProvider, timeZone, value.convertToDate(castDataProvider).getDateValue(), 0L));
        }
        return null;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    public static Time toTime(CastDataProvider castDataProvider, TimeZone timeZone, Value value) {
        switch (value.getValueType()) {
            case 0:
                return null;
            default:
                value = value.convertTo(TypeInfo.TYPE_TIME, castDataProvider);
            case 18:
                return new Time(getMillis(castDataProvider, timeZone, 1008673L, ((ValueTime) value).getNanos()));
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    public static Timestamp toTimestamp(CastDataProvider castDataProvider, TimeZone timeZone, Value value) {
        switch (value.getValueType()) {
            case 0:
                return null;
            case 20:
                ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                long timeNanos = valueTimestamp.getTimeNanos();
                Timestamp timestamp = new Timestamp(getMillis(castDataProvider, timeZone, valueTimestamp.getDateValue(), timeNanos));
                timestamp.setNanos((int) (timeNanos % DateTimeUtils.NANOS_PER_SECOND));
                return timestamp;
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
                long timeNanos2 = valueTimestampTimeZone.getTimeNanos();
                Timestamp timestamp2 = new Timestamp(((DateTimeUtils.absoluteDayFromDateValue(valueTimestampTimeZone.getDateValue()) * 86400000) + (timeNanos2 / 1000000)) - (valueTimestampTimeZone.getTimeZoneOffsetSeconds() * 1000));
                timestamp2.setNanos((int) (timeNanos2 % DateTimeUtils.NANOS_PER_SECOND));
                return timestamp2;
            default:
                value = value.convertTo(TypeInfo.TYPE_TIMESTAMP, castDataProvider);
                ValueTimestamp valueTimestamp2 = (ValueTimestamp) value;
                long timeNanos3 = valueTimestamp2.getTimeNanos();
                Timestamp timestamp3 = new Timestamp(getMillis(castDataProvider, timeZone, valueTimestamp2.getDateValue(), timeNanos3));
                timestamp3.setNanos((int) (timeNanos3 % DateTimeUtils.NANOS_PER_SECOND));
                return timestamp3;
        }
    }

    public static long getMillis(CastDataProvider castDataProvider, TimeZone timeZone, long j, long j2) {
        TimeZoneProvider ofId;
        if (timeZone == null) {
            ofId = castDataProvider != null ? castDataProvider.currentTimeZone() : DateTimeUtils.getTimeZone();
        } else {
            ofId = TimeZoneProvider.ofId(timeZone.getID());
        }
        return (ofId.getEpochSecondsFromLocal(j, j2) * 1000) + ((j2 / 1000000) % 1000);
    }

    public static int getTimeZoneOffsetMillis(CastDataProvider castDataProvider, long j) {
        long j2 = j / 1000;
        if (j < 0 && j2 * 1000 != j) {
            j2--;
        }
        return (castDataProvider != null ? castDataProvider.currentTimeZone() : DateTimeUtils.getTimeZone()).getTimeZoneOffsetUTC(j2) * 1000;
    }

    public static Value legacyObjectToValue(CastDataProvider castDataProvider, Object obj) {
        if (obj instanceof Date) {
            return fromDate(castDataProvider, null, (Date) obj);
        }
        if (obj instanceof Time) {
            return fromTime(castDataProvider, null, (Time) obj);
        }
        if (obj instanceof Timestamp) {
            return fromTimestamp(castDataProvider, (TimeZone) null, (Timestamp) obj);
        }
        if (obj instanceof java.util.Date) {
            return fromTimestamp(castDataProvider, ((java.util.Date) obj).getTime(), 0);
        }
        if (obj instanceof Calendar) {
            return timestampFromLocalMillis(((Calendar) obj).getTimeInMillis() + r0.getTimeZone().getOffset(r0), 0);
        }
        return null;
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [java.util.GregorianCalendar, T] */
    public static <T> T valueToLegacyType(Class<T> cls, Value value, CastDataProvider castDataProvider) {
        if (cls == Date.class) {
            return (T) toDate(castDataProvider, null, value);
        }
        if (cls == Time.class) {
            return (T) toTime(castDataProvider, null, value);
        }
        if (cls == Timestamp.class) {
            return (T) toTimestamp(castDataProvider, null, value);
        }
        if (cls == java.util.Date.class) {
            return (T) new java.util.Date(toTimestamp(castDataProvider, null, value).getTime());
        }
        if (cls == Calendar.class) {
            ?? r0 = (T) new GregorianCalendar();
            r0.setGregorianChange(PROLEPTIC_GREGORIAN_CHANGE);
            r0.setTime(toTimestamp(castDataProvider, r0.getTimeZone(), value));
            return r0;
        }
        return null;
    }

    public static TypeInfo legacyClassToType(Class<?> cls) {
        if (Date.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_DATE;
        }
        if (Time.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_TIME;
        }
        if (java.util.Date.class.isAssignableFrom(cls) || Calendar.class.isAssignableFrom(cls)) {
            return TypeInfo.TYPE_TIMESTAMP;
        }
        return null;
    }
}

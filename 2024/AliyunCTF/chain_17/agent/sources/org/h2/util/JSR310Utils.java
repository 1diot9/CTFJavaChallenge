package org.h2.util;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueInterval;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/JSR310Utils.class */
public class JSR310Utils {
    private static final long MIN_DATE_VALUE = -511999999455L;
    private static final long MAX_DATE_VALUE = 511999999903L;
    private static final long MIN_INSTANT_SECOND = -31557014167219200L;
    private static final long MAX_INSTANT_SECOND = 31556889864403199L;

    private JSR310Utils() {
    }

    public static LocalDate valueToLocalDate(Value value, CastDataProvider castDataProvider) {
        long dateValue = value.convertToDate(castDataProvider).getDateValue();
        if (dateValue > MAX_DATE_VALUE) {
            return LocalDate.MAX;
        }
        if (dateValue < MIN_DATE_VALUE) {
            return LocalDate.MIN;
        }
        return LocalDate.of(DateTimeUtils.yearFromDateValue(dateValue), DateTimeUtils.monthFromDateValue(dateValue), DateTimeUtils.dayFromDateValue(dateValue));
    }

    public static LocalTime valueToLocalTime(Value value, CastDataProvider castDataProvider) {
        return LocalTime.ofNanoOfDay(((ValueTime) value.convertTo(TypeInfo.TYPE_TIME, castDataProvider)).getNanos());
    }

    public static LocalDateTime valueToLocalDateTime(Value value, CastDataProvider castDataProvider) {
        ValueTimestamp valueTimestamp = (ValueTimestamp) value.convertTo(TypeInfo.TYPE_TIMESTAMP, castDataProvider);
        return localDateTimeFromDateNanos(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos());
    }

    public static Instant valueToInstant(Value value, CastDataProvider castDataProvider) {
        ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, castDataProvider);
        long timeNanos = valueTimestampTimeZone.getTimeNanos();
        long absoluteDayFromDateValue = ((DateTimeUtils.absoluteDayFromDateValue(valueTimestampTimeZone.getDateValue()) * DateTimeUtils.SECONDS_PER_DAY) + (timeNanos / DateTimeUtils.NANOS_PER_SECOND)) - valueTimestampTimeZone.getTimeZoneOffsetSeconds();
        if (absoluteDayFromDateValue > MAX_INSTANT_SECOND) {
            return Instant.MAX;
        }
        if (absoluteDayFromDateValue < MIN_INSTANT_SECOND) {
            return Instant.MIN;
        }
        return Instant.ofEpochSecond(absoluteDayFromDateValue, timeNanos % DateTimeUtils.NANOS_PER_SECOND);
    }

    public static OffsetDateTime valueToOffsetDateTime(Value value, CastDataProvider castDataProvider) {
        ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, castDataProvider);
        return OffsetDateTime.of(localDateTimeFromDateNanos(valueTimestampTimeZone.getDateValue(), valueTimestampTimeZone.getTimeNanos()), ZoneOffset.ofTotalSeconds(valueTimestampTimeZone.getTimeZoneOffsetSeconds()));
    }

    public static ZonedDateTime valueToZonedDateTime(Value value, CastDataProvider castDataProvider) {
        ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, castDataProvider);
        return ZonedDateTime.of(localDateTimeFromDateNanos(valueTimestampTimeZone.getDateValue(), valueTimestampTimeZone.getTimeNanos()), ZoneOffset.ofTotalSeconds(valueTimestampTimeZone.getTimeZoneOffsetSeconds()));
    }

    public static OffsetTime valueToOffsetTime(Value value, CastDataProvider castDataProvider) {
        ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value.convertTo(TypeInfo.TYPE_TIME_TZ, castDataProvider);
        return OffsetTime.of(LocalTime.ofNanoOfDay(valueTimeTimeZone.getNanos()), ZoneOffset.ofTotalSeconds(valueTimeTimeZone.getTimeZoneOffsetSeconds()));
    }

    public static Period valueToPeriod(Value value) {
        if (!(value instanceof ValueInterval)) {
            value = value.convertTo(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH);
        }
        if (!DataType.isYearMonthIntervalType(value.getValueType())) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, (Throwable) null, value.getString());
        }
        ValueInterval valueInterval = (ValueInterval) value;
        IntervalQualifier qualifier = valueInterval.getQualifier();
        boolean isNegative = valueInterval.isNegative();
        long leading = valueInterval.getLeading();
        long remaining = valueInterval.getRemaining();
        return Period.of(Value.convertToInt(IntervalUtils.yearsFromInterval(qualifier, isNegative, leading, remaining), null), Value.convertToInt(IntervalUtils.monthsFromInterval(qualifier, isNegative, leading, remaining), null), 0);
    }

    public static Duration valueToDuration(Value value) {
        if (!(value instanceof ValueInterval)) {
            value = value.convertTo(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND);
        }
        if (DataType.isYearMonthIntervalType(value.getValueType())) {
            throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, (Throwable) null, value.getString());
        }
        BigInteger[] divideAndRemainder = IntervalUtils.intervalToAbsolute((ValueInterval) value).divideAndRemainder(BigInteger.valueOf(DateTimeUtils.NANOS_PER_SECOND));
        return Duration.ofSeconds(divideAndRemainder[0].longValue(), divideAndRemainder[1].longValue());
    }

    public static ValueDate localDateToValue(LocalDate localDate) {
        return ValueDate.fromDateValue(DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()));
    }

    public static ValueTime localTimeToValue(LocalTime localTime) {
        return ValueTime.fromNanos(localTime.toNanoOfDay());
    }

    public static ValueTimestamp localDateTimeToValue(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        return ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()), localDateTime.toLocalTime().toNanoOfDay());
    }

    public static ValueTimestampTimeZone instantToValue(Instant instant) {
        long epochSecond = instant.getEpochSecond();
        int nano = instant.getNano();
        long j = epochSecond / DateTimeUtils.SECONDS_PER_DAY;
        if (epochSecond < 0 && j * DateTimeUtils.SECONDS_PER_DAY != epochSecond) {
            j--;
        }
        return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(j), ((epochSecond - (j * DateTimeUtils.SECONDS_PER_DAY)) * DateTimeUtils.NANOS_PER_SECOND) + nano, 0);
    }

    public static ValueTimestampTimeZone offsetDateTimeToValue(OffsetDateTime offsetDateTime) {
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()), localDateTime.toLocalTime().toNanoOfDay(), offsetDateTime.getOffset().getTotalSeconds());
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [java.time.LocalDateTime] */
    public static ValueTimestampTimeZone zonedDateTimeToValue(ZonedDateTime zonedDateTime) {
        ?? localDateTime = zonedDateTime.toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()), localDateTime.toLocalTime().toNanoOfDay(), zonedDateTime.getOffset().getTotalSeconds());
    }

    public static ValueTimeTimeZone offsetTimeToValue(OffsetTime offsetTime) {
        return ValueTimeTimeZone.fromNanos(offsetTime.toLocalTime().toNanoOfDay(), offsetTime.getOffset().getTotalSeconds());
    }

    private static LocalDateTime localDateTimeFromDateNanos(long j, long j2) {
        if (j > MAX_DATE_VALUE) {
            return LocalDateTime.MAX;
        }
        if (j < MIN_DATE_VALUE) {
            return LocalDateTime.MIN;
        }
        return LocalDateTime.of(LocalDate.of(DateTimeUtils.yearFromDateValue(j), DateTimeUtils.monthFromDateValue(j), DateTimeUtils.dayFromDateValue(j)), LocalTime.ofNanoOfDay(j2));
    }

    public static ValueInterval periodToValue(Period period) {
        IntervalQualifier intervalQualifier;
        int days = period.getDays();
        if (days != 0) {
            throw DbException.getInvalidValueException("Period.days", Integer.valueOf(days));
        }
        int years = period.getYears();
        int months = period.getMonths();
        boolean z = false;
        long j = 0;
        long j2 = 0;
        if (years == 0) {
            if (months == 0) {
                intervalQualifier = IntervalQualifier.YEAR_TO_MONTH;
            } else {
                intervalQualifier = IntervalQualifier.MONTH;
                j = months;
                if (j < 0) {
                    j = -j;
                    z = true;
                }
            }
        } else if (months == 0) {
            intervalQualifier = IntervalQualifier.YEAR;
            j = years;
            if (j < 0) {
                j = -j;
                z = true;
            }
        } else {
            intervalQualifier = IntervalQualifier.YEAR_TO_MONTH;
            long j3 = (years * 12) + months;
            if (j3 < 0) {
                j3 = -j3;
                z = true;
            }
            j2 = j3 % 12;
            j = j3 / 12;
        }
        return ValueInterval.from(intervalQualifier, z, j, j2);
    }

    public static ValueInterval durationToValue(Duration duration) {
        long seconds = duration.getSeconds();
        int nano = duration.getNano();
        boolean z = seconds < 0;
        long abs = Math.abs(seconds);
        if (z && nano != 0) {
            nano = Constants.MAX_STRING_LENGTH - nano;
            abs--;
        }
        return ValueInterval.from(IntervalQualifier.SECOND, z, abs, nano);
    }
}

package org.h2.util;

import java.time.Instant;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeUtils.class */
public class DateTimeUtils {
    public static final long MILLIS_PER_DAY = 86400000;
    public static final long SECONDS_PER_DAY = 86400;
    public static final long NANOS_PER_SECOND = 1000000000;
    public static final long NANOS_PER_MINUTE = 60000000000L;
    public static final long NANOS_PER_HOUR = 3600000000000L;
    public static final long NANOS_PER_DAY = 86400000000000L;
    public static final int SHIFT_YEAR = 9;
    public static final int SHIFT_MONTH = 5;
    public static final int EPOCH_DATE_VALUE = 1008673;
    public static final long MIN_DATE_VALUE = -511999999967L;
    public static final long MAX_DATE_VALUE = 512000000415L;
    private static final int[] NORMAL_DAYS_PER_MONTH = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    static final int[] FRACTIONAL_SECONDS_TABLE = {Constants.MAX_STRING_LENGTH, 100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
    private static volatile TimeZoneProvider LOCAL;

    private DateTimeUtils() {
    }

    public static void resetCalendar() {
        LOCAL = null;
    }

    public static TimeZoneProvider getTimeZone() {
        TimeZoneProvider timeZoneProvider = LOCAL;
        if (timeZoneProvider == null) {
            TimeZoneProvider timeZoneProvider2 = TimeZoneProvider.getDefault();
            timeZoneProvider = timeZoneProvider2;
            LOCAL = timeZoneProvider2;
        }
        return timeZoneProvider;
    }

    public static ValueTimestampTimeZone currentTimestamp(TimeZoneProvider timeZoneProvider) {
        return currentTimestamp(timeZoneProvider, Instant.now());
    }

    public static ValueTimestampTimeZone currentTimestamp(TimeZoneProvider timeZoneProvider, Instant instant) {
        long epochSecond = instant.getEpochSecond();
        int timeZoneOffsetUTC = timeZoneProvider.getTimeZoneOffsetUTC(epochSecond);
        long j = epochSecond + timeZoneOffsetUTC;
        return ValueTimestampTimeZone.fromDateValueAndNanos(dateValueFromAbsoluteDay(j / SECONDS_PER_DAY), ((j % SECONDS_PER_DAY) * NANOS_PER_SECOND) + instant.getNano(), timeZoneOffsetUTC);
    }

    public static long parseDateValue(String str, int i, int i2) {
        int i3;
        int i4;
        int i5;
        if (str.charAt(i) == '+') {
            i++;
        }
        int indexOf = str.indexOf(45, i + 1);
        if (indexOf > 0) {
            i5 = indexOf + 1;
            i4 = str.indexOf(45, i5);
            if (i4 <= i5) {
                throw new IllegalArgumentException(str);
            }
            i3 = i4 + 1;
        } else {
            int i6 = i2 - 2;
            i3 = i6;
            i4 = i6;
            int i7 = i4 - 2;
            i5 = i7;
            indexOf = i7;
            if (indexOf < i + 3) {
                throw new IllegalArgumentException(str);
            }
        }
        int parseInt = Integer.parseInt(str.substring(i, indexOf));
        int parseUInt31 = StringUtils.parseUInt31(str, i5, i4);
        int parseUInt312 = StringUtils.parseUInt31(str, i3, i2);
        if (!isValidDate(parseInt, parseUInt31, parseUInt312)) {
            throw new IllegalArgumentException(parseInt + "-" + parseUInt31 + "-" + parseUInt312);
        }
        return dateValue(parseInt, parseUInt31, parseUInt312);
    }

    public static long parseTimeNanos(String str, int i, int i2) {
        int i3;
        int indexOf;
        int i4;
        int indexOf2;
        int i5;
        int i6;
        int indexOf3 = str.indexOf(58, i);
        if (indexOf3 > 0) {
            i3 = indexOf3 + 1;
            indexOf = str.indexOf(58, i3);
            if (indexOf >= i3) {
                i4 = indexOf + 1;
                indexOf2 = str.indexOf(46, i4);
            } else {
                indexOf = i2;
                indexOf2 = -1;
                i4 = -1;
            }
        } else {
            int indexOf4 = str.indexOf(46, i);
            if (indexOf4 < 0) {
                int i7 = i + 2;
                i3 = i7;
                indexOf3 = i7;
                indexOf = i3 + 2;
                int i8 = i2 - i;
                if (i8 == 6) {
                    i4 = indexOf;
                    indexOf2 = -1;
                } else if (i8 == 4) {
                    indexOf2 = -1;
                    i4 = -1;
                } else {
                    throw new IllegalArgumentException(str);
                }
            } else if (indexOf4 >= i + 6) {
                if (indexOf4 - i != 6) {
                    throw new IllegalArgumentException(str);
                }
                int i9 = i + 2;
                i3 = i9;
                indexOf3 = i9;
                int i10 = i3 + 2;
                i4 = i10;
                indexOf = i10;
                indexOf2 = indexOf4;
            } else {
                indexOf3 = indexOf4;
                i3 = indexOf3 + 1;
                indexOf = str.indexOf(46, i3);
                if (indexOf <= i3) {
                    throw new IllegalArgumentException(str);
                }
                i4 = indexOf + 1;
                indexOf2 = str.indexOf(46, i4);
            }
        }
        int parseUInt31 = StringUtils.parseUInt31(str, i, indexOf3);
        if (parseUInt31 >= 24) {
            throw new IllegalArgumentException(str);
        }
        int parseUInt312 = StringUtils.parseUInt31(str, i3, indexOf);
        if (i4 > 0) {
            if (indexOf2 < 0) {
                i6 = StringUtils.parseUInt31(str, i4, i2);
                i5 = 0;
            } else {
                i6 = StringUtils.parseUInt31(str, i4, indexOf2);
                i5 = parseNanos(str, indexOf2 + 1, i2);
            }
        } else {
            i5 = 0;
            i6 = 0;
        }
        if (parseUInt312 >= 60 || i6 >= 60) {
            throw new IllegalArgumentException(str);
        }
        return (((((parseUInt31 * 60) + parseUInt312) * 60) + i6) * NANOS_PER_SECOND) + i5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int parseNanos(String str, int i, int i2) {
        if (i >= i2) {
            throw new IllegalArgumentException(str);
        }
        int i3 = 0;
        int i4 = 100000000;
        do {
            char charAt = str.charAt(i);
            if (charAt < '0' || charAt > '9') {
                throw new IllegalArgumentException(str);
            }
            i3 += i4 * (charAt - '0');
            i4 /= 10;
            i++;
        } while (i < i2);
        return i3;
    }

    public static Value parseTimestamp(String str, CastDataProvider castDataProvider, boolean z) {
        int i;
        int length;
        long parseTimeNanos;
        int i2;
        int indexOf = str.indexOf(32);
        if (indexOf < 0) {
            indexOf = str.indexOf(84);
            if (indexOf < 0 && castDataProvider != null && castDataProvider.getMode().allowDB2TimestampFormat) {
                indexOf = str.indexOf(45, str.indexOf(45, str.indexOf(45) + 1) + 1);
            }
        }
        if (indexOf < 0) {
            indexOf = str.length();
            i = -1;
        } else {
            i = indexOf + 1;
        }
        long parseDateValue = parseDateValue(str, 0, indexOf);
        TimeZoneProvider timeZoneProvider = null;
        if (i < 0) {
            parseTimeNanos = 0;
        } else {
            int i3 = indexOf + 1;
            if (str.endsWith("Z")) {
                timeZoneProvider = TimeZoneProvider.UTC;
                length = str.length() - 1;
            } else {
                int indexOf2 = str.indexOf(43, i3);
                if (indexOf2 < 0) {
                    indexOf2 = str.indexOf(45, i3);
                }
                if (indexOf2 >= 0) {
                    int indexOf3 = str.indexOf(91, indexOf2 + 1);
                    if (indexOf3 < 0) {
                        indexOf3 = str.length();
                    }
                    timeZoneProvider = TimeZoneProvider.ofId(str.substring(indexOf2, indexOf3));
                    if (str.charAt(indexOf2 - 1) == ' ') {
                        indexOf2--;
                    }
                    length = indexOf2;
                } else {
                    int indexOf4 = str.indexOf(32, i3);
                    if (indexOf4 > 0) {
                        timeZoneProvider = TimeZoneProvider.ofId(str.substring(indexOf4 + 1));
                        length = indexOf4;
                    } else {
                        length = str.length();
                    }
                }
            }
            parseTimeNanos = parseTimeNanos(str, i3, length);
        }
        if (z) {
            if (timeZoneProvider == null) {
                timeZoneProvider = castDataProvider != null ? castDataProvider.currentTimeZone() : getTimeZone();
            }
            if (timeZoneProvider != TimeZoneProvider.UTC) {
                i2 = timeZoneProvider.getTimeZoneOffsetUTC(timeZoneProvider.getEpochSecondsFromLocal(parseDateValue, parseTimeNanos));
            } else {
                i2 = 0;
            }
            return ValueTimestampTimeZone.fromDateValueAndNanos(parseDateValue, parseTimeNanos, i2);
        }
        if (timeZoneProvider != null) {
            long epochSecondsFromLocal = timeZoneProvider.getEpochSecondsFromLocal(parseDateValue, parseTimeNanos) + (castDataProvider != null ? castDataProvider.currentTimeZone() : getTimeZone()).getTimeZoneOffsetUTC(r0);
            parseDateValue = dateValueFromLocalSeconds(epochSecondsFromLocal);
            parseTimeNanos = (parseTimeNanos % NANOS_PER_SECOND) + nanosFromLocalSeconds(epochSecondsFromLocal);
        }
        return ValueTimestamp.fromDateValueAndNanos(parseDateValue, parseTimeNanos);
    }

    public static Value parseTime(String str, CastDataProvider castDataProvider, boolean z) {
        int length;
        int timeZoneOffsetSeconds;
        TimeZoneProvider timeZoneProvider = null;
        if (str.endsWith("Z")) {
            timeZoneProvider = TimeZoneProvider.UTC;
            length = str.length() - 1;
        } else {
            int indexOf = str.indexOf(43, 1);
            if (indexOf < 0) {
                indexOf = str.indexOf(45, 1);
            }
            if (indexOf >= 0) {
                timeZoneProvider = TimeZoneProvider.ofId(str.substring(indexOf));
                if (str.charAt(indexOf - 1) == ' ') {
                    indexOf--;
                }
                length = indexOf;
            } else {
                int indexOf2 = str.indexOf(32, 1);
                if (indexOf2 > 0) {
                    timeZoneProvider = TimeZoneProvider.ofId(str.substring(indexOf2 + 1));
                    length = indexOf2;
                } else {
                    length = str.length();
                }
            }
            if (timeZoneProvider != null && !timeZoneProvider.hasFixedOffset()) {
                throw DbException.get(ErrorCode.INVALID_DATETIME_CONSTANT_2, "TIME WITH TIME ZONE", str);
            }
        }
        long parseTimeNanos = parseTimeNanos(str, 0, length);
        if (z) {
            if (timeZoneProvider != null) {
                timeZoneOffsetSeconds = timeZoneProvider.getTimeZoneOffsetUTC(0L);
            } else {
                timeZoneOffsetSeconds = (castDataProvider != null ? castDataProvider.currentTimestamp() : currentTimestamp(getTimeZone())).getTimeZoneOffsetSeconds();
            }
            return ValueTimeTimeZone.fromNanos(parseTimeNanos, timeZoneOffsetSeconds);
        }
        if (timeZoneProvider != null) {
            parseTimeNanos = normalizeNanosOfDay(parseTimeNanos + (((castDataProvider != null ? castDataProvider.currentTimestamp() : currentTimestamp(getTimeZone())).getTimeZoneOffsetSeconds() - timeZoneProvider.getTimeZoneOffsetUTC(0L)) * NANOS_PER_SECOND));
        }
        return ValueTime.fromNanos(parseTimeNanos);
    }

    public static long getEpochSeconds(long j, long j2, int i) {
        return ((absoluteDayFromDateValue(j) * SECONDS_PER_DAY) + (j2 / NANOS_PER_SECOND)) - i;
    }

    public static long[] dateAndTimeFromValue(Value value, CastDataProvider castDataProvider) {
        long j = 1008673;
        long j2 = 0;
        if (value instanceof ValueTimestamp) {
            ValueTimestamp valueTimestamp = (ValueTimestamp) value;
            j = valueTimestamp.getDateValue();
            j2 = valueTimestamp.getTimeNanos();
        } else if (value instanceof ValueDate) {
            j = ((ValueDate) value).getDateValue();
        } else if (value instanceof ValueTime) {
            j2 = ((ValueTime) value).getNanos();
        } else if (value instanceof ValueTimestampTimeZone) {
            ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
            j = valueTimestampTimeZone.getDateValue();
            j2 = valueTimestampTimeZone.getTimeNanos();
        } else if (value instanceof ValueTimeTimeZone) {
            j2 = ((ValueTimeTimeZone) value).getNanos();
        } else {
            ValueTimestamp valueTimestamp2 = (ValueTimestamp) value.convertTo(TypeInfo.TYPE_TIMESTAMP, castDataProvider);
            j = valueTimestamp2.getDateValue();
            j2 = valueTimestamp2.getTimeNanos();
        }
        return new long[]{j, j2};
    }

    public static Value dateTimeToValue(Value value, long j, long j2) {
        switch (value.getValueType()) {
            case 17:
                return ValueDate.fromDateValue(j);
            case 18:
                return ValueTime.fromNanos(j2);
            case 19:
                return ValueTimeTimeZone.fromNanos(j2, ((ValueTimeTimeZone) value).getTimeZoneOffsetSeconds());
            case 20:
            default:
                return ValueTimestamp.fromDateValueAndNanos(j, j2);
            case 21:
                return ValueTimestampTimeZone.fromDateValueAndNanos(j, j2, ((ValueTimestampTimeZone) value).getTimeZoneOffsetSeconds());
        }
    }

    public static int getDayOfWeek(long j, int i) {
        return getDayOfWeekFromAbsolute(absoluteDayFromDateValue(j), i);
    }

    public static int getDayOfWeekFromAbsolute(long j, int i) {
        return j >= 0 ? ((int) (((j - i) + 11) % 7)) + 1 : ((int) (((j - i) - 2) % 7)) + 7;
    }

    public static int getDayOfYear(long j) {
        int monthFromDateValue = monthFromDateValue(j);
        int dayFromDateValue = (((367 * monthFromDateValue) - 362) / 12) + dayFromDateValue(j);
        if (monthFromDateValue > 2) {
            dayFromDateValue--;
            long yearFromDateValue = yearFromDateValue(j);
            if ((yearFromDateValue & 3) != 0 || (yearFromDateValue % 100 == 0 && yearFromDateValue % 400 != 0)) {
                dayFromDateValue--;
            }
        }
        return dayFromDateValue;
    }

    public static int getIsoDayOfWeek(long j) {
        return getDayOfWeek(j, 1);
    }

    public static int getIsoWeekOfYear(long j) {
        return getWeekOfYear(j, 1, 4);
    }

    public static int getIsoWeekYear(long j) {
        return getWeekYear(j, 1, 4);
    }

    public static int getSundayDayOfWeek(long j) {
        return getDayOfWeek(j, 0);
    }

    public static int getWeekOfYear(long j, int i, int i2) {
        long absoluteDayFromDateValue = absoluteDayFromDateValue(j);
        int yearFromDateValue = yearFromDateValue(j);
        long weekYearAbsoluteStart = getWeekYearAbsoluteStart(yearFromDateValue, i, i2);
        if (absoluteDayFromDateValue - weekYearAbsoluteStart < 0) {
            weekYearAbsoluteStart = getWeekYearAbsoluteStart(yearFromDateValue - 1, i, i2);
        } else if (monthFromDateValue(j) == 12 && 24 + i2 < dayFromDateValue(j) && absoluteDayFromDateValue >= getWeekYearAbsoluteStart(yearFromDateValue + 1, i, i2)) {
            return 1;
        }
        return ((int) ((absoluteDayFromDateValue - weekYearAbsoluteStart) / 7)) + 1;
    }

    public static long getWeekYearAbsoluteStart(int i, int i2, int i3) {
        long absoluteDayFromYear = absoluteDayFromYear(i);
        int dayOfWeekFromAbsolute = 8 - getDayOfWeekFromAbsolute(absoluteDayFromYear, i2);
        long j = absoluteDayFromYear + dayOfWeekFromAbsolute;
        if (dayOfWeekFromAbsolute >= i3) {
            j -= 7;
        }
        return j;
    }

    public static int getWeekYear(long j, int i, int i2) {
        long absoluteDayFromDateValue = absoluteDayFromDateValue(j);
        int yearFromDateValue = yearFromDateValue(j);
        if (absoluteDayFromDateValue < getWeekYearAbsoluteStart(yearFromDateValue, i, i2)) {
            return yearFromDateValue - 1;
        }
        if (monthFromDateValue(j) == 12 && 24 + i2 < dayFromDateValue(j) && absoluteDayFromDateValue >= getWeekYearAbsoluteStart(yearFromDateValue + 1, i, i2)) {
            return yearFromDateValue + 1;
        }
        return yearFromDateValue;
    }

    public static int getDaysInMonth(int i, int i2) {
        if (i2 != 2) {
            return NORMAL_DAYS_PER_MONTH[i2];
        }
        return isLeapYear(i) ? 29 : 28;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isLeapYear(int i) {
        return (i & 3) == 0 && (i % 100 != 0 || i % 400 == 0);
    }

    public static boolean isValidDate(int i, int i2, int i3) {
        return i2 >= 1 && i2 <= 12 && i3 >= 1 && i3 <= getDaysInMonth(i, i2);
    }

    public static int yearFromDateValue(long j) {
        return (int) (j >>> 9);
    }

    public static int monthFromDateValue(long j) {
        return ((int) (j >>> 5)) & 15;
    }

    public static int dayFromDateValue(long j) {
        return (int) (j & 31);
    }

    public static long dateValue(long j, int i, int i2) {
        return (j << 9) | (i << 5) | i2;
    }

    public static long dateValueFromDenormalizedDate(long j, long j2, int i) {
        long j3 = j2 - 1;
        long j4 = j3 / 12;
        if (j3 < 0 && j4 * 12 != j3) {
            j4--;
        }
        int i2 = (int) (j + j4);
        int i3 = (int) (j2 - (j4 * 12));
        if (i < 1) {
            i = 1;
        } else {
            int daysInMonth = getDaysInMonth(i2, i3);
            if (i > daysInMonth) {
                i = daysInMonth;
            }
        }
        return dateValue(i2, i3, i);
    }

    public static long dateValueFromLocalSeconds(long j) {
        long j2 = j / SECONDS_PER_DAY;
        if (j < 0 && j2 * SECONDS_PER_DAY != j) {
            j2--;
        }
        return dateValueFromAbsoluteDay(j2);
    }

    public static long nanosFromLocalSeconds(long j) {
        long j2 = j % SECONDS_PER_DAY;
        if (j2 < 0) {
            j2 += SECONDS_PER_DAY;
        }
        return j2 * NANOS_PER_SECOND;
    }

    public static long normalizeNanosOfDay(long j) {
        long j2 = j % NANOS_PER_DAY;
        if (j2 < 0) {
            j2 += NANOS_PER_DAY;
        }
        return j2;
    }

    public static long absoluteDayFromYear(long j) {
        long j2;
        long j3 = (365 * j) - 719528;
        if (j >= 0) {
            j2 = j3 + (((j + 3) / 4) - ((j + 99) / 100)) + ((j + 399) / 400);
        } else {
            j2 = j3 - (((j / (-4)) - (j / (-100))) + (j / (-400)));
        }
        return j2;
    }

    public static long absoluteDayFromDateValue(long j) {
        return absoluteDay(yearFromDateValue(j), monthFromDateValue(j), dayFromDateValue(j));
    }

    static long absoluteDay(long j, int i, int i2) {
        long absoluteDayFromYear = ((absoluteDayFromYear(j) + (((367 * i) - 362) / 12)) + i2) - 1;
        if (i > 2) {
            absoluteDayFromYear--;
            if ((j & 3) != 0 || (j % 100 == 0 && j % 400 != 0)) {
                absoluteDayFromYear--;
            }
        }
        return absoluteDayFromYear;
    }

    public static long dateValueFromAbsoluteDay(long j) {
        long j2 = j + 719468;
        long j3 = 0;
        if (j2 < 0) {
            long j4 = ((j2 + 1) / 146097) - 1;
            j2 -= j4 * 146097;
            j3 = j4 * 400;
        }
        long j5 = ((400 * j2) + 591) / 146097;
        int i = (int) (j2 - ((((365 * j5) + (j5 / 4)) - (j5 / 100)) + (j5 / 400)));
        if (i < 0) {
            j5--;
            i = (int) (j2 - ((((365 * j5) + (j5 / 4)) - (j5 / 100)) + (j5 / 400)));
        }
        long j6 = j5 + j3;
        int i2 = ((i * 5) + 2) / 153;
        int i3 = i - ((((i2 * 306) + 5) / 10) - 1);
        if (i2 >= 10) {
            j6++;
            i2 -= 12;
        }
        return dateValue(j6, i2 + 3, i3);
    }

    public static long incrementDateValue(long j) {
        int i;
        int dayFromDateValue = dayFromDateValue(j);
        if (dayFromDateValue < 28) {
            return j + 1;
        }
        int yearFromDateValue = yearFromDateValue(j);
        int monthFromDateValue = monthFromDateValue(j);
        if (dayFromDateValue < getDaysInMonth(yearFromDateValue, monthFromDateValue)) {
            return j + 1;
        }
        if (monthFromDateValue < 12) {
            i = monthFromDateValue + 1;
        } else {
            i = 1;
            yearFromDateValue++;
        }
        return dateValue(yearFromDateValue, i, 1);
    }

    public static long decrementDateValue(long j) {
        int i;
        if (dayFromDateValue(j) > 1) {
            return j - 1;
        }
        int yearFromDateValue = yearFromDateValue(j);
        int monthFromDateValue = monthFromDateValue(j);
        if (monthFromDateValue > 1) {
            i = monthFromDateValue - 1;
        } else {
            i = 12;
            yearFromDateValue--;
        }
        return dateValue(yearFromDateValue, i, getDaysInMonth(yearFromDateValue, i));
    }

    public static StringBuilder appendDate(StringBuilder sb, long j) {
        int yearFromDateValue = yearFromDateValue(j);
        if (yearFromDateValue < 1000 && yearFromDateValue > -1000) {
            if (yearFromDateValue < 0) {
                sb.append('-');
                yearFromDateValue = -yearFromDateValue;
            }
            StringUtils.appendZeroPadded(sb, 4, yearFromDateValue);
        } else {
            sb.append(yearFromDateValue);
        }
        StringUtils.appendTwoDigits(sb.append('-'), monthFromDateValue(j)).append('-');
        return StringUtils.appendTwoDigits(sb, dayFromDateValue(j));
    }

    public static StringBuilder appendTime(StringBuilder sb, long j) {
        if (j < 0) {
            sb.append('-');
            j = -j;
        }
        long j2 = (-j) / (-1000000000);
        long j3 = j - (j2 * NANOS_PER_SECOND);
        int i = (int) (j2 / 60);
        long j4 = j2 - (i * 60);
        int i2 = i / 60;
        StringUtils.appendTwoDigits(sb, i2).append(':');
        StringUtils.appendTwoDigits(sb, i - (i2 * 60)).append(':');
        StringUtils.appendTwoDigits(sb, (int) j4);
        return appendNanos(sb, (int) j3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StringBuilder appendNanos(StringBuilder sb, int i) {
        if (i > 0) {
            sb.append('.');
            for (int i2 = 1; i < FRACTIONAL_SECONDS_TABLE[i2]; i2++) {
                sb.append('0');
            }
            if (i % 1000 == 0) {
                i /= 1000;
                if (i % 1000 == 0) {
                    i /= 1000;
                }
            }
            if (i % 10 == 0) {
                i /= 10;
                if (i % 10 == 0) {
                    i /= 10;
                }
            }
            sb.append(i);
        }
        return sb;
    }

    public static StringBuilder appendTimeZone(StringBuilder sb, int i) {
        if (i < 0) {
            sb.append('-');
            i = -i;
        } else {
            sb.append('+');
        }
        int i2 = i / 3600;
        StringUtils.appendTwoDigits(sb, i2);
        int i3 = i - (i2 * 3600);
        if (i3 != 0) {
            int i4 = i3 / 60;
            StringUtils.appendTwoDigits(sb.append(':'), i4);
            int i5 = i3 - (i4 * 60);
            if (i5 != 0) {
                StringUtils.appendTwoDigits(sb.append(':'), i5);
            }
        }
        return sb;
    }

    public static String timeZoneNameFromOffsetSeconds(int i) {
        if (i == 0) {
            return "UTC";
        }
        StringBuilder sb = new StringBuilder(12);
        sb.append("GMT");
        if (i < 0) {
            sb.append('-');
            i = -i;
        } else {
            sb.append('+');
        }
        StringUtils.appendTwoDigits(sb, i / 3600).append(':');
        int i2 = i % 3600;
        StringUtils.appendTwoDigits(sb, i2 / 60);
        int i3 = i2 % 60;
        if (i3 != 0) {
            sb.append(':');
            StringUtils.appendTwoDigits(sb, i3);
        }
        return sb.toString();
    }

    public static long convertScale(long j, int i, long j2) {
        if (i >= 9) {
            return j;
        }
        int i2 = FRACTIONAL_SECONDS_TABLE[i];
        long j3 = j % i2;
        if (j3 >= (i2 >>> 1)) {
            j += i2;
        }
        long j4 = j - j3;
        if (j4 >= j2) {
            j4 = j2 - i2;
        }
        return j4;
    }

    public static ValueTimestampTimeZone timestampTimeZoneAtOffset(long j, long j2, int i, int i2) {
        long j3 = j2 + ((i2 - i) * NANOS_PER_SECOND);
        if (j3 < 0) {
            j3 += NANOS_PER_DAY;
            j = decrementDateValue(j);
            if (j3 < 0) {
                j3 += NANOS_PER_DAY;
                j = decrementDateValue(j);
            }
        } else if (j3 >= NANOS_PER_DAY) {
            j3 -= NANOS_PER_DAY;
            j = incrementDateValue(j);
            if (j3 >= NANOS_PER_DAY) {
                j3 -= NANOS_PER_DAY;
                j = incrementDateValue(j);
            }
        }
        return ValueTimestampTimeZone.fromDateValueAndNanos(j, j3, i2);
    }
}

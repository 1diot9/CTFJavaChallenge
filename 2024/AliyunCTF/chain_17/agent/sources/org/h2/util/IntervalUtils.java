package org.h2.util;

import java.math.BigInteger;
import org.h2.api.ErrorCode;
import org.h2.api.IntervalQualifier;
import org.h2.message.DbException;
import org.h2.value.ValueInterval;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/IntervalUtils.class */
public class IntervalUtils {
    private static final BigInteger NANOS_PER_SECOND_BI = BigInteger.valueOf(DateTimeUtils.NANOS_PER_SECOND);
    private static final BigInteger NANOS_PER_MINUTE_BI = BigInteger.valueOf(DateTimeUtils.NANOS_PER_MINUTE);
    private static final BigInteger NANOS_PER_HOUR_BI = BigInteger.valueOf(DateTimeUtils.NANOS_PER_HOUR);
    public static final BigInteger NANOS_PER_DAY_BI = BigInteger.valueOf(DateTimeUtils.NANOS_PER_DAY);
    private static final BigInteger MONTHS_PER_YEAR_BI = BigInteger.valueOf(12);
    private static final BigInteger HOURS_PER_DAY_BI = BigInteger.valueOf(24);
    private static final BigInteger MINUTES_PER_DAY_BI = BigInteger.valueOf(1440);
    private static final BigInteger MINUTES_PER_HOUR_BI = BigInteger.valueOf(60);
    private static final BigInteger LEADING_MIN = BigInteger.valueOf(-999999999999999999L);
    private static final BigInteger LEADING_MAX = BigInteger.valueOf(999999999999999999L);

    private IntervalUtils() {
    }

    public static ValueInterval parseFormattedInterval(IntervalQualifier intervalQualifier, String str) {
        int skipWS = skipWS(str, 0);
        if (!str.regionMatches(true, skipWS, "INTERVAL", 0, 8)) {
            return parseInterval(intervalQualifier, false, str);
        }
        int skipWS2 = skipWS(str, skipWS + 8);
        boolean z = false;
        char charAt = str.charAt(skipWS2);
        if (charAt == '-') {
            z = true;
            skipWS2 = skipWS(str, skipWS2 + 1);
            charAt = str.charAt(skipWS2);
        } else if (charAt == '+') {
            skipWS2 = skipWS(str, skipWS2 + 1);
            charAt = str.charAt(skipWS2);
        }
        if (charAt != '\'') {
            throw new IllegalArgumentException(str);
        }
        int i = skipWS2 + 1;
        int length = str.length();
        while (i != length) {
            if (str.charAt(i) != '\'') {
                i++;
            } else {
                String substring = str.substring(i, i);
                int skipWS3 = skipWS(str, i + 1);
                if (str.regionMatches(true, skipWS3, "YEAR", 0, 4)) {
                    skipWS3 += 4;
                    int skipWSEnd = skipWSEnd(str, skipWS3);
                    if (skipWSEnd == length) {
                        return parseInterval(IntervalQualifier.YEAR, z, substring);
                    }
                    if (skipWSEnd > skipWS3 && str.regionMatches(true, skipWSEnd, "TO", 0, 2)) {
                        int i2 = skipWSEnd + 2;
                        skipWS3 = skipWS(str, i2);
                        if (skipWS3 > i2 && str.regionMatches(true, skipWS3, "MONTH", 0, 5) && skipWSEnd(str, skipWS3 + 5) == length) {
                            return parseInterval(IntervalQualifier.YEAR_TO_MONTH, z, substring);
                        }
                    }
                } else if (str.regionMatches(true, skipWS3, "MONTH", 0, 5) && skipWSEnd(str, skipWS3 + 5) == length) {
                    return parseInterval(IntervalQualifier.MONTH, z, substring);
                }
                if (str.regionMatches(true, skipWS3, "DAY", 0, 3)) {
                    skipWS3 += 3;
                    int skipWSEnd2 = skipWSEnd(str, skipWS3);
                    if (skipWSEnd2 == length) {
                        return parseInterval(IntervalQualifier.DAY, z, substring);
                    }
                    if (skipWSEnd2 > skipWS3 && str.regionMatches(true, skipWSEnd2, "TO", 0, 2)) {
                        int i3 = skipWSEnd2 + 2;
                        skipWS3 = skipWS(str, i3);
                        if (skipWS3 > i3) {
                            if (str.regionMatches(true, skipWS3, "HOUR", 0, 4)) {
                                if (skipWSEnd(str, skipWS3 + 4) == length) {
                                    return parseInterval(IntervalQualifier.DAY_TO_HOUR, z, substring);
                                }
                            } else if (str.regionMatches(true, skipWS3, "MINUTE", 0, 6)) {
                                if (skipWSEnd(str, skipWS3 + 6) == length) {
                                    return parseInterval(IntervalQualifier.DAY_TO_MINUTE, z, substring);
                                }
                            } else if (str.regionMatches(true, skipWS3, "SECOND", 0, 6) && skipWSEnd(str, skipWS3 + 6) == length) {
                                return parseInterval(IntervalQualifier.DAY_TO_SECOND, z, substring);
                            }
                        }
                    }
                }
                if (str.regionMatches(true, skipWS3, "HOUR", 0, 4)) {
                    skipWS3 += 4;
                    int skipWSEnd3 = skipWSEnd(str, skipWS3);
                    if (skipWSEnd3 == length) {
                        return parseInterval(IntervalQualifier.HOUR, z, substring);
                    }
                    if (skipWSEnd3 > skipWS3 && str.regionMatches(true, skipWSEnd3, "TO", 0, 2)) {
                        int i4 = skipWSEnd3 + 2;
                        skipWS3 = skipWS(str, i4);
                        if (skipWS3 > i4) {
                            if (str.regionMatches(true, skipWS3, "MINUTE", 0, 6)) {
                                if (skipWSEnd(str, skipWS3 + 6) == length) {
                                    return parseInterval(IntervalQualifier.HOUR_TO_MINUTE, z, substring);
                                }
                            } else if (str.regionMatches(true, skipWS3, "SECOND", 0, 6) && skipWSEnd(str, skipWS3 + 6) == length) {
                                return parseInterval(IntervalQualifier.HOUR_TO_SECOND, z, substring);
                            }
                        }
                    }
                }
                if (str.regionMatches(true, skipWS3, "MINUTE", 0, 6)) {
                    skipWS3 += 6;
                    int skipWSEnd4 = skipWSEnd(str, skipWS3);
                    if (skipWSEnd4 == length) {
                        return parseInterval(IntervalQualifier.MINUTE, z, substring);
                    }
                    if (skipWSEnd4 > skipWS3 && str.regionMatches(true, skipWSEnd4, "TO", 0, 2)) {
                        int i5 = skipWSEnd4 + 2;
                        skipWS3 = skipWS(str, i5);
                        if (skipWS3 > i5 && str.regionMatches(true, skipWS3, "SECOND", 0, 6) && skipWSEnd(str, skipWS3 + 6) == length) {
                            return parseInterval(IntervalQualifier.MINUTE_TO_SECOND, z, substring);
                        }
                    }
                }
                if (str.regionMatches(true, skipWS3, "SECOND", 0, 6) && skipWSEnd(str, skipWS3 + 6) == length) {
                    return parseInterval(IntervalQualifier.SECOND, z, substring);
                }
                throw new IllegalArgumentException(str);
            }
        }
        throw new IllegalArgumentException(str);
    }

    private static int skipWS(String str, int i) {
        int length = str.length();
        while (i != length) {
            if (Character.isWhitespace(str.charAt(i))) {
                i++;
            } else {
                return i;
            }
        }
        throw new IllegalArgumentException(str);
    }

    private static int skipWSEnd(String str, int i) {
        int length = str.length();
        while (i != length) {
            if (Character.isWhitespace(str.charAt(i))) {
                i++;
            } else {
                return i;
            }
        }
        return i;
    }

    public static ValueInterval parseInterval(IntervalQualifier intervalQualifier, boolean z, String str) {
        long parseIntervalLeading;
        long parseIntervalRemainingSeconds;
        switch (intervalQualifier) {
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
                parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
                parseIntervalRemainingSeconds = 0;
                break;
            case SECOND:
                int indexOf = str.indexOf(46);
                if (indexOf < 0) {
                    parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
                    parseIntervalRemainingSeconds = 0;
                    break;
                } else {
                    parseIntervalLeading = parseIntervalLeading(str, 0, indexOf, z);
                    parseIntervalRemainingSeconds = DateTimeUtils.parseNanos(str, indexOf + 1, str.length());
                    break;
                }
            case YEAR_TO_MONTH:
                return parseInterval2(intervalQualifier, str, '-', 11, z);
            case DAY_TO_HOUR:
                return parseInterval2(intervalQualifier, str, ' ', 23, z);
            case DAY_TO_MINUTE:
                int indexOf2 = str.indexOf(32);
                if (indexOf2 < 0) {
                    parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
                    parseIntervalRemainingSeconds = 0;
                    break;
                } else {
                    parseIntervalLeading = parseIntervalLeading(str, 0, indexOf2, z);
                    int indexOf3 = str.indexOf(58, indexOf2 + 1);
                    if (indexOf3 < 0) {
                        parseIntervalRemainingSeconds = parseIntervalRemaining(str, indexOf2 + 1, str.length(), 23) * 60;
                        break;
                    } else {
                        parseIntervalRemainingSeconds = (parseIntervalRemaining(str, indexOf2 + 1, indexOf3, 23) * 60) + parseIntervalRemaining(str, indexOf3 + 1, str.length(), 59);
                        break;
                    }
                }
            case DAY_TO_SECOND:
                int indexOf4 = str.indexOf(32);
                if (indexOf4 < 0) {
                    parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
                    parseIntervalRemainingSeconds = 0;
                    break;
                } else {
                    parseIntervalLeading = parseIntervalLeading(str, 0, indexOf4, z);
                    int indexOf5 = str.indexOf(58, indexOf4 + 1);
                    if (indexOf5 < 0) {
                        parseIntervalRemainingSeconds = parseIntervalRemaining(str, indexOf4 + 1, str.length(), 23) * DateTimeUtils.NANOS_PER_HOUR;
                        break;
                    } else {
                        int indexOf6 = str.indexOf(58, indexOf5 + 1);
                        if (indexOf6 < 0) {
                            parseIntervalRemainingSeconds = (parseIntervalRemaining(str, indexOf4 + 1, indexOf5, 23) * DateTimeUtils.NANOS_PER_HOUR) + (parseIntervalRemaining(str, indexOf5 + 1, str.length(), 59) * DateTimeUtils.NANOS_PER_MINUTE);
                            break;
                        } else {
                            parseIntervalRemainingSeconds = (parseIntervalRemaining(str, indexOf4 + 1, indexOf5, 23) * DateTimeUtils.NANOS_PER_HOUR) + (parseIntervalRemaining(str, indexOf5 + 1, indexOf6, 59) * DateTimeUtils.NANOS_PER_MINUTE) + parseIntervalRemainingSeconds(str, indexOf6 + 1);
                            break;
                        }
                    }
                }
            case HOUR_TO_MINUTE:
                return parseInterval2(intervalQualifier, str, ':', 59, z);
            case HOUR_TO_SECOND:
                int indexOf7 = str.indexOf(58);
                if (indexOf7 < 0) {
                    parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
                    parseIntervalRemainingSeconds = 0;
                    break;
                } else {
                    parseIntervalLeading = parseIntervalLeading(str, 0, indexOf7, z);
                    int indexOf8 = str.indexOf(58, indexOf7 + 1);
                    if (indexOf8 < 0) {
                        parseIntervalRemainingSeconds = parseIntervalRemaining(str, indexOf7 + 1, str.length(), 59) * DateTimeUtils.NANOS_PER_MINUTE;
                        break;
                    } else {
                        parseIntervalRemainingSeconds = (parseIntervalRemaining(str, indexOf7 + 1, indexOf8, 59) * DateTimeUtils.NANOS_PER_MINUTE) + parseIntervalRemainingSeconds(str, indexOf8 + 1);
                        break;
                    }
                }
            case MINUTE_TO_SECOND:
                int indexOf9 = str.indexOf(58);
                if (indexOf9 < 0) {
                    parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
                    parseIntervalRemainingSeconds = 0;
                    break;
                } else {
                    parseIntervalLeading = parseIntervalLeading(str, 0, indexOf9, z);
                    parseIntervalRemainingSeconds = parseIntervalRemainingSeconds(str, indexOf9 + 1);
                    break;
                }
            default:
                throw new IllegalArgumentException();
        }
        boolean z2 = parseIntervalLeading < 0;
        if (z2) {
            if (parseIntervalLeading != Long.MIN_VALUE) {
                parseIntervalLeading = -parseIntervalLeading;
            } else {
                parseIntervalLeading = 0;
            }
        }
        return ValueInterval.from(intervalQualifier, z2, parseIntervalLeading, parseIntervalRemainingSeconds);
    }

    private static ValueInterval parseInterval2(IntervalQualifier intervalQualifier, String str, char c, int i, boolean z) {
        long parseIntervalLeading;
        long parseIntervalRemaining;
        int indexOf = str.indexOf(c, 1);
        if (indexOf < 0) {
            parseIntervalLeading = parseIntervalLeading(str, 0, str.length(), z);
            parseIntervalRemaining = 0;
        } else {
            parseIntervalLeading = parseIntervalLeading(str, 0, indexOf, z);
            parseIntervalRemaining = parseIntervalRemaining(str, indexOf + 1, str.length(), i);
        }
        boolean z2 = parseIntervalLeading < 0;
        if (z2) {
            if (parseIntervalLeading != Long.MIN_VALUE) {
                parseIntervalLeading = -parseIntervalLeading;
            } else {
                parseIntervalLeading = 0;
            }
        }
        return ValueInterval.from(intervalQualifier, z2, parseIntervalLeading, parseIntervalRemaining);
    }

    private static long parseIntervalLeading(String str, int i, int i2, boolean z) {
        long parseLong = Long.parseLong(str.substring(i, i2));
        if (parseLong == 0) {
            return z ^ (str.charAt(i) == '-') ? Long.MIN_VALUE : 0L;
        }
        return z ? -parseLong : parseLong;
    }

    private static long parseIntervalRemaining(String str, int i, int i2, int i3) {
        int parseUInt31 = StringUtils.parseUInt31(str, i, i2);
        if (parseUInt31 > i3) {
            throw new IllegalArgumentException(str);
        }
        return parseUInt31;
    }

    private static long parseIntervalRemainingSeconds(String str, int i) {
        int parseUInt31;
        int parseNanos;
        int indexOf = str.indexOf(46, i + 1);
        if (indexOf < 0) {
            parseUInt31 = StringUtils.parseUInt31(str, i, str.length());
            parseNanos = 0;
        } else {
            parseUInt31 = StringUtils.parseUInt31(str, i, indexOf);
            parseNanos = DateTimeUtils.parseNanos(str, indexOf + 1, str.length());
        }
        if (parseUInt31 > 59) {
            throw new IllegalArgumentException(str);
        }
        return (parseUInt31 * DateTimeUtils.NANOS_PER_SECOND) + parseNanos;
    }

    public static StringBuilder appendInterval(StringBuilder sb, IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        sb.append("INTERVAL '");
        if (z) {
            sb.append('-');
        }
        switch (intervalQualifier) {
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
                sb.append(j);
                break;
            case SECOND:
                DateTimeUtils.appendNanos(sb.append(j), (int) j2);
                break;
            case YEAR_TO_MONTH:
                sb.append(j).append('-').append(j2);
                break;
            case DAY_TO_HOUR:
                sb.append(j).append(' ');
                StringUtils.appendTwoDigits(sb, (int) j2);
                break;
            case DAY_TO_MINUTE:
                sb.append(j).append(' ');
                int i = (int) j2;
                StringUtils.appendTwoDigits(sb, i / 60).append(':');
                StringUtils.appendTwoDigits(sb, i % 60);
                break;
            case DAY_TO_SECOND:
                long j3 = j2 % DateTimeUtils.NANOS_PER_MINUTE;
                int i2 = (int) (j2 / DateTimeUtils.NANOS_PER_MINUTE);
                sb.append(j).append(' ');
                StringUtils.appendTwoDigits(sb, i2 / 60).append(':');
                StringUtils.appendTwoDigits(sb, i2 % 60).append(':');
                StringUtils.appendTwoDigits(sb, (int) (j3 / DateTimeUtils.NANOS_PER_SECOND));
                DateTimeUtils.appendNanos(sb, (int) (j3 % DateTimeUtils.NANOS_PER_SECOND));
                break;
            case HOUR_TO_MINUTE:
                sb.append(j).append(':');
                StringUtils.appendTwoDigits(sb, (int) j2);
                break;
            case HOUR_TO_SECOND:
                sb.append(j).append(':');
                StringUtils.appendTwoDigits(sb, (int) (j2 / DateTimeUtils.NANOS_PER_MINUTE)).append(':');
                long j4 = j2 % DateTimeUtils.NANOS_PER_MINUTE;
                StringUtils.appendTwoDigits(sb, (int) (j4 / DateTimeUtils.NANOS_PER_SECOND));
                DateTimeUtils.appendNanos(sb, (int) (j4 % DateTimeUtils.NANOS_PER_SECOND));
                break;
            case MINUTE_TO_SECOND:
                sb.append(j).append(':');
                StringUtils.appendTwoDigits(sb, (int) (j2 / DateTimeUtils.NANOS_PER_SECOND));
                DateTimeUtils.appendNanos(sb, (int) (j2 % DateTimeUtils.NANOS_PER_SECOND));
                break;
        }
        return sb.append("' ").append(intervalQualifier);
    }

    public static BigInteger intervalToAbsolute(ValueInterval valueInterval) {
        BigInteger intervalToAbsolute;
        switch (valueInterval.getQualifier()) {
            case YEAR:
                intervalToAbsolute = BigInteger.valueOf(valueInterval.getLeading()).multiply(MONTHS_PER_YEAR_BI);
                break;
            case MONTH:
                intervalToAbsolute = BigInteger.valueOf(valueInterval.getLeading());
                break;
            case DAY:
                intervalToAbsolute = BigInteger.valueOf(valueInterval.getLeading()).multiply(NANOS_PER_DAY_BI);
                break;
            case HOUR:
                intervalToAbsolute = BigInteger.valueOf(valueInterval.getLeading()).multiply(NANOS_PER_HOUR_BI);
                break;
            case MINUTE:
                intervalToAbsolute = BigInteger.valueOf(valueInterval.getLeading()).multiply(NANOS_PER_MINUTE_BI);
                break;
            case SECOND:
                intervalToAbsolute = intervalToAbsolute(valueInterval, NANOS_PER_SECOND_BI);
                break;
            case YEAR_TO_MONTH:
                intervalToAbsolute = intervalToAbsolute(valueInterval, MONTHS_PER_YEAR_BI);
                break;
            case DAY_TO_HOUR:
                intervalToAbsolute = intervalToAbsolute(valueInterval, HOURS_PER_DAY_BI, NANOS_PER_HOUR_BI);
                break;
            case DAY_TO_MINUTE:
                intervalToAbsolute = intervalToAbsolute(valueInterval, MINUTES_PER_DAY_BI, NANOS_PER_MINUTE_BI);
                break;
            case DAY_TO_SECOND:
                intervalToAbsolute = intervalToAbsolute(valueInterval, NANOS_PER_DAY_BI);
                break;
            case HOUR_TO_MINUTE:
                intervalToAbsolute = intervalToAbsolute(valueInterval, MINUTES_PER_HOUR_BI, NANOS_PER_MINUTE_BI);
                break;
            case HOUR_TO_SECOND:
                intervalToAbsolute = intervalToAbsolute(valueInterval, NANOS_PER_HOUR_BI);
                break;
            case MINUTE_TO_SECOND:
                intervalToAbsolute = intervalToAbsolute(valueInterval, NANOS_PER_MINUTE_BI);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return valueInterval.isNegative() ? intervalToAbsolute.negate() : intervalToAbsolute;
    }

    private static BigInteger intervalToAbsolute(ValueInterval valueInterval, BigInteger bigInteger, BigInteger bigInteger2) {
        return intervalToAbsolute(valueInterval, bigInteger).multiply(bigInteger2);
    }

    private static BigInteger intervalToAbsolute(ValueInterval valueInterval, BigInteger bigInteger) {
        return BigInteger.valueOf(valueInterval.getLeading()).multiply(bigInteger).add(BigInteger.valueOf(valueInterval.getRemaining()));
    }

    public static ValueInterval intervalFromAbsolute(IntervalQualifier intervalQualifier, BigInteger bigInteger) {
        switch (intervalQualifier) {
            case YEAR:
                return ValueInterval.from(intervalQualifier, bigInteger.signum() < 0, leadingExact(bigInteger.divide(MONTHS_PER_YEAR_BI)), 0L);
            case MONTH:
                return ValueInterval.from(intervalQualifier, bigInteger.signum() < 0, leadingExact(bigInteger), 0L);
            case DAY:
                return ValueInterval.from(intervalQualifier, bigInteger.signum() < 0, leadingExact(bigInteger.divide(NANOS_PER_DAY_BI)), 0L);
            case HOUR:
                return ValueInterval.from(intervalQualifier, bigInteger.signum() < 0, leadingExact(bigInteger.divide(NANOS_PER_HOUR_BI)), 0L);
            case MINUTE:
                return ValueInterval.from(intervalQualifier, bigInteger.signum() < 0, leadingExact(bigInteger.divide(NANOS_PER_MINUTE_BI)), 0L);
            case SECOND:
                return intervalFromAbsolute(intervalQualifier, bigInteger, NANOS_PER_SECOND_BI);
            case YEAR_TO_MONTH:
                return intervalFromAbsolute(intervalQualifier, bigInteger, MONTHS_PER_YEAR_BI);
            case DAY_TO_HOUR:
                return intervalFromAbsolute(intervalQualifier, bigInteger.divide(NANOS_PER_HOUR_BI), HOURS_PER_DAY_BI);
            case DAY_TO_MINUTE:
                return intervalFromAbsolute(intervalQualifier, bigInteger.divide(NANOS_PER_MINUTE_BI), MINUTES_PER_DAY_BI);
            case DAY_TO_SECOND:
                return intervalFromAbsolute(intervalQualifier, bigInteger, NANOS_PER_DAY_BI);
            case HOUR_TO_MINUTE:
                return intervalFromAbsolute(intervalQualifier, bigInteger.divide(NANOS_PER_MINUTE_BI), MINUTES_PER_HOUR_BI);
            case HOUR_TO_SECOND:
                return intervalFromAbsolute(intervalQualifier, bigInteger, NANOS_PER_HOUR_BI);
            case MINUTE_TO_SECOND:
                return intervalFromAbsolute(intervalQualifier, bigInteger, NANOS_PER_MINUTE_BI);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static ValueInterval intervalFromAbsolute(IntervalQualifier intervalQualifier, BigInteger bigInteger, BigInteger bigInteger2) {
        BigInteger[] divideAndRemainder = bigInteger.divideAndRemainder(bigInteger2);
        return ValueInterval.from(intervalQualifier, bigInteger.signum() < 0, leadingExact(divideAndRemainder[0]), Math.abs(divideAndRemainder[1].longValue()));
    }

    private static long leadingExact(BigInteger bigInteger) {
        if (bigInteger.compareTo(LEADING_MAX) > 0 || bigInteger.compareTo(LEADING_MIN) < 0) {
            throw DbException.get(ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1, bigInteger.toString());
        }
        return Math.abs(bigInteger.longValue());
    }

    public static boolean validateInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        long j3;
        if (intervalQualifier == null) {
            throw new NullPointerException();
        }
        if (j == 0 && j2 == 0) {
            return false;
        }
        switch (intervalQualifier) {
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
                j3 = 1;
                break;
            case SECOND:
                j3 = 1000000000;
                break;
            case YEAR_TO_MONTH:
                j3 = 12;
                break;
            case DAY_TO_HOUR:
                j3 = 24;
                break;
            case DAY_TO_MINUTE:
                j3 = 1440;
                break;
            case DAY_TO_SECOND:
                j3 = 86400000000000L;
                break;
            case HOUR_TO_MINUTE:
                j3 = 60;
                break;
            case HOUR_TO_SECOND:
                j3 = 3600000000000L;
                break;
            case MINUTE_TO_SECOND:
                j3 = 60000000000L;
                break;
            default:
                throw DbException.getInvalidValueException("interval", intervalQualifier);
        }
        if (j < 0 || j >= 1000000000000000000L) {
            throw DbException.getInvalidValueException("interval", Long.toString(j));
        }
        if (j2 < 0 || j2 >= j3) {
            throw DbException.getInvalidValueException("interval", Long.toString(j2));
        }
        return z;
    }

    public static long yearsFromInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        if (intervalQualifier == IntervalQualifier.YEAR || intervalQualifier == IntervalQualifier.YEAR_TO_MONTH) {
            long j3 = j;
            if (z) {
                j3 = -j3;
            }
            return j3;
        }
        return 0L;
    }

    public static long monthsFromInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        long j3;
        if (intervalQualifier == IntervalQualifier.MONTH) {
            j3 = j;
        } else if (intervalQualifier == IntervalQualifier.YEAR_TO_MONTH) {
            j3 = j2;
        } else {
            return 0L;
        }
        if (z) {
            j3 = -j3;
        }
        return j3;
    }

    public static long daysFromInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        switch (intervalQualifier) {
            case DAY:
            case DAY_TO_HOUR:
            case DAY_TO_MINUTE:
            case DAY_TO_SECOND:
                long j3 = j;
                if (z) {
                    j3 = -j3;
                }
                return j3;
            case HOUR:
            case MINUTE:
            case SECOND:
            case YEAR_TO_MONTH:
            default:
                return 0L;
        }
    }

    public static long hoursFromInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        long j3;
        switch (intervalQualifier) {
            case HOUR:
            case HOUR_TO_MINUTE:
            case HOUR_TO_SECOND:
                j3 = j;
                break;
            case MINUTE:
            case SECOND:
            case YEAR_TO_MONTH:
            default:
                return 0L;
            case DAY_TO_HOUR:
                j3 = j2;
                break;
            case DAY_TO_MINUTE:
                j3 = j2 / 60;
                break;
            case DAY_TO_SECOND:
                j3 = j2 / DateTimeUtils.NANOS_PER_HOUR;
                break;
        }
        if (z) {
            j3 = -j3;
        }
        return j3;
    }

    public static long minutesFromInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        long j3;
        switch (intervalQualifier) {
            case MINUTE:
            case MINUTE_TO_SECOND:
                j3 = j;
                break;
            case SECOND:
            case YEAR_TO_MONTH:
            case DAY_TO_HOUR:
            default:
                return 0L;
            case DAY_TO_MINUTE:
                j3 = j2 % 60;
                break;
            case DAY_TO_SECOND:
                j3 = (j2 / DateTimeUtils.NANOS_PER_MINUTE) % 60;
                break;
            case HOUR_TO_MINUTE:
                j3 = j2;
                break;
            case HOUR_TO_SECOND:
                j3 = j2 / DateTimeUtils.NANOS_PER_MINUTE;
                break;
        }
        if (z) {
            j3 = -j3;
        }
        return j3;
    }

    public static long nanosFromInterval(IntervalQualifier intervalQualifier, boolean z, long j, long j2) {
        long j3;
        switch (intervalQualifier) {
            case SECOND:
                j3 = (j * DateTimeUtils.NANOS_PER_SECOND) + j2;
                break;
            case YEAR_TO_MONTH:
            case DAY_TO_HOUR:
            case DAY_TO_MINUTE:
            case HOUR_TO_MINUTE:
            default:
                return 0L;
            case DAY_TO_SECOND:
            case HOUR_TO_SECOND:
                j3 = j2 % DateTimeUtils.NANOS_PER_MINUTE;
                break;
            case MINUTE_TO_SECOND:
                j3 = j2;
                break;
        }
        if (z) {
            j3 = -j3;
        }
        return j3;
    }
}

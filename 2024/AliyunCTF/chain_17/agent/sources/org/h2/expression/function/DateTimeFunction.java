package org.h2.expression.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDate;
import org.h2.value.ValueInteger;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/DateTimeFunction.class */
public final class DateTimeFunction extends Function1_2 {
    public static final int EXTRACT = 0;
    public static final int DATE_TRUNC = 1;
    public static final int DATEADD = 2;
    public static final int DATEDIFF = 3;
    public static final int LAST_DAY = 4;
    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    public static final int HOUR = 3;
    public static final int MINUTE = 4;
    public static final int SECOND = 5;
    public static final int TIMEZONE_HOUR = 6;
    public static final int TIMEZONE_MINUTE = 7;
    public static final int TIMEZONE_SECOND = 8;
    public static final int MILLENNIUM = 9;
    public static final int CENTURY = 10;
    public static final int DECADE = 11;
    public static final int QUARTER = 12;
    public static final int MILLISECOND = 13;
    public static final int MICROSECOND = 14;
    public static final int NANOSECOND = 15;
    public static final int DAY_OF_YEAR = 16;
    public static final int ISO_DAY_OF_WEEK = 17;
    public static final int ISO_WEEK = 18;
    public static final int ISO_WEEK_YEAR = 19;
    public static final int DAY_OF_WEEK = 20;
    public static final int WEEK = 21;
    public static final int WEEK_YEAR = 22;
    public static final int EPOCH = 23;
    public static final int DOW = 24;
    private static final int FIELDS_COUNT = 25;
    private static volatile WeekFields WEEK_FIELDS;
    private final int function;
    private final int field;
    private static final String[] NAMES = {"EXTRACT", "DATE_TRUNC", "DATEADD", "DATEDIFF", "LAST_DAY"};
    private static final String[] FIELD_NAMES = {"YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TIMEZONE_SECOND", "MILLENNIUM", "CENTURY", "DECADE", "QUARTER", "MILLISECOND", "MICROSECOND", "NANOSECOND", "DAY_OF_YEAR", "ISO_DAY_OF_WEEK", "ISO_WEEK", "ISO_WEEK_YEAR", "DAY_OF_WEEK", "WEEK", "WEEK_YEAR", "EPOCH", "DOW"};
    private static final BigDecimal BD_SECONDS_PER_DAY = new BigDecimal(DateTimeUtils.SECONDS_PER_DAY);
    private static final BigInteger BI_SECONDS_PER_DAY = BigInteger.valueOf(DateTimeUtils.SECONDS_PER_DAY);
    private static final BigDecimal BD_NANOS_PER_SECOND = new BigDecimal(DateTimeUtils.NANOS_PER_SECOND);

    public static int getField(String str) {
        String upperEnglish = StringUtils.toUpperEnglish(str);
        boolean z = -1;
        switch (upperEnglish.hashCode()) {
            case -2125965657:
                if (upperEnglish.equals("ISODOW")) {
                    z = 43;
                    break;
                }
                break;
            case -2020697580:
                if (upperEnglish.equals("MINUTE")) {
                    z = 15;
                    break;
                }
                break;
            case -1892490734:
                if (upperEnglish.equals("MILLISECONDS")) {
                    z = 31;
                    break;
                }
                break;
            case -1852950412:
                if (upperEnglish.equals("SECOND")) {
                    z = 19;
                    break;
                }
                break;
            case -1627991000:
                if (upperEnglish.equals("NANOSECOND")) {
                    z = 36;
                    break;
                }
                break;
            case -1479810526:
                if (upperEnglish.equals("ISOYEAR")) {
                    z = 47;
                    break;
                }
                break;
            case -1351489990:
                if (upperEnglish.equals("SQL_TSI_MONTH")) {
                    z = 7;
                    break;
                }
                break;
            case -1321838393:
                if (upperEnglish.equals("DAYOFWEEK")) {
                    z = 49;
                    break;
                }
                break;
            case -1321778928:
                if (upperEnglish.equals("DAYOFYEAR")) {
                    z = 39;
                    break;
                }
                break;
            case -987618022:
                if (upperEnglish.equals("TIMEZONE_MINUTE")) {
                    z = 24;
                    break;
                }
                break;
            case -956582696:
                if (upperEnglish.equals("MILLENNIUM")) {
                    z = 26;
                    break;
                }
                break;
            case -881372481:
                if (upperEnglish.equals("ISO_DAY_OF_WEEK")) {
                    z = 42;
                    break;
                }
                break;
            case -819870854:
                if (upperEnglish.equals("TIMEZONE_SECOND")) {
                    z = 25;
                    break;
                }
                break;
            case -586644886:
                if (upperEnglish.equals("TIMEZONE_HOUR")) {
                    z = 23;
                    break;
                }
                break;
            case -511733957:
                if (upperEnglish.equals("MICROSECONDS")) {
                    z = 34;
                    break;
                }
                break;
            case -459387190:
                if (upperEnglish.equals("SQL_TSI_HOUR")) {
                    z = 14;
                    break;
                }
                break;
            case -458950438:
                if (upperEnglish.equals("SQL_TSI_WEEK")) {
                    z = 53;
                    break;
                }
                break;
            case -458890973:
                if (upperEnglish.equals("SQL_TSI_YEAR")) {
                    z = 3;
                    break;
                }
                break;
            case -199595423:
                if (upperEnglish.equals("MILLISECOND")) {
                    z = 30;
                    break;
                }
                break;
            case 68:
                if (upperEnglish.equals("D")) {
                    z = 9;
                    break;
                }
                break;
            case 77:
                if (upperEnglish.equals("M")) {
                    z = 5;
                    break;
                }
                break;
            case 78:
                if (upperEnglish.equals("N")) {
                    z = 17;
                    break;
                }
                break;
            case 83:
                if (upperEnglish.equals("S")) {
                    z = 20;
                    break;
                }
                break;
            case 2176:
                if (upperEnglish.equals("DD")) {
                    z = 10;
                    break;
                }
                break;
            case 2197:
                if (upperEnglish.equals("DY")) {
                    z = 40;
                    break;
                }
                break;
            case 2304:
                if (upperEnglish.equals("HH")) {
                    z = 13;
                    break;
                }
                break;
            case 2460:
                if (upperEnglish.equals("MI")) {
                    z = 16;
                    break;
                }
                break;
            case 2464:
                if (upperEnglish.equals("MM")) {
                    z = 6;
                    break;
                }
                break;
            case 2470:
                if (upperEnglish.equals("MS")) {
                    z = 32;
                    break;
                }
                break;
            case 2501:
                if (upperEnglish.equals("NS")) {
                    z = 37;
                    break;
                }
                break;
            case 2656:
                if (upperEnglish.equals("SS")) {
                    z = 21;
                    break;
                }
                break;
            case 2772:
                if (upperEnglish.equals("WK")) {
                    z = 51;
                    break;
                }
                break;
            case 2784:
                if (upperEnglish.equals("WW")) {
                    z = 52;
                    break;
                }
                break;
            case 2848:
                if (upperEnglish.equals("YY")) {
                    z = true;
                    break;
                }
                break;
            case 67452:
                if (upperEnglish.equals("DAY")) {
                    z = 8;
                    break;
                }
                break;
            case 67884:
                if (upperEnglish.equals("DOW")) {
                    z = 56;
                    break;
                }
                break;
            case 67886:
                if (upperEnglish.equals("DOY")) {
                    z = 41;
                    break;
                }
                break;
            case 76157:
                if (upperEnglish.equals("MCS")) {
                    z = 35;
                    break;
                }
                break;
            case 2223588:
                if (upperEnglish.equals("HOUR")) {
                    z = 12;
                    break;
                }
                break;
            case 2660340:
                if (upperEnglish.equals("WEEK")) {
                    z = 50;
                    break;
                }
                break;
            case 2719805:
                if (upperEnglish.equals("YEAR")) {
                    z = false;
                    break;
                }
                break;
            case 2739776:
                if (upperEnglish.equals("YYYY")) {
                    z = 2;
                    break;
                }
                break;
            case 66184297:
                if (upperEnglish.equals("EPOCH")) {
                    z = 55;
                    break;
                }
                break;
            case 73542240:
                if (upperEnglish.equals("MONTH")) {
                    z = 4;
                    break;
                }
                break;
            case 262271446:
                if (upperEnglish.equals("SQL_TSI_DAY")) {
                    z = 11;
                    break;
                }
                break;
            case 710965672:
                if (upperEnglish.equals("WEEK_YEAR")) {
                    z = 54;
                    break;
                }
                break;
            case 1047943546:
                if (upperEnglish.equals("SQL_TSI_MINUTE")) {
                    z = 18;
                    break;
                }
                break;
            case 1177163342:
                if (upperEnglish.equals("ISO_WEEK_YEAR")) {
                    z = 45;
                    break;
                }
                break;
            case 1215690714:
                if (upperEnglish.equals("SQL_TSI_SECOND")) {
                    z = 22;
                    break;
                }
                break;
            case 1369386636:
                if (upperEnglish.equals("QUARTER")) {
                    z = 29;
                    break;
                }
                break;
            case 1376594830:
                if (upperEnglish.equals("ISO_WEEK")) {
                    z = 44;
                    break;
                }
                break;
            case 1376654295:
                if (upperEnglish.equals("ISO_YEAR")) {
                    z = 46;
                    break;
                }
                break;
            case 1383237300:
                if (upperEnglish.equals("CENTURY")) {
                    z = 27;
                    break;
                }
                break;
            case 1784607768:
                if (upperEnglish.equals("MICROSECOND")) {
                    z = 33;
                    break;
                }
                break;
            case 2012565856:
                if (upperEnglish.equals("DECADE")) {
                    z = 28;
                    break;
                }
                break;
            case 2074232729:
                if (upperEnglish.equals("DAY_OF_WEEK")) {
                    z = 48;
                    break;
                }
                break;
            case 2074292194:
                if (upperEnglish.equals("DAY_OF_YEAR")) {
                    z = 38;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
            case true:
            case true:
            case true:
                return 0;
            case true:
            case true:
            case true:
            case true:
                return 1;
            case true:
            case true:
            case true:
            case true:
                return 2;
            case true:
            case true:
            case true:
                return 3;
            case true:
            case true:
            case true:
            case true:
                return 4;
            case true:
            case true:
            case true:
            case true:
                return 5;
            case true:
                return 6;
            case true:
                return 7;
            case true:
                return 8;
            case true:
                return 9;
            case true:
                return 10;
            case true:
                return 11;
            case true:
                return 12;
            case true:
            case true:
            case true:
                return 13;
            case true:
            case true:
            case true:
                return 14;
            case true:
            case true:
                return 15;
            case true:
            case true:
            case true:
            case true:
                return 16;
            case true:
            case true:
                return 17;
            case true:
                return 18;
            case true:
            case true:
            case true:
                return 19;
            case true:
            case true:
                return 20;
            case true:
            case true:
            case true:
            case true:
                return 21;
            case true:
                return 22;
            case true:
                return 23;
            case true:
                return 24;
            default:
                throw DbException.getInvalidValueException("date-time field", str);
        }
    }

    public static String getFieldName(int i) {
        if (i < 0 || i >= 25) {
            throw DbException.getUnsupportedException("datetime field " + i);
        }
        return FIELD_NAMES[i];
    }

    public DateTimeFunction(int i, int i2, Expression expression, Expression expression2) {
        super(expression, expression2);
        this.function = i;
        this.field = i2;
    }

    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        Value lastDay;
        switch (this.function) {
            case 0:
                lastDay = this.field == 23 ? extractEpoch(sessionLocal, value) : ValueInteger.get(extractInteger(sessionLocal, value, this.field));
                break;
            case 1:
                lastDay = truncateDate(sessionLocal, this.field, value);
                break;
            case 2:
                lastDay = dateadd(sessionLocal, this.field, value.getLong(), value2);
                break;
            case 3:
                lastDay = ValueBigint.get(datediff(sessionLocal, this.field, value, value2));
                break;
            case 4:
                lastDay = lastDay(sessionLocal, value);
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return lastDay;
    }

    private static int extractInteger(SessionLocal sessionLocal, Value value, int i) {
        return value instanceof ValueInterval ? extractInterval(value, i) : extractDateTime(sessionLocal, value, i);
    }

    private static int extractInterval(Value value, int i) {
        long nanosFromInterval;
        ValueInterval valueInterval = (ValueInterval) value;
        IntervalQualifier qualifier = valueInterval.getQualifier();
        boolean isNegative = valueInterval.isNegative();
        long leading = valueInterval.getLeading();
        long remaining = valueInterval.getRemaining();
        switch (i) {
            case 0:
                nanosFromInterval = IntervalUtils.yearsFromInterval(qualifier, isNegative, leading, remaining);
                break;
            case 1:
                nanosFromInterval = IntervalUtils.monthsFromInterval(qualifier, isNegative, leading, remaining);
                break;
            case 2:
            case 16:
                nanosFromInterval = IntervalUtils.daysFromInterval(qualifier, isNegative, leading, remaining);
                break;
            case 3:
                nanosFromInterval = IntervalUtils.hoursFromInterval(qualifier, isNegative, leading, remaining);
                break;
            case 4:
                nanosFromInterval = IntervalUtils.minutesFromInterval(qualifier, isNegative, leading, remaining);
                break;
            case 5:
                nanosFromInterval = IntervalUtils.nanosFromInterval(qualifier, isNegative, leading, remaining) / DateTimeUtils.NANOS_PER_SECOND;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            default:
                throw DbException.getUnsupportedException("getDatePart(" + value + ", " + i + ')');
            case 13:
                nanosFromInterval = (IntervalUtils.nanosFromInterval(qualifier, isNegative, leading, remaining) / 1000000) % 1000;
                break;
            case 14:
                nanosFromInterval = (IntervalUtils.nanosFromInterval(qualifier, isNegative, leading, remaining) / 1000) % 1000000;
                break;
            case 15:
                nanosFromInterval = IntervalUtils.nanosFromInterval(qualifier, isNegative, leading, remaining) % DateTimeUtils.NANOS_PER_SECOND;
                break;
        }
        return (int) nanosFromInterval;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int extractDateTime(SessionLocal sessionLocal, Value value, int i) {
        int timeZoneOffsetLocal;
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        long j = dateAndTimeFromValue[0];
        long j2 = dateAndTimeFromValue[1];
        switch (i) {
            case 0:
                return DateTimeUtils.yearFromDateValue(j);
            case 1:
                return DateTimeUtils.monthFromDateValue(j);
            case 2:
                return DateTimeUtils.dayFromDateValue(j);
            case 3:
                return (int) ((j2 / DateTimeUtils.NANOS_PER_HOUR) % 24);
            case 4:
                return (int) ((j2 / DateTimeUtils.NANOS_PER_MINUTE) % 60);
            case 5:
                return (int) ((j2 / DateTimeUtils.NANOS_PER_SECOND) % 60);
            case 6:
            case 7:
            case 8:
                if (value instanceof ValueTimestampTimeZone) {
                    timeZoneOffsetLocal = ((ValueTimestampTimeZone) value).getTimeZoneOffsetSeconds();
                } else if (value instanceof ValueTimeTimeZone) {
                    timeZoneOffsetLocal = ((ValueTimeTimeZone) value).getTimeZoneOffsetSeconds();
                } else {
                    timeZoneOffsetLocal = sessionLocal.currentTimeZone().getTimeZoneOffsetLocal(j, j2);
                }
                if (i == 6) {
                    return timeZoneOffsetLocal / 3600;
                }
                if (i == 7) {
                    return (timeZoneOffsetLocal % 3600) / 60;
                }
                return timeZoneOffsetLocal % 60;
            case 9:
                return millennium(DateTimeUtils.yearFromDateValue(j));
            case 10:
                return century(DateTimeUtils.yearFromDateValue(j));
            case 11:
                return decade(DateTimeUtils.yearFromDateValue(j));
            case 12:
                return ((DateTimeUtils.monthFromDateValue(j) - 1) / 3) + 1;
            case 13:
                return (int) ((j2 / 1000000) % 1000);
            case 14:
                return (int) ((j2 / 1000) % 1000000);
            case 15:
                return (int) (j2 % DateTimeUtils.NANOS_PER_SECOND);
            case 16:
                return DateTimeUtils.getDayOfYear(j);
            case 17:
                return DateTimeUtils.getIsoDayOfWeek(j);
            case 18:
                return DateTimeUtils.getIsoWeekOfYear(j);
            case 19:
                return DateTimeUtils.getIsoWeekYear(j);
            case 20:
                break;
            case 21:
                return getLocalWeekOfYear(j);
            case 22:
                WeekFields weekFields = getWeekFields();
                return DateTimeUtils.getWeekYear(j, weekFields.getFirstDayOfWeek().getValue(), weekFields.getMinimalDaysInFirstWeek());
            case 23:
            default:
                throw DbException.getUnsupportedException("EXTRACT(" + getFieldName(i) + " FROM " + value + ')');
            case 24:
                if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) {
                    return DateTimeUtils.getSundayDayOfWeek(j) - 1;
                }
                break;
        }
        return getLocalDayOfWeek(j);
    }

    private static Value truncateDate(SessionLocal sessionLocal, int i, Value value) {
        int i2;
        int i3;
        int i4;
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        long j = dateAndTimeFromValue[0];
        long j2 = dateAndTimeFromValue[1];
        switch (i) {
            case 0:
                j = (j & (-512)) | 33;
                j2 = 0;
                break;
            case 1:
                j = (j & (-32)) | 1;
                j2 = 0;
                break;
            case 2:
                j2 = 0;
                break;
            case 3:
                j2 = (j2 / DateTimeUtils.NANOS_PER_HOUR) * DateTimeUtils.NANOS_PER_HOUR;
                break;
            case 4:
                j2 = (j2 / DateTimeUtils.NANOS_PER_MINUTE) * DateTimeUtils.NANOS_PER_MINUTE;
                break;
            case 5:
                j2 = (j2 / DateTimeUtils.NANOS_PER_SECOND) * DateTimeUtils.NANOS_PER_SECOND;
                break;
            case 6:
            case 7:
            case 8:
            case 15:
            case 16:
            case 17:
            case 20:
            default:
                throw DbException.getUnsupportedException("DATE_TRUNC " + getFieldName(i));
            case 9:
                int yearFromDateValue = DateTimeUtils.yearFromDateValue(j);
                if (yearFromDateValue > 0) {
                    i2 = (((yearFromDateValue - 1) / 1000) * 1000) + 1;
                } else {
                    i2 = ((yearFromDateValue / 1000) * 1000) - 999;
                }
                j = DateTimeUtils.dateValue(i2, 1, 1);
                j2 = 0;
                break;
            case 10:
                int yearFromDateValue2 = DateTimeUtils.yearFromDateValue(j);
                if (yearFromDateValue2 > 0) {
                    i3 = (((yearFromDateValue2 - 1) / 100) * 100) + 1;
                } else {
                    i3 = ((yearFromDateValue2 / 100) * 100) - 99;
                }
                j = DateTimeUtils.dateValue(i3, 1, 1);
                j2 = 0;
                break;
            case 11:
                int yearFromDateValue3 = DateTimeUtils.yearFromDateValue(j);
                if (yearFromDateValue3 >= 0) {
                    i4 = (yearFromDateValue3 / 10) * 10;
                } else {
                    i4 = ((yearFromDateValue3 - 9) / 10) * 10;
                }
                j = DateTimeUtils.dateValue(i4, 1, 1);
                j2 = 0;
                break;
            case 12:
                j = DateTimeUtils.dateValue(DateTimeUtils.yearFromDateValue(j), (((DateTimeUtils.monthFromDateValue(j) - 1) / 3) * 3) + 1, 1);
                j2 = 0;
                break;
            case 13:
                j2 = (j2 / 1000000) * 1000000;
                break;
            case 14:
                j2 = (j2 / 1000) * 1000;
                break;
            case 18:
                j = truncateToWeek(j, 1);
                j2 = 0;
                break;
            case 19:
                j = truncateToWeekYear(j, 1, 4);
                j2 = 0;
                break;
            case 21:
                j = truncateToWeek(j, getWeekFields().getFirstDayOfWeek().getValue());
                j2 = 0;
                break;
            case 22:
                WeekFields weekFields = getWeekFields();
                j = truncateToWeekYear(j, weekFields.getFirstDayOfWeek().getValue(), weekFields.getMinimalDaysInFirstWeek());
                break;
        }
        Value dateTimeToValue = DateTimeUtils.dateTimeToValue(value, j, j2);
        if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && dateTimeToValue.getValueType() == 17) {
            dateTimeToValue = dateTimeToValue.convertTo(21, sessionLocal);
        }
        return dateTimeToValue;
    }

    private static long truncateToWeek(long j, int i) {
        long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(j);
        int dayOfWeekFromAbsolute = DateTimeUtils.getDayOfWeekFromAbsolute(absoluteDayFromDateValue, i);
        if (dayOfWeekFromAbsolute != 1) {
            j = DateTimeUtils.dateValueFromAbsoluteDay((absoluteDayFromDateValue - dayOfWeekFromAbsolute) + 1);
        }
        return j;
    }

    private static long truncateToWeekYear(long j, int i, int i2) {
        long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(j);
        int yearFromDateValue = DateTimeUtils.yearFromDateValue(j);
        long weekYearAbsoluteStart = DateTimeUtils.getWeekYearAbsoluteStart(yearFromDateValue, i, i2);
        if (absoluteDayFromDateValue < weekYearAbsoluteStart) {
            weekYearAbsoluteStart = DateTimeUtils.getWeekYearAbsoluteStart(yearFromDateValue - 1, i, i2);
        } else if (DateTimeUtils.monthFromDateValue(j) == 12 && 24 + i2 < DateTimeUtils.dayFromDateValue(j)) {
            long weekYearAbsoluteStart2 = DateTimeUtils.getWeekYearAbsoluteStart(yearFromDateValue + 1, i, i2);
            if (absoluteDayFromDateValue >= weekYearAbsoluteStart2) {
                weekYearAbsoluteStart = weekYearAbsoluteStart2;
            }
        }
        return DateTimeUtils.dateValueFromAbsoluteDay(weekYearAbsoluteStart);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:15:0x0048. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:52:0x01e7  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x022f  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0237  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01f7  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0202  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.h2.value.Value dateadd(org.h2.engine.SessionLocal r11, int r12, long r13, org.h2.value.Value r15) {
        /*
            Method dump skipped, instructions count: 577
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.function.DateTimeFunction.dateadd(org.h2.engine.SessionLocal, int, long, org.h2.value.Value):org.h2.value.Value");
    }

    private static Value addYearsMonths(int i, boolean z, long j, Value value, int i2, long j2, long j3) {
        if (i2 == 18 || i2 == 19) {
            throw DbException.getInvalidValueException("DATEADD time part", getFieldName(i));
        }
        long yearFromDateValue = DateTimeUtils.yearFromDateValue(j2);
        long monthFromDateValue = DateTimeUtils.monthFromDateValue(j2);
        if (z) {
            yearFromDateValue += j;
        } else {
            monthFromDateValue += j;
        }
        return DateTimeUtils.dateTimeToValue(value, DateTimeUtils.dateValueFromDenormalizedDate(yearFromDateValue, monthFromDateValue, DateTimeUtils.dayFromDateValue(j2)), j3);
    }

    private static Value addToTimeZone(int i, long j, Value value, int i2, long j2, long j3) {
        if (i2 == 21) {
            return ValueTimestampTimeZone.fromDateValueAndNanos(j2, j3, MathUtils.convertLongToInt(j + ((ValueTimestampTimeZone) value).getTimeZoneOffsetSeconds()));
        }
        if (i2 == 19) {
            return ValueTimeTimeZone.fromNanos(j3, MathUtils.convertLongToInt(j + ((ValueTimeTimeZone) value).getTimeZoneOffsetSeconds()));
        }
        throw DbException.getUnsupportedException("DATEADD " + getFieldName(i));
    }

    private static long datediff(SessionLocal sessionLocal, int i, Value value, Value value2) {
        int timeZoneOffsetLocal;
        int timeZoneOffsetLocal2;
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        long j = dateAndTimeFromValue[0];
        long absoluteDayFromDateValue = DateTimeUtils.absoluteDayFromDateValue(j);
        long[] dateAndTimeFromValue2 = DateTimeUtils.dateAndTimeFromValue(value2, sessionLocal);
        long j2 = dateAndTimeFromValue2[0];
        long absoluteDayFromDateValue2 = DateTimeUtils.absoluteDayFromDateValue(j2);
        switch (i) {
            case 0:
                return DateTimeUtils.yearFromDateValue(j2) - DateTimeUtils.yearFromDateValue(j);
            case 1:
                return (((DateTimeUtils.yearFromDateValue(j2) - DateTimeUtils.yearFromDateValue(j)) * 12) + DateTimeUtils.monthFromDateValue(j2)) - DateTimeUtils.monthFromDateValue(j);
            case 2:
            case 16:
            case 17:
            case 20:
            case 24:
                break;
            case 3:
            case 4:
            case 5:
            case 13:
            case 14:
            case 15:
            case 23:
                long j3 = dateAndTimeFromValue[1];
                long j4 = dateAndTimeFromValue2[1];
                switch (i) {
                    case 3:
                        return ((absoluteDayFromDateValue2 - absoluteDayFromDateValue) * 24) + ((j4 / DateTimeUtils.NANOS_PER_HOUR) - (j3 / DateTimeUtils.NANOS_PER_HOUR));
                    case 4:
                        return ((absoluteDayFromDateValue2 - absoluteDayFromDateValue) * 1440) + ((j4 / DateTimeUtils.NANOS_PER_MINUTE) - (j3 / DateTimeUtils.NANOS_PER_MINUTE));
                    case 5:
                    case 23:
                        return ((absoluteDayFromDateValue2 - absoluteDayFromDateValue) * DateTimeUtils.SECONDS_PER_DAY) + ((j4 / DateTimeUtils.NANOS_PER_SECOND) - (j3 / DateTimeUtils.NANOS_PER_SECOND));
                    case 13:
                        return ((absoluteDayFromDateValue2 - absoluteDayFromDateValue) * 86400000) + ((j4 / 1000000) - (j3 / 1000000));
                    case 14:
                        return ((absoluteDayFromDateValue2 - absoluteDayFromDateValue) * 86400000000L) + ((j4 / 1000) - (j3 / 1000));
                    case 15:
                        return ((absoluteDayFromDateValue2 - absoluteDayFromDateValue) * DateTimeUtils.NANOS_PER_DAY) + (j4 - j3);
                }
            case 6:
            case 7:
            case 8:
                if (value instanceof ValueTimestampTimeZone) {
                    timeZoneOffsetLocal = ((ValueTimestampTimeZone) value).getTimeZoneOffsetSeconds();
                } else if (value instanceof ValueTimeTimeZone) {
                    timeZoneOffsetLocal = ((ValueTimeTimeZone) value).getTimeZoneOffsetSeconds();
                } else {
                    timeZoneOffsetLocal = sessionLocal.currentTimeZone().getTimeZoneOffsetLocal(j, dateAndTimeFromValue[1]);
                }
                if (value2 instanceof ValueTimestampTimeZone) {
                    timeZoneOffsetLocal2 = ((ValueTimestampTimeZone) value2).getTimeZoneOffsetSeconds();
                } else if (value2 instanceof ValueTimeTimeZone) {
                    timeZoneOffsetLocal2 = ((ValueTimeTimeZone) value2).getTimeZoneOffsetSeconds();
                } else {
                    timeZoneOffsetLocal2 = sessionLocal.currentTimeZone().getTimeZoneOffsetLocal(j2, dateAndTimeFromValue2[1]);
                }
                if (i == 6) {
                    return (timeZoneOffsetLocal2 / 3600) - (timeZoneOffsetLocal / 3600);
                }
                if (i == 7) {
                    return (timeZoneOffsetLocal2 / 60) - (timeZoneOffsetLocal / 60);
                }
                return timeZoneOffsetLocal2 - timeZoneOffsetLocal;
            case 9:
                return millennium(DateTimeUtils.yearFromDateValue(j2)) - millennium(DateTimeUtils.yearFromDateValue(j));
            case 10:
                return century(DateTimeUtils.yearFromDateValue(j2)) - century(DateTimeUtils.yearFromDateValue(j));
            case 11:
                return decade(DateTimeUtils.yearFromDateValue(j2)) - decade(DateTimeUtils.yearFromDateValue(j));
            case 12:
                return (((DateTimeUtils.yearFromDateValue(j2) - DateTimeUtils.yearFromDateValue(j)) * 4) + ((DateTimeUtils.monthFromDateValue(j2) - 1) / 3)) - ((DateTimeUtils.monthFromDateValue(j) - 1) / 3);
            case 18:
                return weekdiff(absoluteDayFromDateValue, absoluteDayFromDateValue2, 1);
            case 19:
            case 22:
            default:
                throw DbException.getUnsupportedException("DATEDIFF " + getFieldName(i));
            case 21:
                return weekdiff(absoluteDayFromDateValue, absoluteDayFromDateValue2, getWeekFields().getFirstDayOfWeek().getValue());
        }
        return absoluteDayFromDateValue2 - absoluteDayFromDateValue;
    }

    private static long weekdiff(long j, long j2, int i) {
        long j3 = j + (4 - i);
        long j4 = j3 / 7;
        if (j3 < 0 && j4 * 7 != j3) {
            j4--;
        }
        long j5 = j2 + (4 - i);
        long j6 = j5 / 7;
        if (j5 < 0 && j6 * 7 != j5) {
            j6--;
        }
        return j6 - j4;
    }

    private static int millennium(int i) {
        return i > 0 ? (i + 999) / 1000 : i / 1000;
    }

    private static int century(int i) {
        return i > 0 ? (i + 99) / 100 : i / 100;
    }

    private static int decade(int i) {
        return i >= 0 ? i / 10 : (i - 9) / 10;
    }

    private static int getLocalDayOfWeek(long j) {
        return DateTimeUtils.getDayOfWeek(j, getWeekFields().getFirstDayOfWeek().getValue());
    }

    private static int getLocalWeekOfYear(long j) {
        WeekFields weekFields = getWeekFields();
        return DateTimeUtils.getWeekOfYear(j, weekFields.getFirstDayOfWeek().getValue(), weekFields.getMinimalDaysInFirstWeek());
    }

    private static WeekFields getWeekFields() {
        WeekFields weekFields = WEEK_FIELDS;
        if (weekFields == null) {
            WeekFields of = WeekFields.of(Locale.getDefault());
            weekFields = of;
            WEEK_FIELDS = of;
        }
        return weekFields;
    }

    private static ValueNumeric extractEpoch(SessionLocal sessionLocal, Value value) {
        ValueNumeric valueNumeric;
        if (value instanceof ValueInterval) {
            ValueInterval valueInterval = (ValueInterval) value;
            if (valueInterval.getQualifier().isYearMonth()) {
                ValueInterval valueInterval2 = (ValueInterval) valueInterval.convertTo(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH);
                BigInteger add = BigInteger.valueOf(valueInterval2.getLeading()).multiply(BigInteger.valueOf(31557600L)).add(BigInteger.valueOf(valueInterval2.getRemaining() * 2592000));
                if (valueInterval2.isNegative()) {
                    add = add.negate();
                }
                return ValueNumeric.get(add);
            }
            return ValueNumeric.get(new BigDecimal(IntervalUtils.intervalToAbsolute(valueInterval)).divide(BD_NANOS_PER_SECOND));
        }
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        long j = dateAndTimeFromValue[0];
        long j2 = dateAndTimeFromValue[1];
        if (value instanceof ValueTime) {
            valueNumeric = ValueNumeric.get(BigDecimal.valueOf(j2).divide(BD_NANOS_PER_SECOND));
        } else if (value instanceof ValueDate) {
            valueNumeric = ValueNumeric.get(BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(j)).multiply(BI_SECONDS_PER_DAY));
        } else {
            BigDecimal add2 = BigDecimal.valueOf(j2).divide(BD_NANOS_PER_SECOND).add(BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(j)).multiply(BD_SECONDS_PER_DAY));
            if (value instanceof ValueTimestampTimeZone) {
                valueNumeric = ValueNumeric.get(add2.subtract(BigDecimal.valueOf(((ValueTimestampTimeZone) value).getTimeZoneOffsetSeconds())));
            } else if (value instanceof ValueTimeTimeZone) {
                valueNumeric = ValueNumeric.get(add2.subtract(BigDecimal.valueOf(((ValueTimeTimeZone) value).getTimeZoneOffsetSeconds())));
            } else {
                valueNumeric = ValueNumeric.get(add2);
            }
        }
        return valueNumeric;
    }

    private static Value lastDay(SessionLocal sessionLocal, Value value) {
        long dateValue;
        int valueType = value.getValueType();
        switch (valueType) {
            case 17:
                dateValue = ((ValueDate) value).getDateValue();
                break;
            case 18:
            case 19:
            default:
                dateValue = ((ValueTimestampTimeZone) DateTimeUtils.parseTimestamp(value.getString(), sessionLocal, true)).getDateValue();
                break;
            case 20:
                dateValue = ((ValueTimestamp) value).getDateValue();
                break;
            case 21:
                dateValue = ((ValueTimestampTimeZone) value).getDateValue();
                break;
        }
        int yearFromDateValue = DateTimeUtils.yearFromDateValue(dateValue);
        int monthFromDateValue = DateTimeUtils.monthFromDateValue(dateValue);
        long dateValue2 = DateTimeUtils.dateValue(yearFromDateValue, monthFromDateValue, DateTimeUtils.getDaysInMonth(yearFromDateValue, monthFromDateValue));
        if (dateValue2 == dateValue && valueType == 17) {
            return value;
        }
        return ValueDate.fromDateValue(dateValue2);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        switch (this.function) {
            case 0:
                this.type = this.field == 23 ? TypeInfo.getTypeInfo(13, 28L, 9, null) : TypeInfo.TYPE_INTEGER;
                break;
            case 1:
                this.type = this.left.getType();
                int valueType = this.type.getValueType();
                if (!DataType.isDateTimeType(valueType)) {
                    throw Store.getInvalidExpressionTypeException("DATE_TRUNC datetime argument", this.left);
                }
                if (sessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && valueType == 17) {
                    this.type = TypeInfo.TYPE_TIMESTAMP_TZ;
                    break;
                }
                break;
            case 2:
                int valueType2 = this.right.getType().getValueType();
                if (valueType2 == 17) {
                    switch (this.field) {
                        case 3:
                        case 4:
                        case 5:
                        case 13:
                        case 14:
                        case 15:
                        case 23:
                            valueType2 = 20;
                            break;
                    }
                }
                this.type = TypeInfo.getTypeInfo(valueType2);
                break;
            case 3:
                this.type = TypeInfo.TYPE_BIGINT;
                break;
            case 4:
                this.type = TypeInfo.TYPE_DATE;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.Function1_2, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        sb.append(getName()).append('(');
        if (this.function == 4) {
            this.left.getUnenclosedSQL(sb, i);
        } else {
            sb.append(getFieldName(this.field));
            switch (this.function) {
                case 0:
                    this.left.getUnenclosedSQL(sb.append(" FROM "), i);
                    break;
                case 1:
                    this.left.getUnenclosedSQL(sb.append(", "), i);
                    break;
                case 2:
                case 3:
                    this.left.getUnenclosedSQL(sb.append(", "), i).append(", ");
                    this.right.getUnenclosedSQL(sb, i);
                    break;
                default:
                    throw DbException.getInternalError("function=" + this.function);
            }
        }
        return sb.append(')');
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}

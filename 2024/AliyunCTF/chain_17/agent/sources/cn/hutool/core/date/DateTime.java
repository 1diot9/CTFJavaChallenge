package cn.hutool.core.date;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.date.format.DatePrinter;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateTime.class */
public class DateTime extends Date {
    private static final long serialVersionUID = -5395712593979185936L;
    private static boolean useJdkToStringStyle = false;
    private boolean mutable;
    private Week firstDayOfWeek;
    private TimeZone timeZone;
    private int minimalDaysInFirstWeek;

    public static void setUseJdkToStringStyle(boolean customUseJdkToStringStyle) {
        useJdkToStringStyle = customUseJdkToStringStyle;
    }

    public static DateTime of(long timeMillis) {
        return new DateTime(timeMillis);
    }

    public static DateTime of(Date date) {
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return new DateTime(date);
    }

    public static DateTime of(Calendar calendar) {
        return new DateTime(calendar);
    }

    public static DateTime of(String dateStr, String format) {
        return new DateTime(dateStr, format);
    }

    public static DateTime now() {
        return new DateTime();
    }

    public DateTime() {
        this(TimeZone.getDefault());
    }

    public DateTime(TimeZone timeZone) {
        this(System.currentTimeMillis(), timeZone);
    }

    public DateTime(Date date) {
        this(date, date instanceof DateTime ? ((DateTime) date).timeZone : TimeZone.getDefault());
    }

    public DateTime(Date date, TimeZone timeZone) {
        this(((Date) ObjectUtil.defaultIfNull(date, new Date())).getTime(), timeZone);
    }

    public DateTime(Calendar calendar) {
        this(calendar.getTime(), calendar.getTimeZone());
        setFirstDayOfWeek(Week.of(calendar.getFirstDayOfWeek()));
    }

    public DateTime(Instant instant) {
        this(instant.toEpochMilli());
    }

    public DateTime(Instant instant, ZoneId zoneId) {
        this(instant.toEpochMilli(), ZoneUtil.toTimeZone(zoneId));
    }

    public DateTime(TemporalAccessor temporalAccessor) {
        this(TemporalAccessorUtil.toInstant(temporalAccessor));
    }

    public DateTime(ZonedDateTime zonedDateTime) {
        this(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    public DateTime(long timeMillis) {
        this(timeMillis, TimeZone.getDefault());
    }

    public DateTime(long timeMillis, TimeZone timeZone) {
        super(timeMillis);
        this.mutable = true;
        this.firstDayOfWeek = Week.MONDAY;
        this.timeZone = (TimeZone) ObjectUtil.defaultIfNull(timeZone, (Supplier<? extends TimeZone>) TimeZone::getDefault);
    }

    public DateTime(CharSequence dateStr) {
        this(DateUtil.parse(dateStr));
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public DateTime(java.lang.CharSequence r5, java.lang.String r6) {
        /*
            r4 = this;
            r0 = r4
            r1 = r6
            boolean r1 = cn.hutool.core.date.format.GlobalCustomFormat.isCustomFormat(r1)
            if (r1 == 0) goto L10
            r1 = r5
            r2 = r6
            java.util.Date r1 = cn.hutool.core.date.format.GlobalCustomFormat.parse(r1, r2)
            goto L18
        L10:
            r1 = r5
            r2 = r6
            java.text.SimpleDateFormat r2 = cn.hutool.core.date.DateUtil.newSimpleFormat(r2)
            java.util.Date r1 = parse(r1, r2)
        L18:
            r0.<init>(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.core.date.DateTime.<init>(java.lang.CharSequence, java.lang.String):void");
    }

    public DateTime(CharSequence dateStr, DateFormat dateFormat) {
        this(parse(dateStr, dateFormat), dateFormat.getTimeZone());
    }

    public DateTime(CharSequence dateStr, DateTimeFormatter formatter) {
        this(TemporalAccessorUtil.toInstant(formatter.parse(dateStr)), formatter.getZone());
    }

    public DateTime(CharSequence dateStr, DateParser dateParser) {
        this(dateStr, dateParser, SystemPropsUtil.getBoolean(SystemPropsUtil.HUTOOL_DATE_LENIENT, true));
    }

    public DateTime(CharSequence dateStr, DateParser dateParser, boolean lenient) {
        this(parse(dateStr, dateParser, lenient));
    }

    public DateTime offset(DateField datePart, int offset) {
        if (DateField.ERA == datePart) {
            throw new IllegalArgumentException("ERA is not support offset!");
        }
        Calendar cal = toCalendar();
        cal.add(datePart.getValue(), offset);
        DateTime dt = this.mutable ? this : (DateTime) ObjectUtil.clone(this);
        return dt.setTimeInternal(cal.getTimeInMillis());
    }

    public DateTime offsetNew(DateField datePart, int offset) {
        Calendar cal = toCalendar();
        cal.add(datePart.getValue(), offset);
        return ((DateTime) ObjectUtil.clone(this)).setTimeInternal(cal.getTimeInMillis());
    }

    public int getField(DateField field) {
        return getField(field.getValue());
    }

    public int getField(int field) {
        return toCalendar().get(field);
    }

    public DateTime setField(DateField field, int value) {
        return setField(field.getValue(), value);
    }

    public DateTime setField(int field, int value) {
        Calendar calendar = toCalendar();
        calendar.set(field, value);
        DateTime dt = this;
        if (false == this.mutable) {
            dt = (DateTime) ObjectUtil.clone(this);
        }
        return dt.setTimeInternal(calendar.getTimeInMillis());
    }

    @Override // java.util.Date
    public void setTime(long time) {
        if (this.mutable) {
            super.setTime(time);
            return;
        }
        throw new DateException("This is not a mutable object !");
    }

    public int year() {
        return getField(DateField.YEAR);
    }

    public int quarter() {
        return (month() / 3) + 1;
    }

    public Quarter quarterEnum() {
        return Quarter.of(quarter());
    }

    public int month() {
        return getField(DateField.MONTH);
    }

    public int monthBaseOne() {
        return month() + 1;
    }

    public int monthStartFromOne() {
        return month() + 1;
    }

    public Month monthEnum() {
        return Month.of(month());
    }

    public int weekOfYear() {
        return getField(DateField.WEEK_OF_YEAR);
    }

    public int weekOfMonth() {
        return getField(DateField.WEEK_OF_MONTH);
    }

    public int dayOfMonth() {
        return getField(DateField.DAY_OF_MONTH);
    }

    public int dayOfYear() {
        return getField(DateField.DAY_OF_YEAR);
    }

    public int dayOfWeek() {
        return getField(DateField.DAY_OF_WEEK);
    }

    public int dayOfWeekInMonth() {
        return getField(DateField.DAY_OF_WEEK_IN_MONTH);
    }

    public Week dayOfWeekEnum() {
        return Week.of(dayOfWeek());
    }

    public int hour(boolean is24HourClock) {
        return getField(is24HourClock ? DateField.HOUR_OF_DAY : DateField.HOUR);
    }

    public int minute() {
        return getField(DateField.MINUTE);
    }

    public int second() {
        return getField(DateField.SECOND);
    }

    public int millisecond() {
        return getField(DateField.MILLISECOND);
    }

    public boolean isAM() {
        return 0 == getField(DateField.AM_PM);
    }

    public boolean isPM() {
        return 1 == getField(DateField.AM_PM);
    }

    public boolean isWeekend() {
        int dayOfWeek = dayOfWeek();
        return 7 == dayOfWeek || 1 == dayOfWeek;
    }

    public boolean isLeapYear() {
        return DateUtil.isLeapYear(year());
    }

    public Calendar toCalendar() {
        return toCalendar(Locale.getDefault(Locale.Category.FORMAT));
    }

    public Calendar toCalendar(Locale locale) {
        return toCalendar(this.timeZone, locale);
    }

    public Calendar toCalendar(TimeZone zone) {
        return toCalendar(zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    public Calendar toCalendar(TimeZone zone, Locale locale) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        Calendar cal = null != zone ? Calendar.getInstance(zone, locale) : Calendar.getInstance(locale);
        cal.setFirstDayOfWeek(this.firstDayOfWeek.getValue());
        if (this.minimalDaysInFirstWeek > 0) {
            cal.setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek);
        }
        cal.setTime(this);
        return cal;
    }

    public Date toJdkDate() {
        return new Date(getTime());
    }

    public Timestamp toTimestamp() {
        return new Timestamp(getTime());
    }

    public java.sql.Date toSqlDate() {
        return new java.sql.Date(getTime());
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTimeUtil.of(this);
    }

    public DateBetween between(Date date) {
        return new DateBetween(this, date);
    }

    public long between(Date date, DateUnit unit) {
        return new DateBetween(this, date).between(unit);
    }

    public String between(Date date, DateUnit unit, BetweenFormatter.Level formatLevel) {
        return new DateBetween(this, date).toString(unit, formatLevel);
    }

    public boolean isIn(Date beginDate, Date endDate) {
        long beginMills = beginDate.getTime();
        long endMills = endDate.getTime();
        long thisMills = getTime();
        return thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills);
    }

    public boolean isBefore(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) < 0;
    }

    public boolean isBeforeOrEquals(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) <= 0;
    }

    public boolean isAfter(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) > 0;
    }

    public boolean isAfterOrEquals(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) >= 0;
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public DateTime setMutable(boolean mutable) {
        this.mutable = mutable;
        return this;
    }

    public Week getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    public DateTime setFirstDayOfWeek(Week firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        return this;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public ZoneId getZoneId() {
        return this.timeZone.toZoneId();
    }

    public DateTime setTimeZone(TimeZone timeZone) {
        this.timeZone = (TimeZone) ObjectUtil.defaultIfNull(timeZone, (Supplier<? extends TimeZone>) TimeZone::getDefault);
        return this;
    }

    public DateTime setMinimalDaysInFirstWeek(int minimalDaysInFirstWeek) {
        this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
        return this;
    }

    public boolean isLastDayOfMonth() {
        return dayOfMonth() == getLastDayOfMonth();
    }

    public int getLastDayOfMonth() {
        return monthEnum().getLastDay(isLeapYear());
    }

    @Override // java.util.Date
    public String toString() {
        if (useJdkToStringStyle) {
            return super.toString();
        }
        return toString(this.timeZone);
    }

    public String toStringDefaultTimeZone() {
        return toString(TimeZone.getDefault());
    }

    public String toString(TimeZone timeZone) {
        if (null != timeZone) {
            return toString(DateUtil.newSimpleFormat(DatePattern.NORM_DATETIME_PATTERN, null, timeZone));
        }
        return toString(DatePattern.NORM_DATETIME_FORMAT);
    }

    public String toDateStr() {
        if (null != this.timeZone) {
            return toString(DateUtil.newSimpleFormat("yyyy-MM-dd", null, this.timeZone));
        }
        return toString(DatePattern.NORM_DATE_FORMAT);
    }

    public String toTimeStr() {
        if (null != this.timeZone) {
            return toString(DateUtil.newSimpleFormat(DatePattern.NORM_TIME_PATTERN, null, this.timeZone));
        }
        return toString(DatePattern.NORM_TIME_FORMAT);
    }

    public String toString(String format) {
        if (null != this.timeZone) {
            return toString(DateUtil.newSimpleFormat(format, null, this.timeZone));
        }
        return toString(FastDateFormat.getInstance(format));
    }

    public String toString(DatePrinter format) {
        return format.format(this);
    }

    public String toString(DateFormat format) {
        return format.format((Date) this);
    }

    public String toMsStr() {
        return toString(DatePattern.NORM_DATETIME_MS_FORMAT);
    }

    private static Date parse(CharSequence dateStr, DateFormat dateFormat) {
        String pattern;
        Assert.notBlank(dateStr, "Date String must be not blank !", new Object[0]);
        try {
            return dateFormat.parse(dateStr.toString());
        } catch (Exception e) {
            if (dateFormat instanceof SimpleDateFormat) {
                pattern = ((SimpleDateFormat) dateFormat).toPattern();
            } else {
                pattern = dateFormat.toString();
            }
            throw new DateException(StrUtil.format("Parse [{}] with format [{}] error!", dateStr, pattern), e);
        }
    }

    private static Calendar parse(CharSequence dateStr, DateParser parser, boolean lenient) {
        Assert.notNull(parser, "Parser or DateFromat must be not null !", new Object[0]);
        Assert.notBlank(dateStr, "Date String must be not blank !", new Object[0]);
        Calendar calendar = CalendarUtil.parse(dateStr, lenient, parser);
        if (null == calendar) {
            throw new DateException("Parse [{}] with format [{}] error!", dateStr, parser.getPattern());
        }
        calendar.setFirstDayOfWeek(Week.MONDAY.getValue());
        return calendar;
    }

    private DateTime setTimeInternal(long time) {
        super.setTime(time);
        return this;
    }
}

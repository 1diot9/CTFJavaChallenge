package cn.hutool.core.date;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.date.format.DatePrinter;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Marker;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateUtil.class */
public class DateUtil extends CalendarUtil {
    private static final String[] wtb = {"sun", "mon", "tue", "wed", "thu", "fri", "sat", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"};

    public static DateTime date() {
        return new DateTime();
    }

    public static DateTime dateSecond() {
        return beginOfSecond(date());
    }

    public static DateTime date(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return dateNew(date);
    }

    public static DateTime dateNew(Date date) {
        if (date == null) {
            return null;
        }
        return new DateTime(date);
    }

    public static DateTime date(long date) {
        return new DateTime(date);
    }

    public static DateTime date(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new DateTime(calendar);
    }

    public static DateTime date(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        return new DateTime(temporalAccessor);
    }

    public static long current() {
        return System.currentTimeMillis();
    }

    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static String now() {
        return formatDateTime(new DateTime());
    }

    public static String today() {
        return formatDate(new DateTime());
    }

    public static int year(Date date) {
        return DateTime.of(date).year();
    }

    public static int quarter(Date date) {
        return DateTime.of(date).quarter();
    }

    public static Quarter quarterEnum(Date date) {
        return DateTime.of(date).quarterEnum();
    }

    public static int month(Date date) {
        return DateTime.of(date).month();
    }

    public static Month monthEnum(Date date) {
        return DateTime.of(date).monthEnum();
    }

    public static int weekOfYear(Date date) {
        return DateTime.of(date).weekOfYear();
    }

    public static int weekOfMonth(Date date) {
        return DateTime.of(date).weekOfMonth();
    }

    public static int dayOfMonth(Date date) {
        return DateTime.of(date).dayOfMonth();
    }

    public static int dayOfYear(Date date) {
        return DateTime.of(date).dayOfYear();
    }

    public static int dayOfWeek(Date date) {
        return DateTime.of(date).dayOfWeek();
    }

    public static Week dayOfWeekEnum(Date date) {
        return DateTime.of(date).dayOfWeekEnum();
    }

    public static boolean isWeekend(Date date) {
        Week week = dayOfWeekEnum(date);
        return Week.SATURDAY == week || Week.SUNDAY == week;
    }

    public static int hour(Date date, boolean is24HourClock) {
        return DateTime.of(date).hour(is24HourClock);
    }

    public static int minute(Date date) {
        return DateTime.of(date).minute();
    }

    public static int second(Date date) {
        return DateTime.of(date).second();
    }

    public static int millisecond(Date date) {
        return DateTime.of(date).millisecond();
    }

    public static boolean isAM(Date date) {
        return DateTime.of(date).isAM();
    }

    public static boolean isPM(Date date) {
        return DateTime.of(date).isPM();
    }

    public static int thisYear() {
        return year(date());
    }

    public static int thisMonth() {
        return month(date());
    }

    public static Month thisMonthEnum() {
        return monthEnum(date());
    }

    public static int thisWeekOfYear() {
        return weekOfYear(date());
    }

    public static int thisWeekOfMonth() {
        return weekOfMonth(date());
    }

    public static int thisDayOfMonth() {
        return dayOfMonth(date());
    }

    public static int thisDayOfWeek() {
        return dayOfWeek(date());
    }

    public static Week thisDayOfWeekEnum() {
        return dayOfWeekEnum(date());
    }

    public static int thisHour(boolean is24HourClock) {
        return hour(date(), is24HourClock);
    }

    public static int thisMinute() {
        return minute(date());
    }

    public static int thisSecond() {
        return second(date());
    }

    public static int thisMillisecond() {
        return millisecond(date());
    }

    public static String yearAndQuarter(Date date) {
        return yearAndQuarter(calendar(date));
    }

    public static LinkedHashSet<String> yearAndQuarter(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return new LinkedHashSet<>(0);
        }
        return yearAndQuarter(startDate.getTime(), endDate.getTime());
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return LocalDateTimeUtil.formatNormal(localDateTime);
    }

    public static String format(LocalDateTime localDateTime, String format) {
        return LocalDateTimeUtil.format(localDateTime, format);
    }

    public static String format(Date date, String format) {
        if (null == date || StrUtil.isBlank(format)) {
            return null;
        }
        if (GlobalCustomFormat.isCustomFormat(format)) {
            return GlobalCustomFormat.format(date, format);
        }
        TimeZone timeZone = null;
        if (date instanceof DateTime) {
            timeZone = ((DateTime) date).getTimeZone();
        }
        return format(date, newSimpleFormat(format, null, timeZone));
    }

    public static String format(Date date, DatePrinter format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }

    public static String format(Date date, DateFormat format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }

    public static String format(Date date, DateTimeFormatter format) {
        if (null == format || null == date) {
            return null;
        }
        return TemporalAccessorUtil.format(date.toInstant(), format);
    }

    public static String formatDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATETIME_FORMAT.format(date);
    }

    public static String formatDate(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_TIME_FORMAT.format(date);
    }

    public static String formatHttpDate(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.HTTP_DATETIME_FORMAT.format(date);
    }

    public static String formatChineseDate(Date date, boolean isUppercase, boolean withTime) {
        if (null == date) {
            return null;
        }
        if (false == isUppercase) {
            return (withTime ? DatePattern.CHINESE_DATE_TIME_FORMAT : DatePattern.CHINESE_DATE_FORMAT).format(date);
        }
        return CalendarUtil.formatChineseDate(CalendarUtil.calendar(date), withTime);
    }

    public static LocalDateTime parseLocalDateTime(CharSequence dateStr) {
        return parseLocalDateTime(dateStr, DatePattern.NORM_DATETIME_PATTERN);
    }

    public static LocalDateTime parseLocalDateTime(CharSequence dateStr, String format) {
        return LocalDateTimeUtil.parse(dateStr, format);
    }

    public static DateTime parse(CharSequence dateStr, DateFormat dateFormat) {
        return new DateTime(dateStr, dateFormat);
    }

    public static DateTime parse(CharSequence dateStr, DateParser parser) {
        return new DateTime(dateStr, parser);
    }

    public static DateTime parse(CharSequence dateStr, DateParser parser, boolean lenient) {
        return new DateTime(dateStr, parser, lenient);
    }

    public static DateTime parse(CharSequence dateStr, DateTimeFormatter formatter) {
        return new DateTime(dateStr, formatter);
    }

    public static DateTime parse(CharSequence dateStr, String format) {
        return new DateTime(dateStr, format);
    }

    public static DateTime parse(CharSequence dateStr, String format, Locale locale) {
        if (GlobalCustomFormat.isCustomFormat(format)) {
            return new DateTime(GlobalCustomFormat.parse(dateStr, format));
        }
        return new DateTime(dateStr, newSimpleFormat(format, locale, null));
    }

    public static DateTime parse(String str, String... parsePatterns) throws DateException {
        return new DateTime(CalendarUtil.parseByPatterns(str, parsePatterns));
    }

    public static DateTime parseDateTime(CharSequence dateString) {
        return parse(normalize(dateString), DatePattern.NORM_DATETIME_FORMAT);
    }

    public static DateTime parseDate(CharSequence dateString) {
        return parse(normalize(dateString), DatePattern.NORM_DATE_FORMAT);
    }

    public static DateTime parseTime(CharSequence timeString) {
        return parse(normalize(timeString), DatePattern.NORM_TIME_FORMAT);
    }

    public static DateTime parseTimeToday(CharSequence timeString) {
        CharSequence timeString2 = StrUtil.format("{} {}", today(), timeString);
        if (1 == StrUtil.count(timeString2, ':')) {
            return parse(timeString2, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        }
        return parse(timeString2, DatePattern.NORM_DATETIME_FORMAT);
    }

    public static DateTime parseUTC(String utcString) {
        if (utcString == null) {
            return null;
        }
        int length = utcString.length();
        if (StrUtil.contains((CharSequence) utcString, 'Z')) {
            if (length == DatePattern.UTC_PATTERN.length() - 4) {
                return parse(utcString, DatePattern.UTC_FORMAT);
            }
            int patternLength = DatePattern.UTC_MS_PATTERN.length();
            if (length <= patternLength - 4 && length >= patternLength - 6) {
                return parse(utcString, DatePattern.UTC_MS_FORMAT);
            }
        } else {
            if (StrUtil.contains((CharSequence) utcString, '+')) {
                String utcString2 = utcString.replace(" +", Marker.ANY_NON_NULL_MARKER);
                String zoneOffset = StrUtil.subAfter((CharSequence) utcString2, '+', true);
                if (StrUtil.isBlank(zoneOffset)) {
                    throw new DateException("Invalid format: [{}]", utcString2);
                }
                if (false == StrUtil.contains((CharSequence) zoneOffset, ':')) {
                    String pre = StrUtil.subBefore((CharSequence) utcString2, '+', true);
                    utcString2 = pre + Marker.ANY_NON_NULL_MARKER + zoneOffset.substring(0, 2) + ":00";
                }
                if (StrUtil.contains((CharSequence) utcString2, '.')) {
                    return parse(normalizeMillSeconds(utcString2, ".", Marker.ANY_NON_NULL_MARKER), DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
                }
                return parse(utcString2, DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
            }
            if (ReUtil.contains("-\\d{2}:?00", utcString)) {
                String utcString3 = utcString.replace(" -", "-");
                if (':' != utcString3.charAt(utcString3.length() - 3)) {
                    utcString3 = utcString3.substring(0, utcString3.length() - 2) + ":00";
                }
                if (StrUtil.contains((CharSequence) utcString3, '.')) {
                    return new DateTime(normalizeMillSeconds(utcString3, ".", "-"), DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
                }
                return new DateTime(utcString3, DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
            }
            if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 2) {
                return parse(utcString, DatePattern.UTC_SIMPLE_FORMAT);
            }
            if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 5) {
                return parse(utcString + ":00", DatePattern.UTC_SIMPLE_FORMAT);
            }
            if (StrUtil.contains((CharSequence) utcString, '.')) {
                return parse(normalizeMillSeconds(utcString, ".", null), DatePattern.UTC_SIMPLE_MS_FORMAT);
            }
        }
        throw new DateException("No format fit for date String [{}] !", utcString);
    }

    public static DateTime parseCST(CharSequence cstString) {
        if (cstString == null) {
            return null;
        }
        return parse(cstString, DatePattern.JDK_DATETIME_FORMAT);
    }

    public static DateTime parse(CharSequence dateCharSequence) {
        if (StrUtil.isBlank(dateCharSequence)) {
            return null;
        }
        String dateStr = StrUtil.removeAll(dateCharSequence.toString().trim(), 26085, 31186);
        int length = dateStr.length();
        if (NumberUtil.isNumber(dateStr)) {
            if (length == DatePattern.PURE_DATETIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_FORMAT);
            }
            if (length == DatePattern.PURE_DATETIME_MS_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_MS_FORMAT);
            }
            if (length == DatePattern.PURE_DATE_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATE_FORMAT);
            }
            if (length == DatePattern.PURE_TIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_TIME_FORMAT);
            }
        } else {
            if (ReUtil.isMatch(PatternPool.TIME, dateStr)) {
                return parseTimeToday(dateStr);
            }
            if (StrUtil.containsAnyIgnoreCase(dateStr, wtb)) {
                return parseCST(dateStr);
            }
            if (StrUtil.contains((CharSequence) dateStr, 'T')) {
                return parseUTC(dateStr);
            }
        }
        String dateStr2 = normalize(dateStr);
        if (ReUtil.isMatch(DatePattern.REGEX_NORM, dateStr2)) {
            int colonCount = StrUtil.count((CharSequence) dateStr2, ':');
            switch (colonCount) {
                case 0:
                    return parse(dateStr2, DatePattern.NORM_DATE_FORMAT);
                case 1:
                    return parse(dateStr2, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
                case 2:
                    int indexOfDot = StrUtil.indexOf(dateStr2, '.');
                    if (indexOfDot > 0) {
                        int length1 = dateStr2.length();
                        if (length1 - indexOfDot > 4) {
                            dateStr2 = StrUtil.subPre(dateStr2, indexOfDot + 4);
                        }
                        return parse(dateStr2, DatePattern.NORM_DATETIME_MS_FORMAT);
                    }
                    return parse(dateStr2, DatePattern.NORM_DATETIME_FORMAT);
            }
        }
        throw new DateException("No format fit for date String [{}] !", dateStr2);
    }

    public static DateTime truncate(Date date, DateField dateField) {
        return new DateTime(truncate(calendar(date), dateField));
    }

    public static DateTime round(Date date, DateField dateField) {
        return new DateTime(round(calendar(date), dateField));
    }

    public static DateTime ceiling(Date date, DateField dateField) {
        return new DateTime(ceiling(calendar(date), dateField));
    }

    public static DateTime ceiling(Date date, DateField dateField, boolean truncateMillisecond) {
        return new DateTime(ceiling(calendar(date), dateField, truncateMillisecond));
    }

    public static DateTime beginOfSecond(Date date) {
        return new DateTime(beginOfSecond(calendar(date)));
    }

    public static DateTime endOfSecond(Date date) {
        return new DateTime(endOfSecond(calendar(date)));
    }

    public static DateTime beginOfHour(Date date) {
        return new DateTime(beginOfHour(calendar(date)));
    }

    public static DateTime endOfHour(Date date) {
        return new DateTime(endOfHour(calendar(date)));
    }

    public static DateTime beginOfMinute(Date date) {
        return new DateTime(beginOfMinute(calendar(date)));
    }

    public static DateTime endOfMinute(Date date) {
        return new DateTime(endOfMinute(calendar(date)));
    }

    public static DateTime beginOfDay(Date date) {
        return new DateTime(beginOfDay(calendar(date)));
    }

    public static DateTime endOfDay(Date date) {
        return new DateTime(endOfDay(calendar(date)));
    }

    public static DateTime beginOfWeek(Date date) {
        return new DateTime(beginOfWeek(calendar(date)));
    }

    public static DateTime beginOfWeek(Date date, boolean isMondayAsFirstDay) {
        return new DateTime(beginOfWeek(calendar(date), isMondayAsFirstDay));
    }

    public static DateTime endOfWeek(Date date) {
        return new DateTime(endOfWeek(calendar(date)));
    }

    public static DateTime endOfWeek(Date date, boolean isSundayAsLastDay) {
        return new DateTime(endOfWeek(calendar(date), isSundayAsLastDay));
    }

    public static DateTime beginOfMonth(Date date) {
        return new DateTime(beginOfMonth(calendar(date)));
    }

    public static DateTime endOfMonth(Date date) {
        return new DateTime(endOfMonth(calendar(date)));
    }

    public static DateTime beginOfQuarter(Date date) {
        return new DateTime(beginOfQuarter(calendar(date)));
    }

    public static DateTime endOfQuarter(Date date) {
        return new DateTime(endOfQuarter(calendar(date)));
    }

    public static DateTime beginOfYear(Date date) {
        return new DateTime(beginOfYear(calendar(date)));
    }

    public static DateTime endOfYear(Date date) {
        return new DateTime(endOfYear(calendar(date)));
    }

    public static DateTime yesterday() {
        return offsetDay(new DateTime(), -1);
    }

    public static DateTime tomorrow() {
        return offsetDay(new DateTime(), 1);
    }

    public static DateTime lastWeek() {
        return offsetWeek(new DateTime(), -1);
    }

    public static DateTime nextWeek() {
        return offsetWeek(new DateTime(), 1);
    }

    public static DateTime lastMonth() {
        return offsetMonth(new DateTime(), -1);
    }

    public static DateTime nextMonth() {
        return offsetMonth(new DateTime(), 1);
    }

    public static DateTime offsetMillisecond(Date date, int offset) {
        return offset(date, DateField.MILLISECOND, offset);
    }

    public static DateTime offsetSecond(Date date, int offset) {
        return offset(date, DateField.SECOND, offset);
    }

    public static DateTime offsetMinute(Date date, int offset) {
        return offset(date, DateField.MINUTE, offset);
    }

    public static DateTime offsetHour(Date date, int offset) {
        return offset(date, DateField.HOUR_OF_DAY, offset);
    }

    public static DateTime offsetDay(Date date, int offset) {
        return offset(date, DateField.DAY_OF_YEAR, offset);
    }

    public static DateTime offsetWeek(Date date, int offset) {
        return offset(date, DateField.WEEK_OF_YEAR, offset);
    }

    public static DateTime offsetMonth(Date date, int offset) {
        return offset(date, DateField.MONTH, offset);
    }

    public static DateTime offset(Date date, DateField dateField, int offset) {
        return dateNew(date).offset(dateField, offset);
    }

    public static long between(Date beginDate, Date endDate, DateUnit unit) {
        return between(beginDate, endDate, unit, true);
    }

    public static long between(Date beginDate, Date endDate, DateUnit unit, boolean isAbs) {
        return new DateBetween(beginDate, endDate, isAbs).between(unit);
    }

    public static long betweenMs(Date beginDate, Date endDate) {
        return new DateBetween(beginDate, endDate).between(DateUnit.MS);
    }

    public static long betweenDay(Date beginDate, Date endDate, boolean isReset) {
        if (isReset) {
            beginDate = beginOfDay(beginDate);
            endDate = beginOfDay(endDate);
        }
        return between(beginDate, endDate, DateUnit.DAY);
    }

    public static long betweenWeek(Date beginDate, Date endDate, boolean isReset) {
        if (isReset) {
            beginDate = beginOfDay(beginDate);
            endDate = beginOfDay(endDate);
        }
        return between(beginDate, endDate, DateUnit.WEEK);
    }

    public static long betweenMonth(Date beginDate, Date endDate, boolean isReset) {
        return new DateBetween(beginDate, endDate).betweenMonth(isReset);
    }

    public static long betweenYear(Date beginDate, Date endDate, boolean isReset) {
        return new DateBetween(beginDate, endDate).betweenYear(isReset);
    }

    public static String formatBetween(Date beginDate, Date endDate, BetweenFormatter.Level level) {
        return formatBetween(between(beginDate, endDate, DateUnit.MS), level);
    }

    public static String formatBetween(Date beginDate, Date endDate) {
        return formatBetween(between(beginDate, endDate, DateUnit.MS));
    }

    public static String formatBetween(long betweenMs, BetweenFormatter.Level level) {
        return new BetweenFormatter(betweenMs, level).format();
    }

    public static String formatBetween(long betweenMs) {
        return new BetweenFormatter(betweenMs, BetweenFormatter.Level.MILLISECOND).format();
    }

    public static boolean isIn(Date date, Date beginDate, Date endDate) {
        if (date instanceof DateTime) {
            return ((DateTime) date).isIn(beginDate, endDate);
        }
        return new DateTime(date).isIn(beginDate, endDate);
    }

    public static boolean isSameTime(Date date1, Date date2) {
        return date1.compareTo(date2) == 0;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return CalendarUtil.isSameDay(calendar(date1), calendar(date2));
    }

    public static boolean isSameWeek(Date date1, Date date2, boolean isMon) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return CalendarUtil.isSameWeek(calendar(date1), calendar(date2), isMon);
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return CalendarUtil.isSameMonth(calendar(date1), calendar(date2));
    }

    public static long spendNt(long preTime) {
        return System.nanoTime() - preTime;
    }

    public static long spendMs(long preTime) {
        return System.currentTimeMillis() - preTime;
    }

    @Deprecated
    public static int toIntSecond(Date date) {
        return Integer.parseInt(format(date, "yyMMddHHmm"));
    }

    public static TimeInterval timer() {
        return new TimeInterval();
    }

    public static TimeInterval timer(boolean isNano) {
        return new TimeInterval(isNano);
    }

    public static StopWatch createStopWatch() {
        return new StopWatch();
    }

    public static StopWatch createStopWatch(String id) {
        return new StopWatch(id);
    }

    public static int ageOfNow(String birthDay) {
        return ageOfNow(parse(birthDay));
    }

    public static int ageOfNow(Date birthDay) {
        return age(birthDay, date());
    }

    public static boolean isLeapYear(int year) {
        return Year.isLeap(year);
    }

    public static int age(Date birthday, Date dateToCompare) {
        Assert.notNull(birthday, "Birthday can not be null !", new Object[0]);
        if (null == dateToCompare) {
            dateToCompare = date();
        }
        return age(birthday.getTime(), dateToCompare.getTime());
    }

    @Deprecated
    public static boolean isExpired(Date startDate, DateField dateField, int timeLength, Date endDate) {
        Date offsetDate = offset(startDate, dateField, timeLength);
        return offsetDate.after(endDate);
    }

    @Deprecated
    public static boolean isExpired(Date startDate, Date endDate, Date checkDate) {
        return betweenMs(startDate, checkDate) > betweenMs(startDate, endDate);
    }

    public static int timeToSecond(String timeStr) {
        if (StrUtil.isEmpty(timeStr)) {
            return 0;
        }
        List<String> hms = StrUtil.splitTrim((CharSequence) timeStr, ':', 3);
        int lastIndex = hms.size() - 1;
        int result = 0;
        for (int i = lastIndex; i >= 0; i--) {
            result = (int) (result + (Integer.parseInt(hms.get(i)) * Math.pow(60.0d, lastIndex - i)));
        }
        return result;
    }

    public static String secondToTime(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be a positive number!");
        }
        int hour = seconds / 3600;
        int other = seconds % 3600;
        int minute = other / 60;
        int second = other % 60;
        StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
        }
        sb.append(hour);
        sb.append(":");
        if (minute < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
        }
        sb.append(minute);
        sb.append(":");
        if (second < 10) {
            sb.append(CustomBooleanEditor.VALUE_0);
        }
        sb.append(second);
        return sb.toString();
    }

    public static DateRange range(Date start, Date end, DateField unit) {
        return new DateRange(start, end, unit);
    }

    public static List<DateTime> rangeContains(DateRange start, DateRange end) {
        List<DateTime> startDateTimes = CollUtil.newArrayList((Iterable) start);
        List<DateTime> endDateTimes = CollUtil.newArrayList((Iterable) end);
        Stream<DateTime> stream = startDateTimes.stream();
        endDateTimes.getClass();
        return (List) stream.filter((v1) -> {
            return r1.contains(v1);
        }).collect(Collectors.toList());
    }

    public static List<DateTime> rangeNotContains(DateRange start, DateRange end) {
        List<DateTime> startDateTimes = CollUtil.newArrayList((Iterable) start);
        List<DateTime> endDateTimes = CollUtil.newArrayList((Iterable) end);
        return (List) endDateTimes.stream().filter(item -> {
            return !startDateTimes.contains(item);
        }).collect(Collectors.toList());
    }

    public static <T> List<T> rangeFunc(Date start, Date end, DateField unit, Function<Date, T> func) {
        if (start == null || end == null || start.after(end)) {
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<>();
        Iterator<DateTime> it = range(start, end, unit).iterator();
        while (it.hasNext()) {
            DateTime date = it.next();
            list.add(func.apply(date));
        }
        return list;
    }

    public static void rangeConsume(Date start, Date end, DateField unit, Consumer<Date> consumer) {
        if (start == null || end == null || start.after(end)) {
            return;
        }
        range(start, end, unit).forEach(consumer);
    }

    public static List<DateTime> rangeToList(Date start, Date end, DateField unit) {
        return CollUtil.newArrayList((Iterable) range(start, end, unit));
    }

    public static List<DateTime> rangeToList(Date start, Date end, DateField unit, int step) {
        return CollUtil.newArrayList((Iterable) new DateRange(start, end, unit, step));
    }

    public static String getZodiac(int month, int day) {
        return Zodiac.getZodiac(month, day);
    }

    public static String getChineseZodiac(int year) {
        return Zodiac.getChineseZodiac(year);
    }

    public static int compare(Date date1, Date date2) {
        return CompareUtil.compare(date1, date2);
    }

    public static int compare(Date date1, Date date2, String format) {
        if (format != null) {
            if (date1 != null) {
                date1 = parse(format(date1, format), format);
            }
            if (date2 != null) {
                date2 = parse(format(date2, format), format);
            }
        }
        return CompareUtil.compare(date1, date2);
    }

    public static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    public static double nanosToSeconds(long duration) {
        return duration / 1.0E9d;
    }

    public static Instant toInstant(Date date) {
        if (null == date) {
            return null;
        }
        return date.toInstant();
    }

    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtil.toInstant(temporalAccessor);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTimeUtil.of(instant);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTimeUtil.of(date);
    }

    public static DateTime convertTimeZone(Date date, ZoneId zoneId) {
        return new DateTime(date, ZoneUtil.toTimeZone(zoneId));
    }

    public static DateTime convertTimeZone(Date date, TimeZone timeZone) {
        return new DateTime(date, timeZone);
    }

    public static int lengthOfYear(int year) {
        return Year.of(year).length();
    }

    public static int lengthOfMonth(int month, boolean isLeapYear) {
        return java.time.Month.of(month).length(isLeapYear);
    }

    public static SimpleDateFormat newSimpleFormat(String pattern) {
        return newSimpleFormat(pattern, null, null);
    }

    public static SimpleDateFormat newSimpleFormat(String pattern, Locale locale, TimeZone timeZone) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        if (null != timeZone) {
            format.setTimeZone(timeZone);
        }
        format.setLenient(false);
        return format;
    }

    /* renamed from: cn.hutool.core.date.DateUtil$1, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/DateUtil$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$util$concurrent$TimeUnit = new int[TimeUnit.values().length];

        static {
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.NANOSECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MICROSECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MILLISECONDS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public static String getShotName(TimeUnit unit) {
        switch (AnonymousClass1.$SwitchMap$java$util$concurrent$TimeUnit[unit.ordinal()]) {
            case 1:
                return "ns";
            case 2:
                return "μs";
            case 3:
                return "ms";
            case 4:
                return "s";
            case 5:
                return "min";
            case 6:
                return "h";
            default:
                return unit.name().toLowerCase();
        }
    }

    public static boolean isOverlap(Date realStartTime, Date realEndTime, Date startTime, Date endTime) {
        return realStartTime.compareTo(endTime) <= 0 && startTime.compareTo(realEndTime) <= 0;
    }

    public static boolean isLastDayOfMonth(Date date) {
        return date(date).isLastDayOfMonth();
    }

    public static int getLastDayOfMonth(Date date) {
        return date(date).getLastDayOfMonth();
    }

    private static String normalize(CharSequence dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return StrUtil.str(dateStr);
        }
        List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
        int size = dateAndTime.size();
        if (size < 1 || size > 2) {
            return StrUtil.str(dateStr);
        }
        StringBuilder builder = StrUtil.builder();
        String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
        builder.append(StrUtil.removeSuffix(datePart, "日"));
        if (size == 2) {
            builder.append(' ');
            String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
            builder.append(StrUtil.removeSuffix(timePart, ":").replace(',', '.'));
        }
        return builder.toString();
    }

    private static String normalizeMillSeconds(String dateStr, CharSequence before, CharSequence after) {
        if (StrUtil.isBlank(after)) {
            String millOrNaco = StrUtil.subPre(StrUtil.subAfter((CharSequence) dateStr, before, true), 3);
            return StrUtil.subBefore((CharSequence) dateStr, before, true) + ((Object) before) + millOrNaco;
        }
        String millOrNaco2 = StrUtil.subPre(StrUtil.subBetween(dateStr, before, after), 3);
        return StrUtil.subBefore((CharSequence) dateStr, before, true) + ((Object) before) + millOrNaco2 + ((Object) after) + StrUtil.subAfter((CharSequence) dateStr, after, true);
    }
}

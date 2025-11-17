package cn.hutool.core.date;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DateModifier;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.date.format.FastDateParser;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/CalendarUtil.class */
public class CalendarUtil {
    public static Calendar calendar() {
        return Calendar.getInstance();
    }

    public static Calendar calendar(Date date) {
        if (date instanceof DateTime) {
            return ((DateTime) date).toCalendar();
        }
        return calendar(date.getTime());
    }

    public static Calendar calendar(long millis) {
        return calendar(millis, TimeZone.getDefault());
    }

    public static Calendar calendar(long millis, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(millis);
        return cal;
    }

    public static boolean isAM(Calendar calendar) {
        return 0 == calendar.get(9);
    }

    public static boolean isPM(Calendar calendar) {
        return 1 == calendar.get(9);
    }

    public static Calendar truncate(Calendar calendar, DateField dateField) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.TRUNCATE);
    }

    public static Calendar round(Calendar calendar, DateField dateField) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.ROUND);
    }

    public static Calendar ceiling(Calendar calendar, DateField dateField) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING);
    }

    public static Calendar ceiling(Calendar calendar, DateField dateField, boolean truncateMillisecond) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING, truncateMillisecond);
    }

    public static Calendar beginOfSecond(Calendar calendar) {
        return truncate(calendar, DateField.SECOND);
    }

    public static Calendar endOfSecond(Calendar calendar) {
        return ceiling(calendar, DateField.SECOND);
    }

    public static Calendar beginOfHour(Calendar calendar) {
        return truncate(calendar, DateField.HOUR_OF_DAY);
    }

    public static Calendar endOfHour(Calendar calendar) {
        return ceiling(calendar, DateField.HOUR_OF_DAY);
    }

    public static Calendar beginOfMinute(Calendar calendar) {
        return truncate(calendar, DateField.MINUTE);
    }

    public static Calendar endOfMinute(Calendar calendar) {
        return ceiling(calendar, DateField.MINUTE);
    }

    public static Calendar beginOfDay(Calendar calendar) {
        return truncate(calendar, DateField.DAY_OF_MONTH);
    }

    public static Calendar endOfDay(Calendar calendar) {
        return ceiling(calendar, DateField.DAY_OF_MONTH);
    }

    public static Calendar beginOfWeek(Calendar calendar) {
        return beginOfWeek(calendar, true);
    }

    public static Calendar beginOfWeek(Calendar calendar, boolean isMondayAsFirstDay) {
        calendar.setFirstDayOfWeek(isMondayAsFirstDay ? 2 : 1);
        return truncate(calendar, DateField.WEEK_OF_MONTH);
    }

    public static Calendar endOfWeek(Calendar calendar) {
        return endOfWeek(calendar, true);
    }

    public static Calendar endOfWeek(Calendar calendar, boolean isSundayAsLastDay) {
        calendar.setFirstDayOfWeek(isSundayAsLastDay ? 2 : 1);
        return ceiling(calendar, DateField.WEEK_OF_MONTH);
    }

    public static Calendar beginOfMonth(Calendar calendar) {
        return truncate(calendar, DateField.MONTH);
    }

    public static Calendar endOfMonth(Calendar calendar) {
        return ceiling(calendar, DateField.MONTH);
    }

    public static Calendar beginOfQuarter(Calendar calendar) {
        calendar.set(2, (calendar.get(DateField.MONTH.getValue()) / 3) * 3);
        calendar.set(5, 1);
        return beginOfDay(calendar);
    }

    public static Calendar endOfQuarter(Calendar calendar) {
        int year = calendar.get(1);
        int month = ((calendar.get(DateField.MONTH.getValue()) / 3) * 3) + 2;
        Calendar resultCal = Calendar.getInstance(calendar.getTimeZone());
        resultCal.set(year, month, Month.of(month).getLastDay(DateUtil.isLeapYear(year)));
        return endOfDay(resultCal);
    }

    public static Calendar beginOfYear(Calendar calendar) {
        return truncate(calendar, DateField.YEAR);
    }

    public static Calendar endOfYear(Calendar calendar) {
        return ceiling(calendar, DateField.YEAR);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(6) == cal2.get(6) && cal1.get(1) == cal2.get(1) && cal1.get(0) == cal2.get(0);
    }

    public static boolean isSameWeek(Calendar cal1, Calendar cal2, boolean isMon) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal12 = (Calendar) cal1.clone();
        Calendar cal22 = (Calendar) cal2.clone();
        if (isMon) {
            cal12.setFirstDayOfWeek(2);
            cal12.set(7, 2);
            cal22.setFirstDayOfWeek(2);
            cal22.set(7, 2);
        } else {
            cal12.setFirstDayOfWeek(1);
            cal12.set(7, 1);
            cal22.setFirstDayOfWeek(1);
            cal22.set(7, 1);
        }
        return isSameDay(cal12, cal22);
    }

    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(1) == cal2.get(1) && cal1.get(2) == cal2.get(2) && cal1.get(0) == cal2.get(0);
    }

    public static boolean isSameInstant(Calendar date1, Calendar date2) {
        return null == date1 ? null == date2 : null != date2 && date1.getTimeInMillis() == date2.getTimeInMillis();
    }

    public static LinkedHashSet<String> yearAndQuarter(long startDate, long endDate) {
        LinkedHashSet<String> quarters = new LinkedHashSet<>();
        Calendar cal = calendar(startDate);
        while (startDate <= endDate) {
            quarters.add(yearAndQuarter(cal));
            cal.add(2, 3);
            startDate = cal.getTimeInMillis();
        }
        return quarters;
    }

    public static String yearAndQuarter(Calendar cal) {
        return StrUtil.builder().append(cal.get(1)).append((cal.get(2) / 3) + 1).toString();
    }

    public static int getBeginValue(Calendar calendar, DateField dateField) {
        return getBeginValue(calendar, dateField.getValue());
    }

    public static int getBeginValue(Calendar calendar, int dateField) {
        if (7 == dateField) {
            return calendar.getFirstDayOfWeek();
        }
        return calendar.getActualMinimum(dateField);
    }

    public static int getEndValue(Calendar calendar, DateField dateField) {
        return getEndValue(calendar, dateField.getValue());
    }

    public static int getEndValue(Calendar calendar, int dateField) {
        if (7 == dateField) {
            return (calendar.getFirstDayOfWeek() + 6) % 7;
        }
        return calendar.getActualMaximum(dateField);
    }

    public static Instant toInstant(Calendar calendar) {
        if (null == calendar) {
            return null;
        }
        return calendar.toInstant();
    }

    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    }

    public static int compare(Calendar calendar1, Calendar calendar2) {
        return CompareUtil.compare(calendar1, calendar2);
    }

    public static int age(Calendar birthday, Calendar dateToCompare) {
        return age(birthday.getTimeInMillis(), dateToCompare.getTimeInMillis());
    }

    public static String formatChineseDate(Calendar calendar, boolean withTime) {
        StringBuilder result = StrUtil.builder();
        String year = String.valueOf(calendar.get(1));
        int length = year.length();
        for (int i = 0; i < length; i++) {
            result.append(NumberChineseFormatter.numberCharToChinese(year.charAt(i), false));
        }
        result.append((char) 24180);
        int month = calendar.get(2) + 1;
        result.append(NumberChineseFormatter.formatThousand(month, false));
        result.append((char) 26376);
        int day = calendar.get(5);
        result.append(NumberChineseFormatter.formatThousand(day, false));
        result.append((char) 26085);
        String temp = result.toString().replace((char) 38646, (char) 12295);
        result.delete(0, result.length());
        result.append(temp);
        if (withTime) {
            int hour = calendar.get(11);
            result.append(NumberChineseFormatter.formatThousand(hour, false));
            result.append((char) 26102);
            int minute = calendar.get(12);
            result.append(NumberChineseFormatter.formatThousand(minute, false));
            result.append((char) 20998);
            int second = calendar.get(13);
            result.append(NumberChineseFormatter.formatThousand(second, false));
            result.append((char) 31186);
        }
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int age(long birthday, long dateToCompare) {
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);
        int year = cal.get(1);
        int month = cal.get(2);
        int dayOfMonth = cal.get(5);
        boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(5);
        cal.setTimeInMillis(birthday);
        int age = year - cal.get(1);
        int monthBirth = cal.get(2);
        if (age == 0) {
            return 0;
        }
        if (month == monthBirth) {
            int dayOfMonthBirth = cal.get(5);
            boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(5);
            if ((false == isLastDayOfMonth || false == isLastDayOfMonthBirth) && dayOfMonth <= dayOfMonthBirth) {
                age--;
            }
        } else if (month < monthBirth) {
            age--;
        }
        return age;
    }

    public static Calendar parseByPatterns(String str, String... parsePatterns) throws DateException {
        return parseByPatterns(str, null, parsePatterns);
    }

    public static Calendar parseByPatterns(String str, Locale locale, String... parsePatterns) throws DateException {
        return parseByPatterns(str, locale, true, parsePatterns);
    }

    public static Calendar parseByPatterns(String str, Locale locale, boolean lenient, String... parsePatterns) throws DateException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        TimeZone tz = TimeZone.getDefault();
        Locale lcl = (Locale) ObjectUtil.defaultIfNull(locale, Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(lenient);
        for (String parsePattern : parsePatterns) {
            if (GlobalCustomFormat.isCustomFormat(parsePattern)) {
                Date parse = GlobalCustomFormat.parse(str, parsePattern);
                if (null != parse) {
                    calendar.setTime(parse);
                    return calendar;
                }
            } else {
                FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
                calendar.clear();
                try {
                    if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                        return calendar;
                    }
                } catch (IllegalArgumentException e) {
                }
                pos.setIndex(0);
            }
        }
        throw new DateException("Unable to parse the date: {}", str);
    }

    public static Calendar parse(CharSequence str, boolean lenient, DateParser parser) {
        Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
        calendar.clear();
        calendar.setLenient(lenient);
        if (parser.parse(StrUtil.str(str), new ParsePosition(0), calendar)) {
            return calendar;
        }
        return null;
    }
}

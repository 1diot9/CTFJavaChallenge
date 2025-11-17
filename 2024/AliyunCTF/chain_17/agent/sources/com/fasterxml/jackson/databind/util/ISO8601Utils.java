package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import org.apache.coyote.http11.Constants;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/ISO8601Utils.class */
public class ISO8601Utils {
    protected static final int DEF_8601_LEN = "yyyy-MM-ddThh:mm:ss.SSS+00:00".length();
    private static final TimeZone TIMEZONE_Z = TimeZone.getTimeZone("UTC");

    public static String format(Date date) {
        return format(date, false, TIMEZONE_Z);
    }

    public static String format(Date date, boolean millis) {
        return format(date, millis, TIMEZONE_Z);
    }

    @Deprecated
    public static String format(Date date, boolean millis, TimeZone tz) {
        return format(date, millis, tz, Locale.US);
    }

    public static String format(Date date, boolean millis, TimeZone tz, Locale loc) {
        Calendar calendar = new GregorianCalendar(tz, loc);
        calendar.setTime(date);
        StringBuilder sb = new StringBuilder(30);
        sb.append(String.format("%04d-%02d-%02dT%02d:%02d:%02d", Integer.valueOf(calendar.get(1)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12)), Integer.valueOf(calendar.get(13))));
        if (millis) {
            sb.append(String.format(".%03d", Integer.valueOf(calendar.get(14))));
        }
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / Constants.DEFAULT_CONNECTION_TIMEOUT) / 60);
            int minutes = Math.abs((offset / Constants.DEFAULT_CONNECTION_TIMEOUT) % 60);
            Object[] objArr = new Object[3];
            objArr[0] = Character.valueOf(offset < 0 ? '-' : '+');
            objArr[1] = Integer.valueOf(hours);
            objArr[2] = Integer.valueOf(minutes);
            sb.append(String.format("%c%02d:%02d", objArr));
        } else {
            sb.append('Z');
        }
        return sb.toString();
    }

    public static Date parse(String date, ParsePosition pos) throws ParseException {
        int offset;
        TimeZone timezone;
        char c;
        Objects.requireNonNull(date);
        try {
            int offset2 = pos.getIndex();
            int offset3 = offset2 + 4;
            int year = parseInt(date, offset2, offset3);
            if (checkOffset(date, offset3, '-')) {
                offset3++;
            }
            int i = offset3;
            int offset4 = offset3 + 2;
            int month = parseInt(date, i, offset4);
            if (checkOffset(date, offset4, '-')) {
                offset4++;
            }
            int i2 = offset4;
            int offset5 = offset4 + 2;
            int day = parseInt(date, i2, offset5);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            boolean hasT = checkOffset(date, offset5, 'T');
            if (!hasT && date.length() <= offset5) {
                Calendar calendar = new GregorianCalendar(year, month - 1, day);
                pos.setIndex(offset5);
                return calendar.getTime();
            }
            if (hasT) {
                int offset6 = offset5 + 1;
                int offset7 = offset6 + 2;
                hour = parseInt(date, offset6, offset7);
                if (checkOffset(date, offset7, ':')) {
                    offset7++;
                }
                int i3 = offset7;
                offset5 = offset7 + 2;
                minutes = parseInt(date, i3, offset5);
                if (checkOffset(date, offset5, ':')) {
                    offset5++;
                }
                if (date.length() > offset5 && (c = date.charAt(offset5)) != 'Z' && c != '+' && c != '-') {
                    int i4 = offset5;
                    offset5 += 2;
                    seconds = parseInt(date, i4, offset5);
                    if (seconds > 59 && seconds < 63) {
                        seconds = 59;
                    }
                    if (checkOffset(date, offset5, '.')) {
                        int offset8 = offset5 + 1;
                        int endOffset = indexOfNonDigit(date, offset8 + 1);
                        int parseEndOffset = Math.min(endOffset, offset8 + 3);
                        int fraction = parseInt(date, offset8, parseEndOffset);
                        switch (parseEndOffset - offset8) {
                            case 1:
                                milliseconds = fraction * 100;
                                break;
                            case 2:
                                milliseconds = fraction * 10;
                                break;
                            default:
                                milliseconds = fraction;
                                break;
                        }
                        offset5 = endOffset;
                    }
                }
            }
            if (date.length() <= offset5) {
                throw new IllegalArgumentException("No time zone indicator");
            }
            char timezoneIndicator = date.charAt(offset5);
            if (timezoneIndicator == 'Z') {
                timezone = TIMEZONE_Z;
                offset = offset5 + 1;
            } else if (timezoneIndicator == '+' || timezoneIndicator == '-') {
                String timezoneOffset = date.substring(offset5);
                offset = offset5 + timezoneOffset.length();
                if ("+0000".equals(timezoneOffset) || "+00:00".equals(timezoneOffset)) {
                    timezone = TIMEZONE_Z;
                } else {
                    String timezoneId = "GMT" + timezoneOffset;
                    timezone = TimeZone.getTimeZone(timezoneId);
                    String act = timezone.getID();
                    if (!act.equals(timezoneId)) {
                        String cleaned = act.replace(":", "");
                        if (!cleaned.equals(timezoneId)) {
                            throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                        }
                    }
                }
            } else {
                throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
            }
            Calendar calendar2 = new GregorianCalendar(timezone);
            calendar2.setLenient(false);
            calendar2.set(1, year);
            calendar2.set(2, month - 1);
            calendar2.set(5, day);
            calendar2.set(11, hour);
            calendar2.set(12, minutes);
            calendar2.set(13, seconds);
            calendar2.set(14, milliseconds);
            pos.setIndex(offset);
            return calendar2.getTime();
        } catch (Exception e) {
            String input = date == null ? null : '\"' + date + '\"';
            String msg = e.getMessage();
            if (msg == null || msg.isEmpty()) {
                msg = "(" + e.getClass().getName() + ")";
            }
            ParseException ex = new ParseException("Failed to parse date " + input + ": " + msg, pos.getIndex());
            ex.initCause(e);
            throw ex;
        }
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        int i = beginIndex;
        int result = 0;
        if (i < endIndex) {
            i++;
            int digit = Character.digit(value.charAt(i), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = -digit;
        }
        while (i < endIndex) {
            int i2 = i;
            i++;
            int digit2 = Character.digit(value.charAt(i2), 10);
            if (digit2 < 0) {
                throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }
            result = (result * 10) - digit2;
        }
        return -result;
    }

    private static int indexOfNonDigit(String string, int offset) {
        for (int i = offset; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c < '0' || c > '9') {
                return i;
            }
        }
        return string.length();
    }
}

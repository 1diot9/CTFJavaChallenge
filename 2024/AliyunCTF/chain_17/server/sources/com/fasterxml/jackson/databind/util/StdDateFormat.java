package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.core.io.NumberInput;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.coyote.http11.Constants;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/StdDateFormat.class */
public class StdDateFormat extends DateFormat {
    protected static final String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";
    protected static final Pattern PATTERN_PLAIN = Pattern.compile(PATTERN_PLAIN_STR);
    protected static final Pattern PATTERN_ISO8601;
    public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
    protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    protected static final String[] ALL_FORMATS;
    protected static final TimeZone DEFAULT_TIMEZONE;
    protected static final Locale DEFAULT_LOCALE;
    protected static final DateFormat DATE_FORMAT_RFC1123;
    public static final StdDateFormat instance;
    protected static final Calendar CALENDAR;
    protected transient TimeZone _timezone;
    protected final Locale _locale;
    protected Boolean _lenient;
    private transient Calendar _calendar;
    private transient DateFormat _formatRFC1123;
    private boolean _tzSerializedWithColon;

    static {
        try {
            Pattern p = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?(\\.\\d+)?(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?");
            PATTERN_ISO8601 = p;
            ALL_FORMATS = new String[]{DATE_FORMAT_STR_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSS", DATE_FORMAT_STR_RFC1123, "yyyy-MM-dd"};
            DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
            DEFAULT_LOCALE = Locale.US;
            DATE_FORMAT_RFC1123 = new SimpleDateFormat(DATE_FORMAT_STR_RFC1123, DEFAULT_LOCALE);
            DATE_FORMAT_RFC1123.setTimeZone(DEFAULT_TIMEZONE);
            instance = new StdDateFormat();
            CALENDAR = new GregorianCalendar(DEFAULT_TIMEZONE, DEFAULT_LOCALE);
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }

    public StdDateFormat() {
        this._tzSerializedWithColon = true;
        this._locale = DEFAULT_LOCALE;
    }

    @Deprecated
    public StdDateFormat(TimeZone tz, Locale loc) {
        this._tzSerializedWithColon = true;
        this._timezone = tz;
        this._locale = loc;
    }

    protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
        this(tz, loc, lenient, false);
    }

    protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient, boolean formatTzOffsetWithColon) {
        this._tzSerializedWithColon = true;
        this._timezone = tz;
        this._locale = loc;
        this._lenient = lenient;
        this._tzSerializedWithColon = formatTzOffsetWithColon;
    }

    public static TimeZone getDefaultTimeZone() {
        return DEFAULT_TIMEZONE;
    }

    public StdDateFormat withTimeZone(TimeZone tz) {
        if (tz == null) {
            tz = DEFAULT_TIMEZONE;
        }
        if (tz == this._timezone || tz.equals(this._timezone)) {
            return this;
        }
        return new StdDateFormat(tz, this._locale, this._lenient, this._tzSerializedWithColon);
    }

    public StdDateFormat withLocale(Locale loc) {
        if (loc.equals(this._locale)) {
            return this;
        }
        return new StdDateFormat(this._timezone, loc, this._lenient, this._tzSerializedWithColon);
    }

    public StdDateFormat withLenient(Boolean b) {
        if (_equals(b, this._lenient)) {
            return this;
        }
        return new StdDateFormat(this._timezone, this._locale, b, this._tzSerializedWithColon);
    }

    public StdDateFormat withColonInTimeZone(boolean b) {
        if (this._tzSerializedWithColon == b) {
            return this;
        }
        return new StdDateFormat(this._timezone, this._locale, this._lenient, b);
    }

    @Override // java.text.DateFormat, java.text.Format
    public StdDateFormat clone() {
        return new StdDateFormat(this._timezone, this._locale, this._lenient, this._tzSerializedWithColon);
    }

    @Deprecated
    public static DateFormat getISO8601Format(TimeZone tz, Locale loc) {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_STR_ISO8601, loc);
        df.setTimeZone(DEFAULT_TIMEZONE);
        return df;
    }

    @Deprecated
    public static DateFormat getRFC1123Format(TimeZone tz, Locale loc) {
        return _cloneFormat(DATE_FORMAT_RFC1123, DATE_FORMAT_STR_RFC1123, tz, loc, null);
    }

    @Override // java.text.DateFormat
    public TimeZone getTimeZone() {
        return this._timezone;
    }

    @Override // java.text.DateFormat
    public void setTimeZone(TimeZone tz) {
        if (!tz.equals(this._timezone)) {
            _clearFormats();
            this._timezone = tz;
        }
    }

    @Override // java.text.DateFormat
    public void setLenient(boolean enabled) {
        Boolean newValue = Boolean.valueOf(enabled);
        if (!_equals(newValue, this._lenient)) {
            this._lenient = newValue;
            _clearFormats();
        }
    }

    @Override // java.text.DateFormat
    public boolean isLenient() {
        return this._lenient == null || this._lenient.booleanValue();
    }

    public boolean isColonIncludedInTimeZone() {
        return this._tzSerializedWithColon;
    }

    @Override // java.text.DateFormat
    public Date parse(String dateStr) throws ParseException {
        String dateStr2 = dateStr.trim();
        ParsePosition pos = new ParsePosition(0);
        Date dt = _parseDate(dateStr2, pos);
        if (dt != null) {
            return dt;
        }
        StringBuilder sb = new StringBuilder();
        for (String f : ALL_FORMATS) {
            if (sb.length() > 0) {
                sb.append("\", \"");
            } else {
                sb.append('\"');
            }
            sb.append(f);
        }
        sb.append('\"');
        throw new ParseException(String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)", dateStr2, sb.toString()), pos.getErrorIndex());
    }

    @Override // java.text.DateFormat
    public Date parse(String dateStr, ParsePosition pos) {
        try {
            return _parseDate(dateStr, pos);
        } catch (ParseException e) {
            return null;
        }
    }

    protected Date _parseDate(String dateStr, ParsePosition pos) throws ParseException {
        if (looksLikeISO8601(dateStr)) {
            return parseAsISO8601(dateStr, pos);
        }
        int i = dateStr.length();
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            char ch2 = dateStr.charAt(i);
            if (ch2 < '0' || ch2 > '9') {
                if (i > 0 || ch2 != '-') {
                    break;
                }
            }
        }
        if (i < 0 && (dateStr.charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
            return _parseDateFromLong(dateStr, pos);
        }
        return parseAsRFC1123(dateStr, pos);
    }

    @Override // java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        TimeZone tz = this._timezone;
        if (tz == null) {
            tz = DEFAULT_TIMEZONE;
        }
        _format(tz, this._locale, date, toAppendTo);
        return toAppendTo;
    }

    protected void _format(TimeZone tz, Locale loc, Date date, StringBuffer buffer) {
        Calendar cal = _getCalendar(tz);
        cal.setTime(date);
        int year = cal.get(1);
        if (cal.get(0) == 0) {
            _formatBCEYear(buffer, year);
        } else {
            if (year > 9999) {
                buffer.append('+');
            }
            pad4(buffer, year);
        }
        buffer.append('-');
        pad2(buffer, cal.get(2) + 1);
        buffer.append('-');
        pad2(buffer, cal.get(5));
        buffer.append('T');
        pad2(buffer, cal.get(11));
        buffer.append(':');
        pad2(buffer, cal.get(12));
        buffer.append(':');
        pad2(buffer, cal.get(13));
        buffer.append('.');
        pad3(buffer, cal.get(14));
        int offset = tz.getOffset(cal.getTimeInMillis());
        if (offset != 0) {
            int hours = Math.abs((offset / Constants.DEFAULT_CONNECTION_TIMEOUT) / 60);
            int minutes = Math.abs((offset / Constants.DEFAULT_CONNECTION_TIMEOUT) % 60);
            buffer.append(offset < 0 ? '-' : '+');
            pad2(buffer, hours);
            if (this._tzSerializedWithColon) {
                buffer.append(':');
            }
            pad2(buffer, minutes);
            return;
        }
        if (this._tzSerializedWithColon) {
            buffer.append("+00:00");
        } else {
            buffer.append("+0000");
        }
    }

    protected void _formatBCEYear(StringBuffer buffer, int bceYearNoSign) {
        if (bceYearNoSign == 1) {
            buffer.append("+0000");
            return;
        }
        int isoYear = bceYearNoSign - 1;
        buffer.append('-');
        pad4(buffer, isoYear);
    }

    private static void pad2(StringBuffer buffer, int value) {
        int tens = value / 10;
        if (tens == 0) {
            buffer.append('0');
        } else {
            buffer.append((char) (48 + tens));
            value -= 10 * tens;
        }
        buffer.append((char) (48 + value));
    }

    private static void pad3(StringBuffer buffer, int value) {
        int h = value / 100;
        if (h == 0) {
            buffer.append('0');
        } else {
            buffer.append((char) (48 + h));
            value -= h * 100;
        }
        pad2(buffer, value);
    }

    private static void pad4(StringBuffer buffer, int value) {
        int h = value / 100;
        if (h == 0) {
            buffer.append('0').append('0');
        } else {
            if (h > 99) {
                buffer.append(h);
            } else {
                pad2(buffer, h);
            }
            value -= 100 * h;
        }
        pad2(buffer, value);
    }

    public String toString() {
        return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)", getClass().getName(), this._timezone, this._locale, this._lenient);
    }

    public String toPattern() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("[one of: '").append(DATE_FORMAT_STR_ISO8601).append("', '").append(DATE_FORMAT_STR_RFC1123).append("' (");
        sb.append(Boolean.FALSE.equals(this._lenient) ? "strict" : "lenient").append(")]");
        return sb.toString();
    }

    @Override // java.text.DateFormat
    public boolean equals(Object o) {
        return o == this;
    }

    @Override // java.text.DateFormat
    public int hashCode() {
        return System.identityHashCode(this);
    }

    protected boolean looksLikeISO8601(String dateStr) {
        if (dateStr.length() >= 7 && Character.isDigit(dateStr.charAt(0)) && Character.isDigit(dateStr.charAt(3)) && dateStr.charAt(4) == '-' && Character.isDigit(dateStr.charAt(5))) {
            return true;
        }
        return false;
    }

    private Date _parseDateFromLong(String longStr, ParsePosition pos) throws ParseException {
        try {
            long ts = NumberInput.parseLong(longStr);
            return new Date(ts);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Timestamp value %s out of 64-bit value range", longStr), pos.getErrorIndex());
        }
    }

    protected Date parseAsISO8601(String dateStr, ParsePosition pos) throws ParseException {
        try {
            return _parseAsISO8601(dateStr, pos);
        } catch (IllegalArgumentException e) {
            throw new ParseException(String.format("Cannot parse date \"%s\", problem: %s", dateStr, e.getMessage()), pos.getErrorIndex());
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:39:0x018e. Please report as an issue. */
    protected Date _parseAsISO8601(String dateStr, ParsePosition bogus) throws IllegalArgumentException, ParseException {
        String formatStr;
        int seconds;
        int offsetSecs;
        int totalLen = dateStr.length();
        TimeZone tz = DEFAULT_TIMEZONE;
        if (this._timezone != null && 'Z' != dateStr.charAt(totalLen - 1)) {
            tz = this._timezone;
        }
        Calendar cal = _getCalendar(tz);
        cal.clear();
        if (totalLen <= 10) {
            if (PATTERN_PLAIN.matcher(dateStr).matches()) {
                int year = _parse4D(dateStr, 0);
                int month = _parse2D(dateStr, 5) - 1;
                int day = _parse2D(dateStr, 8);
                cal.set(year, month, day, 0, 0, 0);
                cal.set(14, 0);
                return cal.getTime();
            }
            formatStr = "yyyy-MM-dd";
        } else {
            Matcher m = PATTERN_ISO8601.matcher(dateStr);
            if (m.matches()) {
                int start = m.start(2);
                int end = m.end(2);
                int len = end - start;
                if (len > 1) {
                    int offsetSecs2 = _parse2D(dateStr, start + 1) * 3600;
                    if (len >= 5) {
                        offsetSecs2 += _parse2D(dateStr, end - 2) * 60;
                    }
                    if (dateStr.charAt(start) == '-') {
                        offsetSecs = offsetSecs2 * (-1000);
                    } else {
                        offsetSecs = offsetSecs2 * 1000;
                    }
                    cal.set(15, offsetSecs);
                    cal.set(16, 0);
                }
                int year2 = _parse4D(dateStr, 0);
                int month2 = _parse2D(dateStr, 5) - 1;
                int day2 = _parse2D(dateStr, 8);
                int hour = _parse2D(dateStr, 11);
                int minute = _parse2D(dateStr, 14);
                if (totalLen > 16 && dateStr.charAt(16) == ':') {
                    seconds = _parse2D(dateStr, 17);
                } else {
                    seconds = 0;
                }
                cal.set(year2, month2, day2, hour, minute, seconds);
                int start2 = m.start(1) + 1;
                int end2 = m.end(1);
                if (start2 >= end2) {
                    cal.set(14, 0);
                } else {
                    int msecs = 0;
                    int fractLen = end2 - start2;
                    switch (fractLen) {
                        case 0:
                            cal.set(14, msecs);
                            break;
                        case 1:
                            msecs += 100 * (dateStr.charAt(start2) - '0');
                            cal.set(14, msecs);
                            break;
                        case 2:
                            msecs += 10 * (dateStr.charAt(start2 + 1) - '0');
                            msecs += 100 * (dateStr.charAt(start2) - '0');
                            cal.set(14, msecs);
                            break;
                        case 3:
                            msecs = 0 + (dateStr.charAt(start2 + 2) - '0');
                            msecs += 10 * (dateStr.charAt(start2 + 1) - '0');
                            msecs += 100 * (dateStr.charAt(start2) - '0');
                            cal.set(14, msecs);
                            break;
                        default:
                            if (fractLen > 9) {
                                throw new ParseException(String.format("Cannot parse date \"%s\": invalid fractional seconds '%s'; can use at most 9 digits", dateStr, m.group(1).substring(1)), start2);
                            }
                            msecs = 0 + (dateStr.charAt(start2 + 2) - '0');
                            msecs += 10 * (dateStr.charAt(start2 + 1) - '0');
                            msecs += 100 * (dateStr.charAt(start2) - '0');
                            cal.set(14, msecs);
                            break;
                    }
                }
                return cal.getTime();
            }
            formatStr = DATE_FORMAT_STR_ISO8601;
        }
        throw new ParseException(String.format("Cannot parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)", dateStr, formatStr, this._lenient), 0);
    }

    private static int _parse4D(String str, int index) {
        return (1000 * (str.charAt(index) - '0')) + (100 * (str.charAt(index + 1) - '0')) + (10 * (str.charAt(index + 2) - '0')) + (str.charAt(index + 3) - '0');
    }

    private static int _parse2D(String str, int index) {
        return (10 * (str.charAt(index) - '0')) + (str.charAt(index + 1) - '0');
    }

    protected Date parseAsRFC1123(String dateStr, ParsePosition pos) {
        if (this._formatRFC1123 == null) {
            this._formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, DATE_FORMAT_STR_RFC1123, this._timezone, this._locale, this._lenient);
        }
        return this._formatRFC1123.parse(dateStr, pos);
    }

    private static final DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient) {
        DateFormat df2;
        if (!loc.equals(DEFAULT_LOCALE)) {
            df2 = new SimpleDateFormat(format, loc);
            df2.setTimeZone(tz == null ? DEFAULT_TIMEZONE : tz);
        } else {
            df2 = (DateFormat) df.clone();
            if (tz != null) {
                df2.setTimeZone(tz);
            }
        }
        if (lenient != null) {
            df2.setLenient(lenient.booleanValue());
        }
        return df2;
    }

    protected void _clearFormats() {
        this._formatRFC1123 = null;
    }

    protected Calendar _getCalendar(TimeZone tz) {
        Calendar cal = this._calendar;
        if (cal == null) {
            Calendar calendar = (Calendar) CALENDAR.clone();
            cal = calendar;
            this._calendar = calendar;
        }
        if (!cal.getTimeZone().equals(tz)) {
            cal.setTimeZone(tz);
        }
        cal.setLenient(isLenient());
        return cal;
    }

    protected static <T> boolean _equals(T value1, T value2) {
        if (value1 == value2) {
            return true;
        }
        return value1 != null && value1.equals(value2);
    }
}
